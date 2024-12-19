package com.example.flashcards.controller.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.model.ThreeCardUiState
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.model.repositories.CardTypeRepository
import com.example.flashcards.model.repositories.FlashCardRepository
import com.example.flashcards.model.tablesAndApplication.ThreeCardType
import com.example.flashcards.model.tablesAndApplication.ThreeFieldCard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class ThreeCardViewModel(
    private val flashCardRepository: FlashCardRepository,
    private val cardTypeRepository: CardTypeRepository
) : ViewModel() {
    var threeCardUiState = MutableStateFlow(ThreeCardUiState())

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

    fun getThreeCard(cardId: Int): Flow<ThreeCardType> {
        return cardTypeRepository.getThreeCard(cardId)
    }

    fun setErrorMessage(message: String) {
        threeCardUiState.value = threeCardUiState.value.copy(errorMessage = message)
    }

    fun clearErrorMessage() {
        threeCardUiState.value = threeCardUiState.value.copy(errorMessage = "")
    }

}