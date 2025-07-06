package com.belmontCrest.cardCrafter.localDatabase.dbInterface.daoInterfaces.deckAndCardDao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import com.belmontCrest.cardCrafter.controller.cardHandlers.toBasicList
import com.belmontCrest.cardCrafter.controller.cardHandlers.toCardList
import com.belmontCrest.cardCrafter.controller.cardHandlers.toCustomList
import com.belmontCrest.cardCrafter.controller.cardHandlers.toHintList
import com.belmontCrest.cardCrafter.controller.cardHandlers.toMultiChoiceList
import com.belmontCrest.cardCrafter.controller.cardHandlers.toNotationList
import com.belmontCrest.cardCrafter.controller.cardHandlers.toThreeFieldList
import com.belmontCrest.cardCrafter.localDatabase.tables.AllCardTypes
import com.belmontCrest.cardCrafter.localDatabase.tables.BasicCard
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.localDatabase.tables.Card
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.localDatabase.tables.HintCard
import com.belmontCrest.cardCrafter.localDatabase.tables.MultiChoiceCard
import com.belmontCrest.cardCrafter.localDatabase.tables.NotationCard
import com.belmontCrest.cardCrafter.localDatabase.tables.NullableCustomCard
import com.belmontCrest.cardCrafter.localDatabase.tables.ThreeFieldCard
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.MiddleParam
import com.belmontCrest.cardCrafter.localDatabase.tables.deleteFiles
import com.belmontCrest.cardCrafter.localDatabase.tables.toNullableCustomCard
import com.belmontCrest.cardCrafter.localDatabase.tables.toNullableCustomCards
import com.belmontCrest.cardCrafter.model.daoHelpers.InsertOrAbortDao
import com.belmontCrest.cardCrafter.model.Type.BASIC
import com.belmontCrest.cardCrafter.model.Type.HINT
import com.belmontCrest.cardCrafter.model.Type.MULTI
import com.belmontCrest.cardCrafter.model.Type.NOTATION
import com.belmontCrest.cardCrafter.model.Type.THREE
import com.belmontCrest.cardCrafter.model.daoHelpers.UpdateAndDeleteHelper
import com.belmontCrest.cardCrafter.model.ui.states.CDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Date

@Dao
interface CardTypesDao : InsertOrAbortDao, UpdateAndDeleteHelper {
    @Transaction
    @Query(
        """SELECT * FROM cards WHERE deckId = :deckId 
        AND nextReview <= :currentTime 
        ORDER BY nextReview ASC, partOfList DESC, reviewsLeft DESC
        LIMIT :cardAmount"""
    )
    fun getDueAllCardTypesFlow(deckId: Int, cardAmount: Int, currentTime: Long):
            Flow<List<AllCardTypes>>

    @Transaction
    @Query(
        """SELECT * FROM cards WHERE deckId = :deckId 
        AND nextReview <= :currentTime AND reviewsLeft >= 1
        ORDER BY nextReview ASC, partOfList DESC, reviewsLeft DESC
        LIMIT :cardAmount"""
    )
    fun getDueAllCardTypes(deckId: Int, cardAmount: Int, currentTime: Long): List<AllCardTypes>

    @Transaction
    @Query(
        """SELECT * FROM cards WHERE deckId = :deckId
        ORDER BY cards.id"""
    )
    fun getAllCardTypesStream(deckId: Int): Flow<List<AllCardTypes>>

    @Transaction
    @Query(
        """SELECT * FROM cards WHERE deckId = :deckId
        ORDER BY cards.id"""
    )
    suspend fun getAllCardTypes(deckId: Int): List<AllCardTypes>

    @Transaction
    @Query("""SELECT * FROM cards where id = :id""")
    fun getACardType(id: Int): AllCardTypes

    @Transaction
    @Query("""SELECT * FROM cards where id = :id""")
    fun getACardTypeStream(id: Int): Flow<AllCardTypes>


    @Query("Update cards set type = :type where id = :cardId")
    suspend fun updateCard(cardId: Int, type: String)

    @Transaction
    suspend fun updateCT(
        cardId: Int, type: String, fields: CDetails,
        deleteCT: CT
    ) = withContext(Dispatchers.IO) {
        when (type) {
            BASIC -> {
                insertBasicCard(
                    BasicCard(
                        cardId = cardId,
                        question = fields.question,
                        answer = fields.answer
                    )
                )
            }

            THREE -> {
                insertThreeCard(
                    ThreeFieldCard(
                        cardId = cardId,
                        question = fields.question,
                        middle = fields.middle,
                        answer = fields.answer,
                        field = fields.isQOrA
                    )
                )

            }

            HINT -> {
                insertHintCard(
                    HintCard(
                        cardId = cardId,
                        question = fields.question,
                        hint = fields.middle,
                        answer = fields.answer
                    )
                )
            }

            MULTI -> {
                insertMultiChoiceCard(
                    MultiChoiceCard(
                        cardId = cardId,
                        question = fields.question,
                        choiceA = fields.choices[0],
                        choiceB = fields.choices[1],
                        choiceC = fields.choices[2],
                        choiceD = fields.choices[3],
                        correct = fields.correct
                    )
                )
            }

            NOTATION -> {
                insertNotationCard(
                    NotationCard(
                        cardId = cardId,
                        question = fields.question,
                        steps = fields.steps,
                        answer = fields.answer
                    )
                )
            }

            else -> {
                insertCustomCard(
                    NullableCustomCard(
                        cardId = cardId,
                        question = fields.customQuestion,
                        middle =
                            if (fields.customMiddle == MiddleParam.Empty) null
                            else fields.customMiddle,
                        answer = fields.customAnswer
                    )
                )
            }
        }
        updateCard(cardId, type)
        when (deleteCT) {
            is CT.Basic -> {
                deleteBasicCard(deleteCT.basicCard)
            }

            is CT.ThreeField -> {
                deleteThreeCard(deleteCT.threeFieldCard)
            }

            is CT.Hint -> {
                deleteHintCard(deleteCT.hintCard)
            }

            is CT.MultiChoice -> {
                deleteMultiChoiceCard(deleteCT.multiChoiceCard)
            }

            is CT.Notation -> {
                deleteNotationCard(deleteCT.notationCard)
            }

            is CT.Custom -> {
                val (message, result) = deleteCT.customCard.deleteFiles()
                if (!result) throw Exception(message)
                deleteCustomCard(deleteCT.customCard.toNullableCustomCard())
            }
        }
    }

