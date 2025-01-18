package com.example.flashcards.model.daoFiles

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.flashcards.model.tablesAndApplication.SavedCard
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedCardDao {

    @Query("SELECT * from savedCards")
    fun getAllSavedCards(): Flow<List<SavedCard>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSavedCard(savedCard: SavedCard)
    @Query(
        """
        update cards
        set reviewsLeft = :reviewsLeft,
        nextReview = :nextReview,
        passes = :passes,
        prevSuccess = :prevSuccess,
        totalPasses = :totalPasses
        where id = :cardId
    """
    )
    fun updateCardsOnStart(
        cardId: Int,
        reviewsLeft: Int,
        nextReview: Long,
        passes: Int,
        prevSuccess: Boolean,
        totalPasses: Int
    )
    @Query("DELETE FROM savedCards")
    fun deleteSavedCards()
}