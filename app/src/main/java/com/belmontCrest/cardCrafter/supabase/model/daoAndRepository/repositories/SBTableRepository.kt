@file:OptIn(SupabaseExperimental::class)

package com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories

import android.util.Log
import com.belmontCrest.cardCrafter.BuildConfig
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.supabase.controller.converters.localCTToSBCT
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.CC_LESS_THAN_20
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.CTD_ERROR
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.DECK_EXISTS
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.EMPTY_CARD_LIST
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.NOT_DECK_OWNER
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.NULL_CARDS
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.NULL_USER
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.SUCCESS
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.UNKNOWN_ERROR
import com.belmontCrest.cardCrafter.supabase.model.tables.CardsToDisplay
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardDto
import com.belmontCrest.cardCrafter.supabase.model.tables.SBDeckCoOwnerDto
import com.belmontCrest.cardCrafter.supabase.model.tables.SBDeckDto
import com.belmontCrest.cardCrafter.supabase.model.tables.SBDeckOwnerDto
import com.belmontCrest.cardCrafter.supabase.model.tables.SBDeckUUIDDto
import com.belmontCrest.cardCrafter.supabase.model.tables.isEmpty
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.rpc
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.realtime
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
        deck: Deck, description: String, cts: List<CT>, cardsToDisplay: CardsToDisplay,
    ): Pair<String, Int>

    /**suspend fun deleteDeck(uuid: String)*/
    suspend fun upsertDeck(
        deck: Deck, description: String, cts: List<CT>, cardsToDisplay: CardsToDisplay,
        lastUpdatedOn: String
    ): Pair<String, Int>

    suspend fun checkCardList(sbDeckDto: SBDeckDto): Pair<List<SBCardDto>, Int>

    suspend fun getCardsToDisplay(uuid: String): Pair<CardsToDisplay, Int>

    suspend fun connectRealtime(): Boolean

    fun disconnectRealtime(): Boolean
}

