package com.example.data

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

class CustomStickerManager(private val context: Context) {
    private val prefs = context.getSharedPreferences("custom_stickers_store", Context.MODE_PRIVATE)
    private val customDir = File(context.filesDir, "custom_stickers").apply {
        if (!exists()) mkdirs()
    }

    companion object {
        private const val KEY_CUSTOM_ITEMS = "custom_items_json"
    }

    fun getAllCustomStickers(): List<StickerItem> {
        val jsonStr = prefs.getString(KEY_CUSTOM_ITEMS, "[]") ?: "[]"
        val list = mutableListOf<StickerItem>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val id = obj.getString("id")
                val fileName = obj.getString("fileName")
                val file = File(customDir, fileName)
                if (file.exists()) {
                    list.add(
                        StickerItem(
                            id = id,
                            name = obj.getString("name"),
                            sambalpuriLabel = obj.optString("sambalpuriLabel", obj.getString("name")),
                            category = obj.optString("category", "Custom Stickers"),
                            fileUriString = file.absolutePath,
                            tags = listOf("custom", "user", obj.getString("name").lowercase()),
                            isCustom = true,
                            dateAdded = obj.optLong("dateAdded", System.currentTimeMillis())
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun importSticker(sourceUri: Uri, name: String, category: String, sambalpuriLabel: String?): StickerItem? {
        return try {
            val id = "custom_" + UUID.randomUUID().toString().take(8)
            val extension = getExtension(sourceUri)
            val targetFileName = "$id.$extension"
            val targetFile = File(customDir, targetFileName)

            val inputStream: InputStream? = context.contentResolver.openInputStream(sourceUri)
            val outputStream = FileOutputStream(targetFile)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            val newItem = StickerItem(
                id = id,
                name = name.ifBlank { "My Sticker" },
                sambalpuriLabel = sambalpuriLabel?.ifBlank { name } ?: name,
                category = category.ifBlank { "Custom Stickers" },
                fileUriString = targetFile.absolutePath,
                tags = listOf("custom", name.lowercase()),
                isCustom = true,
                dateAdded = System.currentTimeMillis()
            )

            // Save to JSON
            val currentList = getAllCustomStickers().toMutableList()
            currentList.add(0, newItem)
            saveList(currentList)

            newItem
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun deleteCustomSticker(id: String): Boolean {
        return try {
            val currentList = getAllCustomStickers().toMutableList()
            val itemToRemove = currentList.find { it.id == id }
            if (itemToRemove != null) {
                if (itemToRemove.fileUriString != null) {
                    val file = File(itemToRemove.fileUriString)
                    if (file.exists()) {
                        file.delete()
                    }
                }
                currentList.remove(itemToRemove)
                saveList(currentList)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getSharableUri(item: StickerItem): Uri? {
        return try {
            if (item.fileUriString != null) {
                val file = File(item.fileUriString)
                if (file.exists()) {
                    FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )
                } else null
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun saveList(items: List<StickerItem>) {
        val array = JSONArray()
        for (item in items) {
            val file = item.fileUriString?.let { File(it) }
            val fileName = file?.name ?: "${item.id}.png"
            val obj = JSONObject().apply {
                put("id", item.id)
                put("name", item.name)
                put("sambalpuriLabel", item.sambalpuriLabel)
                put("category", item.category)
                put("fileName", fileName)
                put("dateAdded", item.dateAdded)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_CUSTOM_ITEMS, array.toString()).apply()
    }

    private fun getExtension(uri: Uri): String {
        val type = context.contentResolver.getType(uri) ?: ""
        return when {
            type.contains("png") -> "png"
            type.contains("webp") -> "webp"
            type.contains("jpeg") || type.contains("jpg") -> "jpg"
            else -> "png"
        }
    }
}
