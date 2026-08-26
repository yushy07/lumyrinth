package com.lumyrinth.app.notifications

import java.time.Duration
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Test

class ReminderSchedulerTest {
    @Test
    fun nextDelaySchedulesLaterToday() {
        val now = Instant.parse("2026-08-26T10:00:00Z")
        val delay = ReminderScheduler.nextDelay(now, LocalTime.of(20, 0), ZoneId.of("UTC"))
        assertEquals(Duration.ofHours(10), delay)
    }

    @Test
    fun nextDelaySchedulesTomorrowAfterTargetPassed() {
        val now = Instant.parse("2026-08-26T21:00:00Z")
        val delay = ReminderScheduler.nextDelay(now, LocalTime.of(20, 0), ZoneId.of("UTC"))
        assertEquals(Duration.ofHours(23), delay)
    }

    @Test
    fun nextDelayUsesLocalTimezoneAcrossDst() {
        val zone = ZoneId.of("America/New_York")
        val now = Instant.parse("2026-03-08T06:30:00Z")
        val delay = ReminderScheduler.nextDelay(now, LocalTime.of(8, 0), zone)
        assertEquals(Duration.ofMinutes(90), delay)
    }

    @Test
    fun invalidTimeFallsBackToEightPm() {
        assertEquals(LocalTime.of(20, 0), ReminderScheduler.parseTime("not-a-time"))
    }
}
