package com.example.keyboard

import android.content.ClipDescription
import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.inputmethod.EditorInfoCompat
import androidx.core.view.inputmethod.InputConnectionCompat
import androidx.core.view.inputmethod.InputContentInfoCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.MainActivity
import com.example.data.PreferencesRepository
import com.example.data.StickerItem
import com.example.data.StickerRepository
import com.example.ui.theme.SambalpuriKeyboardTheme

class SambalpuriInputMethodService : InputMethodService(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private val store = ViewModelStore()

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry
    override val viewModelStore: ViewModelStore get() = store

    private lateinit var stickerRepository: StickerRepository
    private lateinit var preferencesRepository: PreferencesRepository

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        stickerRepository = StickerRepository(this)
        preferencesRepository = PreferencesRepository(this)
    }

    override fun onCreateInputView(): View {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)

        val composeView = ComposeView(this).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setViewTreeLifecycleOwner(this@SambalpuriInputMethodService)
            setViewTreeViewModelStoreOwner(this@SambalpuriInputMethodService)
            setViewTreeSavedStateRegistryOwner(this@SambalpuriInputMethodService)

            setContent {
                SambalpuriKeyboardTheme {
                    KeyboardView(
                        repository = stickerRepository,
                        preferencesRepository = preferencesRepository,
                        onTextCommit = { text ->
                            currentInputConnection?.commitText(text, 1)
                        },
                        onDeleteSurroundingText = {
                            val ic = currentInputConnection ?: return@KeyboardView
                            val selected = ic.getSelectedText(0)
                            if (!selected.isNullOrEmpty()) {
                                ic.commitText("", 1)
                            } else {
                                ic.deleteSurroundingText(1, 0)
                            }
                        },
                        onPerformAction = {
                            val ic = currentInputConnection ?: return@KeyboardView
                            val editorInfo = currentInputEditorInfo
                            val imeOptions = editorInfo?.imeOptions ?: 0
                            val actionId = imeOptions and EditorInfo.IME_MASK_ACTION
                            if (actionId != EditorInfo.IME_ACTION_NONE && actionId != EditorInfo.IME_ACTION_UNSPECIFIED) {
                                ic.performEditorAction(actionId)
                            } else {
                                ic.commitText("\n", 1)
                            }
                        },
                        onStickerSelected = { sticker ->
                            handleStickerCommit(sticker)
                        },
                        onOpenSettings = {
                            val intent = Intent(this@SambalpuriInputMethodService, MainActivity::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            startActivity(intent)
                        },
                        onOpenAddCustom = {
                            val intent = Intent(this@SambalpuriInputMethodService, MainActivity::class.java).apply {
                                putExtra("NAV_TARGET", "my_stickers")
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            startActivity(intent)
                        }
                    )
                }
            }
        }
        return composeView
    }

    override fun onWindowShown() {
        super.onWindowShown()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onWindowHidden() {
        super.onWindowHidden()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        store.clear()
    }

    /**
     * Attempts commitContent() to the active input connection.
     * If the target app doesn't support rich image input, falls back to Android Share Chooser.
     */
    private fun handleStickerCommit(sticker: StickerItem) {
        val uri: Uri = stickerRepository.getSharableUriForSticker(sticker) ?: run {
            Toast.makeText(this, "Could not load sticker image", Toast.LENGTH_SHORT).show()
            return
        }

        stickerRepository.markRecent(sticker.id)

        val editorInfo = currentInputEditorInfo
        val ic = currentInputConnection

        val supportedMimeTypes = if (editorInfo != null) {
            EditorInfoCompat.getContentMimeTypes(editorInfo)
        } else {
            emptyArray()
        }

        val hasImageSupport = supportedMimeTypes.any {
            it.startsWith("image/", ignoreCase = true) || it == "*/*"
        }

        if (ic != null && editorInfo != null && hasImageSupport) {
            val mimeType = if (sticker.fileUriString?.endsWith(".webp", true) == true) "image/webp" else "image/png"
            val description = ClipDescription(sticker.name, arrayOf(mimeType))
            val inputContentInfo = InputContentInfoCompat(uri, description, null)

            val flags = InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION
            val committed = InputConnectionCompat.commitContent(ic, editorInfo, inputContentInfo, flags, null)

            if (committed) {
                return
            }
        }

        // Graceful Fallback: Share Intent
        Toast.makeText(this, "Sharing ${sticker.sambalpuriLabel}...", Toast.LENGTH_SHORT).show()
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "${sticker.sambalpuriLabel} - Sambalpuri Keyboard")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(shareIntent, "Share Sticker").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(chooser)
    }
}
