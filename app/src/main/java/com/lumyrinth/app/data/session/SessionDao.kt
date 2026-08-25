package com.lumyrinth.app.data.session

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: SessionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sessions: List<SessionEntity>)

    @Update
    suspend fun update(session: SessionEntity)

    @Query("UPDATE breathing_sessions SET mood = :mood WHERE id = :sessionId")
    suspend fun updateMood(sessionId: Long, mood: String)

    @Query("SELECT * FROM breathing_sessions ORDER BY startedAtEpochMillis DESC")
    fun observeAll(): Flow<List<SessionEntity>>

    @Query("SELECT * FROM breathing_sessions WHERE completedNaturally = 1 OR durationMinutesActual > 0 ORDER BY startedAtEpochMillis DESC")
    fun observeCompleted(): Flow<List<SessionEntity>>

    @Query("SELECT * FROM breathing_sessions ORDER BY startedAtEpochMillis DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<SessionEntity>>

    @Query("SELECT * FROM breathing_sessions WHERE startedAtEpochMillis >= :startEpochMillis AND startedAtEpochMillis <= :endEpochMillis ORDER BY startedAtEpochMillis DESC")
    fun observeByTimeRange(startEpochMillis: Long, endEpochMillis: Long): Flow<List<SessionEntity>>

    @Query("SELECT * FROM breathing_sessions WHERE dateIso = :dateIso ORDER BY startedAtEpochMillis DESC")
    fun observeByDate(dateIso: String): Flow<List<SessionEntity>>

    @Query("SELECT * FROM breathing_sessions WHERE dateIso >= :startDateIso AND dateIso <= :endDateIso ORDER BY startedAtEpochMillis DESC")
    fun observeByDateRange(startDateIso: String, endDateIso: String): Flow<List<SessionEntity>>

    @Query("SELECT COUNT(*) FROM breathing_sessions")
    fun observeTotalSessionCount(): Flow<Int>

    @Query("SELECT COALESCE(SUM(durationMinutesActual), 0) FROM breathing_sessions")
    fun observeTotalDurationMinutes(): Flow<Int>

    @Query("SELECT COALESCE(AVG(durationMinutesActual), 0.0) FROM breathing_sessions")
    fun observeAverageDurationMinutes(): Flow<Double>

    @Query("SELECT dateIso, COUNT(*) AS sessionCount, COALESCE(SUM(durationMinutesActual), 0) AS totalMinutes FROM breathing_sessions GROUP BY dateIso ORDER BY dateIso DESC")
    fun observeDailyFrequency(): Flow<List<DailyFrequencyStat>>

    @Query("SELECT rhythmId, rhythmNameSnapshot, COUNT(*) AS sessionCount, COALESCE(SUM(durationMinutesActual), 0) AS totalMinutes FROM breathing_sessions GROUP BY rhythmId ORDER BY sessionCount DESC")
    fun observeRhythmFrequency(): Flow<List<RhythmFrequencyStat>>

    @Query("SELECT * FROM breathing_sessions ORDER BY startedAtEpochMillis DESC LIMIT 1")
    suspend fun getLatestSession(): SessionEntity?

    @Query("SELECT * FROM breathing_sessions WHERE id = :id LIMIT 1")
    suspend fun getSessionById(id: Long): SessionEntity?

    @Query("DELETE FROM breathing_sessions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM breathing_sessions")
    suspend fun clearAllSessions()
}

