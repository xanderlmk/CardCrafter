package com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories

import android.util.Log
import com.belmontCrest.cardCrafter.BuildConfig
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.BASIC_CT_ERROR
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.HINT_CT_ERROR
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.MULTI_CT_ERROR
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.NOTATION_CT_ERROR
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.SUCCESS
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.THREE_CT_ERROR
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsBasic
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsHint
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsMulti
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsNotation
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsThree
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface ImportRepository {
    suspend fun checkBasicCardList(uuid: String): Pair<List<SBCardColsBasic>, Int>

    suspend fun checkHintCardList(uuid: String): Pair<List<SBCardColsHint>, Int>

    suspend fun checkThreeCardList(uuid: String): Pair<List<SBCardColsThree>, Int>

    suspend fun checkMultiCardList(uuid: String): Pair<List<SBCardColsMulti>, Int>

    suspend fun checkNotationCardList(uuid: String): Pair<List<SBCardColsNotation>, Int>
}

class ImportRepositoryImpl(
    private val sharedSupabase : SupabaseClient
) : ImportRepository {
    override suspend fun checkBasicCardList(uuid: String): Pair<List<SBCardColsBasic>, Int> {
        return withContext(Dispatchers.IO) {
            try {
                val data = sharedSupabase.from(SBCardTN)
                    .select(
                        Columns.raw(
                            "id, type, deckUUID, cardIdentifier," +
                                    " basicCard(cardId, question, answer)"
                        )
                    )
                    {
                        filter {
                            eq("deckUUID", uuid)
                            eq("type", "basic")
                        }
                    }.decodeList<SBCardColsBasic>()
                Pair(data, SUCCESS)
            } catch (e: Exception) {
                Log.e("BasicCardList", "$e")
                Pair(listOf(), BASIC_CT_ERROR)
            }

        }
    }

    override suspend fun checkHintCardList(uuid: String): Pair<List<SBCardColsHint>, Int> {
        return withContext(Dispatchers.IO) {
            try {
                val data = sharedSupabase.from(SBCardTN)
                    .select(
                        Columns.raw(
                            "id, type, deckUUID, cardIdentifier," +
                                    " hintCard(cardId, question, hint, answer)"
                        )
                    )
                    {
                        filter {
                            eq("deckUUID", uuid)
                            eq("type", "hint")
                        }
                    }.decodeList<SBCardColsHint>()
                Pair(data, SUCCESS)
            } catch (e: Exception) {
                Log.e("HintCardList", "$e")
                Pair(listOf(), HINT_CT_ERROR)
            }
        }
    }
    override suspend fun checkThreeCardList(uuid: String): Pair<List<SBCardColsThree>, Int> {
        return withContext(Dispatchers.IO) {
            try {
                val data = sharedSupabase.from(SBCardTN)
                    .select(
                        Columns.raw(
                            "id, type, deckUUID, cardIdentifier," +
                                    " threeCard(cardId, question, middle, answer)"
                        )
                    )
                    {
                        filter {
                            eq("deckUUID", uuid)
                            eq("type", "three")
                        }
                    }.decodeList<SBCardColsThree>()
                Pair(data, SUCCESS)
            } catch (e: Exception) {
                Log.e("HintCardList", "$e")
                Pair(listOf(), THREE_CT_ERROR)
            }
        }
    }
    override suspend fun checkMultiCardList(uuid: String): Pair<List<SBCardColsMulti>, Int> {
        return withContext(Dispatchers.IO) {
            try {
                val data = sharedSupabase.from(SBCardTN)
                    .select(
                        Columns.raw(
                            "id, type, deckUUID, cardIdentifier," +
                                    " multiCard(cardId, question, choiceA, choiceB, " +
                                    " choiceC, choiceD, correct)"
                        )
                    )
                    {
                        filter {
                            eq("deckUUID", uuid)
                            eq("type", "multi")
                        }
                    }.decodeList<SBCardColsMulti>()
                Pair(data, SUCCESS)
            } catch (e: Exception) {
                Log.e("ThreeCardList", "$e")
                Pair(listOf(), MULTI_CT_ERROR)
            }
        }
    }

    override suspend fun checkNotationCardList(uuid: String): Pair<List<SBCardColsNotation>, Int> {
        return withContext(Dispatchers.IO) {
            try {
                val data = sharedSupabase.from(SBCardTN)
                    .select(
                        Columns.raw(
                            "id, type, deckUUID, cardIdentifier," +
                                    " notationCard(cardId, question, steps, answer)"
                        )
                    )
                    {
                        filter {
                            eq("deckUUID", uuid)
                            eq("type", "notation")
                        }
                    }.decodeList<SBCardColsNotation>()
                Pair(data, SUCCESS)
            } catch (e: Exception) {
                Log.e("NotationCardList", "$e")
                Pair(listOf(), NOTATION_CT_ERROR)
            }
        }
    }
}

private const val SBCardTN = BuildConfig.SB_CARD_TN