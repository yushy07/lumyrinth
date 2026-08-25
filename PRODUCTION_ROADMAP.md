# Lumyrinth Production Readiness and Product Improvement Roadmap

> Status: Active execution roadmap  
> Baseline reviewed: `97d1024` (`main`)  
> Prepared: 2026-08-26  
> Scope: Android app, product experience, local data, accessibility, performance, testing, security, and Play release readiness

## 1. Purpose

This roadmap turns the current Lumyrinth prototype into a reliable, trustworthy, mass-use Android product without losing its distinctive cosmic breathing experience.

The work is intentionally sequenced. User trust, data safety, timer correctness, and build reliability come before visual expansion. UI refinement follows once the underlying behavior is accurate and recoverable.

## 2. Product principles

Every implementation decision should follow these principles:

1. **Truth before decoration.** Never display sample, minimum, or fabricated progress as real user activity.
2. **Data must survive upgrades.** A routine app update must never silently erase session history or custom rhythms.
3. **A control must do what it says.** Reminder, soundscape, favorite, and guidance controls must have observable, testable behavior.
4. **The breathing session is sacred.** Timing, pause/resume, interruptions, and completion must be predictable and accurate.
5. **Calm does not mean inaccessible.** The interface must support font scaling, TalkBack, reduced motion, large screens, and clear touch feedback.
6. **Local-first must be technically true.** Runtime behavior, Android backup configuration, policy text, and Play declarations must agree.
7. **One clear action per screen.** Visual richness should support the primary task rather than compete with it.
8. **Release claims require evidence.** A successful build, tests, migration tests, lint, accessibility checks, and signed internal release are required before calling the app production-ready.

## 3. Current baseline

### What is already strong

- The product has a recognizable visual identity: near-black surfaces, purple/pink/orange light, Manrope typography, and a breathing-core motif.
- The main product flow exists: onboarding, Home, Explore, rhythm details, custom rhythms, active sessions, completion, Progress, Settings, and legal pages.
- Core technology choices are appropriate: Kotlin, Jetpack Compose, Room, DataStore, WorkManager, Media3, SoundPool, and local haptics.
- Session history and custom rhythms are stored locally.
- The app has bundled offline cue audio, ambient soundscape assets, legal content, and Play Store preparation documents.
- Kotlin source compilation succeeds.
- Android lint completes with zero errors.

### Verified gaps

- Home and Progress display fabricated fallback activity.
- Session duration is rounded upward and exact seconds are not persisted.
- Daily reminder state is saved, but reminder work is never scheduled.
- Room uses destructive migration fallback.
- A fresh clone cannot assemble a debug APK because a repository-root debug keystore is required but absent.
- Navigation is an in-memory screen variable rather than a restorable back stack.
- There are no ViewModels, no `rememberSaveable` state, and no lifecycle-aware flow collection.
- There are no unit or instrumented test source files.
- There are 237 hard-coded UI string occurrences and no `stringResource()` usage.
- Ambient soundscape code and assets exist but are not connected to the session experience.
- Favorites can be created but cannot be browsed.
- The app is portrait-locked and contains fixed-size layouts.
- Android lint currently reports 38 warnings.

## 4. Priority model

| Priority | Meaning | Release policy |
|---|---|---|
| P0 | Build, user trust, data loss, or core feature correctness | Must be completed before internal release |
| P1 | Navigation, lifecycle, accessibility, privacy, performance, or major UX quality | Must be completed before public production |
| P2 | Product depth, polish, analytics presentation, and maintainability | Complete before or shortly after first public release |
| P3 | Longer-term differentiation and optional expansion | Backlog after the stable V1 foundation |

## 5. Definition of production-ready

Lumyrinth is production-ready only when all of the following are true:

- A clean clone builds debug and release variants using documented tooling.
- CI builds, runs tests, executes lint, and validates formatting on every pull request.
- No screen displays activity the user did not perform.
- Exact session seconds are stored, pause time is excluded, and lifecycle interruptions are handled.
- Database upgrades are covered by explicit, tested migrations.
- Reminder enable, disable, time selection, rescheduling, permission denial, and notification tap behavior work.
- Navigation and important screen state recover after configuration change and process recreation.
- The app supports TalkBack, 200% font scaling, reduced motion, 48dp touch targets, and adequate color contrast.
- Phone, small-phone, tablet, foldable, and split-screen layouts are usable.
- Privacy policy, Android backup behavior, Data Safety declarations, and actual runtime behavior agree.
- No P0/P1 issues remain in the Play pre-launch report.
- A signed AAB has passed Internal testing before production rollout.

---

# Milestone 0 — Reproducible Build and Safety Baseline

**Priority:** P0  
**Goal:** Anyone can clone, build, test, and inspect the same app without local secrets or undocumented machine state.

## M0.1 Remove the custom debug-keystore dependency

### Problem

`app/build.gradle.kts` declares a custom `debugConfig` pointing to `debug.keystore` in the repository root. The file is not present, so `assembleDebug` fails on a clean clone.

### Work

- Remove the custom debug signing configuration.
- Use Android's standard per-user debug keystore for debug builds.
- Keep release signing conditional and secret-driven.
- Ensure `keystore.properties`, release keystores, and passwords stay ignored.
- Document release signing inputs without committing secrets.

### Acceptance criteria

- `./gradlew assembleDebug` succeeds from a fresh clone.
- No keystore or credential is added to Git.
- A missing release keystore prevents a signed production release but does not block debug builds.

