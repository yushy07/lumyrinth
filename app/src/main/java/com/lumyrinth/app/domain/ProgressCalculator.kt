package com.lumyrinth.app.domain

import com.lumyrinth.app.data.session.SessionEntity
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

data class DayMinuteStat(
    val dayOfWeek: DayOfWeek,
    val dayLabel: String, // "M", "T", "W", "T", "F", "S", "S"
    val date: LocalDate,
    val minutes: Int,
    val isToday: Boolean,
)

data class ProgressSummary(
    val todaysSessionCount: Int,
    val todaysMindfulMinutes: Int,
    val currentStreakDays: Int,
    val thisWeekMinutes: Int,
    val totalSessionsCount: Int,
    val weeklyChart: List<DayMinuteStat>,
    val activeDates: Set<LocalDate>,
    val streakDates: Set<LocalDate>,
    val latestSession: SessionEntity?,
)

object ProgressCalculator {

    fun compute(
        sessions: List<SessionEntity>,
        zoneId: ZoneId = ZoneId.systemDefault(),
        today: LocalDate = LocalDate.now(zoneId),
    ): ProgressSummary {
        // Map session to local date and duration
        val sessionDates = sessions.map { session ->
            val date = try {
                Instant.ofEpochMilli(session.startedAtEpochMillis).atZone(zoneId).toLocalDate()
            } catch (e: Exception) {
                today
            }
            date to session
        }

        val activeDates = sessionDates.map { it.first }.toSet()

        // Today's stats
        val todaySessions = sessionDates.filter { it.first == today }.map { it.second }
        val todaysSessionCount = todaySessions.size
        val todaysMindfulMinutes = todaySessions.sumOf { it.durationMinutesActual }

        // Total sessions
        val totalSessionsCount = sessions.size

        // Streak calculation
        val currentStreakDays = calculateStreak(activeDates, today)
        val streakDates = calculateStreakDates(activeDates)

        // Weekly chart calculation (Monday through Sunday)
        val mondayOfThisWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val weeklyChart = (0..6).map { dayOffset ->
            val date = mondayOfThisWeek.plusDays(dayOffset.toLong())
            val daySessions = sessionDates.filter { it.first == date }.map { it.second }
            val minutes = daySessions.sumOf { it.durationMinutesActual }
            val label = when (date.dayOfWeek) {
                DayOfWeek.MONDAY -> "M"
                DayOfWeek.TUESDAY -> "T"
                DayOfWeek.WEDNESDAY -> "W"
                DayOfWeek.THURSDAY -> "T"
                DayOfWeek.FRIDAY -> "F"
                DayOfWeek.SATURDAY -> "S"
                DayOfWeek.SUNDAY -> "S"
            }
            DayMinuteStat(
                dayOfWeek = date.dayOfWeek,
                dayLabel = label,
                date = date,
                minutes = minutes,
                isToday = (date == today),
            )
        }

        val thisWeekMinutes = weeklyChart.sumOf { it.minutes }
        val latestSession = sessions.maxByOrNull { it.startedAtEpochMillis }

        return ProgressSummary(
            todaysSessionCount = todaysSessionCount,
            todaysMindfulMinutes = todaysMindfulMinutes,
            currentStreakDays = currentStreakDays,
            thisWeekMinutes = thisWeekMinutes,
            totalSessionsCount = totalSessionsCount,
            weeklyChart = weeklyChart,
            activeDates = activeDates,
            streakDates = streakDates,
            latestSession = latestSession,
        )
    }

    private fun calculateStreak(activeDates: Set<LocalDate>, today: LocalDate): Int {
        if (activeDates.isEmpty()) return 0
        var streak = 0
        var checkDate = if (today in activeDates) today else today.minusDays(1)
        if (checkDate !in activeDates) return 0

        while (checkDate in activeDates) {
            streak++
            checkDate = checkDate.minusDays(1)
        }
        return streak
    }

    private fun calculateStreakDates(activeDates: Set<LocalDate>): Set<LocalDate> {
        val streakDates = mutableSetOf<LocalDate>()
        for (date in activeDates) {
            val hasPrev = date.minusDays(1) in activeDates
            val hasNext = date.plusDays(1) in activeDates
            if (hasPrev || hasNext) {
                streakDates.add(date)
            }
        }
        return streakDates
    }
}
