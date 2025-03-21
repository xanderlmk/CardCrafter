package com.example.flashcards.controller.viewModels.cardViewsModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.model.repositories.CardTypeRepository
import com.example.flashcards.model.repositories.FlashCardRepository
import com.example.flashcards.model.repositories.ScienceSpecificRepository
import com.example.flashcards.model.tablesAndApplication.BasicCard
import com.example.flashcards.model.tablesAndApplication.CT
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.model.tablesAndApplication.HintCard
import com.example.flashcards.model.tablesAndApplication.NotationCard
import com.example.flashcards.model.tablesAndApplication.ListStringConverter
import com.example.flashcards.model.tablesAndApplication.MultiChoiceCard
import com.example.flashcards.model.tablesAndApplication.ThreeFieldCard
import com.example.flashcards.model.uiModels.Fields
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditCardViewModel(
    private val flashCardRepository: FlashCardRepository,
    private val cardTypeRepository: CardTypeRepository,
    private val sSRepository: ScienceSpecificRepository
) : ViewModel() {

    private val listStringConverter = ListStringConverter()
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

    fun updateNotationCard(
        cardId: Int,
        question: String,
        steps : List<String>,
        answer: String
    ) {
        viewModelScope.launch(Dispatchers.IO){
            val stepsToString = listStringConverter.listToString(steps)
            sSRepository.updateNotationCard(
                question, stepsToString ,answer, cardId
            )
        }
    }
    suspend fun updateCardType(
        cardId: Int, type: String, fields: Fields,
        deleteCT : CT
    ) {
        viewModelScope.launch {
            when (type){
                "basic" -> {
                    cardTypeRepository.insertBasicCard(
                        BasicCard(
                            cardId =  cardId,
                            question = fields.question.value,
                            answer = fields.answer.value
                        )
                    )
                }
                "three" -> {
                    cardTypeRepository.insertThreeCard(
                        ThreeFieldCard(
                            cardId = cardId,
                            question = fields.question.value,
                            middle = fields.middleField.value,
                            answer = fields.answer.value
                        )
                    )

                }
                "hint" -> {
                    cardTypeRepository.insertHintCard(
                        HintCard(
                            cardId = cardId,
                            question = fields.question.value,
                            hint = fields.middleField.value,
                            answer = fields.answer.value
                        )
                    )
                }
                "multi" -> {
                    cardTypeRepository.insertMultiChoiceCard(
                        MultiChoiceCard(
                            cardId = cardId,
                            question = fields.question.value,
                            choiceA = fields.choices[0].value,
                            choiceB = fields.choices[1].value,
                            choiceC = fields.choices[2].value,
                            choiceD = fields.choices[3].value,
                            correct = fields.correct.value
                        )
                    )
                }
                "notation" -> {
                    sSRepository.insertNotationCard(
                        NotationCard(
                            cardId = cardId,
                            question = fields.question.value,
                            steps = fields.stringList.map { it.value },
                            answer = fields.answer.value
                        )
                    )
                }
                else -> {
                    throw IllegalStateException("Can't update card type")
                }
            }.also {
                flashCardRepository.updateCardType(cardId, type)
            }
            when (deleteCT) {
                is CT.Basic -> {
                    cardTypeRepository.deleteBasicCard(deleteCT.basicCard)
                }
                is CT.ThreeField -> {
                    cardTypeRepository.deleteThreeCard(deleteCT.threeFieldCard)
                }
                is CT.Hint -> {
                    cardTypeRepository.deleteHintCard(deleteCT.hintCard)
                }
                is CT.MultiChoice -> {
                    cardTypeRepository.deleteMultiChoiceCard(deleteCT.multiChoiceCard)
                }
                is CT.Notation -> {
                    sSRepository.deleteNotationCard(deleteCT.notationCard)
                }
            }
        }.join()
    }
}

