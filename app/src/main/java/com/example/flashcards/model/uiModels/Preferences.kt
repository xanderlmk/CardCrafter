package com.example.flashcards.model.uiModels

import android.content.Context
import android.os.Bundle
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

class CreatedBundle(private val savedStateHandle: Bundle?){
    fun getSavedHandle():Bundle?{
        return savedStateHandle
    }
}

class PreferencesManager(
    context: Context,
    ) {

    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    val customScheme: MutableState<Boolean> = mutableStateOf(false)
    val darkTheme: MutableState<Boolean> = mutableStateOf(false)

    init {
        // Initialize themes
        darkTheme.value = isDarkThemeEnabled
        customScheme.value = isDynamicThemeEnabled
    }


    fun saveCustomScheme() {
        isDynamicThemeEnabled = customScheme.value
    }

    fun saveDarkTheme() {
        isDarkThemeEnabled = darkTheme.value
    }

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

    var isFirstTime: Boolean
        get() = sharedPreferences.getBoolean("first_time", true)
        set(value) {
            sharedPreferences.edit().putBoolean("first_time", value).apply()
        }
}