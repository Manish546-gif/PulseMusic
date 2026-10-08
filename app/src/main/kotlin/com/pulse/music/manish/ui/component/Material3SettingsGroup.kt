package com.pulse.music.manish.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pulse.music.manish.R
import com.pulse.music.manish.ui.theme.SettingsIconColors
import com.pulse.music.manish.ui.theme.pulseTheme
import com.pulse.music.manish.ui.utils.scrollToOnHighlight

/**
 * The single container treatment for every settings group in the app.
 *
 * Per DESIGN.md §2 ("Cards & Surfaces") this reuses the app's existing
 * `Modifier.liquidGlass` panel rather than a standard M3 `Card`:
 * `RoundedCornerShape(24.dp)`, `0.dp` elevation. On devices/settings where glass
 * is active the panel samples a backdrop and draws the configured
 * blur/vibrancy/lens/tint; otherwise it falls back to a translucent
 * `surfaceVariant.copy(alpha = 0.3f)` fill, so the panel always reads correctly.
 *
 * *Why the panel must not sample `appBackdrop`:* these panels are drawn *inside*
 * the region that `MainActivity` records (`Modifier.layerBackdrop(appBackdrop)`
 * wraps the NavHost). That node re-runs its `drawContent()` inside the record and
 * the vendored `recordLayer` has no recursion guard, so a surface sampling
 * `appBackdrop` from within that region samples the very layer being recorded.
 * With a handful of panels per screen that recursed until the RenderThread
 * exhausted its stack (`SIGSEGV` in `RenderNode::prepareTreeImpl`).
 *
 * `MainActivity` therefore provides a *flat* backdrop (`settingsBackdrop`) for
 * the NavHost subtree: `rememberLayerBackdrop { drawRect(baseBg) }`. Its record
 * block only draws a rectangle - it never re-enters `drawContent()` - so panels
 * here sample it safely and still get the real liquid-glass treatment over the
 * app's base background. The four pre-existing glass surfaces (mini player, nav
 * bar, toolbar) sit *outside* the NavHost and keep sampling `appBackdrop`.
 *
 * Public so settings screens that build their own row DSL (see
 * `PreferenceGroup` in DiscordSettings) share the one container instead of
 * re-implementing a per-row `Card` stack.
 */
@Composable
fun SettingsGlassPanel(
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit
) {
  val glassConfig = LocalGlassEffectConfig.current
  // `liquidGlass` is a no-op below API 31 (`isGlassSupported()`), and our minSdk
  // is 26 -- so gate on it, or the transparent panel below would render invisible.
  val useGlass = glassConfig.globalEnabled && isGlassSupported()
  val shape = RoundedCornerShape(24.dp)

  Surface(
    modifier =
      modifier
        .fillMaxWidth()
        .then(
          if (useGlass) Modifier.liquidGlass(config = glassConfig, shape = shape) else Modifier
        ),
    color = if (useGlass) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
    shape = shape
  ) {
    content()
  }
}

/**
 * Renders a titled group of settings rows as one liquid-glass panel with a
 * hairline divider between rows.
 *
 * This is the shared container for every settings *sub-page*, so a sub-page only
 * has to declare its sections and rows to pick up the app-wide treatment. Rows
 * keep their own `onClick`, `isHighlighted` and scroll-into-view behaviour.
 */
@Composable
fun Material3SettingsGroup(
  title: String? = null,
  compact: Boolean = false,
  scrollState: ScrollState? = null,
  items: List<Material3SettingsItem>
) {
  if (items.isEmpty()) return
  Column(modifier = Modifier.fillMaxWidth()) {
    title?.let {
      SettingsSectionLabel(
        text = it,
        topPadding = if (compact) 4.dp else 8.dp,
        bottomPadding = if (compact) 4.dp else 8.dp
      )
    }

    SettingsGlassPanel(modifier = Modifier.animateContentSize()) {
      Column(modifier = Modifier.fillMaxWidth()) {
        items.forEachIndexed { index, item ->
          if (index > 0) {
            SettingsRowDivider(compact = compact)
          }
          Material3SettingsItemRow(item = item, compact = compact, scrollState = scrollState)
        }
      }
    }
  }
}

