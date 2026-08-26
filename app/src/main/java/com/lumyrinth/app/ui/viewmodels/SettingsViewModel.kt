package com.lumyrinth.app.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lumyrinth.app.data.UserPreferences
import com.lumyrinth.app.data.UserPreferencesRepository
import com.lumyrinth.app.data.session.SessionRepository
import com.lumyrinth.app.notifications.ReminderScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val prefsRepo: UserPreferencesRepository,
    private val sessionRepo: SessionRepository,
) : ViewModel() {

    val userPreferences: StateFlow<UserPreferences> = prefsRepo.preferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    fun setHapticsDefault(enabled: Boolean) {
        viewModelScope.launch { prefsRepo.setHapticGuidanceDefault(enabled) }
    }

    fun setSoundDefault(enabled: Boolean) {
        viewModelScope.launch { prefsRepo.setSoundGuidanceDefault(enabled) }
    }

    fun setAmbientSoundscape(soundscape: String) {
        viewModelScope.launch { prefsRepo.setAmbientSoundscape(soundscape) }
    }

    fun setReminderTime(context: Context, time: String) {
        viewModelScope.launch {
            prefsRepo.setDailyReminderTime(time)
            if (userPreferences.value.dailyReminderEnabled) ReminderScheduler.schedule(context, time)
        }
    }

    fun setDailyReminder(context: Context, enabled: Boolean) {
        viewModelScope.launch {
            prefsRepo.setDailyReminderEnabled(enabled)
            if (enabled) {
                ReminderScheduler.schedule(context, userPreferences.value.dailyReminderTime)
            } else {
                ReminderScheduler.disable(context)
            }
        }
    }

    fun setAppTheme(theme: String) {
        viewModelScope.launch { prefsRepo.setAppTheme(theme) }
    }

    fun resetOnboarding() {
        viewModelScope.launch { prefsRepo.resetOnboarding() }
    }

    fun clearAllData(context: Context, onCleared: () -> Unit) {
        viewModelScope.launch {
            ReminderScheduler.disable(context)
            sessionRepo.clearAllData()
            prefsRepo.clearAllPreferences()
            onCleared()
        }
    }

    class Factory(
        private val prefsRepo: UserPreferencesRepository,
        private val sessionRepo: SessionRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(prefsRepo, sessionRepo) as T
        }
    }
}
