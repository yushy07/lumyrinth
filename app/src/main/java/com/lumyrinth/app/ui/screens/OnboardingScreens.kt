package com.lumyrinth.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Adjust
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.CenterFocusStrong
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material.icons.rounded.LocalCafe
import androidx.compose.material.icons.rounded.NightlightRound
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.TrackChanges
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumyrinth.app.haptics.HapticController
import com.lumyrinth.app.ui.components.GhostButton
import com.lumyrinth.app.ui.components.GlowOrb
import com.lumyrinth.app.ui.components.IconCircleButton
import com.lumyrinth.app.ui.components.OrbAnimationState
import com.lumyrinth.app.ui.components.OrbCenterContent
import com.lumyrinth.app.ui.components.OrbSize
import com.lumyrinth.app.ui.components.PageIndicatorDots
import com.lumyrinth.app.ui.components.PreferenceCard
import com.lumyrinth.app.ui.components.PrimaryButton
import com.lumyrinth.app.ui.components.SelectableRow
import com.lumyrinth.app.ui.theme.LumyrinthColors
import com.lumyrinth.app.ui.theme.LumyrinthTypography
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.Waves
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import com.lumyrinth.app.R
import com.lumyrinth.app.ui.theme.LumyrinthThemeTokens
import kotlinx.coroutines.launch

