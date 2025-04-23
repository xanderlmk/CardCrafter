package com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

import com.belmontCrest.cardCrafter.supabase.model.ReturnValues
import com.belmontCrest.cardCrafter.supabase.model.tables.DeckWithLotsCards
import com.belmontCrest.cardCrafter.supabase.model.tables.ListOfDecks
import com.belmontCrest.cardCrafter.supabase.model.tables.PersonalDecks
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

import kotlinx.serialization.json.jsonObject
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

interface PersonalDeckSyncRepository {
    suspend fun syncUserDecks(decks: List<DeckWithLotsCards>): Int
    suspend fun fetchRemoteDecks(): Pair<List<PersonalDecks>, Int>
}

@RequiresApi(Build.VERSION_CODES.O)

class PersonalDeckSyncRepositoryImpl(

    private val syncedSupabase: SupabaseClient
) : PersonalDeckSyncRepository {

    companion object {
        private const val PERSONAL_DECKS_TABLE = "personal_decks"
        private val pgFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSxx")
    }

    override suspend fun syncUserDecks(decks: List<DeckWithLotsCards>): Int {
        return withContext(Dispatchers.IO) {
            val user = syncedSupabase.auth.currentUserOrNull()
                ?: return@withContext ReturnValues.NULL_USER

            try {

                if (decks.isEmpty()) {
                    return@withContext ReturnValues.EMPTY_CARD_LIST
                }


                val listOfDecks = ListOfDecks(decks)
                val json = Json
                val jsonElement = json.encodeToJsonElement(ListOfDecks.serializer(), listOfDecks)
                val jsonObject = jsonElement.jsonObject

                val currentTimestamp = OffsetDateTime.now().format(pgFmt)

                val personalDecks = PersonalDecks(
                    user_id = user.id,
                    data = jsonObject,
                    created_at = currentTimestamp,
                    updated_on = currentTimestamp
                )

                //check if user already has personal decks synced
                val existingDecks = syncedSupabase.from(PERSONAL_DECKS_TABLE)
                    .select(Columns.ALL) {
                        filter {
                            eq("user_id", user.id)
                        }
                    }.decodeList<PersonalDecks>()

                if (existingDecks.isEmpty()) {
                    //insert new record
                    syncedSupabase.from(PERSONAL_DECKS_TABLE)
                        .insert(personalDecks)
                } else {
                    //updates existing record
                    syncedSupabase.from(PERSONAL_DECKS_TABLE)
                        .update({
                            set("data", jsonObject)
                            set("updated_on", currentTimestamp)
                        }) {
                            filter {
                                eq("user_id", user.id)
                            }
                        }
                }

                return@withContext ReturnValues.SUCCESS
            } catch (e: Exception) {
                Log.e("PersonalDeckSyncRepo", "Error syncing decks: ${e.message}")
                return@withContext ReturnValues.UNKNOWN_ERROR
            }
        }
    }

    override suspend fun fetchRemoteDecks(): Pair<List<PersonalDecks>, Int> {
        return withContext(Dispatchers.IO) {
            val user = syncedSupabase.auth.currentUserOrNull()
                ?: return@withContext Pair(emptyList(), ReturnValues.NULL_USER)

            try {
                val personalDecks = syncedSupabase.from(PERSONAL_DECKS_TABLE)
                    .select(Columns.ALL) {
                        filter {
                            eq("user_id", user.id)
                        }
                    }.decodeList<PersonalDecks>()

                Pair(personalDecks, ReturnValues.SUCCESS)
            } catch (e: Exception) {
                Log.e("PersonalDeckSyncRepo", "Error fetching remote decks: ${e.message}")
                Pair(emptyList(), ReturnValues.UNKNOWN_ERROR)
            }
        }
    }


}