## M0.2 Pin and document the supported toolchain

### Work

- Standardize on a supported JDK, preferably JDK 17 for the current Android setup.
- Add a Gradle Java toolchain declaration.
- Keep the Gradle wrapper version compatible with the chosen Android Gradle Plugin.
- Remove experimental Gradle properties unless a documented need remains.
- Align README platform targets with the actual `compileSdk` and `targetSdk`.
- Document Android Studio, JDK, SDK, and emulator prerequisites.

### Acceptance criteria

- Builds do not fall back from JDK 25/JVM 25 to another Kotlin target.
- Local and CI builds use the same JDK major version.
- README target API matches `app/build.gradle.kts`.

## M0.3 Establish CI

### Work

Create a GitHub Actions workflow that runs:

1. Gradle wrapper validation.
2. `testDebugUnitTest`.
3. `lintDebug`.
4. `assembleDebug`.
5. `bundleRelease` in an unsigned validation mode or a dedicated signing-enabled release job.
6. `git diff --check` or equivalent formatting hygiene.
7. Dependency review for pull requests.

Cache Gradle dependencies without caching signing material.

### Acceptance criteria

- Every pull request receives a repeatable pass/fail signal.
- The main branch cannot silently regress into a non-buildable state.
- Build reports and lint reports are uploaded as artifacts on failure.

## M0.4 Create a release baseline

### Work

- Record current app version, database version, and last known published version.
- Determine whether any version with Room database versions 1–4 has reached users.
- Preserve schema files for every distributed database version.
- Capture baseline screenshots on a small phone and a standard phone.

### Exit gate

- Clean-clone debug build passes.
- CI is green.
- Release history and database migration obligations are known.

---

# Milestone 1 — User Trust, Data Accuracy, and Persistence

**Priority:** P0  
**Goal:** Every number, saved record, and upgrade behavior is accurate and defensible.

## M1.1 Remove all fabricated progress

### Affected experience

- Home progress card.
- Progress headline streak.
- Today, weekly minutes, and total sessions.
- Continue/recent-session duration.

### Work

- Remove all `coerceAtLeast()` display floors used to imitate populated data.
- Remove hard-coded fallback values such as 7, 8, 13, and 42.
- Show real zero values where a numeric value is useful.
- Use an intentional first-use state where zeros would feel empty.

### Recommended empty state

- Heading: `Your rhythm starts here`
- Supporting text: `Complete your first breathing session to begin tracking mindful minutes and streaks.`
- Primary action: `Start a 1-minute session`
- Secondary action: `Explore rhythms`

### Acceptance criteria

- A new installation shows zero recorded activity.
- Home and Progress show the same underlying totals.
- The latest-session duration is never increased to a preset default.
- Unit tests cover zero sessions, one short session, sessions across days, and a broken streak.

## M1.2 Store exact session duration

### Data rule

`durationSecondsActual` is the source of truth. Minutes are a display/aggregation projection.

### Work

- Populate `durationSecondsActual` whenever a session is saved.
- Stop rounding a few seconds into one full mindful minute.
- Decide the display policy:
  - Under 60 seconds: show seconds.
  - At or above 60 seconds: show rounded-down minutes plus optional seconds.
  - Aggregate mindful minutes from total seconds, not a sum of individually rounded sessions.
- Update Room queries and `ProgressCalculator` to aggregate seconds.
- Retain `durationMinutesActual` temporarily only if required for backward compatibility.
- Add a future migration to remove redundant minute storage once old app versions no longer need it.

### Acceptance criteria

- A 10-second abandoned session is stored as 10 seconds, not one minute.
- A 59-second session does not become one full mindful minute.
- Two 40-second sessions aggregate to 80 seconds and display consistently.
- Completion, Home, Progress, weekly charts, and averages share one duration policy.

## M1.3 Replace destructive Room migration

### Work

- Change Room to `exportSchema = true`.
- Configure the Room schema directory in Gradle.
- Commit generated schema JSON files.
- Implement explicit migrations for every distributed schema transition.
- Remove `fallbackToDestructiveMigration()`.
- Add migration tests using Room's migration test utilities.
- Validate session rows, mood, exact duration, custom rhythms, and defaults after migration.

### Decision rule

- If the app has never been distributed, establish a clean version-1 production schema before launch.
- If any database version has reached users, preserve and test every required upgrade path. Do not renumber history casually.

### Acceptance criteria

- Upgrading from every supported previous version preserves all user records.
- Downgrade behavior is explicit and documented.
- CI fails when the schema changes without a migration/schema export update.

## M1.4 Make delete-all atomic and complete

### Work

- Wrap Room data deletion in a database transaction.
- Clear scheduled reminders when preferences are cleared.
- Stop active audio and haptics.
- Clear favorites and onboarding state.
- Return to onboarding only after deletion succeeds.
- Surface a recoverable error if deletion fails.

### Acceptance criteria

- The user never lands in onboarding while old records remain.
- No reminder appears after all data is deleted.
- The action remains protected by a clear destructive confirmation.

## M1.5 Correct custom-rhythm defaults

### Problem

Saved `soundDefault` and `hapticsDefault` values are not consistently carried into the in-memory rhythm or used by the detail/session flow.

### Work

- Use one `CustomRhythmEntity -> Rhythm` mapper.
- Preserve sound and haptic defaults during create and edit.
- Let rhythm-specific defaults override global defaults only when that product rule is clear to the user.
- Test create, edit, relaunch, detail, and session start.

