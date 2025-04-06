package com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repository

import com.belmontCrest.cardCrafter.supabase.model.SBDecks
import com.belmontCrest.cardCrafter.supabase.model.SealedCT
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.DeckSignature

interface SupabaseRepository {
    suspend fun insertDeckList(
        deck: SBDecks, cardList: List<SealedCT>,
        reviewAmount: Int, cardAmount: Int,
        onProgress: (Float) -> Unit, total : Int
    )

    suspend fun insertDeckList(
        deck: SBDecks, cardList: List<SealedCT>,
        name: String, reviewAmount: Int, cardAmount: Int,
        onProgress: (Float) -> Unit, total : Int
    )

    suspend fun replaceDeckList(
        deck: SBDecks, cardList: List<SealedCT>,
        reviewAmount: Int, cardAmount: Int, name : String,
        onProgress: (Float) -> Unit, total : Int
    )

    suspend fun validateDeckSignature(deckUUID: String) : DeckSignature?

    suspend fun validateDeckName(name: String) : DeckSignature?

}