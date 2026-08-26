package com.lumyrinth.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.lumyrinth.app.domain.DayMinuteStat
import com.lumyrinth.app.ui.theme.LumyrinthColors
import com.lumyrinth.app.ui.theme.LumyrinthTypography
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ProgressBar(
    progress: Float, // 0.0f to 1.0f
    modifier: Modifier = Modifier,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        label = "progress_bar",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(4.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(LumyrinthColors.OverlayWhite08),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
                .clip(RoundedCornerShape(999.dp))
                .background(LumyrinthColors.GradientPrimary)
        )
    }
}

/**
 * Weekly Bar Chart matching the exact visual styling in image 11:
 * - Rounded track capsules behind each bar
 * - Multi-color vibrant gradient fills (purple-magenta for weekdays, amber-coral for weekend/active)
 * - Animated bar expansion on display
 * - Day letters below (M, T, W, T, F, S, S)
 */
@Composable
fun WeeklyBarChart(
    dayStats: List<DayMinuteStat>,
    modifier: Modifier = Modifier,
) {
    var animationTriggered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        animationTriggered = true
    }

    val maxMinutes = (dayStats.maxOfOrNull { it.minutes } ?: 0).coerceAtLeast(1)
    val chartDescription = dayStats.joinToString(", ") { "${it.dayOfWeek}: ${it.minutes} minutes" }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
            .semantics { contentDescription = "Mindful minutes this week. $chartDescription" }
            .padding(horizontal = 6.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        dayStats.forEachIndexed { index, stat ->
            val targetHeightFraction = if (stat.minutes == 0) {
                0f
            } else {
                (stat.minutes.toFloat() / maxMinutes.toFloat()).coerceIn(0.20f, 1.0f)
            }

            val animatedHeight by animateFloatAsState(
                targetValue = if (animationTriggered) targetHeightFraction else 0.05f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow,
                ),
                label = "bar_height_$index",
            )

            val isWeekend = index >= 5 // S, S (Saturday, Sunday)
            val barBrush = if (isWeekend) {
                // Coral / Amber gradient for weekend
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFB7185), // Coral/pink top
                        Color(0xFFF97316), // Orange bottom
                    )
                )
            } else {
                // Purple / Magenta gradient for weekdays
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFE879F9), // Magenta/lavender top
                        Color(0xFFA855F7), // Purple mid
                        Color(0xFF6B21A8), // Deep purple bottom
                    )
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.weight(1f),
            ) {
                // Track Container (Pill background)
                Box(
                    modifier = Modifier
                        .width(18.dp)
                        .height(96.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color(0xFF161226)),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    // Active filled bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(animatedHeight)
                            .clip(RoundedCornerShape(999.dp))
                            .background(barBrush)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Day of week letter (M, T, W, T, F, S, S)
                Text(
                    text = stat.dayLabel,
                    style = LumyrinthTypography.Label.copy(
                        fontSize = 12.sp,
                        fontWeight = if (stat.isToday) FontWeight.Bold else FontWeight.Medium,
                    ),
                    color = if (stat.isToday) Color.White else LumyrinthColors.TextSecondary,
                )
            }
        }
    }
}

/**
 * Calendar Grid matching the exact visual layout and styling in image 11:
 * - Month navigation header with "May 2024 >"
 * - Day headers M T W T F S S
 * - Highlighted active/streak days with glowing circular markers
 */
