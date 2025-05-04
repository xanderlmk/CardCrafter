package com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.ownerRepos

import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.daos.MergeDecksDao
import com.belmontCrest.cardCrafter.supabase.model.tables.SBDeckDto
import com.belmontCrest.cardCrafter.supabase.model.tables.SealedCTToImport

interface MergeDecksRepository {
    suspend fun mergeDeck(
        sbDeckDto: SBDeckDto, remoteCL: List<SealedCTToImport>, onProgress: (Float) -> Unit
    )
}

class OfflineMergeDecksRepository(
    private val mergeDecksDao: MergeDecksDao
) : MergeDecksRepository {
    override suspend fun mergeDeck(
        sbDeckDto: SBDeckDto, remoteCL: List<SealedCTToImport>, onProgress: (Float) -> Unit
    ) = mergeDecksDao.mergeDeck(
        sbDeckDto, remoteCL
    ) { onProgress(it) }
}