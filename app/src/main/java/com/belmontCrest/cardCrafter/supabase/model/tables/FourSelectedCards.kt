package com.belmontCrest.cardCrafter.supabase.model.tables

import android.os.Parcelable
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class CardsToDisplay(
    val id: Int = -1,
    val deckUUID: String = "",
    @SerialName("card_one")
    val cardOne: String? = null,
    @SerialName("card_two")
    val cardTwo: String? = null,
    @SerialName("card_three")
    val cardThree: String? = null,
    @SerialName("card_four")
    val cardFour: String? = null,
)
// Helper to slot a cardIdentifier into the first null field
fun CardsToDisplay.add(cardIdentifier: String): CardsToDisplay = when {
    cardOne == null -> copy(cardOne = cardIdentifier)
    cardTwo == null -> copy(cardTwo = cardIdentifier)
    cardThree == null -> copy(cardThree = cardIdentifier)
    cardFour == null -> copy(cardFour = cardIdentifier)
    else -> this // all full, do nothing or replace oldest as you see fit
}

fun CardsToDisplay.isEmpty(): Boolean =
    listOf(cardOne, cardTwo, cardThree, cardFour).all { it == null }

@Serializable
data class FourSBCards(
    val first: SBCardColsWithCT? = null,
    val second: SBCardColsWithCT? = null,
    val third: SBCardColsWithCT? = null,
    val fourth: SBCardColsWithCT? = null
)

@Parcelize
data class FourSelectedCards(
    val first: CT? = null,
    val second: CT? = null,
    val third: CT? = null,
    val fourth: CT? = null,
): Parcelable

fun FourSelectedCards.isEmpty(): Boolean =
    listOf(first, second, third, fourth).all { it == null }

fun FourSelectedCards.isThereCards(): Boolean = !isEmpty()