@Composable
private fun Material3SettingsItemRow(
  item: Material3SettingsItem,
  compact: Boolean = false,
  scrollState: ScrollState? = null,
  highlightFill: Color? = null
) {
  Row(
    modifier =
      Modifier.fillMaxWidth()
        .then(
          if (highlightFill != null) Modifier.background(highlightFill) else Modifier
        )
        .clickable(
          enabled = item.enabled && item.onClick != null,
          onClick = { item.onClick?.invoke() }
        )
        .then(
          if (scrollState != null) Modifier.scrollToOnHighlight(scrollState, item.isHighlighted)
          else Modifier
        )
        .padding(
          horizontal = if (compact) 14.dp else 20.dp,
          vertical = if (compact) 10.dp else 16.dp
        ),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // AGENT.md's monochrome rule, applied app-wide: one neutral tone for every
    // resting row icon, with the Pulse accent reserved for a highlighted row.
    val iconSize = if (compact) 34.dp else 40.dp
    val iconShape = item.iconShape ?: RoundedCornerShape(12.dp)
    val iconTint =
      when {
        !item.enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        item.isHighlighted -> SettingsIconColors.Accent
        else -> SettingsIconColors.Neutral
      }
    // DESIGN.md "Custom Cards": a translucent surfaceVariant tile, never a solid
    // M3 container colour.
    val iconContainerModifier =
      Modifier.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))

    if (item.customIcon != null) {
      Box(
        modifier = Modifier.size(iconSize).clip(iconShape).then(iconContainerModifier),
        contentAlignment = Alignment.Center
      ) {
        item.customIcon.invoke()
      }
      Spacer(modifier = Modifier.width(if (compact) 14.dp else 20.dp))
    } else
      item.icon?.let { icon ->
        Box(
          modifier = Modifier.size(iconSize).clip(iconShape).then(iconContainerModifier),
          contentAlignment = Alignment.Center
        ) {
          if (item.showBadge) {
            BadgedBox(badge = { Badge(containerColor = MaterialTheme.colorScheme.error) }) {
              if (item.tintIcon) {
                Icon(
                  painter = icon,
                  contentDescription = null,
                  tint = iconTint,
                  modifier = Modifier.size(if (compact) 20.dp else 24.dp)
                )
              } else {
                Image(
                  painter = icon,
                  contentDescription = null,
                  modifier = Modifier.size(iconSize),
                  contentScale = ContentScale.Crop
                )
              }
            }
          } else {
            if (item.tintIcon) {
              Icon(
                painter = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(if (compact) 20.dp else 24.dp)
              )
            } else {
              Image(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(iconSize),
                contentScale = ContentScale.Crop
              )
            }
          }
        }

        Spacer(modifier = Modifier.width(if (compact) 12.dp else 16.dp))
      }

    Column(modifier = Modifier.weight(1f)) {
      ProvideTextStyle(
        MaterialTheme.typography.titleMedium.copy(
          color =
            if (!item.enabled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            else MaterialTheme.colorScheme.onSurface
        )
      ) {
        item.title()
      }

      item.description?.let { desc ->
        Spacer(modifier = Modifier.height(2.dp))
        ProvideTextStyle(
          MaterialTheme.typography.bodyMedium.copy(
            color =
              if (!item.enabled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
              else MaterialTheme.colorScheme.onSurfaceVariant
          )
        ) {
          desc()
        }
      }
    }

    item.trailingContent?.let { trailing ->
      Spacer(modifier = Modifier.width(8.dp))
      trailing()
    }
  }
}

data class Material3SettingsItem(
  val icon: Painter? = null,
  val customIcon: (@Composable () -> Unit)? = null,
  val title: @Composable () -> Unit,
  val description: (@Composable () -> Unit)? = null,
  val trailingContent: (@Composable () -> Unit)? = null,
  val showBadge: Boolean = false,
  val isHighlighted: Boolean = false,
  val tintIcon: Boolean = true,
  val iconShape: Shape? = null,
  val enabled: Boolean = true,
  val onClick: (() -> Unit)? = null,
  /**
   * Section this row belongs to. When set, the row opts in to the sectioned
   * treatment: a translucent icon tile and one neutral icon tone, and the screen
   * can group rows via `groupBy { it.section }`. Defaults to null so the
   * sub-page call sites keep rendering exactly as before.
   */
  val section: SettingsSection? = null
)

/** Labelled grouping for settings rows. */
enum class SettingsSection(val label: String) {
  GENERAL("General"),
  EXPERIENCE("Experience"),
  DATA("Data"),
  SYSTEM("System")
}

/** Uppercase section caption above a settings group. */
@Composable
private fun SettingsSectionLabel(
  text: String,
  topPadding: Dp = 8.dp,
  bottomPadding: Dp = 8.dp
) {
  Text(
    text = text.uppercase(),
    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
    color = MaterialTheme.colorScheme.onSurfaceVariant,
    modifier = Modifier.padding(start = 8.dp, top = topPadding, bottom = bottomPadding)
  )
}

/** Hairline separator inset from the row text, so a group reads as one panel. */
@Composable
private fun SettingsRowDivider(compact: Boolean = false) {
  HorizontalDivider(
    thickness = 0.5.dp,
    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
    modifier = Modifier.padding(start = if (compact) 14.dp else 20.dp, end = 20.dp)
  )
}

