package com.lumyrinth.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Adjust
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.CenterFocusStrong
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material.icons.rounded.LocalCafe
import androidx.compose.material.icons.rounded.NightlightRound
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumyrinth.app.haptics.HapticController
import com.lumyrinth.app.ui.components.GhostButton
import com.lumyrinth.app.ui.components.GlowOrb
import com.lumyrinth.app.ui.components.IconCircleButton
import com.lumyrinth.app.ui.components.OrbAnimationState
import com.lumyrinth.app.ui.components.OrbCenterContent
import com.lumyrinth.app.ui.components.OrbSize
import com.lumyrinth.app.ui.components.PageIndicatorDots
import com.lumyrinth.app.ui.components.PreferenceCard
import com.lumyrinth.app.ui.components.PrimaryButton
import com.lumyrinth.app.ui.components.SelectableRow
import com.lumyrinth.app.ui.theme.LumyrinthColors
import com.lumyrinth.app.ui.theme.LumyrinthTypography
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LumyrinthColors.BgBase)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // LUMYRINTH wordmark
        Text(
            text = "L U M Y R I N T H",
            style = LumyrinthTypography.Wordmark,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.weight(0.7f))

        // Center Celestial GlowOrb
        GlowOrb(
            sizeVariant = OrbSize.Lg,
            centerContent = OrbCenterContent.None,
            animationState = OrbAnimationState.Idle(),
        )

        Spacer(modifier = Modifier.weight(0.6f))

        // Headline & Subtitle
        Text(
            text = "Find your rhythm.",
            style = LumyrinthTypography.H1,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Breathe. Focus. Unwind.",
            style = LumyrinthTypography.Body,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.weight(0.8f))

        // Bottom CTA & Page indicator
        PrimaryButton(
            label = "Get Started",
            onClick = onGetStarted,
            backgroundBrush = LumyrinthColors.GradientButton,
        )

        Spacer(modifier = Modifier.height(20.dp))

        PageIndicatorDots(
            total = 4,
            activeIndex = 0,
        )

        Spacer(modifier = Modifier.height(12.dp))
    }
}

data class GoalOption(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val accentColor: Color,
)

@Composable
fun GoalsScreen(
    initialGoals: Set<String>,
    onBack: () -> Unit,
    onNext: (Set<String>) -> Unit,
) {
    // Default selection to match the visual mock if empty
    val defaultInitial = if (initialGoals.isEmpty()) {
        setOf("relax", "focus", "unwind", "build_habit")
    } else {
        initialGoals
    }
    var selectedGoals by remember { mutableStateOf(defaultInitial) }

    val goalsList = listOf(
        GoalOption("relax", "Relax", Icons.Rounded.Spa, Color(0xFFF43F5E)),
        GoalOption("focus", "Focus", Icons.Rounded.CenterFocusStrong, Color(0xFFFB7185)),
        GoalOption("unwind", "Unwind", Icons.Rounded.NightlightRound, Color(0xFFA855F7)),
        GoalOption("sleep", "Sleep", Icons.Rounded.Bedtime, Color(0xFFC084FC)),
        GoalOption("break", "Take a quick break", Icons.Rounded.Coffee, Color(0xFFEC4899)),
        GoalOption("build_habit", "Build a habit", Icons.Rounded.Eco, Color(0xFF4ADE80)),
    )

    // Staggered entrance animation
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LumyrinthColors.BgBase)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        // Top Back arrow button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconCircleButton(
                icon = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Back",
                onClick = onBack,
                size = 40.dp,
                iconSize = 20.dp,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Centered Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(animProgress.value)
                .offset { IntOffset(0, ((1f - animProgress.value) * 30).toInt()) },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "What are you\nlooking for?",
                style = LumyrinthTypography.H1.copy(
                    fontSize = 26.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = LumyrinthColors.TextPrimary,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Choose what matters most to you.",
                style = LumyrinthTypography.Body.copy(
                    fontSize = 14.sp,
                    color = LumyrinthColors.TextSecondary,
                ),
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Goals List with staggered animations
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            goalsList.forEachIndexed { index, goal ->
                val isSelected = goal.id in selectedGoals

                // Staggered calculation for each item
                val itemDelayProgress = (animProgress.value * 1.4f - (index * 0.08f)).coerceIn(0f, 1f)

                GoalItemCard(
                    goal = goal,
                    isSelected = isSelected,
                    onToggle = {
                        selectedGoals = if (isSelected) {
                            selectedGoals - goal.id
                        } else {
                            selectedGoals + goal.id
                        }
                    },
                    modifier = Modifier
                        .alpha(itemDelayProgress)
                        .offset { IntOffset(0, ((1f - itemDelayProgress) * 40).toInt()) },
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom Next Button
        PrimaryButton(
            label = "Next",
            onClick = { onNext(selectedGoals) },
            enabled = selectedGoals.isNotEmpty(),
            backgroundBrush = LumyrinthColors.GradientButton,
            modifier = Modifier
                .fillMaxWidth()
                .alpha(animProgress.value)
                .offset { IntOffset(0, ((1f - animProgress.value) * 20).toInt()) },
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun GoalItemCard(
    goal: GoalOption,
    isSelected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.975f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "goal_card_scale",
    )

    val cardBgColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF16122C) else Color(0xFF110E22),
        animationSpec = tween(durationMillis = 200),
        label = "goal_card_bg",
    )

    val cardBorderColor by animateColorAsState(
        targetValue = if (isSelected) Color(0x409333EA) else Color(0x1AFFFFFF),
        animationSpec = tween(durationMillis = 200),
        label = "goal_card_border",
    )

    val checkboxBgColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF9333EA) else Color(0x1F2D2545),
        animationSpec = tween(durationMillis = 200),
        label = "checkbox_bg",
    )

    val checkboxBorderColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF9333EA) else Color(0x336D5B8D),
        animationSpec = tween(durationMillis = 200),
        label = "checkbox_border",
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(cardBgColor)
            .border(1.dp, cardBorderColor, RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Checkbox,
                onClick = onToggle,
            )
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Neon-accented Outline Icon
        Box(
            modifier = Modifier.size(36.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = goal.icon,
                contentDescription = null,
                tint = goal.accentColor,
                modifier = Modifier.size(24.dp),
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Goal Title
        Text(
            text = goal.label,
            style = LumyrinthTypography.H3.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            ),
            color = LumyrinthColors.TextPrimary,
            modifier = Modifier.weight(1f),
        )

        // Squircle Rounded Checkbox
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(checkboxBgColor)
                .border(1.5.dp, checkboxBorderColor, RoundedCornerShape(7.dp)),
            contentAlignment = Alignment.Center,
        ) {
            androidx.compose.animation.AnimatedVisibility(
                visible = isSelected,
                enter = scaleIn(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium,
                    )
                ) + fadeIn(tween(150)),
                exit = scaleOut(tween(100)) + fadeOut(tween(100)),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier.size(17.dp),
                )
            }
        }
    }
}

