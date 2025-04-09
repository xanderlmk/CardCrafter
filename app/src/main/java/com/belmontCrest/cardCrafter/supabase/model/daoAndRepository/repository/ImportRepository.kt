package com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repository

import android.util.Log
import com.belmontCrest.cardCrafter.BuildConfig
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.BASIC_CT_ERROR
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.HINT_CT_ERROR
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.MULTI_CT_ERROR
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.NOTATION_CT_ERROR
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.SUCCESS
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.THREE_CT_ERROR
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardBasic
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardHint
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardMulti
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardNotation
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardThree
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface ImportRepository {
    suspend fun checkBasicCardList(uuid: String): Pair<List<SBCardBasic>, Int>

    suspend fun checkHintCardList(uuid: String): Pair<List<SBCardHint>, Int>

    suspend fun checkThreeCardList(uuid: String): Pair<List<SBCardThree>, Int>

    suspend fun checkMultiCardList(uuid: String): Pair<List<SBCardMulti>, Int>

    suspend fun checkNotationCardList(uuid: String): Pair<List<SBCardNotation>, Int>
}

class ImportRepositoryImpl(
    private val supabase : SupabaseClient
) : ImportRepository {
    override suspend fun checkBasicCardList(uuid: String): Pair<List<SBCardBasic>, Int> {
        return withContext(Dispatchers.IO) {
            try {
                val data = supabase.from(SBCardTN)
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
                    }.decodeList<SBCardBasic>()
                Pair(data, SUCCESS)
            } catch (e: Exception) {
                Log.e("BasicCardList", "$e")
                Pair(listOf(), BASIC_CT_ERROR)
            }

        }
    }

    override suspend fun checkHintCardList(uuid: String): Pair<List<SBCardHint>, Int> {
        return withContext(Dispatchers.IO) {
            try {
                val data = supabase.from(SBCardTN)
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
                    }.decodeList<SBCardHint>()
                Pair(data, SUCCESS)
            } catch (e: Exception) {
                Log.e("HintCardList", "$e")
                Pair(listOf(), HINT_CT_ERROR)
            }
        }
    }
    override suspend fun checkThreeCardList(uuid: String): Pair<List<SBCardThree>, Int> {
        return withContext(Dispatchers.IO) {
            try {
                val data = supabase.from(SBCardTN)
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
                    }.decodeList<SBCardThree>()
                Pair(data, SUCCESS)
            } catch (e: Exception) {
                Log.e("HintCardList", "$e")
                Pair(listOf(), THREE_CT_ERROR)
            }
        }
    }
    override suspend fun checkMultiCardList(uuid: String): Pair<List<SBCardMulti>, Int> {
        return withContext(Dispatchers.IO) {
            try {
                val data = supabase.from(SBCardTN)
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
                    }.decodeList<SBCardMulti>()
                Pair(data, SUCCESS)
            } catch (e: Exception) {
                Log.e("ThreeCardList", "$e")
                Pair(listOf(), MULTI_CT_ERROR)
            }
        }
    }

    override suspend fun checkNotationCardList(uuid: String): Pair<List<SBCardNotation>, Int> {
        return withContext(Dispatchers.IO) {
            try {
                val data = supabase.from(SBCardTN)
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
                    }.decodeList<SBCardNotation>()
                Pair(data, SUCCESS)
            } catch (e: Exception) {
                Log.e("NotationCardList", "$e")
                Pair(listOf(), NOTATION_CT_ERROR)
            }
        }
    }
}

private const val SBCardTN = BuildConfig.SB_CARD_TN