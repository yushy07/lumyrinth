@file:Suppress("UNREACHABLE_CODE")

package com.lumyrinth.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumyrinth.app.domain.BreathPhase
import com.lumyrinth.app.domain.Rhythm
import com.lumyrinth.app.ui.theme.LumyrinthColors
import com.lumyrinth.app.ui.theme.LumyrinthThemeTokens
import com.lumyrinth.app.ui.theme.LumyrinthTypography
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Reusable visual anchor for meditation and breathwork sessions.
 * Smoothly expands and contracts according to breath phases and animation states.
 */
@Composable
fun BreathingCircle(
    modifier: Modifier = Modifier,
    circleSize: Dp = 260.dp,
    animationState: OrbAnimationState = OrbAnimationState.Idle(),
    centerContent: OrbCenterContent = OrbCenterContent.None,
    customContent: (@Composable BoxScope.() -> Unit)? = null,
) {
    ExpressiveBreathingFlower(
        modifier = modifier,
        size = circleSize,
        animationState = animationState,
        centerContent = centerContent,
        customContent = customContent,
    )
    return

    val isReducedMotion = rememberIsReducedMotion()
    val infiniteTransition = rememberInfiniteTransition(label = "breathing_circle_ambient")

    // 1. Idle scale pulse: 0.96 -> 1.04 -> 0.96 over 4000ms
    val idleScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bc_idle_scale",
    )

    // 2. Idle aura glow intensity: 0.70 -> 1.0 -> 0.70
    val idleGlowOpacity by infiniteTransition.animateFloat(
        initialValue = 0.70f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bc_idle_glow",
    )

    // 3. Smooth orbital celestial rotation
    val continuousRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 96_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "bc_ring_rotation",
    )

    val isPaused = when (animationState) {
        is OrbAnimationState.Idle -> animationState.isPaused
        is OrbAnimationState.Breathing -> animationState.isPaused
        is OrbAnimationState.Complete -> false
    }

    val activeRotation = if (isReducedMotion) {
        0f
    } else if (isPaused) {
        remember { continuousRotation }
    } else {
        continuousRotation
    }

    // 4. Calculate dynamic scale and phase glow intensity
    val currentScale: Float
    val glowIntensity: Float
    val activePhase: BreathPhase? = when (animationState) {
        is OrbAnimationState.Breathing -> animationState.phase
        else -> null
    }

    when (animationState) {
        is OrbAnimationState.Idle -> {
            currentScale = idleScale
            glowIntensity = idleGlowOpacity
        }
        is OrbAnimationState.Breathing -> {
            currentScale = animationState.scale
            val normalizedProgress = ((animationState.scale - 0.75f) / 0.25f).coerceIn(0f, 1f)
            glowIntensity = 0.70f + (normalizedProgress * 0.30f)
        }
        is OrbAnimationState.Complete -> {
            currentScale = 0.92f * idleScale
            glowIntensity = idleGlowOpacity
        }
    }

    // 5. Checkmark entrance animation for session completion
    val checkmarkAlpha = remember { Animatable(0f) }
    val checkmarkScale = remember { Animatable(0.8f) }

    LaunchedEffect(centerContent, animationState) {
        if (centerContent is OrbCenterContent.Checkmark) {
            checkmarkAlpha.snapTo(0f)
            checkmarkScale.snapTo(0.8f)
            delay(300L)
            if (isReducedMotion) {
                checkmarkAlpha.animateTo(1.0f, tween(250))
                checkmarkScale.animateTo(1.0f, tween(250))
            } else {
                checkmarkAlpha.animateTo(1.0f, tween(400, easing = FastOutSlowInEasing))
                checkmarkScale.animateTo(1.05f, tween(280, easing = FastOutSlowInEasing))
                checkmarkScale.animateTo(1.0f, tween(120, easing = FastOutSlowInEasing))
            }
        }
    }

    val palette = LumyrinthThemeTokens.palette

    // Harmonic phase color dynamic aura reflecting the active app theme
    val phaseAuraColors = when (activePhase) {
        BreathPhase.INHALE -> listOf(
            palette.phaseInhale.copy(alpha = 0.45f * glowIntensity),
            palette.primaryAccent.copy(alpha = 0.28f * glowIntensity),
            palette.primaryAccent.copy(alpha = 0.12f * glowIntensity),
            Color.Transparent,
        )
        BreathPhase.HOLD_AFTER_INHALE -> listOf(
            palette.phaseHold1.copy(alpha = 0.45f * glowIntensity),
            palette.secondaryAccent.copy(alpha = 0.28f * glowIntensity),
            palette.secondaryAccent.copy(alpha = 0.14f * glowIntensity),
            Color.Transparent,
        )
        BreathPhase.EXHALE -> listOf(
            palette.phaseExhale.copy(alpha = 0.45f * glowIntensity),
            palette.warmAccent.copy(alpha = 0.28f * glowIntensity),
            palette.warmAccent.copy(alpha = 0.12f * glowIntensity),
            Color.Transparent,
        )
        BreathPhase.HOLD_AFTER_EXHALE -> listOf(
            palette.phaseHold2.copy(alpha = 0.38f * glowIntensity),
            palette.primaryAccent.copy(alpha = 0.22f * glowIntensity),
            palette.primaryAccent.copy(alpha = 0.10f * glowIntensity),
            Color.Transparent,
        )
        null -> listOf(
            palette.primaryAccent.copy(alpha = 0.38f * glowIntensity),
            palette.secondaryAccent.copy(alpha = 0.22f * glowIntensity),
            palette.primaryAccent.copy(alpha = 0.12f * glowIntensity),
            Color.Transparent,
        )
    }

    Box(
        modifier = modifier
            .size(circleSize)
            .scale(currentScale)
            .testTag("breathing_circle"),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(circleSize)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = size.minDimension / 2f

            // Outer Atmospheric Glowing Aura
            val broadGlow = Brush.radialGradient(
                colors = phaseAuraColors,
                center = center,
                radius = baseRadius * 0.98f,
            )
            drawCircle(
                brush = broadGlow,
                radius = baseRadius * 0.98f,
                center = center,
            )

            // Inner warm coronal flare
            val coronaGlow = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFF5277).copy(alpha = 0.50f * glowIntensity),
                    Color(0xFFD946EF).copy(alpha = 0.32f * glowIntensity),
                    Color(0xFF8B5CF6).copy(alpha = 0.15f * glowIntensity),
                    Color.Transparent,
                ),
                center = center,
                radius = baseRadius * 0.45f,
            )
            drawCircle(
                brush = coronaGlow,
                radius = baseRadius * 0.45f,
                center = center,
            )

            // Concentric Celestial Astrolabe Rings
            val ringRadii = listOf(
                baseRadius * 0.44f, // Inner guidance ring
                baseRadius * 0.56f, // Harmonic mid ring
                baseRadius * 0.68f, // Constellation ring
                baseRadius * 0.80f, // Orbital perimeter
                baseRadius * 0.92f, // Outer celestial fringe
            )
            val ringOpacities = listOf(0.95f, 0.75f, 0.55f, 0.38f, 0.22f)

            rotate(degrees = activeRotation, pivot = center) {
                // Floating celestial nodes
                val satelliteNodes: List<Triple<Float, Float, Color>> = listOf(
                    Triple(0.88f, -0.65f, Color(0xFFF43F5E)),
                    Triple(0.45f, -0.85f, Color(0xFFE879F9)),
                    Triple(0.92f, 0.20f, Color(0xFFD946EF)),
                    Triple(-0.85f, 0.45f, Color(0xFFF97316)),
                    Triple(0.70f, 0.70f, Color(0xFFA855F7)),
                    Triple(-0.40f, 0.88f, Color(0xFFFB7185)),
                    Triple(-0.75f, -0.50f, Color(0xFFC084FC)),
                )

                satelliteNodes.forEachIndexed { sIndex, node ->
                    val xRel = node.first
                    val yRel = node.second
                    val nodeColor = node.third
                    val satRadius = baseRadius * 0.95f
                    val satX = center.x + (xRel * satRadius)
                    val satY = center.y + (yRel * satRadius)
                    val nodePulse = (0.75f + 0.25f * sin(activeRotation * (PI / 90) + sIndex)).toFloat()

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                nodeColor.copy(alpha = 0.50f * glowIntensity * nodePulse),
                                Color.Transparent,
                            ),
                            center = Offset(satX, satY),
                            radius = 12f,
                        ),
                        radius = 12f,
                        center = Offset(satX, satY),
                    )

                    drawCircle(
                        color = Color.White.copy(alpha = 0.85f * glowIntensity * nodePulse),
                        radius = 2.4f,
                        center = Offset(satX, satY),
                    )
                }

                // Render each orbit layer with glowing markers
                for (i in ringRadii.indices) {
                    val radius = ringRadii[i]
                    val ringAlpha = ringOpacities[i] * glowIntensity
                    val numDots = 32 + (i * 12)

                    if (i == 0) {
                        drawCircle(
                            brush = Brush.sweepGradient(
                                listOf(
                                    palette.secondaryAccent,
                                    palette.primaryAccent,
                                    palette.warmAccent,
                                    palette.secondaryAccent,
                                )
                            ),
                            radius = radius,
                            center = center,
                            style = Stroke(width = 3.2f),
                            alpha = (0.92f * glowIntensity).coerceIn(0f, 1f),
                        )
                        drawCircle(
                            color = palette.secondaryAccent.copy(alpha = 0.35f * glowIntensity),
                            radius = radius,
                            center = center,
                            style = Stroke(width = 8f),
                        )
                    } else {
                        drawCircle(
                            color = palette.primaryAccent.copy(alpha = ringAlpha * 0.25f),
                            radius = radius,
                            center = center,
                            style = Stroke(
                                width = 0.8f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(3f, 6f), 0f),
                            ),
                        )
                    }

                    for (d in 0 until numDots) {
                        val angle = (d.toDouble() / numDots) * 2 * PI
                        val x = center.x + (radius * cos(angle)).toFloat()
                        val y = center.y + (radius * sin(angle)).toFloat()

                        val isMajorNode = (d % 8 == 0)
                        val isSemiNode = (d % 4 == 0)

                        val dotRadius = when {
                            isMajorNode -> if (i == 0) 3.2f else 2.2f
                            isSemiNode -> 1.6f
                            else -> 1.0f
                        }

                        val dotColor = when {
                            isMajorNode -> Color.White.copy(alpha = (ringAlpha * 1.3f).coerceIn(0f, 1f))
                            d % 3 == 0 -> Color(0xFFFF71D4).copy(alpha = ringAlpha)
                            d % 3 == 1 -> Color(0xFFC084FC).copy(alpha = ringAlpha)
                            else -> Color(0xFFFDBA74).copy(alpha = ringAlpha * 0.9f)
                        }

                        if (isMajorNode) {
                            drawCircle(
                                color = Color(0xFFE879F9).copy(alpha = ringAlpha * 0.55f),
                                radius = dotRadius * 2.4f,
                                center = Offset(x, y),
                            )
                        }

                        drawCircle(
                            color = dotColor,
                            radius = dotRadius,
                            center = Offset(x, y),
                        )
                    }
                }
            }

            // Central radiant core when idle / no overlay
            if (centerContent is OrbCenterContent.None && customContent == null) {
                val coreCorona = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFF0F5).copy(alpha = 0.95f * glowIntensity),
                        Color(0xFFFF3377).copy(alpha = 0.85f * glowIntensity),
                        Color(0xFFC026D3).copy(alpha = 0.50f * glowIntensity),
                        Color.Transparent,
                    ),
                    center = center,
                    radius = baseRadius * 0.22f,
                )
                drawCircle(
                    brush = coreCorona,
                    radius = baseRadius * 0.22f,
                    center = center,
                )

                drawCircle(
                    color = Color.White,
                    radius = baseRadius * 0.085f,
                    center = center,
                )

                val flareAlpha = (0.75f * glowIntensity).coerceIn(0f, 1f)
                val flareLen = baseRadius * 0.16f
                drawLine(
                    color = Color.White.copy(alpha = flareAlpha),
                    start = Offset(center.x - flareLen, center.y),
                    end = Offset(center.x + flareLen, center.y),
                    strokeWidth = 1.2f,
                )
                drawLine(
                    color = Color.White.copy(alpha = flareAlpha),
                    start = Offset(center.x, center.y - flareLen),
                    end = Offset(center.x, center.y + flareLen),
                    strokeWidth = 1.2f,
                )
            } else {
                val centerBackingGlow = Brush.radialGradient(
                    colors = listOf(
                        LumyrinthColors.GradientPrimaryMid.copy(alpha = 0.32f * glowIntensity),
                        Color.Transparent,
                    ),
                    center = center,
                    radius = baseRadius * 0.36f,
                )
                drawCircle(
                    brush = centerBackingGlow,
                    radius = baseRadius * 0.36f,
                    center = center,
                )
            }
        }

        // Center Content Rendering
        if (customContent != null) {
            customContent()
        } else {
            when (centerContent) {
                is OrbCenterContent.None -> {}
                is OrbCenterContent.Countdown -> {
                    AnimatedContent(
                        targetState = centerContent.seconds,
                        transitionSpec = {
                            (fadeIn(animationSpec = tween(150, easing = LinearEasing)) +
                                    scaleIn(initialScale = 0.92f, animationSpec = tween(150, easing = FastOutSlowInEasing)))
                                .togetherWith(
                                    fadeOut(animationSpec = tween(120, easing = LinearEasing)) +
                                            scaleOut(targetScale = 1.05f, animationSpec = tween(120, easing = FastOutSlowInEasing))
                                )
                        },
                        label = "bc_countdown_transition",
                    ) { seconds ->
                        Text(
                            text = "$seconds",
                            style = LumyrinthTypography.Countdown,
                            color = LumyrinthColors.TextPrimary,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                is OrbCenterContent.Checkmark -> {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .scale(checkmarkScale.value)
                            .alpha(checkmarkAlpha.value),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = "Session Completed",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpressiveBreathingFlower(
    modifier: Modifier,
    size: Dp,
    animationState: OrbAnimationState,
    centerContent: OrbCenterContent,
    customContent: (@Composable BoxScope.() -> Unit)?,
) {
    val palette = LumyrinthThemeTokens.palette
    val visualScale = when (animationState) {
        is OrbAnimationState.Breathing -> animationState.scale
        is OrbAnimationState.Complete -> 0.92f
        is OrbAnimationState.Idle -> 0.86f
    }
    val phase = (animationState as? OrbAnimationState.Breathing)?.phase
    val fill = when (phase) {
        BreathPhase.INHALE -> palette.warmAccent
        BreathPhase.HOLD_AFTER_INHALE -> palette.surfaceCardAlt
        BreathPhase.EXHALE -> palette.primaryAccent.copy(alpha = 0.72f)
        BreathPhase.HOLD_AFTER_EXHALE, null -> palette.surfaceCardAlt
    }
    val outline = when (phase) {
        BreathPhase.INHALE -> palette.secondaryAccent
        else -> palette.primaryAccent.copy(alpha = 0.42f)
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(
            modifier = Modifier
                .size(size)
                .scale(visualScale.coerceIn(0.68f, 1.04f))
                .clearAndSetSemantics { },
        ) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val baseRadius = this.size.minDimension * 0.40f
            val lobes = 10
            val points = 160
            val path = Path()
            repeat(points + 1) { index ->
                val angle = (index.toFloat() / points) * (2f * PI.toFloat())
                val radius = baseRadius * (1f + 0.10f * sin(lobes * angle))
                val x = center.x + cos(angle) * radius
                val y = center.y + sin(angle) * radius
                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()

            drawPath(path = path, color = outline.copy(alpha = 0.12f))
            drawPath(path = path, color = fill)
            drawPath(path = path, color = outline, style = Stroke(width = 3.dp.toPx()))
            drawCircle(
                color = palette.primaryAccent,
                radius = this.size.minDimension * 0.035f,
                center = center,
            )
        }

        when (centerContent) {
            is OrbCenterContent.Countdown -> Text(
                text = centerContent.seconds.toString(),
                style = LumyrinthTypography.Countdown,
                color = palette.textPrimary,
                textAlign = TextAlign.Center,
            )
            OrbCenterContent.Checkmark -> Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = "Complete",
                tint = palette.textPrimary,
                modifier = Modifier.size(48.dp),
            )
            OrbCenterContent.None -> Unit
        }
        customContent?.invoke(this)
    }
}

/**
 * Self-driving autonomous BreathingCircle Composable that smoothly loops through
 * a selected Rhythm preset (e.g., Box Breathing, 4-7-8 Technique), dynamically
 * adjusting its expansion/contraction animation speed to match the active preset.
 */
@Composable
fun RhythmBreathingCircle(
    rhythm: Rhythm,
    modifier: Modifier = Modifier,
    circleSize: Dp = 240.dp,
    isPaused: Boolean = false,
    showPhaseLabel: Boolean = true,
    showCountdown: Boolean = true,
    onPhaseChange: ((BreathPhase) -> Unit)? = null,
) {
    val isReducedMotion = rememberIsReducedMotion()
    val activePhases = remember(rhythm) { rhythm.activePhases() }

    var currentPhaseIndex by remember(rhythm) { mutableIntStateOf(0) }
    var phaseElapsedMillis by remember(rhythm) { mutableLongStateOf(0L) }
    var currentScale by remember(rhythm) { mutableFloatStateOf(0.75f) }

    val safeIndex = currentPhaseIndex % activePhases.size
    val currentPhasePair = activePhases.getOrElse(safeIndex) { BreathPhase.INHALE to 4 }
    val activePhase = currentPhasePair.first
    val currentPhaseDurationMs = (currentPhasePair.second * 1000f).coerceAtLeast(500f)

    LaunchedEffect(activePhase) {
        onPhaseChange?.invoke(activePhase)
    }

    LaunchedEffect(rhythm, isPaused) {
        var lastFrameTime = 0L
        while (true) {
            withFrameMillis { frameTime ->
                if (!isPaused) {
                    if (lastFrameTime != 0L) {
                        val delta = (frameTime - lastFrameTime).coerceIn(0L, 50L)
                        phaseElapsedMillis += delta

                        if (phaseElapsedMillis >= currentPhaseDurationMs) {
                            phaseElapsedMillis = 0L
                            currentPhaseIndex = (currentPhaseIndex + 1) % activePhases.size
                        }
                    }
                }
                lastFrameTime = frameTime
            }

            val fraction = (phaseElapsedMillis / currentPhaseDurationMs).coerceIn(0f, 1f)
            currentScale = when (activePhase) {
                BreathPhase.INHALE -> {
                    if (isReducedMotion) {
                        0.75f + (0.25f * FastOutSlowInEasing.transform(fraction))
                    } else {
                        0.75f + (0.25f * InhaleEasing.transform(fraction))
                    }
                }
                BreathPhase.HOLD_AFTER_INHALE -> {
                    if (isReducedMotion || isPaused) 1.0f
                    else 1.0f + (sin((phaseElapsedMillis / 2000.0) * 2 * PI) * 0.012f).toFloat()
                }
                BreathPhase.EXHALE -> {
                    if (isReducedMotion) {
                        1.0f - (0.25f * FastOutSlowInEasing.transform(fraction))
                    } else {
                        1.0f - (0.25f * ExhaleEasing.transform(fraction))
                    }
                }
                BreathPhase.HOLD_AFTER_EXHALE -> {
                    if (isReducedMotion || isPaused) 0.75f
                    else 0.75f * (1.0f + (sin((phaseElapsedMillis / 2000.0) * 2 * PI) * 0.012f).toFloat())
                }
            }
        }
    }

    val remainingMillis = (currentPhaseDurationMs - phaseElapsedMillis).coerceAtLeast(0f)
    val secondsLeft = kotlin.math.ceil(remainingMillis / 1000f).toInt().coerceAtLeast(0)

    BreathingCircle(
        modifier = modifier,
        circleSize = circleSize,
        animationState = OrbAnimationState.Breathing(
            scale = currentScale,
            phase = activePhase,
            isPaused = isPaused,
        ),
        customContent = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
            ) {
                if (showPhaseLabel) {
                    Text(
                        text = activePhase.label,
                        style = LumyrinthTypography.Label.copy(
                            fontSize = 13.sp,
                            letterSpacing = 2.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        ),
                        color = Color.White.copy(alpha = 0.9f),
                    )
                }
                if (showCountdown) {
                    Text(
                        text = "$secondsLeft",
                        style = LumyrinthTypography.Countdown.copy(
                            fontSize = if (showPhaseLabel) 36.sp else 44.sp,
                        ),
                        color = LumyrinthColors.TextPrimary,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        },
    )
}

/**
 * Self-driving autonomous BreathingCircle Composable.
 * Automatically animates expansion and contraction smoothly across the given breath phase.
 */
@Composable
fun AutonomousBreathingCircle(
    phase: BreathPhase,
    phaseDurationSeconds: Int,
    modifier: Modifier = Modifier,
    size: Dp = 260.dp,
    isPaused: Boolean = false,
    showCountdown: Boolean = true,
    content: (@Composable BoxScope.() -> Unit)? = null,
) {
    val isReducedMotion = rememberIsReducedMotion()
    var phaseElapsedMillis by remember { mutableLongStateOf(0L) }
    var currentScale by remember { mutableFloatStateOf(0.75f) }
    val phaseDurationMs = (phaseDurationSeconds * 1000f).coerceAtLeast(1000f)

    LaunchedEffect(phase, isPaused, phaseDurationSeconds) {
        phaseElapsedMillis = 0L
        var lastFrameTime = 0L
        while (phaseElapsedMillis < phaseDurationMs) {
            withFrameMillis { frameTime ->
                if (!isPaused) {
                    if (lastFrameTime != 0L) {
                        val delta = (frameTime - lastFrameTime).coerceIn(0L, 50L)
                        phaseElapsedMillis += delta
                    }
                }
                lastFrameTime = frameTime
            }

            val fraction = (phaseElapsedMillis / phaseDurationMs).coerceIn(0f, 1f)
            currentScale = when (phase) {
                BreathPhase.INHALE -> {
                    if (isReducedMotion) {
                        0.75f + (0.25f * FastOutSlowInEasing.transform(fraction))
                    } else {
                        0.75f + (0.25f * InhaleEasing.transform(fraction))
                    }
                }
                BreathPhase.HOLD_AFTER_INHALE -> {
                    if (isReducedMotion || isPaused) 1.0f
                    else 1.0f + (sin((phaseElapsedMillis / 2000.0) * 2 * PI) * 0.01f).toFloat()
                }
                BreathPhase.EXHALE -> {
                    if (isReducedMotion) {
                        1.0f - (0.25f * FastOutSlowInEasing.transform(fraction))
                    } else {
                        1.0f - (0.25f * ExhaleEasing.transform(fraction))
                    }
                }
                BreathPhase.HOLD_AFTER_EXHALE -> {
                    if (isReducedMotion || isPaused) 0.75f
                    else 0.75f * (1.0f + (sin((phaseElapsedMillis / 2000.0) * 2 * PI) * 0.01f).toFloat())
                }
            }
        }
    }

    val remainingMillis = (phaseDurationMs - phaseElapsedMillis).coerceAtLeast(0f)
    val secondsLeft = kotlin.math.ceil(remainingMillis / 1000f).toInt().coerceAtLeast(0)

    BreathingCircle(
        modifier = modifier,
        circleSize = size,
        animationState = OrbAnimationState.Breathing(
            scale = currentScale,
            phase = phase,
            isPaused = isPaused,
        ),
        centerContent = if (showCountdown && content == null) {
            OrbCenterContent.Countdown(secondsLeft)
        } else {
            OrbCenterContent.None
        },
        customContent = content,
    )
}

/**
 * Interactive selector component to choose between different breathing rhythm presets
 * (e.g., Box Breathing, 4-7-8 Technique, Slow Down, Equal Rhythm, Awaken).
 */
@Composable
fun BreathingPresetSelector(
    presets: List<Rhythm>,
    selectedRhythm: Rhythm,
    onSelectRhythm: (Rhythm) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LumyrinthThemeTokens.palette

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 4.dp),
    ) {
        items(presets.size) { index ->
            val preset = presets[index]
            val isSelected = preset.id == selectedRhythm.id
            val activeBorder = if (isSelected) palette.primaryAccent else palette.borderSubtle
            val activeBg = if (isSelected) palette.primaryAccent.copy(alpha = 0.20f) else palette.surfaceCard.copy(alpha = 0.50f)

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(activeBg)
                    .border(1.dp, activeBorder, RoundedCornerShape(16.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = LocalIndication.current,
                        role = Role.RadioButton,
                        onClick = { onSelectRhythm(preset) },
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = preset.name,
                        style = LumyrinthTypography.BodySm.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp,
                        ),
                        color = if (isSelected) palette.textPrimary else palette.textSecondary,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = preset.patternCode.replace(" Breathing", ""),
                        style = LumyrinthTypography.Label.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                        color = if (isSelected) palette.secondaryAccent else palette.textTertiary,
                    )
                }
            }
        }
    }
}

