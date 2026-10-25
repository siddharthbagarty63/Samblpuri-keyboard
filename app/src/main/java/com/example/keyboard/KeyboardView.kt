package com.example.keyboard

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.KeyboardReturn
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.KeyboardLayoutMode
import com.example.data.PreferencesRepository
import com.example.data.StickerItem
import com.example.data.StickerRepository
import com.example.ui.theme.SambalpuriCrimson
import com.example.ui.theme.SambalpuriGold
import com.example.ui.theme.SambalpuriKeyDark
import com.example.ui.theme.SambalpuriKeySpecialDark

@Composable
fun KeyboardView(
    repository: StickerRepository,
    preferencesRepository: PreferencesRepository,
    onTextCommit: (String) -> Unit,
    onDeleteSurroundingText: () -> Unit,
    onPerformAction: () -> Unit,
    onStickerSelected: (StickerItem) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAddCustom: () -> Unit = onOpenSettings,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    var currentMode by remember {
        mutableStateOf(
            if (preferencesRepository.preferredLayout == "ENGLISH")
                KeyboardLayoutMode.ENGLISH
            else
                KeyboardLayoutMode.ODIA
        )
    }
    var isShiftActive by remember { mutableStateOf(false) }
    var isOdiaVowelsActive by remember { mutableStateOf(false) }

    fun triggerFeedback() {
        if (preferencesRepository.vibrationEnabled) {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
    }

    fun toggleLanguage() {
        triggerFeedback()
        if (currentMode == KeyboardLayoutMode.ODIA) {
            preferencesRepository.preferredLayout = "ENGLISH"
            currentMode = KeyboardLayoutMode.ENGLISH
        } else {
            preferencesRepository.preferredLayout = "ODIA"
            currentMode = KeyboardLayoutMode.ODIA
        }
    }

    val keyHeight: Dp = when (preferencesRepository.keyboardHeight) {
        "COMPACT" -> 40.dp
        "TALL" -> 52.dp
        else -> 46.dp
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF19181C))
            .padding(bottom = 6.dp)
    ) {
        // Toolbar: Language indicator, quick phrases, sticker button, settings button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF222026))
                .padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Language switch shortcut on toolbar
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(SambalpuriCrimson)
                    .clickable { toggleLanguage() }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Switch Language",
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (currentMode == KeyboardLayoutMode.ODIA) "ଓଡ଼ିଆ" else "ENG",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Horizontally scrolling Sambalpuri phrase chips
            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(KeyboardLayouts.sambalpuriQuickPhrases) { (phrase, _) ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF333038))
                            .clickable {
                                triggerFeedback()
                                onTextCommit("$phrase ")
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = phrase,
                            color = SambalpuriGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Emoji / Sticker panel toggle
            IconButton(
                onClick = {
                    triggerFeedback()
                    currentMode = if (currentMode == KeyboardLayoutMode.EMOJI_STICKER) {
                        if (preferencesRepository.preferredLayout == "ENGLISH")
                            KeyboardLayoutMode.ENGLISH
                        else
                            KeyboardLayoutMode.ODIA
                    } else {
                        KeyboardLayoutMode.EMOJI_STICKER
                    }
                },
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Mood,
                    contentDescription = "Emojis and Stickers",
                    tint = if (currentMode == KeyboardLayoutMode.EMOJI_STICKER) SambalpuriGold else Color.LightGray
                )
            }

            // Settings button
            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.LightGray
                )
            }
        }

        // Layout Body
        if (currentMode == KeyboardLayoutMode.EMOJI_STICKER) {
            EmojiStickerPickerView(
                repository = repository,
                onEmojiClicked = { emoji ->
                    triggerFeedback()
                    onTextCommit(emoji)
                },
                onStickerClicked = { sticker ->
                    triggerFeedback()
                    onStickerSelected(sticker)
                },
                onBackspaceClicked = {
                    triggerFeedback()
                    onDeleteSurroundingText()
                },
                onCloseClicked = {
                    triggerFeedback()
                    currentMode = if (preferencesRepository.preferredLayout == "ENGLISH")
                        KeyboardLayoutMode.ENGLISH
                    else
                        KeyboardLayoutMode.ODIA
                },
                onOpenAddCustom = onOpenAddCustom
            )
        } else {
            // Typing Layouts
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 3.dp, vertical = 3.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                when (currentMode) {
                    KeyboardLayoutMode.ODIA -> {
                        // Quick Matra Bar for Sambalpuri
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 1.dp),
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            items(KeyboardLayouts.odiaMatras) { matra ->
                                KeyButton(
                                    label = matra,
                                    height = 36.dp,
                                    bgColor = Color(0xFF2C2833),
                                    textColor = SambalpuriGold,
                                    fontSize = 15.sp,
                                    onClick = {
                                        triggerFeedback()
                                        onTextCommit(matra)
                                    }
                                )
                            }
                        }

                        // Consonants or Independent Vowels based on toggle
                        if (!isOdiaVowelsActive) {
                            KeyboardLayouts.odiaConsonantRows.forEach { row ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    row.forEach { char ->
                                        KeyButton(
                                            label = char,
                                            height = keyHeight,
                                            modifier = Modifier.weight(1f),
                                            onClick = {
                                                triggerFeedback()
                                                onTextCommit(char)
                                            }
                                        )
                                    }
                                }
                            }
                        } else {
                            KeyboardLayouts.odiaVowels.forEach { row ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    row.forEach { char ->
                                        KeyButton(
                                            label = char,
                                            height = keyHeight,
                                            modifier = Modifier.weight(1f),
                                            bgColor = Color(0xFF2D2A35),
                                            textColor = Color(0xFFFFD54F),
                                            onClick = {
                                                triggerFeedback()
                                                onTextCommit(char)
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Bottom Control Row: ?123, Vowel Switch, Dedicated Language Switch, Space, Enter, Backspace
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Symbols Switch
                            KeyButton(
                                label = "?123",
                                height = keyHeight,
                                bgColor = SambalpuriKeySpecialDark,
                                modifier = Modifier.weight(1.2f),
                                onClick = {
                                    triggerFeedback()
                                    currentMode = KeyboardLayoutMode.NUMBERS_SYMBOLS
                                }
                            )

                            // Vowel / Consonant Mode toggle
                            KeyButton(
                                label = if (isOdiaVowelsActive) "କ-ମ" else "ଅ-ଔ",
                                height = keyHeight,
                                bgColor = if (isOdiaVowelsActive) SambalpuriCrimson else SambalpuriKeySpecialDark,
                                modifier = Modifier.weight(1.2f),
                                onClick = {
                                    triggerFeedback()
                                    isOdiaVowelsActive = !isOdiaVowelsActive
                                }
                            )

                            // Dedicated Language Switch Key to English
                            Box(
                                modifier = Modifier
                                    .weight(1.2f)
                                    .height(keyHeight)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SambalpuriKeySpecialDark)
                                    .clickable { toggleLanguage() },
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Language,
                                        contentDescription = "Switch to English",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "ENG",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Spacebar showing active language
                            KeyButton(
                                label = "ଓଡ଼ିଆ (Sambalpuri)",
                                height = keyHeight,
                                bgColor = Color(0xFF2E2C34),
                                textColor = Color.LightGray,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(3.8f),
                                onClick = {
                                    triggerFeedback()
                                    onTextCommit(" ")
                                }
                            )

                            // Backspace
                            Box(
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(keyHeight)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SambalpuriKeySpecialDark)
                                    .clickable {
                                        triggerFeedback()
                                        onDeleteSurroundingText()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                                    contentDescription = "Backspace",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Enter / Action
                            Box(
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(keyHeight)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SambalpuriCrimson)
                                    .clickable {
                                        triggerFeedback()
                                        onPerformAction()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardReturn,
                                    contentDescription = "Enter",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    KeyboardLayoutMode.ENGLISH -> {
                        // English QWERTY
                        // Row 1
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            KeyboardLayouts.englishRow1.forEach { char ->
                                val display = if (isShiftActive) char.uppercase() else char
                                KeyButton(
                                    label = display,
                                    height = keyHeight,
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        triggerFeedback()
                                        onTextCommit(display)
                                    }
                                )
                            }
                        }

                        // Row 2
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Spacer(modifier = Modifier.weight(0.5f))
                            KeyboardLayouts.englishRow2.forEach { char ->
                                val display = if (isShiftActive) char.uppercase() else char
                                KeyButton(
                                    label = display,
                                    height = keyHeight,
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        triggerFeedback()
                                        onTextCommit(display)
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.weight(0.5f))
                        }

                        // Row 3: Shift, z-m, Backspace
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Shift
                            KeyButton(
                                label = if (isShiftActive) "⇧ ON" else "⇧",
                                height = keyHeight,
                                bgColor = if (isShiftActive) SambalpuriCrimson else SambalpuriKeySpecialDark,
                                modifier = Modifier.weight(1.5f),
                                onClick = {
                                    triggerFeedback()
                                    isShiftActive = !isShiftActive
                                }
                            )

                            KeyboardLayouts.englishRow3.forEach { char ->
                                val display = if (isShiftActive) char.uppercase() else char
                                KeyButton(
                                    label = display,
                                    height = keyHeight,
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        triggerFeedback()
                                        onTextCommit(display)
                                    }
                                )
                            }

                            // Backspace
                            Box(
                                modifier = Modifier
                                    .weight(1.5f)
                                    .height(keyHeight)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SambalpuriKeySpecialDark)
                                    .clickable {
                                        triggerFeedback()
                                        onDeleteSurroundingText()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                                    contentDescription = "Backspace",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Row 4: 123, Dedicated Language Switch to Odia, Space, Enter
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            KeyButton(
                                label = "?123",
                                height = keyHeight,
                                bgColor = SambalpuriKeySpecialDark,
                                modifier = Modifier.weight(1.3f),
                                onClick = {
                                    triggerFeedback()
                                    currentMode = KeyboardLayoutMode.NUMBERS_SYMBOLS
                                }
                            )

                            // Dedicated Language Switch Key to Odia
                            Box(
                                modifier = Modifier
                                    .weight(1.4f)
                                    .height(keyHeight)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SambalpuriKeySpecialDark)
                                    .clickable { toggleLanguage() },
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Language,
                                        contentDescription = "Switch to Odia",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "ଓଡ଼ିଆ",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Spacebar showing active English language
                            KeyButton(
                                label = "English (QWERTY)",
                                height = keyHeight,
                                bgColor = Color(0xFF2E2C34),
                                textColor = Color.LightGray,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(4.2f),
                                onClick = {
                                    triggerFeedback()
                                    onTextCommit(" ")
                                }
                            )

                            // Enter
                            Box(
                                modifier = Modifier
                                    .weight(1.4f)
                                    .height(keyHeight)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SambalpuriCrimson)
                                    .clickable {
                                        triggerFeedback()
                                        onPerformAction()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardReturn,
                                    contentDescription = "Enter",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    KeyboardLayoutMode.NUMBERS_SYMBOLS -> {
                        // Numbers & Symbols
                        // Row 1: Odia Numerals
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            KeyboardLayouts.odiaNumbers.forEach { num ->
                                KeyButton(
                                    label = num,
                                    height = keyHeight,
                                    bgColor = Color(0xFF2B2832),
                                    textColor = SambalpuriGold,
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        triggerFeedback()
                                        onTextCommit(num)
                                    }
                                )
                            }
                        }

                        // Row 2: Standard Numerals
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            KeyboardLayouts.standardNumbers.forEach { num ->
                                KeyButton(
                                    label = num,
                                    height = keyHeight,
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        triggerFeedback()
                                        onTextCommit(num)
                                    }
                                )
                            }
                        }

                        // Row 3: Symbols Row 1 (with Odia danda '।' and '॥')
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            KeyboardLayouts.symbolRow1.forEach { sym ->
                                KeyButton(
                                    label = sym,
                                    height = keyHeight,
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        triggerFeedback()
                                        onTextCommit(sym)
                                    }
                                )
                            }
                        }

                        // Row 4: Symbols Row 2 + Backspace
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            KeyboardLayouts.symbolRow2.take(8).forEach { sym ->
                                KeyButton(
                                    label = sym,
                                    height = keyHeight,
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        triggerFeedback()
                                        onTextCommit(sym)
                                    }
                                )
                            }
                            // Backspace
                            Box(
                                modifier = Modifier
                                    .weight(1.5f)
                                    .height(keyHeight)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SambalpuriKeySpecialDark)
                                    .clickable {
                                        triggerFeedback()
                                        onDeleteSurroundingText()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                                    contentDescription = "Backspace",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Bottom Row: Return to Odia / English, Space, Enter
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            KeyButton(
                                label = "ଓଡ଼ିଆ",
                                height = keyHeight,
                                bgColor = SambalpuriCrimson,
                                modifier = Modifier.weight(1.5f),
                                onClick = {
                                    triggerFeedback()
                                    preferencesRepository.preferredLayout = "ODIA"
                                    currentMode = KeyboardLayoutMode.ODIA
                                }
                            )

                            KeyButton(
                                label = "ABC",
                                height = keyHeight,
                                bgColor = SambalpuriKeySpecialDark,
                                modifier = Modifier.weight(1.5f),
                                onClick = {
                                    triggerFeedback()
                                    preferencesRepository.preferredLayout = "ENGLISH"
                                    currentMode = KeyboardLayoutMode.ENGLISH
                                }
                            )

                            KeyButton(
                                label = "Space",
                                height = keyHeight,
                                bgColor = Color(0xFF2E2C34),
                                textColor = Color.LightGray,
                                modifier = Modifier.weight(3.5f),
                                onClick = {
                                    triggerFeedback()
                                    onTextCommit(" ")
                                }
                            )

                            Box(
                                modifier = Modifier
                                    .weight(1.5f)
                                    .height(keyHeight)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SambalpuriCrimson)
                                    .clickable {
                                        triggerFeedback()
                                        onPerformAction()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardReturn,
                                    contentDescription = "Enter",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    else -> Unit
                }
            }
        }
    }
}

@Composable
private fun KeyButton(
    label: String,
    height: Dp,
    modifier: Modifier = Modifier,
    bgColor: Color = SambalpuriKeyDark,
    textColor: Color = Color.White,
    fontSize: androidx.compose.ui.unit.TextUnit = 16.sp,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = SambalpuriGold),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = fontSize,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}
