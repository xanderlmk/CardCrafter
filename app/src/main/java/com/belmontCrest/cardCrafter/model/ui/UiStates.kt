package com.belmontCrest.cardCrafter.model.ui


import android.os.Parcelable
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.localDatabase.tables.SavedCard
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.util.Date

/** MainViewModel States */
@Parcelize
data class DeckUiState(
    val deckList: List<Deck> = listOf()
) : Parcelable

@Serializable
@Parcelize
data class CardListUiCount(
    val cardListCount: List<Int> = listOf()
) : Parcelable

@Parcelize
data class SealedAllCTs(
    var allCTs: MutableList<CT> = mutableListOf()
) : Parcelable

@Serializable

/** NavViewModel States */
@Parcelize
data class StringVar(
    val name: String = ""
) : Parcelable

@Parcelize
data class WhichDeck(
    val deck: Deck? = null
) : Parcelable

@Parcelize
data class SelectedCard(
    val ct: CT?
) : Parcelable

/** DueCards States */
data class DueDeckDetails(
    val id: Int = 0,
    var cardsLeft: Int = 0,
    val cardAmount: Int = 0,
    val reviewAmount: Int = 0,
    val nextReview: Date = Date()
)

/** For CardDeckVM */
@Parcelize
data class SealedDueCTs(
    var allCTs: MutableList<CT> = mutableListOf(),
    var savedCTs: MutableList<CT> = mutableListOf()
) : Parcelable

/** Also for our OnCreate MA */
data class SavedCardUiState(
    var savedCards: List<SavedCard> = emptyList()
)

@Serializable
@Parcelize
sealed class CardState : Parcelable {
    @Serializable
    @Parcelize
    data object Idle : CardState()

    @Serializable
    @Parcelize
    data object Loading : CardState()

    @Serializable
    @Parcelize
    data object Finished : CardState()
}

/** Selected keyboard which is tied to NavVM */
@Serializable
sealed class SelectedKeyboard {
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

