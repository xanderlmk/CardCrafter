package com.example.flashcards.model.daoFiles.allCardTypesDao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.flashcards.model.tablesAndApplication.HintCard

@Dao
interface HintCardDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertHintCard(hintCard: HintCard) : Long

    @Query("DELETE FROM hintCard WHERE cardId = :cardId")
    suspend fun deleteHintCard(cardId: Int)

    @Query("""
        Update hintCard
        Set question = :newQuestion, 
        hint = :newHint,
        answer = :newAnswer
        where cardId = :id""")
    suspend fun updateHintCard(
        id: Int,
        newQuestion: String,
        newHint: String,
        newAnswer: String
    )
}