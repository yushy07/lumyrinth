package com.lumyrinth.app.domain

import kotlin.math.ceil

data class SessionSnapshot(
    val phase: BreathPhase,
    val phaseProgress: Float,
    val phaseRemainingSeconds: Int,
    val targetRemainingMillis: Long,
    val cyclesCompleted: Int,
    val isFinishingCycle: Boolean,
)

/** Derives the entire session from elapsed monotonic time; it never accumulates timer drift. */
fun sessionSnapshot(pattern: BreathingPattern, targetMillis: Long, elapsedMillis: Long): SessionSnapshot {
    val position = elapsedMillis % pattern.cycleMillis
    var phaseStart = 0L
    val (phase, durationSeconds) = pattern.phases().first { (_, seconds) ->
        val end = phaseStart + seconds * 1_000L
        if (position < end) true else { phaseStart = end; false }
    }
    val phaseDuration = durationSeconds * 1_000L
    val progress = ((position - phaseStart).toFloat() / phaseDuration).coerceIn(0f, 1f)
    return SessionSnapshot(
        phase = phase,
        phaseProgress = progress,
        phaseRemainingSeconds = ceil((phaseDuration - (position - phaseStart)) / 1_000.0).toInt().coerceAtLeast(1),
        targetRemainingMillis = (targetMillis - elapsedMillis).coerceAtLeast(0),
        cyclesCompleted = (elapsedMillis / pattern.cycleMillis).toInt(),
        isFinishingCycle = elapsedMillis >= targetMillis,
    )
}
