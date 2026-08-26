package com.lumyrinth.app.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.lumyrinthDataStore by preferencesDataStore(name = "lumyrinth_preferences")

data class UserPreferences(
    val onboardingComplete: Boolean = false,
    val selectedGoals: Set<String> = setOf("relax", "focus", "build_habit"),
    val hapticGuidanceDefault: Boolean = true,
    val soundGuidanceDefault: Boolean = true,
    val ambientSoundscape: String = "None",
    val ambientVolume: Float = 0.35f,
    val dailyReminderEnabled: Boolean = false,
    val dailyReminderTime: String = "20:00",
    val favoriteRhythmIds: Set<String> = emptySet(),
    val appTheme: String = "twilight",
)

class UserPreferencesRepository(private val context: Context) {
    private object Keys {
        val onboardingComplete = booleanPreferencesKey("onboarding_complete")
        val selectedGoals = stringSetPreferencesKey("selected_goals")
        val haptics = booleanPreferencesKey("haptics_guidance_default")
        val sound = booleanPreferencesKey("sound_guidance_default")
        val ambientSoundscape = stringPreferencesKey("ambient_soundscape")
        val ambientVolume = floatPreferencesKey("ambient_volume")
        val dailyReminderEnabled = booleanPreferencesKey("daily_reminder_enabled")
        val dailyReminderTime = stringPreferencesKey("daily_reminder_time")
        val favoriteRhythmIds = stringSetPreferencesKey("favorite_rhythm_ids")
        val appTheme = stringPreferencesKey("app_theme")
    }

    val preferences: Flow<UserPreferences> = context.lumyrinthDataStore.data.map { stored ->
        UserPreferences(
            onboardingComplete = stored[Keys.onboardingComplete] ?: false,
            selectedGoals = stored[Keys.selectedGoals] ?: setOf("relax", "focus", "build_habit"),
            hapticGuidanceDefault = stored[Keys.haptics] ?: true,
            soundGuidanceDefault = stored[Keys.sound] ?: true,
            ambientSoundscape = stored[Keys.ambientSoundscape] ?: "None",
            ambientVolume = (stored[Keys.ambientVolume] ?: 0.35f).coerceIn(0f, 1f),
            dailyReminderEnabled = stored[Keys.dailyReminderEnabled] ?: false,
            dailyReminderTime = stored[Keys.dailyReminderTime] ?: "20:00",
            favoriteRhythmIds = stored[Keys.favoriteRhythmIds] ?: emptySet(),
            appTheme = stored[Keys.appTheme] ?: "twilight",
        )
    }

    suspend fun setOnboardingComplete(complete: Boolean) = context.lumyrinthDataStore.edit {
        it[Keys.onboardingComplete] = complete
    }

    suspend fun setSelectedGoals(goals: Set<String>) = context.lumyrinthDataStore.edit {
        it[Keys.selectedGoals] = goals
    }

    suspend fun setHapticGuidanceDefault(enabled: Boolean) = context.lumyrinthDataStore.edit {
        it[Keys.haptics] = enabled
    }

    suspend fun setSoundGuidanceDefault(enabled: Boolean) = context.lumyrinthDataStore.edit {
        it[Keys.sound] = enabled
    }

    suspend fun setAmbientSoundscape(soundscape: String) = context.lumyrinthDataStore.edit {
        it[Keys.ambientSoundscape] = soundscape
    }

    suspend fun setAmbientVolume(volume: Float) = context.lumyrinthDataStore.edit {
        it[Keys.ambientVolume] = volume.coerceIn(0f, 1f)
    }

    suspend fun setDailyReminderEnabled(enabled: Boolean) = context.lumyrinthDataStore.edit {
        it[Keys.dailyReminderEnabled] = enabled
    }

    suspend fun setDailyReminderTime(time: String) = context.lumyrinthDataStore.edit {
        it[Keys.dailyReminderTime] = time
    }

    suspend fun toggleFavorite(rhythmId: String) = context.lumyrinthDataStore.edit { prefs ->
        val current = prefs[Keys.favoriteRhythmIds] ?: emptySet()
        prefs[Keys.favoriteRhythmIds] = if (rhythmId in current) current - rhythmId else current + rhythmId
    }

    suspend fun setAppTheme(theme: String) = context.lumyrinthDataStore.edit {
        it[Keys.appTheme] = theme
    }

    suspend fun resetOnboarding() = context.lumyrinthDataStore.edit {
        it[Keys.onboardingComplete] = false
    }

    suspend fun clearAllPreferences() = context.lumyrinthDataStore.edit {
        it.clear()
    }
}
