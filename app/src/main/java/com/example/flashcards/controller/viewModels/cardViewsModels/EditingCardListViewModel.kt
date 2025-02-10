package com.example.flashcards.controller.viewModels.cardViewsModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.controller.cardHandlers.mapAllCardTypesToCTs
import com.example.flashcards.model.uiModels.CardListUiState
import com.example.flashcards.model.repositories.CardTypeRepository
import com.example.flashcards.model.tablesAndApplication.AllCardTypes
import com.example.flashcards.model.uiModels.BasicCardUiState
import com.example.flashcards.model.uiModels.HintCardUiState
import com.example.flashcards.model.uiModels.MultiChoiceUiCardState
import com.example.flashcards.model.uiModels.SealedAllCTs
import com.example.flashcards.model.uiModels.ThreeCardUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    private val basicUiState = MutableStateFlow(BasicCardUiState())
    var basicCardUiState = basicUiState.asStateFlow()
    private val hintUiState = MutableStateFlow(HintCardUiState())
    val hintCardUiState: StateFlow<HintCardUiState> = hintUiState.asStateFlow()
    private val threeUiState = MutableStateFlow(ThreeCardUiState())
    val threeCardUiState: StateFlow<ThreeCardUiState> = threeUiState.asStateFlow()
    private val choiceUiState = MutableStateFlow(MultiChoiceUiCardState())
    var multiChoiceUiState = choiceUiState.asStateFlow()
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
            viewModelScope.launch {
                getAllChoicesForDeck(deckId)
                getAllBasicsForDeck(deckId)
                getAllHintsForDeck(deckId)
                getAllThreeForDeck(deckId)
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

    fun getAllBasicsForDeck(deckId: Int) {
        viewModelScope.launch {
            cardTypeRepository.getAllBasicCards(deckId).map { allCards ->
                BasicCardUiState(basicCards = allCards)
            }.collect { state ->
                basicUiState.value = state
            }
            clearErrorMessage()
        }
    }

    fun getAllHintsForDeck(deckId: Int) {
        viewModelScope.launch {
            cardTypeRepository.getAllHintCards(deckId).map { allCards ->
                HintCardUiState(hintCards = allCards)
            }.collect { state ->
                hintUiState.value = state
            }
            clearErrorMessage()
        }
    }

    fun getAllThreeForDeck(deckId: Int) {
        viewModelScope.launch {
            cardTypeRepository.getAllThreeCards(deckId).map { allCards ->
                ThreeCardUiState(threeFieldCards = allCards)
            }.collect { state ->
                threeUiState.value = state
            }
            clearErrorMessage()
        }
    }

    fun getAllChoicesForDeck(deckId: Int) {
        viewModelScope.launch {
            cardTypeRepository.getAllMultiChoiceCards(deckId).map { allCards ->
                MultiChoiceUiCardState(multiChoiceCard = allCards)
            }.collect { state ->
                choiceUiState.value = state
            }
            clearErrorMessage()
        }
    }

    fun setErrorMessage(message: String) {
        sealedUiState.value = sealedUiState.value.copy(errorMessage = message)
    }

    private fun clearErrorMessage() {
       sealedUiState.value = sealedUiState.value.copy(errorMessage = "")
    }
}



