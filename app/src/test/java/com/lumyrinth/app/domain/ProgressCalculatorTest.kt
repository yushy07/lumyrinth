package com.lumyrinth.app.domain

import com.lumyrinth.app.data.session.SessionEntity
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Locale

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

    @Test
    fun computeProgress_combinesShortSessionsBeforeRounding() {
        val today = LocalDate.of(2026, 8, 26)
        val zone = ZoneId.of("UTC")
        val sessions = listOf(sessionAt(today, 40, zone, 1), sessionAt(today, 40, zone, 2))
        val summary = ProgressCalculator.compute(sessions, zone, today, Locale.US)
        assertEquals(1, summary.totalMindfulMinutes)
        assertEquals(2.0 / 3.0, summary.averageSessionMinutes, 0.001)
    }

    @Test
    fun computeProgress_countsCurrentAndLongestStreaks() {
        val today = LocalDate.of(2026, 8, 26)
        val zone = ZoneId.of("UTC")
        val dates = listOf(today.minusDays(6), today.minusDays(5), today.minusDays(4), today.minusDays(1), today)
        val sessions = dates.mapIndexed { index, date -> sessionAt(date, 60, zone, index.toLong()) }
        val summary = ProgressCalculator.compute(sessions, zone, today, Locale.US)
        assertEquals(2, summary.currentStreakDays)
        assertEquals(3, summary.longestStreakDays)
    }

    @Test
    fun computeProgress_usesSuppliedTimezoneAtDayBoundary() {
        val zone = ZoneId.of("Asia/Kolkata")
        val today = LocalDate.of(2026, 8, 27)
        val instant = ZonedDateTime.parse("2026-08-26T23:30:00Z").toInstant().toEpochMilli()
        val session = sessionAt(today, 60, zone, 1).copy(startedAtEpochMillis = instant)
        val summary = ProgressCalculator.compute(listOf(session), zone, today, Locale.US)
        assertEquals(1, summary.todaysSessionCount)
    }

    private fun sessionAt(date: LocalDate, seconds: Int, zone: ZoneId, id: Long) = SessionEntity(
        id = id,
        rhythmId = "slow_down",
        rhythmNameSnapshot = "Slow Down",
        dateIso = date.toString(),
        startedAtEpochMillis = date.atStartOfDay(zone).toInstant().toEpochMilli(),
        completedNaturally = true,
        durationMinutesPlanned = 1,
        durationMinutesActual = seconds / 60,
        durationSecondsActual = seconds,
        cyclesCompleted = 0,
        soundOn = false,
        hapticsOn = false,
    )
}
