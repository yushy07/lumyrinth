package com.lumyrinth.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocalFireDepartment
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumyrinth.app.domain.ProgressSummary
import com.lumyrinth.app.R
import com.lumyrinth.app.ui.components.CalendarGrid
import com.lumyrinth.app.ui.components.StandardCard
import com.lumyrinth.app.ui.components.ProgressBar
import com.lumyrinth.app.ui.components.WeeklyBarChart
import com.lumyrinth.app.ui.components.rememberIsReducedMotion
import com.lumyrinth.app.ui.theme.LumyrinthColors
import com.lumyrinth.app.ui.theme.LumyrinthTypography
import java.time.LocalDate
import java.time.YearMonth

import com.lumyrinth.app.ui.components.CosmicSectionBackground
import com.lumyrinth.app.ui.components.SectionTheme

@Composable
fun ProgressScreen(
    progressSummary: ProgressSummary,
) {
    val today = remember { LocalDate.now() }
    var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    val reducedMotion = rememberIsReducedMotion()
    val flamePulse = if (reducedMotion) 1f else {
        val infiniteTransition = rememberInfiniteTransition(label = "flame_anim")
        val pulse by infiniteTransition.animateFloat(
            initialValue = 0.85f,
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "flame_pulse",
        )
        pulse
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CosmicSectionBackground(theme = SectionTheme.PROGRESS)

        Column(
            modifier = Modifier
                .widthIn(max = 720.dp)
                .fillMaxSize()
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(horizontal = 20.dp),
        ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top Header with streak flame badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = "Your Rhythm",
                    style = LumyrinthTypography.H1.copy(
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = LumyrinthColors.TextPrimary,
                )
                Spacer(modifier = Modifier.height(4.dp))
                val streakCount = progressSummary.currentStreakDays
                Text(
                    text = pluralStringResource(R.plurals.streak_days, streakCount, streakCount),
                    style = LumyrinthTypography.BodySm.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    color = LumyrinthColors.AccentPurple,
                )
            }

            // Celebrate only a real streak.
            if (progressSummary.currentStreakDays > 0) Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.size(48.dp)) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                LumyrinthColors.AccentYellow.copy(alpha = 0.45f * flamePulse),
                                LumyrinthColors.AccentYellow.copy(alpha = 0.16f),
                                Color.Transparent,
                            ),
                            center = center,
                            radius = size.minDimension / 1.6f,
                        ),
                        radius = size.minDimension / 1.6f,
                    )
                }

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(LumyrinthColors.SurfaceCard)
                        .border(1.dp, LumyrinthColors.BorderSubtle, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.LocalFireDepartment,
                        contentDescription = "${progressSummary.currentStreakDays} day streak",
                        tint = LumyrinthColors.AccentSuccess,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(400)) + slideInVertically(
                animationSpec = tween(400, easing = FastOutSlowInEasing),
                initialOffsetY = { 30 }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            ) {
                if (progressSummary.totalSessionsCount == 0) {
                    StandardCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = stringResource(R.string.progress_empty_title),
                                style = LumyrinthTypography.H3,
                                color = LumyrinthColors.TextPrimary,
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = stringResource(R.string.progress_empty_body),
                                style = LumyrinthTypography.BodySm,
                                color = LumyrinthColors.TextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                // Unified Stat Card: Today, This week, Sessions
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(LumyrinthColors.SurfaceCard)
                        .border(1.dp, LumyrinthColors.BorderSubtle, RoundedCornerShape(22.dp))
                        .padding(vertical = 16.dp, horizontal = 12.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround,
                    ) {
                        // Metric 1: Today
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(
                                text = "Today",
                                style = LumyrinthTypography.BodySm.copy(
                                    fontSize = 12.sp,
                                    color = LumyrinthColors.TextSecondary,
                                ),
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${progressSummary.todaysMindfulMinutes}",
                                    style = LumyrinthTypography.StatNumber.copy(
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                    ),
                                    color = LumyrinthColors.TextPrimary,
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "min",
                                    style = LumyrinthTypography.BodySm.copy(
                                        fontSize = 12.sp,
                                        color = LumyrinthColors.TextSecondary,
                                    ),
                                    modifier = Modifier.padding(bottom = 2.dp),
                                )
                            }
                        }

                        // Divider 1
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(32.dp)
                                .background(LumyrinthColors.BorderSubtle)
                        )

                        // Metric 2: This week
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1.2f),
                        ) {
                            Text(
                                text = "This week",
                                style = LumyrinthTypography.BodySm.copy(
                                    fontSize = 12.sp,
                                    color = LumyrinthColors.TextSecondary,
                                ),
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${progressSummary.thisWeekMinutes}",
                                    style = LumyrinthTypography.StatNumber.copy(
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                    ),
                                    color = LumyrinthColors.TextPrimary,
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "min",
                                    style = LumyrinthTypography.BodySm.copy(
                                        fontSize = 12.sp,
                                        color = LumyrinthColors.TextSecondary,
                                    ),
                                    modifier = Modifier.padding(bottom = 2.dp),
                                )
                            }
                        }

                        // Divider 2
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(32.dp)
                                .background(LumyrinthColors.BorderSubtle)
                        )

                        // Metric 3: Sessions
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(
                                text = "Sessions",
                                style = LumyrinthTypography.BodySm.copy(
                                    fontSize = 12.sp,
                                    color = LumyrinthColors.TextSecondary,
                                ),
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${progressSummary.totalSessionsCount}",
                                style = LumyrinthTypography.StatNumber.copy(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                ),
                                color = LumyrinthColors.TextPrimary,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(LumyrinthColors.BgElevated)
                        .padding(16.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("Weekly rhythm", style = LumyrinthTypography.H3, color = LumyrinthColors.TextPrimary)
                        Text(
                            "${progressSummary.activeDates.count { it >= today.minusDays(6) }}/7 days",
                            style = LumyrinthTypography.BodySm,
                            color = LumyrinthColors.TextSecondary,
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    ProgressBar(
                        progress = progressSummary.activeDates.count { it >= today.minusDays(6) } / 7f,
                        modifier = Modifier.height(7.dp),
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // This week section
                Text(
                    text = "This week",
                    style = LumyrinthTypography.H3.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = LumyrinthColors.TextPrimary,
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Weekly bar chart container
                WeeklyBarChart(
                    dayStats = progressSummary.weeklyChart,
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Calendar Section
                CalendarGrid(
                    yearMonth = currentYearMonth,
                    activeDates = progressSummary.activeDates,
                    streakDates = progressSummary.streakDates,
                    today = today,
                    onPrevMonth = { currentYearMonth = currentYearMonth.minusMonths(1) },
                    onNextMonth = { currentYearMonth = currentYearMonth.plusMonths(1) },
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Duration & Frequency Insights Card
                Text(
                    text = "Session Insights",
                    style = LumyrinthTypography.H3.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = LumyrinthColors.TextPrimary,
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(LumyrinthColors.SurfaceCard)
                        .border(1.dp, LumyrinthColors.BorderSubtle, RoundedCornerShape(20.dp))
                        .padding(18.dp),
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Total Mindful Minutes",
                                style = LumyrinthTypography.BodySm.copy(fontSize = 13.sp),
                                color = LumyrinthColors.TextSecondary,
                            )
                            Text(
                                text = "${progressSummary.totalMindfulMinutes} min",
                                style = LumyrinthTypography.H3.copy(fontSize = 15.sp, fontWeight = FontWeight.Bold),
                                color = LumyrinthColors.AccentPurple,
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(LumyrinthColors.BorderSubtle))
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Avg. Session Length",
                                style = LumyrinthTypography.BodySm.copy(fontSize = 13.sp),
                                color = LumyrinthColors.TextSecondary,
                            )
                            val avgFormatted = if (progressSummary.averageSessionMinutes > 0) {
                                String.format(java.util.Locale.getDefault(), "%.1f min", progressSummary.averageSessionMinutes)
                            } else {
                                "0.0 min"
                            }
                            Text(
                                text = avgFormatted,
                                style = LumyrinthTypography.H3.copy(fontSize = 15.sp, fontWeight = FontWeight.Bold),
                                color = LumyrinthColors.TextPrimary,
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(LumyrinthColors.BorderSubtle))
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Practice Frequency",
                                style = LumyrinthTypography.BodySm.copy(fontSize = 13.sp),
                                color = LumyrinthColors.TextSecondary,
                            )
                            Text(
                                text = pluralStringResource(
                                    R.plurals.active_days,
                                    progressSummary.activeDates.size,
                                    progressSummary.activeDates.size,
                                ),
                                style = LumyrinthTypography.H3.copy(fontSize = 15.sp, fontWeight = FontWeight.Bold),
                                color = LumyrinthColors.AccentSuccess,
                            )
                        }
                    }
                }

                // Generous bottom spacer so content clears floating bottom nav bar
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}
}

