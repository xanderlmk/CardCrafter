package com.belmontCrest.cardCrafter.supabase.controller.supabaseVMFunctions.exportDeck

import android.util.Log
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.supabase.controller.supabaseVMFunctions.converters.ctsToSbCts
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.CC_LESS_THAN_20
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.DECK_EXISTS
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.EMPTY_CARD_LIST
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.NULL_USER
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.SUCCESS
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.UNKNOWN_ERROR
import com.belmontCrest.cardCrafter.supabase.model.tables.SBDeckUUIDDto
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
        .select(columns = Columns.type<SBDeckUUIDDto>()) {
            filter {
                eq("deckUUID", deck.uuid)
            }
        }
        .decodeSingleOrNull<SBDeckUUIDDto>()

    if (response?.deckUUID == deck.uuid) {
        Log.d("SupabaseVM", "Deck already Exists!")
        return DECK_EXISTS
    }

    if (cts.isEmpty()) {
        return EMPTY_CARD_LIST
    } else if(cts.size < 20){
        return CC_LESS_THAN_20
    }

    val deckToExport = ctsToSbCts(
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
        } else if (successResponse.data == "Card Count less than 20.") {
            return CC_LESS_THAN_20
        }
    } catch (e: Exception) {
        Log.d("NEW export", "$e")
    }
    return UNKNOWN_ERROR
}
