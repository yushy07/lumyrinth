package com.lumyrinth.app.domain

import com.lumyrinth.app.data.session.SessionEntity
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class ProgressCalculatorTest {

    @Test
    fun computeProgress_withNoSessions_returnsZeros() {
        val summary = ProgressCalculator.compute(
            sessions = emptyList(),
            zoneId = ZoneId.of("UTC"),
            today = LocalDate.of(2025, 1, 15)
        )

        assertEquals(0, summary.todaysSessionCount)
        assertEquals(0, summary.todaysMindfulMinutes)
        assertEquals(0, summary.totalSessionsCount)
        assertEquals(0, summary.totalMindfulMinutes)
        assertEquals(0, summary.currentStreakDays)
        assertEquals(0.0, summary.averageSessionMinutes, 0.001)
    }

    @Test
    fun computeProgress_aggregatesActualSecondsCorrectly() {
        val nowMillis = System.currentTimeMillis()
        val session1 = SessionEntity(
            id = 1,
            rhythmId = "square",
            rhythmNameSnapshot = "Box Breathing",
            dateIso = "2025-01-15",
            startedAtEpochMillis = nowMillis,
            completedNaturally = true,
            durationMinutesPlanned = 5,
            durationMinutesActual = 1,
            durationSecondsActual = 45, // 45 seconds
            cyclesCompleted = 3,
            soundOn = true,
            hapticsOn = true
        )

        val session2 = SessionEntity(
            id = 2,
            rhythmId = "square",
            rhythmNameSnapshot = "Box Breathing",
            dateIso = "2025-01-15",
            startedAtEpochMillis = nowMillis + 1000,
            completedNaturally = true,
            durationMinutesPlanned = 5,
            durationMinutesActual = 2,
            durationSecondsActual = 90, // 90 seconds
            cyclesCompleted = 5,
            soundOn = true,
            hapticsOn = true
        )

        val summary = ProgressCalculator.compute(
            sessions = listOf(session1, session2),
            zoneId = ZoneId.systemDefault(),
            today = LocalDate.now()
        )

        // Total seconds: 45 + 90 = 135s -> 2 mindful minutes (135 / 60)
        assertEquals(2, summary.todaysSessionCount)
        assertEquals(2, summary.todaysMindfulMinutes)
        assertEquals(2, summary.totalSessionsCount)
        assertEquals(2, summary.totalMindfulMinutes)
    }
}
