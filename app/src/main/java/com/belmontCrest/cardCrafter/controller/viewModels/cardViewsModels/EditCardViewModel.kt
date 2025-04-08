package com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.CardTypeRepository
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.ScienceSpecificRepository
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.localDatabase.tables.ListStringConverter
import com.belmontCrest.cardCrafter.model.uiModels.Fields
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditCardViewModel(
    private val cardTypeRepository: CardTypeRepository,
    private val sSRepository: ScienceSpecificRepository
) : ViewModel() {

    private val listStringConverter = ListStringConverter()
    private val privateErrMessage = MutableStateFlow("")
    val errorMessage = privateErrMessage.asStateFlow()
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

    fun updateNotationCard(
        cardId: Int,
        question: String,
        steps: List<String>,
        answer: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val stepsToString = listStringConverter.listToString(steps)
            sSRepository.updateNotationCard(
                question, stepsToString, answer, cardId
            )
        }
    }

    suspend fun updateCardType(
        cardId: Int, type: String, fields: Fields,
        deleteCT: CT
    ) {
        viewModelScope.launch {
            cardTypeRepository.updateCT(cardId, type, fields, deleteCT)
        }.join()
    }

    fun setErrorMessage(message: String) {
        privateErrMessage.update { message }
    }

    fun clearErrorMessage() {
        privateErrMessage.update { "" }
    }
}

