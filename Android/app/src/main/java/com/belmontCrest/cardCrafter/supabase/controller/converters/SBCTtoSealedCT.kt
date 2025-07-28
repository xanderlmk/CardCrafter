package com.belmontCrest.cardCrafter.supabase.controller.converters

import com.belmontCrest.cardCrafter.localDatabase.tables.ListStringConverter
import com.belmontCrest.cardCrafter.localDatabase.tables.MultiChoiceCard
import com.belmontCrest.cardCrafter.localDatabase.tables.NotationCard
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsBasic
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardDto
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsHint
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsMulti
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsNotation
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsThree
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsWithCT
import com.belmontCrest.cardCrafter.supabase.model.tables.SealedCTToImport

/** Converting our Supabase Card Columns with its Card Type to
 *  a SealedCardType */
fun cardColsCTToSBCT(
    ctList: List<SBCardColsWithCT>, onProgress: (Float) -> Unit, total: Int
): List<SealedCTToImport> {
    val list: List<SealedCTToImport> = ctList.mapIndexed { index, card ->
        when (card) {
            is SBCardColsBasic -> {
                onProgress((index + 1).toFloat() / total)
                SealedCTToImport.Basic(
                    sbWctToSCT(card), card.basicCard
                )
            }

            is SBCardColsThree -> {
                onProgress((index + 1).toFloat() / total)
                SealedCTToImport.Three(
                    sbWctToSCT(card), card.threeCard
                )
            }
            is SBCardColsHint -> {
                onProgress((index + 1).toFloat() / total)
                SealedCTToImport.Hint(sbWctToSCT(card), card.hintCard)
            }
            is SBCardColsMulti -> {
                onProgress((index + 1).toFloat() / total)
                SealedCTToImport.Multi(
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

            is SBCardColsNotation -> {
                val listStringConverter = ListStringConverter()
                onProgress((index + 1).toFloat() / total)
                SealedCTToImport.Notation(
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

private fun sbWctToSCT(card: SBCardColsWithCT): SBCardDto {
    return SBCardDto(
        id = card.id,
        deckUUID = card.deckUUID,
        type = card.type,
        cardIdentifier = card.cardIdentifier
    )
}