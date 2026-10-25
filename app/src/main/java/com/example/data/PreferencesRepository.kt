package com.example.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("sambalpuri_kb_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PREFERRED_LAYOUT = "preferred_layout"
        private const val KEY_KEYBOARD_HEIGHT = "keyboard_height"
        private const val KEY_VIBRATION = "vibration_enabled"
        private const val KEY_SOUND = "sound_enabled"
        private const val KEY_FAVORITES = "favorite_stickers"
        private const val KEY_FAVORITE_EMOJIS = "favorite_emojis"
        private const val KEY_RECENTS = "recent_stickers"
        private const val KEY_RECENT_EMOJIS = "recent_emojis"
        private const val KEY_WELCOME_COMPLETED = "welcome_completed"
    }

    var preferredLayout: String
        get() = prefs.getString(KEY_PREFERRED_LAYOUT, "ODIA") ?: "ODIA"
        set(value) = prefs.edit().putString(KEY_PREFERRED_LAYOUT, value).apply()

    var keyboardHeight: String
        get() = prefs.getString(KEY_KEYBOARD_HEIGHT, "NORMAL") ?: "NORMAL"
        set(value) = prefs.edit().putString(KEY_KEYBOARD_HEIGHT, value).apply()

    var vibrationEnabled: Boolean
        get() = prefs.getBoolean(KEY_VIBRATION, true)
        set(value) = prefs.edit().putBoolean(KEY_VIBRATION, value).apply()

    var soundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND, false)
        set(value) = prefs.edit().putBoolean(KEY_SOUND, value).apply()

    var welcomeCompleted: Boolean
        get() = prefs.getBoolean(KEY_WELCOME_COMPLETED, false)
        set(value) = prefs.edit().putBoolean(KEY_WELCOME_COMPLETED, value).apply()

    fun getFavoriteStickerIds(): Set<String> {
        return prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
    }

    fun toggleFavoriteSticker(id: String): Boolean {
        val current = getFavoriteStickerIds().toMutableSet()
        val isNowFavorite = if (current.contains(id)) {
            current.remove(id)
            false
        } else {
            current.add(id)
            true
        }
        prefs.edit().putStringSet(KEY_FAVORITES, current).apply()
        return isNowFavorite
    }

    fun isFavorite(id: String): Boolean {
        return getFavoriteStickerIds().contains(id)
    }

    fun getFavoriteEmojiChars(): Set<String> {
        return prefs.getStringSet(KEY_FAVORITE_EMOJIS, setOf("🙏", "🪘", "🌾", "❤️")) ?: emptySet()
    }

    fun toggleFavoriteEmoji(char: String): Boolean {
        val current = getFavoriteEmojiChars().toMutableSet()
        val isNowFav = if (current.contains(char)) {
            current.remove(char)
            false
        } else {
            current.add(char)
            true
        }
        prefs.edit().putStringSet(KEY_FAVORITE_EMOJIS, current).apply()
        return isNowFav
    }

    fun isEmojiFavorite(char: String): Boolean {
        return getFavoriteEmojiChars().contains(char)
    }

    fun getRecentStickerIds(): List<String> {
        val raw = prefs.getString(KEY_RECENTS, "") ?: ""
        if (raw.isEmpty()) return emptyList()
        return raw.split(",").filter { it.isNotBlank() }
    }

    fun addRecentSticker(id: String) {
        val current = getRecentStickerIds().toMutableList()
        current.remove(id)
        current.add(0, id)
        val trimmed = current.take(25)
        prefs.edit().putString(KEY_RECENTS, trimmed.joinToString(",")).apply()
    }

    fun getRecentEmojis(): List<String> {
        val raw = prefs.getString(KEY_RECENT_EMOJIS, "🙏,🌺,😊,❤️,🎉,🌾,🪘,🔥") ?: ""
        return raw.split(",").filter { it.isNotBlank() }
    }

    fun addRecentEmoji(emoji: String) {
        val current = getRecentEmojis().toMutableList()
        current.remove(emoji)
        current.add(0, emoji)
        val trimmed = current.take(30)
        prefs.edit().putString(KEY_RECENT_EMOJIS, trimmed.joinToString(",")).apply()
    }

    fun clearRecents() {
        prefs.edit()
            .remove(KEY_RECENTS)
            .remove(KEY_RECENT_EMOJIS)
            .apply()
    }
}
