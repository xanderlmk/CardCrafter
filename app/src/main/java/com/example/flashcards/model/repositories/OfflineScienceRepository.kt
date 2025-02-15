package com.example.flashcards.model.repositories

import com.example.flashcards.model.daoFiles.allCardTypesDao.MathCardDao
import com.example.flashcards.model.tablesAndApplication.MathCard

class OfflineScienceRepository(
    private val mathCardDao: MathCardDao
) : ScienceSpecificRepository {
    override fun insertMathCard(mathCard: MathCard) =
        mathCardDao.insertMathCard(mathCard)

    override fun deleteMathCard(cardId: Int) =
        mathCardDao.deleteMathCard(cardId)

    override fun updateMathCard(
        question: String, steps: String,
        answer: String, cardId: Int
    ) = mathCardDao.updateMathCard(question,steps,answer,cardId)
}