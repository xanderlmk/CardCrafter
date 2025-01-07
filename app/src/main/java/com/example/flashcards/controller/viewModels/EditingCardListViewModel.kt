package com.example.flashcards.controller.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.model.uiModels.CardListUiState
import com.example.flashcards.model.repositories.CardTypeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class EditingCardListViewModel(
    private val cardTypeRepository: CardTypeRepository
) : ViewModel() {

    private val uiState = MutableStateFlow(CardListUiState())

    var cardListUiState = uiState.asStateFlow()

    fun getAllTypesForDeck(deckId: Int) {
        viewModelScope.launch {
            cardTypeRepository.getAllCardTypes(deckId).map { allCards ->
                CardListUiState(allCards = allCards)
            }.collect { state ->
                uiState.value = state
            }
            clearErrorMessage()
        }
    }

    fun setErrorMessage(message: String) {
        uiState.value = uiState.value.copy(errorMessage = message)
    }

    fun clearErrorMessage() {
        uiState.value = uiState.value.copy(errorMessage = "")
    }
}



