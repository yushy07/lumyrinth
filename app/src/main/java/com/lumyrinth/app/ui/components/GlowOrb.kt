package com.lumyrinth.app.ui.components

import android.provider.Settings
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lumyrinth.app.domain.BreathPhase
import com.lumyrinth.app.ui.theme.LumyrinthColors
import com.lumyrinth.app.ui.theme.LumyrinthTypography
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** Standard Easings from ANIMATIONS.md spec **/
val InhaleEasing = CubicBezierEasing(0.45f, 0.0f, 0.55f, 1.0f)
val ExhaleEasing = CubicBezierEasing(0.55f, 0.0f, 0.45f, 1.0f)

enum class OrbSize(val dp: Dp) {
    Sm(140.dp),
    Md(200.dp),
    Lg(260.dp),
    Xl(300.dp),
}

sealed class OrbCenterContent {
    data object None : OrbCenterContent()
    data class Countdown(val seconds: Int) : OrbCenterContent()
    data object Checkmark : OrbCenterContent()
}

sealed class OrbAnimationState {
    data class Idle(val isPaused: Boolean = false) : OrbAnimationState()
    data class Breathing(
        val scale: Float, // Direct scale value (e.g. 0.75f to 1.0f + wobble)
        val phase: BreathPhase?,
        val isPaused: Boolean = false,
    ) : OrbAnimationState()
    data class Complete(val isReducedMotion: Boolean = false) : OrbAnimationState()
}

@Composable
fun rememberIsReducedMotion(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        try {
            val resolver = context.contentResolver
            val durationScale = Settings.Global.getFloat(
                resolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1.0f,
            )
            val transitionScale = Settings.Global.getFloat(
                resolver,
                Settings.Global.TRANSITION_ANIMATION_SCALE,
                1.0f,
            )
            durationScale == 0f || transitionScale == 0f
        } catch (_: Throwable) {
            false
        }
    }
}

