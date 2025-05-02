package com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories

import android.util.Log
import com.belmontCrest.cardCrafter.BuildConfig
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.CANCELLED
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.NETWORK_ERROR
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.NULL_USER
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.SUCCESS
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.UNKNOWN_ERROR
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.USER_NOT_FOUND
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCoOwner
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCoOwnerWithDeck
import com.belmontCrest.cardCrafter.supabase.model.tables.SBDeckDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.postgrest.rpc
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import java.net.SocketException

interface CoOwnerRequestsRepository {
    suspend fun getRequests(): Pair<Flow<List<SBCoOwnerWithDeck>>, Int>

    suspend fun acceptRequest(uuid: String): Int

    suspend fun declineRequest(uuid: String): Int
}

@OptIn(SupabaseExperimental::class, ExperimentalCoroutinesApi::class)
class CoOwnerRequestsRepositoryImpl(
    private val sharedSupabase: SupabaseClient
) : CoOwnerRequestsRepository {
    companion object {
        private const val SB_DACO_TN = BuildConfig.SB_DACO_TN
        private const val SB_DECK_TN = BuildConfig.SB_DECK_TN
        private const val COR_REPO = "CoOwnerRequestsRepository"
    }

    override suspend fun getRequests(): Pair<Flow<List<SBCoOwnerWithDeck>>, Int> {
        return withContext(Dispatchers.IO) {
            try {
                val user = sharedSupabase.auth.currentUserOrNull()
                if (user == null) {
                    return@withContext Pair(flowOf(listOf()), NULL_USER)
                }
                val coOwnerFlow = sharedSupabase.from(SB_DACO_TN).selectAsFlow(
                    primaryKeys = listOf(SBCoOwner::deckUUID, SBCoOwner::coOwnerId),
                    filter = FilterOperation("co_owner_id", FilterOperator.IN, "(${user.id})")
                )
                val deckFlow: Flow<List<SBDeckDto>> =
                    coOwnerFlow.map { owners -> owners.map { it.deckUUID } }
                        .flatMapLatest { ids ->
                            if (ids.isEmpty()) {
                                flowOf(emptyList())
                            } else {
                                val idList =
                                    ids.joinToString(",", prefix = "(", postfix = ")") { it }
                                sharedSupabase.from(SB_DECK_TN).selectAsFlow(
                                    primaryKey = SBDeckDto::deckUUID,
                                    filter = FilterOperation("deckUUID", FilterOperator.IN, idList)
                                )
                            }
                        }
                val mergedFlow: Flow<List<SBCoOwnerWithDeck>> =
                    coOwnerFlow.combine(deckFlow) { coOwnerOf, decks ->
                        val deckById = decks.associateBy { it.deckUUID }
                        coOwnerOf.mapNotNull { co ->
                            deckById[co.deckUUID]?.let { deck ->
                                SBCoOwnerWithDeck(
                                    deckUUID = co.deckUUID, deckName = deck.name,
                                    description = deck.description, coOwnerId = co.coOwnerId,
                                    status = co.status, invitedBy = co.invitedBy,
                                    createdAt = co.createdAt, updatedOn = co.updatedOn
                                )
                            }
                        }
                    }
                Pair(mergedFlow, SUCCESS)
            } catch (e: Exception) {
                when (e) {
                    is SocketException -> {
                        Log.e(COR_REPO, "Network Error: $e")
                        Pair(flowOf(listOf()), NETWORK_ERROR)
                    }

                    is CancellationException -> {
                        Log.e(COR_REPO, "Cancelled: $e")
                        Pair(flowOf(listOf()), CANCELLED)
                    }

                    else -> {
                        Log.e(COR_REPO, "Unknown Error: $e")
                        Pair(flowOf(listOf()), UNKNOWN_ERROR)
                    }
                }
            }
        }
    }

    override suspend fun acceptRequest(uuid: String): Int {
        return withContext(Dispatchers.IO) {
            val param = buildJsonObject {
                put("deck_uuid", JsonPrimitive(uuid))
            }
            try {
                val response = sharedSupabase.postgrest.rpc(
                    function = "accept_request",
                    parameters = param
                )
                if (response.data == "co-owner not found or not requested") {
                    USER_NOT_FOUND
                } else {
                    SUCCESS
                }

            } catch (e: Exception) {
                when (e) {
                    is SocketException -> {
                        Log.e(COR_REPO, "Network Error: $e")
                        NETWORK_ERROR
                    }

                    is CancellationException -> {
                        Log.e(COR_REPO, "Cancelled: $e")
                        CANCELLED
                    }

                    else -> {
                        Log.e(COR_REPO, "Unknown Error: $e")
                        UNKNOWN_ERROR
                    }
                }
            }
        }
    }

    override suspend fun declineRequest(uuid: String): Int {
        return withContext(Dispatchers.IO) {
            val param = buildJsonObject {
                put("deck_uuid", JsonPrimitive(uuid))
            }
            try {
                val response = sharedSupabase.postgrest.rpc(
                    function = "decline_request",
                    parameters = param
                )
                if (response.data == "co-owner not found or not requested") {
                    USER_NOT_FOUND
                } else {
                    SUCCESS
                }
            } catch (e : Exception) {
                when (e) {
                    is SocketException -> {
                        Log.e(COR_REPO, "Network Error: $e")
                        NETWORK_ERROR
                    }

                    is CancellationException -> {
                        Log.e(COR_REPO, "Cancelled: $e")
                        CANCELLED
                    }

                    else -> {
                        Log.e(COR_REPO, "Unknown Error: $e")
                        UNKNOWN_ERROR
                    }
                }
            }
        }
    }
}