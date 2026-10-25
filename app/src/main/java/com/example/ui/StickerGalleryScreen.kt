package com.example.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.StickerItem
import com.example.data.StickerRepository
import com.example.ui.theme.SambalpuriCrimson
import com.example.ui.theme.SambalpuriGold
import com.example.ui.theme.SambalpuriMarigold
import java.io.File

@Composable
fun StickerGalleryScreen(
    repository: StickerRepository,
    onBack: () -> Unit,
    onNavigateToMyStickers: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var showOnlyFavorites by remember { mutableStateOf(false) }
    var showOnlyRecents by remember { mutableStateOf(false) }
    var selectedStickerForPreview by remember { mutableStateOf<StickerItem?>(null) }
    var refreshTrigger by remember { mutableStateOf(0) }

    val allStickers = remember(selectedCategory, searchQuery, showOnlyFavorites, showOnlyRecents, refreshTrigger) {
        when {
            showOnlyFavorites -> repository.getFavoriteStickers()
            showOnlyRecents -> repository.getRecentStickers()
            searchQuery.isNotBlank() -> repository.searchStickers(searchQuery)
            else -> repository.getStickersByCategory(selectedCategory)
        }
    }

    fun shareSticker(sticker: StickerItem) {
        val uri: Uri? = repository.getSharableUriForSticker(sticker)
        if (uri != null) {
            repository.markRecent(sticker.id)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, "${sticker.sambalpuriLabel} - Sambalpuri Keyboard")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Sticker"))
        } else {
            Toast.makeText(context, "Failed to prepare sticker for sharing", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Sambalpuri Sticker Gallery",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Cultural expressions, folk instruments, and attire",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onNavigateToMyStickers) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Custom Sticker",
                        tint = SambalpuriCrimson
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToMyStickers,
                containerColor = SambalpuriCrimson,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Sticker")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    if (it.isNotEmpty()) {
                        showOnlyFavorites = false
                        showOnlyRecents = false
                    }
                },
                placeholder = { Text("Search stickers, e.g. Nuakhai, Dhol...", fontSize = 13.sp) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = SambalpuriCrimson)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = SambalpuriCrimson,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            // Filter Chips: All, Favorites, Recents, My Stickers
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = !showOnlyFavorites && !showOnlyRecents && selectedCategory != "custom",
                    onClick = {
                        showOnlyFavorites = false
                        showOnlyRecents = false
                        selectedCategory = "All"
                    },
                    label = { Text("All", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SambalpuriCrimson,
                        selectedLabelColor = Color.White
                    )
                )

                FilterChip(
                    selected = showOnlyFavorites,
                    onClick = {
                        showOnlyFavorites = !showOnlyFavorites
                        showOnlyRecents = false
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (showOnlyFavorites) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    label = { Text("Favorites", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SambalpuriCrimson,
                        selectedLabelColor = Color.White
                    )
                )

                FilterChip(
                    selected = showOnlyRecents,
                    onClick = {
                        showOnlyRecents = !showOnlyRecents
                        showOnlyFavorites = false
                    },
                    label = { Text("Recents 🕒", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SambalpuriCrimson,
                        selectedLabelColor = Color.White
                    )
                )

                FilterChip(
                    selected = selectedCategory == "custom" && !showOnlyFavorites && !showOnlyRecents,
                    onClick = {
                        selectedCategory = "custom"
                        showOnlyFavorites = false
                        showOnlyRecents = false
                    },
                    label = { Text("My Stickers ⭐", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SambalpuriCrimson,
                        selectedLabelColor = Color.White
                    )
                )
            }

            // Categories horizontal scroll (if not in favorites/recents mode)
            if (!showOnlyFavorites && !showOnlyRecents) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(repository.categories) { cat ->
                        val isSelected = selectedCategory.equals(cat.name, ignoreCase = true) || (cat.id == "custom" && selectedCategory == "custom")
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) SambalpuriMarigold else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable {
                                    selectedCategory = cat.name
                                    searchQuery = ""
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cat.name,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Sticker Grid
            if (allStickers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (showOnlyFavorites) "No favorite stickers yet" else if (showOnlyRecents) "No recently used stickers" else "No stickers found",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap ❤️ on any sticker to add it to your favorites, or tap '+' to import your own photos.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 105.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(allStickers, key = { it.id }) { sticker ->
                        StickerCard(
                            sticker = sticker,
                            onClick = { selectedStickerForPreview = sticker },
                            onShare = { shareSticker(sticker) },
                            onToggleFavorite = {
                                repository.toggleFavorite(sticker.id)
                                refreshTrigger++
                            }
                        )
                    }
                }
            }
        }
    }

    // Sticker Preview Modal
    selectedStickerForPreview?.let { sticker ->
        AlertDialog(
            onDismissRequest = { selectedStickerForPreview = null },
            title = {
                Text(
                    text = sticker.sambalpuriLabel,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SambalpuriCrimson
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        if (sticker.drawableRes != null) {
                            Icon(
                                painter = painterResource(id = sticker.drawableRes),
                                contentDescription = sticker.name,
                                tint = Color.Unspecified,
                                modifier = Modifier.size(140.dp)
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

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Category: ${sticker.category}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (sticker.tags.isNotEmpty()) {
                        Text(
                            text = "Tags: ${sticker.tags.joinToString(", ")}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        shareSticker(sticker)
                        selectedStickerForPreview = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SambalpuriCrimson)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share Sticker")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { selectedStickerForPreview = null }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun StickerCard(
    sticker: StickerItem,
    onClick: () -> Unit,
    onShare: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFFF8E1)),
                contentAlignment = Alignment.Center
            ) {
                if (sticker.drawableRes != null) {
                    Icon(
                        painter = painterResource(id = sticker.drawableRes),
                        contentDescription = sticker.name,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(68.dp)
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

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = sticker.sambalpuriLabel,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Text(
                text = sticker.category,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Action row: Favorite & Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (sticker.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (sticker.isFavorite) Color.Red else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = onShare,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = SambalpuriCrimson,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
