package com.belmontCrest.cardCrafter.supabase.controller.supabaseVMFunctions.upsertDeck

import android.util.Log
import com.belmontCrest.cardCrafter.model.tablesAndApplication.BasicCard
import com.belmontCrest.cardCrafter.model.tablesAndApplication.CT
import com.belmontCrest.cardCrafter.model.tablesAndApplication.Deck
import com.belmontCrest.cardCrafter.model.tablesAndApplication.HintCard
import com.belmontCrest.cardCrafter.model.tablesAndApplication.ListStringConverter
import com.belmontCrest.cardCrafter.model.tablesAndApplication.ThreeFieldCard
import com.belmontCrest.cardCrafter.supabase.model.SBCards
import com.belmontCrest.cardCrafter.supabase.model.SBMultiCard
import com.belmontCrest.cardCrafter.supabase.model.SBNotationCard
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

suspend fun upsertCTList(
    cts: List<CT>, supabase: SupabaseClient, deck: Deck
): Boolean {
    val cardsToInsert = cts.map { ct ->
        SBCards(
            deckUUID = deck.uuid,
            type = when (ct) {
                is CT.Basic -> ct.card.type
                is CT.Hint -> ct.card.type
                is CT.ThreeField -> ct.card.type
                is CT.MultiChoice -> ct.card.type
                is CT.Notation -> ct.card.type
            }
        )
    }
    try {
        Log.d("SupabaseViewModel", "Updating cards..")
        val responses = supabase.from("card")
            .upsert(cardsToInsert) {
                select()
            }
            .decodeList<SBCards>()
        Log.d("SupabaseViewModel", "Updated cards..")

        val basicCards = mutableListOf<BasicCard>()
        val hintCards = mutableListOf<HintCard>()
        val threeFieldCards = mutableListOf<ThreeFieldCard>()
        val multiCards = mutableListOf<SBMultiCard>()
        val notationCards = mutableListOf<SBNotationCard>()

        Log.d("SupabaseViewModel", "Updating cardTypes..")

        responses.indices.map { index ->
            when (val ct = cts[index]) {
                is CT.Basic -> basicCards.add(
                    BasicCard(
                        cardId = responses[index].id,
                        question = ct.basicCard.question,
                        answer = ct.basicCard.answer
                    )
                )

                is CT.Hint -> hintCards.add(
                    HintCard(
                        cardId = responses[index].id,
                        question = ct.hintCard.question,
                        hint = ct.hintCard.hint,
                        answer = ct.hintCard.answer
                    )
                )

                is CT.ThreeField -> threeFieldCards.add(
                    ThreeFieldCard(
                        cardId = responses[index].id,
                        question = ct.threeFieldCard.question,
                        middle = ct.threeFieldCard.middle,
                        answer = ct.threeFieldCard.answer
                    )
                )

                is CT.MultiChoice -> multiCards.add(
                    SBMultiCard(
                        cardId = responses[index].id,
                        question = ct.multiChoiceCard.question,
                        choiceA = ct.multiChoiceCard.choiceA,
                        choiceB = ct.multiChoiceCard.choiceB,
                        choiceC = ct.multiChoiceCard.choiceC,
                        choiceD = ct.multiChoiceCard.choiceD,
                        correct = ct.multiChoiceCard.correct
                    )
                )

                is CT.Notation -> {
                    val listStringConverter = ListStringConverter()
                    notationCards.add(
                        SBNotationCard(
                            cardId = responses[index].id,
                            question = ct.notationCard.question,
                            steps = listStringConverter.listToString(ct.notationCard.steps),
                            answer = ct.notationCard.answer
                        )
                    )
                }
            }
        }
        if (basicCards.isNotEmpty()) {
            supabase.from("basicCard").upsert(basicCards)
        }
        if (hintCards.isNotEmpty()) {
            supabase.from("hintCard").upsert(hintCards)
        }
        if (threeFieldCards.isNotEmpty()) {
            supabase.from("threeCard").upsert(threeFieldCards)
        }
        if (multiCards.isNotEmpty()) {
            supabase.from("multiCard").upsert(multiCards)
        }
        if (notationCards.isNotEmpty()) {
            supabase.from("notationCard").upsert(notationCards)
        }
        Log.d("SupabaseViewModel", "Updated cardTypes..")
    } catch (e: Exception) {
        Log.d("upsertCTList", "Couldn't upload CTS: $e")
        return false
    }
    return true
}