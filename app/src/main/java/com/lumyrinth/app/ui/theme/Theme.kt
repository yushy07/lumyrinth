package com.lumyrinth.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

object LumyrinthTypography {
    val Display = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 37.sp,
        color = LumyrinthColors.TextPrimary,
    )

    val H1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 31.sp,
        color = LumyrinthColors.TextPrimary,
    )

    val H2 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 25.sp,
        color = LumyrinthColors.TextPrimary,
    )

    val H3 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 21.sp,
        color = LumyrinthColors.TextPrimary,
    )

    val Body = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        color = LumyrinthColors.TextSecondary,
    )

    val BodySm = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        color = LumyrinthColors.TextSecondary,
    )

    val Label = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 13.sp,
        letterSpacing = 0.06.sp,
        color = LumyrinthColors.TextSecondary,
    )

    val Button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        color = LumyrinthColors.TextPrimary,
    )

    val Countdown = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 72.sp,
        lineHeight = 72.sp,
        color = LumyrinthColors.TextPrimary,
    )

    val StatNumber = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 26.sp,
        color = LumyrinthColors.TextPrimary,
    )

    val Wordmark = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        letterSpacing = 8.sp,
        color = LumyrinthColors.TextPrimary,
    )
}

private val DarkColorScheme = darkColorScheme(
    primary = LumyrinthColors.AccentPurple,
    onPrimary = LumyrinthColors.TextPrimary,
    secondary = LumyrinthColors.AccentPink,
    onSecondary = LumyrinthColors.TextPrimary,
    tertiary = LumyrinthColors.AccentOrange,
    background = LumyrinthColors.BgBase,
    onBackground = LumyrinthColors.TextPrimary,
    surface = LumyrinthColors.SurfaceCard,
    onSurface = LumyrinthColors.TextPrimary,
    surfaceVariant = LumyrinthColors.SurfaceCardAlt,
    onSurfaceVariant = LumyrinthColors.TextSecondary,
)

@Composable
fun LumyrinthTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = LumyrinthColors.BgBase.toArgb()
                window.navigationBarColor = LumyrinthColors.BgElevated.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography(),
        content = content,
    )
}
