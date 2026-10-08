# Pulse — Architecture & Functionality

> Android music player for YouTube Music, built with Kotlin, Jetpack Compose, Media3, and Room.

| | |
|---|---|
| **Application ID** | `com.pulse.music.manish` (debug: `.debug`) |
| **Version** | `versionCode 161`, `versionName 1.4` |
| **Language / Toolchain** | Kotlin, JVM target 21, JDK 21 toolchain |
| **SDK** | `minSdk 26`, `targetSdk 36`, `compileSdk 37` |
| **Build variants** | `Universal`, `arm64`, `armeabi`, `x86`, `x86_64` × `Gms` (Google Play) / `Foss` |
| **Database** | Room, `song.db`, **version 46** |
| **Modules** | 18 |
| **Kotlin LOC** | ~160,000 |

---

## Table of Contents

1. [Module Map](#1-module-map)
2. [Layering & Dependency Direction](#2-layering--dependency-direction)
3. [Data Layer](#3-data-layer)
4. [Network Layer](#4-network-layer)
5. [Playback Layer](#5-playback-layer)
6. [UI Layer](#6-ui-layer)
7. [Feature Inventory](#7-feature-inventory)
8. [Provider Modules](#8-provider-modules)
9. [Startup Pipeline](#9-startup-pipeline)
10. [Build System & Flavors](#10-build-system--flavors)
11. [Platform Integration](#11-platform-integration)
12. [Known Architectural Debt](#12-known-architectural-debt)

---

## 1. Module Map

18 Gradle modules. Provider modules are intentionally tiny and independently replaceable.

| Module | Lines | Responsibility |
|---|---:|---|
| `:app` | 130,919 | Everything user-facing: UI, playback service, ViewModels, DI, all screens |
| `:innertube` | 9,611 | YouTube InnerTube API client — `YouTube.kt` facade (2,526 lines) + response models |
| `:core` | 5,617 | `MusicDatabase`, entities, DAOs, preference keys, shared constants |
| `:lyrics` | 4,183 | Lyrics provider registry, time-sync, translation, AI (DeepL/Mistral/OpenRouter) |
| `:playback` | 2,787 | Queue types, audio DSP chain, EQ, sleep timer, beat analysis |
| `:shazamkit` | 643 | Shazam music-recognition client |
| `:applecanvas` | 557 | Apple Music Canvas provider |
| `:canvas` | 546 | Canvas artwork orchestration (Apple Music, Tidal) |
| `:betterlyrics` | 495 | BetterLyrics provider + TTML parser |
| `:paxsenixlyrics` | 503 | PaxSenix lyrics provider |
| `:lrclib` | 461 | LrcLib lyrics provider |
| `:kugou` | 277 | KuGou lyrics provider |
| `:youlyplus` | 266 | YouLyPlus lyrics provider |
| `:artistvideo` | 245 | Artist-video / animated canvas provider |
| `:unison` | 225 | Unison lyrics provider |
| `:simpmusic` | 197 | SimpMusic lyrics provider |
| `:pulsecanvas` | 101 | First-party Canvas provider |

### `:app` internal package layout

| Package | Contents |
|---|---|
| `ai/` | `AiPlaylistGenerator`, `AiPlaylistModifier`, `AiRecommendationWorker`, weather cover overlay, IP geolocation |
| `api/` | `FlowNeuroengineApi` |
| `constants/` | Preference keys (304 keys), enums, dimensions |
| `data/` | `SponsorBlockRepository` |
| `di/` | `AppModule`, `NetworkModule` (Hilt) |
| `discord/` | Full Discord Rich Presence: OAuth, Gateway WS client, social presence |
| `extensions/` | Compose/Kotlin extension functions |
| `listentogether/` | `ListenTogetherManager` (1,641), `ListenTogetherClient` (1,630) — realtime sync rooms |
| `localmedia/` | `LocalSongScanner`, `SupportedLocalAudio` |
| `playback/` | `MusicService` (4,626), `PlayerConnection`, queue orchestration |
| `pulse/` | Pulse-branded subsystem: updater, changelog screen, commit screen, `AudioDeviceBottomSheet` |
| `quicksettings/` | `MusicRecognizerTileService` (Android QS tile) |
| `recognition/` | `MusicRecognitionService`, signature generation, resampling, foreground service |
| `spotify/` | `Spotify.kt` (1,616) API client |
| `spotifyimport/` | Spotify playlist import repository + ViewModel |
| `ui/` | All Compose UI — `screens/`, `component/`, `player/`, `menu/`, `theme/` |
| `utils/` | `cipher/` (decipher), `potoken/` (PoToken WebView), `sabr/` (EJS solver), `lastfm/` |
| `viewmodels/` | 35 ViewModels |
| `widget/` | Home-screen widgets + `PulseWidgetManager`, turntable, playlist widgets |

---

## 2. Layering & Dependency Direction

```
┌──────────────────────────────────────────────────────────────┐
│  UI (Jetpack Compose)                                        │
│  ui/screens · ui/component · ui/player · ui/menu           │
│  Navigation Compose · 3-tab NavigationBar/Rail              │
└───────────────┬───────────────────────────┬──────────────────┘
                │ collectAsState             │ hiltViewModel()
                ▼                            ▼
┌──────────────────────────────┐  ┌────────────────────────────┐
│  ViewModels (35)             │  │  PlayerConnection          │
│  StateFlow-based, no XML     │  │  UI ↔ ExoPlayer bridge     │
└───────┬──────────────┬───────┘  └───────────┬────────────────┘
        │              │                       │
        ▼              ▼                       ▼
┌───────────────┐  ┌─────────────────┐  ┌──────────────────────┐
│ :core Room    │  │ :innertube      │  │ :playback            │
│ Database/DAO  │  │ YouTube API     │  │ Queue · DSP · EQ     │
└───────────────┘  └─────────────────┘  └──────────────────────┘
        │                                       ▲
        │  providers (:lyrics, :canvas, ...)   │
        └───────────────────────────────────────┘
```

**Rule of thumb:** `ui → viewmodels → (core | innertube | playback)`. Providers are leaf modules with no dependency on `:app`.

---

## 3. Data Layer

### Room database

`core/db/MusicDatabase.kt` — `InternalDatabase`, `song.db`, **schema version 46**, **40+ auto-migrations**, no destructive fallback (deliberate: user data preservation).

**Entities (17)**

| Entity | Purpose |
|---|---|
| `SongEntity` | Canonical local song record |
| `AlbumEntity` | Album metadata |
| `ArtistEntity` | Artist metadata |
| `PlaylistEntity` | Playlist header |
| `FormatEntity` | Stream format metadata |
| `LyricsEntity` | Cached lyrics |
| `Event` | Listening events (play counts, history) |
| `PlayCountEntity` | Per-song play counts |
| `BeatInfoEntity` | Beat/analysis data |
| `FormatEntity`, `SetVideoIdEntity` | Set-video + format lookup |
| `SpeedDialItem` | Home-screen speed dial config |
| `SearchHistory` | Search query history |
| `RecognitionHistory` | Music-recognition history |
| `AlbumArtistMap`, `AlbumArtistMap` | M2M join tables |
| `PlaylistSongMap`, `SongAlbumMap`, `SongArtistMap`, `RelatedSongMap` | M2M join tables |

**DAOs** — `core/db/DatabaseDao.kt` (1,766 lines) + `db/daos/SpeedDialDao.kt`.

**Hilt provisioning** — `AppModule.kt` (`provideDatabase`), built with `Room.databaseBuilder(...).addMigrations(...)`.

### Preferences

`DataStore` (Preferences) — **304 keys** in `core/constants/PreferenceKeys.kt`.
Accessed via `core/utils/DataStoreCompose.kt` (`rememberPreference` composable) and `utils/DataStore.kt`.

> The `dataStore[key]` operator is `runBlocking(Dispatchers.IO)`. Prefer `dataStore.data.first()` off the main thread; `remember { dataStore[key] }` inside composition blocks the first frame.

---

## 4. Network Layer

| Concern | Implementation |
|---|---|
| InnerTube HTTP | `innertube/InnerTube.kt` — Ktor `HttpClient(OkHttp)` |
| API surface | `innertube/YouTube.kt` — `suspend` functions: `home()`, `explore()`, `search()`, `browse()`, `player()`, `next()`, `queue()`, `feedback()`, `suggestions()`, `transcript()` |
| Auth | `YouTube.cookie` — session cookie forwarded on every call |
| Retry | `withRetry` wrapper around InnerTube calls |
| Secondary providers | Ktor (`lyrics` providers, `ai`), Retrofit + Gson (`LastFM`, `ListenBrainz`) |
| Image loading | Coil 3 + `coil-network-okhttp` |
| Playback transport | Cronet (`play-services-cronet`) preferred, OkHttp fallback, Media3 HLS |
| Connectivity | `NetworkConnectivityObserver` (Hilt singleton) |
| EJS/SABR | `utils/sabr/EjsNTransformSolver` — SABR transform solving |

**Security note:** `res/xml/network_security_config.xml` sets `cleartextTrafficPermitted="true"` on `base-config`, which permits plaintext HTTP to *all* domains rather than only the Listen Together local server.

---

## 5. Playback Layer

### `MusicService` (`:app/playback/MusicService.kt`, 4,626 lines)

`MediaLibraryService` subclass — serves Android Auto, Bluetooth, Wear, and the system media session.

- **Engine:** Media3 `ExoPlayer`, custom `DefaultRenderersFactory` + `DefaultAudioSink`
- **Offload:** audio offload path for low-power playback
- **State exposed as `StateFlow`** consumed by `PlayerConnection`
- **Chunking:** `ChunkingDataSource`, `CHUNK_LENGTH = 512 KiB`
- **Load control:** custom `DefaultLoadControl`
- **Stats:** `AnalyticsListener` + `PlaybackStatsListener`
- **Queue mode:** shuffle order via `DefaultShuffleOrder`
- **Features wired here:** crossfade (+ gapless), automix, sleep timer, ducking

### `PlayerConnection` (`:app/playback/PlayerConnection.kt`)

Single UI-facing bridge to the service. Binds to `MusicService`, exposes the player, and turns player state into flows:

```
currentSong · currentLyrics · currentFormat · playbackState · isPlaying
isEffectivelyPlaying · mediaMetadata · queueTitle · queueWindows
currentMediaItemIndex · currentWindowIndex · shuffleModeEnabled · repeatMode
canSkipPrevious · canSkipNext · error · isMuted · isCrossfading · isAutomixing
```

Also owns `playQueue(queue: Queue)` and `startRadioSeamlessly()`.

### `:playback` module

| Area | Types |
|---|---|
| Queues | `Queue`, `ListQueue`, `EmptyQueue`, `YouTubeQueue`, `YouTubePlaylistQueue`, `LocalMixQueue`, `LocalAlbumRadio`, `YouTubeAlbumRadio`, `QueueExt` |
| DSP | `ParametricEQ`, `BiquadFilter`, `CustomEqualizerAudioProcessor`, `StereoWidenerAudioProcessor`, `AutomixDuckAudioProcessor`, `SilenceDetectorAudioProcessor` |
| Analysis | `BeatAnalyzer`, `BiquadFilter`, `FilterType` |
| Persistence | `EQProfileRepository`, `EqualizerService` |
| Utility | `SleepTimer` |

---

## 6. UI Layer

### Navigation

- **Navigation Compose** `NavHost`, routes declared in `ui/screens/Screens.kt`
- **Navigation destinations** — `Screens.MainScreens` = `Home`, `Search`, `ListenTogether`, `Library` (4 items). Driven by the `ListenTogetherInTopBarKey` preference: when **on**, `ListenTogether` is filtered out of the nav bar/rail and promoted to a top-bar action, leaving 3 visible tabs; when **off**, all 4 appear.
- **Default open tab** — `DefaultOpenTabKey` ∈ `NavigationTab` = `HOME` | `SEARCH` | `LIBRARY`. Resolved in `MainActivity.kt:757`.
- Bottom bar and rail variants: `AppNavigationBar`, `AppNavigationRail`
- Custom 400 ms slide/fade transitions; zero-length during cold start
- Adaptive layouts via `androidx.adaptive` (layout, navigation)
- **Overflow sheet** (`AppNavigationRail`) hides Shuffle and the AI Hub behind a `more_horiz` button

### Screens (~75 files under `ui/screens/`)

Notable groups:

| Group | Screens |
|---|---|
| Feed | `HomeScreen`, `ExploreScreen`, `MoodAndGenresScreen`, `ChartsScreen`, `NewReleaseScreen`, `TopPlaylistScreen`, `YouTubeBrowseScreen` |
| Library | `LibraryScreen`, `LibrarySongsScreen`, `LibraryAlbumsScreen`, `LibraryArtistsScreen`, `LibraryPlaylistsScreen`, `LibraryMixScreen` |
| Playlists | `LocalPlaylistScreen`, `OnlinePlaylistScreen`, `CachePlaylistScreen`, `BottomPlaylistScreen`, `AutoPlaylistScreen` |
| Entity detail | `AlbumScreen`, `ArtistScreen`, `ArtistSongsScreen`, `ArtistAlbumsScreen`, `ArtistItemsScreen` |
| Search | `SearchScreen`, `OnlineSearchScreen`, `LocalSearchScreen` |
| Player | `PlayerSettings`, `AxionEqScreen`, `EQScreen`, `CircularEqControl` |
| Settings | `SettingsScreen`, `SearchableSettings` (2,285), `AppearanceSettings` (2,001), `DiscordSettings`, `PrivacySettings`, `StorageSettings`, `ContentSettings`, `UpdateSettings`, `ListenTogetherSettings`, `LastFMSettingsScreen`, `GlassEffectSettings` |
| Social | `ListenTogetherScreen`, `CommentSheet`, `CommentTogether`, `DiscordExperimental` |
| Utility | `StatsScreen`, `ActivityHistory`, `ListeningSummaryScreen`, `DetailedListeningHistoryScreen`, `BackupAndRestore`, `SpotifyImportScreen`, `RecognitionScreen`, `AmbientModeScreen`, `WelcomeDialog`, `CrashActivity` |

### Shared components (`ui/component/`, 54 files)

`EmptyPlaceholder`, `GlassEffect`, `BottomSheetMenu`, `Items`, `Lyrics` / `LyricsV2` / `MetroLyrics` / `PulseLyrics`, `ChipsRow`, `SearchBar`, `SortHeader`, `Preference`, `Material3SettingsGroup`, `PlayerSlider`, `BigSeekBar`, `SquigglySlider`, `WavySlider`, `VolumeSlider`, `RingtoneTrimmerDialog`, `PlaybackLogsDialog`, `CreateAiPlaylistDialog`, `GridMenu`, `HeartBurstIcon`, `DraggableScrollBarOverlay`, `AmbientGlowBackground`

### Theme

`ThemeScreen`, `FontSelectionScreen`, `ColorPicker`, `AppIconSettingsScreen`, dynamic color (Material You), AMOLED/pure-black option, glass/blur effect configuration, backdrop system (`rememberLayerBackdrop`).

---

## 7. Feature Inventory

### Playback
Sleep timer · Crossfade with gapless · Automix (with ducking) · Shuffle & repeat · Audio offload · Ringtone trim/apply · Volume normalization via loudness enhancer · Parametric EQ + Axion EQ + stereo widener · Chunky progressive streaming · Auto/music-queue radio (album radio, playlist radio)

### Library & data
Local media scan (`LocalSongScanner`) · Online playlists · Cache playlists · Auto-playlists (rule-based) · Playlist import (Spotify) · Backup & restore · Listening history + play counts · Detailed listening stats & summaries

### Discovery
YouTube Home · Explore · Charts · New releases · Top playlists · Mood & genres · Browse · Search + suggestions · AI playlist generation and AI playlist modification (worker-based) · Recommendations

### Lyrics
Multiple providers (`YouTube`, `YouTubeSubtitle`, `KuGou`, `LrcLib`, `BetterLyrics`, `PaxSenix`, `SimpMusic`, `YouLyPlus`, `Unison`) · Registry-based selection · Time-sync with multiple renderers · Translation via DeepL · AI via Mistral / OpenRouter (streaming)

### Integrations
Discord Rich Presence (OAuth + Gateway WebSocket) · Android Auto (`MediaLibraryService`) · Listen Together realtime rooms · Cast (flavour-gated) · Last.fm scrobbling · ListenBrainz · SponsorBlock · Home-screen widgets (music, playlist, turntable, recognizer) · Quick Settings tile

### Settings surface
`SearchableSettings` with 304 preference keys spanning appearance, playback, audio, lyrics, privacy, storage, network, integrations, notifications, and accessibility.

---

## 8. Provider Modules

All providers implement a small interface and are registered centrally.

**Lyrics** — `lyrics/LyricsProviderRegistry` aggregates:
`YouTubeLyricsProvider`, `YouTubeSubtitleLyricsProvider`, `KuGouLyricsProvider`, `LrcLibLyricsProvider`, `BetterLyricsProvider` (+ `TTMLParser`), `PaxSenixLyricsProvider`, `SimpMusicLyricsProvider`, `YouLyPlusLyricsProvider`, `UnisonLyricsProvider`

**Canvas / artwork** — `AppleMusicCanvasProvider`, `AppleMusicTokenProvider`, `TidalCanvasProvider`, `AppleMusicArtistBackgroundProvider`, `PulseCanvasProvider`, `ArtistVideoCanvasProvider`

**Recognition** — `:shazamkit` (`Shazam`, `ShazamModels`), signature generation in `:app/recognition`

---

## 9. Startup Pipeline

Current cold-start design (see `utils/StartupGate.kt`):

```
Process start
 └─ App.onCreate              StartupGate.mark("application-onCreate")
     ├─ CipherDeobfuscator.initialize()      ← main-thread asset + file I/O + JSON parse
     ├─ YTPlayerUtils.initialize()           ← launches PoToken WebView on Dispatchers.Main
     └─ settings + DNS prefetch              ← Dispatchers.IO

MainActivity.onCreate
 ├─ installSplashScreen().setKeepOnScreenCondition { StartupGate.shouldKeepSplash() }
 ├─ 3 s hard timeout → StartupGate.forceRelease()
 ├─ MusicService warmup                       ← deferred 150 ms
 ├─ app language read                         ← moved off main thread
 └─ pulseApp() → StartupGate.markComposed()    ← first Compose frame

HomeViewModel.load()
 ├─ HomeCache.read()          cached Home/Explore → render immediately
 ├─ loadLocalDataPhase()
 ├─ loadNetworkDataPhase()
 │    ├─ AWAIT: YouTube.home() + YouTube.explore()      (critical)
 │    └─ DEFERRED: daily discover, community playlists,
 │                 similar recommendations, account playlists
 └─ HomeCache.write()

HomeScreen first meaningful content → StartupGate.markHomeReady() → splash releases
```

**Artifacts:** `cacheDir/home_explore_snapshot.json` via `utils/HomeCache.kt`. Serialization enabled on InnerTube models (`HomePage`, `ExplorePage`, `YTItem` hierarchy, `MoodAndGenres.Item`).

**Measurement:** `StartupGate.mark()` logs milestones to logcat under tag `StartupGate`.

---

## 10. Build System & Flavors

### Product flavours

| Axis | Values |
|---|---|
| Architecture | `universal`, `arm64`, `armeabi`, `x86`, `x86_64` |
| Distribution | `gms` (Google Play), `foss` (F-Droid) |
| Build type | `debug` (`.debug` suffix), `release` |

`CAST_AVAILABLE` is `false` for `foss`, `true` for `gms`.

### Release build

```kotlin
isMinifyEnabled = true
isShrinkResources = true
isDebuggable = false
proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")  // 186 lines
```

### Configuration

| Key | Source |
|---|---|
| `GH_CLIENT_ID` / `GH_CLIENT_SECRET` | `local.properties` → env var → `""` |
| `STORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`, `keystore.jks` | env var / local file; falls back to the Android debug keystore |
| `LASTFM_API_KEY` / `LASTFM_SECRET` | **hardcoded literals in `app/build.gradle.kts`** |
| `DISCORD_APPLICATION_ID`, `DISCORD_REDIRECT_SCHEME` | literals in `build.gradle.kts`, injected as `buildConfigField` + `manifestPlaceholders` |
| `IS_NIGHTLY` | build-time flag |
| `PO_TOKEN` Google API key | `utils/potoken/PoTokenWebView.kt:337` (public InnerTube web key) |

### Tasks

```bash
./gradlew compileArm64GmsDebugKotlin      # compile only
./gradlew assembleUniversalGmsRelease     # signed release APK
./gradlew assembleUniversalFossDebug     # unsigned debug APK
```

### CI

`.github/workflows/android-build.yml` — builds a signed release APK (falls back to debug for fork PRs), uploads the artifact. `codeql.yml` runs CodeQL.

> CI does **not** run `test`, `lint`, or any static analysis.

---

## 11. Platform Integration

| Component | Implementation |
|---|---|
| Foreground service | `MusicService` (`FOREGROUND_SERVICE_MEDIA_PLAYBACK`) |
| Media session | `androidx.media3.session` — Android Auto, Bluetooth, Wear |
| Recognition service | `RecognitionForegroundService` (`FOREGROUND_SERVICE_MICROPHONE`) |
| Notification | `POST_NOTIFICATIONS` |
| Widgets | `PulseWidgetManager`, `MusicWidgetReceiver`, `TurntableWidgetReceiver`, `PlaylistWidgetReceiver`, `MusicRecognizerWidgetReceiver` |
| Quick Settings | `MusicRecognizerTileService` |
| Background work | WorkManager (`AiRecommendationWorker`) |
| Boot | `RECEIVE_BOOT_COMPLETED` |

**Permissions:** `INTERNET`, `POST_NOTIFICATIONS`, `ACCESS_NETWORK_STATE`, `ACCESS_COARSE_LOCATION`, `ACCESS_FINE_LOCATION`, `RECEIVE_BOOT_COMPLETED`, `WAKE_LOCK`, `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_MEDIA_PLAYBACK`, `FOREGROUND_SERVICE_DATA_SYNC`, `FOREGROUND_SERVICE_MICROPHONE`, `RECORD_AUDIO`, `READ_MEDIA_AUDIO`, `READ_EXTERNAL_STORAGE`, `WRITE_SETTINGS`, `BLUETOOTH_CONNECT`, `BLUETOOTH` (≤30), `BLUETOOTH_ADMIN` (≤30), `REQUEST_INSTALL_PACKAGES`

**Logging:** Timber (debug tree only in `BuildConfig.DEBUG`).

---

## 12. Known Architectural Debt

### Size concentration

| File | Lines |
|---|---:|
| `playback/MusicService.kt` | 4,626 |
| `ui/player/Player.kt` | 3,156 |
| `ui/component/Lyrics.kt` | 2,538 |
| `innertube/YouTube.kt` | 2,526 |
| `ui/screens/settings/SearchableSettings.kt` | 2,285 |
| `lyrics/LyricsUtils.kt` | 2,091 |
| `ui/menu/PlayerMenu.kt` | 2,037 |
| `ui/screens/settings/AppearanceSettings.kt` | 2,001 |
| `core/db/DatabaseDao.kt` | 1,766 |

`MusicService` combines playback state, notification building, media session, queue logic, and DSP wiring in one class.

### Concurrency

- **39** `runBlocking` call sites, including inside coroutines (deadlock risk under load)
- **4** `GlobalScope` usages
- **210** `!!` force-unwraps — concentrated in playback paths, turning malformed server responses into crashes mid-playback
- **11** empty `catch` blocks

### Testing

**4 test files total** across the whole repository (3 in `:innertube`, 1 in `:canvas`). None for `:app`. CI never executes them.

### Security

- LastFM API key **and** shared secret are literal values in `app/build.gradle.kts` (the adjacent comment states they come from GitHub Secrets, which is not the case)
- `network_security_config.xml` permits cleartext to all domains instead of scoping to the Listen Together local server

### Internationalisation

Base `values/strings.xml` has ~1,056 keys. Major locales (`es`, `it`, `ja`, `ru`, `zh-rCN`, `th`, `tr`) sit near 613 — roughly 40% untranslated. Several locales are effectively stubs: `values-hi` (8 keys), `values-km` (20), `values-ta` (23), `values-wae` (0).

### Build

- No baseline profile (`baseline-prof.txt` absent)
- No `detekt`/`ktlint`/static-analysis step in CI
- No `lint` gate

### UI consistency

- `EmptyPlaceholder` exists and is used 23×, but there is **no shared error/offline state component**; `errorMessage` appears 67 times, suggesting per-screen bespoke failure handling
- `EmptyPlaceholder` hardcodes `fillMaxSize()` and exposes no action slot, so empty states cannot offer a recovery action and cannot be used in lists or grids
- No first-run onboarding flow (`firstRun` / `isFirstLaunch` logic is absent from the codebase)