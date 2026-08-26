# Lumyrinth release status

Last verified: 2026-08-26

## Code and build status

- JDK: 17
- Gradle wrapper: 9.7.1
- Android Gradle Plugin: 9.3.2
- Compile/target SDK: 36
- Minimum SDK: 26
- Version: 1.1.0 (`versionCode` 2)
- Debug signing: Android-generated per-user debug keystore
- Release signing: external `keystore.properties`; credentials are intentionally not stored in Git
- Backup: cloud backup and device-transfer backup disabled
- Analytics, ads, accounts, cloud sync, and Internet permission: none

## Verified commands

The following gates passed with JDK 17 on 2026-08-26:

- `testDebugUnitTest`: 20 tests, 0 failures
- `lintDebug`: 0 errors, 42 non-blocking warnings documented in the HTML/XML reports
- `assembleDebug`: debug APK produced
- `assembleDebugAndroidTest`: instrumented-test APK produced
- `bundleRelease`: minified release AAB produced and verified unsigned
- `git diff --check`: clean

GitHub Actions passed on clean checkout for commit `5be06c9`. Code commit `f6855f3` must pass the same CI workflow after push before it is considered clean-checkout evidence.

## External release gates still required

These cannot be truthfully completed from source code alone:

- Supply a private production keystore and build/install a signed AAB.
- Replace the legal-name, date, and support-email placeholders in the hosted privacy policy.
- Publish the privacy policy at a public HTTPS URL and configure it in Play Console.
- Run Compose journeys and manual accessibility/device QA on an emulator or physical device.
- Complete Play Internal testing and resolve the pre-launch report.
- Profile startup, frame timing, memory, audio, and battery on representative hardware.
- Start staged rollout only after the above gates pass.

Never mark these items complete based only on a successful local build.
