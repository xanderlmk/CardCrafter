package com.belmontCrest.cardCrafter.views.misc.details

import android.os.Parcelable
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.AnswerParam
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.localDatabase.tables.ListStringConverter
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.MiddleParam
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.Param
import com.belmontCrest.cardCrafter.localDatabase.tables.PartOfQorA
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.toQuestion
import com.belmontCrest.cardCrafter.model.ui.states.CDetails
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsBasic
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsHint
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsMulti
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsNotation
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsThree
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsWithCT
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

fun CT.toQuestion(): String = when (this) {
    is CT.Basic -> this.basicCard.question
    is CT.Hint -> this.hintCard.question
    is CT.MultiChoice -> this.multiChoiceCard.question
    is CT.Notation -> this.notationCard.question
    is CT.ThreeField -> this.threeFieldCard.question
    is CT.Custom -> this.customCard.question.toQuestion()
}

private val listStringC = ListStringConverter()

fun SBCardColsWithCT.toCardDetails(): CDetails =
    when (this) {
        is SBCardColsBasic -> CDetails(
            question = basicCard.question, answer = basicCard.answer
        )

        is SBCardColsThree -> CDetails(
            question = threeCard.question, middle = threeCard.middle, answer = threeCard.answer
        )

        is SBCardColsHint -> CDetails(
            question = hintCard.question, middle = hintCard.hint, answer = hintCard.answer
        )

        is SBCardColsMulti -> CDetails(
            question = multiCard.question,
            choices = listOf(
                multiCard.choiceA, multiCard.choiceB,
                multiCard.choiceC ?: "", multiCard.choiceD ?: ""
            ), correct = multiCard.correct
        )

        is SBCardColsNotation -> CDetails(
            question = notationCard.question,
            answer = notationCard.answer,
            steps = listStringC.fromString(notationCard.steps).map {
                it
            }.toMutableList()
        )
    }


@Serializable
@Parcelize
sealed class CardDetails : Parcelable {
    data class BasicCD(val question: String, val answer: String) : CardDetails()

    data class ThreeCD(
        val question: String, val middle: String, val answer: String, val isQOrA: PartOfQorA
    ) : CardDetails()

    data class HintCD(
        val question: String, val middle: String, val answer: String
    ) : CardDetails()

    data class MultiCD(
        val question: String, val choiceA: String,
        val choiceB: String, val choiceC: String = "",
        val choiceD: String = "", val correct: Char
    ) : CardDetails()

    data class NotationCD(
        val question: String, val steps: List<String>, val answer: String
    ) : CardDetails()

    data class CustomCD(
        val question: Param, val middle: MiddleParam, val answer: AnswerParam
    ) : CardDetails()
}
