package com.belmontCrest.cardCrafter.model.uiModels

import android.os.Parcel
import android.os.Parcelable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

/** This class will have all our UI variable declarations
 *  used throughout the entire application.
 */
@Parcelize
data class Fields(
    var question : MutableState<String> = mutableStateOf(""),
    var middleField : MutableState<String> = mutableStateOf(""),
    var answer : MutableState<String> = mutableStateOf(""),
    var choices: List<MutableState<String>> = List(4) { mutableStateOf("") },
    var correct: MutableState<Char> = mutableStateOf('?'),
    var scrollPosition : MutableState<Int> = mutableIntStateOf(0),
    val mainClicked : MutableState<Boolean> = mutableStateOf(false),
    val inDeckClicked : MutableState<Boolean> = mutableStateOf(false),
    val leftDueCardView : MutableState<Boolean> = mutableStateOf(false),
    var stringList : MutableList<MutableState<String>> = mutableListOf(),
    var newType : MutableState<String> = mutableStateOf(""),
    var isEditing : MutableState<Boolean> = mutableStateOf(false)

) : Parcelable {
    fun resetFields() {
        question.value = ""
        middleField.value = ""
        answer.value = ""
        choices.map {
            it.value = ""
        }
        correct.value = '?'
    }
    fun navigateToDeck() {
        leftDueCardView.value = false
        inDeckClicked.value = false
        scrollPosition.value = 0
    }
    fun navigateToDueCards() {
        inDeckClicked.value = true
        mainClicked.value = false
        leftDueCardView.value = false
    }
    fun navigateToCardList() {
        newType = mutableStateOf("")
        isEditing.value = false
        inDeckClicked.value = false
    }
    constructor(parcel: Parcel) : this(
        question = mutableStateOf(parcel.readString().orEmpty().ifEmpty { "" }),
        middleField = mutableStateOf(parcel.readString().orEmpty().ifEmpty { "" }),
        answer = mutableStateOf(parcel.readString().orEmpty().ifEmpty { "" }),
        choices = List(4) { mutableStateOf(parcel.readString() ?: "") },
        correct = mutableStateOf(parcel.readString().orEmpty().ifEmpty { "?" }.first()),
        scrollPosition = mutableIntStateOf(parcel.readInt()),
        mainClicked = mutableStateOf(parcel.readByte() != 0.toByte()),
        inDeckClicked = mutableStateOf(parcel.readByte() != 0.toByte()),
        leftDueCardView = mutableStateOf(parcel.readByte() != 0.toByte()),
        stringList = mutableListOf<MutableState<String>>().apply {
            parcel.readStringList(this.map { it.value }.toMutableList())
            addAll(this)
        },
        newType = mutableStateOf(parcel.readString().orEmpty().ifEmpty { "" }),
        isEditing = mutableStateOf(parcel.readByte() != 0.toByte())
    )

    companion object : Parceler<Fields> {
        override fun Fields.write(parcel: Parcel, flags: Int) {
            parcel.writeString(question.value)
            parcel.writeString(middleField.value)
            parcel.writeString(answer.value)
            parcel.writeStringList(choices.map { it.value })
            parcel.writeString(correct.toString())
            parcel.writeInt(scrollPosition.value)
            parcel.writeByte(if (mainClicked.value) 1 else 0)
            parcel.writeByte(if (inDeckClicked.value) 1 else 0)
            parcel.writeByte(if (leftDueCardView.value) 1 else 0)
            parcel.writeStringList(stringList.map { it.value })
            parcel.writeString(newType.value)
            parcel.writeByte(if (isEditing.value) 1 else 0)
        }
        override fun create(parcel: Parcel): Fields {
            return Fields(parcel)
        }
    }
}

