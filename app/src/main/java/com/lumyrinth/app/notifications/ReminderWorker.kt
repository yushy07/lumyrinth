package com.lumyrinth.app.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.lumyrinth.app.MainActivity
import com.lumyrinth.app.R
import com.lumyrinth.app.data.UserPreferencesRepository
import kotlinx.coroutines.flow.first

class ReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    return Result.success()
                }
            }
            val manager = applicationContext.getSystemService(NotificationManager::class.java)
            if (manager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        CHANNEL_ID,
                        applicationContext.getString(R.string.reminder_channel_name),
                        NotificationManager.IMPORTANCE_DEFAULT,
                    ).apply {
                        description = applicationContext.getString(R.string.reminder_channel_description)
                    }
                    manager.createNotificationChannel(channel)
                }

                val intent = Intent(applicationContext, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                val pendingIntent = PendingIntent.getActivity(
                    applicationContext,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                )

                manager.notify(
                    REMINDER_ID,
                    NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                        .setSmallIcon(R.drawable.lumyrinth_mark)
                        .setContentTitle(applicationContext.getString(R.string.reminder_title))
                        .setContentText(applicationContext.getString(R.string.reminder_body))
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .build()
                )
            }
            rescheduleIfEnabled()
            Result.success()
        } catch (e: Throwable) {
            Log.w("ReminderWorker", "Failed to post reminder notification", e)
            Result.success()
        }
    }

    private suspend fun rescheduleIfEnabled() {
        val preferences = UserPreferencesRepository(applicationContext).preferences.first()
        if (preferences.dailyReminderEnabled) {
            ReminderScheduler.schedule(applicationContext, preferences.dailyReminderTime)
        }
    }
    companion object { const val CHANNEL_ID = "practice_reminders"; const val REMINDER_ID = 101 }
}
