package com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories

import com.belmontCrest.cardCrafter.supabase.model.tables.SBDeckDto
import com.belmontCrest.cardCrafter.supabase.model.tables.SealedCTToImport
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.daos.DeckSignature

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