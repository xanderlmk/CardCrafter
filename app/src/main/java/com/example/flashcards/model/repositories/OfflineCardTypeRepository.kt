package com.example.flashcards.model.repositories

import com.example.flashcards.model.daoFiles.allCardTypesDao.BasicCardDao
import com.example.flashcards.model.tablesAndApplication.BasicCard
import com.example.flashcards.model.tablesAndApplication.ThreeFieldCard
import com.example.flashcards.model.daoFiles.deckAndCardDao.CardTypesDao
import com.example.flashcards.model.daoFiles.allCardTypesDao.HintCardDao
import com.example.flashcards.model.daoFiles.allCardTypesDao.MultiChoiceCardDao
import com.example.flashcards.model.daoFiles.allCardTypesDao.ThreeCardDao
import com.example.flashcards.model.tablesAndApplication.HintCard
import com.example.flashcards.model.tablesAndApplication.MultiChoiceCard

class OfflineCardTypeRepository(
    private val cardTypesDao: CardTypesDao,
    private val basicCardDao: BasicCardDao,
    private val hintCardDao: HintCardDao,
    private val threeCardDao: ThreeCardDao,
    private val multiChoiceCardDao: MultiChoiceCardDao
) : CardTypeRepository {

    override suspend fun insertBasicCard(basicCard: BasicCard) =
        basicCardDao.insertBasicCard(basicCard)

    override suspend fun insertThreeCard(threeFieldCard: ThreeFieldCard) =
        threeCardDao.insertThreeCard(threeFieldCard)

    override suspend fun insertHintCard(hintCard: HintCard) =
        hintCardDao.insertHintCard(hintCard)

    override suspend fun insertMultiChoiceCard(multiChoiceCard: MultiChoiceCard) =
        multiChoiceCardDao.insertMultiChoiceCard(multiChoiceCard)

    override suspend fun deleteBasicCard(basicCard: BasicCard) =
        basicCardDao.deleteBasicCard(basicCard)

    override suspend fun deleteHintCard(hintCard: HintCard) =
        hintCardDao.deleteHintCard(hintCard)

    override suspend fun deleteThreeCard(threeFieldCard: ThreeFieldCard) =
        threeCardDao.deleteThreeCard(threeFieldCard)

    override suspend fun deleteMultiChoiceCard(multiChoiceCard: MultiChoiceCard) =
        multiChoiceCardDao.deleteMultiChoiceCard(multiChoiceCard)

    override suspend fun updateBasicCard(id: Int, question: String, answer: String) =
        basicCardDao.updateBasicCard(id, question, answer)

    override suspend fun updateThreeCard(
        id: Int,
        question: String,
        middle: String,
        answer: String
    ) = threeCardDao.updateThreeCard(id, question, middle, answer)

    override suspend fun updateHintCard(
        id: Int,
        question: String,
        hint: String,
        answer: String
    ) = hintCardDao.updateHintCard(id, question, hint, answer)

    override suspend fun updateMultiChoiceCard(
        id: Int,
        newQuestion: String,
        newChoiceA: String,
        newChoiceB: String,
        newChoiceC: String,
        newChoiceD: String,
        newCorrect: Char
    ) = multiChoiceCardDao.updateMultiChoiceCard(
        id, newQuestion, newChoiceA, newChoiceB,
        newChoiceC, newChoiceD, newCorrect
    )

    override fun getAllCardTypes(deckId: Int) = cardTypesDao.getAllCardTypes(deckId)

    override fun getAllDueCards(
        deckId: Int,
        cardAmount: Int,
        currentTime: Long
    ) = cardTypesDao.getDueAllCardTypes(deckId, cardAmount, currentTime)

    override suspend fun getACardType(id: Int) = cardTypesDao.getACardType(id)
}