# Al-Hujur AI

Bengali-language Islamic companion Android application built with Kotlin and Jetpack Compose.

> **Project handover / maintenance baseline:** This repository is maintained by [@sifat371](https://github.com/sifat371). The existing application source was provided by the previous maintainer and originates from [mdhasibulhasanofficial-beep/Ai-hujur-](https://github.com/mdhasibulhasanofficial-beep/Ai-hujur-), baseline commit [bca9a04](https://github.com/mdhasibulhasanofficial-beep/Ai-hujur-/commit/bca9a04673e538f7f33f83359502d06c6c63ec98) (September 22, 2026). This is a source snapshot import; the supplied ZIP does not contain the original Git commit history.

## Project status

**Under takeover audit; production readiness not yet verified.** The repository includes a prayer schedule, Hijri calendar, daily Amal tracker, Scholar AI (Gemini), community forum prototype, and Bengali UI. Some visible features are not complete; avoid treating the app as production-ready before a build, device testing, and verification of prayer-time and religious citations.

## Android stack

- Kotlin / Jetpack Compose / Android Gradle Plugin
- Android Room for local Amal activity history
- HTTP-based Gemini client (currently runs on-device and requires security hardening)
- Android alarm and location services

## Build preparation

Open the project in Android Studio and sync Gradle. A Gradle wrapper **JAR is not included** in the upstream snapshot; it must be restored using a trusted Gradle installation before command-line wrapper builds can run.

An `.env.example` placeholder is provided. Keep actual API keys and signing secrets out of Git; **do not ship a production Gemini API key embedded in a mobile APK**. This app's Gemini integration needs to be moved behind a controlled backend before production release.

## Audit priorities

1. Verify local build and run on a physical Android device.
2. Resolve release signing fallback, dependency/build and CI issues.
3. Persist prayer preferences and location correctly across restart/reboot.
4. Replace fake microphone behavior and mark forum as local-only until a server is implemented.
5. Add source-validated religious AI answers, safer Gemini routing and explicit error states.
6. Add database migrations, privacy policy review and sufficient automated tests.

## Ownership and attribution

The original contributors retain credit for their contributions. Before public redistribution or commercial release, confirm applicable source/asset redistribution permissions with the previous owner. No LICENSE file was supplied with the original snapshot.
