package com.lumyrinth.app.data.session

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "breathing_sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val exerciseId: String,
    val exerciseName: String,
    val startedAtEpochMillis: Long,
    val completedAtEpochMillis: Long,
    val targetDurationMillis: Long,
    val actualDurationMillis: Long,
    val cyclesCompleted: Int,
    val completed: Boolean,
    val feeling: String? = null,
)
