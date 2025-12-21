package com.belmontCrest.cardCrafter.model.daoHelpers

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import com.belmontCrest.cardCrafter.local.db.tables.BasicCard
import com.belmontCrest.cardCrafter.local.db.tables.HintCard
import com.belmontCrest.cardCrafter.local.db.tables.MultiChoiceCard
import com.belmontCrest.cardCrafter.local.db.tables.NotationCard
import com.belmontCrest.cardCrafter.local.db.tables.NullableCustomCard
import com.belmontCrest.cardCrafter.local.db.tables.PartOfQorA
import com.belmontCrest.cardCrafter.local.db.tables.ThreeFieldCard
import com.belmontCrest.cardCrafter.local.db.tables.customCardInit.AnswerParam
import com.belmontCrest.cardCrafter.local.db.tables.customCardInit.MiddleParam
import com.belmontCrest.cardCrafter.local.db.tables.customCardInit.Param

@Dao
interface UpdateAndDeleteHelper {
    @Delete
    suspend fun deleteBasicCard(basicCard: BasicCard)

    @Query("""
        UPDATE basicCard SET question = :newQuestion, 
        answer = :newAnswer WHERE cardId = :id""")
    suspend fun updateBasicCard(id: Int, newQuestion: String, newAnswer: String)

    @Delete
    suspend fun deleteHintCard(hintCard: HintCard)

    @Query("""
        UPDATE hintCard
        SET question = :newQuestion, hint = :newHint, answer = :newAnswer
        WHERE cardId = :id""")
    suspend fun updateHintCard(
        id: Int,
        newQuestion: String,
        newHint: String,
        newAnswer: String
    )

    @Delete
    suspend fun deleteThreeCard(threeFieldCard: ThreeFieldCard)

    @Query("""
        UPDATE threeFieldCard
        SET question = :newQuestion, middle = :newMiddle, answer = :newAnswer,
        field = :isQOrA WHERE cardId = :id""")
    suspend fun updateThreeCard(
        id: Int, newQuestion: String, newMiddle: String, newAnswer: String, isQOrA : PartOfQorA
    )

    @Delete
    suspend fun deleteMultiChoiceCard(multiChoiceCard: MultiChoiceCard)

    @Query("""
        UPDATE multiChoiceCard SET question = :newQuestion, 
        choiceA = :newChoiceA, choiceB = :newChoiceB, choiceC = :newChoiceC,
        choiceD = :newChoiceD, correct = :newCorrect WHERE cardId = :id""")
    suspend fun updateMultiChoiceCard(
        id: Int, newQuestion: String, newChoiceA: String, newChoiceB: String,
        newChoiceC: String, newChoiceD: String, newCorrect: Char
    )


    @Delete
    suspend fun deleteNotationCard(notationCard: NotationCard)

    @Query("""
        UPDATE notationCard
        SET question = :question, steps = :steps, answer = :answer
        WHERE cardId = :cardId
        """)
    suspend fun updateNotationCard(
        question: String, steps: String,
        answer: String, cardId: Int
    )

    @Delete
    suspend fun deleteCustomCard(customCard: NullableCustomCard)

    @Query(
        """
        UPDATE custom_card SET question = :newQuestion, middle = :newMiddle,
        answer = :newAnswer WHERE cardId = :id"""
    )
    suspend fun updateCustomCard(
        id: Int, newQuestion: Param, newMiddle: MiddleParam, newAnswer: AnswerParam
    )
}