data class OnboardingSlide(
    val categoryBadge: String,
    val title: String,
    val subtitle: String,
    val primaryBadge: Pair<ImageVector, String>,
    val secondaryBadge: Pair<ImageVector, String>,
    val tertiaryBadge: Pair<ImageVector, String>,
    val orbType: String,
)

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    onOpenTerms: () -> Unit = {},
    onOpenPrivacyPolicy: () -> Unit = {},
) {
    val context = LocalContext.current
    val hapticController = remember(context) { HapticController(context) }
    val palette = LumyrinthThemeTokens.palette
    val coroutineScope = rememberCoroutineScope()

    val slides = remember {
        listOf(
            OnboardingSlide(
                categoryBadge = "MINDFUL BREATHWORK",
                title = "Find your inner calm.",
                subtitle = "Harmonize your breath with celestial visuals, calming haptics, and soothing ambient soundscapes.",
                primaryBadge = Icons.Rounded.Waves to "4 Rhythms",
                secondaryBadge = Icons.Rounded.Vibration to "Haptic Cues",
                tertiaryBadge = Icons.Rounded.Lock to "100% Offline",
                orbType = "calm",
            ),
            OnboardingSlide(
                categoryBadge = "NEURO-RESPIRATORY PACING",
                title = "Evidence-based rhythms.",
                subtitle = "Scientifically backed breath cadences — Box Breathing, 4-7-8 Sleep, Resonance, and Awaken energy.",
                primaryBadge = Icons.Rounded.SelfImprovement to "Box & 4-7-8",
                secondaryBadge = Icons.Rounded.GraphicEq to "Resonance Pacing",
                tertiaryBadge = Icons.Rounded.AutoAwesome to "Custom Speeds",
                orbType = "rhythm",
            ),
            OnboardingSlide(
                categoryBadge = "MULTI-SENSORY REST",
                title = "Feel every breath cycle.",
                subtitle = "Gentle haptic pulses guide your lungs without looking at your screen, paired with procedural ambient audio.",
                primaryBadge = Icons.Rounded.Vibration to "Tactile Engine",
                secondaryBadge = Icons.Rounded.VolumeUp to "Sound Immersion",
                tertiaryBadge = Icons.Rounded.DarkMode to "OLED Dark Mode",
                orbType = "sensory",
            ),
            OnboardingSlide(
                categoryBadge = "PRIVATE & DISTRACTION FREE",
                title = "Your sacred sanctuary.",
                subtitle = "Zero ads, zero accounts required, and zero trackers. Pure mindfulness whenever you need a peaceful breath.",
                primaryBadge = Icons.Rounded.Lock to "Private by Design",
                secondaryBadge = Icons.Rounded.Spa to "Mindful Streaks",
                tertiaryBadge = Icons.Rounded.AutoAwesome to "Circadian Themes",
                orbType = "sanctuary",
            ),
        )
    }

    val pagerState = rememberPagerState(pageCount = { slides.size })

    // Entrance Animation
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        )
    }

    // Gentle ambient pulse for cosmic background
    val infiniteTransition = rememberInfiniteTransition(label = "welcome_ambient")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse_glow",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.bgBase)
            .drawBehind {
                val width = size.width
                val height = size.height

                // Top ambient aura behind logo
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            palette.primaryAccent.copy(alpha = 0.35f),
                            palette.primaryAccent.copy(alpha = 0.12f),
                            Color.Transparent,
                        ),
                        center = Offset(width * 0.5f, height * 0.12f),
                        radius = width * 0.55f * pulseGlow,
                    ),
                    radius = width * 0.55f * pulseGlow,
                    center = Offset(width * 0.5f, height * 0.12f),
                )

                // Center warm bloom behind hero orb
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            palette.secondaryAccent.copy(alpha = 0.30f),
                            palette.primaryAccent.copy(alpha = 0.10f),
                            Color.Transparent,
                        ),
                        center = Offset(width * 0.5f, height * 0.42f),
                        radius = width * 0.70f * pulseGlow,
                    ),
                    radius = width * 0.70f * pulseGlow,
                    center = Offset(width * 0.5f, height * 0.42f),
                )

                // Bottom subtle indigo ground
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            palette.primaryAccent.copy(alpha = 0.18f),
                            Color.Transparent,
                        ),
                        center = Offset(width * 0.5f, height * 0.95f),
                        radius = width * 0.6f,
                    ),
                    radius = width * 0.6f,
                    center = Offset(width * 0.5f, height * 0.95f),
                )

                // Subtle ambient stardust particles
                val stars = listOf(
                    Triple(0.18f, 0.16f, 1.8f),
                    Triple(0.82f, 0.20f, 2.2f),
                    Triple(0.12f, 0.55f, 1.5f),
                    Triple(0.88f, 0.60f, 2.0f),
                    Triple(0.25f, 0.78f, 1.4f),
                    Triple(0.75f, 0.82f, 1.8f),
                )
                for ((sx, sy, sr) in stars) {
                    drawCircle(
                        color = palette.secondaryAccent.copy(alpha = 0.75f),
                        radius = sr,
                        center = Offset(width * sx, height * sy),
                    )
                }
            }
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // 1. Top Header with App Wordmark & Skip Action
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(animProgress.value)
                .offset { IntOffset(0, ((1f - animProgress.value) * 24).toInt()) },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Invisible placeholder for balanced alignment
            Box(modifier = Modifier.size(48.dp))

            // Prominent Glowing App Wordmark
            Text(
                text = "L U M Y R I N T H",
                style = LumyrinthTypography.BrandTitle.copy(
                    fontSize = 24.sp,
                    lineHeight = 30.sp,
                    letterSpacing = 7.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    brush = Brush.linearGradient(
                        listOf(
                            Color.White,
                            Color(0xFFFFF1F2),
                            palette.secondaryAccent,
                            palette.primaryAccent,
                        )
                    ),
                ),
                textAlign = TextAlign.Center,
            )

            // Skip button if not on last page
            if (pagerState.currentPage < slides.size - 1) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = LocalIndication.current,
                            role = Role.Button,
                            onClick = {
                                hapticController.tick()
                                onGetStarted()
                            },
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Skip",
                        style = LumyrinthTypography.Label.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.textTertiary,
                        ),
                    )
                }
            } else {
                Box(modifier = Modifier.size(48.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Interactive Swipeable Carousel Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) { page ->
            val slide = slides[page]

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        // Smooth parallax depth effect during swipe
                        val pageOffset = (
                            (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                        )
                        alpha = 1f - kotlin.math.abs(pageOffset).coerceIn(0f, 0.7f)
                        val scaleFactor = 0.88f + (1f - kotlin.math.abs(pageOffset)).coerceIn(0f, 1f) * 0.12f
                        scaleX = scaleFactor
                        scaleY = scaleFactor
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                // Category pill badge for current slide
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(palette.primaryAccent.copy(alpha = 0.18f))
                        .border(1.dp, palette.secondaryAccent.copy(alpha = 0.35f), RoundedCornerShape(999.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = palette.secondaryAccent,
                        modifier = Modifier.size(13.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = slide.categoryBadge,
                        style = LumyrinthTypography.Label.copy(
                            letterSpacing = 2.0.sp,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFCE7F3),
                        ),
                    )
                }

                // Center Distinctive Celestial Centerpiece per Slide
                Box(
                    modifier = Modifier
                        .alpha(animProgress.value)
                        .scale(0.92f + 0.08f * animProgress.value),
                    contentAlignment = Alignment.Center,
                ) {
                    when (slide.orbType) {
                        "calm" -> OnboardingCalmOrb()
                        "rhythm" -> OnboardingRhythmAstrolabe()
                        "sensory" -> OnboardingSensoryWaves()
                        "sanctuary" -> OnboardingSanctuaryConstellation()
                        else -> OnboardingCalmOrb()
                    }
                }

                // Headline, Description & Feature Badges for slide
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = slide.title,
                        style = LumyrinthTypography.H1.copy(
                            fontSize = 26.sp,
                            lineHeight = 32.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.3).sp,
                        ),
                        color = palette.textPrimary,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = slide.subtitle,
                        style = LumyrinthTypography.Body.copy(
                            fontSize = 13.5.sp,
                            lineHeight = 20.sp,
                            color = palette.textSecondary,
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Feature Badges Row for current slide
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        FeatureBadge(
                            icon = slide.primaryBadge.first,
                            label = slide.primaryBadge.second,
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        FeatureBadge(
                            icon = slide.secondaryBadge.first,
                            label = slide.secondaryBadge.second,
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        FeatureBadge(
                            icon = slide.tertiaryBadge.first,
                            label = slide.tertiaryBadge.second,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 3. Interactive Carousel Dots Indicator with Click to Jump
        Row(
            modifier = Modifier.padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            for (i in 0 until slides.size) {
                val isActive = i == pagerState.currentPage
                val dotWidth by animateDpAsState(
                    targetValue = if (isActive) 24.dp else 7.dp,
                    animationSpec = spring(dampingRatio = 0.75f, stiffness = 400f),
                    label = "dot_width_$i",
                )
                val dotColor by animateColorAsState(
                    targetValue = if (isActive) palette.secondaryAccent else palette.borderMedium,
                    label = "dot_color_$i",
                )

                Box(
                    modifier = Modifier
                        .height(7.dp)
                        .width(dotWidth)
                        .clip(CircleShape)
                        .background(dotColor)
                        .clickable {
                            coroutineScope.launch {
                                hapticController.tick()
                                pagerState.animateScrollToPage(i)
                            }
                        }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 4. Bottom CTA & Next/Get Started Button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(animProgress.value)
                .offset { IntOffset(0, ((1f - animProgress.value) * 16).toInt()) },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val isLastPage = pagerState.currentPage == slides.size - 1
            val buttonLabel = if (isLastPage) "Get Started" else "Continue"
            val buttonIcon = if (isLastPage) Icons.Rounded.AutoAwesome else Icons.AutoMirrored.Rounded.ArrowForward

            PrimaryButton(
                label = buttonLabel,
                icon = buttonIcon,
                onClick = {
                    hapticController.tick()
                    if (isLastPage) {
                        onGetStarted()
                    } else {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                backgroundBrush = palette.gradientPrimary,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Non-blocking consent line
            Row(
                modifier = Modifier.padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "By continuing, you agree to our ",
                    style = LumyrinthTypography.Label.copy(
                        fontSize = 11.sp,
                        color = palette.textTertiary,
                    ),
                )
                Text(
                    text = "Terms",
                    style = LumyrinthTypography.Label.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.secondaryAccent,
                    ),
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = LocalIndication.current,
                            role = Role.Button,
                            onClick = onOpenTerms,
                        )
                        .testTag("welcome_terms_link"),
                )
                Text(
                    text = " & ",
                    style = LumyrinthTypography.Label.copy(
                        fontSize = 11.sp,
                        color = palette.textTertiary,
                    ),
                )
                Text(
                    text = "Privacy Policy",
                    style = LumyrinthTypography.Label.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.secondaryAccent,
                    ),
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = LocalIndication.current,
                            role = Role.Button,
                            onClick = onOpenPrivacyPolicy,
                        )
                        .testTag("welcome_privacy_policy_link"),
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun FeatureBadge(
    icon: ImageVector,
    label: String,
) {
    val palette = LumyrinthThemeTokens.palette

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(palette.surfaceCard.copy(alpha = 0.65f))
            .border(1.dp, palette.borderSubtle, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = palette.primaryAccent,
            modifier = Modifier.size(13.dp),
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = label,
            style = LumyrinthTypography.BodySm.copy(
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                color = palette.textPrimary,
            ),
        )
    }
}

/** 1. Calm Breathing Lotus Orb **/
@Composable
fun OnboardingCalmOrb(
    modifier: Modifier = Modifier,
) {
    val palette = LumyrinthThemeTokens.palette
    val infiniteTransition = rememberInfiniteTransition(label = "calm_orb_trans")

    val breatheScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "calm_breathe",
    )

    val orbitAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "calm_orbit",
    )

    Canvas(
        modifier = modifier.size(240.dp),
    ) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val maxRadius = size.width * 0.46f

        // 1. Broad outer atmospheric bloom
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    palette.primaryAccent.copy(alpha = 0.35f),
                    palette.secondaryAccent.copy(alpha = 0.12f),
                    Color.Transparent,
                ),
                center = Offset(cx, cy),
                radius = maxRadius * breatheScale * 1.15f,
            ),
            radius = maxRadius * breatheScale * 1.15f,
            center = Offset(cx, cy),
        )

        // 2. Secondary luminous aura
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    palette.secondaryAccent.copy(alpha = 0.45f),
                    palette.primaryAccent.copy(alpha = 0.18f),
                    Color.Transparent,
                ),
                center = Offset(cx, cy),
                radius = maxRadius * 0.75f * breatheScale,
            ),
            radius = maxRadius * 0.75f * breatheScale,
            center = Offset(cx, cy),
        )

        // 3. Shimmering halo rings
        drawCircle(
            color = palette.secondaryAccent.copy(alpha = 0.35f),
            radius = maxRadius * 0.82f * breatheScale,
            center = Offset(cx, cy),
            style = Stroke(
                width = 1.5.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f),
            ),
        )

        drawCircle(
            color = palette.primaryAccent.copy(alpha = 0.45f),
            radius = maxRadius * 0.62f,
            center = Offset(cx, cy),
            style = Stroke(width = 1.dp.toPx()),
        )

        // 4. Orbiting stardust satellites
        val numSatellites = 6
        for (i in 0 until numSatellites) {
            val angleDeg = orbitAngle + (i * 360f / numSatellites)
            val rad = angleDeg * (PI.toFloat() / 180f)
            val dist = maxRadius * (0.65f + 0.18f * sin(rad * 2f))
            val sx = cx + dist * cos(rad)
            val sy = cy + dist * sin(rad)
            val dotRadius = if (i % 2 == 0) 3.5.dp.toPx() else 2.2.dp.toPx()

            drawCircle(
                color = Color.White.copy(alpha = 0.85f),
                radius = dotRadius,
                center = Offset(sx, sy),
            )
            drawCircle(
                color = palette.secondaryAccent.copy(alpha = 0.5f),
                radius = dotRadius * 2.2f,
                center = Offset(sx, sy),
            )
        }

        // 5. Central glowing core
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White,
                    palette.secondaryAccent,
                    palette.primaryAccent,
                ),
                center = Offset(cx, cy),
                radius = maxRadius * 0.28f * breatheScale,
            ),
            radius = maxRadius * 0.28f * breatheScale,
            center = Offset(cx, cy),
        )

        // Ultra-bright central pinpoint
        drawCircle(
            color = Color.White,
            radius = maxRadius * 0.10f * breatheScale,
            center = Offset(cx, cy),
        )
    }
}

