package com.belmontCrest.cardCrafter.model.ui.states


import android.os.Parcelable
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.AnswerParam
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.MiddleParam
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.Param
import com.belmontCrest.cardCrafter.localDatabase.tables.PartOfQorA
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.defaultParam
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
/** The state of the current card */
sealed class CardState : Parcelable {
    @Serializable
    @Parcelize
    /** Not loading */
    data object Idle : CardState()

    @Serializable
    @Parcelize
    /** Loading */
    data object Loading : CardState()

    @Serializable
    @Parcelize
    /** Finished updating the card || Finished retrieving the cards */
    data object Finished : CardState()
}

data object CSS {
    const val IDLE = "idle"
    const val LOADING = "loading"
    const val FINISHED = "finished"
}
fun String.toCardState() = when (this) {
    CSS.LOADING -> CardState.Loading
    CSS.FINISHED -> CardState.Finished
    else -> CardState.Idle
}
fun CardState.toCSString() = when (this) {
    CardState.Finished -> CSS.FINISHED
    CardState.Idle -> CSS.IDLE
    CardState.Loading -> CSS.LOADING
}

/** Selected keyboard which is tied to NavVM */
@Serializable
@Parcelize
sealed class SelectedKeyboard : Parcelable {
    @Serializable
    data object Question : SelectedKeyboard()

    /** The pertaining step on the steps from stringList. */
    @Serializable
    data class Step(val index: Int) : SelectedKeyboard()

    @Serializable
    data object Answer : SelectedKeyboard()

    /** The Middle Param for CustomCard */
    @Serializable
    data object Middle : SelectedKeyboard()

    /** Pair Param Type for Question on CustomCard */
    @Serializable
    @Parcelize
    sealed class PairOfQuestion : SelectedKeyboard(), Parcelable {
        data object First : PairOfQuestion()
        data object Second : PairOfQuestion()
    }

    /** Pair Param Type for Middle on CustomCard */
    @Serializable
    @Parcelize
    sealed class PairOfMiddle : SelectedKeyboard(), Parcelable {
        data object First : PairOfMiddle()
        data object Second : PairOfMiddle()
    }

    /** Pair Param Type for Answer onCustomCard */
    @Serializable
    @Parcelize
    sealed class PairOfAnswer : SelectedKeyboard(), Parcelable {
        data object First : PairOfAnswer()
        data object Second : PairOfAnswer()
    }
}

/** Whether the user decides to move the cards or copy it */
@Parcelize
sealed class Decision : Parcelable {
    @Parcelize
    data object Move : Decision()

    @Parcelize
    data object Copy : Decision()

    @Parcelize
    data object Idle : Decision()
}

@Parcelize
data class Dialogs(
    val showDelete: Boolean, val showMoveCopy: Boolean, val showDuplicate: Boolean
) : Parcelable

/**
 * A data class containing all card parameters possible
 *
 * When adding a card and switching between types, the parameters are not overwritten, so
 * you can switch between types and the parameter content will still be there
 *
 * This does not apply when editing a card, the parameters will be overwritten
 * @param question Used in all 5 primitive card types (basic, three, hint, multi, notation)
 * @param middle Used in hint and three
 * @param answer Used in basic, three, hint, and notation
 * @param choices Used in multi, there are 2 choices minimum and 4 maximum.
 * @param correct Used in multi, values range from `'a' .. 'd'`, but is determined
 * based on the choices there are
 * @param steps Used in notation, can be an empty list
 * @param isQOrA Used in three, which determines whether the middle field will be part
 * of the question or answer
 * @param customQuestion Used in creating a new custom card type, or in a already saved
 * custom card.
 * @param customMiddle Used in creating a new custom card type, or in a already saved
 * custom card.
 * @param customAnswer Used in creating a new custom card type, or in a already saved
 * custom card.
 */
@Parcelize
@Serializable
data class CDetails(
    val question: String = "", val middle: String = "", val answer: String = "",
    val choices: List<String> = List(4) { "" }, val correct: Char = '?',
    val steps: List<String> = emptyList(), val isQOrA: PartOfQorA = PartOfQorA.A,
    val customQuestion: Param = Param.Type.String(""),
    val customMiddle: MiddleParam = MiddleParam.Empty,
    val customAnswer: AnswerParam = AnswerParam.WithParam(Param.Type.String(""))
) : Parcelable

fun CDetails.updateQuestion(q: String): CDetails =
    CDetails(
        question = q, middle = middle, answer = answer,
        choices = choices, correct = correct,
        steps = steps, isQOrA = isQOrA, customQuestion = customQuestion,
        customMiddle = customMiddle, customAnswer = customAnswer
    )

fun CDetails.updateAnswer(a: String): CDetails =
    CDetails(
        question = question, middle = middle, answer = a,
        choices = choices, correct = correct,
        steps = steps, isQOrA = isQOrA, customQuestion = customQuestion,
        customMiddle = customMiddle, customAnswer = customAnswer
    )

