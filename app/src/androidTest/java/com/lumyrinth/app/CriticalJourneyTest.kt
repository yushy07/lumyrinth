package com.lumyrinth.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlinx.coroutines.runBlocking

@RunWith(AndroidJUnit4::class)
class CriticalJourneyTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun resetOnboarding() {
        composeRule.activityRule.scenario.onActivity { activity ->
            runBlocking {
                (activity.application as LumyrinthApplication).container
                    .userPreferencesRepository.clearAllPreferences()
            }
        }
        composeRule.activityRule.scenario.recreate()
        composeRule.waitForIdle()
    }

    @Test
    fun onboardingCanOpenPrivacyAndReturnToWelcome() {
        composeRule.onNodeWithText("Privacy Policy").performClick()
        composeRule.onNodeWithText("100% On-Device & Private").assertIsDisplayed()
        composeRule.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        composeRule.onNodeWithText("Get Started").assertIsDisplayed()
    }

    @Test
    fun onboardingGoalsPreserveAVisibleSelection() {
        composeRule.onNodeWithText("Get Started").performClick()
        composeRule.onNodeWithText("What are you looking for?").assertIsDisplayed()
        composeRule.onNodeWithText("Relax").assertIsDisplayed()
    }
}
