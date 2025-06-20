package com.belmontCrest.cardCrafter.model.application

import android.content.Context
import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.core.content.edit
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.parcelize.Parcelize

class PreferencesManager(
    context: Context,
) {
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    private val _dynamicTheme = MutableStateFlow(false)
    val dynamicTheme = _dynamicTheme.asStateFlow()
    private val _darkTheme = MutableStateFlow(false)
    val darkTheme = _darkTheme.asStateFlow()
    private val _cuteTheme = MutableStateFlow(false)
    val cuteTheme = _cuteTheme.asStateFlow()
    private val _cardAmount = MutableStateFlow(20)
    val cardAmount = _cardAmount.asStateFlow()
    private val _reviewAmount = MutableStateFlow(1)
    val reviewAmount = _reviewAmount.asStateFlow()
    private val _height = MutableStateFlow(200)
    val height = _height.asStateFlow()
    private val _width = MutableStateFlow(200)
    val width = _width.asStateFlow()

    init {
        _dynamicTheme.update { isDynamicThemeEnabled }
        _darkTheme.update { isDarkThemeEnabled }
        _cuteTheme.update { isCuteThemeEnabled }
        _cardAmount.update { allCardAmounts }
        _reviewAmount.update { allReviewAmounts }
        _height.update { katexMenuHeight }
        _width.update { katexMenuWidth }
    }

    fun saveDynamicTheme() {
        _dynamicTheme.update { !it }; isDynamicThemeEnabled = _dynamicTheme.value
    }

    fun saveDarkTheme() {
        _darkTheme.update { !it }; isDarkThemeEnabled = _darkTheme.value
    }

    fun saveCuteTheme(value: Boolean = !_cuteTheme.value) {
        _cuteTheme.update { value }; isCuteThemeEnabled = _cuteTheme.value
    }

    fun saveCardAmount(amount: Int) {
        _cardAmount.update { amount }; allCardAmounts = amount
    }

    fun saveReviewAmount(amount: Int) {
        _reviewAmount.update { amount }; allReviewAmounts = amount
    }

    fun saveKatexMenuHeight(h: Int) {
        _height.update { h }; katexMenuHeight = h
    }

    fun saveKatexMenuWidth(w: Int) {
        _width.update { w }; katexMenuWidth = w
    }

    fun savePreferences() {
        isDynamicThemeEnabled = _dynamicTheme.value
        isDarkThemeEnabled = _darkTheme.value
        isCuteThemeEnabled = _cuteTheme.value
        allCardAmounts = _cardAmount.value
        allReviewAmounts = _reviewAmount.value
        katexMenuHeight = _height.value
        katexMenuWidth = _width.value
    }

    private var isDynamicThemeEnabled: Boolean
        get() = sharedPreferences.getBoolean("dynamic_theme", false)
        set(value) = sharedPreferences.edit { putBoolean("dynamic_theme", value) }

    private var isDarkThemeEnabled: Boolean
        get() = sharedPreferences.getBoolean("dark_theme", false)
        set(value) = sharedPreferences.edit { putBoolean("dark_theme", value) }

    private var isCuteThemeEnabled: Boolean
        get() = sharedPreferences.getBoolean("cute_theme", false)
        set(value) = sharedPreferences.edit { putBoolean("cute_theme", value) }


    fun setDarkTheme(isDarkTheme: Boolean) {
        isDarkThemeEnabled = isDarkTheme
    }

    private var isFirstTime: Boolean
        get() = sharedPreferences.getBoolean("first_time", true)
        set(value) = sharedPreferences.edit { putBoolean("first_time", value) }

    fun getIsFirstTime(): Boolean = isFirstTime

    fun setIsFirstTime() {
        isFirstTime = false
    }

    private var allCardAmounts: Int
        get() = sharedPreferences.getInt("card_amount", 20)
        set(value) = sharedPreferences.edit { putInt("card_amount", value) }


    private var allReviewAmounts: Int
        get() = sharedPreferences.getInt("review_amount", 1)
        set(value) = sharedPreferences.edit { putInt("review_amount", value) }


    private var katexMenuHeight: Int
        get() = sharedPreferences.getInt("menu_height", 200)
        set(value) = sharedPreferences.edit { putInt("menu_height", value) }

    private var katexMenuWidth: Int
        get() = sharedPreferences.getInt("menu_width", Int.MIN_VALUE)
        set(value) = sharedPreferences.edit { putInt("menu_width", value) }
}

@Parcelize
data class PreferenceValues(
    val darkTheme: Boolean, val cuteTheme: Boolean, val dynamicTheme: Boolean,
    val cardAmount: Int, val reviewAmount: Int, val height: Int, val width: Int
) : Parcelable

@Composable
fun setPreferenceValues(preferencesManager: PreferencesManager): PreferenceValues {
    val dt by preferencesManager.darkTheme.collectAsStateWithLifecycle()
    val ct by preferencesManager.cuteTheme.collectAsStateWithLifecycle()
    val dynamicTheme by preferencesManager.dynamicTheme.collectAsStateWithLifecycle()
    val ca by preferencesManager.cardAmount.collectAsStateWithLifecycle()
    val ra by preferencesManager.reviewAmount.collectAsStateWithLifecycle()
    val height by preferencesManager.height.collectAsStateWithLifecycle()
    val width by preferencesManager.width.collectAsStateWithLifecycle()
    return PreferenceValues(
        darkTheme = dt, cuteTheme = ct, dynamicTheme = dynamicTheme,
        cardAmount = ca, reviewAmount = ra, height = height, width = width
    )
}