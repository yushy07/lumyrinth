package com.lumyrinth.app.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.lumyrinth.app.data.session.LumyrinthDatabase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class MigrationTest {
    private lateinit var context: Context
    private val databaseName = "migration-test.db"

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        context.deleteDatabase(databaseName)
    }

    @After
    fun tearDown() {
        context.deleteDatabase(databaseName)
    }

    @Test
    fun migrationFromVersionTwoPreservesSessionsAndCustomRhythms() = runBlocking {
        val file = context.getDatabasePath(databaseName)
        file.parentFile?.mkdirs()
        SQLiteDatabase.openOrCreateDatabase(file, null).use { db ->
            db.execSQL("CREATE TABLE breathing_sessions (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, exerciseId TEXT NOT NULL, exerciseName TEXT NOT NULL, startedAtEpochMillis INTEGER NOT NULL, completedAtEpochMillis INTEGER NOT NULL, targetDurationMillis INTEGER NOT NULL, actualDurationMillis INTEGER NOT NULL, cyclesCompleted INTEGER NOT NULL, completed INTEGER NOT NULL, feeling TEXT)")
            db.execSQL("CREATE TABLE custom_rhythms (id TEXT NOT NULL PRIMARY KEY, name TEXT NOT NULL, inhaleSeconds INTEGER NOT NULL, holdAfterInhaleSeconds INTEGER NOT NULL, exhaleSeconds INTEGER NOT NULL, holdAfterExhaleSeconds INTEGER NOT NULL, createdAtEpochMillis INTEGER NOT NULL)")
            db.execSQL("INSERT INTO breathing_sessions VALUES (1, 'slow_down', 'Slow Down', 1724630400000, 1724630490000, 180000, 90000, 9, 1, 'better')")
            db.execSQL("INSERT INTO custom_rhythms VALUES ('custom-1', 'My Calm', 4, 2, 6, 0, 1724630400000)")
            db.version = 2
        }

        val database = Room.databaseBuilder(context, LumyrinthDatabase::class.java, databaseName)
            .addMigrations(*LumyrinthDatabase.ALL_MIGRATIONS)
            .allowMainThreadQueries()
            .build()

        val session = database.sessionDao().getSessionById(1)!!
        assertEquals("slow_down", session.rhythmId)
        assertEquals(60, session.durationSecondsActual)
        assertEquals("better", session.mood)
        val rhythm = database.customRhythmDao().getById("custom-1")!!
        assertEquals(2, rhythm.hold1Seconds)
        assertEquals(3, rhythm.defaultDurationMinutes)
        assertTrue(rhythm.soundDefault)
        database.close()
    }
}