    @Delete
    suspend fun deleteCards(cards: List<Card>)

    @Delete
    suspend fun deleteBasicCards(basicCards: List<BasicCard>)

    @Delete
    suspend fun deleteThreeCards(threeCards: List<ThreeFieldCard>)

    @Delete
    suspend fun deleteHintCards(hintCards: List<HintCard>)

    @Delete
    suspend fun deleteMultiCards(multiCards: List<MultiChoiceCard>)

    @Delete
    suspend fun deleteNotationCards(notationCards: List<NotationCard>)

    @Delete
    suspend fun deleteCustomCards(customCards: List<NullableCustomCard>)

    /** Delete the selected cards */
    @Transaction
    suspend fun deleteCardList(cts: List<CT>) {
        val basicCards = cts.toBasicList()
        val threeCards = cts.toThreeFieldList()
        val hintCards = cts.toHintList()
        val multiCards = cts.toMultiChoiceList()
        val notationCards = cts.toNotationList()
        val customCards = cts.toCustomList()
        val cards = cts.toCardList()
        val (message, result) = customCards.deleteFiles()
        if (!result) throw Exception(message)
        deleteCards(cards); deleteBasicCards(basicCards); deleteThreeCards(threeCards)
        deleteHintCards(hintCards); deleteMultiCards(multiCards)
        deleteNotationCards(notationCards); deleteCustomCards(customCards.toNullableCustomCards())
    }


    @Query("SELECT MAX(deckCardNumber) FROM cards WHERE deckId = :deckId")
    fun getMaxDCNumber(deckId: Int): Int?

    /** Copy the selected cards into a new deck */
    @Transaction
    suspend fun copyCardList(cts: List<CT>, deck: Deck) {
        cts.forEach { ct ->
            val newDeckCardNumber = returnCardDeckNum(deck.id)
            when (ct) {
                is CT.Basic -> {
                    val cardId = returnCard(deck, newDeckCardNumber, BASIC).toInt()
                    val bc = ct.basicCard
                    insertBasicCard(BasicCard(cardId, bc.question, bc.answer))
                }

                is CT.Hint -> {
                    val cardId = returnCard(deck, newDeckCardNumber, HINT).toInt()
                    val hc = ct.hintCard
                    insertHintCard(HintCard(cardId, hc.question, hc.hint, hc.answer))
                }

                is CT.MultiChoice -> {
                    val cardId = returnCard(deck, newDeckCardNumber, MULTI).toInt()
                    val mc = ct.multiChoiceCard
                    insertMultiChoiceCard(
                        MultiChoiceCard(
                            cardId, question = mc.question,
                            choiceA = mc.choiceA, choiceB = mc.choiceB,
                            choiceC = mc.choiceC, choiceD = mc.choiceD, mc.correct
                        )
                    )
                }

                is CT.Notation -> {
                    val cardId = returnCard(deck, newDeckCardNumber, NOTATION).toInt()
                    val nc = ct.notationCard
                    insertNotationCard(NotationCard(cardId, nc.question, nc.steps, nc.answer))
                }

                is CT.ThreeField -> {
                    val cardId = returnCard(deck, newDeckCardNumber, THREE).toInt()
                    val tc = ct.threeFieldCard
                    insertThreeCard(
                        ThreeFieldCard(cardId, tc.question, tc.middle, tc.answer, tc.field)
                    )
                }

                is CT.Custom -> {
                    val cardId = returnCard(deck, newDeckCardNumber, ct.card.type).toInt()
                    val cc = ct.customCard
                    insertCustomCard(
                        NullableCustomCard(
                            cardId, cc.question, cc.middle, cc.answer
                        )
                    )
                }
            }
        }
    }

    /**
     *  First copy the selected cards to the new deck, and then delete the
     *  original selected cards.
     */
    @Transaction
    suspend fun moveCardList(cts: List<CT>, deck: Deck) {
        copyCardList(cts, deck)
        deleteCardList(cts)
    }

    private suspend fun returnCard(
        deck: Deck, newDeckCardNumber: Int, type: String
    ): Long {
        return insertCard(
            Card(
                deckId = deck.id, nextReview = Date(), passes = 0, prevSuccess = false,
                totalPasses = 0, type = type, deckUUID = deck.uuid,
                deckCardNumber = newDeckCardNumber,
                cardIdentifier = "${deck.uuid}-$newDeckCardNumber",
                reviewsLeft = deck.reviewAmount,
            )
        )
    }

    private fun returnCardDeckNum(deckId: Int): Int {
        return (getMaxDCNumber(deckId) ?: 0) + 1
    }
}