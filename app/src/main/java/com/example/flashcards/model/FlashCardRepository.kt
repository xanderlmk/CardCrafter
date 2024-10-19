package com.example.flashcards.model
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface FlashCardRepository {

    fun getAllDecksStream(): Flow<List<Deck>>

    fun getDeckStream(id: Int): Flow<Deck?>

    suspend fun insertDeck(deck: Deck)

    suspend fun deleteDeck(deck: Deck)

    suspend fun updateDeck(deck: Deck)

    suspend fun insertCard(card: Card)

    suspend fun updateCard(card: Card)

    suspend fun deleteCard(card: Card)

    fun getDeckWithCards(deckId: Int): Flow<DeckWithCards>

    suspend fun getDueCards(deckId: Int, currentTime: Long = Date().time): List<Card>

    suspend fun deleteAllCards(deckId: Int)
}