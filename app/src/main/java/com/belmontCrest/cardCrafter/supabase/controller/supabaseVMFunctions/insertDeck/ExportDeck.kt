package com.belmontCrest.cardCrafter.supabase.controller.supabaseVMFunctions.insertDeck

import android.util.Log
import com.belmontCrest.cardCrafter.model.tablesAndApplication.CT
import com.belmontCrest.cardCrafter.model.tablesAndApplication.Deck
import com.belmontCrest.cardCrafter.supabase.model.CARD_UNABLE_TO_UPLOAD
import com.belmontCrest.cardCrafter.supabase.model.DECK_EXISTS
import com.belmontCrest.cardCrafter.supabase.model.DECK_UNABLE_TO_UPLOAD
import com.belmontCrest.cardCrafter.supabase.model.SBDeckUUID
import com.belmontCrest.cardCrafter.supabase.model.SBDecks
import com.belmontCrest.cardCrafter.supabase.model.SUCCESS
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun tryExportDeck(
    supabase : SupabaseClient, deck: Deck, id : String,
    description : String, cts : List<CT>
): Int {
    return withContext(Dispatchers.IO) {
        val response = supabase.from("deck")
            .select(columns = Columns.type<SBDeckUUID>()) {
                filter {
                    eq("deckUUID", deck.uuid)
                }
            }
            .decodeSingleOrNull<SBDeckUUID>()
        if (response?.deckUUID == deck.uuid) {
            Log.d("SupabaseVM", "Deck already Exists!")
            return@withContext DECK_EXISTS
        }
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
            return@withContext DECK_UNABLE_TO_UPLOAD
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
            return@withContext CARD_UNABLE_TO_UPLOAD
        }
        return@withContext SUCCESS
    }
}
