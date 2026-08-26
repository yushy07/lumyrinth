package com.lumyrinth.app.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.CenterFocusStrong
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumyrinth.app.domain.PresetRhythms
import com.lumyrinth.app.domain.ProgressSummary
import com.lumyrinth.app.domain.Rhythm
import com.lumyrinth.app.domain.RhythmCategory
import com.lumyrinth.app.ui.components.ChipFilter
import com.lumyrinth.app.ui.components.ChipVariant
import com.lumyrinth.app.ui.components.ContinueRhythmCard
import com.lumyrinth.app.ui.components.FeatureCard
import com.lumyrinth.app.ui.components.HomeProgressSummaryCard
import com.lumyrinth.app.ui.components.PrimaryButton
import com.lumyrinth.app.ui.components.RhythmBreathingCircle
import com.lumyrinth.app.ui.components.StandardCard
import com.lumyrinth.app.ui.theme.LumyrinthColors
import com.lumyrinth.app.ui.theme.LumyrinthTypography
import java.time.LocalTime

import com.lumyrinth.app.ui.components.CosmicSectionBackground
import com.lumyrinth.app.ui.components.SectionTheme

@Composable
fun HomeScreen(
    featuredRhythm: Rhythm,
    progressSummary: ProgressSummary,
    lastUsedRhythm: Rhythm?,
    onStartFeatured: (Rhythm) -> Unit,
    onMoodFilterClick: (RhythmCategory) -> Unit,
    onRepeatLastSession: (Rhythm) -> Unit,
    onExploreClick: () -> Unit,
    onProfileClick: () -> Unit,
) {
    val greeting = remember {
        val hour = LocalTime.now().hour
        when {
            hour < 12 -> "Good morning"
            hour < 18 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    var selectedPreset by remember(featuredRhythm.id) { mutableStateOf(featuredRhythm) }

    // Staggered screen entry animation
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        )
    }

    val displayDurationText = if (progressSummary.latestSession != null) {
        val actualSecs = progressSummary.latestSession.durationSecondsActual
        if (actualSecs > 0) {
            if (actualSecs < 60) "$actualSecs seconds" else "${actualSecs / 60} minutes"
        } else {
            "${progressSummary.latestSession.durationMinutesActual} minutes"
        }
    } else {
        ""
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CosmicSectionBackground(theme = SectionTheme.HOME)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp),
        ) {
            // Top row: Greeting & Zen Profile Icon
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(animProgress.value)
                    .offset { IntOffset(0, ((1f - animProgress.value) * 20).toInt()) },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = greeting,
                        style = LumyrinthTypography.BodySm.copy(
                            fontSize = 13.sp,
                            color = LumyrinthColors.TextSecondary,
                        ),
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Find your rhythm.",
                        style = LumyrinthTypography.H1.copy(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                        ),
                        color = LumyrinthColors.TextPrimary,
                    )
                }

                // Zen meditation avatar circular button
                IconButton(
                    onClick = onProfileClick,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E1433)),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SelfImprovement,
                        contentDescription = "Profile & Settings",
                        tint = LumyrinthColors.TextPrimary,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Scrollable content area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            ) {
                // Interactive Breathing Rhythm Presets & Live BreathingCircle Visual Anchor
                StandardCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(animProgress.value)
                        .offset { IntOffset(0, ((1f - animProgress.value) * 25).toInt()) },
                    padding = androidx.compose.foundation.layout.PaddingValues(18.dp),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                Text(
                            text = "RECOMMENDED FOR YOU",
                                    style = LumyrinthTypography.Label.copy(
                                        letterSpacing = 1.5.sp,
                                        fontWeight = FontWeight.Bold,
                                    ),
                                    color = Color(0xFFE879F9),
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = selectedPreset.name,
                                    style = LumyrinthTypography.H2.copy(fontSize = 18.sp),
                                    color = Color.White,
                                )
                            }

                            Text(
                                text = selectedPreset.patternSummary,
                                style = LumyrinthTypography.Label.copy(fontSize = 12.sp),
                                color = LumyrinthColors.TextSecondary,
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Compact preview keeps the primary action above the fold.
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(128.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            RhythmBreathingCircle(
                                rhythm = selectedPreset,
                                circleSize = 116.dp,
                                showPhaseLabel = true,
                                showCountdown = true,
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = selectedPreset.shortDescription,
                            style = LumyrinthTypography.BodySm.copy(fontSize = 13.sp),
                            color = LumyrinthColors.TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp),
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        PrimaryButton(
                            label = "Begin ${selectedPreset.name}",
                            onClick = { onStartFeatured(selectedPreset) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Mood Section: "How do you want to feel?"
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(animProgress.value)
                        .offset { IntOffset(0, ((1f - animProgress.value) * 40).toInt()) }
                ) {
                    Text(
                        text = "How do you want to feel?",
                        style = LumyrinthTypography.H3.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                        color = LumyrinthColors.TextPrimary,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        ChipFilter(
                            label = "Calm",
                            icon = Icons.Rounded.Spa,
                            iconTint = Color(0xFFA855F7), // Violet
                            selected = false,
                            onClick = { onMoodFilterClick(RhythmCategory.RELAX) },
                            variant = ChipVariant.MoodCard,
                            modifier = Modifier.weight(1f),
                        )
                        ChipFilter(
                            label = "Focused",
                            icon = Icons.Rounded.CenterFocusStrong,
                            iconTint = Color(0xFFE879F9), // Magenta
                            selected = false,
                            onClick = { onMoodFilterClick(RhythmCategory.FOCUS) },
                            variant = ChipVariant.MoodCard,
                            modifier = Modifier.weight(1f),
                        )
                        ChipFilter(
                            label = "Rested",
                            icon = Icons.Rounded.Bedtime,
                            iconTint = Color(0xFFFDBA74), // Warm Peach/Amber
                            selected = false,
                            onClick = { onMoodFilterClick(RhythmCategory.SLEEP) },
                            variant = ChipVariant.MoodCard,
                            modifier = Modifier.weight(1f),
                        )
                        ChipFilter(
                            label = "Refreshed",
                            icon = Icons.Rounded.WbSunny,
                            iconTint = Color(0xFFFDE047), // Sun Yellow
                            selected = false,
                            onClick = { onMoodFilterClick(RhythmCategory.ENERGY) },
                            variant = ChipVariant.MoodCard,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Continue only when the user has real history.
                if (lastUsedRhythm != null) Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(animProgress.value)
                        .offset { IntOffset(0, ((1f - animProgress.value) * 50).toInt()) }
                ) {
                    Text(
                        text = "Continue your rhythm",
                        style = LumyrinthTypography.H3.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                        color = LumyrinthColors.TextPrimary,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ContinueRhythmCard(
                        title = lastUsedRhythm.name,
                        durationText = displayDurationText,
                        onRepeat = { onRepeatLastSession(lastUsedRhythm) },
                        icon = Icons.Rounded.Spa,
                        iconTint = Color(0xFFF43F5E),
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Today's Progress Stat Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(animProgress.value)
                        .offset { IntOffset(0, ((1f - animProgress.value) * 60).toInt()) }
                ) {
                    HomeProgressSummaryCard(
                        sessionCount = progressSummary.todaysSessionCount,
                        mindfulMinutes = progressSummary.todaysMindfulMinutes,
                        streakDays = progressSummary.currentStreakDays,
                    )
                }

                // Generous bottom spacer so content clears the floating bottom nav bar completely
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}
