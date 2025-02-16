package com.example.flashcards.model.daoFiles.allCardTypesDao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.flashcards.model.tablesAndApplication.MathCard

@Dao
interface MathCardDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMathCard(mathCard: MathCard): Long

    @Delete
    suspend fun deleteMathCard(mathCard: MathCard)

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