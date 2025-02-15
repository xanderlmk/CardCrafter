package com.example.flashcards.model.daoFiles.allCardTypesDao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.flashcards.model.tablesAndApplication.MathCard

@Dao
interface MathCardDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertMathCard(mathCard: MathCard): Long

    @Query("DELETE FROM mathCard where cardId = :cardId")
    fun deleteMathCard(cardId: Int)

    @Query("""
        UPDATE mathCard
        set question = :question,
        steps = :steps,
        answer = :answer
        where cardId = :cardId
        """)
    fun updateMathCard(
        question: String, steps: String,
        answer: String, cardId: Int
    )
}