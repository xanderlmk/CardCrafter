package com.example.flashcards.controller.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.model.HintCardUiState
import com.example.flashcards.model.repositories.CardTypeRepository
import com.example.flashcards.model.repositories.FlashCardRepository
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.model.tablesAndApplication.HintCard
import com.example.flashcards.model.tablesAndApplication.HintCardType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class HintCardViewModel(
    private val flashCardRepository: FlashCardRepository,
    private val cardTypeRepository: CardTypeRepository
) : ViewModel() {
    private val uiState = MutableStateFlow(HintCardUiState())
    val hintCardUiState : StateFlow<HintCardUiState> = uiState.asStateFlow()
    fun addHintCard(
        deckId: Int, question: String,
        hint: String, answer: String
    ) {
        if (question.isNotEmpty() && answer.isNotEmpty()
            && hint.isNotEmpty()
        ) {
            viewModelScope.launch {
                val cardId = flashCardRepository.insertCard(
                    Card(
                        deckId = deckId,
                        nextReview = Date(),
                        passes = 0,
                        prevSuccess = false,
                        totalPasses = 0,
                        type = "hint"
                    )
                )
                cardTypeRepository.insertHintCard(
                    HintCard(
                        cardId = cardId.toInt(),
                        question = question,
                        hint = hint,
                        answer = answer
                    )
                )
                clearErrorMessage()
            }
        }
    }

    fun updateHintCard(cardId: Int, question: String, hint: String, answer: String) {
        viewModelScope.launch {
            cardTypeRepository.updateHintCard(cardId, question, hint, answer)
        }
    }

    fun getHintCard(cardId: Int): Flow<HintCardType> {
        return cardTypeRepository.getHintCard(cardId)
    }

    fun setErrorMessage(message: String) {
        uiState.value = uiState.value.copy(errorMessage = message)
    }

    fun clearErrorMessage() {
        uiState.value = uiState.value.copy(errorMessage = "")
    }
}