@Composable
fun PreferencesScreen(
    initialHaptics: Boolean,
    initialSound: Boolean,
    onBack: () -> Unit,
    onNext: (haptics: Boolean, sound: Boolean) -> Unit,
) {
    val context = LocalContext.current
    val hapticController = remember(context) { HapticController(context) }

    var hapticsEnabled by remember { mutableStateOf(initialHaptics) }
    var soundEnabled by remember { mutableStateOf(initialSound) }

    // Staggered entrance animation
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LumyrinthColors.BgBase)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        // Back navigation button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconCircleButton(
                icon = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Back",
                onClick = onBack,
                size = 40.dp,
                iconSize = 20.dp,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Centered Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(animProgress.value)
                .offset { IntOffset(0, ((1f - animProgress.value) * 30).toInt()) },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Set your preferences",
                style = LumyrinthTypography.H1.copy(
                    fontSize = 26.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = LumyrinthColors.TextPrimary,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "You can change these anytime.",
                style = LumyrinthTypography.Body.copy(
                    fontSize = 14.sp,
                    color = LumyrinthColors.TextSecondary,
                ),
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Cards Column
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // 1. Haptic guidance card
            val hapticDelayProgress = (animProgress.value * 1.3f - 0.05f).coerceIn(0f, 1f)
            HapticPreferenceCard(
                checked = hapticsEnabled,
                onCheckedChange = {
                    hapticsEnabled = it
                    hapticController.tick()
                },
                modifier = Modifier
                    .alpha(hapticDelayProgress)
                    .offset { IntOffset(0, ((1f - hapticDelayProgress) * 40).toInt()) },
            )

            // 2. Sound guidance card
            val soundDelayProgress = (animProgress.value * 1.3f - 0.15f).coerceIn(0f, 1f)
            SoundPreferenceCard(
                checked = soundEnabled,
                onCheckedChange = {
                    soundEnabled = it
                    hapticController.tick()
                },
                modifier = Modifier
                    .alpha(soundDelayProgress)
                    .offset { IntOffset(0, ((1f - soundDelayProgress) * 40).toInt()) },
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom Next Button
        PrimaryButton(
            label = "Next",
            onClick = { onNext(hapticsEnabled, soundEnabled) },
            backgroundBrush = LumyrinthColors.GradientButton,
            modifier = Modifier
                .fillMaxWidth()
                .alpha(animProgress.value)
                .offset { IntOffset(0, ((1f - animProgress.value) * 20).toInt()) },
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun HapticPreferenceCard(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val cardBg = Brush.verticalGradient(
        listOf(
            Color(0xFF28114C),
            Color(0xFF140D2A),
            Color(0xFF190C28),
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(cardBg)
            .border(
                1.dp,
                Brush.verticalGradient(
                    listOf(
                        Color(0x66A855F7),
                        Color(0x26FF5277),
                    )
                ),
                RoundedCornerShape(24.dp),
            )
            .padding(horizontal = 20.dp, vertical = 18.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row: Text + Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                    Text(
                        text = "Haptic guidance",
                        style = LumyrinthTypography.H3.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                        color = LumyrinthColors.TextPrimary,
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Feel inhale and exhale\nwith gentle vibrations.",
                        style = LumyrinthTypography.Body.copy(
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = LumyrinthColors.TextSecondary,
                        ),
                    )
                }

                PurplePillSwitch(
                    checked = checked,
                    onCheckedChange = onCheckedChange,
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Animated Dotted Waveform
            AnimatedDottedWaveform(
                enabled = checked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
            )
        }
    }
}

@Composable
fun SoundPreferenceCard(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val cardBg = Brush.verticalGradient(
        listOf(
            Color(0xFF28114C),
            Color(0xFF140D2A),
            Color(0xFF190C28),
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(cardBg)
            .border(
                1.dp,
                Brush.verticalGradient(
                    listOf(
                        Color(0x66A855F7),
                        Color(0x26FF5277),
                    )
                ),
                RoundedCornerShape(24.dp),
            )
            .padding(horizontal = 20.dp, vertical = 18.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row: Text + Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                    Text(
                        text = "Sound guidance",
                        style = LumyrinthTypography.H3.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                        color = LumyrinthColors.TextPrimary,
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Play subtle sounds during\nyour sessions.",
                        style = LumyrinthTypography.Body.copy(
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = LumyrinthColors.TextSecondary,
                        ),
                    )
                }

                PurplePillSwitch(
                    checked = checked,
                    onCheckedChange = onCheckedChange,
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Animated Equalizer Audio Spectrum
            AnimatedAudioEqualizerSpectrum(
                enabled = checked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
            )
        }
    }
}

@Composable
fun PurplePillSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 22.dp else 3.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "thumb_offset",
    )

    val trackBgColor by animateColorAsState(
        targetValue = if (checked) Color(0xFF9333EA) else Color(0xFF261D3B),
        animationSpec = tween(durationMillis = 200),
        label = "switch_track_bg",
    )

    val trackBorderColor by animateColorAsState(
        targetValue = if (checked) Color(0xFFA855F7) else Color(0x33FFFFFF),
        animationSpec = tween(durationMillis = 200),
        label = "switch_track_border",
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        label = "switch_scale",
    )

    Box(
        modifier = modifier
            .scale(scale)
            .width(48.dp)
            .height(28.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(trackBgColor)
            .border(1.dp, trackBorderColor, RoundedCornerShape(999.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Switch,
                onClick = { onCheckedChange(!checked) },
            )
            .padding(vertical = 3.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(22.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}

@Composable
fun AnimatedDottedWaveform(
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "haptic_wave")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "wave_phase",
    )

    val targetAlpha = if (enabled) 1.0f else 0.25f
    val animatedAlpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = 300),
        label = "wave_alpha",
    )

    val targetAmplitude = if (enabled) 1.0f else 0.2f
    val animatedAmplitude by animateFloatAsState(
        targetValue = targetAmplitude,
        animationSpec = tween(durationMillis = 300),
        label = "wave_amp",
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val midY = height / 2f

        val totalDots = 42
        val step = width / (totalDots - 1)

        // Draw flowing dotted wave
        for (i in 0 until totalDots) {
            val x = i * step
            val normX = (x / width).coerceIn(0f, 1f)
            // Gaussian envelope so it tapers at left and right edges
            val envelope = sin(normX * PI).toFloat()

            // Primary wave
            val primaryAngle = (normX * 3.2f * PI + phase).toDouble()
            val yOffset1 = (sin(primaryAngle) * (height * 0.36f) * envelope * animatedAmplitude).toFloat()
            val y1 = midY + yOffset1

            // Secondary harmonic counter-wave
            val secondaryAngle = (normX * 4.5f * PI - phase * 0.8f).toDouble()
            val yOffset2 = (cos(secondaryAngle) * (height * 0.22f) * envelope * animatedAmplitude).toFloat()
            val y2 = midY + yOffset2

            val dotRadius = (1.4f + (envelope * 0.8f))
            val dotAlpha = ((0.45f + (envelope * 0.55f)) * animatedAlpha).coerceIn(0f, 1f)

            val color1 = when {
                i % 3 == 0 -> Color(0xFFF43F5E) // Pink
                i % 3 == 1 -> Color(0xFFE879F9) // Magenta
                else -> Color(0xFFC084FC) // Lavender
            }

            // Draw primary dot
            drawCircle(
                color = color1.copy(alpha = dotAlpha),
                radius = dotRadius,
                center = Offset(x, y1),
            )

            // Draw secondary subtle counter-dot
            if (i % 2 == 0) {
                drawCircle(
                    color = Color(0xFFA855F7).copy(alpha = dotAlpha * 0.5f),
                    radius = dotRadius * 0.8f,
                    center = Offset(x, y2),
                )
            }

            // Glowing focal star node near center crest (around index 20-22)
            if (i == totalDots / 2) {
                // Radial glow halo
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFF5277).copy(alpha = 0.65f * animatedAlpha),
                            Color(0xFFD946EF).copy(alpha = 0.35f * animatedAlpha),
                            Color.Transparent,
                        ),
                        center = Offset(x, y1),
                        radius = 16f,
                    ),
                    radius = 16f,
                    center = Offset(x, y1),
                )

                // White/pink bright central star particle
                drawCircle(
                    color = Color.White.copy(alpha = animatedAlpha),
                    radius = 3.2f,
                    center = Offset(x, y1),
                )
            }
        }
    }
}

