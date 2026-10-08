# Security Policy

## Supported Versions

We provide security patches and updates for the latest stable major release.

| Version    | Supported          |
| ---------- | ------------------ |
| 1.4.x      | :white_check_mark: |
| older      | :x:                |

## Reporting a Vulnerability

If you discover a security vulnerability in Pulse Music, please report it responsibly:

1. **Do NOT** create a public GitHub issue for undisclosed vulnerabilities.
2. Email us at: [security@pulsemusic.app](mailto:security@pulsemusic.app)
3. Include the following information:
   - Description of the vulnerability
   - Steps to reproduce
   - Potential impact
   - Any suggested fixes

You may alternatively open a **private security advisory** on GitHub.

## Security Best Practices

### For Developers

- **Never commit sensitive files**: API keys, tokens, and credentials must never be committed to version control.
- **Use placeholders, not secrets**: Signing keys, Firebase config, and secret tokens are supplied via excluded files (`google-services.json`, `local.properties`, `secrets.properties`, keystores). Only checked-in templates are allowed.
- **Keep dependencies updated**: Apply patch updates to dependencies (Gradle, AGP, Kotlin, Media3/ExoPlayer, InnerTubeX, etc.) to close known vulnerabilities.
- **Review before merging**: All code changes must be reviewed before merging; see CONTRIBUTING.md.
- **Guard platform APIs**: Any API level > minSdk must be guarded (e.g. `Build.VERSION.SDK_INT` checks) so the app remains safe on supported devices. As of the minSdk 26 baseline, glass/`RenderEffect`-based features must always be gated behind an API-capability check to avoid crashes on older devices.

### For Users

- **Download from official sources**: Only install APKs from official releases or trusted sources (sideload builds only — the app is not distributed via Google Play).
- **Keep the app updated**: Install updates promptly to receive security fixes (in-app OTA updates are permanently removed; download manually from the website).
- **Review permissions and account scope**: The optional YouTube/Discord/Spotify logins are pulled in the app with the narrowest scope required; the app does not run its own backend.

## Sensitive Files (Never Commit)

- `google-services.json` / `app/google-services.json` — Firebase configuration with API keys
- `local.properties` — local development configuration
- `*.keystore` / `*.jks` — app signing keys
- `secrets.properties` — API keys and secrets
- `**/assets/po_token.html` — YouTube bot-check tokens
- Any `.apk` / `.aab` build artifacts

## Data, Privacy & Telemetry

Pulse Music is committed to user privacy:

- **No user-account backend**: The app has no server of its own; there is nothing to leak at rest beyond the device.
- **Local storage**: Playlists, likes, history, settings, and search data are stored locally on the device (Room `song.db` and Jetpack DataStore).
- **Optional third-party identities**: YouTube, Discord, and Spotify credentials/tokens are stored only in the app's private local storage when you explicitly log in, and are used to fetch data from those services. The optional Discord token is stored in the app's DataStore (plaintext, app-private).
- **Analytics/crash reporting**: Firebase Analytics + Crashlytics are compiled **only into the GMS flavor**. The FOSS flavor contains **no Firebase, analytics, or crash reporting**.
- **Open source**: All code is available for review in this repository.

## Known Issues / Notes

- **Unofficial API availability**: Streaming depends on YouTube's unofficial InnerTube interface. This is not an officially supported Google API and can change, break, or be restricted at any time, which may degrade functionality. See README, "Legal Disclaimer & Terms of Use."
- **Android backup**: The app participates in Android system backup (`allowBackup="true"`); offline downloads and ExoPlayer cache are excluded, but the database and settings may be restored on device transfer/reinstall. See PRIVACY_POLICY.md.

## Contact

For security-related questions or to report vulnerabilities:

- Email: [security@pulsemusic.app](mailto:security@pulsemusic.app)
- GitHub: private security advisory (recommended)

Thank you for helping keep Pulse Music secure.