package com.example.flashcards.controller.viewModels.cardViewsModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.model.repositories.CardTypeRepository
import com.example.flashcards.model.repositories.FlashCardRepository
import com.example.flashcards.model.tablesAndApplication.BasicCard
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.model.tablesAndApplication.HintCard
import com.example.flashcards.model.tablesAndApplication.MultiChoiceCard
import com.example.flashcards.model.tablesAndApplication.ThreeFieldCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class AddCardViewModel(
    private val flashCardRepository: FlashCardRepository,
    private val cardTypeRepository: CardTypeRepository
) : ViewModel() {
    private val privateErrorMessage : MutableStateFlow<String> = MutableStateFlow("")
    val errorMessage = privateErrorMessage.asStateFlow()
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
            }
        }
    }
    fun addHintCard(
        deck: Deck, question: String,
        hint: String, answer: String
    ) {
        if (question.isNotEmpty() && answer.isNotEmpty()
            && hint.isNotEmpty()
        ) {
            viewModelScope.launch {
                val cardId = flashCardRepository.insertCard(
                    Card(
                        deckId = deck.id,
                        nextReview = Date(),
                        passes = 0,
                        prevSuccess = false,
                        totalPasses = 0,
                        type = "hint",
                        deckUUID = deck.uuid,
                        reviewsLeft = deck.reviewAmount
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
            }
        }
    }

    fun addThreeCard(
        deck: Deck, question: String,
        middle: String, answer: String
    ) {
        if (question.isNotEmpty() && answer.isNotEmpty()
            && middle.isNotEmpty()
        ) {
            viewModelScope.launch {
                val cardId = flashCardRepository.insertCard(
                    Card(
                        deckId = deck.id,
                        nextReview = Date(),
                        passes = 0,
                        prevSuccess = false,
                        totalPasses = 0,
                        type = "three",
                        deckUUID = deck.uuid,
                        reviewsLeft = deck.reviewAmount
                    )
                )
                cardTypeRepository.insertThreeCard(
                    ThreeFieldCard(
                        cardId = cardId.toInt(),
                        question = question,
                        middle = middle,
                        answer = answer
                    )
                )
            }
        }
    }

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
            }
        }
    }

    fun setErrorMessage(message: String) {
        privateErrorMessage.value = message
    }

    fun clearErrorMessage() {
        privateErrorMessage.value = ""
    }
}