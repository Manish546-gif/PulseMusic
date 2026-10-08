# Privacy Policy for Pulse Music App

## Introduction

Pulse Music ("we," "our," or "us") is committed to protecting your privacy. This Privacy Policy explains what information the App stores and shares when you use our mobile application (the "App").

The important thing to know upfront: **Pulse Music has no servers, no accounts, and no backend of its own.** It is an open-source client that runs entirely on your device. That means there is no "collection" of your data on our side — everything below describes where data lives on *your device* and which third-party services you may connect to.

## Information We Store

### 1. On-Device Data

All app data is stored locally on your device in the app's private storage:

- **Music library data**: songs, artists, albums, playlists, liked songs, play counts, search history, recognition history, lyrics, and settings (Room database `song.db` and Jetpack DataStore).
- **Optional account credentials**: If you choose to log in to YouTube Music, Discord, or Spotify, the tokens/profile data used are kept only in the app's private local storage so we can talk to those services on your behalf. Nothing is sent to a Pulse Music server (there is none).
- **Offline downloads**: Downloaded audio/video is stored on your device.

### 2. Analytics & Crash Reporting (GMS flavor only)

- The **GMS** build (Google Play services variant) integrates **Firebase Analytics** and **Firebase Crashlytics** to collect anonymized usage and crash data for stability improvements. This is subject to Google's own privacy policy.
- The **FOSS** build (F-Droid / source-tree flavor) contains **no Firebase, no analytics, and no crash reporting whatsoever.**
- There is no in-app toggle to disable analytics because there is nothing to disable beyond your choice of which build (GMS vs FOSS) you install.

## How Information Is Used

- **App functionality**: Provide streaming, playback, playlists, lyrics, and library features locally on your device.
- **Connected services**: The app talks to YouTube Music, Spotify, discord.com, Odesli/lyric providers, and similar APIs only to fulfill requests you make (play a song, fetch lyrics, sync your own library). These services receive only what is necessary for your request.
- **Quality/improvement (GMS only)**: Firebase crash/usage data is used to identify and fix bugs.

## Data Sharing and Disclosure

### 1. Third-Party Services

- **YouTube / YouTube Music**: accessed through the unofficial InnerTube interface for streaming and library data.
- **Spotify**: accessed when you import playlists or link a Spotify login.
- **Discord**: accessed only when you enable Discord Rich Presence.
- **Google/Firebase (GMS build only)**: receives anonymized analytics/crash data.

### 2. No Account Is Created

Pulse Music does **not** create accounts, does **not** host your playlists or likes on any server we operate, and does **not** "sync your preferences across devices" — any cross-device continuity comes purely from the third-party services you link (e.g., your own YouTube Music account), which are governed by their own privacy policies.

### 3. Legal Requirements

We may disclose information if required by law, or where the source code itself is subject to a rights claim (see README "Legal Disclaimer & Terms of Use").

## Data Storage and Security

### 1. Local Storage

Your playlists, likes, history, and preferences are stored in the app's private on-device storage. No Pulse-operated server receives them.

### 2. Cloud Storage (only via services you link)

Some data may exist in *your own* accounts at Google (YouTube Music), Spotify, or Discord, under those services' privacy policies. Pulse Music does not store this data.

### 3. Data Retention

- Local data persists until you uninstall the app or clear app data.
- Analytics (GMS build) retention is governed by Google's/Firebase's retention settings for the app.
- Android system backup (`allowBackup="true"`) may restore local data on reinstalls/device transfers; offline downloads and ExoPlayer cache are excluded from backup.

## Your Rights and Choices

- **Data access**: View and manage your data through the app's own backup/export feature (Settings → Backup & Restore) and through the linked service dashboards.
- **Data deletion**: Uninstalling the app or clearing app data removes local data. Logging out of linked services (YouTube, Spotify, Discord) removes the stored credentials within the app.
- **Privacy controls**: Choose to install the FOSS build to forgo Firebase entirely; choose which services you connect to.

## Third-Party Service Policies

- **YouTube / YouTube Music**: [YouTube Terms of Service](https://www.youtube.com/t/terms) · [Google Privacy Policy](https://policies.google.com/privacy)
- **Spotify**: [Spotify Privacy Policy](https://www.spotify.com/legal/privacy-policy/)
- **Discord**: [Discord Privacy Policy](https://discord.com/privacy)

These third parties may collect data about your usage of their services as described in their policies.

## Children's Privacy

The App is not intended for children under 13 years of age. We do not knowingly collect personal information from children under 13.

## International Users

Because the app runs locally and Pulse Music operates no servers, there is no Pulse-operated transfer of your data to any jurisdiction. Any interaction with linked services is governed by their own policies and may involve their servers in various countries.

## Changes to This Privacy Policy

We may update this Privacy Policy from time to time. The latest version always lives in this repository (`PRIVACY_POLICY.md`), with the "Last Updated" date below.

## Contact Us

- GitHub repository issues/discussions (link in README).

## Data Protection Compliance

This Privacy Policy is designed to be compatible with:
- **GDPR** (General Data Protection Regulation) for EU users
- **CCPA** (California Consumer Privacy Act) for California users
- **PIPEDA** (Personal Information Protection and Electronic Documents Act) for Canadian users

## Summary

- Pulse Music has **no backend, no accounts, and no server-side storage**.
- All app data is stored **locally on your device**.
- Optional logins (YouTube Music, Spotify, Discord) store credentials **only in the app's private storage** and link to your own external accounts.
- Analytics/crash reporting exists **only in the GMS build**; the FOSS build has none.
- We do not sell or trade your personal data.

---

**By using Pulse Music, you agree to the use of information as described in this Privacy Policy.**

*Last Updated: 2026-09-29*