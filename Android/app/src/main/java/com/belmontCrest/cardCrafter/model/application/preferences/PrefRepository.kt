package com.belmontCrest.cardCrafter.model.application.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.belmontCrest.cardCrafter.model.application.dataStore
import com.belmontCrest.cardCrafter.uiFunctions.showToastMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

interface PrefRepository {
    val darkTheme: Flow<Boolean>
    val dynamicTheme: Flow<Boolean>
    val cuteTheme: Flow<Boolean>
    val cardAmount: Flow<Int>
    val reviewAmount: Flow<Int>
    val height: Flow<Int>
    val width: Flow<Int>
    fun initialDynamicTheme(): Boolean
    fun initialDarkTheme(): Boolean
    fun initialCuteTheme(): Boolean
    suspend fun saveDynamicTheme(value: Boolean)
    suspend fun saveDarkTheme(value: Boolean)
    suspend fun saveCuteTheme(value: Boolean)
    suspend fun saveCardAmount(amount: Int)
    suspend fun saveReviewAmount(amount: Int)
    suspend fun saveKatexMenuWidth(w: Int)
    suspend fun saveKatexMenuHeight(h: Int)
    suspend fun setDarkTheme(isDarkTheme: Boolean)
    suspend fun getIsFirstTime(): Boolean
    suspend fun setIsFirstTime()
}


class PrefRepositoryImpl(
    private val context: Context, private val sharedPrefs: SharedPreferences
) : PrefRepository {
    companion object {
        private val FIRST_TIME = booleanPreferencesKey("first_time")
        private val DARK_THEME = booleanPreferencesKey("dark_theme")
        private val DYNAMIC_THEME = booleanPreferencesKey("dynamic_theme")
        private val CUTE_THEME = booleanPreferencesKey("cute_theme")
        private val CARD_AMOUNT = intPreferencesKey("card_amount")
        private val REVIEW_AMOUNT = intPreferencesKey("review_amount")
        private val HEIGHT = intPreferencesKey("height")
        private val WIDTH = intPreferencesKey("width")
    }

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

    override val darkTheme = context.dataStore.data.map { preferences ->
        preferences[DARK_THEME] == true
    }.flowOn(Dispatchers.IO)

    override val dynamicTheme = context.dataStore.data.map { preferences ->
        preferences[DYNAMIC_THEME] == true
    }.flowOn(Dispatchers.IO)

    override val cuteTheme = context.dataStore.data.map { preferences ->
        preferences[CUTE_THEME] == true
    }.flowOn(Dispatchers.IO)

    override val cardAmount = context.dataStore.data.map { preferences ->
        preferences[CARD_AMOUNT] ?: 20
    }

    override val reviewAmount = context.dataStore.data.map { preferences ->
        preferences[REVIEW_AMOUNT] ?: 1
    }

    override val height = context.dataStore.data.map { preferences ->
        preferences[HEIGHT] ?: 200
    }

    override val width = context.dataStore.data.map { preferences ->
        preferences[WIDTH] ?: Int.MIN_VALUE
    }

    override fun initialDynamicTheme() = isDynamicThemeEnabled

    override fun initialDarkTheme() = isDarkThemeEnabled

    override fun initialCuteTheme() = isCuteThemeEnabled

    override suspend fun saveDynamicTheme(value: Boolean) = withContext(Dispatchers.IO) {
        try {
            context.dataStore.edit { settings ->
                isDynamicThemeEnabled = value
                settings[DYNAMIC_THEME] = value
            }
        } catch (e: Exception) {
            showToastMessage(context, "$e")
            return@withContext
        }
    }

    override suspend fun saveDarkTheme(value: Boolean) = withContext(Dispatchers.IO) {
        try {
            context.dataStore.edit { settings ->
                isDarkThemeEnabled = value
                settings[DARK_THEME] = value
            }
        } catch (e: Exception) {
            showToastMessage(context, "$e")
            return@withContext
        }
    }

    override suspend fun saveCuteTheme(value: Boolean) = withContext(Dispatchers.IO) {
        try {
            context.dataStore.edit { settings ->
                isCuteThemeEnabled = value
                settings[CUTE_THEME] = value
            }
        } catch (e: Exception) {
            showToastMessage(context, "$e")
            return@withContext
        }
    }

    override suspend fun saveCardAmount(amount: Int) = withContext(Dispatchers.IO) {
        try {
            context.dataStore.edit { settings -> settings[CARD_AMOUNT] = amount }
        } catch (e: Exception) {
            showToastMessage(context, "$e")
            return@withContext
        }
    }

    override suspend fun saveReviewAmount(amount: Int) = withContext(Dispatchers.IO) {
        try {
            context.dataStore.edit { settings -> settings[REVIEW_AMOUNT] = amount }
        } catch (e: Exception) {
            showToastMessage(context, "$e")
            return@withContext
        }
    }

    override suspend fun saveKatexMenuHeight(h: Int) = withContext(Dispatchers.IO) {
        try {
            context.dataStore.edit { settings -> settings[HEIGHT] = h }
        } catch (e: Exception) {
            showToastMessage(context, "$e")
            return@withContext
        }
    }

    override suspend fun saveKatexMenuWidth(w: Int) = withContext(Dispatchers.IO) {
        try {
            context.dataStore.edit { settings -> settings[WIDTH] = w }
        } catch (e: Exception) {
            showToastMessage(context, "$e")
            return@withContext
        }
    }

    override suspend fun setDarkTheme(isDarkTheme: Boolean) = withContext(Dispatchers.IO) {
        try {
            context.dataStore.edit { settings ->
                isDarkThemeEnabled = isDarkTheme
                settings[DARK_THEME] = isDarkTheme
            }
        } catch (e: Exception) {
            showToastMessage(context, "$e")
            return@withContext
        }
    }

    private val isFirstTime = context.dataStore.data.map { preferences ->
        preferences[FIRST_TIME] ?: false
    }

    override suspend fun getIsFirstTime(): Boolean = isFirstTime.first()

    override suspend fun setIsFirstTime() = withContext(Dispatchers.IO) {
        try {
            context.dataStore.edit { settings ->
                isItFirstTime = true
                settings[FIRST_TIME] = true
            }
        } catch (e: Exception) {
            showToastMessage(context, "$e")
            return@withContext
        }
    }
}