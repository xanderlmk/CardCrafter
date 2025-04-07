package com.belmontCrest.cardCrafter.supabase.model.daoAndRepository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.belmontCrest.cardCrafter.model.tablesAndApplication.BasicCard
import com.belmontCrest.cardCrafter.model.tablesAndApplication.Card
import com.belmontCrest.cardCrafter.model.tablesAndApplication.Deck
import com.belmontCrest.cardCrafter.model.tablesAndApplication.HintCard
import com.belmontCrest.cardCrafter.model.tablesAndApplication.MultiChoiceCard
import com.belmontCrest.cardCrafter.model.tablesAndApplication.NotationCard
import com.belmontCrest.cardCrafter.model.tablesAndApplication.ThreeFieldCard
import com.belmontCrest.cardCrafter.supabase.model.SBCardDto
import com.belmontCrest.cardCrafter.supabase.model.SBDeckDto
import com.belmontCrest.cardCrafter.supabase.model.SealedCT

import java.util.Date

data class DeckUUID(
    val uuid: String
)

data class DeckSignature(
    val name: String,
    val uuid: String
)

@Dao
interface SupabaseDao {
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

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertCard(card: Card): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)

    fun insertBasicCard(basicCard: BasicCard)

    @Insert(onConflict = OnConflictStrategy.ABORT)

    fun insertThreeCard(threeFieldCard: ThreeFieldCard)

    @Insert(onConflict = OnConflictStrategy.ABORT)

    fun insertHintCard(hintCard: HintCard)

    @Insert(onConflict = OnConflictStrategy.ABORT)

    fun insertMultiChoiceCard(multiCard: MultiChoiceCard)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertNotationCard(notationCard: NotationCard)

    @Delete(entity = Deck::class)
    fun deleteDeck(vararg uuid: DeckUUID)

    @Delete
    fun deleteCards(cards: List<Card>)

    @Query("""SELECT * from cards where deckUUID = :uuid""")
    fun getCards(uuid: String): List<Card>

    @Transaction
    suspend fun replaceDeckList(
        sbDeckDto: SBDeckDto, cardList: List<SealedCT>,
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
        sbDeckDto: SBDeckDto, cardList: List<SealedCT>,
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
        cardList.forEachIndexed { index, ct ->
            insertTransactionCT(
                ct, deckId, sbDeckDto, reviewAmount
            )
            /** This is at 50% hence we add the total (total/2)
             *  with the index + 1, which is all divided by the total */
            onProgress(((total / 2) + (index + 1).toFloat()) / total)
        }
    }

    private fun insertTransactionCT(
        ct: SealedCT, deckId: Long, sbDeckDto: SBDeckDto,
        reviewAmount: Int
    ) {
        val cardIdentifier = returnCard(ct).cardIdentifier
        val deckCardNumber = cardIdentifier.substringAfterLast("-").toInt()
        when (ct) {
            is SealedCT.Basic -> {
                val cardId = insertCard(
                    Card(
                        deckId = deckId.toInt(),
                        deckUUID = sbDeckDto.deckUUID,
                        deckCardNumber = deckCardNumber,
                        cardIdentifier = "${sbDeckDto.deckUUID}-$deckCardNumber",
                        nextReview = Date(),
                        reviewsLeft = reviewAmount,
                        passes = 0,
                        prevSuccess = false,
                        totalPasses = 0,
                        type = "basic",
                    )
                )
                insertBasicCard(
                    BasicCard(
                        cardId = cardId.toInt(),
                        question = ct.basicCard.question,
                        answer = ct.basicCard.answer
                    )
                )

            }

            is SealedCT.Three -> {
                val cardId = insertCard(
                    Card(
                        deckId = deckId.toInt(),
                        deckUUID = sbDeckDto.deckUUID,
                        deckCardNumber = deckCardNumber,
                        cardIdentifier = "${sbDeckDto.deckUUID}-$deckCardNumber",
                        nextReview = Date(),
                        reviewsLeft = reviewAmount,
                        passes = 0,
                        prevSuccess = false,
                        totalPasses = 0,
                        type = "three",
                    )
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

            is SealedCT.Hint -> {
                val cardId = insertCard(
                    Card(
                        deckId = deckId.toInt(),
                        deckUUID = sbDeckDto.deckUUID,
                        deckCardNumber = deckCardNumber,
                        cardIdentifier = "${sbDeckDto.deckUUID}-$deckCardNumber",
                        nextReview = Date(),
                        reviewsLeft = reviewAmount,
                        passes = 0,
                        prevSuccess = false,
                        totalPasses = 0,
                        type = "hint",
                    )
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

            is SealedCT.Multi -> {
                val cardId = insertCard(
                    Card(
                        deckId = deckId.toInt(),
                        deckUUID = sbDeckDto.deckUUID,
                        deckCardNumber = deckCardNumber,
                        cardIdentifier = "${sbDeckDto.deckUUID}-$deckCardNumber",
                        nextReview = Date(),
                        reviewsLeft = reviewAmount,
                        passes = 0,
                        prevSuccess = false,
                        totalPasses = 0,
                        type = "multi",
                    )
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

            is SealedCT.Notation -> {
                val cardId = insertCard(
                    Card(
                        deckId = deckId.toInt(),
                        deckUUID = sbDeckDto.deckUUID,
                        deckCardNumber = deckCardNumber,
                        cardIdentifier = "${sbDeckDto.deckUUID}-$deckCardNumber",
                        nextReview = Date(),
                        reviewsLeft = reviewAmount,
                        passes = 0,
                        prevSuccess = false,
                        totalPasses = 0,
                        type = "notation",
                    )
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
}

private fun returnCard(ct: SealedCT): SBCardDto {
    return when (ct) {
        is SealedCT.Basic -> ct.card

        is SealedCT.Three -> ct.card

        is SealedCT.Hint -> ct.card

        is SealedCT.Multi -> ct.card

        is SealedCT.Notation -> ct.card
    }
}