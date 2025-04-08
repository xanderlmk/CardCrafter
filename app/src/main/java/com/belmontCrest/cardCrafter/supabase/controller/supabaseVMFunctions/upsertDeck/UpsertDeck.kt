package com.belmontCrest.cardCrafter.supabase.controller.supabaseVMFunctions.upsertDeck

import android.util.Log
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.supabase.controller.supabaseVMFunctions.ctsToSbCts
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.CC_LESS_THAN_20
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.EMPTY_CARD_LIST
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.NOT_DECK_OWNER
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.SUCCESS
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.UNKNOWN_ERROR
import com.belmontCrest.cardCrafter.supabase.model.SBDeckOwnerDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.rpc
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
    val response = supabase.from("deck")
        .select(columns = Columns.type<SBDeckOwnerDto>()) {
            filter {
                eq("deckUUID", deck.uuid)
            }
        }.decodeSingleOrNull<SBDeckOwnerDto>()
    if (response?.user_id != user.id) {
        Log.d("SupabaseVM", "Not the Deck Owner")
        return NOT_DECK_OWNER
    }

    if (cts.isEmpty()) {
        return EMPTY_CARD_LIST
    } else if(cts.size < 20){
        return CC_LESS_THAN_20
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
        }  else if (successResponse.data == "Card Count less than 20.") {
            return CC_LESS_THAN_20
        }
    } catch (e: Exception) {
        Log.d("NEW export", "$e")
    }
    return UNKNOWN_ERROR
}