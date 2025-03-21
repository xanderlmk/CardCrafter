package com.example.flashcards.model.repositories

import com.example.flashcards.model.daoFiles.allCardTypesDao.NotationCardDao
import com.example.flashcards.model.tablesAndApplication.NotationCard

class OfflineScienceRepository(
    private val notationCardDao: NotationCardDao
) : ScienceSpecificRepository {
    override suspend fun insertNotationCard(notationCard: NotationCard) =
        notationCardDao.insertNotationCard(notationCard)

    override suspend fun deleteNotationCard(notationCard: NotationCard) =
        notationCardDao.deleteNotationCard(notationCard)
    override fun updateNotationCard(
        question: String, steps: String,
        answer: String, cardId: Int
    ) = notationCardDao.updateNotationCard(question,steps,answer,cardId)
}