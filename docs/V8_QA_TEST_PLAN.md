# AI Hujur v9 QA smoke testing

## Priority 0 (must not fail)

- Fresh install launches and navigates through Home, Scholar AI, Community, Amal, Profile without crashes.
- Existing installed v1 database migrates to v2 **without deleting** Salah, Quran and Dhikr records. Take a backup before updating any existing production installation.
- Guest profile does not claim login, Google verification, or cloud backup. No password form is shown.
- Scholar AI without a configured key returns an unavailability message, not a fabricated fatwa.
- Ramadan timetable is not populated with invented Sehri/Iftar times.
- A paid Gemini key is never embedded in a release APK.
- Notifications default off; enabling requests Android 13+ permission. Opt-out and selected location survive force-stop and reboot.

## Priority 1

- Quran reader loads seeded verses, search/bookmark persists, and last-read verse updates. Do not assume this is a verified full Quran corpus.
- Local Amal Fajr/Dhuhr toggles, Salah detail updates, Quran pages, Dhikr logs remain consistent.
- Delete log updates aggregate totals and toggling same Salah twice does not duplicate records.
- Date rollover changes active Amal record without restart.
- After Isha the next prayer highlights *tomorrow's* Fajr, not today's passed time.
- Bengali speech input writes editable text into Scholar AI box; no random substituted question.
- Forum posts remain labeled local demo; no simulated human moderation.

## Not yet implemented

Online authentication, real backup/sync, real shared forum, exact official prayer schedule, verified Ramadan calendar, complete Quran texts, secure server-side AI gateway.
