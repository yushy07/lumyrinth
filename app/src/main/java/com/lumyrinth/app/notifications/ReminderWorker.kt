package com.lumyrinth.app.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

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
                    manager.createNotificationChannel(NotificationChannel(CHANNEL_ID, "Lumyrinth reminders", NotificationManager.IMPORTANCE_LOW))
                }
                manager.notify(
                    REMINDER_ID,
                    NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_popup_reminder)
                        .setContentTitle("Find your rhythm")
                        .setContentText("Take a quiet moment to breathe.")
                        .setAutoCancel(true)
                        .build()
                )
            }
            Result.success()
        } catch (e: Throwable) {
            Log.w("ReminderWorker", "Failed to post reminder notification", e)
            Result.success()
        }
    }
    companion object { const val CHANNEL_ID = "practice_reminders"; const val REMINDER_ID = 101 }
}
