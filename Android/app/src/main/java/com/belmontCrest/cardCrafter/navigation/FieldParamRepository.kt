package com.belmontCrest.cardCrafter.navigation

import com.belmontCrest.cardCrafter.localDatabase.tables.PartOfQorA
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.AnswerParam
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.MiddleParam
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.Param
import com.belmontCrest.cardCrafter.model.ui.states.CDetails
import com.belmontCrest.cardCrafter.model.ui.states.TypeInfo
import com.belmontCrest.cardCrafter.model.ui.states.addStep
import com.belmontCrest.cardCrafter.model.ui.states.removeStep
import com.belmontCrest.cardCrafter.model.ui.states.updateAnswer
import com.belmontCrest.cardCrafter.model.ui.states.updateChoices
import com.belmontCrest.cardCrafter.model.ui.states.updateCorrect
import com.belmontCrest.cardCrafter.model.ui.states.updateCustomFields
import com.belmontCrest.cardCrafter.model.ui.states.updateMiddle
import com.belmontCrest.cardCrafter.model.ui.states.updateQOrA
import com.belmontCrest.cardCrafter.model.ui.states.updateQuestion
import com.belmontCrest.cardCrafter.model.ui.states.updateStep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface FieldParamRepository {
    val fields: StateFlow<CDetails>
    fun updateQ(q: String): CDetails
    fun updateA(a: String): CDetails
    fun updateM(m: String): CDetails
    fun updateCh(c: String, idx: Int): CDetails
    fun updateCor(c: Char): CDetails
    fun addStep(): CDetails
    fun removeStep(): CDetails
    fun updateStep(s: String, idx: Int): CDetails
    fun updateQA(qa: PartOfQorA): CDetails
    fun updateQ(q: Param): CDetails
    fun updateA(a: AnswerParam): CDetails
    fun updateM(m: MiddleParam): CDetails
    fun resetFields(): CDetails
    fun createFields(field: CDetails)
    fun updateCustomFields(typeInfo: TypeInfo): CDetails
}

class FieldParamRepositoryImpl() : FieldParamRepository {
    private val _fields = MutableStateFlow(CDetails())
    override val fields = _fields.asStateFlow()

    override fun updateQ(q: String): CDetails {
        val new = fields.value.updateQuestion(q)
        _fields.update { new }
        return new
    }

    override fun updateA(a: String): CDetails {
        val new = _fields.value.updateAnswer(a)
        _fields.update { new }
        return new
    }

    override fun updateM(m: String): CDetails {
        val new = _fields.value.updateMiddle(m)
        _fields.update { new }
        return new
    }

    override fun updateCh(c: String, idx: Int): CDetails {
        val new = _fields.value.updateChoices(c, idx)
        _fields.update { new }
        return new
    }

    override fun updateCor(c: Char): CDetails {
        val new = _fields.value.updateCorrect(c)
        _fields.update { new }
        return new
    }

    override fun addStep(): CDetails {
        val new = _fields.value.addStep()
        _fields.update { new }
        return new
    }

    override fun removeStep(): CDetails {
        val new = _fields.value.removeStep()
        _fields.update { new }
        return new
    }

    override fun updateStep(s: String, idx: Int): CDetails {
        val new = _fields.value.updateStep(s, idx)
        _fields.update { new }
        return new
    }

    override fun updateQA(qa: PartOfQorA): CDetails {
        val new = _fields.value.updateQOrA(qa)
        _fields.update { new }
        return new
    }

    override fun updateQ(q: Param): CDetails {
        val new = _fields.value.updateQuestion(q)
        _fields.update { new }
        return new
    }

    override fun updateA(a: AnswerParam): CDetails {
        val new = _fields.value.updateAnswer(a)
        _fields.update { new }
        return new
    }

    override fun updateM(m: MiddleParam): CDetails {
        val new = _fields.value.updateMiddle(m)
        _fields.update { new }
        return new
    }

    override fun resetFields(): CDetails {
        val new = CDetails()
        _fields.update { new }
        return new
    }

    override fun createFields(field: CDetails) = _fields.update { field }

    override fun updateCustomFields(typeInfo: TypeInfo): CDetails {
        val new = _fields.value.updateCustomFields(typeInfo)
        _fields.update { new }
        return new
    }
}