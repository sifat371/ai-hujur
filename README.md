# AI Hujur — v9 QA Integration

This branch merges the expanded v8 app features from `islamic-mind (4).zip` with the core safety fixes introduced in `main`.

The v9-qa version is **for local and browser-emulator testing only**. Do not send it to Google Play production.

## What is included

- Quran verse reader, bookmarking, search; Islamic quizzes and other v8 UI modules.
- v1 → v2 non-destructive Room migration for the additional Quran verse table.
- Local persisted settings and prayer opt-in, location, profile and Hijri adjustment.
- Prayer calculations refreshed across date changes; after Isha tomorrow's actual estimated Fajr is used.
- Android speech recognition instead of fabricated questions.
- Simulated login replaced with honest guest-only availability notice.
- Gemini failures return an explicit unavailability message, not invented religious rulings.
- 30-day Ramadan fabricated times are removed from the UI.
- Displayed profile worship statistics come from Room, not fake values.
- Better local data consistency for Salah, Quran, Dhikr logs.

## Known limitations

1. Gemini requires a protected server gateway and verified religious citation review for production. Release builds deliberately contain no Gemini key; debug builds may use an optional developer-only key.
2. Online Google/email login, real cloud backup, shared forums, automatic daily AI reminders, and verified 30-day Sehri/Iftar schedules are **not implemented**, and UI must not claim otherwise.
3. Quran seed dataset is a partial offline sample, not verified as a full Quran corpus; content must be independently checked.
4. Prayer times are astronomical estimates and must be compared with an authoritative local schedule.
5. Release keystore must be supplied by authorized Play Console owner. Never fall back to debug signing.
6. The v8 ZIP provenance is not cryptographically proven identical to Google Play's version 8 artifact.

## Development build

Build on GitHub Actions after uploading this source. The CI pipeline inherited from the GitHub repository runs `:app:assembleDebug :app:testDebugUnitTest`, and publishes a debug APK artifact on success.

This source package was prepared for integration and **has not yet passed a full Android build** in this workspace.
