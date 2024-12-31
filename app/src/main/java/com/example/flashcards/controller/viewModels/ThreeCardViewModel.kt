package com.example.flashcards.controller.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.model.uiModels.ThreeCardUiState
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.model.repositories.CardTypeRepository
import com.example.flashcards.model.repositories.FlashCardRepository
import com.example.flashcards.model.tablesAndApplication.ThreeFieldCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Date

class ThreeCardViewModel(
    private val flashCardRepository: FlashCardRepository,
    private val cardTypeRepository: CardTypeRepository
) : ViewModel() {
    private val uiState = MutableStateFlow(ThreeCardUiState())
    val threeCardUiState: StateFlow<ThreeCardUiState> = uiState.asStateFlow()

    fun addThreeCard(
        deckId: Int, question: String,
        middle: String, answer: String
    ) {
        if (question.isNotEmpty() && answer.isNotEmpty()
            && middle.isNotEmpty()
        ) {
            viewModelScope.launch {
                val cardId = flashCardRepository.insertCard(
                    Card(
                        deckId = deckId,
                        nextReview = Date(),
                        passes = 0,
                        prevSuccess = false,
                        totalPasses = 0,
                        type = "three"
                    )
                )
                println(cardId.toInt())
                cardTypeRepository.insertThreeCard(
                    ThreeFieldCard(
                        cardId = cardId.toInt(),
                        question = question,
                        middle = middle,
                        answer = answer
                    )
                )
                clearErrorMessage()
            }
        }
    }

    fun updateThreeCard(cardId: Int, question: String, middle: String, answer: String) {
        viewModelScope.launch {
            cardTypeRepository.updateThreeCard(cardId, question, middle, answer)
        }
    }

    fun getAllThreeForDeck(deckId: Int) {
        viewModelScope.launch {
            cardTypeRepository.getAllThreeCards(deckId).map { allCards ->
                ThreeCardUiState(threeFieldCards = allCards)
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