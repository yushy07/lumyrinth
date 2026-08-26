package com.lumyrinth.app.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.lumyrinth.app.data.UserPreferences
import com.lumyrinth.app.BuildConfig
import com.lumyrinth.app.ui.components.ConfirmDialog
import com.lumyrinth.app.ui.components.StandardCard
import com.lumyrinth.app.ui.components.ToggleSwitch
import com.lumyrinth.app.ui.theme.LumyrinthColors
import com.lumyrinth.app.ui.theme.LumyrinthTypography

import com.lumyrinth.app.ui.components.CosmicSectionBackground
import com.lumyrinth.app.ui.components.SectionTheme

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Check
import com.lumyrinth.app.ui.theme.AppColorTheme
import com.lumyrinth.app.ui.theme.LumyrinthThemeTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    userPreferences: UserPreferences,
    onToggleHaptics: (Boolean) -> Unit,
    onToggleSound: (Boolean) -> Unit,
    onToggleReminder: (Boolean) -> Unit,
    onReminderTimeChange: (String) -> Unit,
    onAmbientSoundscapeChange: (String) -> Unit,
    onThemeChange: (String) -> Unit = {},
    onRetakeOnboarding: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
    onOpenTerms: () -> Unit,
    onClearAllData: () -> Unit,
) {
    val context = LocalContext.current
    var permissionDeniedNote by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }
    var showReminderTimeDialog by remember { mutableStateOf(false) }
    var showSoundscapeDialog by remember { mutableStateOf(false) }

    if (showReminderTimeDialog) {
        val parts = userPreferences.dailyReminderTime.split(":")
        val timeState = rememberTimePickerState(
            initialHour = parts.getOrNull(0)?.toIntOrNull() ?: 20,
            initialMinute = parts.getOrNull(1)?.toIntOrNull() ?: 0,
            is24Hour = true,
        )
        AlertDialog(
            onDismissRequest = { showReminderTimeDialog = false },
            title = { Text("Reminder time") },
            text = { TimePicker(state = timeState) },
            confirmButton = {
                TextButton(onClick = {
                    onReminderTimeChange("%02d:%02d".format(timeState.hour, timeState.minute))
                    showReminderTimeDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showReminderTimeDialog = false }) { Text("Cancel") }
            },
        )
    }

    if (showSoundscapeDialog) {
        val options = listOf("None", "Rain", "Night", "Ocean", "Forest", "Fireplace", "Stream", "Deep Space")
        AlertDialog(
            onDismissRequest = { showSoundscapeDialog = false },
            title = { Text("Default ambient sound") },
            text = {
                Column {
                    options.forEach { option ->
                        Text(
                            text = if (option == userPreferences.ambientSoundscape) "✓  $option" else option,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onAmbientSoundscapeChange(option)
                                    showSoundscapeDialog = false
                                }
                                .padding(vertical = 12.dp),
                        )
                    }
                }
            },
            confirmButton = {},
        )
    }

    if (showClearDataDialog) {
        ConfirmDialog(
            title = "Clear all data?",
            message = "This will permanently delete all your session history, custom rhythms, and preferences. This can't be undone.",
            confirmLabel = "Delete Everything",
            cancelLabel = "Cancel",
            isDestructive = true,
            onConfirm = {
                showClearDataDialog = false
                onClearAllData()
            },
            onDismiss = {
                showClearDataDialog = false
            },
        )
    }

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

    Box(modifier = Modifier.fillMaxSize()) {
        CosmicSectionBackground(theme = SectionTheme.SETTINGS)

        Column(
            modifier = Modifier
                .widthIn(max = 720.dp)
                .fillMaxSize()
                .align(Alignment.TopCenter)
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

            // Color Theme Palette Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "App Color Theme",
                    style = LumyrinthTypography.H3,
                    color = LumyrinthColors.TextPrimary,
                )
                Text(
                    text = AppColorTheme.fromId(userPreferences.appTheme).displayName,
                    style = LumyrinthTypography.Label.copy(
                        color = LumyrinthThemeTokens.palette.primaryAccent,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    ),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            StandardCard(
                padding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Curated ambient palettes crafted for circadian mindfulness and sensory rest.",
                        style = LumyrinthTypography.BodySm,
                        color = LumyrinthColors.TextSecondary,
                        modifier = Modifier.padding(bottom = 12.dp),
                    )

                    AppColorTheme.entries.forEach { theme ->
                        val isSelected = userPreferences.appTheme == theme.id
                        val borderCol = if (isSelected) LumyrinthColors.AccentSuccess else LumyrinthColors.BorderSubtle
                        val bgCol = if (isSelected) LumyrinthColors.AccentYellow.copy(alpha = 0.62f) else LumyrinthColors.SurfaceCard

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(bgCol)
                                .border(1.dp, borderCol, RoundedCornerShape(14.dp))
                                .clickable(role = Role.RadioButton) { onThemeChange(theme.id) }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f),
                                ) {
                                    // Color swatch dot pair
                                    Row(horizontalArrangement = Arrangement.spacedBy((-6).dp)) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(theme.primaryHex)
                                                .border(2.dp, LumyrinthColors.BgBase, CircleShape),
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(theme.secondaryHex)
                                                .border(2.dp, LumyrinthColors.BgBase, CircleShape),
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Column {
                                        Text(
                                            text = theme.displayName,
                                            style = LumyrinthTypography.Body.copy(
                                                fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Medium,
                                            ),
                                            color = LumyrinthColors.TextPrimary,
                                        )
                                        Text(
                                            text = theme.description,
                                            style = LumyrinthTypography.Label.copy(
                                                fontSize = 11.sp,
                                                color = LumyrinthColors.TextSecondary,
                                            ),
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(26.dp)
                                            .clip(CircleShape)
                                            .background(theme.primaryHex),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Check,
                                            contentDescription = "Selected",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp),
                                        )
                                    }
                                }
                            }
                        }
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
                        icon = Icons.AutoMirrored.Rounded.VolumeUp,
                        title = "Sound guidance",
                        subtitle = "Chimes & transition tones",
                        checked = userPreferences.soundGuidanceDefault,
                        onCheckedChange = onToggleSound,
                    )

                    SettingsActionRow(
                        icon = Icons.Rounded.GraphicEq,
                        title = "Ambient sound",
                        subtitle = userPreferences.ambientSoundscape,
                        onClick = { showSoundscapeDialog = true },
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

                    if (userPreferences.dailyReminderEnabled) {
                        SettingsActionRow(
                            icon = Icons.Rounded.Schedule,
                            title = "Reminder time",
                            subtitle = userPreferences.dailyReminderTime,
                            onClick = { showReminderTimeDialog = true },
                        )
                        TextButton(
                            onClick = {
                                context.startActivity(
                                    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                        .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                                )
                            },
                            modifier = Modifier.padding(start = 24.dp),
                        ) {
                            Text("Open notification settings")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // About & Legal Section
            Text(
                text = "About & Legal",
                style = LumyrinthTypography.H3,
                color = LumyrinthColors.TextPrimary,
            )

            Spacer(modifier = Modifier.height(12.dp))

            StandardCard(
                padding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Privacy Policy Row
                    SettingsActionRow(
                        icon = Icons.Rounded.Shield,
                        title = "Privacy Policy",
                        subtitle = "100% on-device & local-first",
                        onClick = onOpenPrivacyPolicy,
                        testTag = "settings_privacy_policy_row",
                    )

                    // Terms of Service Row
                    SettingsActionRow(
                        icon = Icons.Rounded.Description,
                        title = "Terms of Service",
                        subtitle = "Wellness notice & terms",
                        onClick = onOpenTerms,
                        testTag = "settings_terms_row",
                    )

                    // Retake Onboarding Row
                    SettingsActionRow(
                        icon = Icons.Rounded.Refresh,
                        title = "Retake onboarding",
                        subtitle = "Restart introduction flow",
                        onClick = onRetakeOnboarding,
                        testTag = "settings_retake_onboarding_row",
                    )

                    // Version Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "Version",
                            style = LumyrinthTypography.Body,
                            color = LumyrinthColors.TextSecondary,
                        )
                        Text(
                            text = BuildConfig.VERSION_NAME,
                            style = LumyrinthTypography.BodySm,
                            color = LumyrinthColors.TextTertiary,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Data Management Section
            Text(
                text = "Data Management",
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
                            .height(64.dp)
                            .clickable(role = Role.Button, onClick = { showClearDataDialog = true })
                            .testTag("settings_clear_data_row"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.DeleteOutline,
                                contentDescription = null,
                                tint = Color(0xFFFB7185),
                                modifier = Modifier.size(22.dp),
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Clear all my data",
                                    style = LumyrinthTypography.Body.copy(
                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                                    ),
                                    color = Color(0xFFFB7185),
                                )
                                Text(
                                    text = "Wipes history, custom rhythms & settings",
                                    style = LumyrinthTypography.BodySm,
                                    color = LumyrinthColors.TextSecondary,
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = null,
                            tint = LumyrinthColors.TextTertiary,
                            modifier = Modifier.size(18.dp),
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

@Composable
private fun SettingsActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String = "",
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable(role = Role.Button, onClick = onClick)
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = LumyrinthColors.AccentPink,
                modifier = Modifier.size(20.dp),
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

        Icon(
            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            contentDescription = null,
            tint = LumyrinthColors.TextTertiary,
            modifier = Modifier.size(18.dp),
        )
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
            .height(64.dp)
            .clickable(role = Role.Switch) { onCheckedChange(!checked) },
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
