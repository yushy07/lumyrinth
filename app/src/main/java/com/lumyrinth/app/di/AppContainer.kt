package com.lumyrinth.app.di

import android.content.Context
import com.lumyrinth.app.audio.AmbientAudioController
import com.lumyrinth.app.audio.GuidanceSoundController
import com.lumyrinth.app.data.UserPreferencesRepository
import com.lumyrinth.app.data.session.SessionRepository
import com.lumyrinth.app.haptics.HapticController

/**
 * Dependency Injection Container managing singletons across the Lumyrinth application.
 */
class AppContainer(private val context: Context) {
    val sessionRepository: SessionRepository by lazy {
        SessionRepository.from(context)
    }

    val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context)
    }

    val guidanceSoundController: GuidanceSoundController by lazy {
        GuidanceSoundController(context)
    }

    val hapticController: HapticController by lazy {
        HapticController(context)
    }

    val ambientAudioController: AmbientAudioController by lazy {
        AmbientAudioController(context)
    }
}
