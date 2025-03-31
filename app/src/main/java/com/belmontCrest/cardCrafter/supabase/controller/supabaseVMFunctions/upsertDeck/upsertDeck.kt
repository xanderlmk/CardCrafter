package com.belmontCrest.cardCrafter.supabase.controller.supabaseVMFunctions.upsertDeck

import android.util.Log
import com.belmontCrest.cardCrafter.model.tablesAndApplication.CT
import com.belmontCrest.cardCrafter.model.tablesAndApplication.Deck
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.CARD_UNABLE_TO_UPLOAD
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.DECK_UNABLE_TO_UPLOAD
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.NOT_DECK_OWNER
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.SUCCESS
import com.belmontCrest.cardCrafter.supabase.model.SBDeckOwner
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun tryUpsertDeck(
    supabase: SupabaseClient, deck: Deck,
    userId: String, description: String, cts: List<CT>
): Int {
    return withContext(Dispatchers.IO) {
        try {
            Log.d("SupabaseViewModel", "Updating Deck..")
            val response = supabase.from("deck")
                .select(columns = Columns.type<SBDeckOwner>()) {
                    filter {
                        eq("deckUUID", deck.uuid)
                    }
                }.decodeSingleOrNull<SBDeckOwner>()
            if (response?.user_id != userId) {
                Log.d("SupabaseVM", "Not the Deck Owner")
                return@withContext NOT_DECK_OWNER
            }
            supabase.from("deck")
                .update(
                    {
                        set("name", deck.name)
                        set("description", description)
                    }
                ) {
                    filter {
                        eq("deckUUID", deck.uuid)
                    }
                }
            Log.d("SupabaseViewModel", "Deck updated...")

            val upsertCards = upsertCTList(cts, supabase, deck)
            if (!upsertCards) {
                Log.d("SupabaseViewModel", "Couldn't update cards!")
                /** return 2 means some cardType upload was unsuccessful. */
                return@withContext CARD_UNABLE_TO_UPLOAD
            }
            return@withContext SUCCESS
        } catch (e: Exception) {
            Log.d("SupabaseViewModel", "Couldn't upload Deck : $e")
            /** return 4 means the deck did not upload. */
            return@withContext DECK_UNABLE_TO_UPLOAD
        }
    }
}