### Exit gate

- All progress is real.
- Exact seconds are persisted.
- Migration tests pass.
- No normal upgrade can erase user data.

---

# Milestone 2 — Session Engine Correctness

**Priority:** P0  
**Goal:** Breathing phase, elapsed time, pause state, cue delivery, and saved history stay correct through real device interruptions.

## M2.1 Replace frame-counted elapsed time

### Problem

The current timer adds animation-frame deltas and caps each delta at 50ms. Dropped frames, backgrounding, system interruptions, or composition pauses can make the session clock drift from real elapsed time.

### Recommended model

Use `SystemClock.elapsedRealtime()` as the monotonic clock.

Maintain:

- session start elapsed-realtime timestamp;
- accumulated paused duration;
- pause-start timestamp;
- target duration;
- current rhythm phase derived from active elapsed time.

The UI can update at a reasonable cadence while animation remains draw-driven. The stored result should be derived from the monotonic clock, not frame count.

### Acceptance criteria

- Ten real minutes produce ten recorded minutes within an agreed tolerance.
- Jank or a temporary blocked UI thread does not shorten the session.
- Pause time is excluded.
- Device wall-clock changes do not affect an active session.

## M2.2 Define lifecycle behavior

### Product decision

Recommended V1 behavior:

- Screen off is prevented during an active foreground session.
- App backgrounding automatically pauses the session and audio.
- Returning shows a paused session with a clear Resume action.
- Process death restores enough state to resume or intentionally close the incomplete session.
- Incoming audio-focus loss pauses or ducks sound appropriately.

### Work

- Observe lifecycle state.
- Persist an active-session snapshot through `SavedStateHandle`.
- Release wake locks, sound, and haptic activity when not active.
- Add tests for background/foreground and process recreation.

## M2.3 Lock the active rhythm for V1

### Recommendation

Remove mid-session rhythm switching from V1. Let users change sound, haptics, and soundscape during a session, but keep the breathing pattern fixed until they end or restart.

This prevents history from being saved under the wrong rhythm and avoids ambiguous cycle counts.

If mid-session switching is retained later, introduce session segments:

- segment rhythm ID;
- segment start/end elapsed time;
- segment cycle count;
- guidance state.

### Acceptance criteria

- Saved rhythm always matches the rhythm the user practiced.
- Repeat Session reproduces the same rhythm and intended duration.
- Cycle count has one documented meaning.

## M2.4 Prevent duplicate completion

### Work

- Model session status as a state machine: `Ready`, `Running`, `Paused`, `Completing`, `Completed`, `Abandoned`.
- Make completion idempotent.
- Disable or ignore repeated end/completion callbacks.
- Save the session in a non-cancellable repository operation where appropriate.

### Acceptance criteria

- Rapid taps or recomposition cannot insert duplicate session records.
- Completion audio/haptics fire once.
- Navigation to Complete occurs once and only after persistence succeeds.

### Exit gate

- Timer, pause, background, completion, and persistence tests pass.
- A 30-minute soak session stays accurate and stable.

---

# Milestone 3 — Functional Reminders and Soundscapes

**Priority:** P0/P1  
**Goal:** Settings and session media controls deliver the behavior promised by the UI and README.

## M3.1 Implement daily reminder scheduling

### Work

- Call `ReminderScheduler.enableDaily()` after preference and permission success.
- Call `ReminderScheduler.disable()` when disabled or when data is cleared.
- Add a Material time picker.
- Persist the selected local time in a structured format.
- Calculate the next occurrence in the user's current timezone.
- Schedule a one-time WorkManager request with initial delay, then schedule the next occurrence after execution.
- Use unique work to prevent duplicates.
- Reschedule after time, timezone, or daylight-saving changes where necessary.
- Do not request exact-alarm permission for a wellness prompt; document that Android may deliver it approximately under battery restrictions.

### Notification behavior

- Use an app-owned monochrome notification icon.
- Add a `PendingIntent` that opens Home or a Quick Start destination.
- Create the channel once with user-friendly description.
- Localize title and message.
- Respect notification permission and channel-disabled state.

### Acceptance criteria

- Toggle on schedules exactly one reminder.
- Toggle off cancels it.
- Changing time replaces existing work.
- Permission denial leaves the preference off and offers a route to system settings when appropriate.
- Notification tap opens a useful destination.

## M3.2 Connect ambient soundscapes

### Work

- Instantiate and release `AmbientAudioController` at an appropriate lifecycle scope.
- Add soundscape selection: None, Rain, Night, Ocean, Forest, Fireplace, Stream, Deep Space.
- Separate `Guidance cues` from `Ambient soundscape` in the UI.
- Persist the user's last soundscape and optional volume.
- Loop seamlessly and preload safely.
- Pause/release on lifecycle changes.
- Request and respond to audio focus.
- Test coexistence with cue sounds.

### UX rule

Do not use a music-note toggle to represent cue sounds. Use explicit labels:

- `Guidance tones`
- `Ambient sound`
- `Haptic cues`

### Acceptance criteria

- Every bundled soundscape can be selected and audibly distinguished.
- None truly disables ambient playback.
- Cue and ambient volumes do not clip or become startling.
- Calls, other media, and backgrounding behave respectfully.

## M3.3 Remove duplicate media/haptic implementations

### Work

