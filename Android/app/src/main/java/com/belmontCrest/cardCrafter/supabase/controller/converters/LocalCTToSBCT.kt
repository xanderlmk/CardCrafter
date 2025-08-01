package com.belmontCrest.cardCrafter.supabase.controller.converters

import android.util.Log
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.localDatabase.tables.ListStringConverter
import com.belmontCrest.cardCrafter.supabase.model.tables.CardsToDisplay
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCTToExport
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardDto
import com.belmontCrest.cardCrafter.supabase.model.tables.SBDeckToExportDto
import com.belmontCrest.cardCrafter.supabase.model.tables.SBDeckDto
import com.belmontCrest.cardCrafter.supabase.model.tables.SBMultiCardDto
import com.belmontCrest.cardCrafter.supabase.model.tables.SBNotationCardDto

/** Mapping our local CardTypes to the Supabase CardType*/
fun localCTToSBCT(
    deck: Deck, cts: List<CT>, cardsToDisplay: CardsToDisplay,
    description: String, userId: String, updatedOn : String
): SBDeckToExportDto {
    val stringConverter = ListStringConverter()
    return SBDeckToExportDto(
        deck = SBDeckDto(
            deckUUID = deck.uuid,
            userId = userId,
            name = deck.name,
            description = description
        ),
        cts = cts.mapNotNull { ct ->
            when (ct) {
                is CT.Basic -> {
                    SBCTToExport.Basic(
                        card = SBCardDto(
                            deckUUID = ct.card.deckUUID,
                            type = ct.card.type,
                            cardIdentifier = ct.card.cardIdentifier
                        ),
                        basicCard = ct.basicCard
                    )
                }

                is CT.ThreeField -> {
                    SBCTToExport.Three(
                        card = SBCardDto(
                            deckUUID = ct.card.deckUUID,
                            type = ct.card.type,
                            cardIdentifier = ct.card.cardIdentifier
                        ),
                        threeCard = ct.threeFieldCard
                    )
                }

                is CT.Hint -> {
                    SBCTToExport.Hint(
                        card = SBCardDto(
                            deckUUID = ct.card.deckUUID,
                            type = ct.card.type,
                            cardIdentifier = ct.card.cardIdentifier
                        ),
                        hintCard = ct.hintCard
                    )
                }

                is CT.MultiChoice -> {
                    SBCTToExport.Multi(
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
                    SBCTToExport.Notation(
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
                is CT.Custom -> {
                    Log.e("localCTToSBCT", "CT.Custom not yet implemented.")
                    null
                }
            }
        },
        cardsToDisplay = cardsToDisplay, lastUpdatedOn = updatedOn
    )
}