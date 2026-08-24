package com.lumyrinth.app.haptics

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import com.lumyrinth.app.domain.BreathPhase

/** Short boundary cues only; Lumyrinth never vibrates continuously. */
class HapticGuide(context: Context) {
    private val vibrator = context.getSystemService(Vibrator::class.java)

    fun cue(phase: BreathPhase) {
        val pattern = when (phase) {
            BreathPhase.INHALE -> longArrayOf(0, 18)
            BreathPhase.HOLD_AFTER_INHALE, BreathPhase.HOLD_AFTER_EXHALE -> longArrayOf(0, 10)
            BreathPhase.EXHALE -> longArrayOf(0, 14, 42, 14)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else @Suppress("DEPRECATION") {
            vibrator.vibrate(pattern, -1)
        }
    }

    fun complete() {
        val pattern = longArrayOf(0, 20, 55, 35)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
        else @Suppress("DEPRECATION") vibrator.vibrate(pattern, -1)
    }
}
