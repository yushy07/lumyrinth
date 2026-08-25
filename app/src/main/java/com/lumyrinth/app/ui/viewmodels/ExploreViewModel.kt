package com.lumyrinth.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lumyrinth.app.data.UserPreferencesRepository
import com.lumyrinth.app.data.session.SessionRepository
import com.lumyrinth.app.domain.Rhythm
import com.lumyrinth.app.domain.RhythmCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExploreViewModel(
    private val sessionRepo: SessionRepository,
    private val prefsRepo: UserPreferencesRepository,
) : ViewModel() {

    val selectedCategory = MutableStateFlow("all")
    val searchQuery = MutableStateFlow("")

    val favoriteIds: StateFlow<Set<String>> = prefsRepo.preferences
        .map { prefs -> prefs.favoriteRhythmIds }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    val customRhythms: StateFlow<List<Rhythm>> = sessionRepo.customRhythms
        .map { entities ->
            entities.map { entity ->
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
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun toggleFavorite(rhythmId: String) {
        viewModelScope.launch {
            prefsRepo.toggleFavorite(rhythmId)
        }
    }

    fun deleteCustomRhythm(rhythmId: String) {
        viewModelScope.launch {
            sessionRepo.deleteCustomRhythm(rhythmId)
        }
    }

    class Factory(
        private val sessionRepo: SessionRepository,
        private val prefsRepo: UserPreferencesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ExploreViewModel(sessionRepo, prefsRepo) as T
        }
    }
}
