package com.lumyrinth.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumyrinth.app.ui.theme.LumyrinthColors
import com.lumyrinth.app.ui.theme.LumyrinthThemeTokens
import com.lumyrinth.app.ui.theme.LumyrinthTypography
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun StandardCard(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(20.dp),
    cornerRadius: Dp = 20.dp,
    backgroundColor: Color = LumyrinthThemeTokens.palette.surfaceCard,
    borderColor: Color = LumyrinthThemeTokens.palette.borderSubtle,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = LocalIndication.current,
            role = Role.Button,
            onClick = onClick,
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(cornerRadius))
            .then(clickModifier)
            .padding(padding),
    ) {
        content()
    }
}

@Composable
fun FeatureCard(
    badge: String,
    duration: String,
    title: String,
    subtitle: String,
    onStart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.985f else 1.0f,
        label = "feature_card_scale",
    )

    val palette = LumyrinthThemeTokens.palette

    val cardBg = Brush.verticalGradient(
        listOf(
            palette.bgElevated,
            palette.surfaceCard,
            palette.bgBase,
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(26.dp),
                spotColor = palette.primaryAccent.copy(alpha = 0.35f),
                ambientColor = palette.secondaryAccent.copy(alpha = 0.35f),
            )
            .clip(RoundedCornerShape(26.dp))
            .background(cardBg)
            .border(
                1.dp,
                Brush.verticalGradient(
                    listOf(
                        palette.primaryAccent.copy(alpha = 0.50f),
                        palette.secondaryAccent.copy(alpha = 0.20f),
                    )
                ),
                RoundedCornerShape(26.dp),
            )
            .padding(22.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top badges row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Quick breathe label
                Text(
                    text = badge,
                    style = LumyrinthTypography.BodySm.copy(
                        color = LumyrinthColors.TextSecondary,
                        fontWeight = FontWeight.Medium,
                    ),
                )

                // Duration badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color(0x333B1869))
                        .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(999.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = duration,
                        style = LumyrinthTypography.Label.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                        color = Color.White.copy(alpha = 0.9f),
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                style = LumyrinthTypography.Display.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = LumyrinthColors.TextPrimary,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                style = LumyrinthTypography.Body.copy(
                    fontSize = 13.sp,
                    color = LumyrinthColors.TextSecondary,
                ),
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Start button + Play circular button row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                // Wide Start Pill Button
                val startInteractionSource = remember { MutableInteractionSource() }
                val isStartPressed by startInteractionSource.collectIsPressedAsState()
                val startScale by animateFloatAsState(
                    targetValue = if (isStartPressed) 0.97f else 1.0f,
                    label = "start_btn_scale",
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .scale(startScale)
                        .clip(RoundedCornerShape(999.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF381B68),
                                    Color(0xFF301556),
                                )
                            )
                        )
                        .border(1.dp, Color(0x40A855F7), RoundedCornerShape(999.dp))
                        .clickable(
                            interactionSource = startInteractionSource,
                            indication = LocalIndication.current,
                            role = Role.Button,
                            onClick = onStart,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Start",
                        style = LumyrinthTypography.Button.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                        ),
                        color = LumyrinthColors.TextPrimary,
                    )
                }

                // Circular play button
                val playInteractionSource = remember { MutableInteractionSource() }
                val isPlayPressed by playInteractionSource.collectIsPressedAsState()
                val playScale by animateFloatAsState(
                    targetValue = if (isPlayPressed) 0.92f else 1.0f,
                    label = "play_btn_scale",
                )

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .scale(playScale)
                        .clip(CircleShape)
                        .background(Color(0xFF22113D))
                        .border(1.dp, Color(0x40A855F7), CircleShape)
                        .clickable(
                            interactionSource = playInteractionSource,
                            indication = LocalIndication.current,
                            role = Role.Button,
                            onClick = onStart,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = "Start session",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun ListRowCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconTint: Color = LumyrinthColors.AccentPink,
    trailingBadge: String? = null,
    showChevron: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.985f else 1.0f,
        label = "row_card_scale",
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(LumyrinthColors.SurfaceCard)
            .border(1.dp, LumyrinthColors.BorderSubtle, RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Left icon or mini orb glyph
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center,
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp),
                )
            } else {
                Canvas(modifier = Modifier.size(20.dp)) {
                    drawCircle(
                        color = iconTint.copy(alpha = 0.8f),
                        radius = size.minDimension / 2.2f,
                        style = Stroke(width = 1.5f),
                    )
                    drawCircle(
                        color = Color.White,
                        radius = size.minDimension / 5f,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = LumyrinthTypography.H3,
                color = LumyrinthColors.TextPrimary,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = LumyrinthTypography.BodySm,
                color = LumyrinthColors.TextSecondary,
            )
        }

        if (trailingBadge != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(LumyrinthColors.OverlayWhite08)
                    .border(1.dp, LumyrinthColors.BorderSubtle, RoundedCornerShape(999.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = trailingBadge,
                    style = LumyrinthTypography.BodySm,
                    color = LumyrinthColors.TextPrimary,
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        if (showChevron) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = LumyrinthColors.TextTertiary,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
fun MiniAstrolabeOrb(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    primaryColor: Color = Color(0xFFA855F7),
    secondaryColor: Color = Color(0xFFF43F5E),
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mini_orb_anim")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "mini_orb_rot",
    )
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "mini_orb_pulse",
    )

    Canvas(modifier = modifier.size(size)) {
        val center = Offset(this.size.width / 2f, this.size.height / 2f)
        val maxRadius = this.size.minDimension / 2f

        // Ambient background glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    primaryColor.copy(alpha = 0.35f * pulse),
                    secondaryColor.copy(alpha = 0.15f * pulse),
                    Color.Transparent,
                ),
                center = center,
                radius = maxRadius * 0.95f,
            ),
            radius = maxRadius * 0.95f,
            center = center,
        )

        // Concentric rings (3 orbital rings)
        val ringRadii = listOf(maxRadius * 0.86f, maxRadius * 0.62f, maxRadius * 0.40f)
        val ringColors = listOf(
            secondaryColor.copy(alpha = 0.50f),
            primaryColor.copy(alpha = 0.65f),
            Color(0xFFE879F9).copy(alpha = 0.80f),
        )

        rotate(rotation, center) {
            for (i in ringRadii.indices) {
                val r = ringRadii[i]
                drawCircle(
                    color = ringColors[i],
                    radius = r,
                    center = center,
                    style = Stroke(
                        width = 1.1f,
                        pathEffect = if (i == 0) PathEffect.dashPathEffect(floatArrayOf(3f, 5f)) else null,
                    ),
                )

                // Small orbital star node
                val angle = (i * 120f) * (PI / 180f)
                val nodeX = center.x + r * cos(angle).toFloat()
                val nodeY = center.y + r * sin(angle).toFloat()
                drawCircle(
                    color = Color.White.copy(alpha = 0.9f),
                    radius = 1.6f,
                    center = Offset(nodeX, nodeY),
                )
            }
        }

        // Center glowing sun / star core
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White,
                    Color(0xFFF472B6),
                    Color.Transparent,
                ),
                center = center,
                radius = maxRadius * 0.28f * pulse,
            ),
            radius = maxRadius * 0.28f * pulse,
            center = center,
        )

        drawCircle(
            color = Color.White,
            radius = maxRadius * 0.12f,
            center = center,
        )
    }
}

