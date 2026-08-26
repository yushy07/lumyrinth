package com.lumyrinth.app.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.lumyrinth.app.data.session.CustomRhythmEntity
import com.lumyrinth.app.data.session.LumyrinthDatabase
import com.lumyrinth.app.data.session.SessionEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class RoomDaoTest {

    private lateinit var database: LumyrinthDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, LumyrinthDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun sessionDao_insertAndObserve_returnsInsertedSession() = runBlocking {
        val session = SessionEntity(
            id = 1,
            rhythmId = "square",
            rhythmNameSnapshot = "Box Breathing",
            dateIso = "2026-08-25",
            startedAtEpochMillis = System.currentTimeMillis(),
            completedNaturally = true,
            durationMinutesPlanned = 5,
            durationMinutesActual = 5,
            durationSecondsActual = 300,
            cyclesCompleted = 10,
            soundOn = true,
            hapticsOn = true
        )

        database.sessionDao().insert(session)

        val sessions = database.sessionDao().observeAll().first()
        assertEquals(1, sessions.size)
        assertEquals("Box Breathing", sessions[0].rhythmNameSnapshot)
        assertEquals(300, sessions[0].durationSecondsActual)
    }

    @Test
    fun sessionDao_exactSeconds_driveAggregatesWithoutRoundingEachSession() = runBlocking {
        val base = SessionEntity(
            rhythmId = "slow_down",
            rhythmNameSnapshot = "Slow Down",
            dateIso = "2026-08-26",
            startedAtEpochMillis = 1L,
            completedNaturally = false,
            durationMinutesPlanned = 5,
            durationMinutesActual = 0,
            durationSecondsActual = 35,
            cyclesCompleted = 1,
            soundOn = false,
            hapticsOn = false,
        )
        database.sessionDao().insert(base)
        database.sessionDao().insert(base.copy(id = 0, startedAtEpochMillis = 2L, durationSecondsActual = 40))

        assertEquals(1, database.sessionDao().observeTotalDurationMinutes().first())
        assertEquals(2, database.sessionDao().observeCompleted().first().size)
        assertEquals(0.625, database.sessionDao().observeAverageDurationMinutes().first(), 0.001)
    }

    @Test
    fun customRhythmDao_saveAndGetById_returnsRhythm() = runBlocking {
        val rhythm = CustomRhythmEntity(
            id = "custom_1",
            name = "Deep Calming 4-7-8",
            inhaleSeconds = 4,
            hold1Seconds = 7,
            exhaleSeconds = 8,
            hold2Seconds = 0,
            defaultDurationMinutes = 5,
            soundDefault = true,
            hapticsDefault = true,
            createdAtEpochMillis = System.currentTimeMillis()
        )

        database.customRhythmDao().save(rhythm)

        val retrieved = database.customRhythmDao().getById("custom_1")
        assertNotNull(retrieved)
        assertEquals("Deep Calming 4-7-8", retrieved?.name)
        assertEquals(4, retrieved?.inhaleSeconds)
        assertEquals(7, retrieved?.hold1Seconds)

        database.customRhythmDao().delete("custom_1")
        val deleted = database.customRhythmDao().getById("custom_1")
        assertNull(deleted)
    }
}
