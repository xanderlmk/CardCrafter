package com.belmontCrest.cardCrafter.model.uiModels


import android.os.Parcelable
import com.belmontCrest.cardCrafter.model.tablesAndApplication.AllCardTypes
import com.belmontCrest.cardCrafter.model.tablesAndApplication.CT
import com.belmontCrest.cardCrafter.model.tablesAndApplication.Deck
import com.belmontCrest.cardCrafter.model.tablesAndApplication.SavedCard
import kotlinx.parcelize.Parcelize
import java.util.Date

/** MainViewModel States */
data class DeckUiState(
    val deckList: List<Deck> = listOf()
)
data class CardListUiCount(
    val cardListCount: List<Int> = listOf()
)
/** EditingListViewModel States */
data class CardListUiState(
    var allCards: List<AllCardTypes> = emptyList(),
    var errorMessage: String = ""
)
data class SealedAllCTs(
    var allCTs: MutableList<CT> = mutableListOf(),
    var errorMessage: String = ""
)
 /** DueCards States */
data class DueDeckDetails(
    val id: Int = 0,
    var cardsLeft: Int = 0,
    val nextReview : Date = Date()
)
data class SealedDueCTs(
    var allCTs: MutableList<CT> = mutableListOf(),
    var savedCTs: MutableList<CT> = mutableListOf(),
)
data class SavedCardUiState(
    var savedCards: List<SavedCard> = emptyList()
)
@Parcelize
sealed class CardState : Parcelable {
    data object Idle : CardState()
    data object Loading : CardState()
    data object Finished : CardState()
}





