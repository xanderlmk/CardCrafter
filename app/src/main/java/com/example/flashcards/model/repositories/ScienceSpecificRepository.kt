package com.example.flashcards.model.repositories

import com.example.flashcards.model.tablesAndApplication.NotationCard

interface ScienceSpecificRepository {
    suspend fun insertNotationCard(notationCard: NotationCard): Long
    suspend fun deleteNotationCard(notationCard: NotationCard)
    fun updateNotationCard(
        question: String, steps: String,
        answer: String, cardId: Int
    )
}