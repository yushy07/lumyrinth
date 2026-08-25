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
    private val vibrator: Vibrator? = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator ?: (context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator)
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    } catch (e: Throwable) {
        Log.w("HapticGuide", "Failed to obtain vibrator service", e)
        null
    }

    fun cue(phase: BreathPhase) {
        val vib = vibrator ?: return
        try {
            if (!vib.hasVibrator()) return
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
        } catch (e: Throwable) {
            Log.w("HapticGuide", "Failed to execute vibration cue", e)
        }
    }

    fun complete() {
        val vib = vibrator ?: return
        try {
            if (!vib.hasVibrator()) return
            val pattern = longArrayOf(0, 20, 55, 35)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vib.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(pattern, -1)
            }
        } catch (e: Throwable) {
            Log.w("HapticGuide", "Failed to execute complete vibration", e)
        }
    }
}
