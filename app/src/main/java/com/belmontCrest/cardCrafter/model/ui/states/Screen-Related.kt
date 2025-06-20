package com.belmontCrest.cardCrafter.model.ui.states


import android.os.Parcel
import android.os.Parcelable
import androidx.compose.ui.text.TextRange
import com.belmontCrest.cardCrafter.localDatabase.tables.PartOfQorA
import kotlinx.parcelize.Parceler
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


@Parcelize
@Serializable
data class MyTextRange(val start: Int = 0, val end: Int = 0) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt()
    )

    companion object : Parceler<MyTextRange> {
        override fun MyTextRange.write(parcel: Parcel, flags: Int) {
            parcel.writeInt(start)
            parcel.writeInt(end)
        }

        override fun create(parcel: Parcel): MyTextRange {
            return MyTextRange(parcel)
        }
    }
}

fun MyTextRange.toTextRange(): TextRange = TextRange(this.start, this.end)

fun TextRange.toMyTextRange(): MyTextRange = MyTextRange(this.start, this.end)
fun TextRange?.toMyTextRange(): MyTextRange? =
    if (this != null) MyTextRange(this.start, this.end) else null

@Parcelize
@Serializable
data class CDetails(
    val question: String = "", val middle: String = "", val answer: String = "",
    val choices: List<String> = List(4) { "" }, val correct: Char = '?',
    val steps: List<String> = emptyList(), val isQOrA: PartOfQorA = PartOfQorA.A
) : Parcelable

fun CDetails.updateQuestion(q: String): CDetails =
    CDetails(
        question = q, middle = middle, answer = answer,
        choices = choices, correct = correct,
        steps = steps, isQOrA = isQOrA
    )

fun CDetails.updateAnswer(a: String): CDetails =
    CDetails(
        question = question, middle = middle, answer = a,
        choices = choices, correct = correct,
        steps = steps, isQOrA = isQOrA
    )

fun CDetails.updateMiddle(m: String): CDetails =
    CDetails(
        question = question, middle = m, answer = answer,
        choices = choices, correct = correct,
        steps = steps, isQOrA = isQOrA
    )

fun CDetails.updateChoices(c: String, idx: Int): CDetails {
    val newChoices = this.choices.mapIndexed { index, string ->
        val value = if (index == idx) c else string
        value
    }
    return CDetails(
        question = question, middle = middle, answer = answer,
        choices = newChoices, correct = correct,
        steps = steps, isQOrA = isQOrA
    )
}

fun CDetails.updateCorrect(c: Char): CDetails =
    CDetails(
        question = question, middle = middle, answer = answer,
        choices = choices, correct = c,
        steps = steps, isQOrA = isQOrA
    )

fun CDetails.addStep(): CDetails {
    val newSteps = steps.toMutableList()
    newSteps.add("")
    return CDetails(
        question = question, middle = middle, answer = answer,
        choices = choices, correct = correct,
        steps = newSteps, isQOrA = isQOrA
    )
}

fun CDetails.removeStep(): CDetails {
    val newSteps = steps.toMutableList()
    newSteps.removeAt(newSteps.lastIndex)
    return CDetails(
        question = question, middle = middle, answer = answer,
        choices = choices, correct = correct,
        steps = newSteps, isQOrA = isQOrA
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
        steps = newSteps, isQOrA = isQOrA
    )
}

fun CDetails.updateQOrA(qa: PartOfQorA): CDetails =
    CDetails(
        question = question, middle = middle, answer = answer,
        choices = choices, correct = correct,
        steps = steps, isQOrA = qa
    )