@Composable
fun CalendarGrid(
    yearMonth: YearMonth,
    activeDates: Set<LocalDate>,
    streakDates: Set<LocalDate>,
    today: LocalDate,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Month navigation header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Calendar",
                style = LumyrinthTypography.H3.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = LumyrinthColors.TextPrimary,
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                IconButton(onClick = onPrevMonth, modifier = Modifier.size(48.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                        contentDescription = "Previous month",
                        tint = LumyrinthColors.TextSecondary,
                    )
                }
                val monthName = yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                Text(
                    text = "$monthName ${yearMonth.year}",
                    style = LumyrinthTypography.BodySm.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    color = LumyrinthColors.TextSecondary,
                )
                IconButton(
                    onClick = onNextMonth,
                    enabled = yearMonth < YearMonth.from(today),
                    modifier = Modifier.size(48.dp),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = "Next month",
                        tint = LumyrinthColors.TextSecondary,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Day of week headers (M T W T F S S)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            listOf("M", "T", "W", "T", "F", "S", "S").forEach { day ->
                Text(
                    text = day,
                    style = LumyrinthTypography.Label.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    color = LumyrinthColors.TextTertiary,
                    modifier = Modifier.width(32.dp),
                    textAlign = TextAlign.Center,
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Days Grid
        val firstDayOfMonth = yearMonth.atDay(1)
        val daysInMonth = yearMonth.lengthOfMonth()
        val firstDayOffset = firstDayOfMonth.dayOfWeek.value - 1 // Monday = 0, Sunday = 6

        val totalCells = firstDayOffset + daysInMonth
        val rows = (totalCells + 6) / 7

        for (row in 0 until rows) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    val dayNum = cellIndex - firstDayOffset + 1

                    if (dayNum in 1..daysInMonth) {
                        val cellDate = yearMonth.atDay(dayNum)
                        val isPracticed = cellDate in activeDates
                        val isStreak = cellDate in streakDates
                        val isToday = cellDate == today

                        val circleBg = when {
                            isStreak -> Brush.radialGradient(
                                listOf(
                                    Color(0xFFFB7185),
                                    Color(0xFFE11D48),
                                )
                            )
                            isPracticed -> Brush.radialGradient(
                                listOf(
                                    Color(0xFFD946EF),
                                    Color(0xFF7E22CE),
                                )
                            )
                            else -> Brush.radialGradient(listOf(Color.Transparent, Color.Transparent))
                        }

                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(circleBg)
                                .then(
                                    if (isToday && !isPracticed) {
                                        Modifier.border(1.dp, LumyrinthColors.AccentOrange, CircleShape)
                                    } else {
                                        Modifier
                                    }
                                )
                                .semantics {
                                    contentDescription = buildString {
                                        append(cellDate.toString())
                                        append(if (isPracticed) ", practiced" else ", no session")
                                        if (isStreak) append(", streak day")
                                    }
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "$dayNum",
                                style = LumyrinthTypography.BodySm.copy(
                                    fontSize = 12.sp,
                                    fontWeight = if (isPracticed || isToday) FontWeight.SemiBold else FontWeight.Normal,
                                ),
                                color = when {
                                    isPracticed || isToday -> Color.White
                                    else -> LumyrinthColors.TextSecondary
                                },
                                textAlign = TextAlign.Center,
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(32.dp))
                    }
                }
            }
        }
    }
}

/**
 * Custom High-Fidelity Mood Emoji Face canvas matching Image 10:
 * - Better: Cheerful yellow face with curved happy eyes and wide open smile
 * - Same: Soft yellow face with neutral dots and straight mouth line
 * - Not great: Coral/salmon-red face with neutral dots and sad downturned mouth
 */
