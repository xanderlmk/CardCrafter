package com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories

import android.util.Log
import com.belmontCrest.cardCrafter.BuildConfig
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.BASIC_CT_ERROR
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.CTD_ERROR
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.HINT_CT_ERROR
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.MULTI_CT_ERROR
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.NOTATION_CT_ERROR
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.SUCCESS
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.THREE_CT_ERROR
import com.belmontCrest.cardCrafter.supabase.model.tables.CardsToDisplay
import com.belmontCrest.cardCrafter.supabase.model.tables.FourSBCards
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsBasic
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsHint
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsMulti
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsNotation
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsThree
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardDto
import com.belmontCrest.cardCrafter.supabase.model.tables.toSBCardColsWithCT
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

    suspend fun getCardsToDisplay(uuid: String): Pair<CardsToDisplay, Int>
    suspend fun getCards(cardsToDisplay: CardsToDisplay): FourSBCards
}

class ImportRepositoryImpl(
    private val sharedSupabase: SupabaseClient
) : ImportRepository {
    companion object {
        private const val SB_CARD_TN = BuildConfig.SB_CARD_TN
        private const val SB_CTD_TN = BuildConfig.SB_CTD_TN
    }
    override suspend fun checkBasicCardList(uuid: String): Pair<List<SBCardColsBasic>, Int> {
        return withContext(Dispatchers.IO) {
            try {
                val data = sharedSupabase.from(SB_CARD_TN)
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
                val data = sharedSupabase.from(SB_CARD_TN)
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
                val data = sharedSupabase.from(SB_CARD_TN)
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
                val data = sharedSupabase.from(SB_CARD_TN)
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
                val data = sharedSupabase.from(SB_CARD_TN)
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

    override suspend fun getCards(cardsToDisplay: CardsToDisplay): FourSBCards {
        return withContext(Dispatchers.IO) {
            try {
                val ids = listOfNotNull(
                    cardsToDisplay.cardOne,
                    cardsToDisplay.cardTwo,
                    cardsToDisplay.cardThree,
                    cardsToDisplay.cardFour
                )
                val cards = sharedSupabase.from(SB_CARD_TN)
                    .select(Columns.ALL) {
                        filter {
                            isIn("cardIdentifier", ids)
                        }
                    }.decodeList<SBCardDto>()
                val sbCTs = cards.mapIndexed { index , it ->
                    it.toSBCardColsWithCT(sharedSupabase)
                }
                FourSBCards(
                    first = sbCTs.getOrNull(0),
                    second = sbCTs.getOrNull(1),
                    third = sbCTs.getOrNull(2),
                    fourth = sbCTs.getOrNull(3)
                )
            } catch (e: Exception) {
                Log.e("ImportRepo", "$e")
                FourSBCards()
            }
        }
    }
}
