package com.belmontCrest.cardCrafter.model.repositories

import android.util.Log
import com.belmontCrest.cardCrafter.controller.cardHandlers.mapAllCardTypesToCTs
import com.belmontCrest.cardCrafter.model.daoFiles.allCardTypesDao.BasicCardDao
import com.belmontCrest.cardCrafter.model.tablesAndApplication.BasicCard
import com.belmontCrest.cardCrafter.model.tablesAndApplication.ThreeFieldCard
import com.belmontCrest.cardCrafter.model.daoFiles.deckAndCardDao.CardTypesDao
import com.belmontCrest.cardCrafter.model.daoFiles.allCardTypesDao.HintCardDao
import com.belmontCrest.cardCrafter.model.daoFiles.allCardTypesDao.MultiChoiceCardDao
import com.belmontCrest.cardCrafter.model.daoFiles.allCardTypesDao.ThreeCardDao
import com.belmontCrest.cardCrafter.model.tablesAndApplication.CT
import com.belmontCrest.cardCrafter.model.tablesAndApplication.HintCard
import com.belmontCrest.cardCrafter.model.tablesAndApplication.MultiChoiceCard
import kotlinx.coroutines.flow.map

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
    ) = cardTypesDao.getDueAllCardTypesFlow(deckId, cardAmount, currentTime).map {
        try {
            mapAllCardTypesToCTs(it)
        } catch (e: IllegalStateException) {
            Log.d("CardTypeRepository", "$e")
            listOf<CT>()
        }
    }

    override fun getDueAllCardTypes(
        deckId: Int, cardAmount: Int, currentTime: Long
    ) = try {
        mapAllCardTypesToCTs(
            cardTypesDao.getDueAllCardTypes(deckId, cardAmount, currentTime)
        )
    } catch (e: IllegalStateException) {
        Log.d("CardTypeRepository", "$e")
        listOf<CT>()
    }


    override suspend fun getACardType(id: Int) = cardTypesDao.getACardType(id)
}