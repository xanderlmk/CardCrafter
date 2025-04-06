package com.belmontCrest.cardCrafter.supabase.controller.supabaseVMFunctions

import android.util.Log
import com.belmontCrest.cardCrafter.model.databaseInterface.repositories.CardTypeRepository
import com.belmontCrest.cardCrafter.model.databaseInterface.repositories.FlashCardRepository
import com.belmontCrest.cardCrafter.model.databaseInterface.repositories.ScienceSpecificRepository
import com.belmontCrest.cardCrafter.model.tablesAndApplication.BasicCard
import com.belmontCrest.cardCrafter.model.tablesAndApplication.Card
import com.belmontCrest.cardCrafter.model.tablesAndApplication.HintCard
import com.belmontCrest.cardCrafter.model.tablesAndApplication.NotationCard
import com.belmontCrest.cardCrafter.model.tablesAndApplication.ListStringConverter
import com.belmontCrest.cardCrafter.model.tablesAndApplication.MultiChoiceCard
import com.belmontCrest.cardCrafter.model.tablesAndApplication.ThreeFieldCard
import com.belmontCrest.cardCrafter.model.uiModels.PreferencesManager
import com.belmontCrest.cardCrafter.supabase.model.SBCards
import com.belmontCrest.cardCrafter.supabase.model.SBMultiCard
import com.belmontCrest.cardCrafter.supabase.model.SBNotationCard
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import java.util.Date

private const val SUCCESS = 0
private const val ERROR_INSERT_CARD_FAILED = 75
private const val ERROR_TYPE_NOT_EXIST = 25

