package com.lumyrinth.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumyrinth.app.ui.theme.LumyrinthColors
import com.lumyrinth.app.ui.theme.LumyrinthTypography
import java.util.Locale

/**
 * State representation for the meditation countdown timer.
 */
enum class TimerRunningState {
    IDLE,
    RUNNING,
    PAUSED,
    COMPLETED
}

/**
 * Self-contained live countdown timer composable with start, pause, and stop controls.
 *
 * @param totalDurationSeconds The full duration of the meditation session in seconds.
 * @param modifier Custom modifier for styling and layout constraints.
 * @param onSessionComplete Callback invoked when the countdown naturally completes.
 * @param onStop Callback invoked when the user manually stops/ends the session early.
 */
@Composable
fun MeditationLiveCountdownTimer(
    totalDurationSeconds: Int,
    modifier: Modifier = Modifier,
    initialAutoStart: Boolean = false,
    onSessionComplete: (() -> Unit)? = null,
    onStop: ((elapsedSeconds: Int) -> Unit)? = null,
) {
    var timerState by remember {
        mutableStateOf(if (initialAutoStart) TimerRunningState.RUNNING else TimerRunningState.IDLE)
    }

    val totalDurationMillis = remember(totalDurationSeconds) {
        (totalDurationSeconds.coerceAtLeast(1) * 1000L)
    }
    var elapsedMillis by remember(totalDurationSeconds) { mutableLongStateOf(0L) }

    val remainingMillis = (totalDurationMillis - elapsedMillis).coerceAtLeast(0L)
    val remainingSeconds = (remainingMillis / 1000L).toInt()
    val progress = (elapsedMillis.toFloat() / totalDurationMillis.toFloat()).coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
        label = "countdown_progress",
    )

    // Accurate high-resolution timer loop
    LaunchedEffect(timerState, totalDurationMillis) {
        if (timerState == TimerRunningState.RUNNING) {
            var lastTime = 0L
            while (elapsedMillis < totalDurationMillis && timerState == TimerRunningState.RUNNING) {
                withFrameMillis { frameTime ->
                    if (lastTime != 0L) {
                        val delta = (frameTime - lastTime).coerceIn(0L, 50L)
                        elapsedMillis += delta
                        if (elapsedMillis >= totalDurationMillis) {
                            elapsedMillis = totalDurationMillis
                            timerState = TimerRunningState.COMPLETED
                        }
                    }
                    lastTime = frameTime
                }
            }
            if (elapsedMillis >= totalDurationMillis) {
                onSessionComplete?.invoke()
            }
        }
    }

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val formattedTime = String.format(Locale.US, "%d:%02d", minutes, seconds)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF130E22))
            .border(1.dp, Color(0x2AFFFFFF), RoundedCornerShape(24.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Circular Countdown Display
        Box(
            modifier = Modifier
                .size(200.dp)
                .testTag("timer_dial_container"),
            contentAlignment = Alignment.Center,
        ) {
            // Track background ring
            CircularProgressIndicator(
                progress = { 1f },
                modifier = Modifier.size(200.dp),
                color = Color(0x1AFFFFFF),
                strokeWidth = 10.dp,
                strokeCap = StrokeCap.Round,
                trackColor = Color.Transparent,
            )

            // Progress indicator ring
            CircularProgressIndicator(
                progress = { 1f - animatedProgress },
                modifier = Modifier.size(200.dp),
                color = Color(0xFFE879F9),
                strokeWidth = 10.dp,
                strokeCap = StrokeCap.Round,
                trackColor = Color.Transparent,
            )

            // Center Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = when (timerState) {
                        TimerRunningState.IDLE -> "READY"
                        TimerRunningState.RUNNING -> "MEDITATING"
                        TimerRunningState.PAUSED -> "PAUSED"
                        TimerRunningState.COMPLETED -> "COMPLETED"
                    },
                    style = LumyrinthTypography.Label.copy(
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                    ),
                    color = when (timerState) {
                        TimerRunningState.RUNNING -> Color(0xFFE879F9)
                        TimerRunningState.PAUSED -> Color(0xFFFBBF24)
                        TimerRunningState.COMPLETED -> Color(0xFF34D399)
                        TimerRunningState.IDLE -> LumyrinthColors.TextSecondary
                    },
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formattedTime,
                    style = LumyrinthTypography.Countdown.copy(
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = LumyrinthColors.TextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("timer_countdown_text"),
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${(progress * 100).toInt()}% completed",
                    style = LumyrinthTypography.Label.copy(
                        fontSize = 12.sp,
                        color = Color(0x99FFFFFF),
                    ),
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Start / Pause / Stop Controls Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Reset / Stop Control
            AnimatedVisibility(
                visible = timerState != TimerRunningState.IDLE,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TimerActionButton(
                        icon = if (timerState == TimerRunningState.COMPLETED) Icons.Rounded.Refresh else Icons.Rounded.Stop,
                        contentDescription = "Stop Session",
                        onClick = {
                            val elapsedSecs = (elapsedMillis / 1000L).toInt()
                            if (timerState == TimerRunningState.COMPLETED) {
                                elapsedMillis = 0L
                                timerState = TimerRunningState.IDLE
                            } else {
                                onStop?.invoke(elapsedSecs)
                                timerState = TimerRunningState.IDLE
                                elapsedMillis = 0L
                            }
                        },
                        size = 48.dp,
                        iconSize = 22.dp,
                        backgroundColor = Color(0x22EF4444),
                        tint = Color(0xFFF87171),
                        testTag = "timer_stop_button",
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                }
            }

            // Main Primary Start / Pause Control
            when (timerState) {
                TimerRunningState.IDLE -> {
                    TimerPrimaryButton(
                        label = "Start Session",
                        icon = Icons.Rounded.PlayArrow,
                        onClick = { timerState = TimerRunningState.RUNNING },
                        testTag = "timer_start_button",
                    )
                }
                TimerRunningState.RUNNING -> {
                    TimerActionButton(
                        icon = Icons.Rounded.Pause,
                        contentDescription = "Pause Session",
                        onClick = { timerState = TimerRunningState.PAUSED },
                        size = 64.dp,
                        iconSize = 28.dp,
                        backgroundColor = Color(0x33A855F7),
                        tint = Color.White,
                        borderGradient = true,
                        testTag = "timer_pause_button",
                    )
                }
                TimerRunningState.PAUSED -> {
                    TimerActionButton(
                        icon = Icons.Rounded.PlayArrow,
                        contentDescription = "Resume Session",
                        onClick = { timerState = TimerRunningState.RUNNING },
                        size = 64.dp,
                        iconSize = 30.dp,
                        backgroundColor = Color(0x33A855F7),
                        tint = Color.White,
                        borderGradient = true,
                        testTag = "timer_resume_button",
                    )
                }
                TimerRunningState.COMPLETED -> {
                    TimerPrimaryButton(
                        label = "Restart",
                        icon = Icons.Rounded.Refresh,
                        onClick = {
                            elapsedMillis = 0L
                            timerState = TimerRunningState.RUNNING
                        },
                        testTag = "timer_restart_button",
                    )
                }
            }
        }
    }
}

