package com.belmontCrest.cardCrafter.model.uiModels

import android.content.Context
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.edit

class PreferencesManager(
    context: Context,
) {
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val customScheme: MutableState<Boolean> = mutableStateOf(false)
    val darkTheme: MutableState<Boolean> = mutableStateOf(false)
    val cardAmount: MutableIntState = mutableIntStateOf(20)
    val reviewAmount: MutableIntState = mutableIntStateOf(1)
    init {
        // Initialize themes
        darkTheme.value = isDarkThemeEnabled
        customScheme.value = isDynamicThemeEnabled
        cardAmount.intValue = allCardAmounts
        reviewAmount.intValue = allReviewAmounts
    }

    fun saveCustomScheme() {
        isDynamicThemeEnabled = customScheme.value
    }

    fun saveDarkTheme() {
        isDarkThemeEnabled = darkTheme.value
    }

    fun saveCardAmount() {
        allCardAmounts = cardAmount.intValue
    }

    fun saveReviewAmount() {
        allReviewAmounts = reviewAmount.intValue
    }
    fun savePreferences(){
        isDynamicThemeEnabled = customScheme.value
        isDarkThemeEnabled = darkTheme.value
        allCardAmounts = cardAmount.intValue
        allReviewAmounts = reviewAmount.intValue
    }
    private var isDynamicThemeEnabled: Boolean
        get() = sharedPreferences.getBoolean("dynamic_theme", false)
        set(value) {
            sharedPreferences.edit { putBoolean("dynamic_theme", value) }
        }
    private var isDarkThemeEnabled: Boolean
        get() = sharedPreferences.getBoolean("dark_theme", false)
        set(value) {
            sharedPreferences.edit { putBoolean("dark_theme", value) }
        }
    fun setDarkTheme(isDarkTheme : Boolean ){
        isDarkThemeEnabled = isDarkTheme
    }

    private var isFirstTime: Boolean
        get() = sharedPreferences.getBoolean("first_time", true)
        set(value) {
            sharedPreferences.edit { putBoolean("first_time", value) }
        }
    fun getIsFirstTime() : Boolean {
        return isFirstTime
    }
    fun setIsFirstTime() {
        isFirstTime = false
    }
    private var allCardAmounts: Int
        get() = sharedPreferences.getInt("card_amount", 20)
        set(value) {
            sharedPreferences.edit { putInt("card_amount", value) }
        }

    private var allReviewAmounts: Int
        get() = sharedPreferences.getInt("review_amount", 1)
        set(value) {
            sharedPreferences.edit { putInt("review_amount", value) }
        }
}