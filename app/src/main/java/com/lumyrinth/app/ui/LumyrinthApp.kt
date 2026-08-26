package com.lumyrinth.app.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lumyrinth.app.LumyrinthApplication
import com.lumyrinth.app.R
import com.lumyrinth.app.data.session.SessionEntity
import com.lumyrinth.app.domain.PresetRhythms
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
import com.lumyrinth.app.ui.theme.LumyrinthTheme
import com.lumyrinth.app.ui.theme.LumyrinthThemeTokens
import com.lumyrinth.app.ui.viewmodels.CustomRhythmViewModel
import com.lumyrinth.app.ui.viewmodels.ExploreViewModel
import com.lumyrinth.app.ui.viewmodels.HomeViewModel
import com.lumyrinth.app.ui.viewmodels.ProgressViewModel
import com.lumyrinth.app.ui.viewmodels.SettingsViewModel
import java.time.LocalDate
import kotlinx.coroutines.launch

private object Routes {
    const val WELCOME = "onboarding/welcome"
    const val GOALS = "onboarding/goals"
    const val PREFERENCES = "onboarding/preferences"
    const val FIRST_SESSION = "onboarding/first-session"
    const val HOME = "main/home"
    const val EXPLORE = "main/explore"
    const val PROGRESS = "main/progress"
    const val SETTINGS = "main/settings"
    const val PRIVACY = "legal/privacy"
    const val TERMS = "legal/terms"
    const val DETAIL = "rhythm/{rhythmId}?origin={origin}"
    const val CUSTOM = "custom?rhythmId={rhythmId}"
    const val SESSION = "session/{rhythmId}/{duration}/{sound}/{haptics}"
    const val COMPLETE = "complete/{sessionId}/{rhythmId}/{actualSeconds}/{plannedMinutes}/{cycles}"

    fun detail(id: String, origin: String) = "rhythm/${Uri.encode(id)}?origin=$origin"
    fun custom(id: String? = null) = "custom?rhythmId=${Uri.encode(id.orEmpty())}"
    fun session(id: String, duration: Int, sound: Boolean, haptics: Boolean) =
        "session/${Uri.encode(id)}/$duration/$sound/$haptics"
    fun complete(sessionId: Long, id: String, seconds: Int, planned: Int, cycles: Int) =
        "complete/$sessionId/${Uri.encode(id)}/$seconds/$planned/$cycles"
}