@Composable
fun AnimatedAudioEqualizerSpectrum(
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sound_spectrum")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "spectrum_time",
    )

    val targetAlpha = if (enabled) 1.0f else 0.25f
    val animatedAlpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = 300),
        label = "spectrum_alpha",
    )

    val targetActivity = if (enabled) 1.0f else 0.15f
    val animatedActivity by animateFloatAsState(
        targetValue = targetActivity,
        animationSpec = tween(durationMillis = 300),
        label = "spectrum_act",
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val midY = height / 2f

        val barCount = 38
        val barWidth = 3.2.dp.toPx()
        val spacing = (width - (barCount * barWidth)) / (barCount - 1)

        for (i in 0 until barCount) {
            val x = i * (barWidth + spacing)
            val normX = (i.toFloat() / (barCount - 1)).coerceIn(0f, 1f)

            // Bell-curve shape (tall in center, tapering to outer edges)
            val distFromCenter = (normX - 0.5f) * 2f // -1.0 to 1.0
            val bellCurve = exp(-3.2 * distFromCenter * distFromCenter).toFloat()

            // Dynamic oscillator per bar
            val osc1 = sin(time * 3.0 + i * 0.45).toFloat()
            val osc2 = cos(time * 2.0 - i * 0.35).toFloat()
            val dynamicFactor = 0.55f + (0.30f * osc1) + (0.15f * osc2)

            val maxBarHeight = height * 0.82f
            val calculatedHeight = (maxBarHeight * bellCurve * dynamicFactor * animatedActivity)
                .coerceAtLeast(3.dp.toPx())

            val barTop = midY - (calculatedHeight / 2f)

            val barBrush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFF472B6).copy(alpha = animatedAlpha),
                    Color(0xFFD946EF).copy(alpha = animatedAlpha * 0.95f),
                    Color(0xFF9333EA).copy(alpha = animatedAlpha * 0.85f),
                ),
                startY = barTop,
                endY = barTop + calculatedHeight,
            )

            drawRoundRect(
                brush = barBrush,
                topLeft = Offset(x, barTop),
                size = Size(barWidth, calculatedHeight),
                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f),
            )
        }
    }
}

