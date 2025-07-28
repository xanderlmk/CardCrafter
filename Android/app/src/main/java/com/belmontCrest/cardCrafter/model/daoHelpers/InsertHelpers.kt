package com.belmontCrest.cardCrafter.model.daoHelpers

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.belmontCrest.cardCrafter.localDatabase.tables.BasicCard
import com.belmontCrest.cardCrafter.localDatabase.tables.Card
import com.belmontCrest.cardCrafter.localDatabase.tables.NullableCustomCard
import com.belmontCrest.cardCrafter.localDatabase.tables.HintCard
import com.belmontCrest.cardCrafter.localDatabase.tables.MultiChoiceCard
import com.belmontCrest.cardCrafter.localDatabase.tables.NotationCard
import com.belmontCrest.cardCrafter.localDatabase.tables.ThreeFieldCard
import com.belmontCrest.cardCrafter.model.Type.BASIC
import com.belmontCrest.cardCrafter.model.Type.HINT
import com.belmontCrest.cardCrafter.model.Type.MULTI
import com.belmontCrest.cardCrafter.model.Type.NOTATION
import com.belmontCrest.cardCrafter.model.Type.THREE
import com.belmontCrest.cardCrafter.supabase.model.tables.SBDeckDto
import com.belmontCrest.cardCrafter.supabase.model.tables.SealedCTToImport
import com.belmontCrest.cardCrafter.supabase.model.tables.toCard
import java.util.Date


@Dao
interface InsertAndReplaceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: Card): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBasicCard(basicCard: BasicCard): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHintCard(hintCard: HintCard): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMultiChoiceCard(multiChoiceCard: MultiChoiceCard): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotationCard(notationCard: NotationCard): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThreeCard(threeFieldCard: ThreeFieldCard): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomCard(nullableCustomCard: NullableCustomCard): Long
}

@Dao
interface InsertOrAbortDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCard(card: Card): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertBasicCard(basicCard: BasicCard): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertHintCard(hintCard: HintCard): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMultiChoiceCard(multiChoiceCard: MultiChoiceCard): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertNotationCard(notationCard: NotationCard): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertThreeCard(threeFieldCard: ThreeFieldCard): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCustomCard(nullableCustomCard: NullableCustomCard): Long
}

@Dao
interface TransactionCT : InsertAndReplaceDao {
    suspend fun insertTransactionCT(
        ct: SealedCTToImport, deckId: Int, sbDeckDto: SBDeckDto, reviewAmount: Int
    ) {
        val cardIdentifier = ct.toCard().cardIdentifier
        val deckCardNumber = cardIdentifier.substringAfterLast("-").toInt()
        when (ct) {
            is SealedCTToImport.Basic -> {
                val cardId = returnCard(
                    deckId, deckCardNumber, BASIC, sbDeckDto.deckUUID, reviewAmount
                )
                insertBasicCard(
                    BasicCard(
                        cardId = cardId.toInt(),
                        question = ct.basicCard.question,
                        answer = ct.basicCard.answer
                    )
                )
            }

            is SealedCTToImport.Three -> {
                val cardId = returnCard(
                    deckId, deckCardNumber, THREE, sbDeckDto.deckUUID, reviewAmount
                )
                insertThreeCard(
                    ThreeFieldCard(
                        cardId = cardId.toInt(),
                        question = ct.threeCard.question,
                        middle = ct.threeCard.middle,
                        answer = ct.threeCard.answer
                    )
                )
            }

            is SealedCTToImport.Hint -> {
                val cardId = returnCard(
                    deckId, deckCardNumber, HINT, sbDeckDto.deckUUID, reviewAmount
                )
                insertHintCard(
                    HintCard(
                        cardId = cardId.toInt(),
                        question = ct.hintCard.question,
                        hint = ct.hintCard.hint,
                        answer = ct.hintCard.answer
                    )
                )
            }

            is SealedCTToImport.Multi -> {
                val cardId = returnCard(
                    deckId, deckCardNumber, MULTI, sbDeckDto.deckUUID, reviewAmount
                )
                insertMultiChoiceCard(
                    MultiChoiceCard(
                        cardId = cardId.toInt(),
                        question = ct.multiCard.question,
                        choiceA = ct.multiCard.choiceA,
                        choiceB = ct.multiCard.choiceB,
                        choiceC = ct.multiCard.choiceC,
                        choiceD = ct.multiCard.choiceD,
                        correct = ct.multiCard.correct
                    )
                )
            }

            is SealedCTToImport.Notation -> {
                val cardId = returnCard(
                    deckId, deckCardNumber, NOTATION, sbDeckDto.deckUUID, reviewAmount
                )
                insertNotationCard(
                    NotationCard(
                        cardId = cardId.toInt(),
                        question = ct.notationCard.question,
                        steps = ct.notationCard.steps,
                        answer = ct.notationCard.answer
                    )
                )
            }
        }
    }

    private suspend fun returnCard(
        deckId: Int, newDeckCardNumber: Int, type: String, uuid: String, reviewAmount: Int
    ): Long {
        return insertCard(
            Card(
                deckId = deckId,
                nextReview = Date(),
                passes = 0,
                prevSuccess = false,
                totalPasses = 0,
                type = type,
                deckUUID = uuid,
                deckCardNumber = newDeckCardNumber,
                cardIdentifier = "${uuid}-$newDeckCardNumber",
                reviewsLeft = reviewAmount,
            )
        )
    }
}