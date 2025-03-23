package com.belmontCrest.cardCrafter.model.repositories

import com.belmontCrest.cardCrafter.model.tablesAndApplication.NotationCard

interface ScienceSpecificRepository {
    suspend fun insertNotationCard(notationCard: NotationCard): Long
    suspend fun deleteNotationCard(notationCard: NotationCard)
    fun updateNotationCard(
        question: String, steps: String,
        answer: String, cardId: Int
    )
}