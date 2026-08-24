package com.lumyrinth.app.domain

enum class ExerciseCategory { RELAX, FOCUS, SLEEP, RESET, CUSTOM }

enum class BreathPhase(val label: String) {
    INHALE("Inhale"),
    HOLD_AFTER_INHALE("Hold"),
    EXHALE("Exhale"),
    HOLD_AFTER_EXHALE("Hold"),
}

data class BreathingPattern(
    val inhaleSeconds: Int,
    val holdAfterInhaleSeconds: Int = 0,
    val exhaleSeconds: Int,
    val holdAfterExhaleSeconds: Int = 0,
) {
    val cycleMillis: Long get() = listOf(
        inhaleSeconds, holdAfterInhaleSeconds, exhaleSeconds, holdAfterExhaleSeconds,
    ).sum().toLong() * 1_000L

    fun phases() = listOf(
        BreathPhase.INHALE to inhaleSeconds,
        BreathPhase.HOLD_AFTER_INHALE to holdAfterInhaleSeconds,
        BreathPhase.EXHALE to exhaleSeconds,
        BreathPhase.HOLD_AFTER_EXHALE to holdAfterExhaleSeconds,
    ).filter { it.second > 0 }
}

data class BreathingExercise(
    val id: String,
    val name: String,
    val category: ExerciseCategory,
    val description: String,
    val pattern: BreathingPattern,
    val defaultMinutes: Int,
)

object PresetExercises {
    val all = listOf(
        BreathingExercise("slow_down", "Slow Down", ExerciseCategory.RELAX, "A gentle rhythm to ease into calm.", BreathingPattern(4, exhaleSeconds = 6), 3),
        BreathingExercise("equal_rhythm", "Equal Rhythm", ExerciseCategory.FOCUS, "A balanced pace for a clear mind.", BreathingPattern(4, exhaleSeconds = 4), 3),
        BreathingExercise("square", "Square", ExerciseCategory.FOCUS, "A structured rhythm for steady attention.", BreathingPattern(4, 4, 4, 4), 4),
        BreathingExercise("steady", "Steady", ExerciseCategory.FOCUS, "Longer, even pacing for balance.", BreathingPattern(5, exhaleSeconds = 5), 5),
        BreathingExercise("nightfall", "Nightfall", ExerciseCategory.SLEEP, "A slow rhythm for winding down.", BreathingPattern(4, 2, 6), 5),
        BreathingExercise("quick_reset", "Quick Reset", ExerciseCategory.RESET, "A short moment to come back to yourself.", BreathingPattern(3, exhaleSeconds = 4), 1),
    )
}
