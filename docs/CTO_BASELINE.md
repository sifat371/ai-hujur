# CTO stabilization audit — October 9, 2026

**Baseline:** original `mdhasibulhasanofficial-beep/Ai-hujur-` commit `bca9a04673e538f7f33f83359502d06c6c63ec98`.

## Fixes included

- Store profile preferences, notification opt-out, selected coordinates and Hijri offset across process restarts.
- Restore prayer alarms after reboot using persisted coordinates and only when notifications are enabled; ignore stale alarms after opt-out.
- Replace microphone's fabricated random questions with Android Bengali speech recognition; review text before submitting.
- Replace silent Gemini fallbacks containing canned religious rulings with a visible service-unavailable response. AI answers and forum replies are not verified scholarly judgments.
- Remove Room destructive migration fallback; database upgrades must carry explicit migrations.
- Restore default Android debug signing; never substitute debug credentials for a release signature.
- Stop embedding debug Gemini API keys in release builds; disable automatic backup of sensitive local records.
- Restore the official Gradle wrapper JAR, add GitHub Actions debug build + unit test CI, add preference regression tests.

## Remaining release blockers

1. The release APK intentionally has no Gemini key. A secure, rate-limited server gateway with abuse protection is required before production AI can work.
2. Prayer calculations and supported calculation methods require comparison with authoritative local schedules. The method dropdown currently is not wired to calculator parameters.
3. Hijri calculations are algorithmic estimates, not official local moon-sighting determinations.
4. Community forum posts and likes are in-memory only; moderation, authentication and persistent multiuser data are unimplemented.
5. Religious citations (including hardcoded text and AI responses) require qualified scholarly review; LLM prompting is not citation verification.
6. AI reminder toggle currently lacks background scheduling implementation.
7. Daily Amal state does not automatically change its date-bound Room subscription at midnight; fix before production. Schema changes require non-destructive migrations.
8. Copyright/assets, Play Console signing custody, the developer contact in privacy policy, notification permission flow, and operating costs need handover and review.
9. Real-device and Play policy validation remain required.

## Verification

- This PR adds CI; **do not claim build/test success until GitHub Actions runs successfully**.
- Test on a real device with settings toggled off, restart, reboot, custom location, Android 13+ notification permissions, and speech recognizer installed.
- Check Gemini with no key and live debug key separately. Release AI must fail closed.
- Do not merge until CI and device smoke testing pass. No new features are within this change.
