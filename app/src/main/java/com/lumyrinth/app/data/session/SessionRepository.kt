package com.lumyrinth.app.data.session

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.flow.Flow

class SessionRepository private constructor(private val database: LumyrinthDatabase) {
    val sessions: Flow<List<SessionEntity>> = database.sessionDao().observeAll()
    val completedSessions: Flow<List<SessionEntity>> = database.sessionDao().observeCompleted()
    val customRhythms: Flow<List<CustomRhythmEntity>> = database.customRhythmDao().observeAll()
    suspend fun save(session: SessionEntity) = database.sessionDao().insert(session)
    suspend fun saveCustomRhythm(rhythm: CustomRhythmEntity) = database.customRhythmDao().save(rhythm)
    suspend fun deleteCustomRhythm(id: String) = database.customRhythmDao().delete(id)

    companion object {
        @Volatile private var instance: SessionRepository? = null
        fun from(context: Context): SessionRepository = instance ?: synchronized(this) {
            instance ?: SessionRepository(Room.databaseBuilder(context.applicationContext, LumyrinthDatabase::class.java, "lumyrinth.db").fallbackToDestructiveMigration(false).build()).also { instance = it }
        }
    }
}
