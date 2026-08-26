package com.lumyrinth.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object LumyrinthColors {
    val BgBase = Color(0xFFF5F3FF)
    val BgElevated = Color(0xFFEDEAFF)
    val SurfaceCard = Color(0xFFE5E2FF)
    val SurfaceCardAlt = Color(0xFFD8DDF8)
    val BorderSubtle = Color(0x1F4E5FB8)
    val BorderMedium = Color(0x3D4E5FB8)

    val TextPrimary = Color(0xFF292946)
    val TextSecondary = Color(0xFF62617A)
    val TextTertiary = Color(0xFF85839A)
    val TextOnGradient = Color(0xFFFFFFFF)

    val GradientButtonStart = Color(0xFF4E5FB8)
    val GradientButtonEnd = Color(0xFF4E5FB8)
    val GradientButton = Brush.horizontalGradient(
        listOf(GradientButtonStart, GradientButtonEnd)
    )

    val GradientPrimaryStart = Color(0xFF6D79C8)
    val GradientPrimaryMid = Color(0xFF8E98DB)
    val GradientPrimaryEnd = Color(0xFFD7DDFB)

    val GradientPrimary = Brush.linearGradient(
        listOf(GradientPrimaryStart, GradientPrimaryMid, GradientPrimaryEnd)
    )

    val GradientCoolStart = Color(0xFF4E5FB8)
    val GradientCoolEnd = Color(0xFF7381CA)
    val GradientCool = Brush.linearGradient(
        listOf(GradientCoolStart, GradientCoolEnd)
    )

    val AccentPurple = Color(0xFF4E5FB8)
    val AccentPink = Color(0xFF6674C4)
    val AccentOrange = Color(0xFF879445)
    val AccentYellow = Color(0xFFF1F679)
    val AccentSuccess = Color(0xFF68743B)

    val StreakFlame = Brush.linearGradient(
        listOf(AccentOrange, AccentPink)
    )

    val PhaseInhale = Color(0xFFF1F679)
    val PhaseHold1 = Color(0xFFCAD1F6)
    val PhaseExhale = Color(0xFF7885CF)
    val PhaseHold2 = Color(0xFFDDE1F8)

    val PhaseInhaleBg = Color(0x66F1F679)
    val PhaseHold1Bg = Color(0x66CAD1F6)
    val PhaseExhaleBg = Color(0x667885CF)
    val PhaseHold2Bg = Color(0x66DDE1F8)

    val OverlayWhite08 = Color(0x14FFFFFF)
    val OverlayWhite12 = Color(0x2EFFFFFF)
    val OverlayScrim = Color(0x8C000000)   // 0.55 alpha

    val ToggleOff = Color(0xFFC7C4D5)
}
