package com.lumyrinth.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.lumyrinth.app.R

private val LumyrinthScheme = darkColorScheme(
    primary = LumyrinthColor.Violet,
    secondary = LumyrinthColor.VioletBright,
    tertiary = LumyrinthColor.Magenta,
    background = LumyrinthColor.Background,
    surface = LumyrinthColor.Surface,
    surfaceVariant = LumyrinthColor.SurfaceSoft,
    onPrimary = LumyrinthColor.TextPrimary,
    onBackground = LumyrinthColor.TextPrimary,
    onSurface = LumyrinthColor.TextPrimary,
    onSurfaceVariant = LumyrinthColor.TextSecondary,
)

private val LumyrinthTypography = Typography().run {
    val manrope = FontFamily(Font(R.font.manrope_variable))
    copy(
        displayLarge = displayLarge.copy(fontFamily = manrope),
        headlineLarge = headlineLarge.copy(fontFamily = manrope),
        titleLarge = titleLarge.copy(fontFamily = manrope),
        titleMedium = titleMedium.copy(fontFamily = manrope),
        bodyLarge = bodyLarge.copy(fontFamily = manrope),
        bodyMedium = bodyMedium.copy(fontFamily = manrope),
        labelLarge = labelLarge.copy(fontFamily = manrope),
    )
}

@Composable
fun LumyrinthTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = LumyrinthScheme, typography = LumyrinthTypography, content = content)
}
