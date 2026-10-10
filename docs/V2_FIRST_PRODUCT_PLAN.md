# AI Hujur — v2-first engineering direction

## Decision (2026-10-10)

The current `main` v2 product is the UX and architecture baseline. The broad `integration/v8-reconciled-qa` branch is a **source/reference snapshot**, not the target to merge. Cherry-pick and redesign *individual verified features*, not its oversized Home UI.

## First milestone: clean feature navigation

- Five bottom tabs remain; Home prioritizes daily greeting, dates, prayer times and today's Amal.
- One compact entry opens a dedicated **Explore** destination, categorizing secondary tools.
- The daily quiz is rebuilt as a self-contained screen with a minimal curated QA bank, not carried over with viral mechanics or unverified counters.
- Existing dialogs continue to function from Explore with no simulated accounts or cloud promises.
- Future modules (full Quran reader, habits, Fajr reminders) must undergo source/content review and isolated testing before exposure.

## Invariants
- No user data migration without preserving v1 Room records.
- No silent cloud/auth/fatwa claims.
- No placeholder-generated complete Quran or fabricated prayer times.
- Debug builds use an isolated `.qa` application ID; release requires real upload signing.
- Do not publish or merge without CI + device walkthrough.

## QA
1. From Home, open Explore. Confirm original daily content remains visible on Home.
2. Open and close every migrated existing tool. Return to Home via back.
3. Open quiz, submit an option, advance, score and restart; rotate/reopen.
4. Check bottom nav remains exactly five tabs.
5. Reopen app to verify Amal settings and records still persist.
6. Confirm notifications, prayer times and compass against real device behavior.
