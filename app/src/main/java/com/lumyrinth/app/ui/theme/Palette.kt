package com.lumyrinth.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

enum class AppColorTheme(
    val id: String,
    val displayName: String,
    val description: String,
    val primaryHex: Color,
    val secondaryHex: Color,
) {
    TWILIGHT(
        id = "twilight",
        displayName = "Twilight Aura",
        description = "Deep violet & celestial magenta for mindful calm",
        primaryHex = Color(0xFF9333EA),
        secondaryHex = Color(0xFFEC4899),
    ),
    SAGE(
        id = "sage",
        displayName = "Celestial Sage",
        description = "Deep biophilic eucalyptus & lucid teal for stress relief",
        primaryHex = Color(0xFF2EC4B6),
        secondaryHex = Color(0xFF52B788),
    ),
    AMBER(
        id = "amber",
        displayName = "Warm Amber",
        description = "Circadian obsidian & soothing candlelight for sleep",
        primaryHex = Color(0xFFF59E0B),
        secondaryHex = Color(0xFFEA580C),
    ),
    OCEAN(
        id = "ocean",
        displayName = "Ocean Abyss",
        description = "Midnight abyss & electric cyan for focus & clarity",
        primaryHex = Color(0xFF00D2D3),
        secondaryHex = Color(0xFF54A0FF),
    );

    companion object {
        fun fromId(id: String): AppColorTheme = entries.find { it.id == id } ?: TWILIGHT
    }
}

data class LumyrinthPalette(
    val bgBase: Color,
    val bgElevated: Color,
    val surfaceCard: Color,
    val surfaceCardAlt: Color,
    val borderSubtle: Color,
    val borderMedium: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val primaryAccent: Color,
    val secondaryAccent: Color,
    val warmAccent: Color,
    val accentSuccess: Color,
    val gradientPrimary: Brush,
    val gradientButton: Brush,
    val phaseInhale: Color,
    val phaseHold1: Color,
    val phaseExhale: Color,
    val phaseHold2: Color,
    val auraGlowColors: List<Color>,
)

val TwilightPalette = LumyrinthPalette(
    bgBase = Color(0xFFF5F3FF),
    bgElevated = Color(0xFFEDEAFF),
    surfaceCard = Color(0xFFE5E2FF),
    surfaceCardAlt = Color(0xFFD8DDF8),
    borderSubtle = Color(0x1F4E5FB8),
    borderMedium = Color(0x3D4E5FB8),
    textPrimary = Color(0xFF292946),
    textSecondary = Color(0xFF62617A),
    textTertiary = Color(0xFF85839A),
    primaryAccent = Color(0xFF4E5FB8),
    secondaryAccent = Color(0xFF68743B),
    warmAccent = Color(0xFFF1F679),
    accentSuccess = Color(0xFF68743B),
    gradientPrimary = Brush.linearGradient(
        listOf(Color(0xFF4E5FB8), Color(0xFF6674C4), Color(0xFF8994D8))
    ),
    gradientButton = Brush.horizontalGradient(
        listOf(Color(0xFF4E5FB8), Color(0xFF4E5FB8))
    ),
    phaseInhale = Color(0xFFF1F679),
    phaseHold1 = Color(0xFFCAD1F6),
    phaseExhale = Color(0xFF7885CF),
    phaseHold2 = Color(0xFFDDE1F8),
    auraGlowColors = listOf(Color(0xFFF1F679), Color(0xFFCAD1F6), Color(0xFF7885CF)),
)

