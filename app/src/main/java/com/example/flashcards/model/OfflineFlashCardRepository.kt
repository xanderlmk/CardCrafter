package com.example.flashcards.model
import kotlinx.coroutines.flow.Flow

class OfflineFlashCardRepository (private val deckDao: DeckDao) : FlashCardRepository {
    override fun getAllDecksStream(): Flow<List<Decks>> = deckDao.getAllDecks()

    override fun getDeckStream(id: Int): Flow<Decks?> = deckDao.getDeck(id)

    override suspend fun insertDeck(decks: Decks) = deckDao.insert(decks)

    override suspend fun deleteDeck(decks: Decks) = deckDao.delete(decks)

    override suspend fun updateDeck(decks: Decks) = deckDao.update(decks)
}