/**
 * Custom styled action button for timer control actions.
 */
@Composable
private fun TimerActionButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 52.dp,
    iconSize: Dp = 24.dp,
    tint: Color = Color.White,
    backgroundColor: Color = LumyrinthColors.OverlayWhite08,
    borderGradient: Boolean = false,
    testTag: String = "timer_action_button",
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        label = "timer_btn_scale",
    )

    val borderModifier = if (borderGradient) {
        Modifier.border(
            width = 1.5.dp,
            brush = Brush.linearGradient(listOf(Color(0xFFE879F9), Color(0xFFA855F7))),
            shape = CircleShape,
        )
    } else {
        Modifier.border(1.dp, Color(0x33FFFFFF), CircleShape)
    }

    Box(
        modifier = modifier
            .size(size)
            .scale(scale)
            .shadow(
                elevation = if (borderGradient) 12.dp else 0.dp,
                shape = CircleShape,
                spotColor = Color(0xFFA855F7).copy(alpha = 0.5f),
            )
            .clip(CircleShape)
            .background(if (isPressed) Color(0x44FFFFFF) else backgroundColor)
            .then(borderModifier)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            )
            .testTag(testTag),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(iconSize),
        )
    }
}

/**
 * Primary Pill Button for Starting/Restarting Session
 */
@Composable
private fun TimerPrimaryButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "timer_primary_button",
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        label = "timer_primary_btn_scale",
    )

    Box(
        modifier = modifier
            .height(52.dp)
            .scale(scale)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(999.dp),
                spotColor = Color(0xFFA855F7).copy(alpha = 0.5f),
            )
            .clip(RoundedCornerShape(999.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFFA855F7), Color(0xFFEC4899))
                )
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = 28.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = LumyrinthTypography.Button,
                color = Color.White,
            )
        }
    }
}
