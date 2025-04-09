@file:OptIn(SupabaseExperimental::class)

package com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repository

import android.util.Log
import com.belmontCrest.cardCrafter.BuildConfig
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.supabase.controller.supabaseVMFunctions.converters.ctsToSbCts
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.CC_LESS_THAN_20
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.DECK_EXISTS
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.EMPTY_CARD_LIST
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.NOT_DECK_OWNER
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.NULL_USER
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.SUCCESS
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.UNKNOWN_ERROR
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardDto
import com.belmontCrest.cardCrafter.supabase.model.tables.SBDeckDto
import com.belmontCrest.cardCrafter.supabase.model.tables.SBDeckOwnerDto
import com.belmontCrest.cardCrafter.supabase.model.tables.SBDeckUUIDDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.rpc
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import java.net.SocketException
import java.util.concurrent.CancellationException

interface SBTablesRepository {
    suspend fun getDeckList(): Flow<List<SBDeckDto>>

    suspend fun exportDeck(
        deck: Deck, description: String, cts: List<CT>
    ): Int

    /**suspend fun deleteDeck(uuid: String)*/
    suspend fun upsertDeck(
        deck: Deck, description: String, cts: List<CT>
    ): Int

    suspend fun checkCardList(sbDeckDto: SBDeckDto): Pair<List<SBCardDto>, Int>
}

class SBTableRepositoryImpl(
    private val supabase: SupabaseClient
) : SBTablesRepository {
    override suspend fun getDeckList(): Flow<List<SBDeckDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val result = supabase.from(SBDeckTN)
                    .selectAsFlow(SBDeckDto::deckUUID)
                result
            } catch (e: Exception) {
                when (e) {
                    is SocketException -> {
                        Log.d("SupabaseVm", "Network Error Occurred: $e")
                    }

                    is CancellationException -> {
                        Log.d("SupabaseVm", "Cancelled: $e")
                    }

                    else -> {
                        Log.d("SupabaseVm", "Unknown error: $e")
                    }
                }
                flowOf(listOf())
            }
        }
    }

    override suspend fun exportDeck(
        deck: Deck, description: String, cts: List<CT>
    ): Int {
        return withContext(Dispatchers.IO) {
            val user = supabase.auth.currentUserOrNull()
            if (user == null) {
                Log.d("SupabaseViewModel", "User is null!")
                return@withContext NULL_USER
            }
            val response = supabase.from(SBDeckTN)
                .select(columns = Columns.type<SBDeckUUIDDto>()) {
                    filter {
                        eq("deckUUID", deck.uuid)
                    }
                }
                .decodeSingleOrNull<SBDeckUUIDDto>()

            if (response?.deckUUID == deck.uuid) {
                Log.d("SupabaseVM", "Deck already Exists!")
                return@withContext DECK_EXISTS
            }

            if (cts.isEmpty()) {
                return@withContext EMPTY_CARD_LIST
            } else if (cts.size < 20) {
                return@withContext CC_LESS_THAN_20
            }
            val deckToExport = ctsToSbCts(
                deck, cts, description, user.id
            )
            try {
                val successResponse = supabase.postgrest.rpc(
                    function = "import_deck",
                    parameters = buildJsonObject {
                        put("deck_data", Json.encodeToJsonElement(deckToExport))
                    }
                )
                if (successResponse.data == "true") {
                    return@withContext SUCCESS
                } else if (successResponse.data == "Card Count less than 20.") {
                    return@withContext CC_LESS_THAN_20
                }
            } catch (e: Exception) {
                Log.d("NEW export", "$e")
            }
            return@withContext UNKNOWN_ERROR
        }
    }

    override suspend fun upsertDeck(
        deck: Deck, description: String, cts: List<CT>
    ): Int {
        return withContext(Dispatchers.IO) {
            val user = supabase.auth.currentUserOrNull()
            if (user == null) {
                Log.d("SupabaseViewModel", "User is null!")
                return@withContext NULL_USER
            }
            val response = supabase.from(SBDeckTN)
                .select(columns = Columns.type<SBDeckOwnerDto>()) {
                    filter {
                        eq("deckUUID", deck.uuid)
                    }
                }.decodeSingleOrNull<SBDeckOwnerDto>()
            if (response?.user_id != user.id) {
                Log.d("SupabaseVM", "Not the Deck Owner")
                return@withContext NOT_DECK_OWNER
            }

            if (cts.isEmpty()) {
                return@withContext EMPTY_CARD_LIST
            } else if (cts.size < 20) {
                return@withContext CC_LESS_THAN_20
            }

            val deckToUpsert = ctsToSbCts(deck, cts, description, user.id)

            try {
                val successResponse = supabase.postgrest.rpc(
                    function = "upsert_deck",
                    parameters = buildJsonObject {
                        put("deck_data", Json.encodeToJsonElement(deckToUpsert))
                    }
                )
                println(successResponse.data)
                if (successResponse.data == "true") {
                    return@withContext SUCCESS
                } else if (successResponse.data == "NOT OWNER") {
                    return@withContext NOT_DECK_OWNER
                } else if (successResponse.data == "Card Count less than 20.") {
                    return@withContext CC_LESS_THAN_20
                }
            } catch (e: Exception) {
                Log.d("NEW export", "$e")
            }
            return@withContext UNKNOWN_ERROR
        }
    }

    override suspend fun checkCardList(sbDeckDto: SBDeckDto): Pair<List<SBCardDto>, Int> {
        return withContext(Dispatchers.IO) {
            val cardList = supabase.from(SBCardTN)
                .select(columns = Columns.ALL) {
                    filter {
                        eq("deckUUID", sbDeckDto.deckUUID)
                    }
                }
                .decodeList<SBCardDto>()
            return@withContext if (cardList.isEmpty()) {
                Pair(listOf(), EMPTY_CARD_LIST)
            } else {
                Pair(cardList, SUCCESS)
            }
        }
    }
}

private const val SBDeckTN = BuildConfig.SB_DECK_TN
private const val SBCardTN = BuildConfig.SB_CARD_TN