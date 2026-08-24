package com.lumyrinth.app.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object ReminderScheduler {
    fun enableDaily(context: Context) = WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "lumyrinth_daily_reminder", ExistingPeriodicWorkPolicy.UPDATE,
        PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS).build(),
    )
    fun disable(context: Context) = WorkManager.getInstance(context).cancelUniqueWork("lumyrinth_daily_reminder")
}
