package com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belmontCrest.cardCrafter.model.repositories.CardTypeRepository
import com.belmontCrest.cardCrafter.model.repositories.FlashCardRepository
import com.belmontCrest.cardCrafter.model.repositories.ScienceSpecificRepository
import com.belmontCrest.cardCrafter.model.tablesAndApplication.BasicCard
import com.belmontCrest.cardCrafter.model.tablesAndApplication.Card
import com.belmontCrest.cardCrafter.model.tablesAndApplication.Deck
import com.belmontCrest.cardCrafter.model.tablesAndApplication.HintCard
import com.belmontCrest.cardCrafter.model.tablesAndApplication.NotationCard
import com.belmontCrest.cardCrafter.model.tablesAndApplication.MultiChoiceCard
import com.belmontCrest.cardCrafter.model.tablesAndApplication.ThreeFieldCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.util.Date

class AddCardViewModel(
    private val flashCardRepository: FlashCardRepository,
    private val cardTypeRepository: CardTypeRepository,
    private val scienceSpecificRepository: ScienceSpecificRepository
) : ViewModel() {
    private val privateErrorMessage: MutableStateFlow<String> = MutableStateFlow("")
    val errorMessage = privateErrorMessage.asStateFlow()
    companion object {
        private const val TIMEOUT_MILLIS = 4_000L
    }
    fun addBasicCard(deck: Deck, question: String, answer: String) {
        if (question.isNotBlank() && answer.isNotBlank()) {
            viewModelScope.launch(Dispatchers.IO) {
                val cardId = createCard("basic", deck)
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
        if (question.isNotBlank() && answer.isNotBlank()
            && hint.isNotBlank()
        ) {
            viewModelScope.launch(Dispatchers.IO) {
                val cardId = createCard("hint", deck)
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
        if (question.isNotBlank() && answer.isNotBlank()
            && middle.isNotBlank()
        ) {
            viewModelScope.launch(Dispatchers.IO) {
                val cardId = createCard("three", deck)
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
        choiceA: String,
        choiceB: String,
        choiceC: String,
        choiceD: String,
        correct: Char
    ) {
        if (question.isNotBlank() && choiceA.isNotBlank() &&
            choiceB.isNotBlank() && correct in 'a'..'d'
        ) {
            viewModelScope.launch(Dispatchers.IO) {
                val cardId = createCard("multi", deck)
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

    fun addNotationCard(
        deck: Deck,
        question: String,
        steps : List<String>,
        answer: String
    ){
        if (question.isNotBlank() &&
            steps.all { it.isNotBlank() } && answer.isNotBlank()) {
            viewModelScope.launch(Dispatchers.IO) {
                val cardId = createCard("notation", deck)
                scienceSpecificRepository.insertNotationCard(
                    NotationCard(
                        cardId = cardId.toInt(),
                        question = question,
                        steps = steps,
                        answer = answer
                    )
                )
            }
        }
    }


    suspend fun createCard(type: String, deck: Deck) : Long {
        val currentMax = flashCardRepository.getMaxDCNumber(deck.uuid) ?: 0
        val newDeckCardNumber = currentMax + 1
        return flashCardRepository.insertCard(
            Card(
                deckId = deck.id,
                nextReview = Date(),
                passes = 0,
                prevSuccess = false,
                totalPasses = 0,
                type = type,
                deckUUID = deck.uuid,
                deckCardNumber = newDeckCardNumber,
                cardIdentifier = "${deck.uuid}-$newDeckCardNumber",
                reviewsLeft = deck.reviewAmount
            )
        ).also {
            updateCardsLeft(deck)
        }
    }


    suspend fun updateCardsLeft(deck: Deck, cardsToAdd: Int = 1) {
        return withContext(Dispatchers.IO) {
            /** Only add the cards if the deck's review is due */
            if (deck.nextReview <= Date()) {
                /** Make sure the cardsLeft + cardsAdded don't
                 * exceed the deck's cardAmount
                 */
                viewModelScope.launch(Dispatchers.IO) {
                    withTimeout(TIMEOUT_MILLIS) {
                        if ((deck.cardsLeft + cardsToAdd) < deck.cardAmount) {
                            flashCardRepository.updateCardsLeft(
                                deck.id,
                                (deck.cardsLeft + cardsToAdd)
                            )
                        } else {
                            flashCardRepository.updateCardsLeft(deck.id, deck.cardAmount)
                        }
                    }
                }
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