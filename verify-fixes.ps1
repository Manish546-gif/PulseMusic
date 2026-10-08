# Standalone verifier for the Canvas + crossfade fixes.
# Requires only PowerShell. Run:  powershell -ExecutionPolicy Bypass -File .\verify-fixes.ps1
# Exit code 0 = all checks pass, 1 = at least one failure.

$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$player = Join-Path $root 'app\src\main\kotlin\com\pulse\music\manish\ui\player\Player.kt'
$conn   = Join-Path $root 'app\src\main\kotlin\com\pulse\music\manish\playback\PlayerConnection.kt'

$script:failed = 0
function Check([string]$name, [bool]$ok, [string]$detail = '') {
    $status = if ($ok) { 'PASS' } else { 'FAIL' }
    if (-not $ok) { $script:failed++ }
    $suffix = if ($detail) { "  -> $detail" } else { '' }
    "  [$status] $name$suffix"
}
function Count([string]$text, [string]$pattern) {
    return [regex]::Matches($text, $pattern).Count
}
function Test-Balanced([string]$text) {
    $stripped = $text -replace '"(\\.|[^"\\])*"', '""'
    $stripped = ($stripped -split "`n" | ForEach-Object { ($_ -split '//')[0] }) -join "`n"
    $open  = ([regex]::Matches($stripped, '\{')).Count
    $close = ([regex]::Matches($stripped, '\}')).Count
    $po    = ([regex]::Matches($stripped, '\(')).Count
    $pc    = ([regex]::Matches($stripped, '\)')).Count
    return (@{ ok = ($open -eq $close -and $po -eq $pc)
               open = $open; close = $close; po = $po; pc = $pc })
}

if (-not (Test-Path $player)) { Write-Output "FATAL: Player.kt not found at $player"; exit 1 }
if (-not (Test-Path $conn))   { Write-Output "FATAL: PlayerConnection.kt not found at $conn"; exit 1 }
$P = Get-Content -LiteralPath $player -Raw
$C = Get-Content -LiteralPath $conn -Raw
$Pl = Get-Content -LiteralPath $player
$Cl = Get-Content -LiteralPath $conn

Write-Output ''
Write-Output '=== BUG 1: Apple Music canvas ==='

# 1. The overlay must not exclude Apple Music anymore.
$badStyleGate = Count $P 'playerBackground\s*!=\s*PlayerBackgroundStyle\.APPLE_MUSIC'
Check 'overlay is style-agnostic (no APPLE_MUSIC exclusion)' ($badStyleGate -eq 0) "found $badStyleGate exclusion(s)"

# 2. The old cancellation-deadlock flag must be gone.
$flag = Count $P 'canvasFetchInFlight'
Check 'canvasFetchInFlight deadlock flag removed' ($flag -eq 0) "found $flag reference(s)"

# 3. enableCanvas must be part of the fetch effect key so toggling refetches.
$keys = $Pl | Where-Object { $_ -match 'LaunchedEffect\(mediaMetadata\?\.id' }
Check 'enableCanvas is a fetch-effect key' (($keys | Where-Object { $_ -match 'enableCanvas' }).Count -eq 1) `
      (($keys | ForEach-Object { $_.Trim() }) -join ' | ')

# 4. Exactly one BackgroundVideoView call site (a second would be the dead Apple Music branch).
$callSites = $Pl | Where-Object { $_ -match '^\s*BackgroundVideoView\(' }
Check 'single shared BackgroundVideoView call site' ($callSites.Count -eq 1) "found $($callSites.Count)"

# 5. Apple Music branch must not render canvas itself (that path was alpha 0 / 65% height).
$start = [array]::IndexOf($Pl, ($Pl | Where-Object { $_ -match 'PlayerBackgroundStyle\.APPLE_MUSIC -> \{' } | Select-Object -First 1))
$end   = -1
if ($start -ge 0) {
    for ($i = $start + 1; $i -lt $Pl.Count; $i++) {
        if ($Pl[$i] -match 'PlayerBackgroundStyle\.LIVE_MESH') { $end = $i; break }
    }
}
$branch = if ($start -ge 0 -and $end -gt $start) { ($Pl[$start..$end] -join "`n") } else { '' }
Check 'Apple Music branch has no inline canvas render' ($start -ge 0 -and $end -gt $start -and $branch -notmatch 'BackgroundVideoView') `
      "branch lines $($start+1)..$($end+1)"

# 6. The shared overlay must be the last child of the background slot (after the when, before onDismiss).
$idxOverlay = ($Pl | Select-String -Pattern '^\s*val overlayEnabled\s*=' | Select-Object -First 1).LineNumber
$idxWhen    = ($Pl | Select-String -Pattern '^\s*when \(playerBackground\) \{' | Select-Object -Last 1).LineNumber
$idxDismiss = ($Pl | Select-String -Pattern '^\s*onDismiss = \{' | Select-Object -First 1).LineNumber
Check 'overlay renders for every style (inside background slot)' `
      ($idxOverlay -and $idxWhen -and $idxDismiss -and $idxOverlay -gt $idxWhen -and $idxOverlay -lt $idxDismiss) `
      "when=$idxWhen overlay=$idxOverlay onDismiss=$idxDismiss"

# 7. Diagnostics present so failures can be attributed at runtime.
$logs = Count $P '"PulseCanvas"'
Check 'runtime diagnostics present (PulseCanvas)' ($logs -ge 5) "$logs log point(s)"

Write-Output ''
Write-Output '=== BUG 2: stale title/thumbnail after crossfade ==='

Check 'PlayerConnection observes service.currentMediaMetadata' ($C -match 'service\.currentMediaMetadata\.collect')
Check 'crossfade metadata hand-off is wired' ($C -match 'Crossfade swaps playback onto a brand-new ExoPlayer')
$logs2 = Count $C '"PulseMeta"'
Check 'runtime diagnostics present (PulseMeta)' ($logs2 -ge 4) "$logs2 log point(s)"

Write-Output ''
Write-Output '=== structural integrity ==='

foreach ($f in @(@{n='Player.kt'; t=$P}, @{n='PlayerConnection.kt'; t=$C})) {
    $b = Test-Balanced $f.t
    Check "$($f.n) braces/parens balanced" $b.ok `
          "braces $($b.open)/$($b.close), parens $($b.po)/$($b.pc)"
}

# Confirm the model fields the fix reads actually exist.
$meta = Get-ChildItem -Path $root -Recurse -Filter 'MediaMetadata.kt' -ErrorAction SilentlyContinue |
        Where-Object { $_.FullName -match 'models\\MediaMetadata\.kt$' } | Select-Object -First 1
if ($meta) {
    $M = Get-Content -LiteralPath $meta.FullName -Raw
    Check 'MediaMetadata exposes id and title' ($M -match 'val id: String,' -and $M -match 'val title: String,') $meta.FullName
} else {
    Check 'MediaMetadata model located' $false
}

Write-Output ''
if ($script:failed -eq 0) {
    Write-Output 'RESULT: ALL CHECKS PASSED'
    exit 0
} else {
    Write-Output "RESULT: $script:failed CHECK(S) FAILED"
    exit 1
}
