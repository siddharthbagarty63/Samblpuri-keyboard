package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.inputmethod.InputMethodManager

object KeyboardStatusHelper {

    fun isKeyboardEnabled(context: Context): Boolean {
        return try {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                ?: return false
            val enabledList = imm.enabledInputMethodList
            enabledList.any { it.packageName == context.packageName }
        } catch (e: Exception) {
            false
        }
    }

    fun isKeyboardSelected(context: Context): Boolean {
        return try {
            val currentIme = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.DEFAULT_INPUT_METHOD
            ) ?: return false
            currentIme.contains(context.packageName)
        } catch (e: Exception) {
            false
        }
    }

    fun openInputMethodSettings(context: Context) {
        val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun showInputMethodPicker(context: Context) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.showInputMethodPicker()
    }
}
