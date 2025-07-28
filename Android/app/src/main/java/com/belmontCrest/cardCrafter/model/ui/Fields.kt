package com.belmontCrest.cardCrafter.model.ui

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
    var scrollPosition: MutableState<Int> = mutableIntStateOf(0),
    val mainClicked: MutableState<Boolean> = mutableStateOf(false),
    val inDeckClicked: MutableState<Boolean> = mutableStateOf(false),
    val leftDueCardView: MutableState<Boolean> = mutableStateOf(false),
    var isEditing: MutableState<Boolean> = mutableStateOf(false)
) : Parcelable {

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
        isEditing.value = false; inDeckClicked.value = false
    }

    constructor(parcel: Parcel) : this(
        scrollPosition = mutableIntStateOf(parcel.readInt()),
        mainClicked = mutableStateOf(parcel.readByte() != 0.toByte()),
        inDeckClicked = mutableStateOf(parcel.readByte() != 0.toByte()),
        leftDueCardView = mutableStateOf(parcel.readByte() != 0.toByte()),
        isEditing = mutableStateOf(parcel.readByte() != 0.toByte())
    )

    companion object : Parceler<Fields> {
        override fun Fields.write(parcel: Parcel, flags: Int) {
            parcel.writeInt(scrollPosition.value)
            parcel.writeByte(if (mainClicked.value) 1 else 0)
            parcel.writeByte(if (inDeckClicked.value) 1 else 0)
            parcel.writeByte(if (leftDueCardView.value) 1 else 0)
            parcel.writeByte(if (isEditing.value) 1 else 0)
        }

        override fun create(parcel: Parcel): Fields {
            return Fields(parcel)
        }
    }
}

