package com.example.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PreferencesRepository
import com.example.data.StickerRepository
import com.example.keyboard.KeyboardView
import com.example.ui.components.KeyboardStatusHelper
import com.example.ui.theme.SambalpuriCrimson
import com.example.ui.theme.SambalpuriGold
import com.example.ui.theme.SambalpuriMarigold

@Composable
fun KeyboardPreviewScreen(
    repository: StickerRepository,
    preferencesRepository: PreferencesRepository,
    onBack: () -> Unit,
    onNavigateToMyStickers: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var typedText by remember { mutableStateOf("ଜୁହାର ! ସମ୍ବଲପୁରୀ କୀବୋର୍ଡ଼ ପରୀକ୍ଷା କରନ୍ତୁ ।") }
    var lastStickerShared by remember { mutableStateOf<String?>(null) }

    fun copyText() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Sambalpuri Text", typedText)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
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
                        text = "Keyboard Interactive Preview",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Try typing with Odia script, English QWERTY, and stickers",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = { KeyboardStatusHelper.showInputMethodPicker(context) }
                ) {
                    Icon(
                        imageVector = Icons.Default.TouchApp,
                        contentDescription = "Switch Input Method",
                        tint = SambalpuriCrimson
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Section: Notepad & Controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = SambalpuriMarigold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Interactive Simulator: Tapping keys below enters text directly. Use the language button to switch between Odia and English.",
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = typedText,
                    onValueChange = { typedText = it },
                    label = { Text("Interactive Notepad") },
                    placeholder = { Text("Type here in Sambalpuri or English...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = SambalpuriCrimson,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Actions bar: Copy, Clear, Share status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { copyText() },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = { typedText = "" },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Clear", fontSize = 11.sp)
                        }
                    }

                    if (lastStickerShared != null) {
                        Text(
                            text = "Selected: $lastStickerShared",
                            fontSize = 11.sp,
                            color = SambalpuriCrimson,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Embedded Keyboard Simulator
            KeyboardView(
                repository = repository,
                preferencesRepository = preferencesRepository,
                onTextCommit = { text ->
                    typedText += text
                },
                onDeleteSurroundingText = {
                    if (typedText.isNotEmpty()) {
                        typedText = typedText.dropLast(1)
                    }
                },
                onPerformAction = {
                    typedText += "\n"
                },
                onStickerSelected = { sticker ->
                    lastStickerShared = sticker.sambalpuriLabel
                    typedText += " [🎨 ${sticker.sambalpuriLabel}] "
                    val uri = repository.getSharableUriForSticker(sticker)
                    if (uri != null) {
                        Toast.makeText(context, "Selected sticker: ${sticker.sambalpuriLabel}", Toast.LENGTH_SHORT).show()
                    }
                },
                onOpenSettings = {
                    Toast.makeText(context, "Configure settings in Home -> Keyboard Settings", Toast.LENGTH_SHORT).show()
                },
                onOpenAddCustom = onNavigateToMyStickers
            )
        }
    }
}
