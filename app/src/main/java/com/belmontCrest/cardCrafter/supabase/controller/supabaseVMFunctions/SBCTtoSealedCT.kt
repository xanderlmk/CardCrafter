package com.belmontCrest.cardCrafter.supabase.controller.supabaseVMFunctions

import com.belmontCrest.cardCrafter.localDatabase.tables.BasicCard
import com.belmontCrest.cardCrafter.localDatabase.tables.HintCard
import com.belmontCrest.cardCrafter.localDatabase.tables.ListStringConverter
import com.belmontCrest.cardCrafter.localDatabase.tables.MultiChoiceCard
import com.belmontCrest.cardCrafter.localDatabase.tables.NotationCard
import com.belmontCrest.cardCrafter.localDatabase.tables.ThreeFieldCard
import com.belmontCrest.cardCrafter.supabase.model.SBCardDto
import com.belmontCrest.cardCrafter.supabase.model.SBMultiCardDto
import com.belmontCrest.cardCrafter.supabase.model.SBNotationCardDto
import com.belmontCrest.cardCrafter.supabase.model.SealedCT
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns


suspend fun sbctToSealedCts(
    sbCards: List<SBCardDto>, supabase: SupabaseClient,
    onProgress : (Float) -> Unit, total : Int
): List<SealedCT> {
    val list : List<SealedCT> = sbCards.mapIndexed {index,  card ->
        when (card.type) {
            "basic" -> {
                val basicCard = supabase.from("basicCard")
                    .select(columns = Columns.ALL) {
                        filter {
                            eq("cardId", card.id)
                        }
                    }.decodeSingle<BasicCard>()
                onProgress((index + 1).toFloat() / total)
                SealedCT.Basic(
                    card = card,
                    basicCard = basicCard
                )
            }
            "three" -> {
                val threeCard = supabase.from("threeCard")
                    .select(columns = Columns.ALL) {
                        filter {
                            eq("cardId", card.id)
                        }
                    }.decodeSingle<ThreeFieldCard>()
                onProgress((index + 1).toFloat() / total)
                SealedCT.Three(
                    card, threeCard
                )
            }

            "hint" -> {
                val hintCard = supabase.from("hintCard")
                    .select(columns = Columns.ALL) {
                        filter {
                            eq("cardId", card.id)
                        }
                    }.decodeSingle<HintCard>()
                onProgress((index + 1).toFloat() / total)
                SealedCT.Hint(
                    card,
                    hintCard
                )
            }

            "multi" -> {
                val multiCard = supabase.from("multiCard")
                    .select(
                        columns = Columns.ALL,
                    ) {
                        filter {
                            eq("cardId", card.id)
                        }
                    }.decodeSingle<SBMultiCardDto>()
                onProgress((index + 1).toFloat() / total)
                SealedCT.Multi(
                    card,
                    MultiChoiceCard(
                        cardId = -1,
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
            }
            "notation" -> {
                val listStringConverter = ListStringConverter()
                val notationCard = supabase.from("notationCard")
                    .select(columns = Columns.ALL) {
                        filter {
                            eq("cardId", card.id)
                        }
                    }.decodeSingle<SBNotationCardDto>()
                onProgress((index + 1).toFloat() / total)
                SealedCT.Notation(
                    card,
                    NotationCard(
                        cardId = -1,
                        question = notationCard.question,
                        steps = listStringConverter.fromString(notationCard.steps),
                        answer = notationCard.answer
                    )
                )
            }
            else -> {
                // Handle any unmatched cases if needed
                throw IllegalArgumentException("Unsupported card type: ${card.type}")
            }

        }
    }
    return list
}