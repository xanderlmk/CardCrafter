package com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.belmontCrest.cardCrafter.localDatabase.tables.BasicCard
import com.belmontCrest.cardCrafter.localDatabase.tables.Card
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.localDatabase.tables.HintCard
import com.belmontCrest.cardCrafter.localDatabase.tables.ImportedDeckInfo
import com.belmontCrest.cardCrafter.localDatabase.tables.MultiChoiceCard
import com.belmontCrest.cardCrafter.localDatabase.tables.NotationCard
import com.belmontCrest.cardCrafter.localDatabase.tables.ThreeFieldCard
import com.belmontCrest.cardCrafter.model.InsertOrAbortDao
import com.belmontCrest.cardCrafter.model.Type.BASIC
import com.belmontCrest.cardCrafter.model.Type.HINT
import com.belmontCrest.cardCrafter.model.Type.MULTI
import com.belmontCrest.cardCrafter.model.Type.NOTATION
import com.belmontCrest.cardCrafter.model.Type.THREE
import com.belmontCrest.cardCrafter.supabase.model.tables.SBDeckDto
import com.belmontCrest.cardCrafter.supabase.model.tables.SealedCTToImport
import com.belmontCrest.cardCrafter.supabase.model.tables.toCard

import java.util.Date

data class DeckUUID(
    val uuid: String
)

data class DeckSignature(
    val name: String,
    val uuid: String
)

@Dao
interface ImportFromSBDao : InsertOrAbortDao {
    @Query(
        """
        SELECT name, uuid 
        FROM decks WHERE uuid = :deckUUID
    """
    )
    fun validateDeckSignature(deckUUID: String): DeckSignature?

    @Query(
        """
        SELECT name, uuid
        FROM decks WHERE name = :name
    """
    )
    fun validateDeckName(name: String): DeckSignature?

    @Insert
    fun insertDeck(deck: Deck): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertImportedDeckInfo(importedDeckInfo: ImportedDeckInfo)

    @Delete(entity = Deck::class)
    fun deleteDeck(vararg uuid: DeckUUID)

    @Delete
    fun deleteCards(cards: List<Card>)

    @Query("""SELECT * from cards where deckUUID = :uuid""")
    fun getCards(uuid: String): List<Card>

    @Transaction
    suspend fun replaceDeckList(
        sbDeckDto: SBDeckDto, cardList: List<SealedCTToImport>,
        reviewAmount: Int, cardAmount: Int, name: String,
        onProgress: (Float) -> Unit, total: Int
    ) {
        val cardsToDelete = getCards(sbDeckDto.deckUUID)

        deleteCards(cardsToDelete)
        deleteDeck(DeckUUID(sbDeckDto.deckUUID))

        val deckId = insertDeck(
            Deck(
                name = name,
                uuid = sbDeckDto.deckUUID,
                nextReview = Date(),
                lastUpdated = Date(),
                reviewAmount = reviewAmount,
                cardAmount = cardAmount
            )
        )
        insertImportedDeckInfo(
            ImportedDeckInfo(
                uuid = sbDeckDto.deckUUID,
                lastUpdatedOn = sbDeckDto.updatedOn
            )
        )
        cardList.forEachIndexed { index, ct ->
            insertTransactionCT(
                ct, deckId, sbDeckDto, reviewAmount
            )
            /** This is at 50% hence we add the total (total/2)
             *  with the index + 1, which is all divided by the total */
            onProgress(((total / 2) + (index + 1).toFloat()) / total)
        }

    }

    @Transaction
    suspend fun insertDeckList(
        sbDeckDto: SBDeckDto, cardList: List<SealedCTToImport>,
        name: String, reviewAmount: Int, cardAmount: Int,
        onProgress: (Float) -> Unit, total: Int
    ) {
        val deckId = insertDeck(
            Deck(
                name = name,
                uuid = sbDeckDto.deckUUID,
                nextReview = Date(),
                lastUpdated = Date(),
                reviewAmount = reviewAmount,
                cardAmount = cardAmount
            )
        )
        insertImportedDeckInfo(
            ImportedDeckInfo(
                uuid = sbDeckDto.deckUUID,
                lastUpdatedOn = sbDeckDto.updatedOn
            )
        )
        cardList.forEachIndexed { index, ct ->
            insertTransactionCT(
                ct, deckId, sbDeckDto, reviewAmount
            )
            /** This is at 50% hence we add the total (total/2)
             *  with the index + 1, which is all divided by the total */
            onProgress(((total / 2) + (index + 1).toFloat()) / total)
        }
    }

    private suspend fun insertTransactionCT(
        ct: SealedCTToImport, deckId: Long, sbDeckDto: SBDeckDto,
        reviewAmount: Int
    ) {
        val cardIdentifier = ct.toCard().cardIdentifier
        val deckCardNumber = cardIdentifier.substringAfterLast("-").toInt()
        when (ct) {
            is SealedCTToImport.Basic -> {
                val cardId = returnCard(
                    deckId.toInt(), deckCardNumber, BASIC, sbDeckDto.deckUUID, reviewAmount
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
                    deckId.toInt(), deckCardNumber, THREE, sbDeckDto.deckUUID, reviewAmount
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
                    deckId.toInt(), deckCardNumber, HINT, sbDeckDto.deckUUID, reviewAmount
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
                    deckId.toInt(), deckCardNumber, MULTI, sbDeckDto.deckUUID, reviewAmount
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
                    deckId.toInt(), deckCardNumber, NOTATION, sbDeckDto.deckUUID, reviewAmount
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
