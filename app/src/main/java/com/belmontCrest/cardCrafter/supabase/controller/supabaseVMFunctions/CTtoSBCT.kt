package com.belmontCrest.cardCrafter.supabase.controller.supabaseVMFunctions

import com.belmontCrest.cardCrafter.model.tablesAndApplication.CT
import com.belmontCrest.cardCrafter.model.tablesAndApplication.Deck
import com.belmontCrest.cardCrafter.model.tablesAndApplication.ListStringConverter
import com.belmontCrest.cardCrafter.supabase.model.SBCT
import com.belmontCrest.cardCrafter.supabase.model.SBCards
import com.belmontCrest.cardCrafter.supabase.model.SBDeckToExport
import com.belmontCrest.cardCrafter.supabase.model.SBDecks
import com.belmontCrest.cardCrafter.supabase.model.SBMultiCard
import com.belmontCrest.cardCrafter.supabase.model.SBNotationCard

fun ctsToSbCts(
    deck: Deck, cts: List<CT>,
    description: String, userId: String
): SBDeckToExport {
    val stringConverter = ListStringConverter()
    return SBDeckToExport(
        deck = SBDecks(
            deckUUID = deck.uuid,
            user_id = userId,
            name = deck.name,
            description = description
        ),
        cts = cts.map { ct ->
            when (ct) {
                is CT.Basic -> {
                    SBCT.Basic(
                        card = SBCards(
                            deckUUID = ct.card.deckUUID,
                            type = ct.card.type,
                            cardIdentifier = ct.card.cardIdentifier
                        ),
                        basicCard = ct.basicCard
                    )
                }

                is CT.ThreeField -> {
                    SBCT.Three(
                        card = SBCards(
                            deckUUID = ct.card.deckUUID,
                            type = ct.card.type,
                            cardIdentifier = ct.card.cardIdentifier
                        ),
                        threeCard = ct.threeFieldCard
                    )
                }

                is CT.Hint -> {
                    SBCT.Hint(
                        card = SBCards(
                            deckUUID = ct.card.deckUUID,
                            type = ct.card.type,
                            cardIdentifier = ct.card.cardIdentifier
                        ),
                        hintCard = ct.hintCard
                    )
                }

                is CT.MultiChoice -> {
                    SBCT.Multi(
                        card = SBCards(
                            deckUUID = ct.card.deckUUID,
                            type = ct.card.type,
                            cardIdentifier = ct.card.cardIdentifier
                        ),
                        multiCard = SBMultiCard(
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
                        card = SBCards(
                            deckUUID = ct.card.deckUUID,
                            type = ct.card.type,
                            cardIdentifier = ct.card.cardIdentifier
                        ),
                        notationCard = SBNotationCard(
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