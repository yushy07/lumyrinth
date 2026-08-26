package com.lumyrinth.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumyrinth.app.ui.theme.LumyrinthColors
import com.lumyrinth.app.ui.theme.LumyrinthTypography

enum class AppTab(val label: String, val icon: ImageVector) {
    HOME("Home", Icons.Rounded.Home),
    EXPLORE("Explore", Icons.Rounded.Explore),
    PROGRESS("Progress", Icons.Rounded.BarChart),
    SETTINGS("Settings", Icons.Rounded.Settings),
}

@Composable
fun BottomTabBar(
    activeTab: AppTab,
    onTabChange: (AppTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF0C0816))
            .navigationBarsPadding(),
    ) {
        // Thin top divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0x1FFFFFFF))
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppTab.entries.forEach { tab ->
                val isActive = tab == activeTab
                val contentColor by animateColorAsState(
                    targetValue = if (isActive) Color(0xFFE879F9) else Color(0x73FFFFFF),
                    label = "tab_color_${tab.name}",
                )
                val iconScale by animateFloatAsState(
                    targetValue = if (isActive) 1.08f else 1.0f,
                    label = "tab_scale_${tab.name}",
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .semantics { selected = isActive }
                        .selectable(
                            selected = isActive,
                            role = Role.Tab,
                            onClick = { onTabChange(tab) },
                        )
                        .padding(vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        tint = contentColor,
                        modifier = Modifier
                            .size(24.dp)
                            .scale(iconScale),
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = tab.label,
                        style = LumyrinthTypography.Label.copy(
                            fontSize = 11.sp,
                            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                        ),
                        color = contentColor,
                    )
                }
            }
        }
    }
}

@Composable
fun PageIndicatorDots(
    total: Int = 4,
    activeIndex: Int = 0,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (i in 0 until total) {
            val isActive = i == activeIndex
            Box(
                modifier = Modifier
                    .size(if (isActive) 7.dp else 6.dp)
                    .clip(CircleShape)
                    .background(
                        if (isActive) Color(0xFFD946EF)
                        else Color(0x33FFFFFF)
                    )
            )
        }
    }
}