/** 2. Sacred Neuro-Respiratory Astrolabe **/
@Composable
fun OnboardingRhythmAstrolabe(
    modifier: Modifier = Modifier,
) {
    val palette = LumyrinthThemeTokens.palette
    val infiniteTransition = rememberInfiniteTransition(label = "astrolabe_trans")

    val outerRot by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "astrolabe_outer_rot",
    )

    val innerRot by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "astrolabe_inner_rot",
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.90f,
        targetValue = 1.10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "astrolabe_pulse",
    )

    Canvas(
        modifier = modifier.size(240.dp),
    ) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val maxR = size.width * 0.45f

        // Ambient radial background glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    palette.primaryAccent.copy(alpha = 0.32f),
                    palette.secondaryAccent.copy(alpha = 0.10f),
                    Color.Transparent,
                ),
                center = Offset(cx, cy),
                radius = maxR * 1.15f * pulseScale,
            ),
            radius = maxR * 1.15f * pulseScale,
            center = Offset(cx, cy),
        )

        // Outer Gear Astrolabe Ring (Clockwise)
        rotate(outerRot, pivot = Offset(cx, cy)) {
            drawCircle(
                color = palette.borderMedium,
                radius = maxR * 0.92f,
                center = Offset(cx, cy),
                style = Stroke(
                    width = 1.2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 8f), 0f),
                ),
            )

            // 4 Cardinal Quadrant Nodes (Inhale, Hold In, Exhale, Hold Out)
            for (i in 0 until 4) {
                val ang = i * 90f * (PI.toFloat() / 180f)
                val nodeX = cx + maxR * 0.92f * cos(ang)
                val nodeY = cy + maxR * 0.92f * sin(ang)

                drawCircle(
                    color = palette.secondaryAccent,
                    radius = 4.dp.toPx(),
                    center = Offset(nodeX, nodeY),
                )
                drawCircle(
                    color = Color.White,
                    radius = 2.dp.toPx(),
                    center = Offset(nodeX, nodeY),
                )
            }
        }

        // Middle Concentric Rhythm Ring
        drawCircle(
            color = palette.primaryAccent.copy(alpha = 0.5f),
            radius = maxR * 0.72f * pulseScale,
            center = Offset(cx, cy),
            style = Stroke(width = 1.5.dp.toPx()),
        )

        // Inner Sacred Cadence Ring (Counter-Clockwise)
        rotate(innerRot, pivot = Offset(cx, cy)) {
            drawCircle(
                color = palette.secondaryAccent.copy(alpha = 0.45f),
                radius = maxR * 0.50f,
                center = Offset(cx, cy),
                style = Stroke(
                    width = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f),
                ),
            )

            // Geometric 8-pointed sacred star lines
            val pts = 8
            val path = Path()
            for (p in 0 until pts) {
                val r = if (p % 2 == 0) maxR * 0.48f else maxR * 0.28f
                val ang = (p * (360f / pts)) * (PI.toFloat() / 180f)
                val px = cx + r * cos(ang)
                val py = cy + r * sin(ang)
                if (p == 0) path.moveTo(px, py) else path.lineTo(px, py)
            }
            path.close()

            drawPath(
                path = path,
                color = palette.secondaryAccent.copy(alpha = 0.35f),
                style = Stroke(width = 1.2.dp.toPx()),
            )
        }

        // Central Pulsing Cadence Core
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White,
                    palette.primaryAccent,
                    Color.Transparent,
                ),
                center = Offset(cx, cy),
                radius = maxR * 0.22f * pulseScale,
            ),
            radius = maxR * 0.22f * pulseScale,
            center = Offset(cx, cy),
        )

        drawCircle(
            color = Color.White,
            radius = maxR * 0.08f * pulseScale,
            center = Offset(cx, cy),
        )
    }
}

