package com.belmontCrest.cardCrafter.model.repositories

import com.belmontCrest.cardCrafter.model.daoFiles.allCardTypesDao.NotationCardDao
import com.belmontCrest.cardCrafter.model.tablesAndApplication.NotationCard

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