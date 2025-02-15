package com.example.flashcards.model.repositories

import com.example.flashcards.model.tablesAndApplication.MathCard

interface ScienceSpecificRepository {
    fun insertMathCard(mathCard: MathCard): Long
    fun deleteMathCard(cardId: Int)
    fun updateMathCard(
        question: String, steps: String,
        answer: String, cardId: Int
    )
}