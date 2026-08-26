<p align="center">
  <img src="docs/images/readme-hero.png" alt="Lumyrinth — Find your rhythm" width="100%" />
</p>

<p align="center">
  <a href="#features"><img src="https://img.shields.io/badge/Android-8.0%2B-4E5FB8?style=flat-square&logo=android&logoColor=white" alt="Android 8.0 or newer" /></a>
  <a href="#privacy"><img src="https://img.shields.io/badge/Private-Offline--first-68743B?style=flat-square" alt="Private and offline first" /></a>
  <a href="#built-with"><img src="https://img.shields.io/badge/Jetpack%20Compose-Material%203%20Expressive-6674C4?style=flat-square" alt="Jetpack Compose and Material 3 Expressive" /></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-Apache%202.0-879445?style=flat-square" alt="Apache 2.0 license" /></a>
</p>

<h1 align="center">Lumyrinth</h1>

<p align="center"><strong>Find your rhythm.</strong><br />A quiet breathing companion for calm, focus, rest, and reset.</p>

<p align="center">
  <a href="#the-experience">Experience</a> ·
  <a href="#features">Features</a> ·
  <a href="#design-language">Design language</a> ·
  <a href="#run-it-locally">Run locally</a>
</p>

---

## The experience

Lumyrinth turns a few spare minutes into a guided breathing ritual. Pick a rhythm, follow the living flower as it expands and settles, and finish with a small, useful reflection on how the session went.

The app is designed around four simple goals:

1. Start a breathing session without friction.
2. Experience and complete the guided rhythm.
3. See the session result at a glance.
4. Check progress toward a personal practice goal.

There are no accounts, feeds, ads, or cloud distractions. Your practice stays close, private, and available when you need it.

## Features

| Practice | What it gives you |
| --- | --- |
| **Guided rhythms** | Slow Down, Equal Rhythm, Square, Steady, Nightfall, and Quick Reset presets. |
| **A living breath guide** | An expressive flower grows, holds, and releases with each phase. |
| **Make it yours** | Build custom inhale, hold, exhale, and duration patterns. |
| **Gentle sensory cues** | Optional phase sounds, haptics, and offline ambient soundscapes. |
| **A useful finish** | Session duration, cycles, breathing rate, and a calm completion state. |
| **Progress without pressure** | Local history, mindful minutes, streaks, weekly rhythm, and calendar highlights. |
| **Comfortable by default** | Reduced-motion support, accessible labels, and a wellness-first safety notice. |

## Design language

Lumyrinth now follows a light, expressive Material 3 direction inspired by the Aura reference: soft tonal surfaces, clear hierarchy, generous spacing, and one confident primary action per screen.

| Role | Color | Use |
| --- | --- | --- |
| Canvas | `#F5F3FF` | Calm lavender app background. |
| Elevated surface | `#EDEAFF` | Sheets, grouped controls, and secondary regions. |
| Card surface | `#E5E2FF` | Messages, metrics, rhythm cards, and progress blocks. |
| Primary | `#4E5FB8` | Main actions, progress fill, and navigation emphasis. |
| Warm accent | `#F1F679` | Inhale state, selected days, and gentle moments of focus. |
| Success / secondary | `#68743B` | Selected settings and completed practice states. |

Manrope typography, rounded controls, semantic color roles, and the organic breathing flower keep the interface expressive without bringing back the old neon overload.

## Built with

- Kotlin and Jetpack Compose
- Material 3 and Compose Material Icons
- Room for local sessions and custom rhythms
- DataStore for preferences
- Media3 for bundled ambient audio
- WorkManager for reminders
- KSP for Room schema generation

| Platform | Minimum | Target |
| --- | ---: | ---: |
| Android | API 26 / Android 8.0 | API 36 |

## Project shape

```text
app/src/main/java/com/lumyrinth/app/
├── domain/       breathing models, timing, and progress calculations
├── data/         Room session storage and DataStore preferences
├── audio/        guidance cues and offline ambient soundscapes
├── haptics/      phase feedback
├── notifications/ reminders and rescheduling
└── ui/           Compose screens, components, theme, and navigation
```

## Run it locally

1. Clone the repository and open the folder in Android Studio.
2. Set **Gradle JDK** to **Embedded JDK** in Android Studio.
3. Click **Sync Project with Gradle Files**.
4. Run the `app` configuration on an emulator or Android device.

```bash
git clone https://github.com/yushy07/lumyrinth.git
cd lumyrinth
./gradlew assembleDebug
```

On Windows, use `gradlew.bat assembleDebug`. The project compiles to Java 21 bytecode while using Android Studio's configured JDK to supply the compiler. Debug builds use Android's generated per-user debug keystore. Release signing belongs in an untracked `keystore.properties` based on `keystore.properties.example`.

## Privacy

Lumyrinth has no account system, advertising, analytics, cloud sync, subscription, or Internet permission. Preferences, custom rhythms, and session history remain on the device. Cloud backup and device-transfer backup are disabled for the V1 data model.

Store-listing copy, Data safety notes, privacy-policy templates, and official release assets live in [`play-store/`](play-store/).

## Wellness notice

Lumyrinth is for general relaxation and breathing practice. It is not a medical device and does not diagnose or treat any condition. Breathe comfortably and stop if you feel dizzy or uncomfortable.

## License

Copyright © 2026 **Ayush Kant**. Licensed under the [Apache License 2.0](LICENSE).
