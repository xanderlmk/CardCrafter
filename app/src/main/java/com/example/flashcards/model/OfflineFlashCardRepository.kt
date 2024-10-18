package com.example.flashcards.model
import kotlinx.coroutines.flow.Flow

class OfflineFlashCardRepository (private val deckDao: DeckDao,
    private val cardDao: CardDao) : FlashCardRepository {
    override fun getAllDecksStream(): Flow<List<Decks>> = deckDao.getAllDecks()

    override fun getDeckStream(id: Int): Flow<Decks?> = deckDao.getDeck(id)

    override suspend fun insertDeck(decks: Decks) = deckDao.insert(decks)

    override suspend fun deleteDeck(decks: Decks) = deckDao.delete(decks)

    override suspend fun updateDeck(decks: Decks) = deckDao.update(decks)

    override suspend fun insert(card: Card) = cardDao.insert(card)

    override suspend fun update(card: Card) = cardDao.update(card)

    override suspend fun delete(card: Card) = cardDao.delete(card)

    override fun getDeckWithCards(deckId: Int):
            Flow<DeckWithCards> = cardDao.getDeckWithCards(deckId)
}