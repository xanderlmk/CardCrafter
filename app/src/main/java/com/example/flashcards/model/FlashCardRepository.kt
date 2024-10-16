package com.example.flashcards.model
import kotlinx.coroutines.flow.Flow

interface FlashCardRepository {

    fun getAllDecksStream(): Flow<List<Decks>>

    fun getDeckStream(id: Int): Flow<Decks?>

    suspend fun insertDeck(decks: Decks)

    suspend fun deleteDeck(decks: Decks)

    suspend fun updateDeck(decks: Decks)
}