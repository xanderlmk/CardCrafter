package com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.ownerRepos

import android.util.Log
import com.belmontCrest.cardCrafter.controller.cardHandlers.toCTList
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.localDatabase.tables.ImportedDeckInfo
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.daos.ExportToSBDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface ExportRepository {
    fun getImportedDeckInfo(id: Int): Flow<ImportedDeckInfo>

    fun getDeckStream(id: Int): Flow<Deck>

    fun getAllCardTypes(deckId: Int): Flow<List<CT>>

    fun updateNewInfo(importedDeckInfo: ImportedDeckInfo, deckId: Int)
}

class OfflineExportRepository(private val exportToSBDao: ExportToSBDao) : ExportRepository {
    override fun getImportedDeckInfo(id: Int) = exportToSBDao.getImportedDeckInfo(id)

    override fun getDeckStream(id: Int) = exportToSBDao.getDeckFlow(id)

    override fun getAllCardTypes(deckId: Int) = exportToSBDao.getAllCardTypes(deckId).map {
        try {
            it.toCTList()
        } catch (e: IllegalStateException) {
            Log.d("CardTypeRepository", "$e")
            listOf()
        }
    }

    override fun updateNewInfo(importedDeckInfo: ImportedDeckInfo, deckId: Int) =
        exportToSBDao.updateNewInfo(importedDeckInfo, deckId)

}