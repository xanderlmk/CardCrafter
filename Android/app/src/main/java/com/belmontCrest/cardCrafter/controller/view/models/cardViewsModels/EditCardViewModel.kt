package com.belmontCrest.cardCrafter.controller.view.models.cardViewsModels

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belmontCrest.cardCrafter.local.db.repositories.CardTypeRepository
import com.belmontCrest.cardCrafter.local.db.tables.CT
import com.belmontCrest.cardCrafter.local.db.tables.ListStringConverter
import com.belmontCrest.cardCrafter.local.db.tables.PartOfQorA
import com.belmontCrest.cardCrafter.local.db.tables.customCardInit.AnswerParam
import com.belmontCrest.cardCrafter.local.db.tables.customCardInit.MiddleParam
import com.belmontCrest.cardCrafter.local.db.tables.customCardInit.Param
import com.belmontCrest.cardCrafter.model.ui.states.CDetails
import com.belmontCrest.cardCrafter.model.ui.states.SelectedKeyboard
import com.belmontCrest.cardCrafter.model.ui.states.hasNotationParam
import com.belmontCrest.cardCrafter.navigation.FieldParamRepo
import com.belmontCrest.cardCrafter.navigation.KeyboardSelectionRepo
import com.belmontCrest.cardCrafter.ui.functions.showToastMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class EditCardViewModel(
    private val cardTypeRepository: CardTypeRepository,
    private val kbRepository: KeyboardSelectionRepo,
    private val fieldParamRepo: FieldParamRepo,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    companion object {
        private const val SHOW_KB = "show_kb"
        private const val CD = "e_card_details"
        private const val TIMEOUT_MILLIS = 4_000L
        private val listStringConverter = ListStringConverter()
    }

    private val _errorMessage = MutableStateFlow("")
    val errorMessage = _errorMessage.asStateFlow()
    val showKatexKeyboard = kbRepository.showKatexKeyboard
    val selectedKB = kbRepository.selectedKB
    val resetOffset = kbRepository.resetOffset

    /**
     * A state flow of CDetails
     *
     * If there's a savedStateHandle, update it on initialValue
     */
    val fields = fieldParamRepo.fields.stateIn(
        scope = viewModelScope, started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = savedStateHandle.get<String>(CD)?.let {
            val init = Json.decodeFromString(CDetails.serializer(), it)
            fieldParamRepo.createFields(init)
            init
        } ?: fieldParamRepo.fields.value
    )
    val type = kbRepository.type

    val triple = combine(fields, showKatexKeyboard, selectedKB) { field, show, kb ->
        Triple(field, show, kb)
    }.stateIn(
        scope = viewModelScope, started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = Triple(fields.value, showKatexKeyboard.value, selectedKB.value)
    )

    fun updateQ(q: String) = fieldParamRepo.updateQ(q).also {
        savedStateHandle[CD] = Json.encodeToString(CDetails.serializer(), it)
    }

    fun updateA(a: String) = fieldParamRepo.updateA(a).also {
        savedStateHandle[CD] = Json.encodeToString(CDetails.serializer(), it)
    }

    fun updateM(m: String) = fieldParamRepo.updateM(m).also {
        savedStateHandle[CD] = Json.encodeToString(CDetails.serializer(), it)
    }

    fun updateCh(c: String, idx: Int) = fieldParamRepo.updateCh(c, idx).also {
        savedStateHandle[CD] = Json.encodeToString(CDetails.serializer(), it)
    }

    fun updateCor(c: Char) = fieldParamRepo.updateCor(c).also {
        savedStateHandle[CD] = Json.encodeToString(CDetails.serializer(), it)
    }

    fun addStep() = fieldParamRepo.addStep().also {
        savedStateHandle[CD] = Json.encodeToString(CDetails.serializer(), it)
    }

    fun removeStep() = fieldParamRepo.removeStep().also {
        savedStateHandle[CD] = Json.encodeToString(CDetails.serializer(), it)
    }

    fun updateStep(s: String, idx: Int) = fieldParamRepo.updateStep(s, idx).also {
        savedStateHandle[CD] = Json.encodeToString(CDetails.serializer(), it)
    }

    fun updateQA(qa: PartOfQorA) = fieldParamRepo.updateQA(qa).also {
        savedStateHandle[CD] = Json.encodeToString(CDetails.serializer(), it)
    }

    fun updateQ(q: Param) = fieldParamRepo.updateQ(q).also {
        savedStateHandle[CD] = Json.encodeToString(CDetails.serializer(), it)
        isNotationType()
    }

    fun updateA(a: AnswerParam) = fieldParamRepo.updateA(a).also {
        savedStateHandle[CD] = Json.encodeToString(CDetails.serializer(), it)
        isNotationType()
    }

    fun updateM(m: MiddleParam) = fieldParamRepo.updateM(m).also {
        savedStateHandle[CD] = Json.encodeToString(CDetails.serializer(), it)
        isNotationType()
    }

    fun resetDone() = kbRepository.resetDone()

    fun updateSelectedKB(selectedKeyboard: SelectedKeyboard) =
        kbRepository.updateSelectedKB(selectedKeyboard)


    fun toggleKeyboard() {
        savedStateHandle[SHOW_KB] = !showKatexKeyboard.value
        kbRepository.toggleKeyboardIcon()
    }

    fun resetKBStuff() {
        savedStateHandle[SHOW_KB] = false; kbRepository.resetKeyboardStuff()
    }

    fun resetSelectedKB() = kbRepository.resetSelectedKB()

    fun updateBasicCard(cardId: Int, question: String, answer: String) =
        viewModelScope.launch {
            cardTypeRepository.updateBasicCard(cardId, question, answer)
        }


    fun updateHintCard(cardId: Int, question: String, hint: String, answer: String) =
        viewModelScope.launch {
            cardTypeRepository.updateHintCard(cardId, question, hint, answer)
        }


    fun updateThreeCard(
        cardId: Int, question: String, middle: String,
        answer: String, isQOrA: PartOfQorA
    ) = viewModelScope.launch {
        cardTypeRepository.updateThreeCard(cardId, question, middle, answer, isQOrA)
    }


    fun updateMultiChoiceCard(
        cardId: Int, question: String, choiceA: String,
        choiceB: String, choiceC: String, choiceD: String, correct: Char
    ) = viewModelScope.launch {
        cardTypeRepository.updateMultiChoiceCard(
            cardId, question, choiceA,
            choiceB, choiceC, choiceD, correct
        )
    }

    fun updateNotationCard(
        cardId: Int,
        question: String,
        steps: List<String>,
        answer: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        val stepsToString = listStringConverter.listToString(steps)
        cardTypeRepository.updateNotationCard(question, stepsToString, answer, cardId)
    }

    fun updateCustomCard(
        cardId: Int, question: Param, middleParam: MiddleParam, answerParam: AnswerParam
    ) = viewModelScope.launch(Dispatchers.IO) {
        cardTypeRepository.updateCustomCard(cardId, question, middleParam, answerParam)
    }

    suspend fun updateCardType(
        cardId: Int, type: String, fields: CDetails,
        deleteCT: CT, context: Context
    ) = withContext(Dispatchers.IO) {
        try {
            cardTypeRepository.updateCT(cardId, type, fields, deleteCT)
        } catch (e: Exception) {
            showToastMessage(context, e.message ?: "Failed to update card.")
        }
    }

    fun setErrorMessage(message: String) = _errorMessage.update { message }

    fun clearErrorMessage() = _errorMessage.update { "" }

    /** Whether the fields have a notation parameter */
    private fun isNotationType() =
        kbRepository.updateNotationParamSelected(fields.value.hasNotationParam())
}

