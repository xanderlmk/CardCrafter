package com.example.flashcards.model.repositories

import com.example.flashcards.model.tablesAndApplication.BasicCard
import com.example.flashcards.model.tablesAndApplication.ThreeFieldCard
import com.example.flashcards.model.daoFiles.CardTypesDao
import com.example.flashcards.model.tablesAndApplication.AllCardTypes
import com.example.flashcards.model.tablesAndApplication.BasicCardType
import com.example.flashcards.model.tablesAndApplication.HintCard
import com.example.flashcards.model.tablesAndApplication.HintCardType
import com.example.flashcards.model.tablesAndApplication.ThreeCardType
import kotlinx.coroutines.flow.Flow
import java.util.Date

class OfflineCardTypeRepository(
    private val cardTypesDao: CardTypesDao)
    : CardTypeRepository {

    override suspend fun insertBasicCard(basicCard: BasicCard) =
        cardTypesDao.insertBasicCard(basicCard)
    override suspend fun insertThreeCard(threeFieldCard: ThreeFieldCard) =
        cardTypesDao.insertThreeCard(threeFieldCard)
    override suspend fun insertHintCard(hintCard: HintCard) =
        cardTypesDao.insertHintCard(hintCard)

    override fun getBasicCard(cardId :Int) = cardTypesDao.getBasicCard(cardId)
    override fun getHintCard(cardId :Int) = cardTypesDao.getHintCard(cardId)
    override fun getThreeCard(cardId :Int) = cardTypesDao.getThreeCard(cardId)

    override suspend fun deleteBasicCard(cardId: Int) =
        cardTypesDao.deleteBasicCard(cardId)
    override suspend fun deleteHintCard(cardId: Int) =
        cardTypesDao.deleteHintCard(cardId)
    override suspend fun deleteThreeCard(cardId: Int) =
        cardTypesDao.deleteThreeCard(cardId)

    override suspend fun updateBasicCard(id: Int,question: String , answer: String)
            = cardTypesDao.updateBasicCard(id, question, answer)
    override suspend fun updateThreeCard(
        id: Int,
        question: String,
        middle: String,
        answer: String
    ) = cardTypesDao.updateThreeCard(id, question, middle, answer)
    override suspend fun updateHintCard(id: Int,
                                        question: String,
                                        hint: String,
                                        answer: String
    ) = cardTypesDao.updateHintCard(id,question,hint,answer)

    override fun getAllBasicCards() = cardTypesDao.getAllBasicCards()
    override fun getAllThreeCards() = cardTypesDao.getAllThreeCards()
    override fun getAllHintCards() = cardTypesDao.getAllHintCards()
    override fun getAllCardTypes(deckId : Int) = cardTypesDao.getAllCardTypes(deckId)

    override fun getDueBasicCards(deckId : Int, currentTime : Long) =
        cardTypesDao.getDueBasicCards(deckId,currentTime)
    override fun getDueThreeCards(deckId : Int, currentTime : Long) =
        cardTypesDao.getDueThreeCards(deckId,currentTime)
    override fun getDueHintCards(deckId : Int, currentTime : Long) =
        cardTypesDao.getDueHintCards(deckId,currentTime)
    override fun getDueAllCardTypes(deckId : Int, currentTime : Long) =
        cardTypesDao.getDueAllCardTypes(deckId,currentTime)

}