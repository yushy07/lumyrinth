package com.lumyrinth.app.data.session

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "breathing_sessions",
    indices = [
        Index(value = ["startedAtEpochMillis"]),
        Index(value = ["dateIso"]),
        Index(value = ["rhythmId"]),
    ]
)
data class SessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rhythmId: String,
    val rhythmNameSnapshot: String,
    val dateIso: String,
    val startedAtEpochMillis: Long,
    val completedNaturally: Boolean,
    val durationMinutesPlanned: Int,
    val durationMinutesActual: Int,
    @ColumnInfo(defaultValue = "0") val durationSecondsActual: Int = 0,
    val cyclesCompleted: Int,
    val soundOn: Boolean,
    val hapticsOn: Boolean,
    val mood: String? = null,
)

/**
 * Aggregated daily meditation frequency and total duration.
 */
data class DailyFrequencyStat(
    val dateIso: String,
    @ColumnInfo(name = "sessionCount") val sessionCount: Int,
    @ColumnInfo(name = "totalMinutes") val totalMinutes: Int,
)

/**
 * Aggregated frequency and duration per breathing rhythm preset.
 */
data class RhythmFrequencyStat(
    val rhythmId: String,
    val rhythmNameSnapshot: String,
    @ColumnInfo(name = "sessionCount") val sessionCount: Int,
    @ColumnInfo(name = "totalMinutes") val totalMinutes: Int,
)

