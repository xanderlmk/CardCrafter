package com.belmontCrest.cardCrafter.supabase.controller.supabaseVMFunctions

import android.util.Log
import com.belmontCrest.cardCrafter.model.tablesAndApplication.CT
import com.belmontCrest.cardCrafter.model.tablesAndApplication.Deck
import com.belmontCrest.cardCrafter.supabase.model.SBDecks
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

private const val DECK_UNABLE_TO_UPLOAD = 4
private const val CARD_UNABLE_TO_UPLOAD = 2
private const val SUCCESS = 0
suspend fun tryExportDeck(
    supabase : SupabaseClient, deck: Deck, id : String,
    description : String, cts : List<CT>
): Int {
    try {
        supabase.from("deck")
            .insert(
                SBDecks(
                    deckUUID = deck.uuid,
                    user_id = id,
                    name = deck.name,
                    description = description
                )
            )
    } catch (e: Exception) {
        Log.d("SupabaseViewModel", "Couldn't upload Deck : $e")
        /** return 4 means the deck did not upload. */
        return DECK_UNABLE_TO_UPLOAD
    }
    val insertCards = insertCTList(cts, supabase, deck)

    if (!insertCards) {
        supabase.from("deck")
            .delete {
                filter {
                    eq("deckUUID", deck.uuid)
                }
            }

        Log.d("SupabaseViewModel", "Couldn't upload cards!")
        /** return 2 means some cardType upload was unsuccessful. */
        return CARD_UNABLE_TO_UPLOAD
    }

    return SUCCESS
}
