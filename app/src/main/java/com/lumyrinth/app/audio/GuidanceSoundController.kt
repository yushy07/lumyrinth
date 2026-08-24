package com.lumyrinth.app.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.lumyrinth.app.R
import com.lumyrinth.app.domain.BreathPhase

class GuidanceSoundController(context: Context) {
    private val pool = SoundPool.Builder().setMaxStreams(2).setAudioAttributes(
        AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build(),
    ).build()
    private val inhale = pool.load(context, R.raw.cue_inhale, 1)
    private val hold = pool.load(context, R.raw.cue_hold, 1)
    private val exhale = pool.load(context, R.raw.cue_exhale, 1)
    private val complete = pool.load(context, R.raw.cue_complete, 1)
    fun cue(phase: BreathPhase) = pool.play(if (phase == BreathPhase.INHALE) inhale else if (phase == BreathPhase.EXHALE) exhale else hold, .55f, .55f, 1, 0, 1f)
    fun complete() = pool.play(complete, .65f, .65f, 1, 0, 1f)
    fun release() = pool.release()
}
