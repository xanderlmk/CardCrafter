package com.belmontCrest.cardCrafter.supabase.controller.supabaseVMFunctions.insertDeck

import android.util.Log
import com.belmontCrest.cardCrafter.model.tablesAndApplication.CT
import com.belmontCrest.cardCrafter.model.tablesAndApplication.Deck
import com.belmontCrest.cardCrafter.supabase.controller.supabaseVMFunctions.returnDeckToExport
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.DECK_EXISTS
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.EMPTY_CARD_LIST
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.NULL_USER
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.SUCCESS
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.UNKNOWN_ERROR
import com.belmontCrest.cardCrafter.supabase.model.SBDeckUUID
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.rpc
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement

suspend fun tryExportDeck(
    supabase: SupabaseClient, deck: Deck,
    description: String, cts: List<CT>
): Int {
    val user = supabase.auth.currentUserOrNull()
    if (user == null) {
        Log.d("SupabaseViewModel", "User is null!")
        return NULL_USER
    }

    val response = supabase.from("deck")
        .select(columns = Columns.type<SBDeckUUID>()) {
            filter {
                eq("deckUUID", deck.uuid)
            }
        }
        .decodeSingleOrNull<SBDeckUUID>()

    if (response?.deckUUID == deck.uuid) {
        Log.d("SupabaseVM", "Deck already Exists!")
        return DECK_EXISTS
    }

    if (cts.isEmpty()) {
        return EMPTY_CARD_LIST
    }

    val deckToExport = returnDeckToExport(
        deck, cts, description, user.id
    )
    try {
        val successResponse = supabase.auth.supabaseClient.postgrest.rpc(
            function = "import_deck",
            parameters = buildJsonObject {
                put("deck_data", Json.encodeToJsonElement(deckToExport))
            }
        )
        if (successResponse.data == "true") {
            return SUCCESS
        }
    } catch (e: Exception) {
        Log.d("NEW export", "$e")
    }

    return UNKNOWN_ERROR
}