val SagePalette = LumyrinthPalette(
    bgBase = Color(0xFF06120E),
    bgElevated = Color(0xFF0B1F19),
    surfaceCard = Color(0xFF102A22),
    surfaceCardAlt = Color(0xFF17382E),
    borderSubtle = Color(0x1A52B788),
    borderMedium = Color(0x332EC4B6),
    textPrimary = Color(0xFFF0FDF4),
    textSecondary = Color(0xFF95D5B2),
    textTertiary = Color(0xFF52796F),
    primaryAccent = Color(0xFF2EC4B6),
    secondaryAccent = Color(0xFF52B788),
    warmAccent = Color(0xFF74C69D),
    accentSuccess = Color(0xFF52B788),
    gradientPrimary = Brush.linearGradient(
        listOf(Color(0xFF2EC4B6), Color(0xFF52B788), Color(0xFF95D5B2))
    ),
    gradientButton = Brush.horizontalGradient(
        listOf(Color(0xFF2EC4B6), Color(0xFF1B6B63))
    ),
    phaseInhale = Color(0xFF2EC4B6),
    phaseHold1 = Color(0xFF52B788),
    phaseExhale = Color(0xFF74C69D),
    phaseHold2 = Color(0xFF40916C),
    auraGlowColors = listOf(Color(0xFF2EC4B6), Color(0xFF52B788), Color(0xFF1B4332)),
)

val AmberPalette = LumyrinthPalette(
    bgBase = Color(0xFF0D0907),
    bgElevated = Color(0xFF1A120E),
    surfaceCard = Color(0xFF241914),
    surfaceCardAlt = Color(0xFF33231C),
    borderSubtle = Color(0x1AF59E0B),
    borderMedium = Color(0x33EA580C),
    textPrimary = Color(0xFFFFFBEB),
    textSecondary = Color(0xFFD4A373),
    textTertiary = Color(0xFF8C6D58),
    primaryAccent = Color(0xFFF59E0B),
    secondaryAccent = Color(0xFFEA580C),
    warmAccent = Color(0xFFFBBF24),
    accentSuccess = Color(0xFF10B981),
    gradientPrimary = Brush.linearGradient(
        listOf(Color(0xFFF59E0B), Color(0xFFEA580C), Color(0xFFE11D48))
    ),
    gradientButton = Brush.horizontalGradient(
        listOf(Color(0xFFF59E0B), Color(0xFFB45309))
    ),
    phaseInhale = Color(0xFFF59E0B),
    phaseHold1 = Color(0xFFFBBF24),
    phaseExhale = Color(0xFFEA580C),
    phaseHold2 = Color(0xFF9A3412),
    auraGlowColors = listOf(Color(0xFFFDE68A), Color(0xFFF59E0B), Color(0xFF7C2D12)),
)

val OceanPalette = LumyrinthPalette(
    bgBase = Color(0xFF040B14),
    bgElevated = Color(0xFF081628),
    surfaceCard = Color(0xFF0E223D),
    surfaceCardAlt = Color(0xFF153154),
    borderSubtle = Color(0x1A00D2D3),
    borderMedium = Color(0x3354A0FF),
    textPrimary = Color(0xFFEBF8FF),
    textSecondary = Color(0xFF70A1FF),
    textTertiary = Color(0xFF4A6B94),
    primaryAccent = Color(0xFF00D2D3),
    secondaryAccent = Color(0xFF54A0FF),
    warmAccent = Color(0xFF48DBFB),
    accentSuccess = Color(0xFF1DD1A1),
    gradientPrimary = Brush.linearGradient(
        listOf(Color(0xFF00D2D3), Color(0xFF54A0FF), Color(0xFF5F27CD))
    ),
    gradientButton = Brush.horizontalGradient(
        listOf(Color(0xFF00D2D3), Color(0xFF0984E3))
    ),
    phaseInhale = Color(0xFF00D2D3),
    phaseHold1 = Color(0xFF48DBFB),
    phaseExhale = Color(0xFF54A0FF),
    phaseHold2 = Color(0xFF2E86DE),
    auraGlowColors = listOf(Color(0xFF48DBFB), Color(0xFF00D2D3), Color(0xFF0A3D62)),
)

val LocalAppPalette = staticCompositionLocalOf { TwilightPalette }

object LumyrinthThemeTokens {
    val palette: LumyrinthPalette
        @Composable
        @ReadOnlyComposable
        get() = LocalAppPalette.current
}
