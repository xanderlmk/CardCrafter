package com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.CardTypeRepository
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.FlashCardRepository
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.ScienceSpecificRepository
import com.belmontCrest.cardCrafter.localDatabase.tables.BasicCard
import com.belmontCrest.cardCrafter.localDatabase.tables.Card
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.localDatabase.tables.HintCard
import com.belmontCrest.cardCrafter.localDatabase.tables.NotationCard
import com.belmontCrest.cardCrafter.localDatabase.tables.MultiChoiceCard
import com.belmontCrest.cardCrafter.localDatabase.tables.ThreeFieldCard
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
        steps: List<String>,
        answer: String
    ) {
        if (question.isNotBlank() &&
            steps.all { it.isNotBlank() } && answer.isNotBlank()
        ) {
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


    suspend fun createCard(type: String, deck: Deck): Long {
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
                viewModelScope.launch(Dispatchers.IO) {
                    withTimeout(TIMEOUT_MILLIS) {
                        /** This keeps a record of the amount of cards left vs done and compares
                         *  it to the card amount. Here's three examples
                         *  cardsLeft = CL, cardsDone = CD, cardAmount = CA
                         *  CA = 20
                         *  1. CL = 19, CD = 1
                         *      CL + 1 = 20, CD + 1 = 2, ! < CA, F; move down
                         *      CD + 1 = 2, ! >= CA, F; just update to CA
                         *
                         *  2. CL = 1, CD = 19
                         *      CL + 1 = 2, CD + 1 = 20, ! < CA, F; move down
                         *      CD + 1 = 20 >= CA, T, Don't update just return.
                         *
                         *  3. CL = 15, CD = 5
                         *      CL + 1 = 16, CD + 1 = 6, 16 && 6 < CA, T; Update accordingly.
                         */
                        if (((deck.cardsLeft + cardsToAdd) < deck.cardAmount) &&
                            ((deck.cardsDone + cardsToAdd) < deck.cardAmount)
                        ) {
                            flashCardRepository.updateCardsLeft(
                                deckId = deck.id, cardsDone = deck.cardsDone,
                                cardsLeft = (deck.cardsLeft + cardsToAdd),
                            )
                        } else if ((deck.cardsDone + cardsToAdd) >= deck.cardAmount) {
                            return@withTimeout
                        } else {
                            flashCardRepository.updateCardsLeft(
                                deck.id, deck.cardAmount, deck.cardsDone
                            )
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