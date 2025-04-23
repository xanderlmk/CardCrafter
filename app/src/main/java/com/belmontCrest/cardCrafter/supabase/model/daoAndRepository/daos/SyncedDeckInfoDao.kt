package com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.belmontCrest.cardCrafter.localDatabase.tables.AllCardTypes
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.localDatabase.tables.SyncedDeckInfo
import com.belmontCrest.cardCrafter.supabase.model.tables.DeckWithCardTypes

@Dao
interface SyncedDeckInfoDao {

    @Query("SELECT * FROM syncedDeckInfo WHERE uuid = :uuid")
    suspend fun getSyncInfo(uuid: String): SyncedDeckInfo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSyncInfo(syncInfo: SyncedDeckInfo)

    @Query("""SELECT * FROM decks """)
    suspend fun getAllDecks():List<Deck>

    @Query("""
        SELECT * FROM cards
        WHERE deckId = :deckId
        ORDER BY cards.id
    """)
    fun getAllCardTypes(
        deckId: Int
    ): List<AllCardTypes>

    @Transaction
    suspend fun getDB():List<DeckWithCardTypes>
    {
        val decks = getAllDecks()
        return decks.map { deck ->
            val cards = getAllCardTypes(deck.id)
            DeckWithCardTypes(deck, cards)
        }
    }

}
