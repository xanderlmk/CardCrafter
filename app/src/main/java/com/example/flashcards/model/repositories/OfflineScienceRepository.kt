package com.example.flashcards.model.repositories

import com.example.flashcards.model.daoFiles.allCardTypesDao.MathCardDao
import com.example.flashcards.model.tablesAndApplication.MathCard

class OfflineScienceRepository(
    private val mathCardDao: MathCardDao
) : ScienceSpecificRepository {
    override suspend fun insertMathCard(mathCard: MathCard) =
        mathCardDao.insertMathCard(mathCard)

    override suspend fun deleteMathCard(mathCard: MathCard) =
        mathCardDao.deleteMathCard(mathCard)
    override fun updateMathCard(
        question: String, steps: String,
        answer: String, cardId: Int
    ) = mathCardDao.updateMathCard(question,steps,answer,cardId)
}