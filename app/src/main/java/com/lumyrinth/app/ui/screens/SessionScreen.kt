package com.lumyrinth.app.ui.screens

import android.app.Activity
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.MusicOff
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumyrinth.app.domain.BreathPhase
import com.lumyrinth.app.domain.Rhythm
import com.lumyrinth.app.ui.components.ConfirmDialog
import com.lumyrinth.app.ui.components.ExhaleEasing
import com.lumyrinth.app.ui.components.GlowOrb
import com.lumyrinth.app.ui.components.IconCircleButton
import com.lumyrinth.app.ui.components.InhaleEasing
import com.lumyrinth.app.ui.components.OrbAnimationState
import com.lumyrinth.app.ui.components.OrbCenterContent
import com.lumyrinth.app.ui.components.OrbSize
import com.lumyrinth.app.ui.components.ToggleSwitch
import com.lumyrinth.app.ui.components.rememberIsReducedMotion
import com.lumyrinth.app.ui.theme.LumyrinthColors
import com.lumyrinth.app.ui.theme.LumyrinthTypography
import kotlin.math.ceil
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreen(
    rhythm: Rhythm,
    durationMinutes: Int,
    initialSoundOn: Boolean,
    initialHapticsOn: Boolean,
    onPhaseTransition: (BreathPhase, soundOn: Boolean, hapticsOn: Boolean) -> Unit,
    onSessionFinished: (
        completedNaturally: Boolean,
        actualDurationSeconds: Int,
        cyclesCompleted: Int,
        soundOn: Boolean,
        hapticsOn: Boolean,
    ) -> Unit,
    onSessionAbandoned: () -> Unit,
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val isReducedMotion = rememberIsReducedMotion()

    val totalSessionMillis = durationMinutes * 60 * 1000L
    var elapsedSessionMillis by remember { mutableLongStateOf(0L) }
    var isPaused by remember { mutableStateOf(false) }
    var soundOn by remember { mutableStateOf(initialSoundOn) }
    var hapticsOn by remember { mutableStateOf(initialHapticsOn) }

    var showConfirmClose by remember { mutableStateOf(false) }
    var showQuickSettings by remember { mutableStateOf(false) }

    // Screen Wake Lock: Acquire when running, release when paused or disposed
    DisposableEffect(isPaused) {
        if (!isPaused) {
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    // Active Phases (0-duration phases are automatically excluded)
    val activePhases = remember(rhythm) { rhythm.activePhases() }
    var currentPhaseIndex by remember { mutableIntStateOf(0) }
    var phaseElapsedMillis by remember { mutableLongStateOf(0L) }
    var cyclesCompleted by remember { mutableIntStateOf(0) }

    val currentPhasePair = activePhases.getOrElse(currentPhaseIndex % activePhases.size) {
        BreathPhase.INHALE to 4
    }
    val currentPhase = currentPhasePair.first
    val currentPhaseTotalSeconds = currentPhasePair.second
    val currentPhaseTotalMillis = currentPhaseTotalSeconds * 1000L

    // Current real-time calculated orb scale (0.75f baseline to 1.0f max + wobble)
    var currentOrbScale by remember { mutableFloatStateOf(0.75f) }

    // Trigger cue sound & haptics when entering a new phase
    LaunchedEffect(currentPhaseIndex) {
        onPhaseTransition(currentPhase, soundOn, hapticsOn)
    }

    // Master High-Resolution Frame-Synced Animation Loop (ANIMATIONS.md Sec 1-5)
    LaunchedEffect(isPaused, totalSessionMillis) {
        var lastFrameTime = 0L
        while (elapsedSessionMillis < totalSessionMillis) {
            withFrameMillis { frameTime ->
                if (!isPaused) {
                    if (lastFrameTime != 0L) {
                        val deltaMillis = (frameTime - lastFrameTime).coerceIn(0L, 50L)
                        elapsedSessionMillis += deltaMillis
                        phaseElapsedMillis += deltaMillis

                        // Advance Phase if phase duration elapsed
                        if (phaseElapsedMillis >= currentPhaseTotalMillis) {
                            phaseElapsedMillis -= currentPhaseTotalMillis
                            val nextIndex = currentPhaseIndex + 1
                            if (nextIndex % activePhases.size == 0) {
                                cyclesCompleted += 1
                            }
                            currentPhaseIndex = nextIndex
                        }
                    }
                }
                lastFrameTime = frameTime
            }

            // Real-Time Scale Calculation (Sec 2.1 - 2.4 & 9)
            val currentPair = activePhases[currentPhaseIndex % activePhases.size]
            val activePhaseType = currentPair.first
            val phaseDurationMs = currentPair.second * 1000f
            val phaseFraction = (phaseElapsedMillis / phaseDurationMs).coerceIn(0f, 1f)

            currentOrbScale = when (activePhaseType) {
                BreathPhase.INHALE -> {
                    if (isReducedMotion) {
                        val fastFraction = (phaseElapsedMillis / 350f).coerceIn(0f, 1f)
                        0.75f + (0.25f * FastOutSlowInEasing.transform(fastFraction))
                    } else {
                        // Section 2.1 InhaleEasing: cubic-bezier(0.45, 0, 0.55, 1)
                        0.75f + (0.25f * InhaleEasing.transform(phaseFraction))
                    }
                }
                BreathPhase.HOLD_AFTER_INHALE -> {
                    if (isReducedMotion || isPaused) {
                        1.0f
                    } else {
                        // Section 2.2 Micro-breathing wobble: 0.99 - 1.01 on 2s cycle
                        val wobble = (sin((phaseElapsedMillis / 2000.0) * 2 * Math.PI) * 0.01f).toFloat()
                        1.0f + wobble
                    }
                }
                BreathPhase.EXHALE -> {
                    if (isReducedMotion) {
                        val fastFraction = (phaseElapsedMillis / 350f).coerceIn(0f, 1f)
                        1.0f - (0.25f * FastOutSlowInEasing.transform(fastFraction))
                    } else {
                        // Section 2.3 ExhaleEasing: cubic-bezier(0.55, 0, 0.45, 1)
                        1.0f - (0.25f * ExhaleEasing.transform(phaseFraction))
                    }
                }
                BreathPhase.HOLD_AFTER_EXHALE -> {
                    if (isReducedMotion || isPaused) {
                        0.75f
                    } else {
                        // Section 2.4 Micro-breathing wobble at small scale
                        val wobble = (sin((phaseElapsedMillis / 2000.0) * 2 * Math.PI) * 0.01f).toFloat()
                        0.75f * (1.0f + wobble)
                    }
                }
            }
        }

        // Natural completion trigger
        if (elapsedSessionMillis >= totalSessionMillis) {
            onSessionFinished(
                true,
                (elapsedSessionMillis / 1000L).toInt(),
                cyclesCompleted.coerceAtLeast(1),
                soundOn,
                hapticsOn,
            )
        }
    }

    // Intercept hardware back button to show confirm dialog
    BackHandler {
        showConfirmClose = true
    }

    // Countdown seconds inside orb (ticks per-second in sync with phase end)
    val remainingPhaseMillis = (currentPhaseTotalMillis - phaseElapsedMillis).coerceAtLeast(0L)
    val phaseSecondsRemaining = ceil(remainingPhaseMillis / 1000.0).toInt().coerceAtLeast(0)

    val remainingTotalSeconds = ((totalSessionMillis - elapsedSessionMillis) / 1000L).toInt().coerceAtLeast(0)
    val remainingMinutes = remainingTotalSeconds / 60
    val remainingSecs = remainingTotalSeconds % 60

    // Smooth continuous progress bar fill (ANIMATIONS.md Section 8)
    val progressFraction = (elapsedSessionMillis.toFloat() / totalSessionMillis.toFloat()).coerceIn(0f, 1f)
    val smoothProgress by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = tween(durationMillis = 100, easing = LinearEasing),
        label = "session_smooth_progress",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF07040D))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Top Bar Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Close 'X' Button
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0x22FFFFFF))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        role = Role.Button,
                        onClick = { showConfirmClose = true },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Close Session",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }

            // Right Quick Actions
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Music / Ambient sound toggle button
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (soundOn) Color(0x33A855F7) else Color(0x1AFFFFFF))
                        .border(1.dp, if (soundOn) Color(0x55C084FC) else Color(0x11FFFFFF), CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            role = Role.Button,
                            onClick = { soundOn = !soundOn },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = if (soundOn) Icons.Rounded.MusicNote else Icons.Rounded.MusicOff,
                        contentDescription = "Sound Toggle",
                        tint = if (soundOn) Color(0xFFF472B6) else Color(0x88FFFFFF),
                        modifier = Modifier.size(20.dp),
                    )
                }

                // Soundscape / Fine Tune Settings button
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0x22FFFFFF))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            role = Role.Button,
                            onClick = { showQuickSettings = true },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.GraphicEq,
                        contentDescription = "Soundscape & Guidance Settings",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(0.4f))

        // Phase Title with Soft Crossfade / Slide Transition (ANIMATIONS.md Section 3)
        AnimatedContent(
            targetState = currentPhase,
            transitionSpec = {
                (fadeIn(animationSpec = tween(220, easing = LinearEasing)) +
                        slideInVertically(
                            initialOffsetY = { it / 3 },
                            animationSpec = tween(220, easing = FastOutSlowInEasing)
                        ))
                    .togetherWith(
                        fadeOut(animationSpec = tween(180, easing = LinearEasing)) +
                                slideOutVertically(
                                    targetOffsetY = { -it / 3 },
                                    animationSpec = tween(180, easing = FastOutSlowInEasing)
                                )
                    )
            },
            label = "phase_label_crossfade",
        ) { phase ->
            Text(
                text = phase.label.uppercase(),
                style = LumyrinthTypography.Label.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 4.sp,
                ),
                color = Color.White,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Center GlowOrb with Real-Time Scale & Countdown
        GlowOrb(
            sizeVariant = OrbSize.Xl,
            centerContent = OrbCenterContent.Countdown(phaseSecondsRemaining),
            animationState = OrbAnimationState.Breathing(
                scale = currentOrbScale,
                phase = currentPhase,
                isPaused = isPaused,
            ),
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Time remaining text: e.g. "2:46 remaining"
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = String.format("%d:%02d", remainingMinutes, remainingSecs),
                style = LumyrinthTypography.Body.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                ),
                color = Color.White,
            )
            Text(
                text = "remaining",
                style = LumyrinthTypography.Body.copy(
                    fontSize = 14.sp,
                    color = Color(0x99FFFFFF),
                ),
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Sleek Gradient Progress Bar
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0x2AFFFFFF)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(smoothProgress)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFFA855F7), // Purple
                                Color(0xFFEC4899), // Pink
                                Color(0xFFFB923C), // Orange/Amber
                            )
                        )
                    ),
            )
        }

        Spacer(modifier = Modifier.weight(0.7f))

        // Bottom Controls Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Mute / Unmute Volume Button
            CircularControlItem(
                icon = if (soundOn) Icons.Rounded.VolumeUp else Icons.Rounded.VolumeOff,
                contentDescription = if (soundOn) "Mute" else "Unmute",
                isActive = soundOn,
                onClick = { soundOn = !soundOn },
            )

            // Large Center Pause / Resume Button with Glowing Gradient Ring Border
            CenterPlayPauseControl(
                isPaused = isPaused,
                onClick = { isPaused = !isPaused },
            )

            // Vibration / Haptics Toggle Button
            CircularControlItem(
                icon = Icons.Rounded.Vibration,
                contentDescription = if (hapticsOn) "Disable Haptics" else "Enable Haptics",
                isActive = hapticsOn,
                onClick = { hapticsOn = !hapticsOn },
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
    }

    // Confirm close modal dialog
    if (showConfirmClose) {
        ConfirmDialog(
            title = "End session early?",
            message = "Your progress up to this point will be saved.",
            confirmLabel = "End Session",
            cancelLabel = "Keep Going",
            onConfirm = {
                showConfirmClose = false
                onSessionFinished(
                    false,
                    (elapsedSessionMillis / 1000L).toInt(),
                    cyclesCompleted,
                    soundOn,
                    hapticsOn,
                )
            },
            onCancel = {
                showConfirmClose = false
            },
        )
    }

    // Quick Settings Bottom Sheet
    if (showQuickSettings) {
        ModalBottomSheet(
            onDismissRequest = { showQuickSettings = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = Color(0xFF160E28),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
            ) {
                Text(
                    text = "Session Guidance",
                    style = LumyrinthTypography.H2.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                    color = LumyrinthColors.TextPrimary,
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Sound Guidance", style = LumyrinthTypography.Body, color = LumyrinthColors.TextPrimary)
                    ToggleSwitch(checked = soundOn, onCheckedChange = { soundOn = it })
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Haptic Guidance", style = LumyrinthTypography.Body, color = LumyrinthColors.TextPrimary)
                    ToggleSwitch(checked = hapticsOn, onCheckedChange = { hapticsOn = it })
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun CircularControlItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1.0f,
        label = "ctrl_scale",
    )

    Box(
        modifier = modifier
            .size(54.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(if (isActive) Color(0x28FFFFFF) else Color(0x14FFFFFF))
            .border(1.dp, if (isActive) Color(0x33FFFFFF) else Color(0x10FFFFFF), CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (isActive) Color.White else Color(0x66FFFFFF),
            modifier = Modifier.size(24.dp),
        )
    }
}

@Composable
private fun CenterPlayPauseControl(
    isPaused: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        label = "play_btn_scale",
    )

    Box(
        modifier = modifier
            .size(76.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(Color(0xFF130A24))
            .border(
                width = 2.dp,
                brush = Brush.sweepGradient(
                    listOf(
                        Color(0xFFC084FC),
                        Color(0xFFF472B6),
                        Color(0xFFFB923C),
                        Color(0xFFC084FC),
                    )
                ),
                shape = CircleShape,
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = if (isPaused) Icons.Rounded.PlayArrow else Icons.Rounded.Pause,
            contentDescription = if (isPaused) "Resume" else "Pause",
            tint = Color.White,
            modifier = Modifier.size(34.dp),
        )
    }
}