@Composable
fun ExploreRhythmCard(
    title: String,
    patternText: String,
    durationText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    primaryColor: Color = Color(0xFFA855F7),
    secondaryColor: Color = Color(0xFFF43F5E),
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1.0f,
        label = "explore_card_scale",
    )

    val cardBg = Brush.horizontalGradient(
        listOf(
            Color(0xFF1B1033),
            Color(0xFF130D24),
            Color(0xFF160B28),
        )
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(78.dp)
            .scale(scale)
            .clip(RoundedCornerShape(22.dp))
            .background(cardBg)
            .border(
                1.dp,
                Brush.horizontalGradient(
                    listOf(
                        primaryColor.copy(alpha = 0.35f),
                        secondaryColor.copy(alpha = 0.15f),
                    )
                ),
                RoundedCornerShape(22.dp),
            )
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Left glowing mini astrolabe
        MiniAstrolabeOrb(
            primaryColor = primaryColor,
            secondaryColor = secondaryColor,
            size = 48.dp,
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Title and 2-line subtitle (Pattern + Duration)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = title,
                style = LumyrinthTypography.H3.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = LumyrinthColors.TextPrimary,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = patternText,
                style = LumyrinthTypography.BodySm.copy(
                    fontSize = 13.sp,
                    color = LumyrinthColors.TextSecondary,
                ),
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = durationText,
                style = LumyrinthTypography.BodySm.copy(
                    fontSize = 12.sp,
                    color = LumyrinthColors.TextTertiary,
                ),
            )
        }

        // Trailing action
        if (onEdit != null || onDelete != null) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (onEdit != null) {
                    IconCircleButton(
                        icon = Icons.Rounded.Edit,
                        contentDescription = "Edit $title",
                        onClick = onEdit,
                        size = 36.dp,
                        iconSize = 16.dp,
                    )
                }
                if (onDelete != null) {
                    IconCircleButton(
                        icon = Icons.Rounded.Delete,
                        contentDescription = "Delete $title",
                        tint = LumyrinthColors.AccentPink,
                        onClick = onDelete,
                        size = 36.dp,
                        iconSize = 16.dp,
                    )
                }
            }
        } else {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = "Select $title",
                tint = Color(0x66FFFFFF),
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
fun ContinueRhythmCard(
    title: String,
    durationText: String,
    onRepeat: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Rounded.Spa,
    iconTint: Color = Color(0xFFF43F5E),
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1.0f,
        label = "continue_card_scale",
    )

    val cardBg = Brush.horizontalGradient(
        listOf(
            Color(0xFF1B1033),
            Color(0xFF130D24),
            Color(0xFF180A22),
        )
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .scale(scale)
            .clip(RoundedCornerShape(22.dp))
            .background(cardBg)
            .border(
                1.dp,
                Brush.horizontalGradient(
                    listOf(
                        Color(0x559333EA),
                        Color(0x22F43F5E),
                    )
                ),
                RoundedCornerShape(22.dp),
            )
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                role = Role.Button,
                onClick = onRepeat,
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Icon container with neon outline
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0x1FF43F5E)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp),
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = LumyrinthTypography.H3.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = LumyrinthColors.TextPrimary,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = durationText,
                style = LumyrinthTypography.BodySm.copy(
                    fontSize = 13.sp,
                    color = LumyrinthColors.TextSecondary,
                ),
            )
        }

        // Repeat Pill Button
        val repeatInteractionSource = remember { MutableInteractionSource() }
        val isRepeatPressed by repeatInteractionSource.collectIsPressedAsState()
        val repeatScale by animateFloatAsState(
            targetValue = if (isRepeatPressed) 0.92f else 1.0f,
            label = "repeat_btn_scale",
        )

        Box(
            modifier = Modifier
                .scale(repeatScale)
                .clip(RoundedCornerShape(999.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xFF581C87),
                            Color(0xFF4C1D95),
                        )
                    )
                )
                .border(1.dp, Color(0x66A855F7), RoundedCornerShape(999.dp))
                .clickable(
                    interactionSource = repeatInteractionSource,
                    indication = LocalIndication.current,
                    role = Role.Button,
                    onClick = onRepeat,
                )
                .padding(horizontal = 14.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Repeat",
                style = LumyrinthTypography.BodySm.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                ),
                color = Color.White,
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            contentDescription = "Open session",
            tint = Color(0x66FFFFFF),
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
fun HomeProgressSummaryCard(
    sessionCount: Int,
    mindfulMinutes: Int,
    streakDays: Int,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "streak_flame_pulse")
    val flameGlow by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "flame_glow",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFF120E22))
            .border(1.dp, Color(0x26FFFFFF), RoundedCornerShape(22.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Left block: Today's progress + Sessions count
            Column(
                modifier = Modifier
                    .weight(1.3f)
                    .padding(end = 8.dp),
            ) {
                Text(
                    text = "Today's progress",
                    style = LumyrinthTypography.BodySm.copy(
                        fontSize = 12.sp,
                        color = LumyrinthColors.TextSecondary,
                    ),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = "$sessionCount",
                        style = LumyrinthTypography.StatNumber.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                        color = LumyrinthColors.TextPrimary,
                    )
                    Text(
                        text = "Sessions",
                        style = LumyrinthTypography.BodySm.copy(
                            fontSize = 12.sp,
                            color = LumyrinthColors.TextSecondary,
                        ),
                        modifier = Modifier.padding(bottom = 2.dp),
                    )
                }
            }

            // Subtle vertical separator
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(36.dp)
                    .background(Color(0x1AFFFFFF))
            )

            // Middle block: Mindful minutes
            Column(
                modifier = Modifier
                    .weight(1.1f)
                    .padding(horizontal = 12.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = "$mindfulMinutes min",
                    style = LumyrinthTypography.StatNumber.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = LumyrinthColors.TextPrimary,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Mindful",
                    style = LumyrinthTypography.BodySm.copy(
                        fontSize = 12.sp,
                        color = LumyrinthColors.TextSecondary,
                    ),
                )
            }

            // Subtle vertical separator
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(36.dp)
                    .background(Color(0x1AFFFFFF))
            )

            // Right block: Rhythm streak with glowing flame
            Row(
                modifier = Modifier
                    .weight(1.3f)
                    .padding(start = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = "$streakDays day",
                        style = LumyrinthTypography.StatNumber.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                        color = LumyrinthColors.TextPrimary,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Rhythm",
                        style = LumyrinthTypography.BodySm.copy(
                            fontSize = 12.sp,
                            color = LumyrinthColors.TextSecondary,
                        ),
                    )
                }

                // Glowing Flame Badge
                Box(
                    modifier = Modifier.size(34.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Canvas(modifier = Modifier.size(34.dp)) {
                        // Outer flame radial aura
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFF5722).copy(alpha = 0.5f * flameGlow.coerceIn(0.5f, 1f)),
                                    Color(0xFFE91E63).copy(alpha = 0.25f),
                                    Color.Transparent,
                                ),
                                center = center,
                                radius = size.minDimension / 1.5f,
                            ),
                            radius = size.minDimension / 1.5f,
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0xFFFF9800),
                                        Color(0xFFE64A19),
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.LocalFireDepartment,
                            contentDescription = "Rhythm streak",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    isFlameBadge: Boolean = false,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(LumyrinthColors.SurfaceCard)
            .border(1.dp, LumyrinthColors.BorderSubtle, RoundedCornerShape(20.dp))
            .padding(14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            if (isFlameBadge) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = value,
                        style = LumyrinthTypography.StatNumber,
                        color = LumyrinthColors.TextPrimary,
                    )
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(LumyrinthColors.StreakFlame),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.LocalFireDepartment,
                            contentDescription = "Streak Flame",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp),
                        )
                    }
                }
            } else {
                Text(
                    text = value,
                    style = LumyrinthTypography.StatNumber,
                    color = LumyrinthColors.TextPrimary,
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = label,
                style = LumyrinthTypography.BodySm,
                color = LumyrinthColors.TextSecondary,
            )
        }
    }
}

