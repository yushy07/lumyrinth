package com.lumyrinth.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.lumyrinth.app.ui.theme.LumyrinthThemeTokens

enum class SectionTheme {
    HOME,
    EXPLORE,
    PROGRESS,
    SETTINGS,
    SESSION,
    DETAIL,
    ONBOARDING,
    LEGAL,
}

/** A quiet tonal backdrop shared by every product section. */
@Composable
fun CosmicSectionBackground(
    theme: SectionTheme,
    modifier: Modifier = Modifier,
) {
    val palette = LumyrinthThemeTokens.palette
    val lowerSurface = when (theme) {
        SectionTheme.SESSION -> palette.surfaceCardAlt
        SectionTheme.PROGRESS -> palette.bgElevated
        else -> palette.bgBase
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(palette.bgBase, lowerSurface),
                )
            )
    )
}
