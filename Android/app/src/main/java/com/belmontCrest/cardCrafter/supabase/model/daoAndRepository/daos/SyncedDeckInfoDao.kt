package com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.daos

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
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
import com.belmontCrest.cardCrafter.localDatabase.tables.Card
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.localDatabase.tables.HintCard
import com.belmontCrest.cardCrafter.localDatabase.tables.MultiChoiceCard
import com.belmontCrest.cardCrafter.localDatabase.tables.NotationCard
import com.belmontCrest.cardCrafter.localDatabase.tables.NullableCustomCard
import com.belmontCrest.cardCrafter.localDatabase.tables.SyncedDeckInfo
import com.belmontCrest.cardCrafter.localDatabase.tables.ThreeFieldCard
import com.belmontCrest.cardCrafter.localDatabase.tables.toNullableCustomCards
import com.belmontCrest.cardCrafter.supabase.model.tables.DeckWithCardTypes
import com.belmontCrest.cardCrafter.supabase.model.tables.ListOfDecks

@Dao
interface SyncedDeckInfoDao {

    @Query("SELECT * FROM syncedDeckInfo WHERE uuid = :uuid")
    suspend fun getSyncInfo(uuid: String): SyncedDeckInfo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSyncInfo(syncInfo: SyncedDeckInfo)

    @Query("""SELECT * FROM decks """)
    suspend fun getAllDecks(): List<Deck>

    @Transaction
    @Query(
        """
        SELECT * FROM cards
        WHERE deckId = :deckId
        ORDER BY cards.id
    """
    )
    suspend fun getAllCardTypes(
        deckId: Int
    ): List<AllCardTypes>

    @Transaction
    suspend fun getDB(): List<DeckWithCardTypes> {
        val decks = getAllDecks()
        return decks.map { deck ->
            val cards = getAllCardTypes(deck.id)
            DeckWithCardTypes(deck, cards)
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeck(deck: Deck)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(card: List<Card>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBasicCards(cardList: List<BasicCard>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThreeCards(cardList: List<ThreeFieldCard>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHintCards(cardList: List<HintCard>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMultiCards(cardList: List<MultiChoiceCard>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotationCards(cardList: List<NotationCard>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomCards(cardList: List<NullableCustomCard>)

    @Query("DELETE FROM decks WHERE id NOT IN (:keepIds)")
    suspend fun dropDecksNotIn(keepIds: List<Int>)

    @Query("""
        DELETE FROM cards
        WHERE deckId = :deckId AND id NOT IN (:keepIds)
    """)
    suspend fun dropCardsNotIn(deckId: Int, keepIds: List<Int>)

    /** Replace the local DB instance with the remote one */
    @Transaction
    suspend fun replaceDB(allDecks : ListOfDecks){
        allDecks.decks.map { deckWithCTs ->
            insertDeck(deckWithCTs.deck)
            val keepDeckIds = allDecks.decks.map { it.deck.id }
            dropDecksNotIn(keepDeckIds)
            val cts = deckWithCTs.cts
            val cards = cts.toCardList()
            val keepIds = cards.map { it.id}
            dropCardsNotIn(deckWithCTs.deck.id, keepIds)
            insertCards(cards)
            insertBasicCards(cts.toBasicList())
            insertThreeCards(cts.toThreeFieldList())
            insertHintCards(cts.toHintList())
            insertMultiCards(cts.toMultiChoiceList())
            insertNotationCards(cts.toNotationList())
            val customCards = cts.toCustomList().toNullableCustomCards()
            Log.i("hi", "$customCards")
            insertCustomCards(customCards)
        }
    }
}
