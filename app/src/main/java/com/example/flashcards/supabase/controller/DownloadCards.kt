package com.example.flashcards.supabase.controller

import android.util.Log
import com.example.flashcards.model.repositories.CardTypeRepository
import com.example.flashcards.model.repositories.FlashCardRepository
import com.example.flashcards.model.repositories.ScienceSpecificRepository
import com.example.flashcards.model.tablesAndApplication.BasicCard
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.model.tablesAndApplication.HintCard
import com.example.flashcards.model.tablesAndApplication.MathCard
import com.example.flashcards.model.tablesAndApplication.MathCardConverter
import com.example.flashcards.model.tablesAndApplication.MultiChoiceCard
import com.example.flashcards.model.tablesAndApplication.ThreeFieldCard
import com.example.flashcards.model.uiModels.PreferencesManager
import com.example.flashcards.supabase.model.SBCards
import com.example.flashcards.supabase.model.SBMathCard
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import java.util.Date

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

                val cardId = flashCardRepository.insertCard(
                    Card(
                        deckId = deckId,
                        deckUUID = deckUUID,
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
                return 0
            } catch (e: Exception) {
                /** Couldn't successfully insert the card */
                Log.d("SupabaseViewModel", "Couldn't insert card $e")
                return 75
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

                val cardId = flashCardRepository.insertCard(
                    Card(
                        deckId = deckId.toInt(),
                        deckUUID = deckUUID,
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
                return 0
            } catch (e: Exception) {
                /** Couldn't successfully insert the card */
                Log.d("SupabaseViewModel", "Couldn't insert card $e")
                return 75
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

                val cardId = flashCardRepository.insertCard(
                    Card(
                        deckId = deckId,
                        deckUUID = deckUUID,
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
                return 0
            } catch (e: Exception) {
                /** Couldn't successfully insert the card */
                Log.d("SupabaseViewModel", "Couldn't insert card $e")
                return 75
            }
        }

        "multi" -> {
            try {
                val multiCard = supabase.from("multiCard")
                    .select(columns = Columns.ALL) {
                        filter {
                            eq("cardId", card.id)
                        }
                    }.decodeSingle<MultiChoiceCard>()

                val cardId = flashCardRepository.insertCard(
                    Card(
                        deckId = deckId,
                        deckUUID = deckUUID,
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
                        choiceC = multiCard.choiceC,
                        choiceD = multiCard.choiceD,
                        correct = multiCard.correct
                    )
                )
                return 0
            } catch (e: Exception) {
                /** Couldn't successfully insert the card */
                Log.d("SupabaseViewModel", "Couldn't insert card $e")
                return 75
            }
        }

        "math" -> {
            try {
                val mathCardConverter = MathCardConverter()
                val mathCard = supabase.from("mathCard")
                    .select(columns = Columns.ALL) {
                        filter {
                            eq("cardId", card.id)
                        }
                    }.decodeSingle<SBMathCard>()

                val cardId = flashCardRepository.insertCard(
                    Card(
                        deckId = deckId,
                        deckUUID = deckUUID,
                        nextReview = Date(),
                        reviewsLeft = preferences.reviewAmount.intValue,
                        passes = 0,
                        prevSuccess = false,
                        totalPasses = 0,
                        type = "math",
                    )
                )
                sSRepository.insertMathCard(
                    MathCard(
                        cardId = cardId.toInt(),
                        question = mathCard.question,
                        steps = mathCardConverter.fromString(mathCard.steps),
                        answer = mathCard.answer
                    )
                )
                return 0
            } catch (e: Exception) {
                /** Couldn't successfully insert the card */
                Log.d("SupabaseViewModel", "Couldn't insert card $e")
                return 75
            }
        }
        else -> {
            /** Card type doesn't exist */
            Log.d("SupabaseViewModel", "Card type doesn't exist!")
            return 25
        }
    }
}