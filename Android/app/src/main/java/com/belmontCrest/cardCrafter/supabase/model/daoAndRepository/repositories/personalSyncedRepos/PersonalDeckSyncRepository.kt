package com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.personalSyncedRepos

import android.util.Log
import com.belmontCrest.cardCrafter.BuildConfig
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
    suspend fun fetchRemoteDecks(): Pair<PersonalDecks?, Int>
    fun getUserUUID(): String?
    suspend fun getLastUpdatedOn(): Pair<PDUpdatedOn?, Int>
}

class PersonalDeckSyncRepositoryImpl(
    private val supabase: SupabaseClient
) : PersonalDeckSyncRepository {

    companion object {
        private const val SB_PD_TN = BuildConfig.SB_PD_TN
        private val pgFmt = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        private val json = Json
        private const val PDC_REPO = "PersonalDeckSyncRepo"
    }

    override suspend fun syncUserDecks(decks: List<DeckWithLotsCards>): Pair<String, Int> {
        return withContext(Dispatchers.IO) {
            val user =
                supabase.auth.currentUserOrNull() ?: return@withContext Pair(
                    "", ReturnValues.NULL_USER
                )
            try {
                if (decks.isEmpty()) return@withContext Pair("", ReturnValues.NO_DECKS_TO_SYNC)
                val listOfDecks = ListOfDecks(decks)

                val jsonElement = json.encodeToJsonElement(ListOfDecks.serializer(), listOfDecks)
                val jsonObject = jsonElement.jsonObject

                val currentTimestamp = OffsetDateTime.now().format(pgFmt)

                val personalDecks = PersonalDecks(userId = user.id, data = jsonObject)

                // check if user already has personal decks synced
                val existingDeck = supabase.from(SB_PD_TN)
                    .select(Columns.ALL) {
                        filter { eq("user_id", user.id) }
                    }.decodeSingleOrNull<PersonalDecks>()

                if (existingDeck == null) {
                    //insert new record
                    val result = supabase.from(SB_PD_TN)
                        .insert(personalDecks) {
                            select(Columns.type<PDUpdatedOn>())
                        }
                        .decodeSingle<PDUpdatedOn>()

                    return@withContext Pair(result.updatedOn, ReturnValues.SUCCESS)
                } else {
                    Log.d(SB_PD_TN, existingDeck.updatedOn)
                    //updates existing record
                    supabase.from(SB_PD_TN)
                        .update({
                            set("data", jsonObject)
                            set("updated_on", currentTimestamp)
                        }) {
                            filter { eq("user_id", user.id) }
                        }
                    return@withContext Pair(currentTimestamp, ReturnValues.SUCCESS)
                }

            } catch (e: Exception) {
                Log.e(PDC_REPO, "Error syncing decks: ${e.printStackTrace()}")
                return@withContext Pair("", ReturnValues.UNKNOWN_ERROR)
            }
        }
    }

    override suspend fun fetchRemoteDecks(): Pair<PersonalDecks?, Int> {
        return withContext(Dispatchers.IO) {
            val user = supabase.auth.currentUserOrNull()
                ?: return@withContext Pair(null, ReturnValues.NULL_USER)

            try {
                val personalDecks = supabase.from(SB_PD_TN)
                    .select(Columns.ALL) {
                        filter { eq("user_id", user.id) }
                    }.decodeSingleOrNull<PersonalDecks>()
                Pair(personalDecks, ReturnValues.SUCCESS)
            } catch (e: Exception) {
                Log.e(PDC_REPO, "Error fetching remote decks: ${e.printStackTrace()}")
                Pair(null, ReturnValues.UNKNOWN_ERROR)
            }
        }
    }

    override fun getUserUUID() = try {
                supabase.auth.currentUserOrNull()?.id
            } catch (e: Exception) {
                Log.e(PDC_REPO, "Error, Couldn't get user ID: ${e.printStackTrace()}")
                null
            }

    override suspend fun getLastUpdatedOn(): Pair<PDUpdatedOn?, Int> {
        return withContext(Dispatchers.IO) {
            try {
                val user = supabase.auth.currentUserOrNull() ?: return@withContext Pair(
                    null, ReturnValues.NULL_USER
                )
                val result = supabase.from(SB_PD_TN)
                        .select(Columns.type<PDUpdatedOn>()) {
                            filter { eq("user_id", user.id) }
                        }.decodeSingleOrNull<PDUpdatedOn>()
                Pair(result, ReturnValues.SUCCESS)
            } catch (e: Exception) {
                Log.e(PDC_REPO, "${e.printStackTrace()}")
                Pair(null, ReturnValues.UNKNOWN_ERROR)
            }
        }
    }
}