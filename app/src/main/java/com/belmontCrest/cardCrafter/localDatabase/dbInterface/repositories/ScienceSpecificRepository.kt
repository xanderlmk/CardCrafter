package com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories

import com.belmontCrest.cardCrafter.localDatabase.tables.NotationCard

interface ScienceSpecificRepository {
    suspend fun insertNotationCard(notationCard: NotationCard): Long
    suspend fun deleteNotationCard(notationCard: NotationCard)
    fun updateNotationCard(
        question: String, steps: String,
        answer: String, cardId: Int
    )
}