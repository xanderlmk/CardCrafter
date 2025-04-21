package com.belmontCrest.cardCrafter.model.uiModels


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
    val name : String = ""
) : Parcelable
@Parcelize
data class WhichDeck(
    val deck : Deck? = null
) : Parcelable
@Parcelize
data class SelectedCard(
    val ct : CT?
) : Parcelable
 /** DueCards States */
data class DueDeckDetails(
    val id: Int = 0,
    var cardsLeft: Int = 0,
    val cardAmount: Int = 0,
    val reviewAmount: Int = 0,
    val nextReview : Date = Date()
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
    data object Idle : CardState()
    data object Loading : CardState()
    data object Finished : CardState()
}