@Composable
fun FirstSessionScreen(
    onBeginSession: () -> Unit,
    onExploreFirst: () -> Unit,
) {
    // Staggered entrance animation
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LumyrinthColors.BgBase)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Centered Header & Subtitle
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(animProgress.value)
                .offset { IntOffset(0, ((1f - animProgress.value) * 30).toInt()) },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Ready for your\nfirst session?",
                style = LumyrinthTypography.H1.copy(
                    fontSize = 26.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = LumyrinthColors.TextPrimary,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Start with a 1-minute session\nand feel the difference.",
                style = LumyrinthTypography.Body.copy(
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = LumyrinthColors.TextSecondary,
                ),
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.weight(0.9f))

        // Center Cosmic GlowOrb with satellite star nodes
        Box(
            modifier = Modifier
                .alpha(animProgress.value)
                .scale(0.85f + 0.15f * animProgress.value),
            contentAlignment = Alignment.Center,
        ) {
            GlowOrb(
                sizeVariant = OrbSize.Lg,
                centerContent = OrbCenterContent.None,
                animationState = OrbAnimationState.Idle(),
            )
        }

        Spacer(modifier = Modifier.weight(1.1f))

        // Action Buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(animProgress.value)
                .offset { IntOffset(0, ((1f - animProgress.value) * 20).toInt()) },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PrimaryButton(
                label = "Begin 1-Minute Session",
                onClick = onBeginSession,
                backgroundBrush = LumyrinthColors.GradientButton,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            GhostButton(
                label = "Explore First",
                onClick = onExploreFirst,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
