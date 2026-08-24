package com.lumyrinth.app.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.lumyrinth.app.R

class ReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val manager = applicationContext.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(NotificationChannel(CHANNEL_ID, "Lumyrinth reminders", NotificationManager.IMPORTANCE_LOW))
        manager.notify(REMINDER_ID, NotificationCompat.Builder(applicationContext, CHANNEL_ID).setSmallIcon(android.R.drawable.ic_popup_reminder).setContentTitle("Find your rhythm").setContentText("Take a quiet moment to breathe.").setAutoCancel(true).build())
        return Result.success()
    }
    companion object { const val CHANNEL_ID = "practice_reminders"; const val REMINDER_ID = 101 }
}
