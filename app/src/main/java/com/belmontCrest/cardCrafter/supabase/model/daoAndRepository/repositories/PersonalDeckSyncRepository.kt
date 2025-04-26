package com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories

import android.util.Log

import com.belmontCrest.cardCrafter.supabase.model.ReturnValues
import com.belmontCrest.cardCrafter.supabase.model.tables.DeckWithLotsCards
import com.belmontCrest.cardCrafter.supabase.model.tables.ListOfDecks
import com.belmontCrest.cardCrafter.supabase.model.tables.PDUpdatedOn
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
    suspend fun syncUserDecks(decks: List<DeckWithLotsCards>): Pair<String, Int>
    suspend fun fetchRemoteDecks(): Pair<List<PersonalDecks>, Int>
    suspend fun getUserUUID(): String?
    suspend fun getLastUpdatedOn(): Pair<PDUpdatedOn?, Int>
}


class PersonalDeckSyncRepositoryImpl(
    private val syncedSupabase: SupabaseClient
) : PersonalDeckSyncRepository {

    companion object {
        private const val PERSONAL_DECKS_TABLE = "personal_decks"
        private val pgFmt = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    }


    override suspend fun syncUserDecks(decks: List<DeckWithLotsCards>): Pair<String, Int> {
        return withContext(Dispatchers.IO) {
            val user =
                syncedSupabase.auth.currentUserOrNull() ?: return@withContext Pair("", ReturnValues.NULL_USER)

            try {

                if (decks.isEmpty()) {
                    return@withContext Pair("", ReturnValues.EMPTY_CARD_LIST)
                }


                val listOfDecks = ListOfDecks(decks)
                val json = Json
                val jsonElement = json.encodeToJsonElement(ListOfDecks.serializer(), listOfDecks)
                val jsonObject = jsonElement.jsonObject

                val currentTimestamp = OffsetDateTime.now().format(pgFmt)

                val personalDecks = PersonalDecks(
                    user_id = user.id,
                    data = jsonObject,
                )

                //check if user already has personal decks synced
                val existingDeck = syncedSupabase.from(PERSONAL_DECKS_TABLE)
                    .select(Columns.ALL) {
                        filter {
                            eq("user_id", user.id)
                        }
                    }.decodeSingleOrNull<PersonalDecks>()

                if (existingDeck == null) {
                    //insert new record
                    val result = syncedSupabase.from(PERSONAL_DECKS_TABLE)
                        .insert(personalDecks)
                        .decodeSingle<PersonalDecks>()
                        return@withContext Pair(result.updated_on, ReturnValues.SUCCESS)
                } else {
                    Log.d(PERSONAL_DECKS_TABLE, existingDeck.updated_on)
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
                    return@withContext Pair(currentTimestamp, ReturnValues.SUCCESS)
                }

            } catch (e: Exception) {
                Log.e("PersonalDeckSyncRepo", "Error syncing decks: ${e.message}")
                return@withContext Pair("", ReturnValues.UNKNOWN_ERROR)
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

    override suspend fun getUserUUID(): String? {
        return withContext(Dispatchers.IO) {
            try {
                syncedSupabase.auth.currentUserOrNull()?.id
            } catch (e: Exception) {
                Log.e("PersonalDeckSyncRepo", "Error, Couldn't get user ID: ${e.message}")
                null
            }
        }
    }

    override suspend fun getLastUpdatedOn(): Pair<PDUpdatedOn?, Int> {
        return withContext(Dispatchers.IO) {
            try {
                val user = syncedSupabase.auth.currentUserOrNull() ?: return@withContext Pair(
                    null, ReturnValues.NULL_USER
                )
                val result =
                    syncedSupabase.from(PERSONAL_DECKS_TABLE).select(Columns.type<PDUpdatedOn>()) {
                        filter {
                            eq("user_id", user.id)
                        }
                    }.decodeSingleOrNull<PDUpdatedOn>()
                Pair(result, ReturnValues.SUCCESS)
            } catch (e: Exception) {
                Log.e("", "${e.message}")
                Pair(null, ReturnValues.UNKNOWN_ERROR)
            }
        }
    }

}