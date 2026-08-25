package com.lumyrinth.app.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

object ReminderScheduler {
    fun schedule(context: Context, timeString: String = "20:00") {
        val targetTime = try {
            LocalTime.parse(timeString)
        } catch (e: Exception) {
            LocalTime.of(20, 0)
        }

        val now = LocalDateTime.now()
        var nextRun = now.with(targetTime)
        if (now.isAfter(nextRun)) {
            nextRun = nextRun.plusDays(1)
        }

        val initialDelayMinutes = Duration.between(now, nextRun).toMinutes().coerceAtLeast(1)

        val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelayMinutes, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest,
        )
    }

    fun disable(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    const val WORK_NAME = "lumyrinth_daily_reminder"
}
