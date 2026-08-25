package com.lumyrinth.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object LumyrinthColors {
    val BgBase = Color(0xFF0B0710)
    val BgElevated = Color(0xFF120C1B)
    val SurfaceCard = Color(0xFF15101E)
    val SurfaceCardAlt = Color(0xFF1B1526)
    val BorderSubtle = Color(0x0FFFFFFF) // rgba(255,255,255,0.06)
    val BorderMedium = Color(0x1FFFFFFF) // rgba(255,255,255,0.12)

    val TextPrimary = Color(0xFFFFFFFF)
    val TextSecondary = Color(0xFFA79FB5)
    val TextTertiary = Color(0xFF6E6579)
    val TextOnGradient = Color(0xFFFFFFFF)

    val GradientButtonStart = Color(0xFF9333EA)
    val GradientButtonEnd = Color(0xFF6B21A8)
    val GradientButton = Brush.horizontalGradient(
        listOf(GradientButtonStart, GradientButtonEnd)
    )

    val GradientPrimaryStart = Color(0xFF8B3FD1)
    val GradientPrimaryMid = Color(0xFFC548A8)
    val GradientPrimaryEnd = Color(0xFFF2724E)

    val GradientPrimary = Brush.linearGradient(
        listOf(GradientPrimaryStart, GradientPrimaryMid, GradientPrimaryEnd)
    )

    val GradientCoolStart = Color(0xFF5B4FE0)
    val GradientCoolEnd = Color(0xFF8A4FE0)
    val GradientCool = Brush.linearGradient(
        listOf(GradientCoolStart, GradientCoolEnd)
    )

    val AccentPurple = Color(0xFF8B3FD1)
    val AccentPink = Color(0xFFD6469C)
    val AccentOrange = Color(0xFFF2724E)
    val AccentYellow = Color(0xFFF2C14E)
    val AccentSuccess = Color(0xFF4ED9A0)

    val StreakFlame = Brush.linearGradient(
        listOf(AccentOrange, AccentPink)
    )

    val PhaseInhale = Color(0xFF8B3FD1)
    val PhaseHold1 = Color(0xFFD6469C)
    val PhaseExhale = Color(0xFFF2724E)
    val PhaseHold2 = Color(0xFFB07238)

    val PhaseInhaleBg = Color(0x338B3FD1)
    val PhaseHold1Bg = Color(0x33D6469C)
    val PhaseExhaleBg = Color(0x33F2724E)
    val PhaseHold2Bg = Color(0x33B07238)

    val OverlayWhite08 = Color(0x14FFFFFF) // 0.08 alpha
    val OverlayWhite12 = Color(0x1FFFFFFF) // 0.12 alpha
    val OverlayScrim = Color(0x8C000000)   // 0.55 alpha

    val ToggleOff = Color(0xFF2A2432)
}
