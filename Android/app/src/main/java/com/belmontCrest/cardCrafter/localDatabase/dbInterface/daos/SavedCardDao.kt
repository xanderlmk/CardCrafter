package com.belmontCrest.cardCrafter.localDatabase.dbInterface.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.belmontCrest.cardCrafter.localDatabase.tables.Card
import com.belmontCrest.cardCrafter.localDatabase.tables.CardRemains
import com.belmontCrest.cardCrafter.localDatabase.tables.SavedCard
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedCardDao {

    @Query("SELECT * from saved_card ORDER BY id DESC")
    fun getAllSavedCards(): Flow<List<SavedCard>>

    @Insert(onConflict = OnConflictStrategy.Companion.ABORT)
    fun insertSavedCard(savedCard: SavedCard)

    @Query(
        """
        update cards
        set reviewsLeft = :reviewsLeft,
        nextReview = :nextReview,
        passes = :passes,
        prevSuccess = :prevSuccess,
        totalPasses = :totalPasses,
        partOfList = :partOfList
        where id = :cardId
    """
    )
    fun updateCardsOnStart(
        cardId: Int,
        reviewsLeft: Int,
        nextReview: Long,
        passes: Int,
        prevSuccess: Boolean,
        totalPasses: Int,
        partOfList : Boolean
    )
    @Query("DELETE FROM saved_card")
    suspend fun deleteSavedCards()

    @Query("""
        SELECT deckId, deckUUID,type, createdOn, deckCardNumber,cardIdentifier
        FROM cards WHERE id = :cardId
        """)
    suspend fun getCardRemains(cardId: Int): CardRemains

    @Update
    suspend fun updateCard(card: Card)

    @Delete
    suspend fun deleteSavedCard(savedCard: SavedCard)

    @Transaction
    suspend fun getCardAndRemoveSavedOne(card: Card, savedCard: SavedCard) {
        updateCard(card); deleteSavedCard(savedCard)
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