package com.lumyrinth.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lumyrinth.app.data.session.CustomRhythmEntity
import com.lumyrinth.app.data.session.SessionRepository
import kotlinx.coroutines.launch
import java.util.UUID

class CustomRhythmViewModel(
    private val sessionRepo: SessionRepository
) : ViewModel() {

    fun saveCustomRhythm(
        existingId: String?,
        name: String,
        inhaleSeconds: Int,
        hold1Seconds: Int,
        exhaleSeconds: Int,
        hold2Seconds: Int,
        defaultDurationMinutes: Int,
        soundDefault: Boolean = true,
        hapticsDefault: Boolean = true,
        onComplete: (String) -> Unit
    ) {
        val totalCycleSeconds = inhaleSeconds + hold1Seconds + exhaleSeconds + hold2Seconds
        if (totalCycleSeconds <= 0 || name.isBlank()) return

        viewModelScope.launch {
            val finalId = existingId ?: UUID.randomUUID().toString()
            val existing = existingId?.let { sessionRepo.getCustomRhythm(it) }
            val entity = CustomRhythmEntity(
                id = finalId,
                name = name.trim(),
                inhaleSeconds = inhaleSeconds.coerceAtLeast(0),
                hold1Seconds = hold1Seconds.coerceAtLeast(0),
                exhaleSeconds = exhaleSeconds.coerceAtLeast(0),
                hold2Seconds = hold2Seconds.coerceAtLeast(0),
                defaultDurationMinutes = defaultDurationMinutes.coerceAtLeast(1),
                soundDefault = soundDefault,
                hapticsDefault = hapticsDefault,
                createdAtEpochMillis = existing?.createdAtEpochMillis ?: System.currentTimeMillis(),
            )
            sessionRepo.saveCustomRhythm(entity)
            onComplete(finalId)
        }
    }

    class Factory(
        private val sessionRepo: SessionRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CustomRhythmViewModel(sessionRepo) as T
        }
    }
}
