package com.lumyrinth.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumyrinth.app.legal.LegalContent
import com.lumyrinth.app.ui.components.IconCircleButton
import com.lumyrinth.app.ui.components.PrimaryButton
import com.lumyrinth.app.ui.theme.LumyrinthColors
import com.lumyrinth.app.ui.theme.LumyrinthTypography

@Composable
fun PrivacyPolicyScreen(
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LumyrinthColors.BgBase)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp),
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Top Navigation Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconCircleButton(
                icon = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Back to previous screen",
                onClick = onBack,
                size = 40.dp,
                iconSize = 20.dp,
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = LegalContent.PrivacyPolicy.TITLE,
                    style = LumyrinthTypography.H2.copy(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = LumyrinthColors.TextPrimary,
                )
                Text(
                    text = "Last updated: ${LegalContent.LAST_UPDATED}",
                    style = LumyrinthTypography.Label.copy(
                        fontSize = 12.sp,
                        color = LumyrinthColors.TextTertiary,
                    ),
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Scrollable Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Local-Only Highlight Summary Card
            LegalHighlightCard(
                icon = Icons.Rounded.Shield,
                iconTint = Color(0xFF4ADE80),
                title = "100% On-Device & Private",
                summary = LegalContent.PrivacyPolicy.SUMMARY,
            )

            // Sections
            LegalContent.PrivacyPolicy.sections.forEach { section ->
                LegalSectionCard(section = section)
            }

            // Developer Note Card (regarding Play Store requirements)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0x1A28114C))
                    .border(1.dp, Color(0x33A855F7), RoundedCornerShape(20.dp))
                    .padding(16.dp),
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = null,
                        tint = Color(0xFFC084FC),
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Play Store Publishing Note",
                            style = LumyrinthTypography.BodySm.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFF3E8FF),
                            ),
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Per Google Play Developer Policy, this privacy policy text must also be hosted at a publicly accessible URL and entered into the Play Console under 'App content → Privacy policy'.",
                            style = LumyrinthTypography.BodySm.copy(
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                color = Color(0xFFD8B4FE),
                            ),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryButton(
                label = "I Understand",
                onClick = onBack,
                backgroundBrush = LumyrinthColors.GradientButton,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("privacy_policy_done_button"),
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun TermsScreen(
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LumyrinthColors.BgBase)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp),
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Top Navigation Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconCircleButton(
                icon = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Back to previous screen",
                onClick = onBack,
                size = 40.dp,
                iconSize = 20.dp,
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = LegalContent.TermsOfService.TITLE,
                    style = LumyrinthTypography.H2.copy(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = LumyrinthColors.TextPrimary,
                )
                Text(
                    text = "Last updated: ${LegalContent.LAST_UPDATED}",
                    style = LumyrinthTypography.Label.copy(
                        fontSize = 12.sp,
                        color = LumyrinthColors.TextTertiary,
                    ),
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Scrollable Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Medical Disclaimer Highlight Card
            LegalHighlightCard(
                icon = Icons.Rounded.HealthAndSafety,
                iconTint = Color(0xFFFB7185),
                title = "Wellness Notice & Disclaimer",
                summary = "Lumyrinth is designed for general mindfulness and relaxation. It is not a medical device and does not substitute professional medical care.",
            )

            // Sections
            LegalContent.TermsOfService.sections.forEach { section ->
                LegalSectionCard(
                    section = section,
                    isImportant = section.title.contains("Medical Advice", ignoreCase = true),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryButton(
                label = "I Agree",
                onClick = onBack,
                backgroundBrush = LumyrinthColors.GradientButton,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("terms_agree_button"),
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun LegalHighlightCard(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    summary: String,
) {
    val cardBg = Brush.verticalGradient(
        listOf(
            Color(0xFF261048),
            Color(0xFF160B2A),
            Color(0xFF180A24),
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(cardBg)
            .border(
                1.dp,
                Brush.verticalGradient(
                    listOf(
                        iconTint.copy(alpha = 0.5f),
                        Color(0x26A855F7),
                    )
                ),
                RoundedCornerShape(22.dp)
            )
            .padding(18.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.16f))
                    .border(1.dp, iconTint.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp),
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = LumyrinthTypography.H3.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = LumyrinthColors.TextPrimary,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = summary,
                    style = LumyrinthTypography.BodySm.copy(
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = LumyrinthColors.TextSecondary,
                    ),
                )
            }
        }
    }
}

@Composable
private fun LegalSectionCard(
    section: LegalContent.LegalSection,
    isImportant: Boolean = false,
) {
    val borderColor = if (isImportant) Color(0x4DF43F5E) else LumyrinthColors.BorderSubtle
    val bgColor = if (isImportant) Color(0xFF1A0E22) else LumyrinthColors.SurfaceCard

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .padding(18.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = section.title,
                style = LumyrinthTypography.H3.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = if (isImportant) Color(0xFFFDA4AF) else LumyrinthColors.TextPrimary,
            )

            Spacer(modifier = Modifier.height(8.dp))

            section.paragraphs.forEachIndexed { index, para ->
                Text(
                    text = para,
                    style = LumyrinthTypography.Body.copy(
                        fontSize = 13.5.sp,
                        lineHeight = 20.sp,
                        color = LumyrinthColors.TextSecondary,
                    ),
                )
                if (index < section.paragraphs.size - 1 || section.bulletPoints.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            if (section.bulletPoints.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    section.bulletPoints.forEach { point ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top,
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 7.dp)
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(LumyrinthColors.AccentPink),
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = point,
                                style = LumyrinthTypography.BodySm.copy(
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp,
                                    color = LumyrinthColors.TextSecondary,
                                ),
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        }
    }
}
