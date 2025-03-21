package com.example.flashcards.supabase.controller

import com.example.flashcards.model.tablesAndApplication.BasicCard
import com.example.flashcards.model.tablesAndApplication.HintCard
import com.example.flashcards.model.tablesAndApplication.NotationCard
import com.example.flashcards.model.tablesAndApplication.ListStringConverter
import com.example.flashcards.model.tablesAndApplication.MultiChoiceCard
import com.example.flashcards.model.tablesAndApplication.ThreeFieldCard
import com.example.flashcards.supabase.model.SBCards
import com.example.flashcards.supabase.model.SBNotationCard
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

suspend fun insertCard(supabase: SupabaseClient, uuid: String, type: String): SBCards {
    val response = supabase.from("card").insert(
        SBCards(
            deckUUID = uuid,
            type = type
        )
    ) {
        select()
    }.decodeSingle<SBCards>()
    return response
}

suspend fun insertBasicCT(id: Int, supabase: SupabaseClient, basicCard: BasicCard) {
    supabase.from("basicCard").insert(
        BasicCard(
            cardId = id,
            question = basicCard.question,
            answer = basicCard.answer
        )
    )
}

suspend fun insertHintCT(id: Int, supabase: SupabaseClient, hintCard: HintCard) {
    supabase.from("hintCard").insert(
        HintCard(
            cardId = id,
            question = hintCard.question,
            hint = hintCard.hint,
            answer = hintCard.answer
        )
    )
}

suspend fun insertThreeCT(id: Int, supabase: SupabaseClient, threeFieldCard: ThreeFieldCard) {
    supabase.from("threeCard").insert(
        ThreeFieldCard(
            cardId = id,
            question = threeFieldCard.question,
            middle = threeFieldCard.middle,
            answer = threeFieldCard.answer
        )
    )
}

suspend fun insertMultiCT(id: Int, supabase: SupabaseClient, multiChoiceCard: MultiChoiceCard) {
    supabase.from("multiCard").insert(
        MultiChoiceCard(
            cardId = id,
            question = multiChoiceCard.question,
            choiceA = multiChoiceCard.choiceA,
            choiceB = multiChoiceCard.choiceB,
            choiceC = multiChoiceCard.choiceC,
            choiceD = multiChoiceCard.choiceD,
            correct = multiChoiceCard.correct
        )
    )
}

suspend fun insertNotationCT(id: Int, supabase: SupabaseClient, notationCard: NotationCard) {
    val listStringConverter = ListStringConverter()
    supabase.from("notationCard").insert(
        SBNotationCard(
            cardId = id,
            question = notationCard.question,
            steps = listStringConverter.listToString(notationCard.steps),
            answer = notationCard.answer
        )
    )
}




