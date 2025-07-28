package com.belmontCrest.cardCrafter.localDatabase.dbInterface.daoInterfaces.deckAndCardDao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.belmontCrest.cardCrafter.localDatabase.tables.BasicCard
import com.belmontCrest.cardCrafter.localDatabase.tables.Card
import com.belmontCrest.cardCrafter.localDatabase.tables.CIForID
import com.belmontCrest.cardCrafter.localDatabase.tables.CardInfo
import com.belmontCrest.cardCrafter.localDatabase.tables.NullableCustomCard
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.localDatabase.tables.HintCard
import com.belmontCrest.cardCrafter.localDatabase.tables.ImportedDeckInfo
import com.belmontCrest.cardCrafter.localDatabase.tables.MultiChoiceCard
import com.belmontCrest.cardCrafter.localDatabase.tables.NotationCard
import com.belmontCrest.cardCrafter.localDatabase.tables.ThreeFieldCard
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.MiddleParam
import com.belmontCrest.cardCrafter.model.daoHelpers.InsertOrAbortDao
import com.belmontCrest.cardCrafter.model.Type.BASIC
import com.belmontCrest.cardCrafter.model.Type.HINT
import com.belmontCrest.cardCrafter.model.Type.MULTI
import com.belmontCrest.cardCrafter.model.Type.NOTATION
import com.belmontCrest.cardCrafter.model.Type.THREE
import com.belmontCrest.cardCrafter.views.miscFunctions.details.CardDetails
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface CardDao : InsertOrAbortDao {

    @Query("SELECT MAX(deckCardNumber) FROM cards WHERE deckUUID = :deckUUID")
    fun getMaxDCNumber(deckUUID: String): Int?


    @Query("""SELECT * FROM importedDeckInfo WHERE uuid = :uuid""")
    fun getDeckInfo(uuid: String): ImportedDeckInfo?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCardInfo(cardInfo: CardInfo)

    @Query("""SELECT cardIdentifier FROM cards WHERE id = :cardId""")
    fun getCardIdentifier(cardId: Int): CIForID

    @Transaction
    suspend fun insertBasicCard(
        deck: Deck, basicCD: CardDetails.BasicCD, isOwnerOrCoOwner: Boolean
    ) {
        val newDeckCardNumber = returnCardDeckNum(deck.uuid)
        val cardId = returnCard(deck, newDeckCardNumber, BASIC)
        insertBasicCard(
            BasicCard(
                cardId = cardId.toInt(),
                question = basicCD.question,
                answer = basicCD.answer
            )
        )
        if (isOwnerOrCoOwner) {
            checkCardInfo(deck.uuid, cardId.toInt())
        }
    }

    @Transaction
    suspend fun insertThreeCard(
        deck: Deck, threeCD: CardDetails.ThreeCD, isOwnerOrCoOwner: Boolean
    ) {
        val newDeckCardNumber = returnCardDeckNum(deck.uuid)
        val cardId = returnCard(deck, newDeckCardNumber, THREE)
        insertThreeCard(
            ThreeFieldCard(
                cardId = cardId.toInt(),
                question = threeCD.question,
                middle = threeCD.middle,
                answer = threeCD.answer,
                field = threeCD.isQOrA
            )
        )

        if (isOwnerOrCoOwner) {
            checkCardInfo(deck.uuid, cardId.toInt())
        }
    }

    @Transaction
    suspend fun insertHintCard(
        deck: Deck, hintCD: CardDetails.HintCD, isOwnerOrCoOwner: Boolean
    ) {
        val newDeckCardNumber = returnCardDeckNum(deck.uuid)
        val cardId = returnCard(deck, newDeckCardNumber, HINT)
        insertHintCard(
            HintCard(
                cardId = cardId.toInt(),
                question = hintCD.question,
                hint = hintCD.middle,
                answer = hintCD.answer
            )
        )
        if (isOwnerOrCoOwner) {
            checkCardInfo(deck.uuid, cardId.toInt())
        }
    }

    @Transaction
    suspend fun insertMultiCard(
        deck: Deck, multiCD: CardDetails.MultiCD, isOwnerOrCoOwner: Boolean
    ) {
        val newDeckCardNumber = returnCardDeckNum(deck.uuid)
        val cardId = returnCard(deck, newDeckCardNumber, MULTI)
        insertMultiChoiceCard(
            MultiChoiceCard(
                cardId = cardId.toInt(),
                question = multiCD.question,
                choiceA = multiCD.choiceA,
                choiceB = multiCD.choiceB,
                choiceC = multiCD.choiceC,
                choiceD = multiCD.choiceD,
                correct = multiCD.correct
            )
        )
        if (isOwnerOrCoOwner) {
            checkCardInfo(deck.uuid, cardId.toInt())
        }
    }

    @Transaction
    suspend fun insertNotationCard(
        deck: Deck, notationCD: CardDetails.NotationCD, isOwnerOrCoOwner: Boolean
    ) {
        val newDeckCardNumber = returnCardDeckNum(deck.uuid)
        val cardId = returnCard(deck, newDeckCardNumber, NOTATION)
        insertNotationCard(
            NotationCard(
                cardId = cardId.toInt(),
                question = notationCD.question,
                steps = notationCD.steps,
                answer = notationCD.answer,
            )
        )
        if (isOwnerOrCoOwner) {
            checkCardInfo(deck.uuid, cardId.toInt())
        }
    }

    @Transaction
    suspend fun insertCustomCard(
        deck: Deck, customCD: CardDetails.CustomCD, type: String, isOwnerOrCoOwner: Boolean
    ) {
        val newDeckCardNumber = returnCardDeckNum(deck.uuid)
        val cardId = returnCard(deck, newDeckCardNumber, type)
        insertCustomCard(
            NullableCustomCard(
                cardId = cardId.toInt(),
                question = customCD.question,
                middle = if (customCD.middle == MiddleParam.Empty) null else customCD.middle,
                answer = customCD.answer
            )
        )
        if (isOwnerOrCoOwner) {
            checkCardInfo(deck.uuid, cardId.toInt())
        }
    }

    @Update
    suspend fun updateCard(card: Card)

    @Delete
    suspend fun deleteCard(card: Card)

    @Query("SELECT * FROM cards WHERE id = :cardId")
    fun getCardStream(cardId: Int): Flow<Card>

    @Query("SELECT * FROM cards WHERE deckId = :deckId AND nextReview <= :currentTime")
    fun getDueCards(deckId: Int, currentTime: Long = Date().time): Flow<List<Card>>

    @Query(
        """
        SELECT * FROM cards WHERE deckId = :deckId 
        AND nextReview <= :currentTime 
        ORDER BY nextReview ASC, partOfList DESC
        LIMIT :cardAmount"""
    )
    suspend fun getBackupDueCards(
        deckId: Int,
        cardAmount: Int,
        currentTime: Long = Date().time
    ): List<Card>

    @Query("DELETE FROM cards WHERE deckId = :deckId")
    suspend fun deleteAllCards(deckId: Int)

    @Query("UPDATE cards SET partOfList = 1 where id = :id")
    suspend fun becomePartOfList(id: Int)

    @Query("SELECT * FROM cards WHERE id = :cardId")
    fun getCardById(cardId: Int): Card

    @Query(
        """
        update cards
        set reviewsLeft = :newReviewAmount
        where deckId = :deckId
    """
    )
    fun updateReviewAmount(newReviewAmount: Int, deckId: Int): Int


    private fun returnCardDeckNum(uuid: String): Int {
        return (getMaxDCNumber(uuid) ?: 0) + 1
    }

    private suspend fun returnCard(deck: Deck, newDeckCardNumber: Int, type: String): Long {
        return insertCard(
            Card(
                deckId = deck.id,
                nextReview = Date(),
                passes = 0,
                prevSuccess = false,
                totalPasses = 0,
                type = type,
                deckUUID = deck.uuid,
                deckCardNumber = newDeckCardNumber,
                cardIdentifier = "${deck.uuid}-$newDeckCardNumber",
                reviewsLeft = deck.reviewAmount
            )
        )
    }

    private suspend fun checkCardInfo(uuid: String, cardId: Int) {
        val deckInfo = getDeckInfo(uuid)
        if (deckInfo != null) {
            val ciForID = getCardIdentifier(cardId)
            insertCardInfo(CardInfo(ciForID.cardIdentifier, true))
        }
    }
}