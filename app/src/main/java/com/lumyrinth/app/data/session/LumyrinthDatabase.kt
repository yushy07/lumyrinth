package com.lumyrinth.app.data.session

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SessionEntity::class, CustomRhythmEntity::class], version = 2, exportSchema = true)
abstract class LumyrinthDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun customRhythmDao(): CustomRhythmDao
}
