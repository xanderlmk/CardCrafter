package com.belmontCrest.cardCrafter.model.application.preferences

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

class PreferencesManager(
    private val prefRepository: PrefRepository, private val scope: CoroutineScope
) {
    companion object {
        private const val TIMEOUT_MILLIS = 4_000L
    }

    val darkTheme = prefRepository.darkTheme.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = prefRepository.initialDarkTheme()
    )

    val dynamicTheme = prefRepository.dynamicTheme.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = prefRepository.initialDynamicTheme()
    )

    val cuteTheme = prefRepository.cuteTheme.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = prefRepository.initialCuteTheme()
    )

    val cardAmount = prefRepository.cardAmount.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = 20
    )

    val reviewAmount = prefRepository.reviewAmount.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = 1
    )

    val height = prefRepository.height.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = 200
    )

    val width = prefRepository.width.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = Int.MIN_VALUE
    )

    fun saveDynamicTheme() = scope.launch {
        prefRepository.saveDynamicTheme(!dynamicTheme.value)
    }

    fun saveDarkTheme() = scope.launch { prefRepository.saveDarkTheme(!darkTheme.value) }

    fun saveCuteTheme(value: Boolean = !cuteTheme.value) = scope.launch {
        prefRepository.saveCuteTheme(value)
    }

    fun saveCardAmount(amount: Int) = scope.launch {
        prefRepository.saveCardAmount(amount)
    }

    fun saveReviewAmount(amount: Int) = scope.launch {
        prefRepository.saveReviewAmount(amount)
    }

    fun saveKatexMenuHeight(h: Int) = scope.launch {
        prefRepository.saveKatexMenuHeight(h)
    }


    fun saveKatexMenuWidth(w: Int) = scope.launch {
        prefRepository.saveKatexMenuWidth(w)
    }

    fun setDarkTheme(isDarkTheme: Boolean) = scope.launch {
        prefRepository.setDarkTheme(isDarkTheme)
    }

    suspend fun getIsFirstTime(): Boolean = prefRepository.getIsFirstTime()
    fun setIsFirstTime() = scope.launch { prefRepository.setIsFirstTime() }

}

@Parcelize
data class PreferenceValues(
    val darkTheme: Boolean, val cuteTheme: Boolean, val dynamicTheme: Boolean,
    val cardAmount: Int, val reviewAmount: Int, val height: Int, val width: Int
) : Parcelable

@Composable
fun setPreferenceValues(preferencesManager: PreferencesManager): PreferenceValues {
    val dt by preferencesManager.darkTheme.collectAsState()
    val ct by preferencesManager.cuteTheme.collectAsState()
    val dynamicTheme by preferencesManager.dynamicTheme.collectAsState()
    val ca by preferencesManager.cardAmount.collectAsStateWithLifecycle()
    val ra by preferencesManager.reviewAmount.collectAsStateWithLifecycle()
    val height by preferencesManager.height.collectAsStateWithLifecycle()
    val width by preferencesManager.width.collectAsStateWithLifecycle()
    return PreferenceValues(
        darkTheme = dt, cuteTheme = ct, dynamicTheme = dynamicTheme,
        cardAmount = ca, reviewAmount = ra, height = height, width = width
    )
}