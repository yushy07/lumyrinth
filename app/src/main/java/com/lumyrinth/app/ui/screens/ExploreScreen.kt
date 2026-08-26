package com.lumyrinth.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumyrinth.app.domain.Rhythm
import com.lumyrinth.app.domain.RhythmCategory
import com.lumyrinth.app.ui.components.ChipFilter
import com.lumyrinth.app.ui.components.ChipVariant
import com.lumyrinth.app.ui.components.ConfirmDialog
import com.lumyrinth.app.ui.components.ExploreRhythmCard
import com.lumyrinth.app.ui.components.IconCircleButton
import com.lumyrinth.app.ui.components.StandardCard
import com.lumyrinth.app.ui.theme.LumyrinthColors
import com.lumyrinth.app.ui.theme.LumyrinthTypography

import com.lumyrinth.app.ui.components.CosmicSectionBackground
import com.lumyrinth.app.ui.components.SectionTheme

@Composable
fun ExploreScreen(
    allRhythms: List<Rhythm>,
    customRhythms: List<Rhythm>,
    favoriteIds: Set<String>,
    selectedCategory: String, // "all", "relax", "focus", "sleep", "energy"
    onCategoryChange: (String) -> Unit,
    onSelectRhythm: (Rhythm) -> Unit,
    onCreateCustomClick: () -> Unit,
    onEditCustomRhythm: (Rhythm) -> Unit = {},
    onDeleteCustomRhythm: (String) -> Unit = {},
) {
    var searchQuery by remember { mutableStateOf("") }
    var rhythmToDelete by remember { mutableStateOf<Rhythm?>(null) }
    var isSearchActive by remember { mutableStateOf(false) }

    val categories = listOf(
        "all" to "All",
        "relax" to "Relax",
        "focus" to "Focus",
        "sleep" to "Sleep",
        "energy" to "Energy",
        "favorites" to "Favorites",
        "custom" to "My rhythms",
    )

    // Staggered screen entrance animation
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        )
    }

    val filteredList = remember(allRhythms, customRhythms, favoriteIds, selectedCategory, searchQuery) {
        val combined = customRhythms + allRhythms
        val searchFiltered = if (searchQuery.isBlank()) {
            combined
        } else {
            combined.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.shortDescription.contains(searchQuery, ignoreCase = true)
            }
        }

        when {
            searchQuery.isNotBlank() || selectedCategory == "all" -> searchFiltered
            selectedCategory == "favorites" -> searchFiltered.filter { it.id in favoriteIds }
            selectedCategory == "custom" -> searchFiltered.filter { it.isCustom }
            else -> searchFiltered.filter { it.category.id.equals(selectedCategory, ignoreCase = true) }
        }
    }

    fun getCategoryOrbColors(cat: RhythmCategory): Pair<Color, Color> {
        return when (cat) {
            RhythmCategory.RELAX -> Color(0xFFA855F7) to Color(0xFFF43F5E)
            RhythmCategory.FOCUS -> Color(0xFF8B5CF6) to Color(0xFFC084FC)
            RhythmCategory.SLEEP -> Color(0xFF6366F1) to Color(0xFFFDBA74)
            RhythmCategory.ENERGY -> Color(0xFFF59E0B) to Color(0xFFEF4444)
            RhythmCategory.CUSTOM -> Color(0xFFEC4899) to Color(0xFFA855F7)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CosmicSectionBackground(theme = SectionTheme.EXPLORE)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp),
        ) {
            Spacer(modifier = Modifier.height(14.dp))

        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(animProgress.value)
                .offset { IntOffset(0, ((1f - animProgress.value) * 15).toInt()) },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Explore",
                style = LumyrinthTypography.H1.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                ),
                color = LumyrinthColors.TextPrimary,
            )

            IconButton(
                onClick = {
                    isSearchActive = !isSearchActive
                    if (!isSearchActive) searchQuery = ""
                },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF160E26)),
            ) {
                Icon(
                    imageVector = if (isSearchActive) Icons.Rounded.Close else Icons.Rounded.Search,
                    contentDescription = "Search rhythms",
                    tint = LumyrinthColors.TextPrimary,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        AnimatedVisibility(
            visible = isSearchActive,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            Column {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search rhythms...", style = LumyrinthTypography.Body) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(999.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = LumyrinthColors.SurfaceCard,
                        unfocusedContainerColor = LumyrinthColors.SurfaceCard,
                        focusedBorderColor = LumyrinthColors.AccentPurple,
                        unfocusedBorderColor = LumyrinthColors.BorderSubtle,
                        focusedTextColor = LumyrinthColors.TextPrimary,
                        unfocusedTextColor = LumyrinthColors.TextPrimary,
                    ),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Category Filter Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(animProgress.value)
                .offset { IntOffset(0, ((1f - animProgress.value) * 20).toInt()) },
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 6.dp),
        ) {
            items(categories) { (id, label) ->
                ChipFilter(
                    label = label,
                    selected = (selectedCategory == id) && searchQuery.isBlank(),
                    onClick = {
                        searchQuery = ""
                        onCategoryChange(id)
                    },
                    variant = ChipVariant.Pill,
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Exercises Content List
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .alpha(animProgress.value)
                .offset { IntOffset(0, ((1f - animProgress.value) * 30).toInt()) }
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (filteredList.isEmpty()) {
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    text = when (selectedCategory) {
                        "favorites" -> "No favorites yet"
                        "custom" -> "No custom rhythms yet"
                        else -> "No rhythms found"
                    },
                    style = LumyrinthTypography.Body,
                    color = LumyrinthColors.TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = when (selectedCategory) {
                        "favorites" -> "Tap the heart on a rhythm to keep it here."
                        "custom" -> "Create a breathing pattern that feels right for you."
                        else -> "Clear the search or choose another category."
                    },
                    style = LumyrinthTypography.BodySm,
                    color = LumyrinthColors.TextTertiary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else if (selectedCategory == "all" && searchQuery.isBlank()) {
                // Group by categories like the reference image
                if (customRhythms.isNotEmpty()) {
                    Text(
                        text = "My Rhythms",
                        style = LumyrinthTypography.H3.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                        ),
                        color = LumyrinthColors.TextPrimary,
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        customRhythms.forEach { rhythm ->
                            val (primary, secondary) = getCategoryOrbColors(rhythm.category)
                            ExploreRhythmCard(
                                title = rhythm.name,
                                patternText = rhythm.patternCode,
                                durationText = rhythm.durationRangeText,
                                onClick = { onSelectRhythm(rhythm) },
                                primaryColor = primary,
                                secondaryColor = secondary,
                                onEdit = { onEditCustomRhythm(rhythm) },
                                onDelete = { rhythmToDelete = rhythm },
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                listOf(
                    RhythmCategory.RELAX to "Relax",
                    RhythmCategory.FOCUS to "Focus",
                    RhythmCategory.SLEEP to "Sleep",
                    RhythmCategory.ENERGY to "Energy",
                ).forEach { (cat, title) ->
                    val sectionItems = allRhythms.filter { it.category == cat }
                    if (sectionItems.isNotEmpty()) {
                        Text(
                            text = title,
                            style = LumyrinthTypography.H3.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                            ),
                            color = LumyrinthColors.TextPrimary,
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            sectionItems.forEach { rhythm ->
                                val (primary, secondary) = getCategoryOrbColors(rhythm.category)
                                ExploreRhythmCard(
                                    title = rhythm.name,
                                    patternText = rhythm.patternCode,
                                    durationText = rhythm.durationRangeText,
                                    onClick = { onSelectRhythm(rhythm) },
                                    primaryColor = primary,
                                    secondaryColor = secondary,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            } else {
                // Single Category or Search Results
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    filteredList.forEach { rhythm ->
                        val (primary, secondary) = getCategoryOrbColors(rhythm.category)
                        ExploreRhythmCard(
                            title = rhythm.name,
                            patternText = rhythm.patternCode,
                            durationText = rhythm.durationRangeText,
                            onClick = { onSelectRhythm(rhythm) },
                            primaryColor = primary,
                            secondaryColor = secondary,
                            onEdit = if (rhythm.isCustom) {
                                { onEditCustomRhythm(rhythm) }
                            } else null,
                            onDelete = if (rhythm.isCustom) {
                                { rhythmToDelete = rhythm }
                            } else null,
                        )
                    }
                }
            }

            // Create Your Own Rhythm CTA Card
            StandardCard(
                modifier = Modifier.padding(top = 4.dp),
                onClick = onCreateCustomClick,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(LumyrinthColors.AccentPurple.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = "Create",
                                tint = LumyrinthColors.AccentPink,
                                modifier = Modifier.size(22.dp),
                            )
                        }

                        Column {
                            Text(
                                text = "Create your own rhythm",
                                style = LumyrinthTypography.H3.copy(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                ),
                                color = LumyrinthColors.TextPrimary,
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Customize timings and durations",
                                style = LumyrinthTypography.BodySm,
                                color = LumyrinthColors.TextSecondary,
                            )
                        }
                    }

                    IconCircleButton(
                        icon = Icons.Rounded.Add,
                        contentDescription = "Create Custom",
                        onClick = onCreateCustomClick,
                    )
                }
            }

            // Generous bottom spacer so content clears floating bottom nav bar
            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    // Confirm Delete Dialog for Custom Rhythm
    val itemToDelete = rhythmToDelete
    if (itemToDelete != null) {
        ConfirmDialog(
            title = "Delete \"${itemToDelete.name}\"?",
            message = "This will remove this custom breathing rhythm. Historical session records using this rhythm will be preserved.",
            confirmLabel = "Delete",
            cancelLabel = "Cancel",
            onConfirm = {
                onDeleteCustomRhythm(itemToDelete.id)
                rhythmToDelete = null
            },
            onCancel = {
                rhythmToDelete = null
            },
        )
    }
}
}

