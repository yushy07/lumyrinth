package com.lumyrinth.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
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

private val manrope = FontFamily(Font(R.font.manrope_variable))

private val LumyrinthTypography = Typography(
    displayLarge = Typography().displayLarge.copy(
        fontFamily = manrope,
        fontWeight = FontWeight.SemiBold,
        fontSize = 42.sp,
        lineHeight = 48.sp,
        letterSpacing = (-1.0).sp,
    ),
    headlineLarge = Typography().headlineLarge.copy(
        fontFamily = manrope,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 38.sp,
        letterSpacing = (-0.8).sp,
    ),
    headlineMedium = Typography().headlineMedium.copy(
        fontFamily = manrope,
        fontWeight = FontWeight.SemiBold,
        fontSize = 26.sp,
        lineHeight = 32.sp,
    ),
    titleLarge = Typography().titleLarge.copy(fontFamily = manrope, fontWeight = FontWeight.SemiBold),
    titleMedium = Typography().titleMedium.copy(fontFamily = manrope, fontWeight = FontWeight.SemiBold),
    bodyLarge = Typography().bodyLarge.copy(fontFamily = manrope, lineHeight = 24.sp),
    bodyMedium = Typography().bodyMedium.copy(fontFamily = manrope, lineHeight = 21.sp),
    labelLarge = Typography().labelLarge.copy(fontFamily = manrope, fontWeight = FontWeight.SemiBold),
    labelMedium = Typography().labelMedium.copy(fontFamily = manrope, fontWeight = FontWeight.SemiBold),
)

@Composable
fun LumyrinthTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = LumyrinthScheme, typography = LumyrinthTypography, content = content)
}
