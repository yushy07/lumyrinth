package com.lumyrinth.app.domain

import com.lumyrinth.app.data.session.SessionEntity
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

data class ProgressSummary(val todayMinutes: Int, val todaySessions: Int, val currentRhythmDays: Int, val bestRhythmDays: Int)

fun progressSummary(sessions: List<SessionEntity>, zoneId: ZoneId = ZoneId.systemDefault(), today: LocalDate = LocalDate.now(zoneId)): ProgressSummary {
    val completedDates = sessions.filter { it.completed }.map { Instant.ofEpochMilli(it.startedAtEpochMillis).atZone(zoneId).toLocalDate() }.distinct().sortedDescending()
    val todayItems = sessions.filter { Instant.ofEpochMilli(it.startedAtEpochMillis).atZone(zoneId).toLocalDate() == today }
    var current = 0; var date = today
    while (date in completedDates) { current++; date = date.minusDays(1) }
    var best = 0; var run = 0; var previous: LocalDate? = null
    completedDates.sorted().forEach { day -> if (previous == null || day == previous!!.plusDays(1)) run++ else run = 1; best = maxOf(best, run); previous = day }
    return ProgressSummary((todayItems.sumOf { it.actualDurationMillis } / 60_000).toInt(), todayItems.count { it.completed }, current, best)
}
