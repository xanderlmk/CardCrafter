package com.belmontCrest.cardCrafter.localDatabase.dbInterface.daoInterfaces.deckAndCardDao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.belmontCrest.cardCrafter.localDatabase.tables.Card
import com.belmontCrest.cardCrafter.localDatabase.tables.DeckWithCards
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface CardDao {
    @Insert(onConflict = OnConflictStrategy.Companion.ABORT)
    suspend fun insertCard(card: Card): Long

    @Query("SELECT MAX(deckCardNumber) FROM cards WHERE deckUUID = :deckUUID")
    fun getMaxDCNumber(deckUUID: String): Int?

    @Update
    suspend fun updateCard(card: Card)

    @Delete
    suspend fun deleteCard(card: Card)

    @Transaction
    @Query("SELECT * FROM decks WHERE id = :deckId")
    fun getDeckWithCards(deckId: Int): Flow<DeckWithCards>

    @Query("SELECT * FROM cards WHERE id = :cardId")
    fun getCardStream(cardId: Int) : Flow<Card>

    @Query("SELECT * FROM cards WHERE deckId = :deckId AND nextReview <= :currentTime")
    fun getDueCards(deckId: Int, currentTime: Long = Date().time): Flow<List<Card>>

    @Query(
        """
        SELECT * FROM cards WHERE deckId = :deckId 
        AND nextReview <= :currentTime 
        ORDER BY nextReview ASC, partOfList DESC
        LIMIT :cardAmount"""
    )
    suspend fun getBackupDueCards(
        deckId: Int,
        cardAmount: Int,
        currentTime: Long = Date().time
    ): List<Card>

    @Query("DELETE FROM cards WHERE deckId = :deckId")
    suspend fun deleteAllCards(deckId: Int)

    @Query("UPDATE cards SET partOfList = 1 where id = :id")
    suspend fun becomePartOfList(id: Int)

    @Query("SELECT * FROM cards WHERE id = :cardId")
    fun getCardById(cardId: Int): Card

    @Query(
        """
        update cards
        set reviewsLeft = :newReviewAmount
        where deckId = :deckId
    """
    )
    fun updateReviewAmount(newReviewAmount: Int, deckId: Int): Int

}