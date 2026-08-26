package com.lumyrinth.app.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.lumyrinth.app.data.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ReminderRescheduleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action !in SUPPORTED_ACTIONS) return
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val preferences = UserPreferencesRepository(context.applicationContext).preferences.first()
                if (preferences.dailyReminderEnabled) {
                    ReminderScheduler.schedule(context.applicationContext, preferences.dailyReminderTime)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private companion object {
        val SUPPORTED_ACTIONS = setOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
        )
    }
}
