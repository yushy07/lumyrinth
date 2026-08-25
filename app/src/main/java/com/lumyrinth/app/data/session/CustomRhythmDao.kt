package com.lumyrinth.app.data.session

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomRhythmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(rhythm: CustomRhythmEntity)

    @Query("SELECT * FROM custom_rhythms ORDER BY createdAtEpochMillis DESC")
    fun observeAll(): Flow<List<CustomRhythmEntity>>

    @Query("SELECT * FROM custom_rhythms WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): CustomRhythmEntity?

    @Query("DELETE FROM custom_rhythms WHERE id = :id")
    suspend fun delete(id: String)
}