/** 3. Multi-Sensory Tactile & Acoustic Soundwave Ripples **/
@Composable
fun OnboardingSensoryWaves(
    modifier: Modifier = Modifier,
) {
    val palette = LumyrinthThemeTokens.palette
    val infiniteTransition = rememberInfiniteTransition(label = "sensory_waves_trans")

    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "wave_phase",
    )

    val barPulse by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bar_pulse",
    )

    Canvas(
        modifier = modifier.size(240.dp),
    ) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val maxR = size.width * 0.45f

        // Ambient soundscape glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    palette.secondaryAccent.copy(alpha = 0.30f),
                    palette.primaryAccent.copy(alpha = 0.12f),
                    Color.Transparent,
                ),
                center = Offset(cx, cy),
                radius = maxR * 1.15f,
            ),
            radius = maxR * 1.15f,
            center = Offset(cx, cy),
        )

        // 4 Propagating Acoustic Ripple Rings
        val rippleCount = 4
        for (i in 0 until rippleCount) {
            val progress = (wavePhase + i.toFloat() / rippleCount) % 1f
            val r = maxR * 0.20f + maxR * 0.75f * progress
            val alpha = (1f - progress).coerceIn(0f, 1f) * 0.60f

            drawCircle(
                color = palette.secondaryAccent.copy(alpha = alpha),
                radius = r,
                center = Offset(cx, cy),
                style = Stroke(
                    width = (2.2f * (1f - progress * 0.5f)).dp.toPx(),
                    pathEffect = if (i % 2 == 1) PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f) else null,
                ),
            )
        }

        // Haptic Vibration Orbit Dots (tactile feedback pulses)
        val hapticNodes = 12
        for (j in 0 until hapticNodes) {
            val angle = j * (360f / hapticNodes) * (PI.toFloat() / 180f)
            val vibrationOffset = sin(wavePhase * 2f * PI.toFloat() + j) * 4.dp.toPx()
            val r = maxR * 0.68f + vibrationOffset
            val nx = cx + r * cos(angle)
            val ny = cy + r * sin(angle)

            drawCircle(
                color = palette.primaryAccent.copy(alpha = 0.75f),
                radius = 2.5.dp.toPx(),
                center = Offset(nx, ny),
            )
        }

        // Center Acoustic Equalizer Waves
        val barCount = 7
        val barWidth = 3.5.dp.toPx()
        val spacing = 5.dp.toPx()
        val totalW = barCount * barWidth + (barCount - 1) * spacing
        val startX = cx - totalW / 2f

        for (k in 0 until barCount) {
            val factor = sin((k.toFloat() / (barCount - 1)) * PI.toFloat())
            val barH = (maxR * 0.32f) * (0.35f + 0.65f * factor * barPulse)
            val bx = startX + k * (barWidth + spacing)

            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White,
                        palette.secondaryAccent,
                        palette.primaryAccent,
                    ),
                    startY = cy - barH,
                    endY = cy + barH,
                ),
                topLeft = Offset(bx, cy - barH),
                size = androidx.compose.ui.geometry.Size(barWidth, barH * 2f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(999f, 999f),
            )
        }
    }
}

