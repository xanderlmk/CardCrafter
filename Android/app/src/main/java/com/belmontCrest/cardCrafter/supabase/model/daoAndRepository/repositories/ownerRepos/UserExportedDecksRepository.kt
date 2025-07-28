package com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.ownerRepos

import android.util.Log
import com.belmontCrest.cardCrafter.BuildConfig
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.NETWORK_ERROR
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.SUCCESS
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.UNKNOWN_ERROR
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.CANCELLED
import com.belmontCrest.cardCrafter.supabase.model.tables.CoOwnerWithUsername
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsBasic
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsHint
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsMulti
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsNotation
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsThree
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsWithCT
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardList
import com.belmontCrest.cardCrafter.supabase.model.tables.SBDeckDto
import com.belmontCrest.cardCrafter.supabase.model.tables.SBDeckUUIDDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.postgrest.rpc
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import java.io.IOException
import java.net.SocketException

interface UserExportedDecksRepository {
    suspend fun userExportedDecks(): Flow<List<SBDeckDto>>

    suspend fun userCoOwnedDecks(): Flow<List<SBDeckDto>>

    suspend fun userDeckCards(uuids: List<String>): SBCardList

    suspend fun coOwners(deckUUID: String): Pair<List<CoOwnerWithUsername>, Int>

    suspend fun insertCoOwner(deckUUID: String, username: String): Int

    fun userId(): String
}