@Composable
fun GlowOrb(
    modifier: Modifier = Modifier,
    sizeVariant: OrbSize = OrbSize.Md,
    centerContent: OrbCenterContent = OrbCenterContent.None,
    animationState: OrbAnimationState = OrbAnimationState.Idle(),
) {
    val isReducedMotion = rememberIsReducedMotion()
    val infiniteTransition = rememberInfiniteTransition(label = "orb_ambient")

    // 1. Idle scale pulse: 0.98 -> 1.02 -> 0.98 over 4000ms
    val idleScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "idle_scale",
    )

    // 2. Idle glow opacity: 0.75 -> 1.0 -> 0.75 in sync with 4s cycle
    val idleGlowOpacity by infiniteTransition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "idle_glow",
    )

    // 3. Slow continuous ring rotation: ~96s per 360 rotation
    val continuousRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 96_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "ring_rotation",
    )

    // Secondary subtle counter-rotation for outer celestial layer
    val counterRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 140_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "counter_rotation",
    )

    // Paused rotation tracker to freeze rotation angle on pause
    val isSessionPaused = when (animationState) {
        is OrbAnimationState.Idle -> animationState.isPaused
        is OrbAnimationState.Breathing -> animationState.isPaused
        is OrbAnimationState.Complete -> false
    }

    val activeRotation = if (isReducedMotion) {
        0f
    } else if (isSessionPaused) {
        remember { continuousRotation }
    } else {
        continuousRotation
    }

    val activeCounterRotation = if (isReducedMotion) {
        0f
    } else if (isSessionPaused) {
        remember { counterRotation }
    } else {
        counterRotation
    }

    // 4. Determine Effective Scale & Glow Multiplier
    val currentScale: Float
    val glowIntensity: Float

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

    // 5. Checkmark Entrance Animation for Complete Screen
    val checkmarkAlpha = remember { Animatable(0f) }
    val checkmarkScale = remember { Animatable(0.8f) }

    LaunchedEffect(centerContent, animationState) {
        if (centerContent is OrbCenterContent.Checkmark) {
            checkmarkAlpha.snapTo(0f)
            checkmarkScale.snapTo(0.8f)
            delay(350L)
            if (isReducedMotion) {
                checkmarkAlpha.animateTo(1.0f, tween(250))
                checkmarkScale.animateTo(1.0f, tween(250))
            } else {
                checkmarkAlpha.animateTo(1.0f, tween(400, easing = FastOutSlowInEasing))
                checkmarkScale.animateTo(
                    targetValue = 1.05f,
                    animationSpec = tween(280, easing = FastOutSlowInEasing),
                )
                checkmarkScale.animateTo(
                    targetValue = 1.0f,
                    animationSpec = tween(120, easing = FastOutSlowInEasing),
                )
            }
        }
    }

    Box(
        modifier = modifier
            .size(sizeVariant.dp)
            .scale(currentScale),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(sizeVariant.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = size.minDimension / 2f

            // 1. Broad Outer Atmospheric Glow
            val broadGlow = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFB535C2).copy(alpha = 0.35f * glowIntensity),
                    Color(0xFF6B21A8).copy(alpha = 0.22f * glowIntensity),
                    Color(0xFF3B0764).copy(alpha = 0.12f * glowIntensity),
                    Color.Transparent,
                ),
                center = center,
                radius = baseRadius * 0.98f,
            )
            drawCircle(
                brush = broadGlow,
                radius = baseRadius * 0.98f,
                center = center,
            )

            // Inner warm magenta/amber coronal flare
            val coronaGlow = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFF5277).copy(alpha = 0.55f * glowIntensity),
                    Color(0xFFD946EF).copy(alpha = 0.35f * glowIntensity),
                    Color(0xFF8B5CF6).copy(alpha = 0.18f * glowIntensity),
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

            // 2. Concentric Astrolabe Celestial Orbit Rings (6 layered rings)
            val ringRadii = listOf(
                baseRadius * 0.44f, // Ring 1: Prominent luminous inner orb ring surrounding countdown
                baseRadius * 0.56f, // Ring 2: Core constellation ring
                baseRadius * 0.68f, // Ring 3: Mid luminous ring
                baseRadius * 0.80f, // Ring 4: Secondary orbital ring
                baseRadius * 0.92f, // Ring 5: Outer celestial perimeter
            )
            val ringOpacities = listOf(0.95f, 0.75f, 0.55f, 0.38f, 0.22f)

            rotate(degrees = activeRotation, pivot = center) {
                // Outer floating stardust & satellite nodes
                val satelliteNodes = listOf(
                    Triple(0.88f, -0.65f, Color(0xFFF43F5E)), // Top-right pink star
                    Triple(0.45f, -0.85f, Color(0xFFE879F9)), // Top lavender star
                    Triple(0.92f, 0.20f, Color(0xFFD946EF)),  // Right magenta star
                    Triple(-0.85f, 0.45f, Color(0xFFF97316)), // Lower-left amber star
                    Triple(0.70f, 0.70f, Color(0xFFA855F7)),  // Lower-right purple star
                    Triple(-0.40f, 0.88f, Color(0xFFFB7185)), // Bottom rose star
                    Triple(-0.75f, -0.50f, Color(0xFFC084FC)), // Upper-left violet star
                )

                satelliteNodes.forEachIndexed { sIndex, (xRel, yRel, nodeColor) ->
                    val satRadius = baseRadius * 0.95f
                    val satX = center.x + (xRel * satRadius)
                    val satY = center.y + (yRel * satRadius)
                    val nodePulse = (0.75f + 0.25f * sin(activeRotation * (PI / 90) + sIndex)).toFloat()

                    // Glow halo around floating star node
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

                    // Bright core
                    drawCircle(
                        color = Color.White.copy(alpha = 0.85f * glowIntensity * nodePulse),
                        radius = 2.4f,
                        center = Offset(satX, satY),
                    )
                }

                // Render each orbit layer with sparkling star nodes
                for (i in ringRadii.indices) {
                    val radius = ringRadii[i]
                    val ringAlpha = ringOpacities[i] * glowIntensity
                    val numDots = 32 + (i * 12)

                    // Continuous track line (Inner ring is extra vibrant solid/glow neon)
                    if (i == 0) {
                        // Neon glow stroke around inner ring
                        drawCircle(
                            brush = Brush.sweepGradient(
                                listOf(
                                    Color(0xFFEC4899),
                                    Color(0xFFA855F7),
                                    Color(0xFFE879F9),
                                    Color(0xFFF43F5E),
                                    Color(0xFFEC4899),
                                )
                            ),
                            radius = radius,
                            center = center,
                            style = Stroke(width = 3.2f),
                            alpha = (0.92f * glowIntensity).coerceIn(0f, 1f),
                        )
                        // Soft glow halo for inner ring
                        drawCircle(
                            color = Color(0xFFD946EF).copy(alpha = 0.35f * glowIntensity),
                            radius = radius,
                            center = center,
                            style = Stroke(width = 8f),
                        )
                    } else {
                        drawCircle(
                            color = Color(0xFFA855F7).copy(alpha = ringAlpha * 0.25f),
                            radius = radius,
                            center = center,
                            style = Stroke(
                                width = 0.8f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(3f, 6f), 0f),
                            ),
                        )
                    }

                    // Star dots on the ring
                    for (d in 0 until numDots) {
                        val angle = (d.toDouble() / numDots) * 2 * PI
                        val x = center.x + (radius * cos(angle)).toFloat()
                        val y = center.y + (radius * sin(angle)).toFloat()

                        // Major celestial cardinal nodes
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

                        // Glow around major star nodes
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

            // 3. Bright Center Core (Radiant Star / Sun)
            if (centerContent is OrbCenterContent.None) {
                // Outer bright corona
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

                // White-hot star center
                drawCircle(
                    color = Color.White,
                    radius = baseRadius * 0.085f,
                    center = center,
                )

                // Subtle diamond flare sparkle at center
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
                // Dimmer ambient center glow behind countdown or checkmark
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

        // 4. Center Content (Countdown or Checkmark)
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
                    label = "countdown_transition",
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