/** 4. Sacred Constellation & Cosmic Sanctuary Shield **/
@Composable
fun OnboardingSanctuaryConstellation(
    modifier: Modifier = Modifier,
) {
    val palette = LumyrinthThemeTokens.palette
    val infiniteTransition = rememberInfiniteTransition(label = "sanctuary_trans")

    val constellationRot by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 26000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "constellation_rot",
    )

    val shieldGlow by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "shield_glow",
    )

    Canvas(
        modifier = modifier.size(240.dp),
    ) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val maxR = size.width * 0.45f

        // 1. Deep protective radiant sanctuary aura
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    palette.primaryAccent.copy(alpha = 0.35f),
                    palette.secondaryAccent.copy(alpha = 0.12f),
                    Color.Transparent,
                ),
                center = Offset(cx, cy),
                radius = maxR * 1.15f * shieldGlow,
            ),
            radius = maxR * 1.15f * shieldGlow,
            center = Offset(cx, cy),
        )

        // 2. Outer Protective Sanctuary Hexagon / Shield Ring
        drawCircle(
            color = palette.secondaryAccent.copy(alpha = 0.40f),
            radius = maxR * 0.90f * shieldGlow,
            center = Offset(cx, cy),
            style = Stroke(
                width = 1.5.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f),
            ),
        )

        // 3. Rotating Constellation Starlight Web
        rotate(constellationRot, pivot = Offset(cx, cy)) {
            val starNodes = listOf(
                Pair(0f, 0.80f),
                Pair(60f, 0.70f),
                Pair(120f, 0.82f),
                Pair(180f, 0.72f),
                Pair(240f, 0.80f),
                Pair(300f, 0.68f),
            )

            // Draw constellation lines connecting outer stars to each other and to center
            val starPositions = starNodes.map { (deg, distFraction) ->
                val rad = deg * (PI.toFloat() / 180f)
                val r = maxR * distFraction
                Offset(cx + r * cos(rad), cy + r * sin(rad))
            }

            for (i in starPositions.indices) {
                val p1 = starPositions[i]
                val p2 = starPositions[(i + 1) % starPositions.size]
                // Inter-star line
                drawLine(
                    color = palette.secondaryAccent.copy(alpha = 0.35f),
                    start = p1,
                    end = p2,
                    strokeWidth = 1.dp.toPx(),
                    cap = StrokeCap.Round,
                )
                // Center anchor starlight beam
                drawLine(
                    color = palette.primaryAccent.copy(alpha = 0.25f),
                    start = Offset(cx, cy),
                    end = p1,
                    strokeWidth = 0.8.dp.toPx(),
                    cap = StrokeCap.Round,
                )
            }

            // Draw star gems at each vertex
            for (pos in starPositions) {
                drawCircle(
                    color = palette.secondaryAccent.copy(alpha = 0.4f),
                    radius = 5.dp.toPx(),
                    center = pos,
                )
                drawCircle(
                    color = Color.White,
                    radius = 2.5.dp.toPx(),
                    center = pos,
                )
            }
        }

        // 4. Inner Crystalline Diamond Shield
        rotate(-constellationRot * 0.6f, pivot = Offset(cx, cy)) {
            val diamondPath = Path().apply {
                moveTo(cx, cy - maxR * 0.42f)
                lineTo(cx + maxR * 0.42f, cy)
                lineTo(cx, cy + maxR * 0.42f)
                lineTo(cx - maxR * 0.42f, cy)
                close()
            }

            drawPath(
                path = diamondPath,
                color = palette.primaryAccent.copy(alpha = 0.45f),
                style = Stroke(width = 1.5.dp.toPx()),
            )
        }

        // 5. Central Sanctuary Radiant Core
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White,
                    palette.secondaryAccent,
                    palette.primaryAccent,
                    Color.Transparent,
                ),
                center = Offset(cx, cy),
                radius = maxR * 0.26f * shieldGlow,
            ),
            radius = maxR * 0.26f * shieldGlow,
            center = Offset(cx, cy),
        )

        drawCircle(
            color = Color.White,
            radius = maxR * 0.09f * shieldGlow,
            center = Offset(cx, cy),
        )
    }
}

data class GoalOption(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val accentColor: Color,
)