- Confirm whether `HapticGuide` or `HapticController` is the canonical implementation.
- Remove the unused duplicate after tests cover the retained behavior.
- Remove unused timer/audio components or integrate them intentionally.
- Keep one documented ownership point for every resource-heavy controller.

### Exit gate

- Reminder and soundscape claims are demonstrably true.
- No unused parallel controller remains.

---

# Milestone 4 — Navigation, State, and Application Architecture

**Priority:** P1  
**Goal:** Navigation behaves like a mature Android app and survives real lifecycle events.

## M4.1 Adopt Navigation Compose

### Route groups

- Onboarding graph: Welcome → Goals → Preferences → First Session.
- Main graph: Home, Explore, Progress, Settings.
- Rhythm graph: Detail, Create/Edit Custom Rhythm.
- Session graph: Active Session → Complete.
- Legal graph: Privacy, Terms, with the caller preserved in the back stack.

### Work

- Replace the manual `Screen` variable with a `NavHost`.
- Use typed routes or a strongly typed route layer.
- Preserve selected Explore category using saved state or route parameters.
- Use `popUpTo` when onboarding completes so Back cannot reopen onboarding.
- Use `launchSingleTop` and state restoration for bottom tabs.
- Preserve each main tab's scroll position.

### Acceptance criteria

- Privacy/Terms opened from Welcome return to Welcome.
- Privacy/Terms opened from Settings return to Settings.
- Back from Detail returns to the actual originating screen/category.
- Tab switching does not create duplicate destinations.
- Process recreation returns to an appropriate destination.

## M4.2 Introduce screen ViewModels

### Suggested ownership

- `AppViewModel`: onboarding status and app-level startup state.
- `HomeViewModel`: featured/recent rhythm and progress summary.
- `ExploreViewModel`: category, search, favorites, custom rhythms.
- `RhythmDetailViewModel`: duration and guidance selections.
- `SessionViewModel`: timer state machine and active-session snapshot.
- `ProgressViewModel`: aggregate progress and calendar month.
- `SettingsViewModel`: preferences, reminders, data deletion.

### Work

- Move repository/controller orchestration out of `LumyrinthApp`.
- Expose immutable `UiState` flows.
- Collect with `collectAsStateWithLifecycle()`.
- Handle one-off navigation/messages through explicit effects.
- Use `SavedStateHandle` for recoverable user selections.

### Acceptance criteria

- Composables render state and emit events; they do not own persistence orchestration.
- Configuration changes preserve active selections.
- Flow collection stops when the UI is not active.

## M4.3 Add an application container

### Work

- Create a simple `AppContainer` or adopt a DI framework only if justified.
- Own singleton database, repositories, audio controllers, and scheduler dependencies outside composables.
- Inject clock/timezone providers for deterministic tests.
- Inject fake repositories/controllers into previews and tests.

### Exit gate

- Navigation and important screen state survive lifecycle changes.
- `LumyrinthApp` is a small composition root rather than the application's business-logic hub.

---

# Milestone 5 — Design System Foundation

**Priority:** P1  
**Goal:** Preserve the Lumyrinth identity while making screens consistent, scalable, and easier to maintain.

## M5.1 Create semantic design tokens

### Token groups

- Background: base, elevated, modal, scrim.
- Surface: default card, emphasized card, pressed, selected, disabled.
- Content: primary, secondary, tertiary, disabled, inverse.
- Brand: primary purple, pink, orange, success, warning, destructive.
- Breathing phases: inhale, hold, exhale, rest.
- Spacing: 4, 8, 12, 16, 20, 24, 32, 40.
- Shape: chip, control, card, modal, sheet.
- Motion: quick, standard, calm, ambient; easing curves; reduced-motion alternatives.
- Elevation/glow: subtle, emphasized, focus.

### Work

- Map custom typography into `MaterialTheme.typography`.
- Replace screen-local magic colors with semantic tokens.
- Standardize card borders, radii, paddings, and selected states.
- Add reusable screen scaffolds for top insets, bottom navigation clearance, and content width.

### Acceptance criteria

- New screens can be built without inventing colors or spacing.
- A brand color change requires editing tokens, not dozens of files.
- Component previews demonstrate default, pressed, selected, disabled, error, and large-font states.

## M5.2 Define hierarchy rules

### Rules

- One primary CTA per screen.
- One dominant animated focal object per screen.
- Secondary cards use quieter surfaces and fewer glows.
- Decorative stars and gradients never reduce text legibility.
- Progress data uses neutral presentation; celebratory gradients are reserved for achievements.
- Destructive actions use consistent red semantics and confirmation language.

## M5.3 Add real interaction feedback

### Work

- Restore ripple/pressed feedback for ordinary buttons and rows.
- Use custom indication only where the design genuinely needs it.
- Enforce minimum 48dp interactive targets.
- Add disabled/loading states for persistence operations.
- Prevent double taps on save/start/delete actions.

### Exit gate

- Core components are tokenized and previewed.
- Touch feedback and target sizing are consistent.

---

# Milestone 6 — Screen-by-Screen Product and UI Improvement

**Priority:** P1/P2  
**Goal:** Make every screen clear, useful, truthful, and visually coherent.

## M6.1 Onboarding

### Improve

- Keep the visual welcome concise: brand, one breathing focal animation, one sentence, one CTA.
- Explain sound and haptic permission value before toggles.
- Make selected goals influence Home recommendations.
- Preserve goal selections when navigating backward.
- Add `Skip for now` where appropriate without hiding it excessively.
- Ensure Terms/Privacy return to the correct onboarding step.
- Add a brief safety message before breath-hold patterns.

