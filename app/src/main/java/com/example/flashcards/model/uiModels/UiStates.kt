package com.example.flashcards.model.uiModels


import com.example.flashcards.model.tablesAndApplication.AllCardTypes
import com.example.flashcards.model.tablesAndApplication.CT
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.model.tablesAndApplication.SavedCard
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
sealed class CardState {
    data object Idle : CardState()
    data object Loading : CardState()
    data object Finished : CardState()
}





