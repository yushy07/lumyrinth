package com.lumyrinth.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lumyrinth.app.data.UserPreferences
import com.lumyrinth.app.data.UserPreferencesRepository
import com.lumyrinth.app.data.session.SessionRepository
import com.lumyrinth.app.domain.PresetRhythms
import com.lumyrinth.app.domain.ProgressCalculator
import com.lumyrinth.app.domain.ProgressSummary
import com.lumyrinth.app.domain.Rhythm
import com.lumyrinth.app.domain.RhythmCategory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    private val sessionRepo: SessionRepository,
    private val prefsRepo: UserPreferencesRepository,
) : ViewModel() {

    val userPreferences: StateFlow<UserPreferences> = prefsRepo.preferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    val progressSummary: StateFlow<ProgressSummary> = sessionRepo.sessions
        .map { sessions -> ProgressCalculator.compute(sessions) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ProgressSummary()
        )

    val lastUsedRhythm: StateFlow<Rhythm?> = combine(
        sessionRepo.sessions,
        sessionRepo.customRhythms
    ) { sessions, customEntities ->
        val lastSession = sessions.maxByOrNull { it.startedAtEpochMillis } ?: return@combine null
        val customRhythms = customEntities.map { entity ->
            Rhythm(
                id = entity.id,
                name = entity.name,
                category = RhythmCategory.CUSTOM,
                inhaleSeconds = entity.inhaleSeconds,
                hold1Seconds = entity.hold1Seconds,
                exhaleSeconds = entity.exhaleSeconds,
                hold2Seconds = entity.hold2Seconds,
                shortDescription = "Custom breathing pattern",
                isCustom = true,
                defaultDurationMinutes = entity.defaultDurationMinutes,
                soundDefault = entity.soundDefault,
                hapticsDefault = entity.hapticsDefault
            )
        }
        val allRhythms = PresetRhythms.all + customRhythms
        allRhythms.find { it.id == lastSession.rhythmId }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    class Factory(
        private val sessionRepo: SessionRepository,
        private val prefsRepo: UserPreferencesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(sessionRepo, prefsRepo) as T
        }
    }
}
