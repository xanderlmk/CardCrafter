package com.example.flashcards.model.repositories

import com.example.flashcards.model.daoFiles.BasicCardDao
import com.example.flashcards.model.tablesAndApplication.BasicCard
import com.example.flashcards.model.tablesAndApplication.ThreeFieldCard
import com.example.flashcards.model.daoFiles.CardTypesDao
import com.example.flashcards.model.daoFiles.HintCardDao
import com.example.flashcards.model.daoFiles.MultiChoiceCardDao
import com.example.flashcards.model.daoFiles.ThreeCardDao
import com.example.flashcards.model.tablesAndApplication.HintCard
import com.example.flashcards.model.tablesAndApplication.MultiChoiceCard
import com.example.flashcards.model.tablesAndApplication.MultiChoiceCardType
import kotlinx.coroutines.flow.Flow

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

    override fun getBasicCard(cardId: Int) = basicCardDao.getBasicCard(cardId)
    override fun getHintCard(cardId: Int) = hintCardDao.getHintCard(cardId)
    override fun getThreeCard(cardId: Int) = threeCardDao.getThreeCard(cardId)
    override fun getMultiChoiceCard(cardId: Int) =
        multiChoiceCardDao.getMultiChoiceCard(cardId)

    override suspend fun deleteBasicCard(cardId: Int) =
        basicCardDao.deleteBasicCard(cardId)

    override suspend fun deleteHintCard(cardId: Int) =
        hintCardDao.deleteHintCard(cardId)

    override suspend fun deleteThreeCard(cardId: Int) =
        threeCardDao.deleteThreeCard(cardId)

    override suspend fun deleteMultiChoiceCard(cardId: Int) =
        multiChoiceCardDao.deleteMultiChoiceCard(cardId)

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

    override fun getAllBasicCards(deckId: Int) = basicCardDao.getAllBasicCards(deckId)
    override fun getAllThreeCards(deckId: Int) = threeCardDao.getAllThreeCards(deckId)
    override fun getAllHintCards(deckId: Int) = hintCardDao.getAllHintCards(deckId)
    override fun getAllMultiChoiceCards(deckId: Int) =
        multiChoiceCardDao.getAllMultiChoiceCards(deckId)

    override fun getAllCardTypes(deckId: Int) = cardTypesDao.getAllCardTypes(deckId)

    override fun getDueBasicCards(deckId: Int, currentTime: Long) =
        basicCardDao.getDueBasicCards(deckId, currentTime)

    override fun getDueThreeCards(deckId: Int, currentTime: Long) =
        threeCardDao.getDueThreeCards(deckId, currentTime)

    override fun getDueHintCards(deckId: Int, currentTime: Long) =
        hintCardDao.getDueHintCards(deckId, currentTime)

    override fun getDueMultiChoiceCards(
        deckId: Int,
        currentTime: Long
    ): Flow<List<MultiChoiceCardType>> =
        multiChoiceCardDao.getDueMultiChoiceCards(deckId)

    override suspend fun getDueAllCardTypes(
        deckId: Int,
        cardAmount: Int,
        currentTime: Long
    ) =
        cardTypesDao.getDueAllCardTypes( deckId, cardAmount, currentTime)

    override suspend fun getACardType(id: Int) = cardTypesDao.getACardType(id)

    override suspend fun getCurrentDueAllCardTypes(
        deckId: Int,
        currentTime: Long
    ) = cardTypesDao.getCurrentDueAllCardTypes(deckId)
}