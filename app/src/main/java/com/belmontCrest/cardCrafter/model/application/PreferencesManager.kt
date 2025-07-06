package com.belmontCrest.cardCrafter.model.application

import android.content.Context
import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.core.content.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.belmontCrest.cardCrafter.uiFunctions.showToastMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

class PreferencesManager(
    private val context: Context, private val scope: CoroutineScope,
) {
    companion object {
        private val FIRST_TIME = booleanPreferencesKey("first_time")
        private val DARK_THEME = booleanPreferencesKey("dark_theme")
        private val DYNAMIC_THEME = booleanPreferencesKey("dynamic_theme")
        private val CUTE_THEME = booleanPreferencesKey("cute_theme")
        private val CARD_AMOUNT = intPreferencesKey("card_amount")
        private val REVIEW_AMOUNT = intPreferencesKey("review_amount")
        private val HEIGHT = intPreferencesKey("height")
        private val WIDTH = intPreferencesKey("width")
        private const val TIMEOUT_MILLIS = 4_000L
    }

    private val sharedPrefs = context.getSharedPreferences("theme", Context.MODE_PRIVATE)
    private var isDynamicThemeEnabled: Boolean
        get() = sharedPrefs.getBoolean("dynamic_theme", false)
        set(value) = sharedPrefs.edit { putBoolean("dynamic_theme", value) }

    private var isDarkThemeEnabled: Boolean
        get() = sharedPrefs.getBoolean("dark_theme", false)
        set(value) = sharedPrefs.edit { putBoolean("dark_theme", value) }

    private var isCuteThemeEnabled: Boolean
        get() = sharedPrefs.getBoolean("cute_theme", false)
        set(value) = sharedPrefs.edit { putBoolean("cute_theme", value) }

    private var isItFirstTime: Boolean
        get() = sharedPrefs.getBoolean("first_time", false)
        set(value) = sharedPrefs.edit { putBoolean("first_time", value) }

    val darkTheme = context.dataStore.data.map { preferences ->
        preferences[DARK_THEME] == true
    }.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = isDarkThemeEnabled
    )

    val dynamicTheme = context.dataStore.data.map { preferences ->
        preferences[DYNAMIC_THEME] == true
    }.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = isDynamicThemeEnabled
    )

    val cuteTheme = context.dataStore.data.map { preferences ->
        preferences[CUTE_THEME] == true
    }.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = isCuteThemeEnabled
    )

    val cardAmount = context.dataStore.data.map { preferences ->
        preferences[CARD_AMOUNT] ?: 20
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = 20
    )

    val reviewAmount = context.dataStore.data.map { preferences ->
        preferences[REVIEW_AMOUNT] ?: 1
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = 1
    )

    val height = context.dataStore.data.map { preferences ->
        preferences[HEIGHT] ?: 200
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = 200
    )

    val width = context.dataStore.data.map { preferences ->
        preferences[WIDTH] ?: Int.MIN_VALUE
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = Int.MIN_VALUE
    )

    fun saveDynamicTheme() {
        try {
            scope.launch {
                context.dataStore.edit { settings ->
                    isDynamicThemeEnabled = !dynamicTheme.value
                    settings[DYNAMIC_THEME] = !dynamicTheme.value
                }
            }
        } catch (e: Exception) {
            showToastMessage(context, "$e")
        }
    }

    fun saveDarkTheme() {
        try {
            scope.launch {
                context.dataStore.edit { settings ->
                    isDarkThemeEnabled = !darkTheme.value
                    settings[DARK_THEME] = !darkTheme.value
                }
            }
        } catch (e: Exception) {
            showToastMessage(context, "$e")
        }
    }

    fun saveCuteTheme(value: Boolean = !cuteTheme.value) {
        try {
            scope.launch {
                context.dataStore.edit { settings ->
                    isCuteThemeEnabled = value
                    settings[CUTE_THEME] = value
                }
            }
        } catch (e: Exception) {
            showToastMessage(context, "$e")
        }
    }

    fun saveCardAmount(amount: Int) {
        try {
            scope.launch {
                context.dataStore.edit { settings ->
                    settings[CARD_AMOUNT] = amount
                }
            }
        } catch (e: Exception) {
            showToastMessage(context, "$e")
        }
    }

    fun saveReviewAmount(amount: Int) {
        try {
            scope.launch {
                context.dataStore.edit { settings ->
                    settings[REVIEW_AMOUNT] = amount
                }
            }
        } catch (e: Exception) {
            showToastMessage(context, "$e")
        }
    }

    fun saveKatexMenuHeight(h: Int) {
        try {
            scope.launch {
                context.dataStore.edit { settings ->
                    settings[HEIGHT] = h
                }
            }
        } catch (e: Exception) {
            showToastMessage(context, "$e")
        }
    }

    fun saveKatexMenuWidth(w: Int) {
        try {
            scope.launch {
                context.dataStore.edit { settings ->
                    settings[WIDTH] = w
                }
            }
        } catch (e: Exception) {
            showToastMessage(context, "$e")
        }
    }

    fun setDarkTheme(isDarkTheme: Boolean) {
        try {
            scope.launch {
                context.dataStore.edit { settings ->
                    settings[DARK_THEME] = isDarkTheme
                }
            }
        } catch (e: Exception) {
            showToastMessage(context, "$e")
        }
    }

    private val isFirstTime = context.dataStore.data.map { preferences ->
        preferences[FIRST_TIME] ?: false
    }.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = isItFirstTime
    )

    fun getIsFirstTime(): Boolean = isFirstTime.value

    fun setIsFirstTime() {
        try {
            scope.launch {
                context.dataStore.edit { settings ->
                    isItFirstTime = true
                    settings[FIRST_TIME] = true
                }
            }
        } catch (e: Exception) {
            showToastMessage(context, "$e")
        }
    }
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