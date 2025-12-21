package com.belmontCrest.cardCrafter.model.application.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.belmontCrest.cardCrafter.model.application.dataStore
import com.belmontCrest.cardCrafter.model.onLogError
import com.belmontCrest.cardCrafter.model.ui.CCTheme
import com.belmontCrest.cardCrafter.ui.functions.showToastMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

interface PrefRepository {
    val theme: Flow<CCTheme>
    val cardAmount: Flow<Int>
    val reviewAmount: Flow<Int>
    val height: Flow<Int>
    val width: Flow<Int>
    suspend fun saveTheme(new: CCTheme)
    suspend fun saveCardAmount(amount: Int)
    suspend fun saveReviewAmount(amount: Int)
    suspend fun saveKatexMenuWidth(w: Int)
    suspend fun saveKatexMenuHeight(h: Int)
}


class PrefRepositoryImpl(private val context: Context) : PrefRepository {
    companion object {
        private val CARD_AMOUNT = intPreferencesKey("card_amount")
        private val REVIEW_AMOUNT = intPreferencesKey("review_amount")
        private val HEIGHT = intPreferencesKey("height")
        private val WIDTH = intPreferencesKey("width")
        private val THEME = stringPreferencesKey("app_theme")
    }

    override val theme = context.dataStore.data.map { preferences ->
        try {
            preferences[THEME]?.let {
                Json.decodeFromString(CCTheme.serializer(), it)
            } ?: CCTheme.Default
        } catch (_: Exception) {
            onLogError("Failed to decode theme, resorting to default.")
            CCTheme.Default
        }
    }
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


    override suspend fun saveTheme(new: CCTheme) = withContext(Dispatchers.IO) {
        try {
            context.dataStore.edit { settings ->
                settings[THEME] = Json.encodeToString(CCTheme.serializer(), new)
            }
        } catch (e: Exception) {
            onLogError("${e.printStackTrace()}")
            withContext(Dispatchers.Main) {
                showToastMessage(context, "Failed to update theme")
            }
            return@withContext
        }
    }

    override suspend fun saveCardAmount(amount: Int) = withContext(Dispatchers.IO) {
        try {
            context.dataStore.edit { settings -> settings[CARD_AMOUNT] = amount }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                showToastMessage(context, "$e")
            }
            return@withContext
        }
    }

    override suspend fun saveReviewAmount(amount: Int) = withContext(Dispatchers.IO) {
        try {
            context.dataStore.edit { settings -> settings[REVIEW_AMOUNT] = amount }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                showToastMessage(context, "$e")
            }
            return@withContext
        }
    }

    override suspend fun saveKatexMenuHeight(h: Int) = withContext(Dispatchers.IO) {
        try {
            context.dataStore.edit { settings -> settings[HEIGHT] = h }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                showToastMessage(context, "$e")
            }
            return@withContext
        }
    }

    override suspend fun saveKatexMenuWidth(w: Int) = withContext(Dispatchers.IO) {
        try {
            context.dataStore.edit { settings -> settings[WIDTH] = w }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                showToastMessage(context, "$e")
            }
            return@withContext
        }
    }

}