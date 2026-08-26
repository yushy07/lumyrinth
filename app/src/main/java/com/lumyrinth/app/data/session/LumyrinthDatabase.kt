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
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE `breathing_sessions_new` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `rhythmId` TEXT NOT NULL,
                        `rhythmNameSnapshot` TEXT NOT NULL,
                        `dateIso` TEXT NOT NULL,
                        `startedAtEpochMillis` INTEGER NOT NULL,
                        `completedNaturally` INTEGER NOT NULL,
                        `durationMinutesPlanned` INTEGER NOT NULL,
                        `durationMinutesActual` INTEGER NOT NULL,
                        `cyclesCompleted` INTEGER NOT NULL,
                        `soundOn` INTEGER NOT NULL,
                        `hapticsOn` INTEGER NOT NULL,
                        `mood` TEXT
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO `breathing_sessions_new` (
                        `id`, `rhythmId`, `rhythmNameSnapshot`, `dateIso`, `startedAtEpochMillis`,
                        `completedNaturally`, `durationMinutesPlanned`, `durationMinutesActual`,
                        `cyclesCompleted`, `soundOn`, `hapticsOn`, `mood`
                    )
                    SELECT `id`, `exerciseId`, `exerciseName`,
                        date(`startedAtEpochMillis` / 1000, 'unixepoch'), `startedAtEpochMillis`,
                        `completed`, CAST(`targetDurationMillis` / 60000 AS INTEGER),
                        CAST(`actualDurationMillis` / 60000 AS INTEGER), `cyclesCompleted`, 1, 1, `feeling`
                    FROM `breathing_sessions`
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE `breathing_sessions`")
                db.execSQL("ALTER TABLE `breathing_sessions_new` RENAME TO `breathing_sessions`")

                db.execSQL(
                    """
                    CREATE TABLE `custom_rhythms_new` (
                        `id` TEXT NOT NULL,
                        `name` TEXT NOT NULL,
                        `inhaleSeconds` INTEGER NOT NULL,
                        `hold1Seconds` INTEGER NOT NULL,
                        `exhaleSeconds` INTEGER NOT NULL,
                        `hold2Seconds` INTEGER NOT NULL,
                        `defaultDurationMinutes` INTEGER NOT NULL,
                        `soundDefault` INTEGER NOT NULL,
                        `hapticsDefault` INTEGER NOT NULL,
                        `createdAtEpochMillis` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO `custom_rhythms_new` (
                        `id`, `name`, `inhaleSeconds`, `hold1Seconds`, `exhaleSeconds`, `hold2Seconds`,
                        `defaultDurationMinutes`, `soundDefault`, `hapticsDefault`, `createdAtEpochMillis`
                    )
                    SELECT `id`, `name`, `inhaleSeconds`, `holdAfterInhaleSeconds`, `exhaleSeconds`,
                        `holdAfterExhaleSeconds`, 3, 1, 1, `createdAtEpochMillis`
                    FROM `custom_rhythms`
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE `custom_rhythms`")
                db.execSQL("ALTER TABLE `custom_rhythms_new` RENAME TO `custom_rhythms`")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE `breathing_sessions` ADD COLUMN `durationSecondsActual` " +
                        "INTEGER NOT NULL DEFAULT 0"
                )
                db.execSQL(
                    "UPDATE `breathing_sessions` SET `durationSecondsActual` = `durationMinutesActual` * 60"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_breathing_sessions_startedAtEpochMillis` ON `breathing_sessions` (`startedAtEpochMillis`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_breathing_sessions_dateIso` ON `breathing_sessions` (`dateIso`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_breathing_sessions_rhythmId` ON `breathing_sessions` (`rhythmId`)")
            }
        }

        val ALL_MIGRATIONS = arrayOf(MIGRATION_2_3, MIGRATION_3_4)
    }
}
