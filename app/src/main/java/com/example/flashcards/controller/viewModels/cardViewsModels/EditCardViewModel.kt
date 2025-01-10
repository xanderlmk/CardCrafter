package com.example.flashcards.controller.viewModels.cardViewsModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.model.repositories.CardTypeRepository
import com.example.flashcards.model.repositories.FlashCardRepository
import com.example.flashcards.model.tablesAndApplication.Card
import kotlinx.coroutines.launch

class EditCardViewModel(
    private val flashCardRepository: FlashCardRepository,
    private val cardTypeRepository: CardTypeRepository,
) : ViewModel() {

    fun deleteCard(card: Card) {
        viewModelScope.launch {
            flashCardRepository.deleteCard(card)
        }
    }
    fun updateBasicCard(cardId: Int, question: String, answer: String) {
        viewModelScope.launch {
            cardTypeRepository.updateBasicCard(cardId, question, answer)
        }
    }
    fun updateHintCard(cardId: Int, question: String, hint: String, answer: String) {
        viewModelScope.launch {
            cardTypeRepository.updateHintCard(cardId, question, hint, answer)
        }
    }
    fun updateThreeCard(cardId: Int, question: String, middle: String, answer: String) {
        viewModelScope.launch {
            cardTypeRepository.updateThreeCard(cardId, question, middle, answer)
        }
    }
    fun updateMultiChoiceCard(
        cardId: Int,
        question: String,
        choiceA: String,
        choiceB: String,
        choiceC: String,
        choiceD: String,
        correct: Char
    ) {
        viewModelScope.launch {
            cardTypeRepository.updateMultiChoiceCard(
                cardId, question, choiceA,
                choiceB, choiceC, choiceD, correct
            )
        }
    }
    fun updateCardType(
        cardId: Int, type: String,
    ) {
        viewModelScope.launch {
            flashCardRepository.updateCardType(cardId, type)
        }
    }
}

