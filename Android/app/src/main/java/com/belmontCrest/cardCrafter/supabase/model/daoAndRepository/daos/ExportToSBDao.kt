package com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.belmontCrest.cardCrafter.localDatabase.tables.AllCardTypes
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.localDatabase.tables.ImportedDeckInfo
import kotlinx.coroutines.flow.Flow


@Dao
interface ExportToSBDao {
    @Query(
        """SELECT * FROM importedDeckInfo WHERE uuid = 
        (SELECT uuid FROM decks WHERE id = :id)"""
    )
    fun getImportedDeckInfo(id: Int): Flow<ImportedDeckInfo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertImportedDeckInfo(importedDeckInfo: ImportedDeckInfo)

    @Query("SELECT * from decks WHERE id = :id")
    fun getDeckFlow(id: Int): Flow<Deck>

    @Transaction
    @Query(
        """SELECT * FROM cards WHERE deckId = :deckId
        ORDER BY cards.id"""
    )
    fun getAllCardTypes(deckId: Int): Flow<List<AllCardTypes>>

    @Query(
        """
        DELETE FROM card_info WHERE card_identifier = (
            SELECT cardIdentifier FROM cards WHERE
            deckId = :deckId
        ) AND is_local = 1
        """
    )
    fun deleteCardInfo(deckId: Int)

    @Transaction
    fun updateNewInfo(importedDeckInfo: ImportedDeckInfo, deckId: Int) {
        insertImportedDeckInfo(importedDeckInfo)
        deleteCardInfo(deckId)
    }

}