@Composable
fun GoalsScreen(
    initialGoals: Set<String>,
    onBack: () -> Unit,
    onNext: (Set<String>) -> Unit,
) {
    // Default selection to match the visual mock if empty
    val defaultInitial = if (initialGoals.isEmpty()) {
        setOf("relax", "focus", "unwind", "build_habit")
    } else {
        initialGoals
    }
    var selectedGoals by remember { mutableStateOf(defaultInitial) }

    val goalsList = listOf(
        GoalOption("relax", "Relax", Icons.Rounded.Spa, Color(0xFFF43F5E)),
        GoalOption("focus", "Focus", Icons.Rounded.CenterFocusStrong, Color(0xFFFB7185)),
        GoalOption("unwind", "Unwind", Icons.Rounded.NightlightRound, Color(0xFFA855F7)),
        GoalOption("sleep", "Sleep", Icons.Rounded.Bedtime, Color(0xFFC084FC)),
        GoalOption("break", "Take a quick break", Icons.Rounded.Coffee, Color(0xFFEC4899)),
        GoalOption("build_habit", "Build a habit", Icons.Rounded.Eco, Color(0xFF4ADE80)),
    )

    // Staggered entrance animation
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LumyrinthColors.BgBase)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        // Top Back arrow button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconCircleButton(
                icon = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Back",
                onClick = onBack,
                size = 40.dp,
                iconSize = 20.dp,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Centered Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(animProgress.value)
                .offset { IntOffset(0, ((1f - animProgress.value) * 30).toInt()) },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "What are you\nlooking for?",
                style = LumyrinthTypography.H1.copy(
                    fontSize = 26.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = LumyrinthColors.TextPrimary,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Choose what matters most to you.",
                style = LumyrinthTypography.Body.copy(
                    fontSize = 14.sp,
                    color = LumyrinthColors.TextSecondary,
                ),
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Goals List with staggered animations
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            goalsList.forEachIndexed { index, goal ->
                val isSelected = goal.id in selectedGoals

                // Staggered calculation for each item
                val itemDelayProgress = (animProgress.value * 1.4f - (index * 0.08f)).coerceIn(0f, 1f)

                GoalItemCard(
                    goal = goal,
                    isSelected = isSelected,
                    onToggle = {
                        selectedGoals = if (isSelected) {
                            selectedGoals - goal.id
                        } else {
                            selectedGoals + goal.id
                        }
                    },
                    modifier = Modifier
                        .alpha(itemDelayProgress)
                        .offset { IntOffset(0, ((1f - itemDelayProgress) * 40).toInt()) },
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom Next Button
        PrimaryButton(
            label = "Next",
            onClick = { onNext(selectedGoals) },
            enabled = selectedGoals.isNotEmpty(),
            backgroundBrush = LumyrinthColors.GradientButton,
            modifier = Modifier
                .fillMaxWidth()
                .alpha(animProgress.value)
                .offset { IntOffset(0, ((1f - animProgress.value) * 20).toInt()) },
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun GoalItemCard(
    goal: GoalOption,
    isSelected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.975f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "goal_card_scale",
    )

    val cardBgColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF16122C) else Color(0xFF110E22),
        animationSpec = tween(durationMillis = 200),
        label = "goal_card_bg",
    )

    val cardBorderColor by animateColorAsState(
        targetValue = if (isSelected) Color(0x409333EA) else Color(0x1AFFFFFF),
        animationSpec = tween(durationMillis = 200),
        label = "goal_card_border",
    )

    val checkboxBgColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF9333EA) else Color(0x1F2D2545),
        animationSpec = tween(durationMillis = 200),
        label = "checkbox_bg",
    )

    val checkboxBorderColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF9333EA) else Color(0x336D5B8D),
        animationSpec = tween(durationMillis = 200),
        label = "checkbox_border",
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(cardBgColor)
            .border(1.dp, cardBorderColor, RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                role = Role.Checkbox,
                onClick = onToggle,
            )
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Neon-accented Outline Icon
        Box(
            modifier = Modifier.size(36.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = goal.icon,
                contentDescription = null,
                tint = goal.accentColor,
                modifier = Modifier.size(24.dp),
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Goal Title
        Text(
            text = goal.label,
            style = LumyrinthTypography.H3.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            ),
            color = LumyrinthColors.TextPrimary,
            modifier = Modifier.weight(1f),
        )

        // Squircle Rounded Checkbox
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(checkboxBgColor)
                .border(1.5.dp, checkboxBorderColor, RoundedCornerShape(7.dp)),
            contentAlignment = Alignment.Center,
        ) {
            androidx.compose.animation.AnimatedVisibility(
                visible = isSelected,
                enter = scaleIn(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium,
                    )
                ) + fadeIn(tween(150)),
                exit = scaleOut(tween(100)) + fadeOut(tween(100)),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier.size(17.dp),
                )
            }
        }
    }
}

