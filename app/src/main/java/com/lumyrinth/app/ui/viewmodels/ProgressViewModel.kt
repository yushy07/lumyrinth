package com.lumyrinth.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lumyrinth.app.data.session.SessionEntity
import com.lumyrinth.app.data.session.SessionRepository
import com.lumyrinth.app.domain.ProgressCalculator
import com.lumyrinth.app.domain.ProgressSummary
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ProgressViewModel(
    private val sessionRepo: SessionRepository
) : ViewModel() {

    val allSessions: StateFlow<List<SessionEntity>> = sessionRepo.sessions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val progressSummary: StateFlow<ProgressSummary> = allSessions
        .map { sessions -> ProgressCalculator.compute(sessions) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ProgressSummary()
        )

    class Factory(
        private val sessionRepo: SessionRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProgressViewModel(sessionRepo) as T
        }
    }
}
