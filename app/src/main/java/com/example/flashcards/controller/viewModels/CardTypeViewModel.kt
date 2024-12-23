package com.example.flashcards.controller.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.model.CardListUiState
import com.example.flashcards.model.repositories.CardTypeRepository
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class CardTypeViewModel(private val cardTypeRepository: CardTypeRepository) : ViewModel() {

    private val uiState = MutableStateFlow(CardListUiState())
    var cardListUiState : StateFlow<CardListUiState> = uiState.asStateFlow()

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
                    }.collect { state ->
                        uiState.value = state
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



