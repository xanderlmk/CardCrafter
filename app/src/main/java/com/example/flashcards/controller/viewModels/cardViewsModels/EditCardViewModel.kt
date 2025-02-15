package com.example.flashcards.controller.viewModels.cardViewsModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.model.repositories.CardTypeRepository
import com.example.flashcards.model.repositories.FlashCardRepository
import com.example.flashcards.model.repositories.ScienceSpecificRepository
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.model.tablesAndApplication.MathCardConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditCardViewModel(
    private val flashCardRepository: FlashCardRepository,
    private val cardTypeRepository: CardTypeRepository,
    private val scienceSpecificRepository: ScienceSpecificRepository
) : ViewModel() {

    private val mathCardConverter = MathCardConverter()
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

    fun updateMathCard(
        cardId: Int,
        question: String,
        steps : List<String>,
        answer: String
    ) {
        viewModelScope.launch(Dispatchers.IO){
            val stepsToString = mathCardConverter.listToString(steps)
            scienceSpecificRepository.updateMathCard(
                question, stepsToString ,answer, cardId
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