@Composable
fun PreferencesScreen(
    initialHaptics: Boolean,
    initialSound: Boolean,
    onBack: () -> Unit,
    onNext: (haptics: Boolean, sound: Boolean) -> Unit,
) {
    val context = LocalContext.current
    val hapticController = remember(context) { HapticController(context) }

    var hapticsEnabled by remember { mutableStateOf(initialHaptics) }
    var soundEnabled by remember { mutableStateOf(initialSound) }

    // Staggered entrance animation
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LumyrinthColors.BgBase)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        // Back navigation button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconCircleButton(
                icon = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Back",
                onClick = onBack,
                size = 40.dp,
                iconSize = 20.dp,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Centered Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(animProgress.value)
                .offset { IntOffset(0, ((1f - animProgress.value) * 30).toInt()) },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Set your preferences",
                style = LumyrinthTypography.H1.copy(
                    fontSize = 26.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = LumyrinthColors.TextPrimary,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "You can change these anytime.",
                style = LumyrinthTypography.Body.copy(
                    fontSize = 14.sp,
                    color = LumyrinthColors.TextSecondary,
                ),
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Cards Column
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // 1. Haptic guidance card
            val hapticDelayProgress = (animProgress.value * 1.3f - 0.05f).coerceIn(0f, 1f)
            HapticPreferenceCard(
                checked = hapticsEnabled,
                onCheckedChange = {
                    hapticsEnabled = it
                    hapticController.tick()
                },
                modifier = Modifier
                    .alpha(hapticDelayProgress)
                    .offset { IntOffset(0, ((1f - hapticDelayProgress) * 40).toInt()) },
            )

            // 2. Sound guidance card
            val soundDelayProgress = (animProgress.value * 1.3f - 0.15f).coerceIn(0f, 1f)
            SoundPreferenceCard(
                checked = soundEnabled,
                onCheckedChange = {
                    soundEnabled = it
                    hapticController.tick()
                },
                modifier = Modifier
                    .alpha(soundDelayProgress)
                    .offset { IntOffset(0, ((1f - soundDelayProgress) * 40).toInt()) },
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom Next Button
        PrimaryButton(
            label = "Next",
            onClick = { onNext(hapticsEnabled, soundEnabled) },
            backgroundBrush = LumyrinthColors.GradientButton,
            modifier = Modifier
                .fillMaxWidth()
                .alpha(animProgress.value)
                .offset { IntOffset(0, ((1f - animProgress.value) * 20).toInt()) },
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun HapticPreferenceCard(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val cardBg = Brush.verticalGradient(
        listOf(
            Color(0xFF28114C),
            Color(0xFF140D2A),
            Color(0xFF190C28),
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(cardBg)
            .border(
                1.dp,
                Brush.verticalGradient(
                    listOf(
                        Color(0x66A855F7),
                        Color(0x26FF5277),
                    )
                ),
                RoundedCornerShape(24.dp),
            )
            .padding(horizontal = 20.dp, vertical = 18.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row: Text + Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                    Text(
                        text = "Haptic guidance",
                        style = LumyrinthTypography.H3.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                        color = LumyrinthColors.TextPrimary,
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Feel inhale and exhale\nwith gentle vibrations.",
                        style = LumyrinthTypography.Body.copy(
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = LumyrinthColors.TextSecondary,
                        ),
                    )
                }

                PurplePillSwitch(
                    checked = checked,
                    onCheckedChange = onCheckedChange,
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Animated Dotted Waveform
            AnimatedDottedWaveform(
                enabled = checked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
            )
        }
    }
}

@Composable
fun SoundPreferenceCard(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val cardBg = Brush.verticalGradient(
        listOf(
            Color(0xFF28114C),
            Color(0xFF140D2A),
            Color(0xFF190C28),
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(cardBg)
            .border(
                1.dp,
                Brush.verticalGradient(
                    listOf(
                        Color(0x66A855F7),
                        Color(0x26FF5277),
                    )
                ),
                RoundedCornerShape(24.dp),
            )
            .padding(horizontal = 20.dp, vertical = 18.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row: Text + Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                    Text(
                        text = "Sound guidance",
                        style = LumyrinthTypography.H3.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                        color = LumyrinthColors.TextPrimary,
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Play subtle sounds during\nyour sessions.",
                        style = LumyrinthTypography.Body.copy(
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = LumyrinthColors.TextSecondary,
                        ),
                    )
                }

                PurplePillSwitch(
                    checked = checked,
                    onCheckedChange = onCheckedChange,
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Animated Equalizer Audio Spectrum
            AnimatedAudioEqualizerSpectrum(
                enabled = checked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
            )
        }
    }
}

@Composable
fun PurplePillSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 22.dp else 3.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "thumb_offset",
    )

    val trackBgColor by animateColorAsState(
        targetValue = if (checked) Color(0xFF9333EA) else Color(0xFF261D3B),
        animationSpec = tween(durationMillis = 200),
        label = "switch_track_bg",
    )

    val trackBorderColor by animateColorAsState(
        targetValue = if (checked) Color(0xFFA855F7) else Color(0x33FFFFFF),
        animationSpec = tween(durationMillis = 200),
        label = "switch_track_border",
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        label = "switch_scale",
    )

    Box(
        modifier = modifier
            .scale(scale)
            .width(48.dp)
            .height(28.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(trackBgColor)
            .border(1.dp, trackBorderColor, RoundedCornerShape(999.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                role = Role.Switch,
                onClick = { onCheckedChange(!checked) },
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

@Composable
fun AnimatedDottedWaveform(
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "haptic_wave")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "wave_phase",
    )

    val targetAlpha = if (enabled) 1.0f else 0.25f
    val animatedAlpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = 300),
        label = "wave_alpha",
    )

    val targetAmplitude = if (enabled) 1.0f else 0.2f
    val animatedAmplitude by animateFloatAsState(
        targetValue = targetAmplitude,
        animationSpec = tween(durationMillis = 300),
        label = "wave_amp",
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val midY = height / 2f

        val totalDots = 42
        val step = width / (totalDots - 1)

        // Draw flowing dotted wave
        for (i in 0 until totalDots) {
            val x = i * step
            val normX = (x / width).coerceIn(0f, 1f)
            // Gaussian envelope so it tapers at left and right edges
            val envelope = sin(normX * PI).toFloat()

            // Primary wave
            val primaryAngle = normX * 3.2f * PI + phase
            val yOffset1 = (sin(primaryAngle) * (height * 0.36f) * envelope * animatedAmplitude).toFloat()
            val y1 = midY + yOffset1

            // Secondary harmonic counter-wave
            val secondaryAngle = normX * 4.5f * PI - phase * 0.8f
            val yOffset2 = (cos(secondaryAngle) * (height * 0.22f) * envelope * animatedAmplitude).toFloat()
            val y2 = midY + yOffset2

            val dotRadius = (1.4f + (envelope * 0.8f))
            val dotAlpha = ((0.45f + (envelope * 0.55f)) * animatedAlpha).coerceIn(0f, 1f)

            val color1 = when {
                i % 3 == 0 -> Color(0xFFF43F5E) // Pink
                i % 3 == 1 -> Color(0xFFE879F9) // Magenta
                else -> Color(0xFFC084FC) // Lavender
            }

            // Draw primary dot
            drawCircle(
                color = color1.copy(alpha = dotAlpha),
                radius = dotRadius,
                center = Offset(x, y1),
            )

            // Draw secondary subtle counter-dot
            if (i % 2 == 0) {
                drawCircle(
                    color = Color(0xFFA855F7).copy(alpha = dotAlpha * 0.5f),
                    radius = dotRadius * 0.8f,
                    center = Offset(x, y2),
                )
            }

            // Glowing focal star node near center crest (around index 20-22)
            if (i == totalDots / 2) {
                // Radial glow halo
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFF5277).copy(alpha = 0.65f * animatedAlpha),
                            Color(0xFFD946EF).copy(alpha = 0.35f * animatedAlpha),
                            Color.Transparent,
                        ),
                        center = Offset(x, y1),
                        radius = 16f,
                    ),
                    radius = 16f,
                    center = Offset(x, y1),
                )

                // White/pink bright central star particle
                drawCircle(
                    color = Color.White.copy(alpha = animatedAlpha),
                    radius = 3.2f,
                    center = Offset(x, y1),
                )
            }
        }
    }
}

