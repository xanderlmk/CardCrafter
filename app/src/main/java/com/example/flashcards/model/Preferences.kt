package com.example.flashcards.model

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

class Preferences(
    val customScheme: MutableState<Boolean> = mutableStateOf(false),
    val darkTheme : MutableState<Boolean>,
    private val preferencesManager: PreferencesManager
){
    init {
        // Initialize themes
        darkTheme.value = preferencesManager.isDarkThemeEnabled
        customScheme.value = preferencesManager.isDynamicThemeEnabled
    }

    fun saveCustomScheme() {
        preferencesManager.isDynamicThemeEnabled = customScheme.value
    }

    fun saveDarkTheme() {
        preferencesManager.isDarkThemeEnabled = darkTheme.value
    }
}

class PreferencesManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    var isDynamicThemeEnabled: Boolean
        get() = sharedPreferences.getBoolean("dynamic_theme", false)
        set(value) {
            sharedPreferences.edit().putBoolean("dynamic_theme", value).apply()
        }
    var isDarkThemeEnabled: Boolean
        get() = sharedPreferences.getBoolean("dark_theme", false)
        set(value) {
            sharedPreferences.edit().putBoolean("dark_theme", value).apply()
        }
}