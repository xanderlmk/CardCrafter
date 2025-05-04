package com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.daos

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.belmontCrest.cardCrafter.controller.cardHandlers.mapAllCardTypesToCTs
import com.belmontCrest.cardCrafter.controller.cardHandlers.toCard
import com.belmontCrest.cardCrafter.controller.cardHandlers.toCardList
import com.belmontCrest.cardCrafter.localDatabase.tables.AllCardTypes
import com.belmontCrest.cardCrafter.localDatabase.tables.BasicCard
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.localDatabase.tables.Card
import com.belmontCrest.cardCrafter.localDatabase.tables.CardInfo
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.localDatabase.tables.HintCard
import com.belmontCrest.cardCrafter.localDatabase.tables.ImportedDeckInfo
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
interface MergeDecksDao {
    @Query("SELECT * from decks where uuid = :uuid")
    suspend fun getDeck(uuid: String): Deck

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImportedDeckInfo(importedDeckInfo: ImportedDeckInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: Card): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBasicCard(basicCard: BasicCard)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThreeCard(threeFieldCard: ThreeFieldCard)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHintCard(hintCard: HintCard)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMultiChoiceCard(multiCard: MultiChoiceCard)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotationCard(notationCard: NotationCard)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCardInfo(cardInfo: CardInfo)

    @Query(
        """
        SELECT * FROM cards AS c
        WHERE  c.deckUUID = :uuid
        AND EXISTS (
            SELECT 1
            FROM   card_info AS ci
            WHERE  ci.card_identifier = c.cardIdentifier
        )
        """
    )
    suspend fun getLocalCards(uuid: String): List<AllCardTypes>

    @Query("SELECT * FROM card_info WHERE card_identifier = :ci")
    suspend fun getByCI(ci: String): CardInfo?

    @Query(
        """
        SELECT * FROM card_info WHERE card_identifier = (
            SELECT cardIdentifier FROM cards WHERE deckId = :deckId
        ) AND is_local = 1
        """
    )
    suspend fun getLocalCardInfo(deckId: Int): List<CardInfo>

    @Query(
        """
        DELETE FROM card_info WHERE card_identifier = (
            SELECT cardIdentifier FROM cards WHERE
            deckId = :deckId
        ) AND is_local = 1
        """
    )
    suspend fun deleteCardInfo(deckId: Int)

    @Query(
        """
        DELETE FROM cards
        WHERE deckId = :deckId AND cardIdentifier NOT IN (:keepIds)
    """
    )
    suspend fun dropCardsNotIn(deckId: Int, keepIds: List<String>)

    @Transaction
    suspend fun mergeDeck(
        sbDeckDto: SBDeckDto, remoteCL: List<SealedCTToImport>, onProgress: (Float) -> Unit,
    ) {
        /** Local CTS that should be identified by card_info*/
        val localCTs = getLocalCTs(sbDeckDto.deckUUID)
        val localCards = localCTs.toCardList()
        val deck = getDeck(sbDeckDto.deckUUID)

        /** Transform the sealed card list to a card */
        val remoteCards = remoteCL.map { it.toCard() }

        val localByIdentifier = localCards.associateBy { it.cardIdentifier }
        var nextDeckNumber = localCards.maxOfOrNull { it.deckCardNumber ?: 0 } ?: 0
        var next = nextDeckNumber
        val duplicateCIToInsert = mutableListOf<CardInfo>()

        for (remote in remoteCards) {
            val localCard = localByIdentifier[remote.cardIdentifier]
            if (localCard != null) { // collision
                // only duplicate if the local copy was user‑created
                val info = getByCI(localCard.cardIdentifier)
                if (info?.isLocal == true) {
                    nextDeckNumber += 1
                    val newIdentifier = "${sbDeckDto.deckUUID}-$nextDeckNumber"
                    duplicateCIToInsert += CardInfo(newIdentifier, isLocal = true)
                }
            }
        }
        val total = remoteCL.size + localCards.size + duplicateCIToInsert.size + 4
        var current = 0
        current += 1
        onProgress((current).toFloat() / total)

        insertImportedDeckInfo(ImportedDeckInfo(sbDeckDto.deckUUID, sbDeckDto.updatedOn))
        current += 1
        onProgress((current).toFloat() / total)

        remoteCL.forEachIndexed { index, ct ->
            insertTransactionCT(ct, deck.id, sbDeckDto, deck.reviewAmount)
            onProgress((current + (index + 1).toFloat()) / total)
            if (index == remoteCL.lastIndex) {
                current += index + 1
            }
        }
        localCTs.forEachIndexed { index, ct ->
            val updated = ct.duplicateWith(++next, sbDeckDto.deckUUID)
            insertTransactionCT(updated, deck.id, sbDeckDto, deck.reviewAmount)
            onProgress((current + (index + 1).toFloat()) / total)
            if (index == localCTs.lastIndex) {
                current += index + 1
            }
        }
        val localCardInfo = getLocalCardInfo(deck.id)
        val keepIdentifiers = remoteCards.map { it.cardIdentifier } +
                duplicateCIToInsert.map { it.cardIdentifier } + // new local copies
                localCards.filter {// all other local cards the user created
                    getByCI(it.cardIdentifier)?.isLocal == true
                }.map { it.cardIdentifier } +
                localCardInfo.map { it.cardIdentifier }
        /** Delete the card info since it has been replaced */
        if (duplicateCIToInsert.isNotEmpty()) {
            deleteCardInfo(deck.id)
        }
        current += 1
        onProgress((current).toFloat() / total)

        duplicateCIToInsert.forEachIndexed { index, it ->
            insertCardInfo(it)
            onProgress((current + (index + 1).toFloat()) / total)
            if (index == remoteCL.lastIndex) {
                current += index + 1
            }
        }
        dropCardsNotIn(deck.id, keepIdentifiers.toList())
        current += 1
        onProgress((current).toFloat() / total)
    }

