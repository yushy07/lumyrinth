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
                        "Lumyrinth practice reminders",
                        NotificationManager.IMPORTANCE_DEFAULT,
                    ).apply {
                        description = "Gentle daily prompts for mindful breathing"
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
                        .setContentTitle("Find your rhythm")
                        .setContentText("Take a quiet moment to breathe.")
                        .setContentIntent(pendingIntent)
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
