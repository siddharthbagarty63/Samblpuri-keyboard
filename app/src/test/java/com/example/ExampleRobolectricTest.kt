package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.PreferencesRepository
import com.example.data.StickerRepository
import com.example.keyboard.KeyboardLayouts
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Sambalpuri Keyboard", appName)
    }

    @Test
    fun `odia keyboard layouts contain required vowels, consonants and danda`() {
        assertTrue("Odia matras should contain matras", KeyboardLayouts.odiaMatras.contains("ା"))
        assertTrue("Odia matras should contain halanta", KeyboardLayouts.odiaMatras.contains("୍"))
        assertTrue("Odia matras should contain anusvara", KeyboardLayouts.odiaMatras.contains("ଂ"))

        val allConsonants = KeyboardLayouts.odiaConsonantRows.flatten()
        assertTrue("Odia consonants should contain ka", allConsonants.contains("କ"))
        assertTrue("Odia consonants should contain danda full stop", allConsonants.contains("।"))
        assertTrue("Odia consonants should contain jha", allConsonants.contains("ଝ"))

        assertEquals(10, KeyboardLayouts.englishRow1.size)
        assertEquals(9, KeyboardLayouts.englishRow2.size)
        assertEquals(7, KeyboardLayouts.englishRow3.size)
    }

    @Test
    fun `sticker repository loads bundled cultural stickers`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = StickerRepository(context)
        val stickers = repo.getAllStickers(includeCustom = false)
        assertTrue("Bundled stickers should not be empty", stickers.isNotEmpty())

        val nuakhai = stickers.find { it.id == "s_nuakhai_juhar" }
        assertNotNull("Nuakhai sticker should exist", nuakhai)
        assertEquals("ନୂଆଁଖାଇ ଜୁହାର", nuakhai?.sambalpuriLabel)

        val dhol = stickers.find { it.id == "s_dhol_nishan" }
        assertNotNull("Dhol Nishan sticker should exist", dhol)

        val categories = repo.categories
        assertTrue("Categories should include Sambalpuri Culture", categories.any { it.name == "Sambalpuri Culture" })
    }

    @Test
    fun `preferences repository language switching and favorites persistence`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = PreferencesRepository(context)
        assertEquals("ODIA", prefs.preferredLayout)

        // Switch to English and verify
        prefs.preferredLayout = "ENGLISH"
        assertEquals("ENGLISH", prefs.preferredLayout)

        // Switch back to Odia and verify
        prefs.preferredLayout = "ODIA"
        assertEquals("ODIA", prefs.preferredLayout)

        // Sticker favorites
        assertFalse(prefs.isFavorite("test_sticker_1"))
        val isNowFav = prefs.toggleFavoriteSticker("test_sticker_1")
        assertTrue(isNowFav)
        assertTrue(prefs.isFavorite("test_sticker_1"))
        prefs.toggleFavoriteSticker("test_sticker_1")
        assertFalse(prefs.isFavorite("test_sticker_1"))

        // Emoji favorites
        assertFalse(prefs.isEmojiFavorite("🤩"))
        val emojiFav = prefs.toggleFavoriteEmoji("🤩")
        assertTrue(emojiFav)
        assertTrue(prefs.isEmojiFavorite("🤩"))
        prefs.toggleFavoriteEmoji("🤩")
        assertFalse(prefs.isEmojiFavorite("🤩"))
    }

    @Test
    fun `search stickers and emojis works correctly`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = StickerRepository(context)

        val nuakhaiResults = repo.searchStickers("nuakhai")
        assertTrue(nuakhaiResults.isNotEmpty())
        assertEquals("Nuakhai Juhar", nuakhaiResults.first().name)

        val joharEmojis = repo.searchEmojis("johar")
        assertTrue(joharEmojis.isNotEmpty())
        assertEquals("🙏", joharEmojis.first().char)
    }
}
