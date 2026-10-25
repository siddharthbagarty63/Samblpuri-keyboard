package com.example.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.R
import java.io.File
import java.io.FileOutputStream

class StickerRepository(private val context: Context) {

    val preferencesRepository = PreferencesRepository(context)
    val customStickerManager = CustomStickerManager(context)

    val categories = listOf(
        StickerCategory("all", "All", "ସବୁ"),
        StickerCategory("culture", "Sambalpuri Culture", "ସମ୍ବଲପୁରୀ ସଂସ୍କୃତି"),
        StickerCategory("festivals", "Festivals", "ପରବ୍ ପୁନି"),
        StickerCategory("music", "Dhol and Nishan", "ଢୋଲ୍ ନିଶାନ୍"),
        StickerCategory("greetings", "Greetings", "ଜୁହାର ଭେଟ୍"),
        StickerCategory("reactions", "Funny Local Reactions", "ହସ କଉତୁକ୍"),
        StickerCategory("clothing", "Traditional Clothing", "ସମ୍ବଲପୁରୀ ଲୁଗା ବସ୍ତ୍ର"),
        StickerCategory("conversations", "Daily Conversations", "ଦୈନନ୍ଦିନ କଥାବାର୍ତ୍ତା"),
        StickerCategory("custom", "Custom Stickers", "ନିଜର ଷ୍ଟିକର୍")
    )

    private val bundledStickers = listOf(
        StickerItem(
            id = "s_nuakhai_juhar",
            name = "Nuakhai Juhar",
            sambalpuriLabel = "ନୂଆଁଖାଇ ଜୁହାର",
            category = "Festivals",
            drawableRes = R.drawable.ic_sticker_nuakhai_juhar,
            tags = listOf("nuakhai", "juhar", "festival", "nabanna", "sambalpuri", "parba")
        ),
        StickerItem(
            id = "s_dhol_nishan",
            name = "Dhol and Nishan",
            sambalpuriLabel = "ଢୋଲ୍ ଆର୍ ନିଶାନ୍",
            category = "Dhol and Nishan",
            drawableRes = R.drawable.ic_sticker_dhol_nishan,
            tags = listOf("dhol", "nishan", "tasha", "baja", "music", "dance", "culture")
        ),
        StickerItem(
            id = "s_maa_samalei",
            name = "Maa Samalei",
            sambalpuriLabel = "ମାଁ ସମଲେଇ",
            category = "Sambalpuri Culture",
            drawableRes = R.drawable.ic_sticker_maa_samalei,
            tags = listOf("samalei", "samaleswari", "maa", "mandir", "bhakti", "puja", "sambalpur")
        ),
        StickerItem(
            id = "s_sambalpuri_ikat",
            name = "Sambalpuri Bandha",
            sambalpuriLabel = "ସମ୍ବଲପୁରୀ ବାନ୍ଧକଳା",
            category = "Traditional Clothing",
            drawableRes = R.drawable.ic_sticker_sambalpuri_ikat,
            tags = listOf("ikat", "pasapalli", "sari", "bandha", "handloom", "clothing", "fashion")
        ),
        StickerItem(
            id = "s_johar",
            name = "Pranam Johar",
            sambalpuriLabel = "ନମସ୍କାର ଜୁହାର",
            category = "Greetings",
            drawableRes = R.drawable.ic_sticker_johar,
            tags = listOf("johar", "pranam", "namaskar", "respect", "greeting", "hello")
        ),
        StickerItem(
            id = "s_hai_re_hai",
            name = "Hai Re Hai!",
            sambalpuriLabel = "ହଇ ରେ ହଇ !",
            category = "Funny Local Reactions",
            drawableRes = R.drawable.ic_sticker_hai_re_hai,
            tags = listOf("hai", "fun", "celebration", "wow", "happiness", "laugh")
        ),
        StickerItem(
            id = "s_bhal_achhu",
            name = "Bhal Achhu?",
            sambalpuriLabel = "ଭଲ ଅଛୁ ନାଁ ?",
            category = "Daily Conversations",
            drawableRes = R.drawable.ic_sticker_johar,
            tags = listOf("bhal", "achhu", "kenta", "conversation", "greeting")
        ),
        StickerItem(
            id = "s_mor_sangata",
            name = "Mor Sangata",
            sambalpuriLabel = "ମୋର ସଙ୍ଗାତ ବାବୁ",
            category = "Daily Conversations",
            drawableRes = R.drawable.ic_sticker_hai_re_hai,
            tags = listOf("sangata", "friend", "dada", "babu", "friendship", "love")
        ),
        StickerItem(
            id = "s_karma_festival",
            name = "Karma Parba",
            sambalpuriLabel = "କରମା ପରବ୍ ଗୀତ୍",
            category = "Festivals",
            drawableRes = R.drawable.ic_sticker_nuakhai_juhar,
            tags = listOf("karma", "nach", "parba", "festival", "nature")
        ),
        StickerItem(
            id = "s_dada_pranam",
            name = "Dada Johar",
            sambalpuriLabel = "ଦାଦା ଜୁହାର ହୋ",
            category = "Greetings",
            drawableRes = R.drawable.ic_sticker_johar,
            tags = listOf("dada", "johar", "brother", "elder", "respect")
        )
    )

