package com.lumyrinth.app.domain

import com.lumyrinth.app.data.session.CustomRhythmEntity

enum class BreathPhase(val label: String) {
    INHALE("INHALE"),
    HOLD_AFTER_INHALE("HOLD"),
    EXHALE("EXHALE"),
    HOLD_AFTER_EXHALE("HOLD"),
}

enum class RhythmCategory(val id: String, val title: String) {
    RELAX("relax", "Relax"),
    FOCUS("focus", "Focus"),
    SLEEP("sleep", "Sleep"),
    ENERGY("energy", "Energy"),
    CUSTOM("custom", "My Rhythms");

    companion object {
        fun fromId(id: String): RhythmCategory = entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: RELAX
    }
}

data class Rhythm(
    val id: String,
    val name: String,
    val category: RhythmCategory,
    val isCustom: Boolean = false,
    val isFavorite: Boolean = false,
    val inhaleSeconds: Int,
    val hold1Seconds: Int = 0,
    val exhaleSeconds: Int,
    val hold2Seconds: Int = 0,
    val recommendedDurationOptions: List<Int> = listOf(1, 3, 5, 10),
    val defaultDurationMinutes: Int = 3,
    val shortDescription: String = "",
    val soundDefault: Boolean = true,
    val hapticsDefault: Boolean = true,
    val createdAtEpochMillis: Long = System.currentTimeMillis(),
) {
    val cycleSeconds: Int get() = inhaleSeconds + hold1Seconds + exhaleSeconds + hold2Seconds

    val patternSummary: String
        get() = when {
            hold1Seconds == 0 && hold2Seconds == 0 -> "${inhaleSeconds} sec inhale · ${exhaleSeconds} sec exhale"
            hold2Seconds == 0 -> "${inhaleSeconds}-${hold1Seconds}-${exhaleSeconds} Breathing"
            else -> "${inhaleSeconds}-${hold1Seconds}-${exhaleSeconds}-${hold2Seconds} Breathing"
        }

    val patternCode: String
        get() = when {
            hold1Seconds == 0 && hold2Seconds == 0 -> "${inhaleSeconds}-${exhaleSeconds} Breathing"
            hold2Seconds == 0 -> "${inhaleSeconds}-${hold1Seconds}-${exhaleSeconds} Breathing"
            else -> "${inhaleSeconds}-${hold1Seconds}-${exhaleSeconds}-${hold2Seconds} Breathing"
        }

    val durationRangeText: String
        get() = when {
            recommendedDurationOptions.isEmpty() -> "3 – 10 min"
            recommendedDurationOptions == listOf(1, 2, 3) -> "1 – 3 min"
            recommendedDurationOptions.contains(3) && recommendedDurationOptions.contains(10) -> "3 – 10 min"
            else -> "${recommendedDurationOptions.minOrNull() ?: 1} – ${recommendedDurationOptions.maxOrNull() ?: 10} min"
        }

    fun activePhases(): List<Pair<BreathPhase, Int>> = buildList {
        if (inhaleSeconds > 0) add(BreathPhase.INHALE to inhaleSeconds)
        if (hold1Seconds > 0) add(BreathPhase.HOLD_AFTER_INHALE to hold1Seconds)
        if (exhaleSeconds > 0) add(BreathPhase.EXHALE to exhaleSeconds)
        if (hold2Seconds > 0) add(BreathPhase.HOLD_AFTER_EXHALE to hold2Seconds)
    }

    companion object {
        fun fromCustomEntity(entity: CustomRhythmEntity, isFavorite: Boolean = false): Rhythm {
            return Rhythm(
                id = entity.id,
                name = entity.name,
                category = RhythmCategory.CUSTOM,
                isCustom = true,
                isFavorite = isFavorite,
                inhaleSeconds = entity.inhaleSeconds,
                hold1Seconds = entity.hold1Seconds,
                exhaleSeconds = entity.exhaleSeconds,
                hold2Seconds = entity.hold2Seconds,
                recommendedDurationOptions = listOf(1, 3, 5, 10),
                defaultDurationMinutes = entity.defaultDurationMinutes,
                shortDescription = "Your personal breathing rhythm.",
                soundDefault = entity.soundDefault,
                hapticsDefault = entity.hapticsDefault,
                createdAtEpochMillis = entity.createdAtEpochMillis,
            )
        }
    }
}

