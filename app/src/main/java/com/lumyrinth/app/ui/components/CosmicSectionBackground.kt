package com.lumyrinth.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.clearAndSetSemantics
import com.lumyrinth.app.ui.theme.LumyrinthColors
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

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

@Composable
fun CosmicSectionBackground(
    theme: SectionTheme,
    modifier: Modifier = Modifier,
) {
    if (rememberIsReducedMotion()) {
        val glow = when (theme) {
            SectionTheme.HOME, SectionTheme.ONBOARDING -> Color(0x332C0E4F)
            SectionTheme.EXPLORE -> Color(0x2B172F63)
            SectionTheme.PROGRESS -> Color(0x25106450)
            SectionTheme.SETTINGS, SectionTheme.LEGAL -> Color(0x252D1552)
            SectionTheme.DETAIL -> Color(0x2B5A1831)
            SectionTheme.SESSION -> Color(0x292B1451)
        }
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(glow, LumyrinthColors.BgBase),
                    )
                )
        )
        return
    }

    val infiniteTransition = rememberInfiniteTransition(label = "cosmic_bg_transition")

    // General cycle progress (0f..1f over 12 seconds)
    val progress1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "bg_progress_1",
    )

    // Secondary reverse cycle progress (0f..1f over 8 seconds)
    val progress2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bg_progress_2",
    )

    // Fast particle cycle progress (0f..1f over 4 seconds)
    val progress3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "bg_progress_3",
    )

    // Pre-calculated star particles
    val stars = remember {
        List(24) { index ->
            StarData(
                xRatio = (index * 37 % 100) / 100f,
                yRatio = (index * 53 % 100) / 100f,
                size = 1.5f + (index % 4) * 0.8f,
                speed = 0.5f + (index % 3) * 0.5f,
                alphaPhase = (index * 13 % 10) / 10f,
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LumyrinthColors.BgBase)
    ) {
        Canvas(modifier = Modifier.fillMaxSize().clearAndSetSemantics { }) {
            val width = size.width
            val height = size.height

            if (width <= 0 || height <= 0) return@Canvas

            val twoPi = (2 * PI).toFloat()

            when (theme) {
                SectionTheme.HOME -> {
                    // Cosmic Dawn: Undulating soft magenta-purple celestial nebulae
                    val angle1 = progress1 * twoPi
                    val orb1X = width * 0.3f + cos(angle1.toDouble()).toFloat() * (width * 0.15f)
                    val orb1Y = height * 0.25f + sin(angle1.toDouble()).toFloat() * (height * 0.12f)

                    val angle2 = progress2 * twoPi
                    val orb2X = width * 0.7f + sin(angle2.toDouble()).toFloat() * (width * 0.2f)
                    val orb2Y = height * 0.65f + cos(angle2.toDouble()).toFloat() * (height * 0.15f)

                    // Draw floating ambient glowing gradient orbs
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x38A855F7), // Vibrant purple glow
                                Color(0x187C3AED),
                                Color.Transparent
                            ),
                            center = Offset(orb1X, orb1Y),
                            radius = width * 0.65f
                        ),
                        center = Offset(orb1X, orb1Y),
                        radius = width * 0.65f
                    )

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x2CE879F9), // Soft magenta glow
                                Color(0x12EC4899),
                                Color.Transparent
                            ),
                            center = Offset(orb2X, orb2Y),
                            radius = width * 0.75f
                        ),
                        center = Offset(orb2X, orb2Y),
                        radius = width * 0.75f
                    )

                    // Rising dust motes
                    stars.forEach { star ->
                        val currentY = ((star.yRatio - progress1 * star.speed) % 1f + 1f) % 1f * height
                        val sinArg = (progress2 * twoPi + star.xRatio * 10).toDouble()
                        val currentX = (star.xRatio * width + sin(sinArg).toFloat() * 15f) % width
                        val alphaArg = ((progress2 + star.alphaPhase) * twoPi).toDouble()
                        val alpha = (0.3f + 0.4f * sin(alphaArg).toFloat()).coerceIn(0.1f, 0.7f)

                        drawCircle(
                            color = Color(0xFFF0ABFC).copy(alpha = alpha),
                            radius = star.size,
                            center = Offset(currentX, currentY)
                        )
                    }
                }

                SectionTheme.EXPLORE -> {
                    // Constellation & Starlight Drift
                    val cx = width * 0.5f + cos((progress2 * twoPi).toDouble()).toFloat() * (width * 0.1f)
                    val cy = height * 0.4f + sin((progress1 * twoPi).toDouble()).toFloat() * (height * 0.15f)

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x303B82F6), // Ocean Blue glow
                                Color(0x1A8B5CF6), // Deep Indigo
                                Color.Transparent
                            ),
                            center = Offset(cx, cy),
                            radius = width * 0.7f
                        ),
                        center = Offset(cx, cy),
                        radius = width * 0.7f
                    )

                    // Twinkling stars with orbital movement
                    stars.forEach { star ->
                        val currentX = (star.xRatio * width + progress1 * width * 0.08f) % width
                        val cosArg = (progress2 * twoPi + star.alphaPhase * 5).toDouble()
                        val currentY = (star.yRatio * height + cos(cosArg).toFloat() * 20f) % height
                        val alphaArg = ((progress3 + star.alphaPhase) * twoPi).toDouble()
                        val alpha = (0.2f + 0.6f * cos(alphaArg).toFloat()).coerceIn(0.1f, 0.85f)

                        drawCircle(
                            color = Color(0xFFC084FC).copy(alpha = alpha),
                            radius = star.size * 1.2f,
                            center = Offset(currentX, currentY)
                        )
                    }
                }

                SectionTheme.PROGRESS -> {
                    // Aurora Northern Lights Wave
                    val wavePath = Path()
                    val waveYBase = height * 0.35f
                    wavePath.moveTo(0f, waveYBase)

                    val steps = 10
                    for (i in 0..steps) {
                        val x = width * (i / steps.toFloat())
                        val sinOffset = sin((progress1 * twoPi + i * 0.5f).toDouble()).toFloat() * 45f
                        val cosOffset = cos((progress2 * twoPi + i * 0.8f).toDouble()).toFloat() * 25f
                        wavePath.lineTo(x, waveYBase + sinOffset + cosOffset)
                    }

                    wavePath.lineTo(width, height)
                    wavePath.lineTo(0f, height)
                    wavePath.close()

                    drawPath(
                        path = wavePath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0x2834D399), // Emerald Aurora
                                Color(0x1A38BDF8), // Cyan Glow
                                Color(0x0A818CF8), // Soft Violet
                                Color.Transparent
                            ),
                            startY = waveYBase - 60f,
                            endY = height * 0.8f
                        )
                    )

                    // Floating aurora sparks
                    stars.take(16).forEach { star ->
                        val sinArg = (progress1 * twoPi + star.yRatio).toDouble()
                        val sparkX = (star.xRatio * width + sin(sinArg).toFloat() * 30f) % width
                        val sparkY = ((star.yRatio - progress2 * 0.6f) % 1f + 1f) % 1f * height
                        val alphaArg = ((progress2 + star.alphaPhase) * twoPi).toDouble()
                        val sparkAlpha = (0.2f + 0.5f * sin(alphaArg).toFloat()).coerceIn(0.1f, 0.75f)

                        drawCircle(
                            color = Color(0xFF6EE7B7).copy(alpha = sparkAlpha),
                            radius = star.size * 1.3f,
                            center = Offset(sparkX, sparkY)
                        )
                    }
                }

                SectionTheme.SETTINGS -> {
                    // Zen Stardust Void & Slow Pulsing Center Halo
                    val haloX = width * 0.5f
                    val haloY = height * 0.35f
                    val pulseRadius = width * 0.45f + sin((progress2 * twoPi).toDouble()).toFloat() * 25f

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x2B9333EA),
                                Color(0x154C1D95),
                                Color.Transparent
                            ),
                            center = Offset(haloX, haloY),
                            radius = pulseRadius
                        ),
                        center = Offset(haloX, haloY),
                        radius = pulseRadius
                    )

                    // Subtle orbital ring
                    drawCircle(
                        color = Color(0x1FA855F7),
                        radius = width * 0.32f,
                        center = Offset(haloX, haloY),
                        style = Stroke(width = 2.5f)
                    )

                    stars.take(18).forEach { star ->
                        val angle = (progress1 * star.speed * twoPi + star.xRatio * 10).toDouble()
                        val radius = width * 0.32f + (star.yRatio - 0.5f) * 40f
                        val starX = haloX + cos(angle).toFloat() * radius
                        val starY = haloY + sin(angle).toFloat() * radius
                        val sinArg = (progress2 * twoPi + star.alphaPhase).toDouble()
                        val alpha = (0.3f + 0.5f * sin(sinArg).toFloat()).coerceIn(0.15f, 0.8f)

                        drawCircle(
                            color = Color(0xFFE9D5FF).copy(alpha = alpha),
                            radius = star.size,
                            center = Offset(starX, starY)
                        )
                    }
                }

                SectionTheme.DETAIL -> {
                    // Starfire Nebula
                    val glowX = width * 0.5f + sin((progress1 * twoPi).toDouble()).toFloat() * (width * 0.15f)
                    val glowY = height * 0.25f + cos((progress2 * twoPi).toDouble()).toFloat() * (height * 0.1f)

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x38F43F5E), // Warm rose pink
                                Color(0x1DA855F7), // Soft purple
                                Color.Transparent
                            ),
                            center = Offset(glowX, glowY),
                            radius = width * 0.65f
                        ),
                        center = Offset(glowX, glowY),
                        radius = width * 0.65f
                    )

                    stars.forEach { star ->
                        val py = ((star.yRatio - progress1 * 0.8f) % 1f + 1f) % 1f * height
                        val cosArg = (progress2 * twoPi + star.yRatio * 5).toDouble()
                        val px = (star.xRatio * width + cos(cosArg).toFloat() * 20f) % width
                        val alphaArg = ((progress1 + star.alphaPhase) * twoPi).toDouble()
                        val alpha = (0.25f + 0.55f * sin(alphaArg).toFloat()).coerceIn(0.1f, 0.8f)

                        drawCircle(
                            color = Color(0xFFFDA4AF).copy(alpha = alpha),
                            radius = star.size,
                            center = Offset(px, py)
                        )
                    }
                }

                SectionTheme.SESSION -> {
                    // Active Phase Aura (Gentle pulsating aura ring in background)
                    val auraCenter = Offset(width * 0.5f, height * 0.45f)
                    val sinArg = (progress1 * twoPi).toDouble()
                    val auraRadius = width * (0.45f + 0.08f * sin(sinArg).toFloat())

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x35A855F7),
                                Color(0x1BE879F9),
                                Color.Transparent
                            ),
                            center = auraCenter,
                            radius = auraRadius
                        ),
                        center = auraCenter,
                        radius = auraRadius
                    )
                }

                SectionTheme.ONBOARDING -> {
                    // Celestial Gateway (Soft rotating ethereal rays)
                    val origin = Offset(width * 0.5f, height * 0.3f)
                    val rayRadius = width * 0.85f

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x40A855F7),
                                Color(0x20EC4899),
                                Color.Transparent
                            ),
                            center = origin,
                            radius = rayRadius
                        ),
                        center = origin,
                        radius = rayRadius
                    )

                    stars.forEach { star ->
                        val angle = (progress1 * star.speed * twoPi + star.xRatio * 10).toDouble()
                        val dist = (star.yRatio * 0.7f + 0.15f) * width
                        val sx = origin.x + cos(angle).toFloat() * dist
                        val sy = origin.y + sin(angle).toFloat() * dist
                        val sinArg = (progress2 * twoPi + star.alphaPhase).toDouble()
                        val alpha = (0.2f + 0.6f * sin(sinArg).toFloat()).coerceIn(0.1f, 0.8f)

                        drawCircle(
                            color = Color(0xFFF472B6).copy(alpha = alpha),
                            radius = star.size * 1.2f,
                            center = Offset(sx, sy)
                        )
                    }
                }

                SectionTheme.LEGAL -> {
                    // Quiet Sanctuary (Soft deep violet wave)
                    val lCenter = Offset(width * 0.5f, height * 0.2f)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x286D28D9),
                                Color(0x124C1D95),
                                Color.Transparent
                            ),
                            center = lCenter,
                            radius = width * 0.6f
                        ),
                        center = lCenter,
                        radius = width * 0.6f
                    )
                }
            }
        }
    }
}

private data class StarData(
    val xRatio: Float,
    val yRatio: Float,
    val size: Float,
    val speed: Float,
    val alphaPhase: Float,
)
