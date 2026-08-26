package com.lumyrinth.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Remove
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumyrinth.app.ui.theme.LumyrinthColors
import com.lumyrinth.app.ui.theme.LumyrinthTypography

@Composable
fun ToggleSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 23.dp else 3.dp,
        label = "thumb_offset",
    )

    Box(
        modifier = modifier
            .width(48.dp)
            .height(48.dp)
            .semantics { stateDescription = if (checked) "On" else "Off" }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = LocalIndication.current,
                role = Role.Switch,
                onClick = { onCheckedChange(!checked) },
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(28.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(
                    if (checked) Brush.linearGradient(listOf(LumyrinthColors.AccentSuccess, LumyrinthColors.AccentSuccess))
                    else Brush.linearGradient(listOf(LumyrinthColors.ToggleOff, LumyrinthColors.ToggleOff))
                )
                .padding(vertical = 3.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Box(
                modifier = Modifier
                    .offset(x = thumbOffset)
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }
    }
}

@Composable
fun SelectableRow(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = LumyrinthColors.AccentPink,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.985f else 1.0f,
        label = "row_scale",
    )

    val borderColor by animateColorAsState(
        targetValue = if (selected) LumyrinthColors.BorderMedium else LumyrinthColors.BorderSubtle,
        label = "row_border",
    )

    val bgColor by animateColorAsState(
        targetValue = if (selected) LumyrinthColors.SurfaceCardAlt else LumyrinthColors.SurfaceCard,
        label = "row_bg",
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                role = Role.Checkbox,
                onClick = onToggle,
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Icon container
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp),
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = label,
            style = LumyrinthTypography.H3,
            color = LumyrinthColors.TextPrimary,
            modifier = Modifier.weight(1f),
        )

        // Checkbox circle
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(
                    if (selected) LumyrinthColors.GradientPrimary
                    else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                )
                .border(
                    width = if (selected) 0.dp else 1.5.dp,
                    color = if (selected) Color.Transparent else LumyrinthColors.TextTertiary,
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) {
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

enum class ChipVariant {
    Pill,
    MoodCard,
    Duration,
}

@Composable
fun ChipFilter(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconTint: Color? = null,
    variant: ChipVariant = ChipVariant.Pill,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1.0f,
        label = "chip_scale",
    )

    when (variant) {
        ChipVariant.Pill, ChipVariant.Duration -> {
            val bgBrush = if (selected) {
                Brush.linearGradient(listOf(LumyrinthColors.AccentSuccess, LumyrinthColors.AccentSuccess))
            } else {
                Brush.linearGradient(listOf(LumyrinthColors.SurfaceCard, LumyrinthColors.SurfaceCard))
            }

            Box(
                modifier = modifier
                    .height(48.dp)
                    .semantics { this.selected = selected }
                    .scale(scale)
                    .clip(RoundedCornerShape(999.dp))
                    .background(bgBrush)
                    .then(
                        if (!selected) Modifier.border(1.dp, LumyrinthColors.BorderSubtle, RoundedCornerShape(999.dp))
                        else Modifier
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = LocalIndication.current,
                        role = Role.Tab,
                        onClick = onClick,
                    )
                    .padding(horizontal = if (variant == ChipVariant.Duration) 16.dp else 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    style = LumyrinthTypography.BodySm,
                    color = if (selected) Color.White else LumyrinthColors.TextSecondary,
                )
            }
        }

        ChipVariant.MoodCard -> {
            val resolvedIconTint = iconTint ?: LumyrinthColors.AccentPink
            val bgBrush = if (selected) {
                Brush.linearGradient(listOf(LumyrinthColors.AccentSuccess, LumyrinthColors.AccentSuccess))
            } else {
                Brush.verticalGradient(
                    listOf(
                        LumyrinthColors.SurfaceCard,
                        LumyrinthColors.BgElevated,
                    )
                )
            }

            val borderColor = if (selected) {
                Color.Transparent
            } else if (iconTint != null) {
                iconTint.copy(alpha = 0.25f)
            } else {
                LumyrinthColors.BorderSubtle
            }

            Column(
                modifier = modifier
                    .height(76.dp)
                    .scale(scale)
                    .clip(RoundedCornerShape(20.dp))
                    .background(bgBrush)
                    .border(1.dp, borderColor, RoundedCornerShape(20.dp))
                    .clickable(
                        interactionSource = interactionSource,
                        indication = LocalIndication.current,
                        role = Role.Button,
                        onClick = onClick,
                    )
                    .padding(vertical = 12.dp, horizontal = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (selected) Color.White else resolvedIconTint,
                        modifier = Modifier.size(22.dp),
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }
                Text(
                    text = label,
                    style = LumyrinthTypography.BodySm.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    color = if (selected) Color.White else LumyrinthColors.TextSecondary,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
fun StepperControl(
    value: Int,
    onValueChange: (Int) -> Unit,
    min: Int,
    max: Int,
    step: Int = 1,
    modifier: Modifier = Modifier,
    unitLabel: String? = null,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // Minus Button
        IconCircleButton(
            icon = Icons.Rounded.Remove,
            contentDescription = "Decrease",
            onClick = {
                if (value - step >= min) onValueChange(value - step)
            },
            size = 32.dp,
            iconSize = 16.dp,
        )

        // Value text
        Text(
            text = if (unitLabel != null) "$value $unitLabel" else "$value",
            style = LumyrinthTypography.H2,
            color = LumyrinthColors.TextPrimary,
            textAlign = TextAlign.Center,
        )

        // Plus Button
        IconCircleButton(
            icon = Icons.Rounded.Add,
            contentDescription = "Increase",
            onClick = {
                if (value + step <= max) onValueChange(value + step)
            },
            size = 32.dp,
            iconSize = 16.dp,
        )
    }
}
