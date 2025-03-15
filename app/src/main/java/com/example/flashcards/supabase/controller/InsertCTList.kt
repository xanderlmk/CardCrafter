package com.example.flashcards.supabase.controller

import android.util.Log
import com.example.flashcards.model.tablesAndApplication.CT
import com.example.flashcards.model.tablesAndApplication.Deck
import io.github.jan.supabase.SupabaseClient

suspend fun insertCTList(
    cts: List<CT>, supabase: SupabaseClient, deck: Deck
) : Boolean {
    cts.map { ct ->
        when (ct) {
            is CT.Basic -> {
                try {
                    val response = insertCard(
                        supabase,
                        uuid = deck.uuid,
                        type = ct.card.type
                    )
                    insertBasicCT(response.id, supabase, ct.basicCard)
                } catch (e: Exception) {
                    Log.d(
                        "SupabaseViewModel", "Unable to upload Card: $e"
                    )
                    return false
                }

            }
            is CT.Hint -> {
                try {
                    val response = insertCard(
                        supabase,
                        uuid = deck.uuid,
                        type = ct.card.type
                    )
                    insertHintCT(response.id, supabase, ct.hintCard)
                } catch (e: Exception) {
                    Log.d(
                        "SupabaseViewModel", "Unable to upload Card: $e"
                    )
                    return false
                }
            }

            is CT.ThreeField -> {
                try {
                    val response = insertCard(
                        supabase,
                        uuid = deck.uuid,
                        type = ct.card.type
                    )
                    insertThreeCT(response.id, supabase, ct.threeFieldCard)
                } catch (e: Exception) {
                    Log.d(
                        "SupabaseViewModel", "Unable to upload Card: $e"
                    )
                    return false
                }
            }

            is CT.MultiChoice -> {
                try {
                    val response = insertCard(
                        supabase,
                        uuid = deck.uuid,
                        type = ct.card.type
                    )
                    insertMultiCT(response.id, supabase, ct.multiChoiceCard)
                } catch (e: Exception) {
                    Log.d(
                        "SupabaseViewModel", "Unable to upload Card: $e"
                    )
                    return false
                }
            }
            is CT.Math -> {
                try {
                    val response = insertCard(
                        supabase,
                        uuid = deck.uuid,
                        type = ct.card.type
                    )
                    insertMathCT(response.id, supabase, ct.mathCard)
                } catch (e: Exception) {
                    Log.d(
                        "SupabaseViewModel", "Unable to upload Card: $e"
                    )
                    return false
                }
            }
        }
    }
    return true
}