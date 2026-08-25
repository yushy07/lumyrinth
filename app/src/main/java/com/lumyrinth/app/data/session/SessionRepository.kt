package com.lumyrinth.app.data.session

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.flow.Flow

class SessionRepository private constructor(private val database: LumyrinthDatabase) {
    val sessions: Flow<List<SessionEntity>> = database.sessionDao().observeAll()
    val completedSessions: Flow<List<SessionEntity>> = database.sessionDao().observeCompleted()
    val customRhythms: Flow<List<CustomRhythmEntity>> = database.customRhythmDao().observeAll()

    suspend fun saveSession(session: SessionEntity): Long = database.sessionDao().insert(session)
    suspend fun updateMood(sessionId: Long, mood: String) = database.sessionDao().updateMood(sessionId, mood)
    suspend fun getLatestSession(): SessionEntity? = database.sessionDao().getLatestSession()

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
