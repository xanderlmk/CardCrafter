package com.example.flashcards.model.uiModels



import com.example.flashcards.model.tablesAndApplication.AllCardTypes
import com.example.flashcards.model.tablesAndApplication.BasicCardType
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.model.tablesAndApplication.HintCardType
import com.example.flashcards.model.tablesAndApplication.MultiChoiceCardType
import com.example.flashcards.model.tablesAndApplication.SavedCard
import com.example.flashcards.model.tablesAndApplication.ThreeCardType


data class DeckUiState(
    val deckList: List<Deck> = listOf()
)
data class CardListUiCount(
    val cardListCount : List<Int> = listOf()
)
data class CardListUiState(
    var allCards: List<AllCardTypes> = emptyList(),
    var errorMessage: String = ""
)
data class CardDeckCardLists(
    var allCards: List<AllCardTypes> = emptyList(),
    var savedCardList: List<AllCardTypes> = emptyList(),
    var collected : Boolean = false,
    var errorMessage: String = ""
)
data class SavedCardUiState(
    var savedCards : List<SavedCard> = emptyList()
)
data class BasicCardUiState(
    var basicCards: List<BasicCardType> = emptyList(),
    var errorMessage: String = "",
)
data class HintCardUiState(
    var hintCards: List<HintCardType> = emptyList(),
    var errorMessage: String = "",
)
data class ThreeCardUiState(
    var threeFieldCards: List<ThreeCardType> = emptyList(),
    var errorMessage: String = "",
)
data class MultiChoiceUiCardState(
    var multiChoiceCard: List<MultiChoiceCardType> = emptyList(),
    var errorMessage: String = "",
)

sealed class CardState {
    data object Idle : CardState()
    data object Loading : CardState()
    //data class ShowingCard(val card: Card) : CardState()
    data object Finished : CardState()
    //data class Error(val message: String) : CardState()
}





