package com.lumyrinth.app.data.session

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [SessionEntity::class, CustomRhythmEntity::class], version = 4, exportSchema = true)
abstract class LumyrinthDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun customRhythmDao(): CustomRhythmDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE breathing_sessions ADD COLUMN durationSecondsActual INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE breathing_sessions ADD COLUMN mood TEXT DEFAULT NULL")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `custom_rhythms` (
                        `id` TEXT NOT NULL,
                        `name` TEXT NOT NULL,
                        `inhaleSeconds` INTEGER NOT NULL,
                        `hold1Seconds` INTEGER NOT NULL,
                        `exhaleSeconds` INTEGER NOT NULL,
                        `hold2Seconds` INTEGER NOT NULL,
                        `defaultDurationMinutes` INTEGER NOT NULL,
                        `soundDefault` INTEGER NOT NULL DEFAULT 1,
                        `hapticsDefault` INTEGER NOT NULL DEFAULT 1,
                        `createdAtEpochMillis` INTEGER NOT NULL DEFAULT 0,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
            }
        }

        val ALL_MIGRATIONS = arrayOf(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
    }
}
