package com.lumyrinth.app.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class SessionTimingTest {
    @Test
    fun activeElapsedExcludesCompletedAndCurrentPause() {
        assertEquals(
            5_000L,
            SessionTiming.activeElapsedMillis(
                nowElapsedRealtime = 20_000,
                startedElapsedRealtime = 10_000,
                accumulatedPauseMillis = 2_000,
                pauseStartedElapsedRealtime = 17_000,
            )
        )
    }

    @Test
    fun derivesPhaseAndCyclesFromMonotonicElapsedTime() {
        val position = SessionTiming.positionAt(PresetRhythms.square, 18_500)
        assertEquals(1, position.cyclesCompleted)
        assertEquals(BreathPhase.INHALE, position.phase)
        assertEquals(2_500L, position.phaseElapsedMillis)
    }

    @Test
    fun zeroLengthHoldsAreSkipped() {
        val position = SessionTiming.positionAt(PresetRhythms.slowDown, 4_500)
        assertEquals(BreathPhase.EXHALE, position.phase)
        assertEquals(500L, position.phaseElapsedMillis)
    }
}
