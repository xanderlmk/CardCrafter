package com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories

import android.util.Log
import com.belmontCrest.cardCrafter.controller.cardHandlers.mapAllCardTypesToCTs
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.daoInterfaces.allCardTypesDao.BasicCardDao
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.daoInterfaces.allCardTypesDao.HintCardDao
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.daoInterfaces.allCardTypesDao.MultiChoiceCardDao
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.daoInterfaces.allCardTypesDao.ThreeCardDao
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.daoInterfaces.deckAndCardDao.CardTypesDao
import com.belmontCrest.cardCrafter.localDatabase.tables.BasicCard
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.localDatabase.tables.HintCard
import com.belmontCrest.cardCrafter.localDatabase.tables.MultiChoiceCard
import com.belmontCrest.cardCrafter.localDatabase.tables.ThreeFieldCard
import com.belmontCrest.cardCrafter.model.uiModels.Fields
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


    override fun getACardType(id: Int) = cardTypesDao.getACardType(id)

    override suspend fun updateCT(
        cardId: Int, type: String, fields: Fields,
        deleteCT: CT
    ) = cardTypesDao.updateCT(cardId, type, fields, deleteCT)
}