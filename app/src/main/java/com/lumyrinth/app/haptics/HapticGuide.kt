package com.lumyrinth.app.haptics

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import com.lumyrinth.app.domain.BreathPhase

/** Short boundary cues only; Lumyrinth never vibrates continuously. */
class HapticGuide(context: Context) {
    private val appContext = context.applicationContext
    private val vibrator: Vibrator? = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator ?: (appContext.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator)
        } else {
            @Suppress("DEPRECATION")
            appContext.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    } catch (_: Throwable) {
        null
    }

    private val isVibrationAvailable: Boolean = try {
        vibrator?.hasVibrator() == true
    } catch (_: Throwable) {
        false
    }

    fun cue(phase: BreathPhase) {
        if (!isVibrationAvailable) return
        val vib = vibrator ?: return
        try {
            val pattern = when (phase) {
                BreathPhase.INHALE -> longArrayOf(0, 18)
                BreathPhase.HOLD_AFTER_INHALE, BreathPhase.HOLD_AFTER_EXHALE -> longArrayOf(0, 10)
                BreathPhase.EXHALE -> longArrayOf(0, 14, 42, 14)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vib.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(pattern, -1)
            }
        } catch (_: Throwable) {
            // Silently ignore
        }
    }

    fun complete() {
        if (!isVibrationAvailable) return
        val vib = vibrator ?: return
        try {
            val pattern = longArrayOf(0, 20, 55, 35)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vib.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(pattern, -1)
            }
        } catch (_: Throwable) {
            // Silently ignore
        }
    }
}
