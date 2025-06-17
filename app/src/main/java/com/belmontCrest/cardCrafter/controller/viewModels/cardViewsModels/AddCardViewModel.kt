package com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belmontCrest.cardCrafter.controller.viewModels.ReusedFunc
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.FlashCardRepository
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.localDatabase.tables.PartOfQorA
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.authRepo.IsOwnerOrCoOwnerRepo
import com.belmontCrest.cardCrafter.views.miscFunctions.details.CDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddCardViewModel(
    private val flashCardRepository: FlashCardRepository,
    private val isOwnerOrCoOwnerRepo: IsOwnerOrCoOwnerRepo,
    private val deckUUID: String
) : ViewModel() {
    private val _errorMessage: MutableStateFlow<String> = MutableStateFlow("")
    val errorMessage = _errorMessage.asStateFlow()
    private val isOwner = MutableStateFlow(false)
    private val rf = ReusedFunc(flashCardRepository)

    /*companion object {
        private const val TIMEOUT_MILLIS = 4_000L
    }*/

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
                    deck, CDetails.HintCD(question, hint, answer), isOwner.value
                ).also { updateCardsLeft(deck) }
            }
        }
    }

    fun addThreeCard(
        deck: Deck, question: String,
        middle: String, answer: String, isQOrA: PartOfQorA
    ) {
        if (question.isNotBlank() && answer.isNotBlank()
            && middle.isNotBlank()
        ) {
            viewModelScope.launch(Dispatchers.IO) {
                flashCardRepository.insertThreeCard(
                    deck, CDetails.ThreeCD(question, middle, answer, isQOrA), isOwner.value
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
        rf.updateCardsLeft(deck, cardsToAdd)
    }

    fun setErrorMessage(message: String) {
        _errorMessage.update { message }
    }

    fun clearErrorMessage() {
        _errorMessage.update { "" }
    }
}