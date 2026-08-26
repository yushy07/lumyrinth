package com.lumyrinth.app.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Share
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumyrinth.app.ui.components.GhostButton
import com.lumyrinth.app.ui.components.GlowOrb
import com.lumyrinth.app.ui.components.MoodPicker
import com.lumyrinth.app.ui.components.OrbAnimationState
import com.lumyrinth.app.ui.components.OrbCenterContent
import com.lumyrinth.app.ui.components.OrbSize
import com.lumyrinth.app.ui.components.PrimaryButton
import com.lumyrinth.app.ui.components.StandardCard
import com.lumyrinth.app.ui.theme.LumyrinthColors
import com.lumyrinth.app.ui.theme.LumyrinthTypography

@Composable
fun CompleteScreen(
    durationSeconds: Int,
    cyclesCompleted: Int,
    initialMood: String?,
    onMoodSelected: (String) -> Unit,
    onDone: () -> Unit,
    onRepeatSession: () -> Unit,
) {
    val context = LocalContext.current
    var selectedMood by remember { mutableStateOf(initialMood) }
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LumyrinthColors.BgBase)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 22.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Top Row: Share button (top right)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "I completed a ${formatSessionDuration(durationSeconds)} breathing session with Lumyrinth."
                        )
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share Session"))
                },
                modifier = Modifier.size(48.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Share,
                    contentDescription = "Share Session",
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(22.dp),
                )
            }
        }

        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(400)) + slideInVertically(
                animationSpec = tween(400, easing = FastOutSlowInEasing),
                initialOffsetY = { 40 }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Complete",
                    style = LumyrinthTypography.H1.copy(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = LumyrinthColors.TextPrimary,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "You've found your rhythm.",
                    style = LumyrinthTypography.Body.copy(
                        fontSize = 14.sp,
                        color = Color(0xFFA89BB9),
                    ),
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Center GlowOrb with Checkmark
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    GlowOrb(
                        sizeVariant = OrbSize.Lg,
                        centerContent = OrbCenterContent.Checkmark,
                        animationState = OrbAnimationState.Complete(),
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 2 Stat cards: Duration & Cycles
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Left stat: Duration
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = formatSessionDuration(durationSeconds),
                            style = LumyrinthTypography.StatNumber.copy(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                            ),
                            color = LumyrinthColors.TextPrimary,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Duration",
                            style = LumyrinthTypography.BodySm.copy(
                                fontSize = 13.sp,
                                color = LumyrinthColors.TextSecondary,
                            ),
                        )
                    }

                    // Right stat: Cycles
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = "$cyclesCompleted",
                            style = LumyrinthTypography.StatNumber.copy(
                                fontSize = 24.sp,
                            ),
                            color = LumyrinthColors.TextPrimary,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Cycles",
                            style = LumyrinthTypography.BodySm.copy(
                                fontSize = 13.sp,
                                color = LumyrinthColors.TextSecondary,
                            ),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Mood Section Card
                StandardCard(
                    modifier = Modifier.fillMaxWidth(),
                    padding = androidx.compose.foundation.layout.PaddingValues(vertical = 18.dp, horizontal = 16.dp),
                    backgroundColor = Color(0xFF130E22),
                    borderColor = Color(0x26FFFFFF),
                    cornerRadius = 24.dp,
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "How do you feel?",
                            style = LumyrinthTypography.H3.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                            ),
                            color = LumyrinthColors.TextPrimary,
                            textAlign = TextAlign.Center,
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        MoodPicker(
                            selectedMood = selectedMood,
                            onMoodSelected = { mood ->
                                selectedMood = mood
                                onMoodSelected(mood)
                            },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Bottom CTAs: Done & Repeat Session
        PrimaryButton(
            label = "Done",
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
        )

        Spacer(modifier = Modifier.height(10.dp))

        GhostButton(
            label = "Repeat Session",
            onClick = onRepeatSession,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(6.dp))
    }
}

private fun formatSessionDuration(totalSeconds: Int): String {
    val safeSeconds = totalSeconds.coerceAtLeast(0)
    val minutes = safeSeconds / 60
    val seconds = safeSeconds % 60
    return when {
        minutes == 0 -> "${seconds}s"
        seconds == 0 -> "${minutes}m"
        else -> "${minutes}m ${seconds}s"
    }
}

