package com.example.flashcards.model.repositories
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.model.tablesAndApplication.DeckWithCards
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface FlashCardRepository {

    suspend fun checkIfDeckExists(deckName: String): Int

    suspend fun updateDeckName(newName: String, deckID: Int): Int

    fun getAllDecksStream(): Flow<List<Deck>>

    fun getDeckStream(id: Int): Flow<Deck?>

    suspend fun insertDeck(deck: Deck)

    suspend fun deleteDeck(deck: Deck)

    suspend fun updateDeck(deck: Deck)

    suspend fun insertCard(card: Card) : Long

    suspend fun updateCard(card: Card)

    suspend fun deleteCard(card: Card)

    suspend fun updateCardType(cardId: Int, type: String)

    fun getDeckWithCards(deckId: Int): Flow<DeckWithCards>

    fun getDueCards(deckId: Int,
                            currentTime: Long = Date().time): Flow<List<Card>>

    suspend fun deleteAllCards(deckId: Int)

    suspend fun getCardById(cardId : Int) : Card?


}