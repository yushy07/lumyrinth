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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
import com.lumyrinth.app.ui.components.StandardCard
import com.lumyrinth.app.ui.components.rememberIsReducedMotion
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
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var rhythmToDelete by remember { mutableStateOf<Rhythm?>(null) }
    var isSearchActive by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

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
    val reducedMotion = rememberIsReducedMotion()
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(reducedMotion) {
        if (reducedMotion) {
            animProgress.snapTo(1f)
        } else {
            animProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
            )
        }
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
            RhythmCategory.RELAX -> LumyrinthColors.AccentPurple to LumyrinthColors.AccentPink
            RhythmCategory.FOCUS -> LumyrinthColors.AccentPurple to LumyrinthColors.PhaseHold1
            RhythmCategory.SLEEP -> LumyrinthColors.AccentPurple to LumyrinthColors.PhaseHold2
            RhythmCategory.ENERGY -> LumyrinthColors.AccentOrange to LumyrinthColors.AccentYellow
            RhythmCategory.CUSTOM -> LumyrinthColors.AccentPink to LumyrinthColors.AccentPurple
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CosmicSectionBackground(theme = SectionTheme.EXPLORE)

        Column(
            modifier = Modifier
                .widthIn(max = 720.dp)
                .fillMaxSize()
                .align(Alignment.TopCenter)
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
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(LumyrinthColors.SurfaceCard),
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
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
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
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .alpha(animProgress.value)
                .offset { IntOffset(0, ((1f - animProgress.value) * 30).toInt()) },
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 100.dp),
        ) {
            if (filteredList.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = when (selectedCategory) {
                                "favorites" -> "No favorites yet"
                                "custom" -> "No custom rhythms yet"
                                else -> "No rhythms found"
                            },
                            style = LumyrinthTypography.Body,
                            color = LumyrinthColors.TextSecondary,
                            textAlign = TextAlign.Center,
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
                        )
                    }
                }
            } else if (selectedCategory == "all" && searchQuery.isBlank()) {
                if (customRhythms.isNotEmpty()) {
                    item {
                        Text(
                            text = "My Rhythms",
                            style = LumyrinthTypography.H3.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                            color = LumyrinthColors.TextPrimary,
                        )
                    }
                    items(customRhythms, key = { it.id }) { rhythm ->
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

                listOf(
                    RhythmCategory.RELAX to "Relax",
                    RhythmCategory.FOCUS to "Focus",
                    RhythmCategory.SLEEP to "Sleep",
                    RhythmCategory.ENERGY to "Energy",
                ).forEach { (cat, title) ->
                    val sectionItems = allRhythms.filter { it.category == cat }
                    if (sectionItems.isNotEmpty()) {
                        item {
                            Text(
                                text = title,
                                style = LumyrinthTypography.H3.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                                color = LumyrinthColors.TextPrimary,
                            )
                        }
                        items(sectionItems, key = { it.id }) { rhythm ->
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
                }
            } else {
                items(filteredList, key = { it.id }) { rhythm ->
                    val (primary, secondary) = getCategoryOrbColors(rhythm.category)
                    ExploreRhythmCard(
                        title = rhythm.name,
                        patternText = rhythm.patternCode,
                        durationText = rhythm.durationRangeText,
                        onClick = { onSelectRhythm(rhythm) },
                        primaryColor = primary,
                        secondaryColor = secondary,
                        onEdit = if (rhythm.isCustom) ({ onEditCustomRhythm(rhythm) }) else null,
                        onDelete = if (rhythm.isCustom) ({ rhythmToDelete = rhythm }) else null,
                    )
                }
            }

            item {
                StandardCard(modifier = Modifier.padding(top = 4.dp), onClick = onCreateCustomClick) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            Box(
                                modifier = Modifier.size(48.dp).clip(CircleShape)
                                    .background(LumyrinthColors.AccentPurple.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(Icons.Rounded.Add, contentDescription = null, tint = LumyrinthColors.AccentPink)
                            }
                            Column {
                                Text("Create your own rhythm", style = LumyrinthTypography.H3, color = LumyrinthColors.TextPrimary)
                                Text("Customize timings and durations", style = LumyrinthTypography.BodySm, color = LumyrinthColors.TextSecondary)
                            }
                        }
                        Icon(Icons.Rounded.Add, contentDescription = null, tint = LumyrinthColors.TextSecondary)
                    }
                }
            }
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