@Composable
fun LumyrinthApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val container = remember(context) {
        (context.applicationContext as? LumyrinthApplication)?.container
            ?: com.lumyrinth.app.di.AppContainer(context.applicationContext)
    }
    val prefsRepo = container.userPreferencesRepository
    val sessionRepo = container.sessionRepository

    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory(sessionRepo, prefsRepo))
    val exploreViewModel: ExploreViewModel = viewModel(factory = ExploreViewModel.Factory(sessionRepo, prefsRepo))
    val progressViewModel: ProgressViewModel = viewModel(factory = ProgressViewModel.Factory(sessionRepo))
    val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory(prefsRepo, sessionRepo))
    val customViewModel: CustomRhythmViewModel = viewModel(factory = CustomRhythmViewModel.Factory(sessionRepo))

    val userPrefs by homeViewModel.userPreferences.collectAsStateWithLifecycle()
    val progressSummary by homeViewModel.progressSummary.collectAsStateWithLifecycle()
    val lastUsedRhythm by homeViewModel.lastUsedRhythm.collectAsStateWithLifecycle()
    val customRhythms by exploreViewModel.customRhythms.collectAsStateWithLifecycle()
    val selectedCategory by exploreViewModel.selectedCategory.collectAsStateWithLifecycle()
    progressViewModel.allSessions.collectAsStateWithLifecycle()

    val preferences = userPrefs
    if (preferences == null) {
        LoadingSplash()
        return
    }

    val allRhythms = remember(customRhythms) { PresetRhythms.all + customRhythms }
    val featuredRhythm = remember(preferences.selectedGoals) {
        when {
            "focus" in preferences.selectedGoals -> PresetRhythms.steady
            "sleep" in preferences.selectedGoals -> PresetRhythms.deepRest
            "break" in preferences.selectedGoals -> PresetRhythms.quickReset
            else -> PresetRhythms.slowDown
        }
    }
    val navController = rememberNavController()
    val startDestination = remember { if (preferences.onboardingComplete) Routes.HOME else Routes.WELCOME }
    val entry by navController.currentBackStackEntryAsState()
    val activeTab = when (entry?.destination?.route) {
        Routes.HOME -> AppTab.HOME
        Routes.EXPLORE -> AppTab.EXPLORE
        Routes.PROGRESS -> AppTab.PROGRESS
        Routes.SETTINGS -> AppTab.SETTINGS
        else -> null
    }

    LumyrinthTheme(appTheme = preferences.appTheme) {
        val palette = LumyrinthThemeTokens.palette
        Box(Modifier.fillMaxSize().background(palette.bgBase)) {
            NavHost(navController = navController, startDestination = startDestination) {
            composable(Routes.WELCOME) {
                WelcomeScreen(
                    onGetStarted = { navController.navigate(Routes.GOALS) },
                    onOpenTerms = { navController.navigate(Routes.TERMS) },
                    onOpenPrivacyPolicy = { navController.navigate(Routes.PRIVACY) },
                )
            }
            composable(Routes.GOALS) {
                GoalsScreen(
                    initialGoals = preferences.selectedGoals,
                    onBack = navController::popBackStack,
                    onNext = { goals ->
                        scope.launch { prefsRepo.setSelectedGoals(goals) }
                        navController.navigate(Routes.PREFERENCES)
                    },
                )
            }
            composable(Routes.PREFERENCES) {
                PreferencesScreen(
                    initialHaptics = preferences.hapticGuidanceDefault,
                    initialSound = preferences.soundGuidanceDefault,
                    onBack = navController::popBackStack,
                    onNext = { haptics, sound ->
                        scope.launch {
                            prefsRepo.setHapticGuidanceDefault(haptics)
                            prefsRepo.setSoundGuidanceDefault(sound)
                        }
                        navController.navigate(Routes.FIRST_SESSION)
                    },
                )
            }
            composable(Routes.FIRST_SESSION) {
                FirstSessionScreen(
                    onBeginSession = {
                        scope.launch { prefsRepo.setOnboardingComplete(true) }
                        navController.navigate(Routes.session(PresetRhythms.slowDown.id, 1, preferences.soundGuidanceDefault, preferences.hapticGuidanceDefault)) {
                            popUpTo(Routes.WELCOME) { inclusive = true }
                        }
                    },
                    onExploreFirst = {
                        scope.launch { prefsRepo.setOnboardingComplete(true) }
                        navController.navigate(Routes.EXPLORE) { popUpTo(Routes.WELCOME) { inclusive = true } }
                    },
                )
            }
            composable(Routes.HOME) {
                HomeScreen(
                    featuredRhythm = featuredRhythm,
                    progressSummary = progressSummary,
                    lastUsedRhythm = lastUsedRhythm,
                    onStartFeatured = { navController.navigate(Routes.detail(it.id, "home")) },
                    onMoodFilterClick = {
                        exploreViewModel.selectedCategory.value = it.id
                        navigateMain(navController, Routes.EXPLORE)
                    },
                    onRepeatLastSession = { navController.navigate(Routes.detail(it.id, "home")) },
                    onExploreClick = { navigateMain(navController, Routes.EXPLORE) },
                    onProfileClick = { navigateMain(navController, Routes.SETTINGS) },
                )
            }
            composable(Routes.EXPLORE) {
                ExploreScreen(
                    allRhythms = PresetRhythms.all,
                    customRhythms = customRhythms,
                    favoriteIds = preferences.favoriteRhythmIds,
                    selectedCategory = selectedCategory,
                    onCategoryChange = { exploreViewModel.selectedCategory.value = it },
                    onSelectRhythm = { navController.navigate(Routes.detail(it.id, "explore")) },
                    onCreateCustomClick = { navController.navigate(Routes.custom()) },
                    onEditCustomRhythm = { navController.navigate(Routes.custom(it.id)) },
                    onDeleteCustomRhythm = exploreViewModel::deleteCustomRhythm,
                )
            }
            composable(Routes.PROGRESS) { ProgressScreen(progressSummary) }
            composable(Routes.SETTINGS) {
                SettingsScreen(
                    userPreferences = preferences,
                    onToggleHaptics = settingsViewModel::setHapticsDefault,
                    onToggleSound = settingsViewModel::setSoundDefault,
                    onToggleReminder = { settingsViewModel.setDailyReminder(context, it) },
                    onReminderTimeChange = { settingsViewModel.setReminderTime(context, it) },
                    onAmbientSoundscapeChange = settingsViewModel::setAmbientSoundscape,
                    onThemeChange = settingsViewModel::setAppTheme,
                    onRetakeOnboarding = {
                        settingsViewModel.resetOnboarding()
                        navController.navigate(Routes.WELCOME) { popUpTo(0) }
                    },
                    onOpenPrivacyPolicy = { navController.navigate(Routes.PRIVACY) },
                    onOpenTerms = { navController.navigate(Routes.TERMS) },
                    onClearAllData = {
                        container.ambientAudioController.pause()
                        settingsViewModel.clearAllData(context) {
                            navController.navigate(Routes.WELCOME) { popUpTo(0) }
                        }
                    },
                )
            }
            composable(Routes.PRIVACY) { PrivacyPolicyScreen(navController::popBackStack) }
            composable(Routes.TERMS) { TermsScreen(navController::popBackStack) }
            composable(
                route = Routes.DETAIL,
                arguments = listOf(
                    navArgument("rhythmId") { type = NavType.StringType },
                    navArgument("origin") { type = NavType.StringType; defaultValue = "explore" },
                ),
            ) { backStack ->
                val rhythm = allRhythms.firstOrNull { it.id == backStack.arguments?.getString("rhythmId") }
                if (rhythm == null) LoadingSplash() else DetailScreen(
                    rhythm = rhythm,
                    isFavorite = rhythm.id in preferences.favoriteRhythmIds,
                    defaultSound = if (rhythm.isCustom) rhythm.soundDefault else preferences.soundGuidanceDefault,
                    defaultHaptics = if (rhythm.isCustom) rhythm.hapticsDefault else preferences.hapticGuidanceDefault,
                    onBack = navController::popBackStack,
                    onToggleFavorite = { exploreViewModel.toggleFavorite(rhythm.id) },
                    onBeginSession = { selected, duration, sound, haptics ->
                        navController.navigate(Routes.session(selected.id, duration, sound, haptics))
                    },
                )
            }
            composable(
                route = Routes.CUSTOM,
                arguments = listOf(navArgument("rhythmId") { type = NavType.StringType; defaultValue = "" }),
            ) { backStack ->
                val id = backStack.arguments?.getString("rhythmId").orEmpty()
                CustomRhythmScreen(
                    initialRhythm = allRhythms.firstOrNull { it.id == id },
                    onBack = navController::popBackStack,
                    onSaveRhythm = { existingId, name, inhale, hold1, exhale, hold2, duration, sound, haptics ->
                        customViewModel.saveCustomRhythm(existingId, name, inhale, hold1, exhale, hold2, duration, sound, haptics) { savedId ->
                            navController.navigate(Routes.detail(savedId, "explore")) { popUpTo(Routes.CUSTOM) { inclusive = true } }
                        }
                    },
                )
            }
            composable(
                route = Routes.SESSION,
                arguments = listOf(
                    navArgument("rhythmId") { type = NavType.StringType },
                    navArgument("duration") { type = NavType.IntType },
                    navArgument("sound") { type = NavType.BoolType },
                    navArgument("haptics") { type = NavType.BoolType },
                ),
            ) { backStack ->
                val args = backStack.arguments
                val rhythm = allRhythms.firstOrNull { it.id == args?.getString("rhythmId") }
                if (rhythm == null) LoadingSplash() else {
                    val plannedMinutes = args?.getInt("duration") ?: rhythm.defaultDurationMinutes
                    SessionScreen(
                        rhythm = rhythm,
                        durationMinutes = plannedMinutes,
                        initialSoundOn = args?.getBoolean("sound") ?: preferences.soundGuidanceDefault,
                        initialHapticsOn = args?.getBoolean("haptics") ?: preferences.hapticGuidanceDefault,
                        initialAmbientSound = preferences.ambientSoundscape,
                        onAmbientSoundChanged = {
                            container.ambientAudioController.setVolume(preferences.ambientVolume)
                            container.ambientAudioController.play(it)
                        },
                        onAmbientSoundStopped = container.ambientAudioController::pause,
                        onPhaseTransition = { phase, sound, haptics ->
                            if (sound) container.guidanceSoundController.cue(phase)
                            if (haptics) container.hapticController.cue(phase)
                        },
                        onSessionFinished = { completed, seconds, cycles, sound, haptics ->
                            if (sound) container.guidanceSoundController.complete()
                            if (haptics) container.hapticController.complete()
                            scope.launch {
                                val savedId = sessionRepo.saveSession(
                                    SessionEntity(
                                        rhythmId = rhythm.id,
                                        rhythmNameSnapshot = rhythm.name,
                                        dateIso = LocalDate.now().toString(),
                                        startedAtEpochMillis = System.currentTimeMillis() - seconds * 1_000L,
                                        completedNaturally = completed,
                                        durationMinutesPlanned = plannedMinutes,
                                        durationMinutesActual = seconds / 60,
                                        durationSecondsActual = seconds,
                                        cyclesCompleted = cycles,
                                        soundOn = sound,
                                        hapticsOn = haptics,
                                    )
                                )
                                navController.navigate(Routes.complete(savedId, rhythm.id, seconds, plannedMinutes, cycles)) {
                                    popUpTo(Routes.SESSION) { inclusive = true }
                                }
                            }
                        },
                        onSessionAbandoned = { navigateMain(navController, Routes.HOME) },
                    )
                }
            }
            composable(
                route = Routes.COMPLETE,
                arguments = listOf(
                    navArgument("sessionId") { type = NavType.LongType },
                    navArgument("rhythmId") { type = NavType.StringType },
                    navArgument("actualSeconds") { type = NavType.IntType },
                    navArgument("plannedMinutes") { type = NavType.IntType },
                    navArgument("cycles") { type = NavType.IntType },
                ),
            ) { backStack ->
                val args = backStack.arguments
                val rhythm = allRhythms.firstOrNull { it.id == args?.getString("rhythmId") } ?: PresetRhythms.slowDown
                val sessionId = args?.getLong("sessionId") ?: 0L
                val seconds = args?.getInt("actualSeconds") ?: 0
                val planned = args?.getInt("plannedMinutes") ?: rhythm.defaultDurationMinutes
                val cycles = args?.getInt("cycles") ?: 0
                CompleteScreen(
                    durationSeconds = seconds,
                    cyclesCompleted = cycles,
                    initialMood = null,
                    onMoodSelected = { scope.launch { sessionRepo.updateMood(sessionId, it) } },
                    onDone = { navigateMain(navController, Routes.HOME) },
                    onRepeatSession = {
                        navController.navigate(Routes.session(rhythm.id, planned, rhythm.soundDefault, rhythm.hapticsDefault)) {
                            popUpTo(Routes.COMPLETE) { inclusive = true }
                        }
                    },
                )
            }
        }

        if (activeTab != null) {
            BottomTabBar(
                activeTab = activeTab,
                onTabChange = { tab ->
                    navigateMain(navController, when (tab) {
                        AppTab.HOME -> Routes.HOME
                        AppTab.EXPLORE -> Routes.EXPLORE
                        AppTab.PROGRESS -> Routes.PROGRESS
                        AppTab.SETTINGS -> Routes.SETTINGS
                    })
                },
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}
}

private fun navigateMain(navController: NavHostController, route: String) {
    navController.navigate(route) {
        launchSingleTop = true
        restoreState = true
        popUpTo(Routes.HOME) { saveState = true }
    }
}

@Composable
private fun LoadingSplash() {
    Box(Modifier.fillMaxSize().background(LumyrinthColors.BgBase), contentAlignment = Alignment.Center) {
        Icon(
            painter = painterResource(R.drawable.lumyrinth_mark),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(64.dp),
        )
    }
}