    private suspend fun insertTransactionCT(
        ct: CT, deckId: Int, sbDeckDto: SBDeckDto, reviewAmount: Int
    ) {
        when (ct) {
            is CT.Basic -> {
                val cardId = returnCard(
                    deckId.toInt(), ct.card.deckCardNumber ?: 0,
                    ct.card.type, sbDeckDto.deckUUID, reviewAmount
                )
                insertBasicCard(ct.basicCard.copy(cardId = cardId.toInt()))

            }

            is CT.Hint -> {
                val cardId = returnCard(
                    deckId.toInt(), ct.card.deckCardNumber ?: 0,
                    ct.card.type, sbDeckDto.deckUUID, reviewAmount
                )
                insertHintCard(ct.hintCard.copy(cardId = cardId.toInt()))
            }

            is CT.ThreeField -> {
                val cardId = returnCard(
                    deckId.toInt(), ct.card.deckCardNumber ?: 0,
                    ct.card.type, sbDeckDto.deckUUID, reviewAmount
                )
                insertThreeCard(ct.threeFieldCard.copy(cardId = cardId.toInt()))
            }

            is CT.MultiChoice -> {
                val cardId = returnCard(
                    deckId.toInt(), ct.card.deckCardNumber ?: 0,
                    ct.card.type, sbDeckDto.deckUUID, reviewAmount
                )
                insertMultiChoiceCard(ct.multiChoiceCard.copy(cardId = cardId.toInt()))
            }

            is CT.Notation -> {
                val cardId = returnCard(
                    deckId.toInt(), ct.card.deckCardNumber ?: 0,
                    ct.card.type, sbDeckDto.deckUUID, reviewAmount
                )
                insertNotationCard(ct.notationCard.copy(cardId = cardId.toInt()))
            }
        }
    }

    private suspend fun insertTransactionCT(
        ct: SealedCTToImport, deckId: Int, sbDeckDto: SBDeckDto, reviewAmount: Int
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

    private suspend fun getLocalCTs(uuid: String): List<CT> {
        return try {
            mapAllCardTypesToCTs(getLocalCards(uuid))
        } catch (e: IllegalStateException) {
            Log.d("CardTypeRepository", "$e")
            listOf<CT>()
        }
    }

    /** duplicates a CT with a fresh Card deckNumber + identifier */
    private fun CT.duplicateWith(
        newNumber: Int,
        deckUUID: String
    ): CT {
        val newId = "$deckUUID-$newNumber"

        val newCard = this.toCard().copy(
            id = 0,
            deckCardNumber = newNumber,
            cardIdentifier = newId
        )
        return when (this) {
            is CT.Basic -> copy(card = newCard)
            is CT.Hint -> copy(card = newCard)
            is CT.ThreeField -> copy(card = newCard)
            is CT.MultiChoice -> copy(card = newCard)
            is CT.Notation -> copy(card = newCard)
        }
    }
}
