package com.lumyrinth.app.data.session

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_rhythms")
data class CustomRhythmEntity(
    @PrimaryKey val id: String,
    val name: String,
    val inhaleSeconds: Int,
    val holdAfterInhaleSeconds: Int,
    val exhaleSeconds: Int,
    val holdAfterExhaleSeconds: Int,
    val createdAtEpochMillis: Long,
)
