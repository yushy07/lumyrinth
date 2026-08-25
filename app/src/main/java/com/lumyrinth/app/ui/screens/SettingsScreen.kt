package com.lumyrinth.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.lumyrinth.app.data.UserPreferences
import com.lumyrinth.app.ui.components.StandardCard
import com.lumyrinth.app.ui.components.ToggleSwitch
import com.lumyrinth.app.ui.theme.LumyrinthColors
import com.lumyrinth.app.ui.theme.LumyrinthTypography

@Composable
fun SettingsScreen(
    userPreferences: UserPreferences,
    onToggleHaptics: (Boolean) -> Unit,
    onToggleSound: (Boolean) -> Unit,
    onToggleReminder: (Boolean) -> Unit,
    onRetakeOnboarding: () -> Unit,
) {
    val context = LocalContext.current
    var permissionDeniedNote by remember { mutableStateOf(false) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            permissionDeniedNote = false
            onToggleReminder(true)
        } else {
            permissionDeniedNote = true
            onToggleReminder(false)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LumyrinthColors.BgBase)
            .statusBarsPadding()
            .padding(horizontal = 20.dp),
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Settings",
            style = LumyrinthTypography.H1,
            color = LumyrinthColors.TextPrimary,
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            // Profile Card
            StandardCard(
                padding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(LumyrinthColors.AccentPurple.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Person,
                            contentDescription = null,
                            tint = LumyrinthColors.AccentPink,
                            modifier = Modifier.size(28.dp),
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Lumyrinth Mindful Member",
                            style = LumyrinthTypography.H3,
                            color = LumyrinthColors.TextPrimary,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Personal Space",
                            style = LumyrinthTypography.BodySm,
                            color = LumyrinthColors.TextSecondary,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Preferences Section
            Text(
                text = "Preferences",
                style = LumyrinthTypography.H3,
                color = LumyrinthColors.TextPrimary,
            )

            Spacer(modifier = Modifier.height(12.dp))

            StandardCard(
                padding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SettingsToggleRow(
                        icon = Icons.Rounded.Vibration,
                        title = "Haptic guidance",
                        subtitle = "Tactile boundary cues",
                        checked = userPreferences.hapticGuidanceDefault,
                        onCheckedChange = onToggleHaptics,
                    )

                    SettingsToggleRow(
                        icon = Icons.Rounded.VolumeUp,
                        title = "Sound guidance",
                        subtitle = "Chimes & transition tones",
                        checked = userPreferences.soundGuidanceDefault,
                        onCheckedChange = onToggleSound,
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Notifications Section
            Text(
                text = "Notifications",
                style = LumyrinthTypography.H3,
                color = LumyrinthColors.TextPrimary,
            )

            Spacer(modifier = Modifier.height(12.dp))

            StandardCard(
                padding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SettingsToggleRow(
                        icon = Icons.Rounded.Notifications,
                        title = "Daily reminder",
                        subtitle = if (userPreferences.dailyReminderEnabled) "Scheduled for ${userPreferences.dailyReminderTime}" else "Daily mindful prompt",
                        checked = userPreferences.dailyReminderEnabled,
                        onCheckedChange = { enable ->
                            if (enable) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    val hasPermission = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.POST_NOTIFICATIONS
                                    ) == PackageManager.PERMISSION_GRANTED

                                    if (hasPermission) {
                                        permissionDeniedNote = false
                                        onToggleReminder(true)
                                    } else {
                                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                } else {
                                    permissionDeniedNote = false
                                    onToggleReminder(true)
                                }
                            } else {
                                permissionDeniedNote = false
                                onToggleReminder(false)
                            }
                        },
                    )

                    if (permissionDeniedNote) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Notification permissions are disabled in system settings.",
                            style = LumyrinthTypography.Label,
                            color = LumyrinthColors.AccentPink,
                            modifier = Modifier.padding(start = 36.dp, bottom = 8.dp),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // About & Reset Section
            Text(
                text = "About",
                style = LumyrinthTypography.H3,
                color = LumyrinthColors.TextPrimary,
            )

            Spacer(modifier = Modifier.height(12.dp))

            StandardCard(
                padding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                role = Role.Button,
                                onClick = onRetakeOnboarding,
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = null,
                                tint = LumyrinthColors.AccentPink,
                                modifier = Modifier.size(20.dp),
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Text(
                                text = "Retake onboarding",
                                style = LumyrinthTypography.Body,
                                color = LumyrinthColors.TextPrimary,
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = null,
                            tint = LumyrinthColors.TextTertiary,
                            modifier = Modifier.size(18.dp),
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "Version",
                            style = LumyrinthTypography.Body,
                            color = LumyrinthColors.TextSecondary,
                        )
                        Text(
                            text = "1.0.0",
                            style = LumyrinthTypography.BodySm,
                            color = LumyrinthColors.TextTertiary,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = LumyrinthColors.AccentPink,
                modifier = Modifier.size(22.dp),
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    style = LumyrinthTypography.Body,
                    color = LumyrinthColors.TextPrimary,
                )
                Text(
                    text = subtitle,
                    style = LumyrinthTypography.BodySm,
                    color = LumyrinthColors.TextSecondary,
                )
            }
        }

        ToggleSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}