    fun getAllStickers(includeCustom: Boolean = true): List<StickerItem> {
        val favorites = preferencesRepository.getFavoriteStickerIds()
        val bundled = bundledStickers.map { it.copy(isFavorite = favorites.contains(it.id)) }
        return if (includeCustom) {
            val custom = customStickerManager.getAllCustomStickers().map {
                it.copy(isFavorite = favorites.contains(it.id))
            }
            custom + bundled
        } else {
            bundled
        }
    }

    fun getStickersByCategory(categoryName: String): List<StickerItem> {
        if (categoryName.equals("All", ignoreCase = true) || categoryName.equals("all", ignoreCase = true)) {
            return getAllStickers()
        }
        if (categoryName.equals("Custom Stickers", ignoreCase = true) || categoryName.equals("custom", ignoreCase = true)) {
            return customStickerManager.getAllCustomStickers()
        }
        return getAllStickers().filter { it.category.equals(categoryName, ignoreCase = true) }
    }

    fun searchStickers(query: String): List<StickerItem> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return getAllStickers()
        return getAllStickers().filter { item ->
            item.name.lowercase().contains(q) ||
                    item.sambalpuriLabel.lowercase().contains(q) ||
                    item.category.lowercase().contains(q) ||
                    item.tags.any { it.lowercase().contains(q) }
        }
    }

    fun getFavoriteStickers(): List<StickerItem> {
        val favorites = preferencesRepository.getFavoriteStickerIds()
        return getAllStickers().filter { favorites.contains(it.id) }
    }

    fun getRecentStickers(): List<StickerItem> {
        val recents = preferencesRepository.getRecentStickerIds()
        val allMap = getAllStickers().associateBy { it.id }
        return recents.mapNotNull { allMap[it] }
    }

    fun toggleFavorite(id: String): Boolean {
        return preferencesRepository.toggleFavoriteSticker(id)
    }

    fun markRecent(id: String) {
        preferencesRepository.addRecentSticker(id)
    }

    /**
     * Converts a bundled or custom sticker into a sharable FileProvider content URI.
     */
    fun getSharableUriForSticker(item: StickerItem): Uri? {
        if (item.isCustom) {
            return customStickerManager.getSharableUri(item)
        }

        // Bundled drawable: render to a PNG in cache/stickers/
        val resId = item.drawableRes ?: return null
        return try {
            val cacheDir = File(context.cacheDir, "stickers").apply {
                if (!exists()) mkdirs()
            }
            val outFile = File(cacheDir, "${item.id}.png")
            if (!outFile.exists() || outFile.length() == 0L) {
                val drawable = ContextCompat.getDrawable(context, resId) ?: return null
                val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 400
                val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 400
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)

                FileOutputStream(outFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
            }
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                outFile
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Emoji Collections
    val emojiCategories = listOf(
        EmojiCategory("expressions", "Sambalpuri Expressions", "🗣️"),
        EmojiCategory("culture", "Local Culture", "🪘"),
        EmojiCategory("greetings", "Greetings", "🙏"),
        EmojiCategory("happy", "Happy", "😊"),
        EmojiCategory("funny", "Funny", "😂"),
        EmojiCategory("love", "Love", "❤️"),
        EmojiCategory("friendship", "Friendship", "🤝"),
        EmojiCategory("celebration", "Celebration", "🎉"),
        EmojiCategory("sad", "Sad", "🥺")
    )

    private val allEmojis = listOf(
        // Sambalpuri Expressions & Culture
        EmojiItem("🙏", "Johar", "greetings", "ଜୁହାର (Johar)"),
        EmojiItem("🪘", "Dhol", "culture", "ଢୋଲ୍ (Dhol)"),
        EmojiItem("🌾", "Dhana Shisa", "culture", "ଧାନ ଶିଶା (New harvest/Nuakhai)"),
        EmojiItem("🌺", "Mandar Phul", "culture", "ମନ୍ଦାର ଫୁଲ୍ (Puja offering)"),
        EmojiItem("🤝", "Sangata", "friendship", "ମୋର ସଙ୍ଗାତ (True buddy)"),
        EmojiItem("🔥", "Aanji", "expressions", "ନିଆଁ ଲଗାଇଦେଲା (Super energetic)"),
        EmojiItem("❤️", "Maa Maya", "love", "ମାୟା ମମତା (Pure affection)"),
        EmojiItem("✨", "Chakamak", "celebration", "ଝଲକ (Dazzling festive glow)"),
        EmojiItem("🥳", "Parba Khushi", "celebration", "ପରବ୍ ଖୁସି (Festive celebration)"),
        EmojiItem("🤣", "Bhasani Hasa", "funny", "ହାହାକାର ହସ (Uncontrollable laughter)"),
        EmojiItem("😂", "Kautuk", "funny", "ମଜାଳିଆ (Funny giggle)"),
        EmojiItem("🤩", "Chamatkar", "happy", "ଦମଦାର୍ (Awesome / Stunned)"),
        EmojiItem("😍", "Man Mohana", "love", "ମନ ମୋହିନେଲା (Charming)"),
        EmojiItem("🤗", "Kola Bandha", "friendship", "କୋଳେଇ ନେଲା (Warm embrace)"),
        EmojiItem("👍", "Bhal Katha", "expressions", "ଠିକ୍ କଥା (Good / Agreed)"),
        EmojiItem("👏", "Bole Re Sathi", "celebration", "ତାଳି (Applause)"),
        EmojiItem("💃", "Nachaniya", "culture", "ଡାଲଖାଇ ନାଚ (Dalkhai dance)"),
        EmojiItem("🕺", "Rasarkeli", "culture", "ରସରକେଲି ନାଚ (Rasarkeli dance)"),
        EmojiItem("🥁", "Madal", "culture", "ମାଦଲ (Folk drum)"),
        EmojiItem("💖", "Dil Thi", "love", "ମନ ଭରିକରି (From the heart)"),
        EmojiItem("🥺", "Kanta Laguchhe", "sad", "ଦୁଃଖ ଲାଗୁଛେ (Feeling sad)"),
        EmojiItem("😢", "Aankhi Looho", "sad", "ଲୁହ (Teary eyes)"),
        EmojiItem("😎", "Kenta Laguchhe", "happy", "ସ୍ୱାଗ୍ / କେନ୍ତା ଲାଗୁଛେ (Cool swagger)"),
        EmojiItem("🙌", "Jai Mauli", "greetings", "ଜୟ ମାଉଲି (Praise & victory)")
    )

    fun getEmojisByCategory(categoryId: String): List<EmojiItem> {
        return allEmojis.filter { it.category.equals(categoryId, ignoreCase = true) }
    }

    fun getAllEmojis(): List<EmojiItem> = allEmojis

    fun searchEmojis(query: String): List<EmojiItem> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return allEmojis
        return allEmojis.filter {
            it.char.contains(q) ||
                    it.name.lowercase().contains(q) ||
                    it.sambalpuriMeaning.lowercase().contains(q)
        }
    }

    fun getFavoriteEmojis(): List<EmojiItem> {
        val favChars = preferencesRepository.getFavoriteEmojiChars()
        return allEmojis.filter { favChars.contains(it.char) }
    }

    fun getRecentEmojis(): List<EmojiItem> {
        val recentChars = preferencesRepository.getRecentEmojis()
        val map = allEmojis.associateBy { it.char }
        return recentChars.map { char ->
            map[char] ?: EmojiItem(char, "Emoji", "recent", "")
        }
    }

    fun markRecentEmoji(emoji: String) {
        preferencesRepository.addRecentEmoji(emoji)
    }

    fun toggleFavoriteEmoji(char: String): Boolean {
        return preferencesRepository.toggleFavoriteEmoji(char)
    }
}
