package com.lumyrinth.app.data.session

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Insert suspend fun insert(session: SessionEntity): Long
    @Query("SELECT * FROM breathing_sessions ORDER BY startedAtEpochMillis DESC") fun observeAll(): Flow<List<SessionEntity>>
    @Query("SELECT * FROM breathing_sessions WHERE completed = 1 ORDER BY startedAtEpochMillis DESC") fun observeCompleted(): Flow<List<SessionEntity>>
}