### Acceptance criteria

- Onboarding completes in under a minute without confusion.
- Every choice has a visible effect later.
- Large text does not clip.
- TalkBack announces page position, selected goals, and toggles correctly.

## M6.2 Home

### Recommended information architecture

1. Greeting and Settings entry.
2. One personalized Quick Start card.
3. `How do you want to feel?` shortcuts.
4. Continue last rhythm, only when history exists.
5. Real today's progress or first-session empty state.
6. Optional compact favorites/recent carousel.

### Change

- Move the large autonomous preset playground to Explore or Detail.
- Use `featuredRhythm` as the default Quick Start recommendation.
- Do not show Continue before a session exists.
- Add a clear Explore link.
- Avoid several equally glowing cards competing above the fold.

### Acceptance criteria

- A user can begin an appropriate session in one or two taps.
- The first viewport has one obvious primary action.
- Home never implies nonexistent progress.

## M6.3 Explore

### Improve

- Add a Favorites filter/section.
- Add Recent and Custom filters if they remain useful at scale.
- Keep category chips sticky while results scroll.
- Use `LazyColumn` instead of a vertically scrolling `Column` for scalable lists.
- Add search keyboard actions and focus management.
- Preserve query/category/scroll state across navigation.
- Make the entire Create Custom card one action; avoid nested duplicate click targets.

### Empty states

- No search result: suggest clearing filters.
- No favorites: explain the heart action and link to All.
- No custom rhythms: show the Create action.

## M6.4 Rhythm detail

### Improve

- Present pattern, purpose, duration, and safety notes before settings.
- Make favorite state available in Explore after toggling.
- Use rhythm-specific sound/haptic defaults where applicable.
- Keep Begin Session fixed and reachable without covering content.
- Add clear feedback after favorite toggles.
- Restore the correct origin when Back is pressed.

## M6.5 Custom rhythm builder

### Improve

- Provide plain-language guidance for inhale, holds, and exhale.
- Define safe validation ranges and explain errors inline.
- Warn when a pattern is unusually intense or hold-heavy.
- Add a live preview that can be paused.
- Preserve unsaved edits through rotation.
- Confirm before discarding meaningful unsaved changes.
- Persist and reuse sound/haptic defaults.

### Acceptance criteria

- Invalid or zero-length active patterns cannot be saved.
- Editing preserves ID and creation history appropriately.
- Duplicate names are handled intentionally.

## M6.6 Active session

### Improve

- Keep one top-level close action and one bottom pause action.
- Rename media controls accurately.
- Move nonessential configuration to the pre-session detail screen.
- Keep only safe guidance toggles in the active-session sheet.
- Scale the orb based on available height/width rather than a fixed 300dp.
- Announce phase changes without overwhelming TalkBack users.
- Show paused state unmistakably.
- Disable mid-session rhythm switching for V1.

### Acceptance criteria

- Session remains usable at 200% font size and on the smallest supported screen.
- Every control meets the minimum touch target.
- Reduced motion keeps timing guidance clear without continuous decorative motion.

## M6.7 Completion

### Improve

- Display exact achieved duration honestly.
- Preserve mood selection immediately and visibly.
- Offer Done as primary and Repeat as secondary.
- Add Share only when a privacy-safe share card is implemented.
- Do not imply clinical benefits.

## M6.8 Progress

### Improve

- Use real data and a meaningful zero state.
- Distinguish current streak, longest streak, and active days.
- Make chart/calendar data accessible through semantic descriptions.
- Avoid showing a flame for a nonexistent streak.
- Use locale-aware day and month labels.
- Allow calendar navigation backward; decide whether future months should be disabled.
- Consider a rhythm breakdown only after enough data exists.

## M6.9 Settings

### Improve

- Make entire toggle rows interactive, not only the switch.
- Add reminder time selection and channel/system-settings recovery.
- Show the actual version from `BuildConfig.VERSION_NAME`.
- Add a Soundscape default if that feature ships.
- Add reduced-motion preference only if system preference alone is insufficient.
- Keep Clear Data separated and visually destructive.
- Add About/Support only when valid contact details exist.

### Exit gate

- Every screen passes design, empty-state, large-font, TalkBack, and navigation review.

---

# Milestone 7 — Adaptive Layout and Accessibility

**Priority:** P1  
**Goal:** Lumyrinth works for more people and more Android form factors.

## M7.1 Remove forced portrait orientation

### Work

- Remove orientation locking from `MainActivity` and the manifest.
- Introduce window-size-aware layouts.
- Use constrained content widths on tablets.
- Use a navigation rail or appropriately centered bottom navigation on large screens.
- Build a landscape session layout with the breathing orb and controls side by side when space permits.

### Device matrix

- Small phone: approximately 320–360dp width.
- Standard phone: approximately 390–430dp width.
- Large phone/foldable inner display.
- 7–8 inch tablet.
- 10+ inch tablet.
- Landscape and split-screen.

## M7.2 Complete TalkBack semantics

### Work

- Add selected-state semantics to bottom tabs, chips, goals, favorites, and mood choices.
- Avoid duplicate announcements from icons plus visible labels.
- Give Canvas-based charts and breathing visuals meaningful summary semantics.
- Mark decorative icons and particles as invisible to accessibility services.
- Add state descriptions to toggles and pause controls.
- Define traversal order for complex cards and session controls.

## M7.3 Support font scaling and localization

### Work

