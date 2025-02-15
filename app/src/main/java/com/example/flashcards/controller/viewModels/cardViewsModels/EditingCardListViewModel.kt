package com.example.flashcards.controller.viewModels.cardViewsModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.controller.cardHandlers.mapAllCardTypesToCTs
import com.example.flashcards.model.uiModels.CardListUiState
import com.example.flashcards.model.repositories.CardTypeRepository
import com.example.flashcards.model.tablesAndApplication.AllCardTypes
import com.example.flashcards.model.uiModels.SealedAllCTs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditingCardListViewModel(
    private val cardTypeRepository: CardTypeRepository
) : ViewModel() {
    private val sealedUiState = MutableStateFlow(SealedAllCTs())
    var sealedAllCTs = sealedUiState.asStateFlow()

    suspend fun getAllCardsForDeck(
        deckId: Int,
    ) {
        return withContext(Dispatchers.IO) {
            viewModelScope.launch {
                cardTypeRepository.getAllCardTypes(deckId).map { allCards ->
                    CardListUiState(allCards = allCards)
                }.collect { state ->
                    sealedUiState.update {
                        updateSealedUiState(state.allCards)
                    }
                }
                clearErrorMessage()
            }
        }
    }

    private fun updateSealedUiState(
        allCards: List<AllCardTypes>
    ): SealedAllCTs {
        var allCTs = try {
            mapAllCardTypesToCTs(allCards)
        } catch (e: IllegalStateException) {
            Log.e(
                "GetDueTypesForDeck",
                "Invalid AllCardType data: ${e.message}"
            )
            return SealedAllCTs()
        }
        return SealedAllCTs(
            allCTs = allCTs.toMutableList()
        )
    }

    fun setErrorMessage(message: String) {
        sealedUiState.value = sealedUiState.value.copy(errorMessage = message)
    }

    private fun clearErrorMessage() {
       sealedUiState.value = sealedUiState.value.copy(errorMessage = "")
    }
}



