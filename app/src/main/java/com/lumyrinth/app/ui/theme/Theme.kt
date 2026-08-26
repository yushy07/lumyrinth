package com.lumyrinth.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.lumyrinth.app.R

val Manrope = FontFamily(
    Font(R.font.manrope_variable, FontWeight.Normal),
    Font(R.font.manrope_variable, FontWeight.Medium),
    Font(R.font.manrope_variable, FontWeight.SemiBold),
    Font(R.font.manrope_variable, FontWeight.Bold),
    Font(R.font.manrope_variable, FontWeight.ExtraBold),
)

object LumyrinthTypography {
    val Display = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 38.sp,
        letterSpacing = (-0.5).sp,
        color = LumyrinthColors.TextPrimary,
    )

    val H1 = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.3).sp,
        color = LumyrinthColors.TextPrimary,
    )

    val H2 = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = (-0.2).sp,
        color = LumyrinthColors.TextPrimary,
    )

    val H3 = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        color = LumyrinthColors.TextPrimary,
    )

    val Body = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        color = LumyrinthColors.TextSecondary,
    )

    val BodySm = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        color = LumyrinthColors.TextSecondary,
    )

    val Label = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.5.sp,
        color = LumyrinthColors.TextSecondary,
    )

    val Button = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.2.sp,
        color = LumyrinthColors.TextPrimary,
    )

    val Countdown = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Bold,
        fontSize = 72.sp,
        lineHeight = 72.sp,
        letterSpacing = (-1.0).sp,
        color = LumyrinthColors.TextPrimary,
    )

    val StatNumber = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.3).sp,
        color = LumyrinthColors.TextPrimary,
    )

    val Wordmark = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 20.sp,
        letterSpacing = 9.sp,
        color = LumyrinthColors.TextPrimary,
    )

    val BrandTitle = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = 6.sp,
        color = LumyrinthColors.TextPrimary,
    )
}

object LumyrinthSpacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
    val xxxl = 32.dp
    val section = 40.dp
}

object LumyrinthShapes {
    val Chip = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    val Control = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    val Card = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
    val Modal = androidx.compose.foundation.shape.RoundedCornerShape(28.dp)
    val Pill = androidx.compose.foundation.shape.RoundedCornerShape(100.dp)
}

object LumyrinthMotion {
    const val QuickMillis = 150
    const val StandardMillis = 300
    const val CalmMillis = 600
    const val AmbientMillis = 12_000
}

private val AppTypography = Typography(
    displayLarge = LumyrinthTypography.Display,
    headlineLarge = LumyrinthTypography.H1,
    headlineMedium = LumyrinthTypography.H2,
    titleLarge = LumyrinthTypography.H3,
    bodyLarge = LumyrinthTypography.Body,
    bodyMedium = LumyrinthTypography.BodySm,
    labelLarge = LumyrinthTypography.Button,
    labelMedium = LumyrinthTypography.Label,
)

private val DarkColorScheme = darkColorScheme(
    primary = LumyrinthColors.AccentPurple,
    onPrimary = LumyrinthColors.TextPrimary,
    secondary = LumyrinthColors.AccentPink,
    onSecondary = LumyrinthColors.TextPrimary,
    tertiary = LumyrinthColors.AccentOrange,
    onTertiary = LumyrinthColors.TextPrimary,
    background = LumyrinthColors.BgBase,
    onBackground = LumyrinthColors.TextPrimary,
    surface = LumyrinthColors.SurfaceCard,
    onSurface = LumyrinthColors.TextPrimary,
    surfaceVariant = LumyrinthColors.SurfaceCardAlt,
    onSurfaceVariant = LumyrinthColors.TextSecondary,
    outline = LumyrinthColors.BorderMedium,
    outlineVariant = LumyrinthColors.BorderSubtle,
    error = androidx.compose.ui.graphics.Color(0xFFEF4444),
    onError = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    surfaceContainer = LumyrinthColors.BgElevated,
)

@Composable
fun LumyrinthTheme(
    appTheme: String = "twilight",
    content: @Composable () -> Unit,
) {
    val selectedTheme = AppColorTheme.fromId(appTheme)
    val palette = when (selectedTheme) {
        AppColorTheme.TWILIGHT -> TwilightPalette
        AppColorTheme.SAGE -> SagePalette
        AppColorTheme.AMBER -> AmberPalette
        AppColorTheme.OCEAN -> OceanPalette
    }

    val dynamicColorScheme = darkColorScheme(
        primary = palette.primaryAccent,
        onPrimary = palette.textPrimary,
        secondary = palette.secondaryAccent,
        onSecondary = palette.textPrimary,
        tertiary = palette.warmAccent,
        onTertiary = palette.textPrimary,
        background = palette.bgBase,
        onBackground = palette.textPrimary,
        surface = palette.surfaceCard,
        onSurface = palette.textPrimary,
        surfaceVariant = palette.surfaceCardAlt,
        onSurfaceVariant = palette.textSecondary,
        outline = palette.borderMedium,
        outlineVariant = palette.borderSubtle,
        error = androidx.compose.ui.graphics.Color(0xFFEF4444),
        onError = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
        surfaceContainer = palette.bgElevated,
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    CompositionLocalProvider(LocalAppPalette provides palette) {
        MaterialTheme(
            colorScheme = dynamicColorScheme,
            typography = AppTypography,
            content = content,
        )
    }
}
