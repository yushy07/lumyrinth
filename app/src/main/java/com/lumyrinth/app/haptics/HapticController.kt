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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                v.areAllPrimitivesSupported(VibrationEffect.Composition.PRIMITIVE_TICK)
            ) {
                v.vibrate(
                    VibrationEffect.startComposition()
                        .addPrimitive(VibrationEffect.Composition.PRIMITIVE_TICK, 0.6f)
                        .compose()
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                v.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
            } else {
                v.vibrate(VibrationEffect.createOneShot(12, VibrationEffect.DEFAULT_AMPLITUDE))
            }
        } catch (_: Throwable) {
            // Gracefully ignore
        }
    }

    fun cue(phase: BreathPhase) {
        if (!isVibrationAvailable) return
        val v = vibrator ?: return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                when (phase) {
                    BreathPhase.INHALE -> {
                        if (v.areAllPrimitivesSupported(VibrationEffect.Composition.PRIMITIVE_SLOW_RISE, VibrationEffect.Composition.PRIMITIVE_CLICK)) {
                            v.vibrate(
                                VibrationEffect.startComposition()
                                    .addPrimitive(VibrationEffect.Composition.PRIMITIVE_SLOW_RISE, 0.7f)
                                    .addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK, 0.5f, 50)
                                    .compose()
                            )
                            return
                        }
                    }
                    BreathPhase.EXHALE -> {
                        if (v.areAllPrimitivesSupported(VibrationEffect.Composition.PRIMITIVE_THUD)) {
                            v.vibrate(
                                VibrationEffect.startComposition()
                                    .addPrimitive(VibrationEffect.Composition.PRIMITIVE_THUD, 0.9f)
                                    .compose()
                            )
                            return
                        }
                    }
                    BreathPhase.HOLD_AFTER_INHALE, BreathPhase.HOLD_AFTER_EXHALE -> {
                        if (v.areAllPrimitivesSupported(VibrationEffect.Composition.PRIMITIVE_TICK)) {
                            v.vibrate(
                                VibrationEffect.startComposition()
                                    .addPrimitive(VibrationEffect.Composition.PRIMITIVE_TICK, 0.8f)
                                    .addPrimitive(VibrationEffect.Composition.PRIMITIVE_TICK, 0.5f, 100)
                                    .compose()
                            )
                            return
                        }
                    }
                }
            }

            // Fallback for earlier versions
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                v.areAllPrimitivesSupported(
                    VibrationEffect.Composition.PRIMITIVE_QUICK_RISE,
                    VibrationEffect.Composition.PRIMITIVE_THUD
                )
            ) {
                v.vibrate(
                    VibrationEffect.startComposition()
                        .addPrimitive(VibrationEffect.Composition.PRIMITIVE_QUICK_RISE, 0.8f)
                        .addPrimitive(VibrationEffect.Composition.PRIMITIVE_THUD, 1.0f, 80)
                        .compose()
                )
            } else {
                val timings = longArrayOf(0, 60, 60, 90)
                v.vibrate(VibrationEffect.createWaveform(timings, -1))
            }
        } catch (_: Throwable) {
            // Gracefully ignore
        }
    }
}

