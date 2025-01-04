package com.example.flashcards.controller.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.model.repositories.CardTypeRepository
import com.example.flashcards.model.repositories.FlashCardRepository
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.model.tablesAndApplication.MultiChoiceCard
import com.example.flashcards.model.uiModels.MultiChoiceUiCardState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Date

class MultiChoiceCardViewModel(
    private val flashCardRepository: FlashCardRepository,
    private val cardTypeRepository: CardTypeRepository
) : ViewModel() {

    private val uiState = MutableStateFlow(MultiChoiceUiCardState())
    var multiChoiceUiState = uiState.asStateFlow()

    fun addMultiChoiceCard(
        deck: Deck,
        question: String,
        choiceA : String,
        choiceB : String,
        choiceC : String,
        choiceD : String,
        correct : Char
    ) {
        if (question.isNotEmpty() && choiceA.isNotEmpty() &&
            choiceB.isNotEmpty() && correct in 'a'..'d') {
            viewModelScope.launch {
                val cardId = flashCardRepository.insertCard(
                    Card(
                        deckId = deck.id,
                        nextReview = Date(),
                        passes = 0,
                        prevSuccess = false,
                        totalPasses = 0,
                        type = "multi",
                        deckUUID = deck.uuid,
                        reviewsLeft = deck.reviewAmount
                    )
                )
                cardTypeRepository.insertMultiChoiceCard(
                    MultiChoiceCard(
                        cardId = cardId.toInt(),
                        question = question,
                        choiceA = choiceA,
                        choiceB = choiceB,
                        choiceC = choiceC,
                        choiceD = choiceD,
                        correct = correct
                    )
                )
                clearErrorMessage()
            }
        }
    }

    fun updateMultiChoiceCard(
        cardId: Int,
        question: String,
        choiceA : String,
        choiceB : String,
        choiceC : String,
        choiceD : String,
        correct : Char
    ) {
        viewModelScope.launch {
            cardTypeRepository.updateMultiChoiceCard(
                cardId, question, choiceA,
                choiceB, choiceC, choiceD , correct
            )
        }
    }

    fun getAllChoicesForDeck(deckId: Int) {
        viewModelScope.launch {
            cardTypeRepository.getAllMultiChoiceCards(deckId).map { allCards ->
                MultiChoiceUiCardState(multiChoiceCard = allCards)
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