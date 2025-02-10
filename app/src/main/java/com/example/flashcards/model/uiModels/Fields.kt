package com.example.flashcards.model.uiModels

import android.os.Parcel
import android.os.Parcelable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

@Parcelize
class Fields(
    var question : MutableState<String> = mutableStateOf(""),
    var middleField : MutableState<String> = mutableStateOf(""),
    var answer : MutableState<String> = mutableStateOf(""),
    var choices: MutableList<MutableState<String>> = MutableList(4) { mutableStateOf("") },
    var correct: MutableState<Char> = mutableStateOf('?'),
    var scrollPosition : MutableState<Int> = mutableIntStateOf(0),
    val mainClicked : MutableState<Boolean> = mutableStateOf(false),
    val inDeckClicked : MutableState<Boolean> = mutableStateOf(false),
    val leftDueCardView : MutableState<Boolean> = mutableStateOf(false)
) : Parcelable {
    fun resetFields() {
        question.value = ""
        middleField.value = ""
        answer.value = ""
        choices[0].value = ""
        choices[1].value = ""
        choices[2].value = ""
        choices[3].value = ""
        correct.value = '?'
    }
    constructor(parcel: Parcel) : this(
        question = mutableStateOf(parcel.readString()!!),
        middleField = mutableStateOf(parcel.readString()!!),
        answer = mutableStateOf(parcel.readString()!!),
        choices = List(4) { mutableStateOf(parcel.readString()!!) }.toMutableList(),
        correct = mutableStateOf(parcel.readString()!![0]),
        scrollPosition = mutableIntStateOf(parcel.readInt()),
        mainClicked = mutableStateOf(parcel.readByte() != 0.toByte()),
        inDeckClicked = mutableStateOf(parcel.readByte() != 0.toByte()),
        leftDueCardView = mutableStateOf(parcel.readByte() != 0.toByte())
    )

    companion object : Parceler<Fields> {
        override fun Fields.write(parcel: Parcel, flags: Int) {
            parcel.writeString(question.value)
            parcel.writeString(middleField.value)
            parcel.writeString(answer.value)
            parcel.writeList(choices.toMutableList())
            parcel.writeString(choices.toString())
            parcel.writeInt(scrollPosition.value)
            parcel.writeByte(if (mainClicked.value) 1 else 0)
            parcel.writeByte(if (inDeckClicked.value) 1 else 0)
            parcel.writeByte(if (leftDueCardView.value) 1 else 0)
        }
        override fun create(parcel: Parcel): Fields {
            return Fields(parcel)
        }
    }
}

