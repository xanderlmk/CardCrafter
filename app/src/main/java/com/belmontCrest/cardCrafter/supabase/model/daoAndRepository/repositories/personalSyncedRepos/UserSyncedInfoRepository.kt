package com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.personalSyncedRepos

import com.belmontCrest.cardCrafter.controller.cardHandlers.mapAllCardTypesToCTs
import com.belmontCrest.cardCrafter.localDatabase.tables.SyncedDeckInfo
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.daos.SyncedDeckInfoDao
import com.belmontCrest.cardCrafter.supabase.model.tables.DeckWithLotsCards
import com.belmontCrest.cardCrafter.supabase.model.tables.ListOfDecks

interface UserSyncedInfoRepository {
    suspend fun getSyncInfo(uuid: String): SyncedDeckInfo?
    suspend fun insertOrUpdateSyncInfo(syncInfo: SyncedDeckInfo)
    suspend fun getDB(): List<DeckWithLotsCards>
    suspend fun replaceDB(allDecks: ListOfDecks)
}

class OfflineUserSyncedInfoRepository(private val syncedDeckInfoDao: SyncedDeckInfoDao) :
    UserSyncedInfoRepository {
    override suspend fun getSyncInfo(uuid: String) = syncedDeckInfoDao.getSyncInfo(uuid)
    override suspend fun insertOrUpdateSyncInfo(syncInfo: SyncedDeckInfo) =
        syncedDeckInfoDao.insertOrUpdateSyncInfo(syncInfo)

    override suspend fun getDB() = syncedDeckInfoDao.getDB().map { deckWithCardTypes ->
        val cts = mapAllCardTypesToCTs(deckWithCardTypes.cardTypes)
        DeckWithLotsCards(deckWithCardTypes.deck, cts)
    }

    override suspend fun replaceDB(allDecks: ListOfDecks) = syncedDeckInfoDao.replaceDB(allDecks)
}
