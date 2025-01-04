package com.example.flashcards.controller.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.model.uiModels.BasicCardUiState
import com.example.flashcards.model.tablesAndApplication.BasicCard
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.model.repositories.CardTypeRepository
import com.example.flashcards.model.repositories.FlashCardRepository
import com.example.flashcards.model.tablesAndApplication.Deck
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Date

class BasicCardViewModel(
    private val flashCardRepository: FlashCardRepository,
    private val cardTypeRepository: CardTypeRepository
) : ViewModel() {

    private val uiState = MutableStateFlow(BasicCardUiState())
    var basicCardUiState = uiState.asStateFlow()

    fun addBasicCard(deck: Deck, question: String, answer: String) {
        if (question.isNotEmpty() && answer.isNotEmpty()) {
            viewModelScope.launch {
                val cardId = flashCardRepository.insertCard(
                    Card(
                        deckId = deck.id,
                        nextReview = Date(),
                        passes = 0,
                        prevSuccess = false,
                        totalPasses = 0,
                        type = "basic",
                        deckUUID = deck.uuid,
                        reviewsLeft = deck.reviewAmount
                    )
                )
                cardTypeRepository.insertBasicCard(
                    BasicCard(
                        cardId = cardId.toInt(),
                        question = question,
                        answer = answer
                    )
                )
                clearErrorMessage()
            }
        }
    }

    fun updateBasicCard(cardId: Int, question: String, answer: String) {
        viewModelScope.launch {
            cardTypeRepository.updateBasicCard(cardId, question, answer)
        }
    }

    fun getAllBasicsForDeck(deckId: Int) {
        viewModelScope.launch {
            cardTypeRepository.getAllBasicCards(deckId).map { allCards ->
                BasicCardUiState(basicCards = allCards)
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