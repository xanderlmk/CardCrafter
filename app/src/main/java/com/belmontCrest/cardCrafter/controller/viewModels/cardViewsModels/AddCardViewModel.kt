package com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.FlashCardRepository
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.model.uiModels.SelectedKeyboard
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.authRepo.IsOwnerOrCoOwnerRepo
import com.belmontCrest.cardCrafter.views.miscFunctions.details.CDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.util.Date

class AddCardViewModel(
    private val flashCardRepository: FlashCardRepository,
    private val isOwnerOrCoOwnerRepo: IsOwnerOrCoOwnerRepo,
    private val deckUUID: String
) : ViewModel() {
    private val _errorMessage: MutableStateFlow<String> = MutableStateFlow("")
    val errorMessage = _errorMessage.asStateFlow()
    private val isOwner = MutableStateFlow(false)
    private val _showKatexKeyboard = MutableStateFlow(false)
    val showKatexKeyboard = _showKatexKeyboard.asStateFlow()
    private val _selectedKB : MutableStateFlow<SelectedKeyboard?> = MutableStateFlow(null)
    val selectedKB = _selectedKB.asStateFlow()

    companion object {
        private const val TIMEOUT_MILLIS = 4_000L
    }

    init {
        viewModelScope.launch {
            isOwner.update { isOwnerOrCoOwnerRepo.isCoOwnerOrCoOwner(deckUUID) }
        }
    }

    fun addBasicCard(deck: Deck, question: String, answer: String) {
        if (question.isNotBlank() && answer.isNotBlank()) {
            println("Is Owner: ${isOwner.value}")
            viewModelScope.launch(Dispatchers.IO) {
                flashCardRepository.insertBasicCard(
                    deck, CDetails.BasicCD(question, answer), isOwner.value
                ).also { updateCardsLeft(deck) }
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
                flashCardRepository.insertHintCard(
                    deck, CDetails.ThreeHintCD(question, hint, answer), isOwner.value
                ).also { updateCardsLeft(deck) }
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
                flashCardRepository.insertThreeCard(
                    deck, CDetails.ThreeHintCD(question, middle, answer), isOwner.value
                ).also {
                    updateCardsLeft(deck)
                }
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
                flashCardRepository.insertMultiCard(
                    deck, CDetails.MultiCD(question, choiceA, choiceB, choiceC, choiceD, correct),
                    isOwner.value
                ).also { updateCardsLeft(deck) }
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
                flashCardRepository.insertNotationCard(
                    deck, CDetails.NotationCD(question, steps, answer), isOwner.value
                ).also { updateCardsLeft(deck) }
            }
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

    fun updateSelectedKB(selectedKeyboard: SelectedKeyboard) {
            _selectedKB.update { selectedKeyboard }
    }

    fun resetSelectedKB() {
        _selectedKB.update { null }
    }

    fun toggleKeyboard() {
        _showKatexKeyboard.update { !it }
    }

    fun setErrorMessage(message: String) {
        _errorMessage.update { message }
    }

    fun clearErrorMessage() {
        _errorMessage.update { "" }
    }
}