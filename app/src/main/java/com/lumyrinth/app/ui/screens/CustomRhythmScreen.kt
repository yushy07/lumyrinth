package com.lumyrinth.app.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.lumyrinth.app.domain.Rhythm
import com.lumyrinth.app.ui.components.GhostButton
import com.lumyrinth.app.ui.components.PrimaryButton
import com.lumyrinth.app.ui.theme.LumyrinthColors
import com.lumyrinth.app.ui.theme.LumyrinthTypography
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun CustomRhythmScreen(
    initialRhythm: Rhythm? = null,
    onBack: () -> Unit,
    onSaveRhythm: (
        id: String?,
        name: String,
        inhale: Int,
        hold1: Int,
        exhale: Int,
        hold2: Int,
        durationMinutes: Int,
        soundDefault: Boolean,
        hapticsDefault: Boolean,
    ) -> Unit,
) {
    var inhaleSeconds by remember { mutableIntStateOf(initialRhythm?.inhaleSeconds ?: 4) }
    var hold1Seconds by remember { mutableIntStateOf(initialRhythm?.hold1Seconds ?: 2) }
    var exhaleSeconds by remember { mutableIntStateOf(initialRhythm?.exhaleSeconds ?: 6) }
    var hold2Seconds by remember { mutableIntStateOf(initialRhythm?.hold2Seconds ?: 0) }
    var durationMinutes by remember { mutableIntStateOf(initialRhythm?.defaultDurationMinutes ?: 5) }

    var showNameDialog by remember { mutableStateOf(false) }
    var rhythmName by remember { mutableStateOf(initialRhythm?.name ?: "") }
    var validationError by remember { mutableStateOf<String?>(null) }

    // Staggered screen entry animation
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 550, easing = FastOutSlowInEasing),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0714))
            .statusBarsPadding()
            .padding(horizontal = 20.dp),
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(animProgress.value)
                .offset { IntOffset(0, ((1f - animProgress.value) * 15).toInt()) },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF160E26)),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Scrollable content area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            // Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(animProgress.value)
                    .offset { IntOffset(0, ((1f - animProgress.value) * 25).toInt()) },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = if (initialRhythm != null) "Edit rhythm" else "Create your rhythm",
                    style = LumyrinthTypography.H1.copy(
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = LumyrinthColors.TextPrimary,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (initialRhythm != null) "Customize your breathing pattern." else "Build a breathing pattern that works for you.",
                    style = LumyrinthTypography.BodySm.copy(
                        fontSize = 14.sp,
                        color = Color(0xFF9E95B8),
                    ),
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4 Phase Stepper Cards
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(animProgress.value)
                    .offset { IntOffset(0, ((1f - animProgress.value) * 35).toInt()) },
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Inhale Phase Card (Purple/Indigo)
                PhaseStepperCard(
                    title = "Inhale",
                    value = inhaleSeconds,
                    onValueChange = { inhaleSeconds = it },
                    min = 1,
                    max = 30,
                    gradientColors = listOf(
                        Color(0xFF381A6E),
                        Color(0xFF231046),
                        Color(0xFF1B0D36),
                    ),
                    borderColor = Color(0x55A855F7),
                )

                // Hold Phase 1 Card (Magenta/Violet)
                PhaseStepperCard(
                    title = "Hold",
                    value = hold1Seconds,
                    onValueChange = { hold1Seconds = it },
                    min = 0,
                    max = 30,
                    gradientColors = listOf(
                        Color(0xFF5A1559),
                        Color(0xFF380C39),
                        Color(0xFF240726),
                    ),
                    borderColor = Color(0x55EC4899),
                )

                // Exhale Phase Card (Rose/Coral)
                PhaseStepperCard(
                    title = "Exhale",
                    value = exhaleSeconds,
                    onValueChange = { exhaleSeconds = it },
                    min = 1,
                    max = 30,
                    gradientColors = listOf(
                        Color(0xFF6B1B3C),
                        Color(0xFF451027),
                        Color(0xFF2B0918),
                    ),
                    borderColor = Color(0x55F43F5E),
                )

                // Hold Phase 2 Card (Warm Amber/Bronze)
                PhaseStepperCard(
                    title = "Hold",
                    value = hold2Seconds,
                    onValueChange = { hold2Seconds = it },
                    min = 0,
                    max = 30,
                    gradientColors = listOf(
                        Color(0xFF5E3416),
                        Color(0xFF3D210D),
                        Color(0xFF261407),
                    ),
                    borderColor = Color(0x55F59E0B),
                )

                // Duration Stepper Card (Dark Subtle Purple)
                DurationStepperCard(
                    value = durationMinutes,
                    onValueChange = { durationMinutes = it },
                    min = 1,
                    max = 60,
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Animated Live Waveform Preview Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(animProgress.value)
                    .offset { IntOffset(0, ((1f - animProgress.value) * 45).toInt()) },
            ) {
                LiveWaveformPreviewCard(
                    inhale = inhaleSeconds,
                    hold1 = hold1Seconds,
                    exhale = exhaleSeconds,
                    hold2 = hold2Seconds,
                )
            }

            if (validationError != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = validationError ?: "",
                    style = LumyrinthTypography.BodySm,
                    color = Color(0xFFF43F5E),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Bottom Action Button: Save Rhythm
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(animProgress.value)
                .offset { IntOffset(0, ((1f - animProgress.value) * 55).toInt()) }
                .padding(bottom = 16.dp),
        ) {
            PrimaryButton(
                label = "Save Rhythm",
                onClick = {
                    val totalSeconds = inhaleSeconds + hold1Seconds + exhaleSeconds + hold2Seconds
                    if (totalSeconds <= 0) {
                        validationError = "Please set at least one breathing phase greater than 0s."
                    } else {
                        validationError = null
                        showNameDialog = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    if (showNameDialog) {
        Dialog(onDismissRequest = { showNameDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF160E28))
                    .border(1.dp, Color(0x33A855F7), RoundedCornerShape(24.dp))
                    .padding(24.dp),
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Name your rhythm",
                        style = LumyrinthTypography.H2.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                        color = LumyrinthColors.TextPrimary,
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = rhythmName,
                        onValueChange = { rhythmName = it },
                        placeholder = { Text("e.g. My Evening Calm", style = LumyrinthTypography.Body, color = Color(0x66FFFFFF)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF0F081C),
                            unfocusedContainerColor = Color(0xFF0F081C),
                            focusedBorderColor = LumyrinthColors.AccentPurple,
                            unfocusedBorderColor = Color(0x22FFFFFF),
                            focusedTextColor = LumyrinthColors.TextPrimary,
                            unfocusedTextColor = LumyrinthColors.TextPrimary,
                        ),
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        GhostButton(
                            label = "Cancel",
                            onClick = { showNameDialog = false },
                            modifier = Modifier.weight(1f),
                        )

                        PrimaryButton(
                            label = "Save",
                            onClick = {
                                val finalName = rhythmName.trim().ifEmpty { "Custom Rhythm" }
                                showNameDialog = false
                                onSaveRhythm(
                                    initialRhythm?.id,
                                    finalName,
                                    inhaleSeconds,
                                    hold1Seconds,
                                    exhaleSeconds,
                                    hold2Seconds,
                                    durationMinutes,
                                    true,
                                    true,
                                )
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PhaseStepperCard(
    title: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    min: Int,
    max: Int,
    gradientColors: List<Color>,
    borderColor: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.horizontalGradient(gradientColors))
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            style = LumyrinthTypography.Body.copy(
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
            ),
            color = Color.White,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Minus button
            CircularStepperButton(
                icon = Icons.Rounded.Remove,
                contentDescription = "Decrease $title",
                enabled = value > min,
                onClick = { if (value > min) onValueChange(value - 1) },
            )

            // Number value
            Text(
                text = "$value",
                style = LumyrinthTypography.H2.copy(
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = Color.White,
                modifier = Modifier.width(20.dp),
                textAlign = TextAlign.Center,
            )

            // Plus button
            CircularStepperButton(
                icon = Icons.Rounded.Add,
                contentDescription = "Increase $title",
                enabled = value < max,
                onClick = { if (value < max) onValueChange(value + 1) },
            )
        }
    }
}

@Composable
private fun DurationStepperCard(
    value: Int,
    onValueChange: (Int) -> Unit,
    min: Int,
    max: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF1B122E),
                        Color(0xFF140D24),
                        Color(0xFF100A1F),
                    )
                )
            )
            .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(20.dp))
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "Duration",
            style = LumyrinthTypography.Body.copy(
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
            ),
            color = Color.White,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Minus button
            CircularStepperButton(
                icon = Icons.Rounded.Remove,
                contentDescription = "Decrease Duration",
                enabled = value > min,
                onClick = { if (value > min) onValueChange(value - 1) },
            )

            // Duration display with "min"
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    text = "$value",
                    style = LumyrinthTypography.H2.copy(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = Color.White,
                )
                Text(
                    text = "min",
                    style = LumyrinthTypography.BodySm.copy(
                        fontSize = 12.sp,
                        color = Color(0x99FFFFFF),
                    ),
                    modifier = Modifier.padding(bottom = 2.dp),
                )
            }

            // Plus button
            CircularStepperButton(
                icon = Icons.Rounded.Add,
                contentDescription = "Increase Duration",
                enabled = value < max,
                onClick = { if (value < max) onValueChange(value + 1) },
            )
        }
    }
}