/**
 * Renders settings rows inside a single liquid-glass panel under an optional
 * section header, with a hairline divider between consecutive rows.
 *
 * Container treatment is shared with [Material3SettingsGroup] via
 * [SettingsGlassPanel], so the main Settings screen and every sub-page use one
 * container. Each row keeps its own `onClick`, `isHighlighted` and
 * scroll-into-view behaviour; only the grouping chrome is new.
 */
@Composable
fun Material3SettingsSectionCard(
  title: String? = null,
  items: List<Material3SettingsItem>,
  scrollState: ScrollState? = null
) {
  if (items.isEmpty()) return
  Column(modifier = Modifier.fillMaxWidth()) {
    title?.let { SettingsSectionLabel(text = it) }
    SettingsGlassPanel {
      Column(modifier = Modifier.fillMaxWidth()) {
        items.forEachIndexed { index, item ->
          if (index > 0) {
            SettingsRowDivider()
          }
          Material3SettingsItemRow(
            item = item,
            scrollState = scrollState,
            // Sparing use of the Pulse accent: a search-highlighted row is the
            // only thing in this list that gets a tinted wash.
            highlightFill =
              if (item.isHighlighted) MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
              else null
          )
        }
      }
    }
  }
}

@Composable
private fun previewSectionItems() =
  listOf(
    Material3SettingsItem(
      section = SettingsSection.EXPERIENCE,
      icon = painterResource(R.drawable.palette),
      title = { Text("Appearance") },
      description = { Text("Theme, colours and layout") }
    ),
    Material3SettingsItem(
      section = SettingsSection.EXPERIENCE,
      icon = painterResource(R.drawable.play),
      title = { Text("Player and audio") },
      description = { Text("Playback, queue and audio quality") },
      // Exercises the sparing-accent highlight treatment.
      isHighlighted = true
    ),
    Material3SettingsItem(
      section = SettingsSection.EXPERIENCE,
      icon = painterResource(R.drawable.group),
      title = { Text("Listen together") },
      description = { Text("Listen with friends in real time") }
    )
  )

/**
 * Representative sub-page rows: a navigation row and a toggle row, matching the
 * shape most settings sub-pages declare through [Material3SettingsGroup].
 */
@Composable
private fun previewGroupItems() =
  listOf(
    Material3SettingsItem(
      icon = painterResource(R.drawable.palette),
      title = { Text("Theme") },
      description = { Text("Pick a colour source and dynamic colour") }
    ),
    Material3SettingsItem(
      icon = painterResource(R.drawable.water_drop),
      title = { Text("Liquid glass") },
      description = { Text("Frosted glass surfaces across the app") },
      trailingContent = { Switch(checked = true, onCheckedChange = {}) }
    ),
    Material3SettingsItem(
      icon = painterResource(R.drawable.speed),
      title = { Text("Enable high refresh rate") },
      description = { Text("Smooth animations on supported displays") },
      trailingContent = { Switch(checked = false, onCheckedChange = {}) }
    )
  )

@Preview(name = "Sub-page group - dark", showBackground = true, widthDp = 400)
@Composable
private fun Material3SettingsGroupPreviewDark() {
  SettingsGroupPreviewWrapper(darkTheme = true)
}

@Preview(name = "Sub-page group - light", showBackground = true, widthDp = 400)
@Composable
private fun Material3SettingsGroupPreviewLight() {
  SettingsGroupPreviewWrapper(darkTheme = false)
}

@Composable
private fun SettingsGroupPreviewWrapper(darkTheme: Boolean) {
  pulseTheme(darkTheme = darkTheme) {
    Surface(color = MaterialTheme.colorScheme.background) {
      PreviewBackdropProvider {
        Column(modifier = Modifier.padding(16.dp)) {
          Material3SettingsGroup(title = "Appearance", items = previewGroupItems())
        }
      }
    }
  }
}

@Preview(name = "Section card - dark", showBackground = true, widthDp = 400)
@Composable
private fun Material3SettingsSectionCardPreviewDark() {
  pulseTheme(darkTheme = true) {
    Surface(color = MaterialTheme.colorScheme.background) {
      PreviewBackdropProvider {
        Column(modifier = Modifier.padding(16.dp)) {
          Material3SettingsSectionCard(
            title = SettingsSection.EXPERIENCE.label,
            items = previewSectionItems()
          )
        }
      }
    }
  }
}

@Preview(name = "Section card - light", showBackground = true, widthDp = 400)
@Composable
private fun Material3SettingsSectionCardPreviewLight() {
  pulseTheme(darkTheme = false) {
    Surface(color = MaterialTheme.colorScheme.background) {
      PreviewBackdropProvider {
        Column(modifier = Modifier.padding(16.dp)) {
          Material3SettingsSectionCard(
            title = SettingsSection.EXPERIENCE.label,
            items = previewSectionItems()
          )
        }
      }
    }
  }
}