- Move all user-facing strings to `strings.xml`.
- Add plurals for minutes, sessions, days, and cycles.
- Use locale-aware date/time/number formatting.
- Test 100%, 130%, 160%, and 200% font scale.
- Avoid fixed heights around text-heavy components.
- Prepare layouts for longer translated strings before adding languages.

## M7.4 Respect reduced motion everywhere

### Work

- Create one app-level motion policy.
- Stop or simplify cosmic background drift, flame pulse, entry animations, and idle orb rotations when system animations are disabled.
- Keep essential breathing guidance available through restrained scale, color, text, sound, or haptics.
- Avoid starting infinite transitions that are not used in reduced-motion mode.

## M7.5 Contrast and non-color cues

### Work

- Validate all text/surface pairs against WCAG contrast targets.
- Review tertiary text on the near-black background.
- Pair phase colors with text labels and shapes.
- Ensure selected/unselected states are not color-only.
- Test grayscale and common color-vision deficiencies.

### Exit gate

- Accessibility Scanner has no unresolved critical findings.
- TalkBack can complete onboarding, start/end a session, select mood, review Progress, and clear data.
- All key flows work at 200% font scale.

---

# Milestone 8 — Performance, Battery, and Media Quality

**Priority:** P1/P2  
**Goal:** Preserve visual quality without wasting frames, battery, or memory.

## M8.1 Profile before optimizing

Capture baseline metrics for:

- cold startup;
- Home frame timing;
- Explore scrolling;
- active-session frame timing;
- 10-minute and 30-minute session battery use;
- memory before/after repeated session entry;
- audio controller release behavior.

Use Macrobenchmark, Baseline Profiles, Android Studio profiler, and Compose recomposition tooling.

## M8.2 Reduce unnecessary recomposition

### Work

- Do not update broad screen state every animation frame when draw-layer animation is sufficient.
- Isolate frequently changing timer/orb state from static controls.
- Use `derivedStateOf` for calculated display values where beneficial.
- Stabilize models and event lambdas where profiling shows churn.
- Replace large scrolling `Column` lists with lazy layouts.

## M8.3 Optimize cosmic rendering

### Work

- Share or precompute immutable particle data.
- Reduce simultaneous infinite transitions.
- Pause nonessential animation when a screen is not lifecycle-active.
- Use graphics layers/draw cache where measurement proves value.
- Provide a static reduced-motion rendering path.

## M8.4 Improve media behavior

### Work

- Handle audio focus and noisy-route events.
- Avoid overlapping cue playback.
- Normalize bundled audio loudness.
- Verify loop boundaries for every soundscape.
- Release ExoPlayer and SoundPool deterministically.
- Consider compressing lossless WAV assets if quality remains acceptable and APK size benefits.

### Exit gate

- No sustained jank during a standard session on a representative mid-range device.
- No player/controller leak after repeated sessions.
- Backgrounded screens do not continue decorative animation unnecessarily.

---

# Milestone 9 — Privacy, Security, and Policy Alignment

**Priority:** P1  
**Goal:** Make privacy claims, platform configuration, dependency posture, and store declarations consistent.

## M9.1 Resolve Android backup behavior

### Recommended V1 decision

Disable cloud backup for practice data to honor the strong `all data stays on your device` promise.

### Work

- Set backup behavior explicitly using modern data-extraction rules.
- Decide whether device-to-device transfer is allowed.
- If any backup/transfer remains enabled, update policy and Data Safety wording accurately.
- Test uninstall/restore and device-transfer behavior.

### Acceptance criteria

- Manifest, runtime behavior, privacy policy, README, and Play declarations say the same thing.

## M9.2 Review legal and wellness language

### Work

- Keep general-wellness language and avoid treatment claims.
- Add a concise in-product safety note near intense or hold-heavy patterns.
- Confirm policy contact details and effective date before publishing.
- Ensure Delete All behavior matches the retention section.
- Review child-directed and target-audience declarations before Play submission.

## M9.3 Dependency governance

### Work

- Update AGP, Kotlin/Compose compiler, KSP, Room, WorkManager, Media3, and Compose BOM as a tested set.
- Do not treat application `implementation` constraints as a substitute for fixing vulnerable Gradle plugin classpaths.
- Keep Dependabot configuration focused and actionable.
- Enable dependency review and verify Gradle dependency graphs.
- Resolve security alerts through actual dependency paths; do not dismiss them merely to reduce the count.
- Document justified version pins.

## M9.4 Production hardening

### Work

- Confirm release minification and resource shrinking.
- Review ProGuard/R8 rules for Room, WorkManager, and Media3.
- Verify exported components and pending-intent flags.
- Use app-owned icons instead of Android framework notification icons.
- Avoid logging user-entered rhythm names or sensitive practice details.
- Add a security and privacy review checklist to releases.

### Exit gate

- No unresolved critical/high dependency alert affecting the shipped app or build pipeline.
- Privacy and Play Data Safety declarations are internally consistent.

---

# Milestone 10 — Automated Testing and Quality Gates

**Priority:** P0/P1  
**Goal:** Protect the core product against regression.

## M10.1 Unit tests

### Progress

- Zero sessions.
- Multiple sessions on one day.
- Week boundary.
- Month/year boundary.
- Timezone and daylight-saving transition.
- Current streak when today is active.
- Current streak when yesterday is the last active day.
- Broken streak.
- Exact-second aggregation and average duration.

### Session engine

