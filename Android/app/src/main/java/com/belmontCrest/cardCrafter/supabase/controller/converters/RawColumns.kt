package com.belmontCrest.cardCrafter.supabase.controller.converters

import com.belmontCrest.cardCrafter.BuildConfig
import io.github.jan.supabase.postgrest.query.Columns

object RawColumns {
    private const val BASIC = BuildConfig.SB_BASIC_TN
    private const val THREE = BuildConfig.SB_THREE_TN
    private const val HINT = BuildConfig.SB_HINT_TN
    private const val MULTI = BuildConfig.SB_MULTI_TN
    private const val NOTATION = BuildConfig.SB_NOTATION_TN
    val Basic = Columns.raw(
        "id, type, deckUUID, cardIdentifier," +
                " $BASIC(card_id, question, answer)"
    )
    val Three = Columns.raw(
        "id, type, deckUUID, cardIdentifier," +
                " $THREE(card_id, question, middle, answer)"
    )
    val Hint = Columns.raw(
        "id, type, deckUUID, cardIdentifier," +
                " $HINT(card_id, question, hint, answer)"
    )
    val Multi = Columns.raw(
        "id, type, deckUUID, cardIdentifier," +
                " $MULTI(card_id, question, choiceA, choiceB, " +
                " choiceC, choiceD, correct)"
    )
    val Notation = Columns.raw(
        "id, type, deckUUID, cardIdentifier," +
                " $NOTATION(card_id, question, steps, answer)"
    )
}