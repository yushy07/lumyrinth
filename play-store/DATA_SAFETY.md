# Google Play Data safety declaration — Lumyrinth V1

Review this against the final built APK/AAB before submitting.

- Does the app collect or share any required user data? **No.**
- Does the app transmit data off the device? **No.**
- Does the app use an account? **No.**
- Does the app contain ads? **No.**
- Does the app use analytics, crash reporting, cloud sync, or third-party tracking SDKs? **No.**
- Session history, preferences, and custom rhythms remain in the device-local Room/DataStore databases.
- Android cloud backup and device-transfer backup are disabled for Lumyrinth app data in V1.
- The app requests vibration only to provide optional breathing cues.
- Notification permission is only used for an optional local daily reminder.

In Play Console: declare no data collected or shared, then ensure the submitted privacy-policy URL says the same thing. Re-check this declaration against the final signed AAB dependency graph before every release.
