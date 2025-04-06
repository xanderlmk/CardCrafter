package com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repository

import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.SupabaseDao
import com.belmontCrest.cardCrafter.supabase.model.SBDecks
import com.belmontCrest.cardCrafter.supabase.model.SealedCT

class OfflineSupabaseRepository(
    private val supabaseDao: SupabaseDao
) : SupabaseRepository {
    override suspend fun insertDeckList(
        deck: SBDecks, cardList: List<SealedCT>,
        reviewAmount: Int, cardAmount: Int,
        onProgress: (Float) -> Unit, total: Int
    ) = supabaseDao.insertDeckList(
        deck, cardList, reviewAmount, cardAmount,
        onProgress = {
            onProgress(it)
        }, total
    )

    override suspend fun insertDeckList(
        deck: SBDecks, cardList: List<SealedCT>,
        name: String, reviewAmount: Int, cardAmount: Int,
        onProgress: (Float) -> Unit, total: Int
    ) = supabaseDao.insertDeckList(
        deck, cardList, name, reviewAmount, cardAmount,
        onProgress = {
            onProgress(it)
        }, total
    )

    override suspend fun replaceDeckList(
        deck: SBDecks, cardList: List<SealedCT>,
        reviewAmount: Int, cardAmount: Int, name: String,
        onProgress: (Float) -> Unit, total: Int
    ) = supabaseDao.replaceDeckList(
        deck, cardList, reviewAmount, cardAmount, name,
        onProgress = {
            onProgress(it)
        }, total
    )

    override suspend fun validateDeckSignature(deckUUID: String) = try {
        supabaseDao.validateDeckSignature(deckUUID)
    } catch (e: Exception) {
        throw e
    }
    override suspend fun validateDeckName(name: String) = try {
        supabaseDao.validateDeckName(name)
    } catch (e: Exception) {
        throw e
    }
}