package com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories

import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.daos.DeckSignature
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.daos.SupabaseDao
import com.belmontCrest.cardCrafter.supabase.model.tables.SBDeckDto
import com.belmontCrest.cardCrafter.supabase.model.tables.SealedCTToImport


interface SupabaseToRoomRepository {
    suspend fun insertDeckList(
        deck: SBDeckDto, cardList: List<SealedCTToImport>,
        name: String, reviewAmount: Int, cardAmount: Int,
        onProgress: (Float) -> Unit, total : Int
    )

    suspend fun replaceDeckList(
        deck: SBDeckDto, cardList: List<SealedCTToImport>,
        reviewAmount: Int, cardAmount: Int, name : String,
        onProgress: (Float) -> Unit, total : Int
    )

    suspend fun validateDeckSignature(deckUUID: String) : DeckSignature?

    suspend fun validateDeckName(name: String) : DeckSignature?

}

class OfflineSupabaseToRoomRepository(
    private val supabaseDao: SupabaseDao
) : SupabaseToRoomRepository {

    override suspend fun insertDeckList(
        deck: SBDeckDto, cardList: List<SealedCTToImport>,
        name: String, reviewAmount: Int, cardAmount: Int,
        onProgress: (Float) -> Unit, total: Int
    ) = supabaseDao.insertDeckList(
        deck, cardList, name, reviewAmount, cardAmount,
        onProgress = {
            onProgress(it)
        }, total
    )

    override suspend fun replaceDeckList(
        deck: SBDeckDto, cardList: List<SealedCTToImport>,
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