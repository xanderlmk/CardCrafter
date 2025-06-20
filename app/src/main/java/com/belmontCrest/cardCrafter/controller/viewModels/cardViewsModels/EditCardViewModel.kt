package com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belmontCrest.cardCrafter.controller.cardHandlers.getCardType
import com.belmontCrest.cardCrafter.controller.cardHandlers.toCDetails
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.CardTypeRepository
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.ScienceSpecificRepository
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.localDatabase.tables.ListStringConverter
import com.belmontCrest.cardCrafter.localDatabase.tables.PartOfQorA
import com.belmontCrest.cardCrafter.model.Type
import com.belmontCrest.cardCrafter.model.ui.states.CDetails
import com.belmontCrest.cardCrafter.model.ui.states.MyTextRange
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
import com.belmontCrest.cardCrafter.uiFunctions.katex.menu.KaTeXMenu
import com.belmontCrest.cardCrafter.uiFunctions.katex.menu.ListOfKatexMenu
import com.belmontCrest.cardCrafter.uiFunctions.katex.menu.SelectedAnnotation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class EditCardViewModel(
    private val cardTypeRepository: CardTypeRepository,
    private val sSRepository: ScienceSpecificRepository,
    private val kbRepository: KeyboardSelectionRepository,
    private val savedStateHandle: SavedStateHandle,
    private val cardId: Int
) : ViewModel() {
    companion object {
        private const val SELECTED_KB = "selected_keyboard"
        private const val SHOW_KB = "show_kb"
        private const val CD = "e_card_details"
        private const val SELECTION = "e_selection"
        private const val COMPOSITION = "e_composition"
        private const val SELECTED_SL = "e_selected"
        private val listStringConverter = ListStringConverter()
    }

    private val _selectedSLSymbols = MutableStateFlow(
        savedStateHandle.get<String>(SELECTED_SL)?.let {
            Json.decodeFromString(ListOfKatexMenu.serializer(), it)
        } ?: ListOfKatexMenu()
    )

    val selectedSLSymbols = _selectedSLSymbols.map { it.km }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(4_000L),
        initialValue = listOf()
    )
    private val _errorMessage = MutableStateFlow("")
    val errorMessage = _errorMessage.asStateFlow()
    val showKatexKeyboard = kbRepository.showKatexKeyboard
    val selectedKB = kbRepository.selectedKB
    val resetOffset = kbRepository.resetOffset

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

    fun updateSelectedSymbol(kw: KaTeXMenu, index: Int) = _selectedSLSymbols.update {
        val new = it.km.toMutableList()
        new[index] = kw
        ListOfKatexMenu(new)
    }

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

    fun addStep() {
        _selectedSLSymbols.update {
            val new = it.km.toMutableList()
            new.add(KaTeXMenu(null, SelectedAnnotation.Idle))
            ListOfKatexMenu(new.toList())
        }
        _fields.update {
            val new = it.addStep()
            savedStateHandle[CD] = Json.encodeToString(CDetails.serializer(), new)
            new
        }
    }

    fun removeStep() {
        _fields.update {
            val new = it.removeStep()
            savedStateHandle[CD] = Json.encodeToString(CDetails.serializer(), new)
            new
        }
        _selectedSLSymbols.update {
            val new = it.km.toMutableList()
            new.removeAt(new.lastIndex)
            ListOfKatexMenu(new.toList())
        }
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

    fun resetDone() = kbRepository.resetDone()

    fun updateSelectedKB(selectedKeyboard: SelectedKeyboard) {
        savedStateHandle[SELECTED_KB] =
            Json.encodeToString(SelectedKeyboard.serializer(), selectedKeyboard)
        kbRepository.updateSelectedKB(selectedKeyboard)
    }

    fun toggleKeyboard() {
        savedStateHandle[SHOW_KB] = !showKatexKeyboard.value
        kbRepository.toggleKeyboard()
    }

    fun onCreate() {
        val kb = savedStateHandle.get<String>(SELECTED_KB)?.let {
            Json.decodeFromString(SelectedKeyboard.serializer(), it)
        }
        val showKB = savedStateHandle.get<Boolean>(SHOW_KB) == true
        kbRepository.onCreate(showKB, kb)
    }

    fun resetSelectedKB() {
        savedStateHandle[SELECTED_KB] = null
        kbRepository.resetSelectedKB()
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

    fun updateThreeCard(
        cardId: Int, question: String, middle: String,
        answer: String, isQOrA: PartOfQorA
    ) {
        viewModelScope.launch {
            cardTypeRepository.updateThreeCard(cardId, question, middle, answer, isQOrA)
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
        cardId: Int, type: String, fields: CDetails,
        deleteCT: CT
    ) {
        viewModelScope.launch {
            cardTypeRepository.updateCT(cardId, type, fields, deleteCT)
        }.join()
    }

    fun setErrorMessage(message: String) {
        _errorMessage.update { message }
    }

    fun clearErrorMessage() {
        _errorMessage.update { "" }
    }

    suspend fun initialCT(): CDetails {
        return withContext(Dispatchers.IO) {
            val ct = cardTypeRepository.getACardType(cardId)
            if (ct.getCardType() == Type.NOTATION) _selectedSLSymbols.update {
                ListOfKatexMenu(
                    ct.toCDetails().steps.map { KaTeXMenu(null, SelectedAnnotation.Idle) }
                )
            }
            ct.toCDetails()
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val cardDetails = initialCT()
            _fields.update { cardDetails }
        }
    }
}