@Composable
fun CustomMoodFace(
    moodType: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "mood_scale",
    )

    val faceColor = when (moodType) {
        "better" -> Color(0xFFFACC15) // Bright golden yellow
        "same" -> Color(0xFFFDE047) // Light warm yellow
        else -> Color(0xFFFB7185) // Coral/salmon red
    }

    Canvas(
        modifier = modifier
            .size(46.dp)
            .scale(scale)
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension / 2.2f

        // Face circle
        drawCircle(
            color = faceColor,
            radius = radius,
            center = center,
        )

        // Features color
        val featureColor = Color(0xFF1E112A)

        val eyeY = center.y - (radius * 0.18f)
        val leftEyeX = center.x - (radius * 0.35f)
        val rightEyeX = center.x + (radius * 0.35f)
        val eyeRadius = radius * 0.10f

        when (moodType) {
            "better" -> {
                // Cheerful round dark eyes
                drawCircle(
                    color = featureColor,
                    radius = eyeRadius,
                    center = Offset(leftEyeX, eyeY),
                )
                drawCircle(
                    color = featureColor,
                    radius = eyeRadius,
                    center = Offset(rightEyeX, eyeY),
                )

                // Wide open happy smile
                val smilePath = Path().apply {
                    val mouthY = center.y + (radius * 0.10f)
                    val mouthWidth = radius * 0.85f
                    val mouthDepth = radius * 0.45f
                    moveTo(center.x - mouthWidth / 2f, mouthY)
                    quadraticTo(
                        center.x, mouthY + mouthDepth,
                        center.x + mouthWidth / 2f, mouthY,
                    )
                }
                drawPath(
                    path = smilePath,
                    color = featureColor,
                    style = Stroke(width = 3f, cap = StrokeCap.Round),
                )
            }

            "same" -> {
                // Neutral eyes
                drawCircle(
                    color = featureColor,
                    radius = eyeRadius,
                    center = Offset(leftEyeX, eyeY),
                )
                drawCircle(
                    color = featureColor,
                    radius = eyeRadius,
                    center = Offset(rightEyeX, eyeY),
                )

                // Straight horizontal mouth line
                val mouthY = center.y + (radius * 0.25f)
                val mouthWidth = radius * 0.70f
                drawLine(
                    color = featureColor,
                    start = Offset(center.x - mouthWidth / 2f, mouthY),
                    end = Offset(center.x + mouthWidth / 2f, mouthY),
                    strokeWidth = 3f,
                    cap = StrokeCap.Round,
                )
            }

            else -> {
                // Sad/Not Great eyes
                drawCircle(
                    color = featureColor,
                    radius = eyeRadius,
                    center = Offset(leftEyeX, eyeY),
                )
                drawCircle(
                    color = featureColor,
                    radius = eyeRadius,
                    center = Offset(rightEyeX, eyeY),
                )

                // Downturned curved mouth
                val frownPath = Path().apply {
                    val mouthY = center.y + (radius * 0.40f)
                    val mouthWidth = radius * 0.75f
                    val mouthHeight = radius * 0.28f
                    moveTo(center.x - mouthWidth / 2f, mouthY)
                    quadraticTo(
                        center.x, mouthY - mouthHeight,
                        center.x + mouthWidth / 2f, mouthY,
                    )
                }
                drawPath(
                    path = frownPath,
                    color = featureColor,
                    style = Stroke(width = 3f, cap = StrokeCap.Round),
                )
            }
        }
    }
}

enum class MoodOption(val id: String, val label: String) {
    BETTER("better", "Better"),
    SAME("same", "Same"),
    NOT_GREAT("not_great", "Not great"),
}

@Composable
fun MoodPicker(
    selectedMood: String?,
    onMoodSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MoodOption.entries.forEach { option ->
            val isSelected = selectedMood == option.id

            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = LocalIndication.current,
                        role = Role.RadioButton,
                        onClick = { onMoodSelected(option.id) },
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CustomMoodFace(
                    moodType = option.id,
                    isSelected = isSelected,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = option.label,
                    style = LumyrinthTypography.BodySm.copy(
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                    ),
                    color = if (isSelected) Color.White else LumyrinthColors.TextSecondary,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmLabel: String,
    cancelLabel: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    Dialog(onDismissRequest = onCancel) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(LumyrinthColors.SurfaceCard)
                .border(1.dp, LumyrinthColors.BorderMedium, RoundedCornerShape(24.dp))
                .padding(24.dp),
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = title,
                    style = LumyrinthTypography.H2,
                    color = LumyrinthColors.TextPrimary,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = message,
                    style = LumyrinthTypography.Body,
                    color = LumyrinthColors.TextSecondary,
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    // Cancel button
                    GhostButton(
                        label = cancelLabel,
                        onClick = onCancel,
                        modifier = Modifier.weight(1f),
                    )

                    // Confirm button
                    PrimaryButton(
                        label = confirmLabel,
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        backgroundBrush = Brush.linearGradient(
                            listOf(LumyrinthColors.AccentOrange, LumyrinthColors.AccentPink)
                        ),
                    )
                }
            }
        }
    }
}

