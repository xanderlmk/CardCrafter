package com.belmontCrest.cardCrafter.localDatabase.dbInterface.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.belmontCrest.cardCrafter.localDatabase.tables.Card
import com.belmontCrest.cardCrafter.localDatabase.tables.SavedCard
import java.util.Date

@Dao
interface DueCardsDao {
    @Query(
        """
        update decks
        set nextReview = :nextReview
        where id = :deckId
    """
    )
    suspend fun updateNextReview(nextReview: Date, deckId: Int)

    @Transaction
    suspend fun updateDeckAndCard(
        id: Int, nextReview: Date, cardsLeft: Int, cardsDone: Int, card: Card, savedCard: SavedCard
    ) {
        updateCard(card)
        insertSavedCard(savedCard)
        updateCardsLeft(id, cardsLeft = cardsLeft, cardsDone)
        updateNextReview(nextReview, id)
    }

    @Query(
        """ 
        UPDATE decks 
        SET cardsLeft = :cardsLeft,
        cardsDone = :cardsDone
        WHERE id = :deckId
    """
    )
    fun updateCardsLeft(deckId: Int, cardsLeft: Int, cardsDone: Int)


    @Update
    suspend fun updateCard(card: Card)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSavedCard(savedCard: SavedCard)

    @Delete
    suspend fun deleteSavedCard(savedCard: SavedCard)

    @Transaction
    suspend fun getCardAndRemoveSavedOne(
        card: Card, savedCard: SavedCard, id: Int, cardsLeft: Int, cardsDone: Int, nextReview: Date
    ) {
        updateCard(card)
        deleteSavedCard(savedCard)
        updateCardsLeft(id, cardsLeft = cardsLeft, cardsDone)
        updateNextReview(nextReview, id)
    }

    @Transaction
    suspend fun updateCardWithSavedCard(card: Card, savedCard: SavedCard) {
        try {
            updateCard(card); insertSavedCard(savedCard)
        } catch (e: Exception) {
            throw e
        }
    }
}