@Composable
fun AnimatedAudioEqualizerSpectrum(
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sound_spectrum")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "spectrum_time",
    )

    val targetAlpha = if (enabled) 1.0f else 0.25f
    val animatedAlpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = 300),
        label = "spectrum_alpha",
    )

    val targetActivity = if (enabled) 1.0f else 0.15f
    val animatedActivity by animateFloatAsState(
        targetValue = targetActivity,
        animationSpec = tween(durationMillis = 300),
        label = "spectrum_act",
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val midY = height / 2f

        val barCount = 38
        val barWidth = 3.2.dp.toPx()
        val spacing = (width - (barCount * barWidth)) / (barCount - 1)

        for (i in 0 until barCount) {
            val x = i * (barWidth + spacing)
            val normX = (i.toFloat() / (barCount - 1)).coerceIn(0f, 1f)

            // Bell-curve shape (tall in center, tapering to outer edges)
            val distFromCenter = (normX - 0.5f) * 2f // -1.0 to 1.0
            val bellCurve = exp(-3.2 * distFromCenter * distFromCenter).toFloat()

            // Dynamic oscillator per bar
            val osc1 = sin(time * 3.0 + i * 0.45).toFloat()
            val osc2 = cos(time * 2.0 - i * 0.35).toFloat()
            val dynamicFactor = 0.55f + (0.30f * osc1) + (0.15f * osc2)

            val maxBarHeight = height * 0.82f
            val calculatedHeight = (maxBarHeight * bellCurve * dynamicFactor * animatedActivity)
                .coerceAtLeast(3.dp.toPx())

            val barTop = midY - (calculatedHeight / 2f)

            val barBrush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFF472B6).copy(alpha = animatedAlpha),
                    Color(0xFFD946EF).copy(alpha = animatedAlpha * 0.95f),
                    Color(0xFF9333EA).copy(alpha = animatedAlpha * 0.85f),
                ),
                startY = barTop,
                endY = barTop + calculatedHeight,
            )

            drawRoundRect(
                brush = barBrush,
                topLeft = Offset(x, barTop),
                size = Size(barWidth, calculatedHeight),
                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f),
            )
        }
    }
}

@Composable
fun FirstSessionScreen(
    onBeginSession: () -> Unit,
    onExploreFirst: () -> Unit,
) {
    // Staggered entrance animation
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LumyrinthColors.BgBase)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Centered Header & Subtitle
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(animProgress.value)
                .offset { IntOffset(0, ((1f - animProgress.value) * 30).toInt()) },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Ready for your\nfirst session?",
                style = LumyrinthTypography.H1.copy(
                    fontSize = 26.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = LumyrinthColors.TextPrimary,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Start with a 1-minute session\nand feel the difference.",
                style = LumyrinthTypography.Body.copy(
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = LumyrinthColors.TextSecondary,
                ),
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.weight(0.9f))

        // Center Cosmic GlowOrb with satellite star nodes
        Box(
            modifier = Modifier
                .alpha(animProgress.value)
                .scale(0.85f + 0.15f * animProgress.value),
            contentAlignment = Alignment.Center,
        ) {
            GlowOrb(
                sizeVariant = OrbSize.Lg,
                centerContent = OrbCenterContent.None,
                animationState = OrbAnimationState.Idle(),
            )
        }

        Spacer(modifier = Modifier.weight(1.1f))

        // Action Buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(animProgress.value)
                .offset { IntOffset(0, ((1f - animProgress.value) * 20).toInt()) },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PrimaryButton(
                label = "Begin 1-Minute Session",
                onClick = onBeginSession,
                backgroundBrush = LumyrinthColors.GradientButton,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            GhostButton(
                label = "Explore First",
                onClick = onExploreFirst,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
