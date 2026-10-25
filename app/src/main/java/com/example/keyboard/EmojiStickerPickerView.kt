package com.example.keyboard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.EmojiItem
import com.example.data.StickerItem
import com.example.data.StickerRepository
import com.example.ui.theme.SambalpuriCrimson
import com.example.ui.theme.SambalpuriGold
import com.example.ui.theme.SambalpuriMarigold
import java.io.File

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EmojiStickerPickerView(
    repository: StickerRepository,
    onEmojiClicked: (String) -> Unit,
    onStickerClicked: (StickerItem) -> Unit,
    onBackspaceClicked: () -> Unit,
    onCloseClicked: () -> Unit,
    onOpenAddCustom: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Emojis, 1: Stickers
    var selectedCategory by remember { mutableStateOf("all") }
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var refreshTrigger by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(295.dp)
            .background(Color(0xFF1E1C21))
    ) {
        // Top Bar: Back button, Tabs, Search, and Backspace
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onCloseClicked,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to Keyboard",
                    tint = Color.White
                )
            }

            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                containerColor = Color.Transparent,
                contentColor = SambalpuriGold
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                        selectedCategory = "all"
                    },
                    text = {
                        Text(
                            "Emojis 😊",
                            color = if (selectedTab == 0) SambalpuriGold else Color.LightGray,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        selectedCategory = "all"
                    },
                    text = {
                        Text(
                            "Stickers 🎨",
                            color = if (selectedTab == 1) SambalpuriGold else Color.LightGray,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
            }

            IconButton(
                onClick = { isSearching = !isSearching },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = if (isSearching) SambalpuriGold else Color.LightGray
                )
            }

            IconButton(
                onClick = onBackspaceClicked,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = "Backspace",
                    tint = Color.White
                )
            }
        }

        // Search Input (Expandable)
        if (isSearching) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        "Search ${if (selectedTab == 0) "emojis" else "stickers"}...",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .padding(horizontal = 12.dp, vertical = 2.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF2C2A30),
                    unfocusedContainerColor = Color(0xFF2C2A30),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedIndicatorColor = SambalpuriGold,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp)
            )
        }

        // Horizontal Category Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selectedTab == 0) {
                // Emojis categories: All, Favourites, Recents, then regional & standard
                item {
                    CategoryChip(
                        label = "All",
                        isSelected = selectedCategory == "all",
                        onClick = { selectedCategory = "all" }
                    )
                }
                item {
                    CategoryChip(
                        label = "Favourites ❤️",
                        isSelected = selectedCategory == "favorites",
                        onClick = { selectedCategory = "favorites" }
                    )
                }
                item {
                    CategoryChip(
                        label = "Recent 🕒",
                        isSelected = selectedCategory == "recent",
                        onClick = { selectedCategory = "recent" }
                    )
                }
                items(repository.emojiCategories) { cat ->
                    CategoryChip(
                        label = "${cat.icon} ${cat.name}",
                        isSelected = selectedCategory == cat.id,
                        onClick = { selectedCategory = cat.id }
                    )
                }
            } else {
                // Stickers categories: + Add button, All, Favourites, Recents, My Stickers, Cultural
                item {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(SambalpuriMarigold)
                            .clickable(onClick = onOpenAddCustom)
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Sticker",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Add My Own",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                item {
                    CategoryChip(
                        label = "All",
                        isSelected = selectedCategory == "all",
                        onClick = { selectedCategory = "all" }
                    )
                }
                item {
                    CategoryChip(
                        label = "Favourites ❤️",
                        isSelected = selectedCategory == "favorites",
                        onClick = { selectedCategory = "favorites" }
                    )
                }
                item {
                    CategoryChip(
                        label = "Recent 🕒",
                        isSelected = selectedCategory == "recent",
                        onClick = { selectedCategory = "recent" }
                    )
                }
                item {
                    CategoryChip(
                        label = "My Stickers ⭐",
                        isSelected = selectedCategory == "custom",
                        onClick = { selectedCategory = "custom" }
                    )
                }
                items(repository.categories.filter { it.id != "all" && it.id != "custom" }) { cat ->
                    CategoryChip(
                        label = cat.name,
                        isSelected = selectedCategory == cat.name,
                        onClick = { selectedCategory = cat.name }
                    )
                }
            }
        }

        // Content Grid
        if (selectedTab == 0) {
            // Emojis
            val emojiList = remember(selectedCategory, searchQuery, refreshTrigger) {
                when {
                    searchQuery.isNotBlank() -> repository.searchEmojis(searchQuery)
                    selectedCategory == "favorites" -> repository.getFavoriteEmojis()
                    selectedCategory == "recent" -> repository.getRecentEmojis()
                    selectedCategory == "all" -> repository.getAllEmojis()
                    else -> repository.getEmojisByCategory(selectedCategory)
                }
            }

            if (emojiList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (selectedCategory == "favorites") "No favourite emojis yet" else "No emojis found",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 46.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 6.dp),
                    contentPadding = PaddingValues(bottom = 6.dp)
                ) {
                    items(emojiList, key = { it.char + it.name }) { emoji ->
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    repository.markRecentEmoji(emoji.char)
                                    onEmojiClicked(emoji.char)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = emoji.char,
                                fontSize = 24.sp
                            )
                        }
                    }
                }
            }
        } else {
            // Stickers
            val stickerList = remember(selectedCategory, searchQuery, refreshTrigger) {
                when {
                    searchQuery.isNotBlank() -> repository.searchStickers(searchQuery)
                    selectedCategory == "favorites" -> repository.getFavoriteStickers()
                    selectedCategory == "recent" -> repository.getRecentStickers()
                    selectedCategory == "custom" -> repository.customStickerManager.getAllCustomStickers()
                    selectedCategory == "all" -> repository.getAllStickers()
                    else -> repository.getStickersByCategory(selectedCategory)
                }
            }

            if (stickerList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (selectedCategory == "favorites") "No favourite stickers yet"
                            else if (selectedCategory == "custom") "No custom stickers yet"
                            else if (selectedCategory == "recent") "No recently used stickers"
                            else "No stickers found",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                        if (selectedCategory == "custom") {
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SambalpuriCrimson)
                                    .clickable(onClick = onOpenAddCustom)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "+ Add First Sticker",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 6.dp),
                    contentPadding = PaddingValues(bottom = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(stickerList, key = { it.id }) { sticker ->
                        StickerKeyboardTile(
                            sticker = sticker,
                            onClick = { onStickerClicked(sticker) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) SambalpuriCrimson else Color(0xFF2C2A30))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else Color.LightGray,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1
        )
    }
}

@Composable
private fun StickerKeyboardTile(
    sticker: StickerItem,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF2A282E))
            .clickable(onClick = onClick)
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFF38353D)),
            contentAlignment = Alignment.Center
        ) {
            if (sticker.drawableRes != null) {
                Icon(
                    painter = painterResource(id = sticker.drawableRes),
                    contentDescription = sticker.name,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(54.dp)
                )
            } else if (sticker.fileUriString != null) {
                AsyncImage(
                    model = File(sticker.fileUriString),
                    contentDescription = sticker.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = sticker.sambalpuriLabel,
            fontSize = 10.sp,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}
