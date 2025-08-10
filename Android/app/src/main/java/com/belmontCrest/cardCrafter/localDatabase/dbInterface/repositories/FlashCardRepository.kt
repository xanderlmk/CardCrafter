package com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories

import com.belmontCrest.cardCrafter.localDatabase.tables.Card
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.model.daoHelpers.OrderBy
import com.belmontCrest.cardCrafter.model.ui.states.DueDeckDetails
import com.belmontCrest.cardCrafter.views.misc.details.CardDetails
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface FlashCardRepository {
    suspend fun checkIfDeckExists(deckName: String): Int

    suspend fun checkIfDeckExists(deckName: String, deckUUID: String): Int

    suspend fun checkIfDeckUUIDExists(deckUUID: String): Int

    suspend fun updateDeckName(newName: String, deckId: Int): Int

    fun updateDeckGoodMultiplier(newMultiplier: Double, deckId: Int): Int

    fun updateDeckBadMultiplier(newMultiplier: Double, deckId: Int): Int

    fun updateDeckReviewAmount(newReviewAmount: Int, deckId: Int): Int

    suspend fun updateNextReview(nextReview: Date, deckId: Int)

    fun updateCardReviewAmount(newReviewAmount: Int, deckId: Int): Int

    fun updateCardAmount(cardAmount: Int, deckId: Int): Int

    fun getDecksAndCC(currentTime: Long, orderBy: OrderBy): Pair<Flow<List<Deck>>, Flow<List<Int>>>

    suspend fun getAllDecks(): List<Deck>

    fun getDeck(id: Int): Deck

    fun getDeckName(id: Int): Flow<String?>

    fun resetCardLefts()

    suspend fun insertDeck(deck: Deck): Long

    suspend fun deleteDeck(deck: Deck)

    suspend fun deleteDeckById(id: Int)

    suspend fun updateDeck(deck: Deck)

    suspend fun insertCard(card: Card): Long

    suspend fun getMaxDCNumber(deckUUID: String): Int?

    suspend fun updateCard(card: Card)

    suspend fun deleteCard(card: Card)

    fun getCardStream(cardId: Int): Flow<Card>

    suspend fun getDueCards(
        deckId: Int,
        currentTime: Long = Date().time
    ): Flow<List<Card>>

    suspend fun deleteAllCards(deckId: Int)

    fun getCardById(cardId: Int): Card

    suspend fun getBackupDueCards(deckId: Int, cardAmount: Int): List<Card>

    suspend fun becomePartOfList(id: Int)

    fun updateCardsLeft(deckId: Int, cardsLeft: Int, cardsDone: Int)

    fun getDueDeckDetails(id: Int): Flow<DueDeckDetails?>

    suspend fun insertBasicCard(
        deck: Deck, basicCD: CardDetails.BasicCD, isOwnerOrCoOwner: Boolean
    )

    suspend fun insertThreeCard(
        deck: Deck, threeCD: CardDetails.ThreeCD, isOwnerOrCoOwner: Boolean
    )

    suspend fun insertHintCard(
        deck: Deck, hintCD: CardDetails.HintCD, isOwnerOrCoOwner: Boolean
    )

    suspend fun insertMultiCard(
        deck: Deck, multiCD: CardDetails.MultiCD, isOwnerOrCoOwner: Boolean
    )

    suspend fun insertNotationCard(
        deck: Deck, notationCD: CardDetails.NotationCD, isOwnerOrCoOwner: Boolean
    )

    suspend fun insertCustomCard(
        deck: Deck, customCD: CardDetails.CustomCD, type: String, isOwnerOrCoOwner: Boolean
    )
}