fun CDetails.updateMiddle(m: String): CDetails =
    CDetails(
        question = question, middle = m, answer = answer,
        choices = choices, correct = correct,
        steps = steps, isQOrA = isQOrA, customQuestion = customQuestion,
        customMiddle = customMiddle, customAnswer = customAnswer
    )

fun CDetails.updateChoices(c: String, idx: Int): CDetails {
    val newChoices = this.choices.mapIndexed { index, string ->
        val value = if (index == idx) c else string
        value
    }
    return CDetails(
        question = question, middle = middle, answer = answer,
        choices = newChoices, correct = correct,
        steps = steps, isQOrA = isQOrA, customQuestion = customQuestion,
        customMiddle = customMiddle, customAnswer = customAnswer
    )
}

fun CDetails.updateCorrect(c: Char): CDetails =
    CDetails(
        question = question, middle = middle, answer = answer,
        choices = choices, correct = c,
        steps = steps, isQOrA = isQOrA, customQuestion = customQuestion,
        customMiddle = customMiddle, customAnswer = customAnswer
    )

fun CDetails.addStep(): CDetails {
    val newSteps = steps.toMutableList()
    newSteps.add("")
    return CDetails(
        question = question, middle = middle, answer = answer,
        choices = choices, correct = correct,
        steps = newSteps, isQOrA = isQOrA, customQuestion = customQuestion,
        customMiddle = customMiddle, customAnswer = customAnswer
    )
}

fun CDetails.removeStep(): CDetails {
    val newSteps = steps.toMutableList()
    newSteps.removeAt(newSteps.lastIndex)
    return CDetails(
        question = question, middle = middle, answer = answer,
        choices = choices, correct = correct,
        steps = newSteps, isQOrA = isQOrA, customQuestion = customQuestion,
        customMiddle = customMiddle, customAnswer = customAnswer
    )
}

fun CDetails.updateStep(s: String, idx: Int): CDetails {
    val newSteps = steps.mapIndexed { index, string ->
        val value = if (index == idx) s else string
        value
    }
    return CDetails(
        question = question, middle = middle, answer = answer,
        choices = choices, correct = correct,
        steps = newSteps, isQOrA = isQOrA, customQuestion = customQuestion,
        customMiddle = customMiddle, customAnswer = customAnswer
    )
}

fun CDetails.updateQOrA(qa: PartOfQorA): CDetails =
    CDetails(
        question = question, middle = middle, answer = answer,
        choices = choices, correct = correct,
        steps = steps, isQOrA = qa, customQuestion = customQuestion,
        customMiddle = customMiddle, customAnswer = customAnswer
    )

fun CDetails.updateQuestion(q: Param): CDetails =
    CDetails(
        question = question, middle = middle, answer = answer,
        choices = choices, correct = correct,
        steps = steps, isQOrA = isQOrA, customQuestion = q,
        customMiddle = customMiddle, customAnswer = customAnswer
    )

fun CDetails.updateMiddle(m: MiddleParam): CDetails =
    CDetails(
        question = question, middle = middle, answer = answer,
        choices = choices, correct = correct,
        steps = steps, isQOrA = isQOrA, customQuestion = customQuestion,
        customMiddle = m, customAnswer = customAnswer
    )

fun CDetails.updateAnswer(a: AnswerParam): CDetails =
    CDetails(
        question = question, middle = middle, answer = answer,
        choices = choices, correct = correct,
        steps = steps, isQOrA = isQOrA, customQuestion = customQuestion,
        customMiddle = customMiddle, customAnswer = a
    )

fun CDetails.updateCustomFields(ti: TypeInfo): CDetails =
    CDetails(
        question = question, middle = middle, answer = answer,
        choices = choices, correct = correct,
        steps = steps, isQOrA = isQOrA, customQuestion = ti.q.defaultParam(),
        customMiddle = ti.m.defaultParam(), customAnswer = ti.a.defaultParam()
    )

fun CDetails.hasNotationParam(): Boolean =
    this.notationQuestionParamCheck() ||
            this.notationMiddleParamCheck() ||
            this.notationAnswerParamCheck()

private fun CDetails.notationQuestionParamCheck(): Boolean =
    this.customQuestion is Param.Type.Notation || (this.customQuestion is Param.Pair &&
            (this.customQuestion.first is Param.Type.Notation ||
                    this.customQuestion.second is Param.Type.Notation))

private fun CDetails.notationMiddleParamCheck(): Boolean =
    this.customMiddle is MiddleParam.WithParam &&
            (this.customMiddle.param is Param.Type.Notation ||
                    (this.customMiddle.param is Param.Pair &&
                            (this.customMiddle.param.first is Param.Type.Notation ||
                                    this.customMiddle.param.second is Param.Type.Notation)))

private fun CDetails.notationAnswerParamCheck(): Boolean =
    this.customAnswer is AnswerParam.NotationList || (this.customAnswer is AnswerParam.WithParam &&
            (this.customAnswer.param is Param.Type.Notation ||
                    (this.customAnswer.param is Param.Pair &&
                            (this.customAnswer.param.first is Param.Type.Notation ||
                                    this.customAnswer.param.second is Param.Type.Notation))))