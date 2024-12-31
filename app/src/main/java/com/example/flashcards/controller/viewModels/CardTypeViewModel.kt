package com.example.flashcards.controller.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.model.uiModels.CardListUiState
import com.example.flashcards.model.repositories.CardTypeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

class CardTypeViewModel(private val cardTypeRepository: CardTypeRepository) : ViewModel() {

    private val uiState = MutableStateFlow(CardListUiState())
    var cardListUiState: StateFlow<CardListUiState> = uiState.asStateFlow()

    companion object {
        private const val TIMEOUT_MILLIS = 4_000L
    }

    private val errorMessage = MutableStateFlow<String?>(null)

    suspend fun getDueTypesForDeck(deckId: Int): Boolean {

        return withContext(Dispatchers.IO) {
            var complete = false
            try {
                viewModelScope.launch {
                    withTimeout(TIMEOUT_MILLIS) {
                        cardTypeRepository.getDueAllCardTypes(deckId).map { allCards ->
                            CardListUiState(allCards = allCards)
                        }.collect { state ->
                            uiState.value = state
                            complete = true
                        }
                    }
                }
                while (!complete) {
                    delay(20)
                }
                true
            } catch (e: TimeoutCancellationException) {
                errorMessage.value = "Request timed out. Please try again."
                println(e)
                complete
            }
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