@Composable
fun PhaseCard(
    phaseName: String,
    phaseColor: Color,
    phaseBgColor: Color,
    seconds: Int,
    onSecondsChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(phaseBgColor)
            .border(1.dp, phaseColor.copy(alpha = 0.35f), RoundedCornerShape(28.dp))
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = phaseName,
            style = LumyrinthTypography.H3,
            color = LumyrinthColors.TextPrimary,
        )

        StepperControl(
            value = seconds,
            onValueChange = onSecondsChange,
            min = 0,
            max = 60,
            step = 1,
        )
    }
}

@Composable
fun PreferenceCard(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    showWaveform: Boolean = true,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(LumyrinthColors.SurfaceCard)
            .border(1.dp, LumyrinthColors.BorderSubtle, RoundedCornerShape(20.dp))
            .padding(20.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    style = LumyrinthTypography.H3,
                    color = LumyrinthColors.TextPrimary,
                )

                ToggleSwitch(
                    checked = checked,
                    onCheckedChange = onCheckedChange,
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                style = LumyrinthTypography.Body,
                color = LumyrinthColors.TextSecondary,
                modifier = Modifier.fillMaxWidth(0.85f),
            )

            if (showWaveform) {
                Spacer(modifier = Modifier.height(14.dp))
                WaveformGraphic(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp),
                )
            }
        }
    }
}

@Composable
fun WaveformGraphic(
    modifier: Modifier = Modifier,
    color: Color = LumyrinthColors.AccentPink.copy(alpha = 0.45f),
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val midY = height / 2f
        val path = Path()

        path.moveTo(0f, midY)
        val waveCount = 5
        val step = width / (waveCount * 20f)
        var x = 0f
        var index = 0

        while (x < width) {
            val progress = x / width
            val envelope = sin(progress * Math.PI).toFloat() // tapered at edges
            val y = midY + (sin(index * 0.35) * (height * 0.38f) * envelope).toFloat()
            path.lineTo(x, y)
            x += step
            index++
        }

        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 1.8f),
        )
    }
}
