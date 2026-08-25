package com.lumyrinth.app.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import com.lumyrinth.app.R
import com.lumyrinth.app.domain.BreathPhase

class GuidanceSoundController(context: Context) {
    private val appContext = context.applicationContext
    private var pool: SoundPool? = null
    private var inhale: Int = 0
    private var hold: Int = 0
    private var exhale: Int = 0
    private var complete: Int = 0

    init {
        try {
            val p = SoundPool.Builder()
                .setMaxStreams(2)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                ).build()
            pool = p
            inhale = p.load(appContext, R.raw.cue_inhale, 1)
            hold = p.load(appContext, R.raw.cue_hold, 1)
            exhale = p.load(appContext, R.raw.cue_exhale, 1)
            complete = p.load(appContext, R.raw.cue_complete, 1)
        } catch (e: Throwable) {
            Log.w("GuidanceSound", "Failed to initialize SoundPool", e)
        }
    }

    fun cue(phase: BreathPhase) {
        try {
            val p = pool ?: return
            val soundId = when (phase) {
                BreathPhase.INHALE -> inhale
                BreathPhase.EXHALE -> exhale
                else -> hold
            }
            if (soundId != 0) {
                p.play(soundId, 0.55f, 0.55f, 1, 0, 1f)
            }
        } catch (e: Throwable) {
            Log.w("GuidanceSound", "Error playing cue", e)
        }
    }

    fun complete() {
        try {
            val p = pool ?: return
            if (complete != 0) {
                p.play(complete, 0.65f, 0.65f, 1, 0, 1f)
            }
        } catch (e: Throwable) {
            Log.w("GuidanceSound", "Error playing complete sound", e)
        }
    }

    fun release() {
        try {
            pool?.release()
            pool = null
        } catch (e: Throwable) {
            Log.w("GuidanceSound", "Error releasing SoundPool", e)
        }
    }
}

