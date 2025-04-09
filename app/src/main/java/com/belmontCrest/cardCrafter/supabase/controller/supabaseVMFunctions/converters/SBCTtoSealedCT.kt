package com.belmontCrest.cardCrafter.supabase.controller.supabaseVMFunctions.converters

import com.belmontCrest.cardCrafter.localDatabase.tables.ListStringConverter
import com.belmontCrest.cardCrafter.localDatabase.tables.MultiChoiceCard
import com.belmontCrest.cardCrafter.localDatabase.tables.NotationCard
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardBasic
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardDto
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardHint
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardMulti
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardNotation
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardThree
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardWithCT
import com.belmontCrest.cardCrafter.supabase.model.tables.SealedCT

/** Converting our Supabase Card Columns with its Card Type to
 *  a SealedCardType */
fun sbctToSealedCts(
    ctList: List<SBCardWithCT>, onProgress: (Float) -> Unit, total: Int
): List<SealedCT> {
    val list: List<SealedCT> = ctList.mapIndexed { index, card ->
        when (card) {
            is SBCardBasic -> {
                onProgress((index + 1).toFloat() / total)
                SealedCT.Basic(
                    sbWctToSCT(card), card.basicCard
                )
            }

            is SBCardThree -> {
                onProgress((index + 1).toFloat() / total)
                SealedCT.Three(
                    sbWctToSCT(card), card.threeCard
                )
            }
            is SBCardHint -> {
                onProgress((index + 1).toFloat() / total)
                SealedCT.Hint(sbWctToSCT(card), card.hintCard)
            }
            is SBCardMulti -> {
                onProgress((index + 1).toFloat() / total)
                SealedCT.Multi(
                    sbWctToSCT(card),
                    MultiChoiceCard(
                        cardId = -1,
                        question = card.multiCard.question,
                        choiceA = card.multiCard.choiceA,
                        choiceB = card.multiCard.choiceB,
                        choiceC = if (card.multiCard.choiceC.isNullOrBlank()) {
                            ""
                        } else {
                            card.multiCard.choiceC
                        },
                        choiceD = if (card.multiCard.choiceD.isNullOrBlank()) {
                            ""
                        } else {
                            card.multiCard.choiceD
                        },
                        correct = card.multiCard.correct
                    )
                )
            }

            is SBCardNotation -> {
                val listStringConverter = ListStringConverter()
                onProgress((index + 1).toFloat() / total)
                SealedCT.Notation(
                    sbWctToSCT(card),
                    NotationCard(
                        cardId = -1,
                        question = card.notationCard.question,
                        steps = listStringConverter.fromString(card.notationCard.steps),
                        answer = card.notationCard.answer
                    )
                )
            }
        }
    }
    return list
}

private fun sbWctToSCT(card: SBCardWithCT): SBCardDto {
    return SBCardDto(
        id = card.id,
        deckUUID = card.deckUUID,
        type = card.type,
        cardIdentifier = card.cardIdentifier
    )
}