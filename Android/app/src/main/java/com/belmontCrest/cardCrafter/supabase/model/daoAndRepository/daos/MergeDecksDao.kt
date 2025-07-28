package com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.daos

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.belmontCrest.cardCrafter.controller.cardHandlers.toCTList
import com.belmontCrest.cardCrafter.controller.cardHandlers.toCard
import com.belmontCrest.cardCrafter.controller.cardHandlers.toCardList
import com.belmontCrest.cardCrafter.localDatabase.tables.AllCardTypes
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.localDatabase.tables.Card
import com.belmontCrest.cardCrafter.localDatabase.tables.CardInfo
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.localDatabase.tables.ImportedDeckInfo
import com.belmontCrest.cardCrafter.localDatabase.tables.toNullableCustomCard
import com.belmontCrest.cardCrafter.model.daoHelpers.DeckHelperDao
import com.belmontCrest.cardCrafter.model.daoHelpers.TransactionCT
import com.belmontCrest.cardCrafter.supabase.model.tables.SBDeckDto
import com.belmontCrest.cardCrafter.supabase.model.tables.SealedCTToImport
import com.belmontCrest.cardCrafter.supabase.model.tables.toCard
import java.util.Date

@Dao
interface MergeDecksDao : TransactionCT, DeckHelperDao {

    @Query("SELECT * from decks where uuid = :uuid")
    suspend fun getDeck(uuid: String): Deck

    @Query("SELECT * from decks where uuid = :uuid")
    suspend fun doesDeckExist(uuid: String): Deck?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImportedDeckInfo(importedDeckInfo: ImportedDeckInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCardInfo(cardInfo: CardInfo)

    @Transaction
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
    suspend fun insertDeckList(
        sbDeckDto: SBDeckDto, cardList: List<SealedCTToImport>,
        reviewAmount: Int, cardAmount: Int,
        onProgress: (Float) -> Unit
    ) {
        var current = 0
        val total = cardList.size + 2
        val deckId = insertDeck(
            Deck(
                name = sbDeckDto.name,
                uuid = sbDeckDto.deckUUID,
                nextReview = Date(),
                lastUpdated = Date(),
                reviewAmount = reviewAmount,
                cardAmount = cardAmount
            )
        )
        current += 1
        onProgress((current).toFloat() / total)
        insertImportedDeckInfo(
            ImportedDeckInfo(
                uuid = sbDeckDto.deckUUID,
                lastUpdatedOn = sbDeckDto.updatedOn
            )
        )
        current += 1
        onProgress((current).toFloat() / total)
        cardList.forEachIndexed { index, ct ->
            insertTransactionCT(
                ct, deckId.toInt(), sbDeckDto, reviewAmount
            )
            current += 1
            onProgress((current).toFloat() / total)
        }
    }

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
            if (index == duplicateCIToInsert.lastIndex) {
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
                    deckId, ct.card.deckCardNumber ?: 0,
                    ct.card.type, sbDeckDto.deckUUID, reviewAmount
                )
                insertBasicCard(ct.basicCard.copy(cardId = cardId.toInt()))

            }

            is CT.Hint -> {
                val cardId = returnCard(
                    deckId, ct.card.deckCardNumber ?: 0,
                    ct.card.type, sbDeckDto.deckUUID, reviewAmount
                )
                insertHintCard(ct.hintCard.copy(cardId = cardId.toInt()))
            }

            is CT.ThreeField -> {
                val cardId = returnCard(
                    deckId, ct.card.deckCardNumber ?: 0,
                    ct.card.type, sbDeckDto.deckUUID, reviewAmount
                )
                insertThreeCard(ct.threeFieldCard.copy(cardId = cardId.toInt()))
            }

            is CT.MultiChoice -> {
                val cardId = returnCard(
                    deckId, ct.card.deckCardNumber ?: 0,
                    ct.card.type, sbDeckDto.deckUUID, reviewAmount
                )
                insertMultiChoiceCard(ct.multiChoiceCard.copy(cardId = cardId.toInt()))
            }

            is CT.Notation -> {
                val cardId = returnCard(
                    deckId, ct.card.deckCardNumber ?: 0,
                    ct.card.type, sbDeckDto.deckUUID, reviewAmount
                )
                insertNotationCard(ct.notationCard.copy(cardId = cardId.toInt()))
            }

            is CT.Custom -> {
                val cardId = returnCard(
                    deckId, ct.card.deckCardNumber ?: 0,
                    ct.card.type, sbDeckDto.deckUUID, reviewAmount
                )

                insertCustomCard(ct.customCard.copy(cardId = cardId.toInt()).toNullableCustomCard())
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
            getLocalCards(uuid).toCTList()
        } catch (e: IllegalStateException) {
            Log.d("CardTypeRepository", "$e")
            listOf()
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
            is CT.Custom -> copy(card = newCard)
        }
    }
}
