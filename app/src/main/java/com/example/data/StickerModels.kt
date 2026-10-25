package com.example.data

import androidx.annotation.DrawableRes

enum class KeyboardLayoutMode {
    ODIA,
    ENGLISH,
    NUMBERS_SYMBOLS,
    EMOJI_STICKER
}

data class StickerItem(
    val id: String,
    val name: String,
    val sambalpuriLabel: String,
    val category: String,
    @DrawableRes val drawableRes: Int? = null,
    val fileUriString: String? = null,
    val tags: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val isCustom: Boolean = false,
    val dateAdded: Long = System.currentTimeMillis()
)

data class StickerCategory(
    val id: String,
    val name: String,
    val sambalpuriName: String
)

data class EmojiItem(
    val char: String,
    val name: String,
    val category: String,
    val sambalpuriMeaning: String = ""
)

data class EmojiCategory(
    val id: String,
    val name: String,
    val icon: String
)
