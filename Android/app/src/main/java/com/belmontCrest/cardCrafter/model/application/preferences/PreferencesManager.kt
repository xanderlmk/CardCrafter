package com.belmontCrest.cardCrafter.model.application.preferences

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.belmontCrest.cardCrafter.model.ui.CCTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

class PreferencesManager(
    private val prefRepository: PrefRepository, private val scope: CoroutineScope
) {
    companion object {
        private const val TIMEOUT_MILLIS = 4_000L
    }

    private val theme = prefRepository.theme.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = CCTheme.Default
    )

    private val cardAmount = prefRepository.cardAmount.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = 20
    )

    private val reviewAmount = prefRepository.reviewAmount.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = 1
    )

    private val height = prefRepository.height.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = 200
    )

    private val width = prefRepository.width.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = Int.MIN_VALUE
    )

    val pv = combine(theme, cardAmount, reviewAmount, height, width) { t, ca, ra, h, w ->
        PreferenceValues(t, ca, reviewAmount = ra, height = h, width = w)
    }.stateIn(
        scope = scope, started = SharingStarted.Lazily, initialValue = PreferenceValues()
    )


    fun saveTheme(new: CCTheme) = scope.launch { prefRepository.saveTheme(new) }

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
}

@Parcelize
data class PreferenceValues(
    val theme: CCTheme = CCTheme.Default, val cardAmount: Int = Int.MIN_VALUE,
    val reviewAmount: Int = Int.MIN_VALUE,
    val height: Int = 200, val width: Int = 200
) : Parcelable
