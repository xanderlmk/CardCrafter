package com.example.flashcards.model.repositories
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.model.tablesAndApplication.DeckWithCards
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.model.daoFiles.CardDao
import com.example.flashcards.model.daoFiles.DeckDao
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce

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

    override fun updateDeckGoodMultiplier(newMultiplier: Double, deckId: Int) =
        try{
            deckDao.updateDeckGoodMultiplier(newMultiplier, deckId)
        } catch(e: Exception){
            throw  e
        }

    override fun updateDeckBadMultiplier(newMultiplier: Double, deckId: Int) =
        try{
            deckDao.updateDeckBadMultiplier(newMultiplier, deckId)
        } catch(e: Exception){
            throw  e
        }

    override suspend fun insertCard(card: Card) = cardDao.insertCard(card)

    override suspend fun updateCard(card: Card) = cardDao.updateCard(card)

    override suspend fun deleteCard(card: Card) = cardDao.deleteCard(card)

    @OptIn(FlowPreview::class)
    override fun getDeckWithCards(deckId: Int):
            Flow<DeckWithCards> = cardDao.getDeckWithCards(deckId).debounce(20)

    override suspend fun getDueCards(deckId: Int, currentTime: Long):
            Flow<List<Card>> = cardDao.getDueCards(deckId, currentTime)

    override suspend fun deleteAllCards(deckId: Int) = cardDao.deleteAllCards(deckId)

    override suspend fun getCardById(cardId : Int) = cardDao.getCardById(cardId)

}