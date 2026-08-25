package com.lumyrinth.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lumyrinth.app.R
import com.lumyrinth.app.audio.GuidanceSoundController
import com.lumyrinth.app.data.UserPreferences
import com.lumyrinth.app.data.UserPreferencesRepository
import com.lumyrinth.app.data.session.CustomRhythmEntity
import com.lumyrinth.app.data.session.SessionEntity
import com.lumyrinth.app.data.session.SessionRepository
import com.lumyrinth.app.domain.PresetRhythms
import com.lumyrinth.app.domain.ProgressCalculator
import com.lumyrinth.app.domain.Rhythm
import com.lumyrinth.app.domain.RhythmCategory
import com.lumyrinth.app.haptics.HapticController
import com.lumyrinth.app.ui.components.AppTab
import com.lumyrinth.app.ui.components.BottomTabBar
import com.lumyrinth.app.ui.screens.CompleteScreen
import com.lumyrinth.app.ui.screens.CustomRhythmScreen
import com.lumyrinth.app.ui.screens.DetailScreen
import com.lumyrinth.app.ui.screens.ExploreScreen
import com.lumyrinth.app.ui.screens.FirstSessionScreen
import com.lumyrinth.app.ui.screens.GoalsScreen
import com.lumyrinth.app.ui.screens.HomeScreen
import com.lumyrinth.app.ui.screens.PreferencesScreen
import com.lumyrinth.app.ui.screens.PrivacyPolicyScreen
import com.lumyrinth.app.ui.screens.ProgressScreen
import com.lumyrinth.app.ui.screens.SessionScreen
import com.lumyrinth.app.ui.screens.SettingsScreen
import com.lumyrinth.app.ui.screens.TermsScreen
import com.lumyrinth.app.ui.screens.WelcomeScreen
import com.lumyrinth.app.ui.theme.LumyrinthColors
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

sealed class Screen {
    // Onboarding
    data object Welcome : Screen()
    data object Goals : Screen()
    data object Preferences : Screen()
    data object FirstSession : Screen()

    // Main App
    data object Home : Screen()
    data object Explore : Screen()
    data object Progress : Screen()
    data object Settings : Screen()

    // Legal
    data object PrivacyPolicy : Screen()
    data object Terms : Screen()

    // Secondary & Modals
    data class Detail(val rhythm: Rhythm) : Screen()
    data class CustomRhythm(val editRhythm: Rhythm? = null) : Screen()
    data class ActiveSession(
        val rhythm: Rhythm,
        val durationMinutes: Int,
        val soundOn: Boolean,
        val hapticsOn: Boolean,
    ) : Screen()
    data class Complete(
        val sessionId: Long,
        val rhythm: Rhythm,
        val durationMinutes: Int,
        val cyclesCompleted: Int,
        val initialMood: String?,
    ) : Screen()
}

