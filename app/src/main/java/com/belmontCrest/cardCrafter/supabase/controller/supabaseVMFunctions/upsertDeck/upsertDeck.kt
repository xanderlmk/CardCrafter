package com.belmontCrest.cardCrafter.supabase.controller.supabaseVMFunctions.upsertDeck

import android.util.Log
import com.belmontCrest.cardCrafter.model.tablesAndApplication.CT
import com.belmontCrest.cardCrafter.model.tablesAndApplication.Deck
import com.belmontCrest.cardCrafter.supabase.controller.supabaseVMFunctions.ctsToSbCts
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.CARD_UNABLE_TO_UPLOAD
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.DECK_UNABLE_TO_UPLOAD
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.EMPTY_CARD_LIST
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.NOT_DECK_OWNER
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.SUCCESS
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.UNKNOWN_ERROR
import com.belmontCrest.cardCrafter.supabase.model.SBDeckOwner
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement

suspend fun tryUpsertDeck(
    supabase: SupabaseClient, deck: Deck,
    description: String, cts: List<CT>
): Int {
    val user = supabase.auth.currentUserOrNull()
    if (user == null) {
        Log.d("SupabaseViewModel", "User is null!")
        return ReturnValues.NULL_USER
    }
    Log.d("SupabaseViewModel", "Updating Deck..")
    val response = supabase.from("deck")
        .select(columns = Columns.type<SBDeckOwner>()) {
            filter {
                eq("deckUUID", deck.uuid)
            }
        }.decodeSingleOrNull<SBDeckOwner>()
    if (response?.user_id != user.id) {
        Log.d("SupabaseVM", "Not the Deck Owner")
        return NOT_DECK_OWNER
    }

    if (cts.isEmpty()) {
        return EMPTY_CARD_LIST
    }

    val deckToUpsert = ctsToSbCts(deck, cts, description, user.id)

    try {
        val successResponse = supabase.auth.supabaseClient.postgrest.rpc(
            function = "upsert_deck",
            parameters = buildJsonObject {
                put("deck_data", Json.encodeToJsonElement(deckToUpsert))
            }
        )
        println(successResponse.data)
        if (successResponse.data == "true") {
            return SUCCESS
        } else if (successResponse.data == "NOT OWNER") {
            return NOT_DECK_OWNER
        }
    } catch (e: Exception) {
        Log.d("NEW export", "$e")
    }
    return UNKNOWN_ERROR
}