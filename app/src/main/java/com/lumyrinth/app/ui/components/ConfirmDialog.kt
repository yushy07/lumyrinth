package com.lumyrinth.app.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.lumyrinth.app.ui.theme.LumyrinthColors
import com.lumyrinth.app.ui.theme.LumyrinthTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    cancelLabel: String = "Cancel",
    icon: ImageVector = Icons.Rounded.WarningAmber,
    isDestructive: Boolean = true,
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        ),
    ) {
        val dialogBg = Brush.verticalGradient(
            listOf(
                LumyrinthColors.BgElevated,
                LumyrinthColors.SurfaceCard,
                LumyrinthColors.BgElevated,
            )
        )

        val confirmBg = if (isDestructive) {
            Brush.horizontalGradient(
                listOf(
                    Color(0xFFE11D48),
                    Color(0xFFBE123C),
                )
            )
        } else {
            LumyrinthColors.GradientButton
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(dialogBg)
                .border(
                    1.dp,
                    if (isDestructive) Color(0x40F43F5E) else LumyrinthColors.BorderSubtle,
                    RoundedCornerShape(26.dp)
                )
                .padding(24.dp)
                .testTag("confirm_dialog"),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(
                            if (isDestructive) Color(0x26F43F5E) else Color(0x26A855F7)
                        )
                        .border(
                            1.dp,
                            if (isDestructive) Color(0x4DF43F5E) else Color(0x4DA855F7),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isDestructive) Color(0xFFFB7185) else Color(0xFFC084FC),
                        modifier = Modifier.size(28.dp),
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = title,
                    style = LumyrinthTypography.H2.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = LumyrinthColors.TextPrimary,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Message
                Text(
                    text = message,
                    style = LumyrinthTypography.Body.copy(
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = LumyrinthColors.TextSecondary,
                    ),
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    // Confirm button
                    val confirmInteraction = remember { MutableInteractionSource() }
                    val confirmPressed by confirmInteraction.collectIsPressedAsState()
                    val confirmScale = if (confirmPressed) 0.97f else 1.0f

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .scale(confirmScale)
                            .clip(RoundedCornerShape(999.dp))
                            .background(confirmBg)
                            .clickable(
                                interactionSource = confirmInteraction,
                                indication = LocalIndication.current,
                                role = Role.Button,
                                onClick = onConfirm,
                            )
                            .testTag("confirm_dialog_confirm_button"),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = confirmLabel,
                            style = LumyrinthTypography.Button.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                            ),
                        )
                    }

                    // Cancel button
                    val cancelInteraction = remember { MutableInteractionSource() }
                    val cancelPressed by cancelInteraction.collectIsPressedAsState()
                    val cancelScale = if (cancelPressed) 0.97f else 1.0f

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .scale(cancelScale)
                            .clip(RoundedCornerShape(999.dp))
                            .background(LumyrinthColors.BgElevated)
                            .border(1.dp, LumyrinthColors.BorderSubtle, RoundedCornerShape(999.dp))
                            .clickable(
                                interactionSource = cancelInteraction,
                                indication = LocalIndication.current,
                                role = Role.Button,
                                onClick = onDismiss,
                            )
                            .testTag("confirm_dialog_cancel_button"),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = cancelLabel,
                            style = LumyrinthTypography.Button.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = LumyrinthColors.TextSecondary,
                            ),
                        )
                    }
                }
            }
        }
    }
}