object PresetRhythms {
    val slowDown = Rhythm(
        id = "slow_down",
        name = "Slow Down",
        category = RhythmCategory.RELAX,
        inhaleSeconds = 4,
        hold1Seconds = 0,
        exhaleSeconds = 6,
        hold2Seconds = 0,
        recommendedDurationOptions = listOf(1, 3, 5, 10),
        defaultDurationMinutes = 3,
        shortDescription = "A gentle rhythm to ease into calm.",
    )

    val equalRhythm = Rhythm(
        id = "equal_rhythm",
        name = "Equal Rhythm",
        category = RhythmCategory.RELAX,
        inhaleSeconds = 4,
        hold1Seconds = 0,
        exhaleSeconds = 4,
        hold2Seconds = 0,
        recommendedDurationOptions = listOf(1, 3, 5, 10),
        defaultDurationMinutes = 3,
        shortDescription = "A balanced pace for a clear mind.",
    )

    val square = Rhythm(
        id = "square",
        name = "Box Breathing",
        category = RhythmCategory.RELAX,
        inhaleSeconds = 4,
        hold1Seconds = 4,
        exhaleSeconds = 4,
        hold2Seconds = 4,
        recommendedDurationOptions = listOf(1, 3, 5, 10),
        defaultDurationMinutes = 4,
        shortDescription = "A balanced 4-4-4-4 technique for focus and steady composure.",
    )

    val steady = Rhythm(
        id = "steady",
        name = "Steady",
        category = RhythmCategory.FOCUS,
        inhaleSeconds = 5,
        hold1Seconds = 0,
        exhaleSeconds = 5,
        hold2Seconds = 0,
        recommendedDurationOptions = listOf(1, 3, 5, 10),
        defaultDurationMinutes = 5,
        shortDescription = "Longer, even pacing for balance.",
    )

    val quickReset = Rhythm(
        id = "quick_reset",
        name = "Quick Reset",
        category = RhythmCategory.FOCUS,
        inhaleSeconds = 4,
        hold1Seconds = 0,
        exhaleSeconds = 4,
        hold2Seconds = 0,
        recommendedDurationOptions = listOf(1, 2, 3),
        defaultDurationMinutes = 2,
        shortDescription = "A short moment to come back to yourself.",
    )

    val nightfall = Rhythm(
        id = "nightfall",
        name = "Nightfall",
        category = RhythmCategory.SLEEP,
        inhaleSeconds = 4,
        hold1Seconds = 2,
        exhaleSeconds = 6,
        hold2Seconds = 0,
        recommendedDurationOptions = listOf(1, 3, 5, 10),
        defaultDurationMinutes = 5,
        shortDescription = "A slow breathing rhythm designed for winding down.",
    )

    val deepRest = Rhythm(
        id = "deep_rest",
        name = "4-7-8 Technique",
        category = RhythmCategory.SLEEP,
        inhaleSeconds = 4,
        hold1Seconds = 7,
        exhaleSeconds = 8,
        hold2Seconds = 0,
        recommendedDurationOptions = listOf(1, 3, 5, 10),
        defaultDurationMinutes = 5,
        shortDescription = "A classic 4-7-8 calming pattern for deep relaxation and sleep.",
    )

    val awaken = Rhythm(
        id = "awaken",
        name = "Awaken",
        category = RhythmCategory.ENERGY,
        inhaleSeconds = 4,
        hold1Seconds = 2,
        exhaleSeconds = 4,
        hold2Seconds = 2,
        recommendedDurationOptions = listOf(1, 2, 3, 5),
        defaultDurationMinutes = 3,
        shortDescription = "Invigorating rhythmic breathing to restore alertness.",
    )

    val energyFlow = Rhythm(
        id = "energy_flow",
        name = "Energy Flow",
        category = RhythmCategory.ENERGY,
        inhaleSeconds = 6,
        hold1Seconds = 0,
        exhaleSeconds = 2,
        hold2Seconds = 0,
        recommendedDurationOptions = listOf(1, 2, 3, 5),
        defaultDurationMinutes = 3,
        shortDescription = "Fast, rhythmic breathing to boost mental energy.",
    )

    val all: List<Rhythm> = listOf(
        slowDown,
        equalRhythm,
        square,
        steady,
        quickReset,
        nightfall,
        deepRest,
        awaken,
        energyFlow,
    )

    fun getById(id: String): Rhythm? = all.firstOrNull { it.id == id }
}
