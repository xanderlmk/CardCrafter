package com.example.flashcards.model.repositories
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.model.tablesAndApplication.DeckWithCards
import com.example.flashcards.model.tablesAndApplication.SavedCard
import com.example.flashcards.model.uiModels.DueDeckDetails
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface FlashCardRepository {

    suspend fun checkIfDeckExists(deckName: String): Int

    suspend fun updateDeckName(newName: String, deckId: Int): Int

    fun updateDeckGoodMultiplier(newMultiplier : Double, deckId: Int): Int

    fun updateDeckBadMultiplier(newMultiplier : Double, deckId: Int): Int

    fun updateDeckReviewAmount(newReviewAmount : Int, deckId: Int): Int

    suspend fun updateNextReview(nextReview: Date, deckId: Int)

    fun updateCardReviewAmount(newReviewAmount : Int, deckId: Int): Int

    fun updateCardAmount(cardAmount : Int, deckId: Int) : Int

    fun getAllDecksStream(): Flow<List<Deck>>

    fun getDeckStream(id: Int): Flow<Deck>

    fun getDeck(id: Int): Deck

    fun getCardCount(currentTime: Long): Flow<List<Int>>

    fun resetCardLefts()

    suspend fun insertDeck(deck: Deck) : Long

    suspend fun deleteDeck(deck: Deck)

    suspend fun updateDeck(deck: Deck)

    suspend fun insertCard(card: Card) : Long

    suspend fun updateCard(card: Card)

    suspend fun deleteCard(card: Card)

    suspend fun updateCardType(cardId: Int, type: String)

    fun getCardStream(cardId: Int) : Flow<Card>

    fun getDeckWithCards(deckId: Int): Flow<DeckWithCards>

    suspend fun getDueCards(deckId: Int,
                            currentTime: Long = Date().time): Flow<List<Card>>

    suspend fun deleteAllCards(deckId: Int)

    suspend fun getCardById(cardId : Int) : Card

    suspend fun getBackupDueCards(deckId: Int, cardAmount : Int) : List<Card>

    fun updateSavedCards(
        cardId: Int,
        reviewsLeft: Int,
        nextReview: Long,
        passes: Int,
        prevSuccess: Boolean,
        totalPasses: Int,
        partOfList : Boolean
    )
    fun deleteSavedCards()
    fun insertSavedCard(savedCard: SavedCard)
    fun getAllSavedCards(): Flow<List<SavedCard>>

    suspend fun becomePartOfList(id: Int)

    fun updateCardsLeft(deckId: Int, cardsLeft : Int)

    fun getDueDeckDetails(id: Int): Flow<DueDeckDetails>
}