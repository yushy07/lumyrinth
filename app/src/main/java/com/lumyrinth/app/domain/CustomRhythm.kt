package com.lumyrinth.app.domain

/** V1-safe timing bounds for a user-created rhythm. */
data class CustomRhythmDraft(
    val name: String = "My Rhythm",
    val inhaleSeconds: Int = 4,
    val holdAfterInhaleSeconds: Int = 0,
    val exhaleSeconds: Int = 6,
    val holdAfterExhaleSeconds: Int = 0,
) {
    fun pattern() = BreathingPattern(
        inhaleSeconds = inhaleSeconds.coerceIn(2, 8),
        holdAfterInhaleSeconds = holdAfterInhaleSeconds.coerceIn(0, 4),
        exhaleSeconds = exhaleSeconds.coerceIn(2, 8),
        holdAfterExhaleSeconds = holdAfterExhaleSeconds.coerceIn(0, 4),
    )

    fun asExercise() = BreathingExercise(
        id = "custom_${name.trim().lowercase().replace(" ", "_")}",
        name = name.trim().take(24).ifBlank { "My Rhythm" },
        category = ExerciseCategory.CUSTOM,
        description = "Your personal breathing rhythm.",
        pattern = pattern(),
        defaultMinutes = 3,
    )
}
