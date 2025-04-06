package com.belmontCrest.cardCrafter.model.databaseInterface.daoInterfaces.allCardTypesDao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.belmontCrest.cardCrafter.model.tablesAndApplication.NotationCard

@Dao
interface NotationCardDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertNotationCard(notationCard: NotationCard): Long

    @Delete
    suspend fun deleteNotationCard(notationCard: NotationCard)

    @Query("""
        UPDATE notationCard
        set question = :question,
        steps = :steps,
        answer = :answer
        where cardId = :cardId
        """)
    fun updateNotationCard(
        question: String, steps: String,
        answer: String, cardId: Int
    )
}