package com.belmontCrest.cardCrafter.supabase.controller.supabaseVMFunctions

import com.belmontCrest.cardCrafter.model.tablesAndApplication.CT
import com.belmontCrest.cardCrafter.model.tablesAndApplication.Deck
import com.belmontCrest.cardCrafter.model.tablesAndApplication.ListStringConverter
import com.belmontCrest.cardCrafter.supabase.model.SBCT
import com.belmontCrest.cardCrafter.supabase.model.SBCardDto
import com.belmontCrest.cardCrafter.supabase.model.SBDeckToExportDto
import com.belmontCrest.cardCrafter.supabase.model.SBDeckDto
import com.belmontCrest.cardCrafter.supabase.model.SBMultiCardDto
import com.belmontCrest.cardCrafter.supabase.model.SBNotationCardDto

fun ctsToSbCts(
    deck: Deck, cts: List<CT>,
    description: String, userId: String
): SBDeckToExportDto {
    val stringConverter = ListStringConverter()
    return SBDeckToExportDto(
        deck = SBDeckDto(
            deckUUID = deck.uuid,
            user_id = userId,
            name = deck.name,
            description = description
        ),
        cts = cts.map { ct ->
            when (ct) {
                is CT.Basic -> {
                    SBCT.Basic(
                        card = SBCardDto(
                            deckUUID = ct.card.deckUUID,
                            type = ct.card.type,
                            cardIdentifier = ct.card.cardIdentifier
                        ),
                        basicCard = ct.basicCard
                    )
                }

                is CT.ThreeField -> {
                    SBCT.Three(
                        card = SBCardDto(
                            deckUUID = ct.card.deckUUID,
                            type = ct.card.type,
                            cardIdentifier = ct.card.cardIdentifier
                        ),
                        threeCard = ct.threeFieldCard
                    )
                }

                is CT.Hint -> {
                    SBCT.Hint(
                        card = SBCardDto(
                            deckUUID = ct.card.deckUUID,
                            type = ct.card.type,
                            cardIdentifier = ct.card.cardIdentifier
                        ),
                        hintCard = ct.hintCard
                    )
                }

                is CT.MultiChoice -> {
                    SBCT.Multi(
                        card = SBCardDto(
                            deckUUID = ct.card.deckUUID,
                            type = ct.card.type,
                            cardIdentifier = ct.card.cardIdentifier
                        ),
                        multiCard = SBMultiCardDto(
                            question = ct.multiChoiceCard.question,
                            choiceA = ct.multiChoiceCard.choiceA,
                            choiceB = ct.multiChoiceCard.choiceB,
                            choiceC = ct.multiChoiceCard.choiceC,
                            choiceD = ct.multiChoiceCard.choiceD,
                            correct = ct.multiChoiceCard.correct
                        )
                    )
                }

                is CT.Notation -> {
                    SBCT.Notation(
                        card = SBCardDto(
                            deckUUID = ct.card.deckUUID,
                            type = ct.card.type,
                            cardIdentifier = ct.card.cardIdentifier
                        ),
                        notationCard = SBNotationCardDto(
                            question = ct.notationCard.question,
                            steps = stringConverter.listToString(ct.notationCard.steps),
                            answer = ct.notationCard.answer
                        )
                    )
                }
            }
        }
    )

}