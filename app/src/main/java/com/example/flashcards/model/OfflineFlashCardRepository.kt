package com.example.flashcards.model
import kotlinx.coroutines.flow.Flow

class OfflineFlashCardRepository(
    private val deckDao: DeckDao,
    private val cardDao: CardDao
) : FlashCardRepository {

    override suspend fun checkIfDeckExists(deckName: String): Int =
        try {
            deckDao.checkIfDeckExists(deckName)
        } catch (e: Exception) {
            throw e
        }

    override fun getAllDecksStream(): Flow<List<Deck>> = deckDao.getAllDecks()

    override fun getDeckStream(id: Int): Flow<Deck?> = deckDao.getDeck(id)

    override suspend fun insertDeck(deck: Deck) = deckDao.insertDeck(deck)

    override suspend fun deleteDeck(deck: Deck) = deckDao.deleteDeck(deck)

    override suspend fun updateDeck(deck: Deck) = deckDao.updateDeck(deck)

    override suspend fun insertCard(card: Card) = cardDao.insertCard(card)

    override suspend fun updateCard(card: Card) = cardDao.updateCard(card)

    override suspend fun deleteCard(card: Card) = cardDao.deleteCard(card)

    override fun getDeckWithCards(deckId: Int):
            Flow<DeckWithCards> = cardDao.getDeckWithCards(deckId)

    override fun getDueCards(deckId: Int, currentTime: Long):
            Flow<List<Card>> = cardDao.getDueCards(deckId, currentTime)

    override suspend fun deleteAllCards(deckId: Int) = cardDao.deleteAllCards(deckId)
}