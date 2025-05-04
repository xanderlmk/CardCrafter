package com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories

import com.belmontCrest.cardCrafter.localDatabase.dbInterface.daoInterfaces.allCardTypesDao.NotationCardDao
import com.belmontCrest.cardCrafter.localDatabase.tables.NotationCard

class OfflineScienceRepository(
    private val notationCardDao: NotationCardDao
) : ScienceSpecificRepository {

    override suspend fun deleteNotationCard(notationCard: NotationCard) =
        notationCardDao.deleteNotationCard(notationCard)
    override fun updateNotationCard(
        question: String, steps: String,
        answer: String, cardId: Int
    ) = notationCardDao.updateNotationCard(question,steps,answer,cardId)
}