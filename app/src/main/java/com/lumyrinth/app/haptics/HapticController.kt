package com.lumyrinth.app.haptics

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.lumyrinth.app.domain.BreathPhase

class HapticController(context: Context) {
    private val vibrator: Vibrator? = try {
        val appContext = context.applicationContext
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
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

    fun tick() {
        if (!isVibrationAvailable) return
        val v = vibrator ?: return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                v.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                v.vibrate(VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE))
            }
        } catch (_: Throwable) {
            // Gracefully ignore
        }
    }

    fun cue(phase: BreathPhase) {
        if (!isVibrationAvailable) return
        val v = vibrator ?: return

        try {
            val effect = when (phase) {
                BreathPhase.INHALE -> VibrationEffect.createOneShot(45, VibrationEffect.DEFAULT_AMPLITUDE)
                BreathPhase.EXHALE -> VibrationEffect.createOneShot(65, VibrationEffect.DEFAULT_AMPLITUDE)
                BreathPhase.HOLD_AFTER_INHALE, BreathPhase.HOLD_AFTER_EXHALE ->
                    VibrationEffect.createWaveform(longArrayOf(0, 25, 40, 25), -1)
            }
            v.vibrate(effect)
        } catch (_: Throwable) {
            // Gracefully ignore vibration errors on unsupported hardware
        }
    }

    fun complete() {
        if (!isVibrationAvailable) return
        val v = vibrator ?: return

        try {
            val timings = longArrayOf(0, 60, 60, 90)
            v.vibrate(VibrationEffect.createWaveform(timings, -1))
        } catch (_: Throwable) {
            // Gracefully ignore
        }
    }
}

