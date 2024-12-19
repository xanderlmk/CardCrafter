package com.example.flashcards.controller.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.model.CardListUiState
import com.example.flashcards.model.repositories.CardTypeRepository
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class CardTypeViewModel(private val cardTypeRepository: CardTypeRepository) : ViewModel() {

    var cardListUiState = MutableStateFlow(CardListUiState())

    companion object {
        private const val TIMEOUT_MILLIS = 4_000L
    }
    private val errorMessage = MutableStateFlow<String?>(null)

    fun getDueTypesForDeck(deckId: Int) {
        try {
            viewModelScope.launch {
                withTimeout(TIMEOUT_MILLIS) {
                    cardTypeRepository.getDueAllCardTypes(deckId).map { allCards ->
                        CardListUiState(allCards = allCards)
                    }.collect { uiState ->
                        cardListUiState.value = uiState
                    }
                }
            }
        } catch (e : TimeoutCancellationException){
                errorMessage.value = "Request timed out. Please try again."
                println(e)
        }
    }

    fun getAllTypesForDeck(deckId: Int) {
        viewModelScope.launch {
            cardTypeRepository.getAllCardTypes(deckId).map { allCards ->
                CardListUiState(allCards = allCards)
            }.collect { uiState ->
                cardListUiState.value = uiState
            }
            clearErrorMessage()
        }
    }

    fun setErrorMessage(message: String) {
        cardListUiState.value = cardListUiState.value.copy(errorMessage = message)
    }

    fun clearErrorMessage() {
        cardListUiState.value = cardListUiState.value.copy(errorMessage = "")
    }
}



