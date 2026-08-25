package com.lumyrinth.app.data.session

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "breathing_sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rhythmId: String,
    val rhythmNameSnapshot: String,
    val dateIso: String,
    val startedAtEpochMillis: Long,
    val completedNaturally: Boolean,
    val durationMinutesPlanned: Int,
    val durationMinutesActual: Int,
    val cyclesCompleted: Int,
    val soundOn: Boolean,
    val hapticsOn: Boolean,
    val mood: String? = null,
)