class SBTableRepositoryImpl(
    private val sharedSupabase: SupabaseClient,
    private val syncedSupabase: SupabaseClient
) : SBTablesRepository {

    companion object {
        private const val SB_TABLE_REPO = "SBTableRepository"
        private const val SB_DECK_TN = BuildConfig.SB_DECK_TN
        private const val SB_CARD_TN = BuildConfig.SB_CARD_TN
        private const val SB_CTD_TN = BuildConfig.SB_CTD_TN
        private const val SB_DACO_TN = BuildConfig.SB_DACO_TN
    }

    override suspend fun connectRealtime(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                sharedSupabase.useHTTPS
                syncedSupabase.useHTTPS
                if (sharedSupabase.realtime.status.value != Realtime.Status.CONNECTED) {
                    sharedSupabase.realtime.connect()
                }
                if (syncedSupabase.realtime.status.value != Realtime.Status.CONNECTED) {
                    syncedSupabase.realtime.connect()
                }
                true
            } catch (e: SocketException) {
                Log.d("Socket Issue", "SocketException: $e")
                false
            }
        }
    }

    override fun disconnectRealtime(): Boolean {
        try {
            sharedSupabase.realtime.disconnect()
            syncedSupabase.realtime.disconnect()
            return true
        } catch (e: Exception) {
            Log.d("Socket Issue", "SocketException: $e")
            return false
        }
    }

    override suspend fun getDeckList(): Flow<List<SBDeckDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val result = sharedSupabase.from(SB_DECK_TN)
                    .selectAsFlow(SBDeckDto::deckUUID)
                result
            } catch (e: Exception) {
                when (e) {
                    is SocketException -> {
                        Log.d(SB_TABLE_REPO, "Network Error Occurred: $e")
                    }

                    is CancellationException -> {
                        Log.d(SB_TABLE_REPO, "Cancelled: $e")
                    }

                    else -> {
                        Log.d(SB_TABLE_REPO, "Unknown error: $e")
                    }
                }
                flowOf(listOf())
            }
        }
    }

    override suspend fun exportDeck(
        deck: Deck, description: String, cts: List<CT>, cardsToDisplay: CardsToDisplay
    ): Pair<String, Int> {
        return withContext(Dispatchers.IO) {
            val user = sharedSupabase.auth.currentUserOrNull()
            if (user == null) {
                Log.e(SB_TABLE_REPO, "User is null!")
                return@withContext Pair("", NULL_USER)
            }
            val response = sharedSupabase.from(SB_DECK_TN)
                .select(columns = Columns.type<SBDeckUUIDDto>()) {
                    filter {
                        eq("deckUUID", deck.uuid)
                    }
                }
                .decodeSingleOrNull<SBDeckUUIDDto>()

            if (response?.deckUUID == deck.uuid) {
                Log.e(SB_TABLE_REPO, "Deck already Exists!")
                return@withContext Pair("", DECK_EXISTS)
            }

            if (cts.isEmpty()) {
                return@withContext Pair("", EMPTY_CARD_LIST)
            } else if (cts.size < 20) {
                return@withContext Pair("", CC_LESS_THAN_20)
            }
            val deckToExport = localCTToSBCT(
                deck, cts, cardsToDisplay, description, user.id, ""
            )
            try {
                val successResponse = sharedSupabase.postgrest.rpc(
                    function = "import_deck",
                    parameters = buildJsonObject {
                        put("deck_data", Json.encodeToJsonElement(deckToExport))
                    }
                )
                return@withContext Pair(successResponse.data, SUCCESS)
            } catch (e: Exception) {
                Log.e(SB_TABLE_REPO, "$e")
                return@withContext Pair("", UNKNOWN_ERROR)
            }
        }
    }

    override suspend fun upsertDeck(
        deck: Deck, description: String, cts: List<CT>, cardsToDisplay: CardsToDisplay,
        lastUpdatedOn: String
    ): Pair<String, Int> {
        return withContext(Dispatchers.IO) {
            val user = sharedSupabase.auth.currentUserOrNull()
            if (user == null) {
                Log.d("SupabaseViewModel", "User is null!")
                return@withContext Pair("", NULL_USER)
            }
            if (!userCanEdit(deck.uuid, user.id)) {
                Log.d(SB_TABLE_REPO, "Not owner or accepted co-owner")
                return@withContext Pair("", NOT_DECK_OWNER)
            }

            if (cts.isEmpty()) {
                Log.d(SB_TABLE_REPO, "Empty card list")
                return@withContext Pair("", EMPTY_CARD_LIST)
            } else if (cts.size < 20) {
                return@withContext Pair("", CC_LESS_THAN_20)
            }

            val deckToUpsert = if (cardsToDisplay.isEmpty()) {
                val ctd = sharedSupabase.from("cards_to_display")
                    .select(columns = Columns.ALL) {
                        filter {
                            eq("deckUUID", deck.uuid)
                        }
                    }.decodeSingleOrNull<CardsToDisplay>()
                if (ctd == null) {
                    Log.d(SB_TABLE_REPO, "no cards to display")
                    return@withContext Pair("", NULL_CARDS)
                }
                localCTToSBCT(deck, cts, ctd, description, user.id, lastUpdatedOn)
            } else {
                localCTToSBCT(deck, cts, cardsToDisplay, description, user.id, lastUpdatedOn)
            }
            try {
                val successResponse = sharedSupabase.postgrest.rpc(
                    function = "upsert_deck",
                    parameters = buildJsonObject {
                        put("deck_data", Json.encodeToJsonElement(deckToUpsert))
                    }
                )
                return@withContext Pair(successResponse.data, SUCCESS)
            } catch (e: Exception) {
                Log.d("NEW export", "$e")
            }
            return@withContext Pair("", UNKNOWN_ERROR)
        }
    }

    override suspend fun checkCardList(sbDeckDto: SBDeckDto): Pair<List<SBCardDto>, Int> {
        return withContext(Dispatchers.IO) {
            val cardList = sharedSupabase.from(SB_CARD_TN)
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

    override suspend fun getCardsToDisplay(uuid: String): Pair<CardsToDisplay, Int> {
        return withContext(Dispatchers.IO) {
            try {
                val data = sharedSupabase.from(SB_CTD_TN)
                    .select(Columns.ALL) {
                        filter {
                            eq("deckUUID", uuid)
                        }
                    }.decodeSingle<CardsToDisplay>()
                return@withContext Pair(data, SUCCESS)
            } catch (e: Exception) {
                Log.e("CardsToDisplay", "$e")
                Pair(CardsToDisplay(), CTD_ERROR)
            }
        }
    }

    private suspend fun userCanEdit(deckUUID: String, userId: String): Boolean {
        /** Is the user the deck owner? */
        val isOwner = sharedSupabase.from(SB_DECK_TN)
            .select(Columns.type<SBDeckOwnerDto>()) {
                filter {
                    eq("deckUUID", deckUUID)
                    eq("user_id", userId)
                }
            }
            .decodeSingleOrNull<SBDeckOwnerDto>() != null

        if (isOwner) return true

        /** Is the user an accepted co-owner? */
        val isCoOwner = sharedSupabase.from(SB_DACO_TN)
            .select(Columns.raw("co_owner_id")) {
                filter {
                    eq("deckUUID", deckUUID)
                    eq("co_owner_id", userId)
                    eq("status", "accepted")
                }
            }
            .decodeSingleOrNull<SBDeckCoOwnerDto>() != null

        return isCoOwner
    }

}