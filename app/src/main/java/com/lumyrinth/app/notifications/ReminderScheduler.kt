package com.lumyrinth.app.notifications

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

object ReminderScheduler {
    fun schedule(
        context: Context,
        timeString: String = DEFAULT_TIME,
        now: Instant = Instant.now(),
        zoneId: ZoneId = ZoneId.systemDefault(),
    ) {
        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(nextDelay(now, parseTime(timeString), zoneId).toMillis(), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            workRequest,
        )
    }

    fun disable(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    internal fun parseTime(value: String): LocalTime =
        runCatching { LocalTime.parse(value) }.getOrDefault(LocalTime.of(20, 0))

    internal fun nextDelay(now: Instant, targetTime: LocalTime, zoneId: ZoneId): Duration {
        val zonedNow = now.atZone(zoneId)
        var next = zonedNow.toLocalDate().atTime(targetTime).atZone(zoneId)
        if (!next.toInstant().isAfter(now)) next = next.plusDays(1)
        return Duration.between(now, next.toInstant()).coerceAtLeast(Duration.ofSeconds(1))
    }

    const val WORK_NAME = "lumyrinth_daily_reminder"
    const val DEFAULT_TIME = "20:00"
}
