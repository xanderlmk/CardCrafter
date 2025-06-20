package com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belmontCrest.cardCrafter.controller.viewModels.ReusedFunc
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.FlashCardRepository
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.localDatabase.tables.PartOfQorA
import com.belmontCrest.cardCrafter.model.ui.states.CDetails
import com.belmontCrest.cardCrafter.model.ui.states.MyTextRange
import com.belmontCrest.cardCrafter.views.miscFunctions.details.CDetails.BasicCD
import com.belmontCrest.cardCrafter.views.miscFunctions.details.CDetails.HintCD
import com.belmontCrest.cardCrafter.views.miscFunctions.details.CDetails.ThreeCD
import com.belmontCrest.cardCrafter.views.miscFunctions.details.CDetails.MultiCD
import com.belmontCrest.cardCrafter.views.miscFunctions.details.CDetails.NotationCD
import com.belmontCrest.cardCrafter.model.ui.states.SelectedKeyboard
import com.belmontCrest.cardCrafter.model.ui.states.addStep
import com.belmontCrest.cardCrafter.model.ui.states.removeStep
import com.belmontCrest.cardCrafter.model.ui.states.updateAnswer
import com.belmontCrest.cardCrafter.model.ui.states.updateChoices
import com.belmontCrest.cardCrafter.model.ui.states.updateCorrect
import com.belmontCrest.cardCrafter.model.ui.states.updateMiddle
import com.belmontCrest.cardCrafter.model.ui.states.updateQOrA
import com.belmontCrest.cardCrafter.model.ui.states.updateQuestion
import com.belmontCrest.cardCrafter.model.ui.states.updateStep
import com.belmontCrest.cardCrafter.navigation.KeyboardSelectionRepository
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.authRepo.IsOwnerOrCoOwnerRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class AddCardViewModel(
    private val flashCardRepository: FlashCardRepository,
    private val isOwnerOrCoOwnerRepo: IsOwnerOrCoOwnerRepo,
    private val kbRepository: KeyboardSelectionRepository,
    private val savedStateHandle: SavedStateHandle,
    private val deckUUID: String
) : ViewModel() {

    companion object {
        private const val SELECTED_KB = "selected_keyboard"
        private const val SHOW_KB = "show_kb"
        private const val CD = "a_card_details"
        private const val SELECTION = "a_selection"
        private const val COMPOSITION = "a_composition"
    }

    private val _fields = MutableStateFlow(
        savedStateHandle.get<String>(CD)?.let {
            Json.decodeFromString(CDetails.serializer(), it)
        } ?: CDetails())
    val fields = _fields.asStateFlow()

    private val _selection = MutableStateFlow(
        savedStateHandle.get<String>(SELECTION)?.let {
            Json.decodeFromString(MyTextRange.serializer(), it)
        } ?: MyTextRange()
    )
    val selection = _selection.asStateFlow()

    private val _composition = MutableStateFlow(
        savedStateHandle.get<String>(SELECTION)?.let {
            Json.decodeFromString(MyTextRange.serializer(), it)
        }
    )
    val composition = _composition.asStateFlow()

    fun updateTRs(tr: MyTextRange, com: MyTextRange?) {
        _selection.update {
            savedStateHandle[SELECTION] = Json.encodeToString(MyTextRange.serializer(), tr)
            tr
        }
        _composition.update {
            if (com != null)
                savedStateHandle[COMPOSITION] = Json.encodeToString(MyTextRange.serializer(), com)
            else
                savedStateHandle[COMPOSITION] = null
            com
        }
    }

    fun updateQ(q: String) = _fields.update {
        val new = it.updateQuestion(q)
        savedStateHandle[CD] = Json.encodeToString(CDetails.serializer(), new)
        new
    }

    fun updateA(a: String) = _fields.update {
        val new = it.updateAnswer(a)
        savedStateHandle[CD] = Json.encodeToString(CDetails.serializer(), new)
        new
    }

    fun updateM(m: String) = _fields.update {
        val new = it.updateMiddle(m)
        savedStateHandle[CD] = Json.encodeToString(CDetails.serializer(), new)
        new
    }

    fun updateCh(c: String, idx: Int) = _fields.update {
        val new = it.updateChoices(c, idx)
        savedStateHandle[CD] = Json.encodeToString(CDetails.serializer(), new)
        new
    }

    fun updateCor(c: Char) = _fields.update {
        val new = it.updateCorrect(c)
        savedStateHandle[CD] = Json.encodeToString(CDetails.serializer(), new)
        new
    }

    fun addStep() = _fields.update {
        val new = it.addStep()
        savedStateHandle[CD] = Json.encodeToString(CDetails.serializer(), new)
        new
    }

    fun removeStep() = _fields.update {
        val new = it.removeStep()
        savedStateHandle[CD] = Json.encodeToString(CDetails.serializer(), new)
        new
    }

    fun updateStep(s: String, idx: Int) = _fields.update {
        val new = it.updateStep(s, idx)
        savedStateHandle[CD] = Json.encodeToString(CDetails.serializer(), new)
        new
    }

    fun updateQA(qa: PartOfQorA) = _fields.update {
        val new = it.updateQOrA(qa)
        savedStateHandle[CD] = Json.encodeToString(CDetails.serializer(), new)
        new
    }

    private fun resetFields() = _fields.update {
        val new = CDetails()
        savedStateHandle[CD] = Json.encodeToString(CDetails.serializer(), new)
        new
    }

    private val _errorMessage: MutableStateFlow<String> = MutableStateFlow("")
    val errorMessage = _errorMessage.asStateFlow()
    private val isOwner = MutableStateFlow(false)
    private val rf = ReusedFunc(flashCardRepository)

    val showKatexKeyboard = kbRepository.showKatexKeyboard
    val selectedKB = kbRepository.selectedKB
    val resetOffset = kbRepository.resetOffset

    init {
        viewModelScope.launch {
            isOwner.update { isOwnerOrCoOwnerRepo.isCoOwnerOrCoOwner(deckUUID) }
        }
    }

    fun resetDone() = kbRepository.resetDone()

    fun updateSelectedKB(selectedKeyboard: SelectedKeyboard) {
        savedStateHandle[SELECTED_KB] =
            Json.encodeToString(SelectedKeyboard.serializer(), selectedKeyboard)
        kbRepository.updateSelectedKB(selectedKeyboard)
    }

    fun onCreate() {
        val kb = savedStateHandle.get<String>(SELECTED_KB)?.let {
            Json.decodeFromString(SelectedKeyboard.serializer(), it)
        }
        val showKB = savedStateHandle.get<Boolean>(SHOW_KB) == true
        kbRepository.onCreate(showKB, kb)
    }


    fun toggleKeyboard() {
        savedStateHandle[SHOW_KB] = !showKatexKeyboard.value
        kbRepository.toggleKeyboard()
    }

    fun resetSelectedKB() {
        savedStateHandle[SELECTED_KB] = null
        kbRepository.resetSelectedKB()
    }

    fun addBasicCard(deck: Deck, question: String, answer: String) {
        if (question.isNotBlank() && answer.isNotBlank()) {
            println("Is Owner: ${isOwner.value}")
            viewModelScope.launch(Dispatchers.IO) {
                flashCardRepository.insertBasicCard(
                    deck, BasicCD(question, answer), isOwner.value
                ).also { updateCardsLeft(deck); resetFields() }
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
                    deck, HintCD(question, hint, answer), isOwner.value
                ).also { updateCardsLeft(deck); resetFields() }
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
                    deck, ThreeCD(question, middle, answer, isQOrA), isOwner.value
                ).also { updateCardsLeft(deck); resetFields() }
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
                    deck, MultiCD(question, choiceA, choiceB, choiceC, choiceD, correct),
                    isOwner.value
                ).also { updateCardsLeft(deck); resetFields() }
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
                    deck, NotationCD(question, steps, answer), isOwner.value
                ).also { updateCardsLeft(deck); resetFields() }
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