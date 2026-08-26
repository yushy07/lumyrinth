package com.lumyrinth.app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.LocalIndication
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
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.navigationBarsPadding
import com.lumyrinth.app.ui.components.CosmicSectionBackground
import com.lumyrinth.app.ui.components.SectionTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumyrinth.app.domain.Rhythm
import com.lumyrinth.app.ui.components.BreathingCircle
import com.lumyrinth.app.ui.components.OrbAnimationState
import com.lumyrinth.app.ui.components.PrimaryButton
import com.lumyrinth.app.ui.theme.LumyrinthColors
import com.lumyrinth.app.ui.theme.LumyrinthTypography

@Composable
fun DetailAstrolabeHero(
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 86.dp,
) {
    BreathingCircle(
        modifier = modifier,
        circleSize = size,
        animationState = OrbAnimationState.Idle(),
    )
}

@Composable
fun DetailScreen(
    rhythm: Rhythm,
    isFavorite: Boolean,
    defaultSound: Boolean,
    defaultHaptics: Boolean,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onBeginSession: (rhythm: Rhythm, durationMinutes: Int, soundOn: Boolean, hapticsOn: Boolean) -> Unit,
) {
    // Available durations: default [1, 3, 5, 10]
    val durationOptions = listOf(1, 3, 5, 10)
    var selectedDurationMinutes by remember {
        mutableIntStateOf(
            if (durationOptions.contains(rhythm.defaultDurationMinutes)) rhythm.defaultDurationMinutes else 5
        )
    }
    var soundOn by remember { mutableStateOf(defaultSound) }
    var hapticsOn by remember { mutableStateOf(defaultHaptics) }

    // Staggered screen entry animation
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 550, easing = FastOutSlowInEasing),
        )
    }

    val cardBg = Brush.horizontalGradient(
        listOf(
            LumyrinthColors.SurfaceCard,
            LumyrinthColors.BgElevated,
            LumyrinthColors.SurfaceCard,
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        CosmicSectionBackground(theme = SectionTheme.DETAIL)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp),
        ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Top App Bar: Back Arrow and Favorite Icon
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(animProgress.value)
                .offset { IntOffset(0, ((1f - animProgress.value) * 15).toInt()) },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(LumyrinthColors.SurfaceCard),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = LumyrinthColors.TextPrimary,
                    modifier = Modifier.size(20.dp),
                )
            }

            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(LumyrinthColors.SurfaceCard),
            ) {
                val favTint by animateColorAsState(
                    targetValue = if (isFavorite) LumyrinthColors.AccentSuccess else LumyrinthColors.TextPrimary,
                    label = "fav_tint",
                )
                Icon(
                    imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                    contentDescription = if (isFavorite) "Remove favorite" else "Add favorite",
                    tint = favTint,
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
            // Header Info & Glowing Astrolabe Hero
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(animProgress.value)
                    .offset { IntOffset(0, ((1f - animProgress.value) * 25).toInt()) },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp)
                ) {
                    Text(
                        text = rhythm.name,
                        style = LumyrinthTypography.H1.copy(
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                        color = LumyrinthColors.TextPrimary,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = rhythm.shortDescription,
                        style = LumyrinthTypography.BodySm.copy(
                            fontSize = 13.sp,
                            color = LumyrinthColors.TextSecondary,
                            lineHeight = 18.sp,
                        ),
                    )
                }

                DetailAstrolabeHero(
                    size = 86.dp,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section: Pattern
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(animProgress.value)
                    .offset { IntOffset(0, ((1f - animProgress.value) * 35).toInt()) }
            ) {
                Text(
                    text = "Pattern",
                    style = LumyrinthTypography.H3.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    color = LumyrinthColors.TextPrimary,
                )

                Spacer(modifier = Modifier.height(10.dp))

                val phases = listOf(
                    Triple("Inhale", "${rhythm.inhaleSeconds} sec", LumyrinthColors.PhaseInhale),
                    Triple("Hold", "${rhythm.hold1Seconds} sec", LumyrinthColors.PhaseHold1),
                    Triple("Exhale", "${rhythm.exhaleSeconds} sec", LumyrinthColors.PhaseExhale),
                    Triple("Hold", "${rhythm.hold2Seconds} sec", LumyrinthColors.PhaseHold2),
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(cardBg)
                        .border(1.dp, LumyrinthColors.BorderSubtle, RoundedCornerShape(22.dp))
                        .padding(horizontal = 18.dp, vertical = 6.dp),
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        phases.forEachIndexed { index, (label, duration, dotColor) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Glowing dot
                                    Box(
                                        modifier = Modifier
                                            .size(9.dp)
                                            .clip(CircleShape)
                                            .background(dotColor)
                                    )
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Text(
                                        text = label,
                                        style = LumyrinthTypography.Body.copy(
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Normal,
                                        ),
                                        color = LumyrinthColors.TextPrimary,
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    Text(
                                        text = duration,
                                        style = LumyrinthTypography.BodySm.copy(
                                            fontSize = 13.sp,
                                            color = LumyrinthColors.TextSecondary,
                                        ),
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                                        contentDescription = null,
                                        tint = LumyrinthColors.TextTertiary,
                                        modifier = Modifier.size(16.dp),
                                    )
                                }
                            }

                            if (index < phases.lastIndex) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(LumyrinthColors.BorderSubtle)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Section: Duration
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(animProgress.value)
                    .offset { IntOffset(0, ((1f - animProgress.value) * 45).toInt()) }
            ) {
                Text(
                    text = "Duration",
                    style = LumyrinthTypography.H3.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    color = LumyrinthColors.TextPrimary,
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    durationOptions.forEach { mins ->
                        val isSelected = selectedDurationMinutes == mins
                        val interactionSource = remember { MutableInteractionSource() }
                        val isPressed by interactionSource.collectIsPressedAsState()
                        val chipScale by animateFloatAsState(
                            targetValue = if (isPressed) 0.94f else 1.0f,
                            label = "chip_scale_$mins",
                        )

                        val bgBrush = if (isSelected) {
                            Brush.horizontalGradient(
                                listOf(
                                    LumyrinthColors.AccentSuccess,
                                    LumyrinthColors.AccentSuccess,
                                )
                            )
                        } else {
                            Brush.verticalGradient(
                                listOf(
                                    LumyrinthColors.SurfaceCard,
                                    LumyrinthColors.BgElevated,
                                )
                            )
                        }

                        val borderColor = if (isSelected) LumyrinthColors.AccentSuccess else LumyrinthColors.BorderSubtle

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .scale(chipScale)
                                .clip(RoundedCornerShape(999.dp))
                                .background(bgBrush)
                                .border(1.dp, borderColor, RoundedCornerShape(999.dp))
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = LocalIndication.current,
                                    role = Role.Button,
                                    onClick = { selectedDurationMinutes = mins },
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "$mins min",
                                style = LumyrinthTypography.BodySm.copy(
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                ),
                                color = if (isSelected) Color.White else LumyrinthColors.TextSecondary,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section: Sound & Haptics Preferences Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(animProgress.value)
                    .offset { IntOffset(0, ((1f - animProgress.value) * 55).toInt()) }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(cardBg)
                        .border(1.dp, LumyrinthColors.BorderSubtle, RoundedCornerShape(22.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Sound Row
                        PreferenceToggleRow(
                                icon = Icons.AutoMirrored.Rounded.VolumeUp,
                            title = "Sound",
                            isOn = soundOn,
                            onToggle = { soundOn = !soundOn },
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(LumyrinthColors.BorderSubtle)
                        )

                        // Haptics Row
                        PreferenceToggleRow(
                            icon = Icons.Rounded.Vibration,
                            title = "Haptics",
                            isOn = hapticsOn,
                            onToggle = { hapticsOn = !hapticsOn },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Bottom Action Button: Begin Session
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(animProgress.value)
                .offset { IntOffset(0, ((1f - animProgress.value) * 65).toInt()) }
                .navigationBarsPadding()
                .padding(bottom = 16.dp)
        ) {
            PrimaryButton(
                label = "Begin Session",
                onClick = {
                    onBeginSession(rhythm, selectedDurationMinutes, soundOn, hapticsOn)
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
}

@Composable
private fun PreferenceToggleRow(
    icon: ImageVector,
    title: String,
    isOn: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val rowScale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1.0f,
        label = "pref_row_scale",
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .scale(rowScale)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                role = Role.Button,
                onClick = onToggle,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Neon glyph badge
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(LumyrinthColors.SurfaceCardAlt)
                    .border(1.dp, LumyrinthColors.BorderSubtle, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = LumyrinthColors.AccentPurple,
                    modifier = Modifier.size(17.dp),
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                style = LumyrinthTypography.Body.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                ),
                color = LumyrinthColors.TextPrimary,
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = if (isOn) "On" else "Off",
                style = LumyrinthTypography.BodySm.copy(
                    fontSize = 13.sp,
                    color = if (isOn) LumyrinthColors.TextSecondary else LumyrinthColors.TextTertiary,
                ),
            )
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = LumyrinthColors.TextTertiary,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

