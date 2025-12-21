package com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.ownerRepos

import android.util.Log
import com.belmontCrest.cardCrafter.BuildConfig
import com.belmontCrest.cardCrafter.supabase.controller.converters.RawColumns
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
import com.belmontCrest.cardCrafter.supabase.model.tables.SBDeckListDto
import com.belmontCrest.cardCrafter.supabase.model.tables.SBDeckUUIDDto
import com.belmontCrest.cardCrafter.supabase.model.tables.toUUIDs
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import java.io.IOException
import java.net.SocketException

interface UserExportedDecksRepository {
    suspend fun userExportedDecks(): Flow<List<SBDeckDto>>

    suspend fun userCoOwnedDecks(): Flow<List<SBDeckDto>>

    suspend fun coOwners(deckUUID: String): Pair<List<CoOwnerWithUsername>, Int>

    suspend fun insertCoOwner(deckUUID: String, username: String): Int

    fun updateUserDeckList(list: List<SBDeckDto>)
    fun updateUserCoOwnedDecks(list: List<SBDeckDto>)

    val allDecks: Flow<SBDeckListDto>
    val userCards: Flow<SBCardList>
    val pickedDeck: Flow<SBDeckDto?>
    val isCoOwner: Flow<Boolean>
    val isLoading: StateFlow<Boolean>
    fun updateUUID(s: String)
    fun uuidVal(): String
    fun updateIsLoading(l : Boolean)
}

@OptIn(SupabaseExperimental::class)
class UserExportDecksRepositoryImpl(
    private val supabase: SupabaseClient
) : UserExportedDecksRepository {
    companion object {
        private const val SB_DECK_TN = BuildConfig.SB_DECK_TN
        private const val SB_CARD_TN = BuildConfig.SB_CARD_TN
        private const val SB_DACO_TN = BuildConfig.SB_DACO_TN
        private const val UED_REPO = "UserExportedDecksRepo"
    }

    private val _userExportedDecks = MutableStateFlow(SBDeckListDto())

    private val _coOwnedDecks = MutableStateFlow(SBDeckListDto())

    override val allDecks = combine(_coOwnedDecks, _userExportedDecks) { coOwned, owned ->
        SBDeckListDto((coOwned.list + owned.list).distinctBy { it.deckUUID }).also {
            userDeckCards(it.toUUIDs())
        }
    }

    private val _userCards = MutableStateFlow(SBCardList())
    private val _uuid = MutableStateFlow("")
    override val userCards = combine(_userCards, _uuid) { cards, uuid ->
        val filtered = cards.cts.filter { it.deckUUID == uuid }
        SBCardList(filtered)
    }
    override val pickedDeck = combine(allDecks, _uuid) { decks, uuid ->
        decks.list.find { it.deckUUID == uuid }
    }
    private val userId = supabase.auth.currentUserOrNull()?.id
    override val isCoOwner = combine(_uuid, allDecks) { uuid, decks ->
        val deck = decks.list.find { it.deckUUID == uuid }
        if (deck == null) {
            false
        } else {
            deck.userId == userId
        }
    }

    private val _isLoading = MutableStateFlow(true)
    override val isLoading = _isLoading.asStateFlow()
    override fun updateIsLoading(l : Boolean) = _isLoading.update { l }
    override fun uuidVal() = _uuid.value

    override fun updateUUID(s: String) = _uuid.update { s }

    override fun updateUserDeckList(list: List<SBDeckDto>) = _userExportedDecks.update {
        SBDeckListDto(list)
    }

    override fun updateUserCoOwnedDecks(list: List<SBDeckDto>) = _coOwnedDecks.update {
        SBDeckListDto(list)
    }

    override suspend fun userExportedDecks(): Flow<List<SBDeckDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val userInfo = supabase.auth.currentUserOrNull()
                if (userInfo == null) {
                    throw IllegalAccessException("User is not authenticated.")
                }
                val result = supabase.from(SB_DECK_TN)
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
                val user = supabase.auth.currentUserOrNull()
                if (user == null) {
                    throw IllegalAccessException("User is not authenticated.")
                }
                val coOwnedDeckUUIDS = supabase.from(SB_DACO_TN)
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
                        supabase.from(SB_DECK_TN)
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

    private suspend fun userDeckCards(uuids: List<String>) = withContext(Dispatchers.IO) {
        try {
            val basicCards = supabase.from(SB_CARD_TN).select(RawColumns.Basic)
                {
                    filter {
                        isIn("deckUUID", uuids)
                        eq("type", "basic")
                    }
                }.decodeList<SBCardColsBasic>()
            val hintCards = supabase.from(SB_CARD_TN).select(RawColumns.Hint)
                {
                    filter {
                        isIn("deckUUID", uuids)
                        eq("type", "hint")
                    }
                }.decodeList<SBCardColsHint>()
            val threeCards = supabase.from(SB_CARD_TN).select(RawColumns.Three)
                {
                    filter {
                        isIn("deckUUID", uuids)
                        eq("type", "three")
                    }
                }.decodeList<SBCardColsThree>()
            val multiCards = supabase.from(SB_CARD_TN)
                .select(RawColumns.Multi)
                {
                    filter {
                        isIn("deckUUID", uuids)
                        eq("type", "multi")
                    }
                }.decodeList<SBCardColsMulti>()
            val notationCards = supabase.from(SB_CARD_TN)
                .select(RawColumns.Notation)
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
            _userCards.update { SBCardList(sortedCards) }
        } catch (e: IllegalAccessException) {
            Log.e(UED_REPO, "Authentication error retrieving user deck cards", e)
            _userCards.update { SBCardList() }
        } catch (e: IOException) {
            Log.e(UED_REPO, "Network error retrieving user deck cards", e)
            _userCards.update { SBCardList() }
        } catch (e: Exception) {
            Log.e(UED_REPO, "Unexpected error retrieving user deck cards", e)
            _userCards.update { SBCardList() }
        }
    }

    override suspend fun coOwners(deckUUID: String): Pair<List<CoOwnerWithUsername>, Int> {
        return withContext(Dispatchers.IO) {
            try {
                val result = supabase.from(SB_DACO_TN)
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
                        Log.e(UED_REPO, "${e.printStackTrace()}")
                        Pair(listOf(), NETWORK_ERROR)
                    }

                    is CancellationException -> {
                        Log.e(UED_REPO, "${e.printStackTrace()}")
                        Pair(listOf(), CANCELLED)
                    }

                    else -> {
                        Log.e(UED_REPO, "${e.printStackTrace()}")
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

                val result = supabase.postgrest.rpc(
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
}

