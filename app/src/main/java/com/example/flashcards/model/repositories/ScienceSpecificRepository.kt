package com.example.flashcards.model.repositories

import com.example.flashcards.model.tablesAndApplication.MathCard

interface ScienceSpecificRepository {
    suspend fun insertMathCard(mathCard: MathCard): Long
    suspend fun deleteMathCard(mathCard: MathCard)
    fun updateMathCard(
        question: String, steps: String,
        answer: String, cardId: Int
    )
}