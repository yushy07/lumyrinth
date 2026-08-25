package com.lumyrinth.app.data.session

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_rhythms")
data class CustomRhythmEntity(
    @PrimaryKey val id: String,
    val name: String,
    val inhaleSeconds: Int,
    val hold1Seconds: Int,
    val exhaleSeconds: Int,
    val hold2Seconds: Int,
    val defaultDurationMinutes: Int,
    val soundDefault: Boolean = true,
    val hapticsDefault: Boolean = true,
    val createdAtEpochMillis: Long = System.currentTimeMillis(),
)
