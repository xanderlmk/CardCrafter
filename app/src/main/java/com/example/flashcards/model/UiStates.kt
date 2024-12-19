package com.example.flashcards.model

import com.example.flashcards.model.tablesAndApplication.AllCardTypes
import com.example.flashcards.model.tablesAndApplication.BasicCardType
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.model.tablesAndApplication.HintCardType
import com.example.flashcards.model.tablesAndApplication.ThreeCardType

data class MainUiState(
    val deckList: List<Deck> = listOf()
)
data class CardListUiState(
    var allCards: List<AllCardTypes> = emptyList(),
    var errorMessage: String = ""
)
data class CardUiState(
    var cardList: List<Card> = emptyList()
)
data class BasicCardUiState(
    var basicCards: List<BasicCardType> = emptyList(),
    var errorMessage: String = ""
)
data class HintCardUiState(
    var hintCards: List<HintCardType> = emptyList(),
    var errorMessage: String = ""
)
data class ThreeCardUiState(
    var threeFieldCards: List<ThreeCardType> = emptyList(),
    var errorMessage: String = ""
)

