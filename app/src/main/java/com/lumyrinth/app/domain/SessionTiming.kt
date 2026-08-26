package com.lumyrinth.app.domain

enum class SessionStatus { READY, RUNNING, PAUSED, COMPLETING, COMPLETED, ABANDONED }

data class PhasePosition(
    val phase: BreathPhase,
    val phaseIndex: Int,
    val phaseElapsedMillis: Long,
    val cyclesCompleted: Int,
)

object SessionTiming {
    fun activeElapsedMillis(
        nowElapsedRealtime: Long,
        startedElapsedRealtime: Long,
        accumulatedPauseMillis: Long,
        pauseStartedElapsedRealtime: Long?,
    ): Long {
        val currentPause = pauseStartedElapsedRealtime?.let { (nowElapsedRealtime - it).coerceAtLeast(0) } ?: 0
        return (nowElapsedRealtime - startedElapsedRealtime - accumulatedPauseMillis - currentPause).coerceAtLeast(0)
    }

    fun positionAt(rhythm: Rhythm, activeElapsedMillis: Long): PhasePosition {
        val phases = rhythm.activePhases()
        if (phases.isEmpty() || rhythm.cycleSeconds <= 0) {
            return PhasePosition(BreathPhase.INHALE, 0, 0, 0)
        }
        val cycleMillis = rhythm.cycleSeconds * 1_000L
        val elapsed = activeElapsedMillis.coerceAtLeast(0)
        val cycles = (elapsed / cycleMillis).toInt()
        val offset = elapsed % cycleMillis
        var phaseStart = 0L
        phases.forEachIndexed { index, (_, seconds) ->
            val phaseEnd = phaseStart + seconds * 1_000L
            if (offset < phaseEnd) {
                return PhasePosition(phases[index].first, index, offset - phaseStart, cycles)
            }
            phaseStart = phaseEnd
        }
        return PhasePosition(phases.last().first, phases.lastIndex, phases.last().second * 1_000L, cycles)
    }
}