@OptIn(SupabaseExperimental::class)
class UserExportDecksRepositoryImpl(
    private val sharedSupabase: SupabaseClient
) : UserExportedDecksRepository {
    companion object {
        private const val SB_DECK_TN = BuildConfig.SB_DECK_TN
        private const val SB_CARD_TN = BuildConfig.SB_CARD_TN
        private const val SB_DACO_TN = BuildConfig.SB_DACO_TN
        private const val UED_REPO = "UserExportedDecksRepo"
    }

    override suspend fun userExportedDecks(): Flow<List<SBDeckDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val userInfo = sharedSupabase.auth.currentUserOrNull()
                if (userInfo == null) {
                    throw IllegalAccessException("User is not authenticated.")
                }
                val result = sharedSupabase.from(SB_DECK_TN)
                    .selectAsFlow(
                        SBDeckDto::deckUUID,
                        filter = FilterOperation(
                            column = "user_id",
                            operator = FilterOperator.EQ,
                            value = userInfo.id
                        )
                    )
                result
            } catch (e: Exception) {
                Log.e(UED_REPO, "Something went wrong: $e")
                flowOf(emptyList())
            }
        }
    }

    override suspend fun userCoOwnedDecks(): Flow<List<SBDeckDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val user = sharedSupabase.auth.currentUserOrNull()
                if (user == null) {
                    throw IllegalAccessException("User is not authenticated.")
                }
                val coOwnedDeckUUIDS = sharedSupabase.from(SB_DACO_TN)
                    .select(Columns.type<SBDeckUUIDDto>()) {
                        filter {
                            eq("status", "accepted")
                            eq("co_owner_id", user.id)

                        }
                    }.decodeList<SBDeckUUIDDto>().map { it.deckUUID }

                val coOwnerDecksFlow: Flow<List<SBDeckDto>> =
                    if (coOwnedDeckUUIDS.isEmpty()) {
                        flowOf(emptyList())
                    } else {
                        val idString = coOwnedDeckUUIDS.joinToString(",", "(", ")")
                        sharedSupabase.from(SB_DECK_TN)
                            .selectAsFlow(
                                SBDeckDto::deckUUID,
                                filter = FilterOperation("deckUUID", FilterOperator.IN, idString)
                            )
                    }
                coOwnerDecksFlow
            } catch (e: Exception) {
                Log.e(UED_REPO, "Something went wrong: $e")
                flowOf(emptyList())
            }
        }
    }

    override suspend fun userDeckCards(uuids: List<String>): SBCardList {
        return withContext(Dispatchers.IO) {
            try {
                val basicCards = sharedSupabase.from(SB_CARD_TN)
                    .select(
                        Columns.raw(
                            "id, type, deckUUID, cardIdentifier," +
                                    " basicCard(cardId, question, answer)"
                        )
                    )
                    {
                        filter {
                            isIn("deckUUID", uuids)
                            eq("type", "basic")
                        }
                    }.decodeList<SBCardColsBasic>()
                val hintCards = sharedSupabase.from(SB_CARD_TN)
                    .select(
                        Columns.raw(
                            "id, type, deckUUID, cardIdentifier," +
                                    " hintCard(cardId, question, hint, answer)"
                        )
                    )
                    {
                        filter {
                            isIn("deckUUID", uuids)
                            eq("type", "hint")
                        }
                    }.decodeList<SBCardColsHint>()
                val threeCards = sharedSupabase.from(SB_CARD_TN)
                    .select(
                        Columns.raw(
                            "id, type, deckUUID, cardIdentifier," +
                                    " threeCard(cardId, question, middle, answer)"
                        )
                    )
                    {
                        filter {
                            isIn("deckUUID", uuids)
                            eq("type", "three")
                        }
                    }.decodeList<SBCardColsThree>()
                val multiCards = sharedSupabase.from(SB_CARD_TN)
                    .select(
                        Columns.raw(
                            "id, type, deckUUID, cardIdentifier," +
                                    " multiCard(cardId, question, choiceA, choiceB, " +
                                    " choiceC, choiceD, correct)"
                        )
                    )
                    {
                        filter {
                            isIn("deckUUID", uuids)
                            eq("type", "multi")
                        }
                    }.decodeList<SBCardColsMulti>()
                val notationCards = sharedSupabase.from(SB_CARD_TN)
                    .select(
                        Columns.raw(
                            "id, type, deckUUID, cardIdentifier," +
                                    " notationCard(cardId, question, steps, answer)"
                        )
                    )
                    {
                        filter {
                            isIn("deckUUID", uuids)
                            eq("type", "notation")
                        }
                    }.decodeList<SBCardColsNotation>()

                val allCards = mutableListOf<SBCardColsWithCT>().apply {
                    addAll(basicCards)
                    addAll(hintCards)
                    addAll(threeCards)
                    addAll(multiCards)
                    addAll(notationCards)
                }
                val sortedCards = allCards.sortedBy { it.sortKey() }
                SBCardList(sortedCards)
            } catch (e: IllegalAccessException) {
                Log.e(UED_REPO, "Authentication error retrieving user deck cards", e)
                throw e
            } catch (e: IOException) {
                Log.e(UED_REPO, "Network error retrieving user deck cards", e)
                throw e
            } catch (e: Exception) {
                Log.e(UED_REPO, "Unexpected error retrieving user deck cards", e)
                throw e
            }
        }
    }

    override suspend fun coOwners(deckUUID: String): Pair<List<CoOwnerWithUsername>, Int> {
        return withContext(Dispatchers.IO) {
            try {
                val result = sharedSupabase.from(SB_DACO_TN)
                    .select(
                        Columns.raw(
                            """deckUUID, status, 
                            co_owner:owner!co_owner_id(username,f_name,l_name),
                            inviter :owner!invited_by(username,f_name,l_name)
                            """.trimIndent()
                        )
                    ) {
                        filter { eq("deckUUID", deckUUID) }
                    }.decodeList<CoOwnerWithUsername>()
                Pair(result, SUCCESS)
            } catch (e: Exception) {
                when (e) {
                    is SocketException -> {
                        Log.e(UED_REPO, "$e")
                        Pair(listOf(), NETWORK_ERROR)
                    }

                    is CancellationException -> {
                        Log.e(UED_REPO, "$e")
                        Pair(listOf(), CANCELLED)
                    }

                    else -> {
                        Log.e(UED_REPO, "$e")
                        Pair(listOf(), UNKNOWN_ERROR)
                    }
                }
            }
        }
    }

    override suspend fun insertCoOwner(deckUUID: String, username: String): Int {
        return withContext(Dispatchers.IO) {
            try {
                val params = buildJsonObject {
                    put("input_username", JsonPrimitive(username))
                    put("deck_uuid", JsonPrimitive(deckUUID))
                }

                Log.i(UED_REPO, "$params")

                val result = sharedSupabase.postgrest.rpc(
                    function = "import_co_owner",
                    parameters = params
                )
                Log.i(UED_REPO, "$result")
                SUCCESS
            } catch (e: Exception) {
                when (e) {
                    is SocketException -> {
                        Log.e(UED_REPO, "Network Error: $e")
                        NETWORK_ERROR
                    }

                    is CancellationException -> {
                        Log.e(UED_REPO, "Cancelled: $e")
                        CANCELLED
                    }

                    else -> {
                        Log.e(UED_REPO, "Unknown Error: $e")
                        UNKNOWN_ERROR
                    }
                }
            }
        }
    }

    override fun userId(): String {
        val user = sharedSupabase.auth.currentUserOrNull()
        if (user == null) return ""
        return user.id
    }
}

