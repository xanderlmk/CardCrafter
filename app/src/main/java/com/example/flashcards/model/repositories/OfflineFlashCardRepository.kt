package com.example.flashcards.model.repositories
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.model.tablesAndApplication.DeckWithCards
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.model.daoFiles.CardDao
import com.example.flashcards.model.daoFiles.DeckDao
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

    override suspend fun updateCardType(cardId: Int, type:  String) =
        cardDao.updateCard(cardId, type)

    override fun getDeckStream(id: Int): Flow<Deck?> = deckDao.getDeck(id)

    override suspend fun insertDeck(deck: Deck) = deckDao.insertDeck(deck)

    override suspend fun deleteDeck(deck: Deck) = deckDao.deleteDeck(deck)

    override suspend fun updateDeck(deck: Deck) = deckDao.updateDeck(deck)

    override suspend fun updateDeckName(newName: String, deckID: Int): Int =
        try {
            deckDao.updateDeckName(newName, deckID)
        } catch (e: Exception) {
            throw e
        }

    override suspend fun insertCard(card: Card) = cardDao.insertCard(card)

    override suspend fun updateCard(card: Card) = cardDao.updateCard(card)

    override suspend fun deleteCard(card: Card) = cardDao.deleteCard(card)

    override fun getDeckWithCards(deckId: Int):
            Flow<DeckWithCards> = cardDao.getDeckWithCards(deckId)

    override fun getDueCards(deckId: Int, currentTime: Long):
            Flow<List<Card>> = cardDao.getDueCards(deckId, currentTime)

    override suspend fun deleteAllCards(deckId: Int) = cardDao.deleteAllCards(deckId)

}