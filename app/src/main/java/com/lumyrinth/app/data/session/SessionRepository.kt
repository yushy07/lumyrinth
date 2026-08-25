package com.lumyrinth.app.data.session

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.flow.Flow

class SessionRepository private constructor(private val database: LumyrinthDatabase) {
    val sessions: Flow<List<SessionEntity>> = database.sessionDao().observeAll()
    val completedSessions: Flow<List<SessionEntity>> = database.sessionDao().observeCompleted()
    val totalMindfulMinutes: Flow<Int> = database.sessionDao().observeTotalDurationMinutes()
    val totalSessionCount: Flow<Int> = database.sessionDao().observeTotalSessionCount()
    val averageDurationMinutes: Flow<Double> = database.sessionDao().observeAverageDurationMinutes()
    val dailyFrequency: Flow<List<DailyFrequencyStat>> = database.sessionDao().observeDailyFrequency()
    val rhythmFrequency: Flow<List<RhythmFrequencyStat>> = database.sessionDao().observeRhythmFrequency()
    val customRhythms: Flow<List<CustomRhythmEntity>> = database.customRhythmDao().observeAll()

    fun observeRecentSessions(limit: Int = 10): Flow<List<SessionEntity>> =
        database.sessionDao().observeRecent(limit)

    fun observeByDateRange(startDateIso: String, endDateIso: String): Flow<List<SessionEntity>> =
        database.sessionDao().observeByDateRange(startDateIso, endDateIso)

    fun observeByTimeRange(startEpochMillis: Long, endEpochMillis: Long): Flow<List<SessionEntity>> =
        database.sessionDao().observeByTimeRange(startEpochMillis, endEpochMillis)

    suspend fun saveSession(session: SessionEntity): Long = database.sessionDao().insert(session)
    suspend fun updateMood(sessionId: Long, mood: String) = database.sessionDao().updateMood(sessionId, mood)
    suspend fun getLatestSession(): SessionEntity? = database.sessionDao().getLatestSession()
    suspend fun getSessionById(id: Long): SessionEntity? = database.sessionDao().getSessionById(id)
    suspend fun deleteSession(id: Long) = database.sessionDao().deleteById(id)
    suspend fun clearAllSessions() = database.sessionDao().clearAllSessions()
    suspend fun clearAllData() {
        database.sessionDao().clearAllSessions()
        database.customRhythmDao().clearAll()
    }

    suspend fun saveCustomRhythm(rhythm: CustomRhythmEntity) = database.customRhythmDao().save(rhythm)
    suspend fun deleteCustomRhythm(id: String) = database.customRhythmDao().delete(id)
    suspend fun getCustomRhythm(id: String): CustomRhythmEntity? = database.customRhythmDao().getById(id)

    companion object {
        @Volatile
        private var instance: SessionRepository? = null

        fun from(context: Context): SessionRepository = instance ?: synchronized(this) {
            instance ?: SessionRepository(
                Room.databaseBuilder(
                    context.applicationContext,
                    LumyrinthDatabase::class.java,
                    "lumyrinth.db"
                ).fallbackToDestructiveMigration().build()
            ).also { instance = it }
        }
    }
}

