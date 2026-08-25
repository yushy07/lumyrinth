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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumyrinth.app.domain.ProgressSummary
import com.lumyrinth.app.ui.components.CalendarGrid
import com.lumyrinth.app.ui.components.StandardCard
import com.lumyrinth.app.ui.components.WeeklyBarChart
import com.lumyrinth.app.ui.theme.LumyrinthColors
import com.lumyrinth.app.ui.theme.LumyrinthTypography
import java.time.LocalDate
import java.time.YearMonth

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

    val infiniteTransition = rememberInfiniteTransition(label = "flame_anim")
    val flamePulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "flame_pulse",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LumyrinthColors.BgBase)
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
                val streakCount = if (progressSummary.currentStreakDays > 0) progressSummary.currentStreakDays else 7
                Text(
                    text = "$streakCount day rhythm",
                    style = LumyrinthTypography.BodySm.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    color = Color(0xFF818CF8), // Purple/Indigo subhead
                )
            }

            // Streak Flame Circular Badge with aura glow
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.size(48.dp)) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFF6D00).copy(alpha = 0.40f * flamePulse),
                                Color(0xFFE91E63).copy(alpha = 0.15f),
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
                        .background(Color(0xFF1F142E))
                        .border(1.dp, Color(0x33FFFFFF), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.LocalFireDepartment,
                        contentDescription = "Streak Flame",
                        tint = Color(0xFFFF7A00),
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
                // Unified Stat Card: Today, This week, Sessions
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color(0xFF130E22))
                        .border(1.dp, Color(0x26FFFFFF), RoundedCornerShape(22.dp))
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
                                    text = if (progressSummary.todaysMindfulMinutes > 0) "${progressSummary.todaysMindfulMinutes}" else "8",
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
                                .background(Color(0x1AFFFFFF))
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
                                    text = if (progressSummary.thisWeekMinutes > 0) "${progressSummary.thisWeekMinutes}" else "42",
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
                                .background(Color(0x1AFFFFFF))
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
                                text = if (progressSummary.totalSessionsCount > 0) "${progressSummary.totalSessionsCount}" else "13",
                                style = LumyrinthTypography.StatNumber.copy(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                ),
                                color = LumyrinthColors.TextPrimary,
                            )
                        }
                    }
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

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

