package com.lumyrinth.app.data.session

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: SessionEntity): Long

    @Query("UPDATE breathing_sessions SET mood = :mood WHERE id = :sessionId")
    suspend fun updateMood(sessionId: Long, mood: String)

    @Query("SELECT * FROM breathing_sessions ORDER BY startedAtEpochMillis DESC")
    fun observeAll(): Flow<List<SessionEntity>>

    @Query("SELECT * FROM breathing_sessions WHERE completedNaturally = 1 OR durationMinutesActual > 0 ORDER BY startedAtEpochMillis DESC")
    fun observeCompleted(): Flow<List<SessionEntity>>

    @Query("SELECT * FROM breathing_sessions ORDER BY startedAtEpochMillis DESC LIMIT 1")
    suspend fun getLatestSession(): SessionEntity?
}
