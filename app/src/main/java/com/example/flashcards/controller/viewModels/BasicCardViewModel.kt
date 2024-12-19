package com.example.flashcards.controller.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.model.BasicCardUiState
import com.example.flashcards.model.tablesAndApplication.BasicCard
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.model.repositories.CardTypeRepository
import com.example.flashcards.model.repositories.FlashCardRepository
import com.example.flashcards.model.tablesAndApplication.BasicCardType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class BasicCardViewModel(
    private val flashCardRepository: FlashCardRepository,
    private val cardTypeRepository: CardTypeRepository
) : ViewModel() {

    var basicCardUiState = MutableStateFlow(BasicCardUiState())

    fun addBasicCard(deckId: Int, question: String, answer: String) {
        if (question.isNotEmpty() && answer.isNotEmpty()) {
            viewModelScope.launch {
                val cardId = flashCardRepository.insertCard(
                    Card(
                        deckId = deckId,
                        nextReview = Date(),
                        passes = 0,
                        prevSuccess = false,
                        totalPasses = 0,
                        type = "basic"
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

    fun getBasicCard(cardId: Int): Flow<BasicCardType> {
        return cardTypeRepository.getBasicCard(cardId)
    }

    fun setErrorMessage(message: String) {
        basicCardUiState.value = basicCardUiState.value.copy(errorMessage = message)
    }

    fun clearErrorMessage() {
        basicCardUiState.value = basicCardUiState.value.copy(errorMessage = "")
    }

}