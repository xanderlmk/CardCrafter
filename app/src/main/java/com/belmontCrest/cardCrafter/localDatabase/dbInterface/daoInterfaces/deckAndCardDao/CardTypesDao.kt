package com.belmontCrest.cardCrafter.localDatabase.dbInterface.daoInterfaces.deckAndCardDao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.belmontCrest.cardCrafter.localDatabase.tables.AllCardTypes
import com.belmontCrest.cardCrafter.localDatabase.tables.BasicCard
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.localDatabase.tables.HintCard
import com.belmontCrest.cardCrafter.localDatabase.tables.MultiChoiceCard
import com.belmontCrest.cardCrafter.localDatabase.tables.NotationCard
import com.belmontCrest.cardCrafter.localDatabase.tables.ThreeFieldCard
import com.belmontCrest.cardCrafter.model.Type.BASIC
import com.belmontCrest.cardCrafter.model.Type.HINT
import com.belmontCrest.cardCrafter.model.Type.MULTI
import com.belmontCrest.cardCrafter.model.Type.NOTATION
import com.belmontCrest.cardCrafter.model.Type.THREE
import com.belmontCrest.cardCrafter.model.uiModels.Fields
import kotlinx.coroutines.flow.Flow

@Dao
interface CardTypesDao {
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
    fun getDueAllCardTypes(deckId: Int, cardAmount: Int, currentTime: Long):
            List<AllCardTypes>

    @Transaction
    @Query(
        """SELECT * FROM cards WHERE deckId = :deckId
        ORDER BY cards.id"""
    )
    fun getAllCardTypes(deckId: Int): Flow<List<AllCardTypes>>


    @Transaction
    @Query("""SELECT * FROM cards where id = :id""")
    fun getACardType(id: Int): AllCardTypes

    @Transaction
    @Query("""SELECT * FROM cards where id = :id""")
    fun getACardTypeStream(id: Int) : Flow<AllCardTypes>

    @Insert(onConflict = OnConflictStrategy.Companion.ABORT)
    suspend fun insertBasicCard(basicCard: BasicCard)

    @Insert(onConflict = OnConflictStrategy.Companion.ABORT)
    suspend fun insertThreeCard(threeFieldCard: ThreeFieldCard)

    @Insert(onConflict = OnConflictStrategy.Companion.ABORT)
    suspend fun insertHintCard(hintCard: HintCard)

    @Insert(onConflict = OnConflictStrategy.Companion.ABORT)
    suspend fun insertMultiChoiceCard(multiChoiceCard: MultiChoiceCard)

    @Insert(onConflict = OnConflictStrategy.Companion.ABORT)
    suspend fun insertNotationCard(notationCard: NotationCard)

    @Query("Update cards set type = :type where id = :cardId")
    suspend fun updateCard(cardId: Int, type: String)

    @Delete
    suspend fun deleteBasicCard(basicCard: BasicCard)

    @Delete
    suspend fun deleteThreeCard(threeFieldCard: ThreeFieldCard)

    @Delete
    suspend fun deleteHintCard(hintCard: HintCard)

    @Delete
    suspend fun deleteMultiChoiceCard(multiChoiceCard: MultiChoiceCard)

    @Delete
    suspend fun deleteNotationCard(notationCard: NotationCard)

    @Transaction
    suspend fun updateCT(
        cardId: Int, type: String, fields: Fields,
        deleteCT: CT
    ) {
        when (type) {
            BASIC -> {
                insertBasicCard(
                    BasicCard(
                        cardId = cardId,
                        question = fields.question.value,
                        answer = fields.answer.value
                    )
                )
            }

            THREE -> {
                insertThreeCard(
                    ThreeFieldCard(
                        cardId = cardId,
                        question = fields.question.value,
                        middle = fields.middleField.value,
                        answer = fields.answer.value
                    )
                )

            }

            HINT -> {
                insertHintCard(
                    HintCard(
                        cardId = cardId,
                        question = fields.question.value,
                        hint = fields.middleField.value,
                        answer = fields.answer.value
                    )
                )
            }

            MULTI -> {
                insertMultiChoiceCard(
                    MultiChoiceCard(
                        cardId = cardId,
                        question = fields.question.value,
                        choiceA = fields.choices[0].value,
                        choiceB = fields.choices[1].value,
                        choiceC = fields.choices[2].value,
                        choiceD = fields.choices[3].value,
                        correct = fields.correct.value
                    )
                )
            }
            NOTATION -> {
                insertNotationCard(
                    NotationCard(
                        cardId = cardId,
                        question = fields.question.value,
                        steps = fields.stringList.map { it.value },
                        answer = fields.answer.value
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
        }
    }
}