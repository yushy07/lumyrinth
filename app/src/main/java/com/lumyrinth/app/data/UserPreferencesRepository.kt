package com.lumyrinth.app.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.lumyrinthDataStore by preferencesDataStore(name = "lumyrinth_preferences")

data class UserPreferences(
    val onboardingComplete: Boolean = false,
    val hapticsEnabled: Boolean = true,
    val guidanceSoundsEnabled: Boolean = true,
    val ambientSound: String = "None",
    val keepScreenAwake: Boolean = true,
)

class UserPreferencesRepository(private val context: Context) {
    private object Keys {
        val onboardingComplete = booleanPreferencesKey("onboarding_complete")
        val haptics = booleanPreferencesKey("haptics_enabled")
        val guidanceSounds = booleanPreferencesKey("guidance_sounds_enabled")
        val ambientSound = stringPreferencesKey("ambient_sound")
        val keepScreenAwake = booleanPreferencesKey("keep_screen_awake")
    }

    val preferences: Flow<UserPreferences> = context.lumyrinthDataStore.data.map { stored ->
        UserPreferences(
            onboardingComplete = stored[Keys.onboardingComplete] ?: false,
            hapticsEnabled = stored[Keys.haptics] ?: true,
            guidanceSoundsEnabled = stored[Keys.guidanceSounds] ?: true,
            ambientSound = stored[Keys.ambientSound] ?: "None",
            keepScreenAwake = stored[Keys.keepScreenAwake] ?: true,
        )
    }

    suspend fun completeOnboarding() = context.lumyrinthDataStore.edit { it[Keys.onboardingComplete] = true }
    suspend fun setHaptics(enabled: Boolean) = context.lumyrinthDataStore.edit { it[Keys.haptics] = enabled }
    suspend fun setGuidanceSounds(enabled: Boolean) = context.lumyrinthDataStore.edit { it[Keys.guidanceSounds] = enabled }
    suspend fun setAmbientSound(sound: String) = context.lumyrinthDataStore.edit { it[Keys.ambientSound] = sound }
    suspend fun setKeepScreenAwake(enabled: Boolean) = context.lumyrinthDataStore.edit { it[Keys.keepScreenAwake] = enabled }
}