@Composable
fun LumyrinthApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val prefsRepo = remember { UserPreferencesRepository(context) }
    val userPrefsState = prefsRepo.preferences.collectAsState(initial = null)
    val userPrefs = userPrefsState.value

    val sessionRepo = remember { SessionRepository.from(context) }
    val sessions by sessionRepo.sessions.collectAsState(initial = emptyList())
    val customRhythmEntities by sessionRepo.customRhythms.collectAsState(initial = emptyList())

    val soundController = remember { GuidanceSoundController(context) }
    val hapticController = remember { HapticController(context) }

    DisposableEffect(Unit) {
        onDispose {
            soundController.release()
        }
    }

    // Convert custom DB entities to domain Rhythm models
    val customRhythms = remember(customRhythmEntities) {
        customRhythmEntities.map { entity ->
            Rhythm(
                id = entity.id,
                name = entity.name,
                category = RhythmCategory.CUSTOM,
                shortDescription = "Your custom breathing pattern",
                inhaleSeconds = entity.inhaleSeconds,
                hold1Seconds = entity.hold1Seconds,
                exhaleSeconds = entity.exhaleSeconds,
                hold2Seconds = entity.hold2Seconds,
                defaultDurationMinutes = entity.defaultDurationMinutes,
                recommendedDurationOptions = listOf(1, 3, 5, 10),
                isCustom = true,
            )
        }
    }

    val allRhythms = remember(customRhythms) {
        PresetRhythms.all + customRhythms
    }

    // Compute progress stats from real session entities
    val progressSummary = remember(sessions) {
        ProgressCalculator.compute(sessions)
    }

    // Initial screen state (null until userPrefs is loaded)
    var currentScreen by remember { mutableStateOf<Screen?>(null) }
    var selectedExploreCategory by remember { mutableStateOf("all") }

    // Initialize current screen once preferences are available
    LaunchedEffect(userPrefs) {
        if (userPrefs != null && currentScreen == null) {
            currentScreen = if (!userPrefs.onboardingComplete) Screen.Welcome else Screen.Home
        }
    }

    // While loading preferences, show calm dark splash to prevent screen flicker
    if (userPrefs == null || currentScreen == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LumyrinthColors.BgBase),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.lumyrinth_mark),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(64.dp),
            )
        }
        return
    }

    val activeScreen = currentScreen ?: Screen.Home

    // Hardware Back Button navigation handling
    when (activeScreen) {
        is Screen.Goals -> BackHandler { currentScreen = Screen.Welcome }
        is Screen.Preferences -> BackHandler { currentScreen = Screen.Goals }
        is Screen.FirstSession -> BackHandler { currentScreen = Screen.Preferences }
        is Screen.Explore -> BackHandler { currentScreen = Screen.Home }
        is Screen.Progress -> BackHandler { currentScreen = Screen.Home }
        is Screen.Settings -> BackHandler { currentScreen = Screen.Home }
        is Screen.PrivacyPolicy -> BackHandler { currentScreen = Screen.Settings }
        is Screen.Terms -> BackHandler { currentScreen = Screen.Settings }
        is Screen.Detail -> BackHandler { currentScreen = Screen.Explore }
        is Screen.CustomRhythm -> BackHandler { currentScreen = Screen.Explore }
        is Screen.Complete -> BackHandler { currentScreen = Screen.Home }
        else -> { /* Welcome, Home, ActiveSession handle back natively */ }
    }

    // Map screen to bottom navigation tab if applicable
    val activeTab = when (activeScreen) {
        is Screen.Home -> AppTab.HOME
        is Screen.Explore -> AppTab.EXPLORE
        is Screen.Progress -> AppTab.PROGRESS
        is Screen.Settings -> AppTab.SETTINGS
        else -> null
    }

    val lastUsedRhythm = remember(progressSummary.latestSession, allRhythms) {
        val latest = progressSummary.latestSession
        if (latest != null) {
            allRhythms.find { it.id == latest.rhythmId } ?: PresetRhythms.slowDown
        } else {
            null
        }
    }

    val featuredRhythm = remember(userPrefs.selectedGoals) {
        when {
            userPrefs.selectedGoals.contains("focus") -> PresetRhythms.steady
            userPrefs.selectedGoals.contains("sleep") -> PresetRhythms.deepRest
            userPrefs.selectedGoals.contains("break") -> PresetRhythms.quickReset
            else -> PresetRhythms.slowDown
        }
    }

    fun getScreenTabIndex(screen: Screen): Int {
        return when (screen) {
            is Screen.Home -> 0
            is Screen.Explore -> 1
            is Screen.Progress -> 2
            is Screen.Settings -> 3
            else -> -1
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LumyrinthColors.BgBase),
    ) {
        AnimatedContent(
            targetState = activeScreen,
            transitionSpec = {
                val initialIndex = getScreenTabIndex(initialState)
                val targetIndex = getScreenTabIndex(targetState)

                if (initialIndex != -1 && targetIndex != -1) {
                    // Tab switch transition: slide left/right depending on index direction
                    if (targetIndex > initialIndex) {
                        (slideInHorizontally(animationSpec = tween(300)) { width -> width / 3 } +
                                fadeIn(animationSpec = tween(300))) togetherWith
                                (slideOutHorizontally(animationSpec = tween(300)) { width -> -width / 3 } +
                                        fadeOut(animationSpec = tween(250)))
                    } else {
                        (slideInHorizontally(animationSpec = tween(300)) { width -> -width / 3 } +
                                fadeIn(animationSpec = tween(300))) togetherWith
                                (slideOutHorizontally(animationSpec = tween(300)) { width -> width / 3 } +
                                        fadeOut(animationSpec = tween(250)))
                    }
                } else {
                    // Secondary screen / Modal push & pop transition
                    (slideInVertically(animationSpec = tween(320)) { height -> height / 4 } +
                            fadeIn(animationSpec = tween(300)) +
                            scaleIn(initialScale = 0.96f, animationSpec = tween(300))) togetherWith
                            (slideOutVertically(animationSpec = tween(280)) { height -> height / 6 } +
                                    fadeOut(animationSpec = tween(240)) +
                                    scaleOut(targetScale = 0.96f, animationSpec = tween(240)))
                }
            },
            label = "screen_transition",
        ) { target ->
            when (target) {
                // Onboarding Screens
                is Screen.Welcome -> {
                    WelcomeScreen(
                        onGetStarted = { currentScreen = Screen.Goals },
                        onOpenTerms = { currentScreen = Screen.Terms },
                        onOpenPrivacyPolicy = { currentScreen = Screen.PrivacyPolicy },
                    )
                }

                is Screen.Goals -> {
                    GoalsScreen(
                        initialGoals = userPrefs.selectedGoals,
                        onBack = { currentScreen = Screen.Welcome },
                        onNext = { selectedGoals ->
                            scope.launch { prefsRepo.setSelectedGoals(selectedGoals) }
                            currentScreen = Screen.Preferences
                        },
                    )
                }

                is Screen.Preferences -> {
                    PreferencesScreen(
                        initialHaptics = userPrefs.hapticGuidanceDefault,
                        initialSound = userPrefs.soundGuidanceDefault,
                        onBack = { currentScreen = Screen.Goals },
                        onNext = { haptics, sound ->
                            scope.launch {
                                prefsRepo.setHapticGuidanceDefault(haptics)
                                prefsRepo.setSoundGuidanceDefault(sound)
                            }
                            currentScreen = Screen.FirstSession
                        },
                    )
                }

                is Screen.FirstSession -> {
                    FirstSessionScreen(
                        onBeginSession = {
                            scope.launch { prefsRepo.setOnboardingComplete(true) }
                            currentScreen = Screen.ActiveSession(
                                rhythm = PresetRhythms.slowDown,
                                durationMinutes = 1,
                                soundOn = userPrefs.soundGuidanceDefault,
                                hapticsOn = userPrefs.hapticGuidanceDefault,
                            )
                        },
                        onExploreFirst = {
                            scope.launch { prefsRepo.setOnboardingComplete(true) }
                            currentScreen = Screen.Explore
                        },
                    )
                }

                // Main Navigation Screens
                is Screen.Home -> {
                    HomeScreen(
                        featuredRhythm = featuredRhythm,
                        progressSummary = progressSummary,
                        lastUsedRhythm = lastUsedRhythm,
                        onStartFeatured = { rhythm ->
                            currentScreen = Screen.Detail(rhythm)
                        },
                        onMoodFilterClick = { category ->
                            selectedExploreCategory = category.id
                            currentScreen = Screen.Explore
                        },
                        onRepeatLastSession = { rhythm ->
                            currentScreen = Screen.Detail(rhythm)
                        },
                        onExploreClick = { currentScreen = Screen.Explore },
                        onProfileClick = { currentScreen = Screen.Settings },
                    )
                }

                is Screen.Explore -> {
                    ExploreScreen(
                        allRhythms = PresetRhythms.all,
                        customRhythms = customRhythms,
                        selectedCategory = selectedExploreCategory,
                        onCategoryChange = { selectedExploreCategory = it },
                        onSelectRhythm = { rhythm ->
                            currentScreen = Screen.Detail(rhythm)
                        },
                        onCreateCustomClick = {
                            currentScreen = Screen.CustomRhythm(editRhythm = null)
                        },
                        onEditCustomRhythm = { rhythm ->
                            currentScreen = Screen.CustomRhythm(editRhythm = rhythm)
                        },
                        onDeleteCustomRhythm = { rhythmId ->
                            scope.launch {
                                sessionRepo.deleteCustomRhythm(rhythmId)
                            }
                        },
                    )
                }

                is Screen.Progress -> {
                    ProgressScreen(
                        progressSummary = progressSummary,
                    )
                }

                is Screen.Settings -> {
                    SettingsScreen(
                        userPreferences = userPrefs,
                        onToggleHaptics = { scope.launch { prefsRepo.setHapticGuidanceDefault(it) } },
                        onToggleSound = { scope.launch { prefsRepo.setSoundGuidanceDefault(it) } },
                        onToggleReminder = { scope.launch { prefsRepo.setDailyReminderEnabled(it) } },
                        onRetakeOnboarding = {
                            scope.launch { prefsRepo.resetOnboarding() }
                            currentScreen = Screen.Welcome
                        },
                        onOpenPrivacyPolicy = { currentScreen = Screen.PrivacyPolicy },
                        onOpenTerms = { currentScreen = Screen.Terms },
                        onClearAllData = {
                            scope.launch {
                                sessionRepo.clearAllData()
                                prefsRepo.clearAllPreferences()
                                currentScreen = Screen.Welcome
                            }
                        },
                    )
                }

                // Legal Screens
                is Screen.PrivacyPolicy -> {
                    PrivacyPolicyScreen(
                        onBack = { currentScreen = Screen.Settings },
                    )
                }

                is Screen.Terms -> {
                    TermsScreen(
                        onBack = { currentScreen = Screen.Settings },
                    )
                }

                // Secondary Flow Screens
                is Screen.Detail -> {
                    val isFav = userPrefs.favoriteRhythmIds.contains(target.rhythm.id)
                    DetailScreen(
                        rhythm = target.rhythm,
                        isFavorite = isFav,
                        defaultSound = userPrefs.soundGuidanceDefault,
                        defaultHaptics = userPrefs.hapticGuidanceDefault,
                        onBack = { currentScreen = Screen.Explore },
                        onToggleFavorite = {
                            scope.launch { prefsRepo.toggleFavorite(target.rhythm.id) }
                        },
                        onBeginSession = { rhythm, durationMins, soundOn, hapticsOn ->
                            currentScreen = Screen.ActiveSession(
                                rhythm = rhythm,
                                durationMinutes = durationMins,
                                soundOn = soundOn,
                                hapticsOn = hapticsOn,
                            )
                        },
                    )
                }

                is Screen.CustomRhythm -> {
                    CustomRhythmScreen(
                        initialRhythm = target.editRhythm,
                        onBack = { currentScreen = Screen.Explore },
                        onSaveRhythm = { id, name, inhale, hold1, exhale, hold2, durationMins, soundDefault, hapticsDefault ->
                            val finalId = id ?: "custom_${UUID.randomUUID().toString().take(8)}"
                            val newRhythm = Rhythm(
                                id = finalId,
                                name = name,
                                category = RhythmCategory.CUSTOM,
                                shortDescription = "Custom rhythm ($inhale-$hold1-$exhale-$hold2)",
                                inhaleSeconds = inhale,
                                hold1Seconds = hold1,
                                exhaleSeconds = exhale,
                                hold2Seconds = hold2,
                                defaultDurationMinutes = durationMins,
                                recommendedDurationOptions = listOf(1, 3, 5, 10),
                                isCustom = true,
                            )

                            scope.launch {
                                sessionRepo.saveCustomRhythm(
                                    CustomRhythmEntity(
                                        id = finalId,
                                        name = name,
                                        inhaleSeconds = inhale,
                                        hold1Seconds = hold1,
                                        exhaleSeconds = exhale,
                                        hold2Seconds = hold2,
                                        defaultDurationMinutes = durationMins,
                                        soundDefault = soundDefault,
                                        hapticsDefault = hapticsDefault,
                                    )
                                )
                            }

                            currentScreen = Screen.Detail(newRhythm)
                        },
                    )
                }

                is Screen.ActiveSession -> {
                    SessionScreen(
                        rhythm = target.rhythm,
                        durationMinutes = target.durationMinutes,
                        initialSoundOn = target.soundOn,
                        initialHapticsOn = target.hapticsOn,
                        onPhaseTransition = { phase, soundOn, hapticsOn ->
                            if (soundOn) soundController.cue(phase)
                            if (hapticsOn) hapticController.cue(phase)
                        },
                        onSessionFinished = { completedNaturally, actualDurationSecs, cyclesCompleted, soundOn, hapticsOn ->
                            if (soundOn) soundController.complete()
                            if (hapticsOn) hapticController.complete()

                            val actualMinutes = (actualDurationSecs + 59) / 60
                            val sessionEntity = SessionEntity(
                                rhythmId = target.rhythm.id,
                                rhythmNameSnapshot = target.rhythm.name,
                                dateIso = LocalDate.now().toString(),
                                startedAtEpochMillis = System.currentTimeMillis() - (actualDurationSecs * 1000L),
                                completedNaturally = completedNaturally,
                                durationMinutesPlanned = target.durationMinutes,
                                durationMinutesActual = actualMinutes.coerceAtLeast(1),
                                cyclesCompleted = cyclesCompleted,
                                soundOn = soundOn,
                                hapticsOn = hapticsOn,
                            )

                            scope.launch {
                                val savedId = sessionRepo.saveSession(sessionEntity)
                                currentScreen = Screen.Complete(
                                    sessionId = savedId,
                                    rhythm = target.rhythm,
                                    durationMinutes = actualMinutes.coerceAtLeast(1),
                                    cyclesCompleted = cyclesCompleted,
                                    initialMood = null,
                                )
                            }
                        },
                        onSessionAbandoned = {
                            currentScreen = Screen.Home
                        },
                    )
                }

                is Screen.Complete -> {
                    CompleteScreen(
                        durationMinutes = target.durationMinutes,
                        cyclesCompleted = target.cyclesCompleted,
                        initialMood = target.initialMood,
                        onMoodSelected = { mood ->
                            scope.launch {
                                sessionRepo.updateMood(target.sessionId, mood)
                            }
                        },
                        onDone = {
                            currentScreen = Screen.Home
                        },
                        onRepeatSession = {
                            currentScreen = Screen.ActiveSession(
                                rhythm = target.rhythm,
                                durationMinutes = target.durationMinutes,
                                soundOn = userPrefs.soundGuidanceDefault,
                                hapticsOn = userPrefs.hapticGuidanceDefault,
                            )
                        },
                    )
                }
            }
        }

        // Persistent Bottom Tab Bar on main screens
        if (activeTab != null) {
            BottomTabBar(
                activeTab = activeTab,
                onTabChange = { tab ->
                    currentScreen = when (tab) {
                        AppTab.HOME -> Screen.Home
                        AppTab.EXPLORE -> Screen.Explore
                        AppTab.PROGRESS -> Screen.Progress
                        AppTab.SETTINGS -> Screen.Settings
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}