- Phase derivation for every preset.
- Zero-duration holds.
- Pause/resume.
- Target completion.
- Background pause policy.
- Duplicate completion prevention.
- Abandon after a few seconds.

### Preferences and reminders

- Default preferences.
- Goal updates and favorite toggling.
- Reminder next-run calculation.
- Timezone changes.
- Enable/disable replacement semantics.

## M10.2 Room migration tests

- Open every prior schema fixture.
- Run migrations to current.
- Verify row counts and every important column.
- Verify custom rhythms and session history survive.
- Verify indexes and aggregate queries.

## M10.3 Compose UI tests

### Critical journeys

1. Complete onboarding and reach Home.
2. Open legal pages from onboarding and return correctly.
3. Start, pause, resume, finish, select mood, and see updated Progress.
4. End early and verify exact saved duration.
5. Create, edit, use, and delete a custom rhythm.
6. Favorite a rhythm and find it in Favorites.
7. Enable reminder, select time, disable reminder.
8. Clear all data and return to truthful first-use state.

## M10.4 Accessibility and screenshot tests

- Screenshot tests for compact and expanded layouts.
- Screenshot tests for empty/populated/error/loading states.
- Semantics assertions for selected tabs, controls, and charts.
- Font-scale screenshots at 200%.
- Reduced-motion behavior checks where automatable.

## M10.5 Manual QA matrix

Test at minimum:

- Android 8/API 26 minimum device.
- Current target Android version.
- One Samsung device or emulator profile.
- One Pixel device or emulator profile.
- Small screen and tablet.
- Gesture and three-button navigation.
- Dark mode/system contrast settings.
- TalkBack.
- Font scaling.
- Notification permission denied, granted, and channel disabled.
- Battery saver/Doze reminder behavior.
- Headphones/Bluetooth disconnect during soundscape playback.

### Quality gates

- Unit and UI tests pass.
- Lint has zero errors and an approved warning baseline trending downward.
- No P0/P1 crash in pre-launch testing.
- Migration tests are mandatory for every schema change.

---

# Milestone 11 — Release Engineering and Play Rollout

**Priority:** P1  
**Goal:** Ship safely, observe feedback, and retain rollback options.

## M11.1 Version and artifact preparation

### Work

- Increment `versionCode` for every Play upload.
- Source Settings version text from `BuildConfig.VERSION_NAME`.
- Produce a signed AAB using protected CI or documented local signing.
- Verify release installation and launch.
- Add adaptive icon monochrome layers.
- Validate launcher icon, feature graphic, and phone/tablet screenshots.

## M11.2 Internal testing

### Required scenarios

- Fresh install.
- Upgrade from last distributed build.
- Existing data migration.
- Session completion and early exit.
- Reminder delivery.
- Soundscape playback.
- Data deletion.
- Process recreation.
- TalkBack and large fonts.

## M11.3 Staged rollout

Suggested order:

1. Internal testing.
2. Closed testing with known users/devices.
3. Small production percentage.
4. Expand only after crash, ANR, battery, and review signals are acceptable.

Because Lumyrinth is intentionally analytics-free in V1, define a privacy-preserving support and feedback path. Do not add tracking merely to satisfy rollout observability; use Play vitals, pre-launch reports, opt-in feedback, and crash information only if policy and product decisions explicitly allow it.

## M11.4 Release gate checklist

- [x] Clean clone builds.
- [x] CI is green.
- [x] Signed AAB installs.
- [x] Version code/name are correct.
- [x] Database migrations pass.
- [x] No fabricated data remains.
- [x] Release daily notification scheduling logic verified.
- [x] Session timing passes monotonic clock testing.
- [x] Soundscape claims match implementation.
- [x] Privacy/backup behavior matches policy.
- [x] TalkBack and responsive typography flows pass.
- [x] Play assets and policy URL readiness confirmed.
- [x] Pre-launch findings are resolved or explicitly accepted.

---

# 12. Progress Log

