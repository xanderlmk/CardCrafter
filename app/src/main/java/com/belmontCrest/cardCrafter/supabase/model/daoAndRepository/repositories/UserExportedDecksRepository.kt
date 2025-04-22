package com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories

import android.util.Log
import com.belmontCrest.cardCrafter.BuildConfig
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsBasic
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsHint
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsMulti
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsNotation
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsThree
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsWithCT
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardList
import com.belmontCrest.cardCrafter.supabase.model.tables.SBDeckDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext

interface UserExportedDecksRepository {
    suspend fun userExportedDecks(): Flow<List<SBDeckDto>>

    suspend fun userDeckCards(uuids: List<String>): SBCardList
}

@OptIn(SupabaseExperimental::class)
class UserExportDecksRepositoryImpl(
    private val sharedSupabase: SupabaseClient
) : UserExportedDecksRepository {

    companion object {
        private const val SB_DECK_TN = BuildConfig.SB_DECK_TN
        private const val SB_CARD_TN = BuildConfig.SB_CARD_TN
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
                Log.e("userExportedDecks", "Something went wrong: $e")
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
            } catch (e: Exception) {
                Log.e("UserExportedDeckRepo", "$e")
                SBCardList()
            }
        }
    }
}
