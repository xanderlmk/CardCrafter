package com.belmontCrest.cardCrafter.controller.view.models.cardViewsModels


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belmontCrest.cardCrafter.controller.view.models.ReusedFunc
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.FlashCardRepository
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.AnswerParam
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.MiddleParam
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.Param
import com.belmontCrest.cardCrafter.localDatabase.tables.PartOfQorA
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.isNotBlankOrEmpty
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.toParamType
import com.belmontCrest.cardCrafter.model.isPrimitiveType
import com.belmontCrest.cardCrafter.model.ui.states.CDetails
import com.belmontCrest.cardCrafter.model.ui.states.SelectedKeyboard
import com.belmontCrest.cardCrafter.model.ui.states.TypeInfo
import com.belmontCrest.cardCrafter.model.ui.states.hasNotationParam
import com.belmontCrest.cardCrafter.navigation.FieldParamRepository
import com.belmontCrest.cardCrafter.navigation.KeyboardSelectionRepository
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.authRepo.IsOwnerOrCoOwnerRepo
import com.belmontCrest.cardCrafter.views.misc.details.CardDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class AddCardViewModel(
    private val flashCardRepository: FlashCardRepository,
    private val isOwnerOrCoOwnerRepo: IsOwnerOrCoOwnerRepo,
    private val kbRepository: KeyboardSelectionRepository,
    private val fieldParamRepository: FieldParamRepository,
    private val savedStateHandle: SavedStateHandle,
    private val deckUUID: String
) : ViewModel() {
    companion object {
        private const val SHOW_KB = "show_kb"
        private const val CD = "a_card_details"
        private const val TIMEOUT_MILLIS = 4_000L
    }

    /**
     * A state flow of CDetails
     *
     * If there's a savedStateHandle, update it on initialValue
     */
    val fields = fieldParamRepository.fields.stateIn(
        scope = viewModelScope, started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = savedStateHandle.get<String>(CD)?.let {
            val init = Json.decodeFromString(CDetails.serializer(), it)
            fieldParamRepository.createFields(init)
            init
        } ?: CDetails()
    )
    val type = kbRepository.type

    fun updateQ(q: String) = fieldParamRepository.updateQ(q).also {
        savedStateHandle[CD] = Json.encodeToString(CDetails.serializer(), it)
    }

    fun updateA(a: String) = fieldParamRepository.updateA(a).also {
        savedStateHandle[CD] = Json.encodeToString(CDetails.serializer(), it)
    }

    fun updateM(m: String) = fieldParamRepository.updateM(m).also {
        savedStateHandle[CD] = Json.encodeToString(CDetails.serializer(), it)
    }

    fun updateCh(c: String, idx: Int) = fieldParamRepository.updateCh(c, idx).also {
        savedStateHandle[CD] = Json.encodeToString(CDetails.serializer(), it)
    }

    fun updateCor(c: Char) = fieldParamRepository.updateCor(c).also {
        savedStateHandle[CD] = Json.encodeToString(CDetails.serializer(), it)
    }

    fun addStep() = fieldParamRepository.addStep().also {
        savedStateHandle[CD] = Json.encodeToString(CDetails.serializer(), it)
    }

    fun removeStep() = fieldParamRepository.removeStep().also {
        savedStateHandle[CD] = Json.encodeToString(CDetails.serializer(), it)
    }

    fun updateStep(s: String, idx: Int) = fieldParamRepository.updateStep(s, idx).also {
        savedStateHandle[CD] = Json.encodeToString(CDetails.serializer(), it)
    }

    fun updateQA(qa: PartOfQorA) = fieldParamRepository.updateQA(qa).also {
        savedStateHandle[CD] = Json.encodeToString(CDetails.serializer(), it)
    }

    fun updateQ(q: Param) = fieldParamRepository.updateQ(q).also {
        savedStateHandle[CD] = Json.encodeToString(CDetails.serializer(), it)
        isNotationType()
    }

    fun updateA(a: AnswerParam) = fieldParamRepository.updateA(a).also {
        savedStateHandle[CD] = Json.encodeToString(CDetails.serializer(), it)
        isNotationType()
    }

    fun updateM(m: MiddleParam) = fieldParamRepository.updateM(m).also {
        savedStateHandle[CD] = Json.encodeToString(CDetails.serializer(), it)
        isNotationType()
    }

    private fun resetFields() = fieldParamRepository.resetFields().also {
        savedStateHandle[CD] = Json.encodeToString(CDetails.serializer(), it)
        isNotationType()
    }

    private fun updateType(new: String) = kbRepository.updateType(new)

    private val _errorMessage: MutableStateFlow<String> = MutableStateFlow("")
    val errorMessage = _errorMessage.asStateFlow()
    private val isOwner = MutableStateFlow(false)
    private val rf = ReusedFunc(flashCardRepository)

    val showKatexKeyboard = kbRepository.showKatexKeyboard
    val selectedKB = kbRepository.selectedKB
    val resetOffset = kbRepository.resetOffset

    @OptIn(ExperimentalCoroutinesApi::class)
    private val types = kbRepository.customTypes.flatMapLatest { types ->
        val strings = types.ts.map { it.t }
        flowOf(strings)
    }.stateIn(
        scope = viewModelScope, started = SharingStarted.Eagerly,
        initialValue = kbRepository.customTypes.value.ts.map { it.t }
    )

    init {
        viewModelScope.launch {
            isOwner.update { isOwnerOrCoOwnerRepo.isCoOwnerOrCoOwner(deckUUID) }
        }
    }

    fun resetDone() = kbRepository.resetDone()

    fun updateSelectedKB(selectedKeyboard: SelectedKeyboard) =
        kbRepository.updateSelectedKB(selectedKeyboard)

    fun toggleKeyboardIcon() {
        savedStateHandle[SHOW_KB] = !showKatexKeyboard.value
        kbRepository.toggleKeyboardIcon()
    }

    fun resetKBStuff() {
        savedStateHandle[SHOW_KB] = false; kbRepository.resetKeyboardStuff()
    }

    fun resetSelectedKB() = kbRepository.resetSelectedKB()

    fun addBasicCard(deck: Deck, question: String, answer: String) =
        viewModelScope.launch(Dispatchers.IO) {
            if (question.isNotBlank() && answer.isNotBlank()) {
                // println("Is Owner: ${isOwner.value}")
                flashCardRepository.insertBasicCard(
                    deck, CardDetails.BasicCD(question, answer), isOwner.value
                ).also { updateCardsLeft(deck); resetFields() }
            }
        }

    fun addHintCard(
        deck: Deck, question: String, hint: String, answer: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        if (question.isNotBlank() && answer.isNotBlank() && hint.isNotBlank()) {
            flashCardRepository.insertHintCard(
                deck, CardDetails.HintCD(question, hint, answer), isOwner.value
            ).also { updateCardsLeft(deck); resetFields() }
        }
    }


    fun addThreeCard(
        deck: Deck, question: String, middle: String, answer: String, isQOrA: PartOfQorA
    ) = viewModelScope.launch(Dispatchers.IO) {
        if (question.isNotBlank() && answer.isNotBlank() && middle.isNotBlank()) {
            flashCardRepository.insertThreeCard(
                deck, CardDetails.ThreeCD(question, middle, answer, isQOrA), isOwner.value
            ).also { updateCardsLeft(deck); resetFields() }
        }
    }


    fun addMultiChoiceCard(
        deck: Deck, question: String, choiceA: String, choiceB: String,
        choiceC: String, choiceD: String, correct: Char
    ) = viewModelScope.launch(Dispatchers.IO) {
        if (question.isNotBlank() && choiceA.isNotBlank() && choiceB.isNotBlank() &&
            correct in 'a'..'d'
        ) {
            flashCardRepository.insertMultiCard(
                deck,
                CardDetails.MultiCD(question, choiceA, choiceB, choiceC, choiceD, correct),
                isOwner.value
            ).also { updateCardsLeft(deck); resetFields() }
        }
    }

    fun addNotationCard(
        deck: Deck, question: String, steps: List<String>, answer: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        if (question.isNotBlank() && steps.all { it.isNotBlank() } && answer.isNotBlank()) {
            flashCardRepository.insertNotationCard(
                deck, CardDetails.NotationCD(question, steps, answer), isOwner.value
            ).also { updateCardsLeft(deck); resetFields() }
        }
    }


    /** Add a new custom card type and save the parameters */
    fun addCustomCard(
        deck: Deck, question: Param, middleParam: MiddleParam, answer: AnswerParam, type: String,
    ) = viewModelScope.launch(Dispatchers.IO) {
        if (question.isNotBlankOrEmpty() && middleParam.isNotBlankOrEmpty() &&
            answer.isNotBlankOrEmpty()
        ) {
            val typeInfo = TypeInfo(
                type, question.toParamType(), middleParam.toParamType(), answer.toParamType()
            )
            kbRepository.updateList(typeInfo)
            flashCardRepository.insertCustomCard(
                deck, CardDetails.CustomCD(question, middleParam, answer), type, isOwner.value
            ).also { updateCardsLeft(deck); resetFields(); updateType(type) }
        }
    }

    /** Add a already saved custom card type */
    fun addSavedTypeCard(
        deck: Deck, question: Param, middleParam: MiddleParam, answer: AnswerParam, type: String,
    ) = viewModelScope.launch(Dispatchers.IO) {
        if (question.isNotBlankOrEmpty() && middleParam.isNotBlankOrEmpty() &&
            answer.isNotBlankOrEmpty()
        ) {
            flashCardRepository.insertCustomCard(
                deck, CardDetails.CustomCD(question, middleParam, answer), type, isOwner.value
            ).also { updateCardsLeft(deck); resetFields() }
        }
    }

    /** Check whether the type already exist of it's a primitive type */
    fun checkIfTypeExists(type: String): Boolean {
        val set = types.value.toSet()
        return type in set || type.isPrimitiveType()
    }

    suspend fun updateCardsLeft(deck: Deck, cardsToAdd: Int = 1) =
        rf.updateCardsLeft(deck, cardsToAdd)

    fun setErrorMessage(message: String) = _errorMessage.update { message }

    fun clearErrorMessage() = _errorMessage.update { "" }

    /** Whether the fields have a notation parameter */
    private fun isNotationType() =
        kbRepository.updateNotationParamSelected(fields.value.hasNotationParam())

}