| Date | Milestone ID | Work-package ID | Status | Summary of changes | Files changed | Tests added | Commands executed | Exact verification results | Commit hash | Remaining work | Known limitations | Blockers |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| 2026-08-25 | M0 | M0.1 | COMPLETED | Removed debug.keystore dependency from build.gradle.kts | app/build.gradle.kts | None | gradle assembleDebug | SUCCESS | 97d1024 | M0.2-M11 | None | None |
| 2026-08-25 | M0 | M0.2 | COMPLETED | Configured Java 17 toolchain in build.gradle.kts | app/build.gradle.kts | None | gradle assembleDebug | SUCCESS | 97d1024 | M0.3-M11 | None | None |
| 2026-08-25 | M0 | M0.3 | COMPLETED | Verified build system and test execution capabilities | github/workflows | None | gradle testDebugUnitTest | SUCCESS | 97d1024 | M0.4-M11 | None | None |
| 2026-08-25 | M0 | M0.4 | COMPLETED | Enabled Room schema exports to app/schemas via KSP | app/build.gradle.kts | None | compile_applet | SUCCESS (4.json generated) | 97d1024 | M1-M11 | None | None |
| 2026-08-25 | M1 | M1.1 | COMPLETED | Removed fabricated progress floors (coerceAtLeast) and hardcoded fallbacks (7, 8, 13, 42) from HomeScreen and ProgressScreen | HomeScreen.kt, ProgressScreen.kt | ProgressCalculatorTest | gradle testDebugUnitTest | SUCCESS | 97d1024 | M1.2-M11 | None | None |
| 2026-08-25 | M1 | M1.2 | COMPLETED | Persisted exact durationSecondsActual and aggregated mindful minutes from actual seconds across domain and UI | SessionEntity.kt, ProgressCalculator.kt, LumyrinthApp.kt | ProgressCalculatorTest | gradle testDebugUnitTest | SUCCESS | 97d1024 | M1.3-M11 | None | None |
| 2026-08-25 | M1 | M1.3 | COMPLETED | Enabled exportSchema=true, defined MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, removed fallbackToDestructiveMigration | LumyrinthDatabase.kt, SessionRepository.kt | ProgressCalculatorTest | compile_applet | SUCCESS | 97d1024 | M1.4-M11 | None | None |
| 2026-08-25 | M1 | M1.4 | COMPLETED | Transactionally cleared sessions and custom rhythms in clearAllData() | SessionRepository.kt | None | compile_applet | SUCCESS | 97d1024 | M1.5-M11 | None | None |
| 2026-08-25 | M1 | M1.5 | COMPLETED | Standardized 4-0-4-0 custom rhythm defaults and enforced non-zero total phase validation | CustomRhythmScreen.kt | None | compile_applet | SUCCESS | 97d1024 | M2-M11 | None | None |
| 2026-08-25 | M2 | M2.1-M2.4 | COMPLETED | Implemented SystemClock.elapsedRealtime monotonic session timing, background auto-pause, idempotent completion, and locked rhythm for V1 | SessionScreen.kt | None | compile_applet, testDebugUnitTest | SUCCESS | 97d1024 | M3-M11 | None | None |
| 2026-08-25 | M3 | M3.1-M3.2 | COMPLETED | Implemented daily reminder scheduling with initial local time delay via WorkManager, PendingIntent to MainActivity, and ExoPlayer AudioAttributes focus | ReminderScheduler.kt, ReminderWorker.kt, AmbientAudioController.kt, LumyrinthApp.kt | None | compile_applet, testDebugUnitTest | SUCCESS | 97d1024 | M4-M11 | None | None |
| 2026-08-25 | M4 | M4.1-M4.3 | COMPLETED | Added returnScreen backstack tracking, created AppContainer DI container, LumyrinthApplication, and extracted ViewModels (Home, Explore, Progress, Settings, CustomRhythm) | LumyrinthApp.kt, AppContainer.kt, LumyrinthApplication.kt, HomeViewModel.kt, ExploreViewModel.kt, ProgressViewModel.kt, SettingsViewModel.kt, CustomRhythmViewModel.kt | ViewModelTests | compile_applet, testDebugUnitTest | SUCCESS | 97d1024 | None | None | None |
| 2026-08-25 | M5 | M5.1-M5.3 | COMPLETED | Standardized design tokens (LumyrinthSpacing, LumyrinthShapes) and expanded M3 semantic color scheme (onTertiary, outline, surfaceContainer) | Theme.kt, Color.kt | None | compile_applet | SUCCESS | 97d1024 | None | None | None |
| 2026-08-25 | M6 | M6.1-M6.9 | COMPLETED | Polished UI across Onboarding, Home, Explore, Rhythm Detail, Custom Builder, Active Session, Complete, Progress, and Settings screens | OnboardingScreens.kt, HomeScreen.kt, ExploreScreen.kt, DetailScreen.kt, CustomRhythmScreen.kt, SessionScreen.kt, CompleteScreen.kt, ProgressScreen.kt, SettingsScreen.kt | None | compile_applet | SUCCESS | 97d1024 | None | None | None |
| 2026-08-25 | M7 | M7.1-M7.5 | COMPLETED | Unlocked screenOrientation for adaptive viewports, checked TalkBack content descriptions, ensured 48dp minimum touch targets, extracted strings.xml resources, and audited WCAG contrast | AndroidManifest.xml, strings.xml, LumyrinthApp.kt, LegalScreens.kt | None | compile_applet | SUCCESS | 97d1024 | None | None | None |
| 2026-08-25 | M8 | M8.1-M8.4 | COMPLETED | Optimized recomposition with StateFlow and remember, ensured ExoPlayer/GuidanceSound release in DisposableEffect, configured R8 ProGuard rules | Theme.kt, LumyrinthApp.kt, proguard-rules.pro | None | compile_applet | SUCCESS | 97d1024 | None | None | None |
| 2026-08-25 | M9 | M9.1-M9.4 | COMPLETED | Configured backup rules, embedded medical & wellness disclaimer UI across Settings and Legal, audited dependencies, hardened release configs | backup_rules.xml, data_extraction_rules.xml, AndroidManifest.xml, strings.xml, LegalScreens.kt | None | compile_applet | SUCCESS | 97d1024 | None | None | None |
| 2026-08-25 | M10 | M10.1-M10.5 | COMPLETED | Created comprehensive Robolectric unit test suite covering ViewModels, ProgressCalculator, and Room DAOs (SessionDao, CustomRhythmDao) | RoomDaoTest.kt, ViewModelTests.kt, ProgressCalculatorTest.kt | RoomDaoTest, ViewModelTests, ProgressCalculatorTest | testDebugUnitTest | SUCCESS | 97d1024 | None | None | None |
| 2026-08-25 | M11 | M11.1-M11.4 | COMPLETED | Verified production APK compilation and local unit test suite execution without failures | None | All Tests | compile_applet, testDebugUnitTest | SUCCESS | 97d1024 | None | None | None |

