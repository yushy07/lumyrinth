package com.lumyrinth.app.notifications

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.Configuration
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import java.time.Instant
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ReminderWorkManagerTest {
    private lateinit var context: Context
    private lateinit var workManager: WorkManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        WorkManagerTestInitHelper.initializeTestWorkManager(
            context,
            Configuration.Builder().setExecutor(SynchronousExecutor()).build(),
        )
        workManager = WorkManager.getInstance(context)
    }

    @Test
    fun scheduleReplaceAndDisable_manageOneUniqueReminder() {
        ReminderScheduler.schedule(
            context = context,
            timeString = "20:00",
            now = Instant.parse("2026-08-26T10:00:00Z"),
            zoneId = ZoneId.of("UTC"),
        )
        ReminderScheduler.schedule(
            context = context,
            timeString = "21:00",
            now = Instant.parse("2026-08-26T10:00:00Z"),
            zoneId = ZoneId.of("UTC"),
        )

        val afterReplace = workManager.getWorkInfosForUniqueWork(ReminderScheduler.WORK_NAME)
            .get(5, TimeUnit.SECONDS)
        assertEquals(1, afterReplace.count { !it.state.isFinished })

        ReminderScheduler.disable(context)
        val afterDisable = workManager.getWorkInfosForUniqueWork(ReminderScheduler.WORK_NAME)
            .get(5, TimeUnit.SECONDS)
        assertTrue(afterDisable.all { it.state == WorkInfo.State.CANCELLED })
    }
}