suspend fun downloadCards(
    card: SBCards, supabase: SupabaseClient, deckUUID: String,
    flashCardRepository: FlashCardRepository,
    cardTypeRepository: CardTypeRepository, deckId: Int,
    sSRepository: ScienceSpecificRepository, preferences: PreferencesManager
): Int {
    when (card.type) {
        "basic" -> {
            try {
                val basicCard = supabase.from("basicCard")
                    .select(columns = Columns.ALL) {
                        filter {
                            eq("cardId", card.id)
                        }
                    }.decodeSingle<BasicCard>()
                val currentMax = flashCardRepository.getMaxDCNumber(deckUUID) ?: 0
                val newDeckCardNumber = currentMax + 1
                val cardId = flashCardRepository.insertCard(
                    Card(
                        deckId = deckId,
                        deckUUID = deckUUID,
                        deckCardNumber = newDeckCardNumber,
                        cardIdentifier = "$deckUUID-$newDeckCardNumber",
                        nextReview = Date(),
                        reviewsLeft = preferences.reviewAmount.intValue,
                        passes = 0,
                        prevSuccess = false,
                        totalPasses = 0,
                        type = "basic",
                    )
                )
                cardTypeRepository.insertBasicCard(
                    BasicCard(
                        cardId = cardId.toInt(),
                        question = basicCard.question,
                        answer = basicCard.answer
                    )
                )
                return SUCCESS
            } catch (e: Exception) {
                /** Couldn't successfully insert the card */
                Log.d("SupabaseViewModel", "Couldn't insert card $e")
                return ERROR_INSERT_CARD_FAILED
            }
        }

        "three" -> {
            try {
                val threeCard = supabase.from("threeCard")
                    .select(columns = Columns.ALL) {
                        filter {
                            eq("cardId", card.id)
                        }
                    }.decodeSingle<ThreeFieldCard>()
                val currentMax = flashCardRepository.getMaxDCNumber(deckUUID) ?: 0
                val newDeckCardNumber = currentMax + 1
                val cardId = flashCardRepository.insertCard(
                    Card(
                        deckId = deckId.toInt(),
                        deckUUID = deckUUID,
                        deckCardNumber = newDeckCardNumber,
                        cardIdentifier = "$deckUUID-$newDeckCardNumber",
                        nextReview = Date(),
                        reviewsLeft = preferences.reviewAmount.intValue,
                        passes = 0,
                        prevSuccess = false,
                        totalPasses = 0,
                        type = "three",
                    )
                )
                cardTypeRepository.insertThreeCard(
                    ThreeFieldCard(
                        cardId = cardId.toInt(),
                        question = threeCard.question,
                        middle = threeCard.middle,
                        answer = threeCard.answer
                    )
                )
                return SUCCESS
            } catch (e: Exception) {
                /** Couldn't successfully insert the card */
                Log.d("SupabaseViewModel", "Couldn't insert card $e")
                return ERROR_INSERT_CARD_FAILED
            }
        }
        "hint" -> {
            try {
                val hintCard = supabase.from("hintCard")
                    .select(columns = Columns.ALL) {
                        filter {
                            eq("cardId", card.id)
                        }
                    }.decodeSingle<HintCard>()
                val currentMax = flashCardRepository.getMaxDCNumber(deckUUID) ?: 0
                val newDeckCardNumber = currentMax + 1
                val cardId = flashCardRepository.insertCard(
                    Card(
                        deckId = deckId,
                        deckUUID = deckUUID,
                        deckCardNumber = newDeckCardNumber,
                        cardIdentifier = "$deckUUID-$newDeckCardNumber",
                        nextReview = Date(),
                        reviewsLeft = preferences.reviewAmount.intValue,
                        passes = 0,
                        prevSuccess = false,
                        totalPasses = 0,
                        type = "hint",
                    )
                )
                cardTypeRepository.insertHintCard(
                    HintCard(
                        cardId = cardId.toInt(),
                        question = hintCard.question,
                        hint = hintCard.hint,
                        answer = hintCard.answer
                    )
                )
                return SUCCESS
            } catch (e: Exception) {
                /** Couldn't successfully insert the card */
                Log.d("SupabaseViewModel", "Couldn't insert card $e")
                return ERROR_INSERT_CARD_FAILED
            }
        }
        "multi" -> {
            try {
                val multiCard = supabase.from("multiCard")
                    .select(
                        columns = Columns.ALL,
                    ) {
                        filter {
                            eq("cardId", card.id)
                        }
                    }.decodeSingle<SBMultiCard>()
                val currentMax = flashCardRepository.getMaxDCNumber(deckUUID) ?: 0
                val newDeckCardNumber = currentMax + 1
                val cardId = flashCardRepository.insertCard(
                    Card(
                        deckId = deckId,
                        deckUUID = deckUUID,
                        deckCardNumber = newDeckCardNumber,
                        cardIdentifier = "$deckUUID-$newDeckCardNumber",
                        nextReview = Date(),
                        reviewsLeft = preferences.reviewAmount.intValue,
                        passes = 0,
                        prevSuccess = false,
                        totalPasses = 0,
                        type = "multi",
                    )
                )
                cardTypeRepository.insertMultiChoiceCard(
                    MultiChoiceCard(
                        cardId = cardId.toInt(),
                        question = multiCard.question,
                        choiceA = multiCard.choiceA,
                        choiceB = multiCard.choiceB,
                        choiceC = if (multiCard.choiceC.isNullOrBlank()) {
                            ""
                        } else {
                            multiCard.choiceC
                        },
                        choiceD = if (multiCard.choiceD.isNullOrBlank()) {
                            ""
                        } else {
                            multiCard.choiceD
                        },
                        correct = multiCard.correct
                    )
                )
                return SUCCESS
            } catch (e: Exception) {
                /** Couldn't successfully insert the card */
                Log.d("SupabaseViewModel", "Couldn't insert card: $e")
                return ERROR_INSERT_CARD_FAILED
            }
        }

        "notation" -> {
            try {
                val listStringConverter = ListStringConverter()
                val notationCard = supabase.from("notationCard")
                    .select(columns = Columns.ALL) {
                        filter {
                            eq("cardId", card.id)
                        }
                    }.decodeSingle<SBNotationCard>()
                val currentMax = flashCardRepository.getMaxDCNumber(deckUUID) ?: 0
                val newDeckCardNumber = currentMax + 1
                val cardId = flashCardRepository.insertCard(
                    Card(
                        deckId = deckId,
                        deckUUID = deckUUID,
                        deckCardNumber = newDeckCardNumber,
                        cardIdentifier = "$deckUUID-$newDeckCardNumber",
                        nextReview = Date(),
                        reviewsLeft = preferences.reviewAmount.intValue,
                        passes = 0,
                        prevSuccess = false,
                        totalPasses = 0,
                        type = "notation",
                    )
                )
                sSRepository.insertNotationCard(
                    NotationCard(
                        cardId = cardId.toInt(),
                        question = notationCard.question,
                        steps = listStringConverter.fromString(notationCard.steps),
                        answer = notationCard.answer
                    )
                )
                return SUCCESS
            } catch (e: Exception) {
                /** Couldn't successfully insert the card */
                Log.d("SupabaseViewModel", "Couldn't insert card $e")
                return ERROR_INSERT_CARD_FAILED
            }
        }

        else -> {
            /** Card type doesn't exist */
            Log.d("SupabaseViewModel", "Card type doesn't exist!")
            return ERROR_TYPE_NOT_EXIST
        }
    }
}