@Composable
private fun CircularStepperButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.90f else 1.0f,
        label = "stepper_btn_scale",
    )

    Box(
        modifier = modifier
            .size(34.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(if (enabled) Color(0x33FFFFFF) else Color(0x11FFFFFF))
            .border(1.dp, if (enabled) Color(0x22FFFFFF) else Color(0x0AFFFFFF), CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                enabled = enabled,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (enabled) Color.White else Color(0x44FFFFFF),
            modifier = Modifier.size(16.dp),
        )
    }
}

@Composable
private fun LiveWaveformPreviewCard(
    inhale: Int,
    hold1: Int,
    exhale: Int,
    hold2: Int,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave_anim")
    val phaseOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "wave_phase_offset",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF160E26),
                        Color(0xFF120B20),
                        Color(0xFF150D24),
                    )
                )
            )
            .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(22.dp))
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Preview",
                style = LumyrinthTypography.BodySm.copy(
                    fontSize = 13.sp,
                    color = Color(0x99FFFFFF),
                ),
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Canvas rendering continuous dotted glowing sine wave
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
            ) {
                val w = size.width
                val h = size.height
                val midY = h / 2f
                val amplitude = h * 0.36f

                val totalSecs = (inhale + hold1 + exhale + hold2).coerceAtLeast(1).toFloat()
                // Number of cycles visible across the card
                val cycleCount = 2.2f

                val dotCount = 75
                for (i in 0..dotCount) {
                    val progress = i.toFloat() / dotCount.toFloat()
                    val x = progress * w

                    // Calculate phase angle based on proportion & animation phase
                    val angle = (progress * cycleCount * 2 * PI.toFloat()) - phaseOffset
                    val y = midY + amplitude * sin(angle)

                    // Determine vibrant color interpolation along wave
                    // Transition: Amber -> Coral/Orange -> Pink/Magenta -> Purple/Violet -> Lilac
                    val colorProgress = (progress + (phaseOffset / (2 * PI.toFloat()))) % 1f
                    val dotColor = when {
                        colorProgress < 0.25f -> {
                            val t = colorProgress / 0.25f
                            Color(
                                red = 1f,
                                green = 0.7f - 0.25f * t,
                                blue = 0.2f + 0.1f * t,
                                alpha = 0.95f,
                            )
                        }
                        colorProgress < 0.50f -> {
                            val t = (colorProgress - 0.25f) / 0.25f
                            Color(
                                red = 1f - 0.1f * t,
                                green = 0.45f - 0.2f * t,
                                blue = 0.3f + 0.35f * t,
                                alpha = 0.95f,
                            )
                        }
                        colorProgress < 0.75f -> {
                            val t = (colorProgress - 0.50f) / 0.25f
                            Color(
                                red = 0.9f - 0.3f * t,
                                green = 0.25f + 0.1f * t,
                                blue = 0.65f + 0.32f * t,
                                alpha = 0.95f,
                            )
                        }
                        else -> {
                            val t = (colorProgress - 0.75f) / 0.25f
                            Color(
                                red = 0.6f + 0.4f * t,
                                green = 0.35f + 0.35f * t,
                                blue = 0.97f - 0.77f * t,
                                alpha = 0.95f,
                            )
                        }
                    }

                    // Draw soft glow under dot
                    drawCircle(
                        color = dotColor.copy(alpha = 0.3f),
                        radius = 3.2f,
                        center = Offset(x, y),
                    )

                    // Draw crisp dot
                    drawCircle(
                        color = dotColor,
                        radius = 1.8f,
                        center = Offset(x, y),
                    )
                }
            }
        }
    }
}

