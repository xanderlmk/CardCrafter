package com.example.flashcards.model.daoFiles.deckAndCardDao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Update
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.flashcards.model.tablesAndApplication.DeckWithCards
import com.example.flashcards.model.tablesAndApplication.Card
import kotlinx.coroutines.flow.Flow
import java.util.Date


@Dao
interface CardDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCard(card: Card): Long

    @Update
    suspend fun updateCard(card: Card)

    @Delete
    suspend fun deleteCard(card: Card)

    @Transaction
    @Query("SELECT * FROM decks WHERE id = :deckId")
    fun getDeckWithCards(deckId: Int): Flow<DeckWithCards>

    @Query("SELECT * FROM cards WHERE deckId = :deckId AND nextReview <= :currentTime")
    fun getDueCards(deckId: Int, currentTime: Long = Date().time): Flow<List<Card>>

    @Query(
        """
        SELECT * FROM cards WHERE deckId = :deckId 
        AND nextReview <= :currentTime LIMIT :cardAmount"""
    )
    suspend fun getBackupDueCards(
        deckId: Int,
        cardAmount: Int,
        currentTime: Long = Date().time
    ): List<Card>

    @Query("DELETE FROM cards WHERE deckId = :deckId")
    suspend fun deleteAllCards(deckId: Int)

    @Query("Update cards set id = :cardId and type = :type")
    suspend fun updateCard(cardId: Int, type: String)

    @Query("UPDATE cards SET partOfList = 1 where id = :id")
    suspend fun becomePartOfList(id: Int)

    @Query("SELECT * FROM cards WHERE id = :cardId")
    suspend fun getCardById(cardId: Int): Card


    @Query(
        """
        update cards
        set reviewsLeft = :newReviewAmount
        where deckId = :deckId
    """
    )
    fun updateReviewAmount(newReviewAmount: Int, deckId: Int): Int

}
/**
 * data class Card(
 *     @PrimaryKey(autoGenerate = true) val id: Int = 0,
 *     var deckId : Int,
 *     val deckUUID: String,
 *     var reviewsLeft : Int,
 *     var nextReview: Date?,
 *     var passes: Int = 0,
 *     var prevSuccess: Boolean,
 *     var totalPasses: Int = 0,
 *     val type: String,
 *     val createdOn: Long = Date().time
 * )
 */
