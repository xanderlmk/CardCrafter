package com.belmontCrest.cardCrafter.views.miscFunctions.details

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.belmontCrest.cardCrafter.localDatabase.tables.BasicCard
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.localDatabase.tables.ListStringConverter
import com.belmontCrest.cardCrafter.localDatabase.tables.MultiChoiceCard
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsBasic
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsHint
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsMulti
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsNotation
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsThree
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsWithCT

data class CardDetails(
    val question: MutableState<String> = mutableStateOf(""),
    val middleField: MutableState<String> = mutableStateOf(""),
    val answer: MutableState<String> = mutableStateOf(""),
    val choices: MutableList<MutableState<String>> = MutableList(4) { mutableStateOf("") },
    val correct: MutableState<Char> = mutableStateOf('?'),
    val stringList: MutableList<MutableState<String>> = mutableListOf()
)

private val listStringC = ListStringConverter()


fun SBCardColsWithCT.toCardDetails(): CardDetails = when (this) {
    is SBCardColsBasic -> CardDetails(
        question = mutableStateOf(basicCard.question),
        answer = mutableStateOf(basicCard.answer)
    )

    is SBCardColsThree -> CardDetails(
        question = mutableStateOf(threeCard.question),
        middleField = mutableStateOf(threeCard.middle),
        answer = mutableStateOf(threeCard.answer)
    )

    is SBCardColsHint -> CardDetails(
        question = mutableStateOf(hintCard.question),
        middleField = mutableStateOf(hintCard.hint),
        answer = mutableStateOf(hintCard.answer)
    )

    is SBCardColsMulti -> CardDetails(
        question = mutableStateOf(multiCard.question),
        choices = mutableListOf(
            mutableStateOf(multiCard.choiceA),
            mutableStateOf(multiCard.choiceB),
            mutableStateOf(multiCard.choiceC ?: ""),
            mutableStateOf(multiCard.choiceD ?: "")
        ),
        correct = mutableStateOf(multiCard.correct)
    )

    is SBCardColsNotation -> CardDetails(
        question = mutableStateOf(notationCard.question),
        answer = mutableStateOf(notationCard.answer),
        stringList = listStringC.fromString(notationCard.steps).map {
            val thisString = mutableStateOf(it)
            thisString
        }.toMutableList()
    )
}

fun CT.toCardDetails(): CardDetails = when (this) {
    is CT.Basic -> CardDetails(
        question = mutableStateOf(basicCard.question),
        answer = mutableStateOf(basicCard.answer)
    )

    is CT.Hint -> CardDetails(
        question = mutableStateOf(hintCard.question),
        answer = mutableStateOf(hintCard.answer),
        middleField = mutableStateOf(hintCard.hint)
    )

    is CT.ThreeField -> CardDetails(
        question = mutableStateOf(threeFieldCard.question),
        middleField = mutableStateOf(threeFieldCard.middle),
        answer = mutableStateOf(threeFieldCard.answer)
    )

    is CT.MultiChoice -> CardDetails(
        question = mutableStateOf(multiChoiceCard.question),
        // only include non‑blank choices
        choices = mutableListOf(
            mutableStateOf(multiChoiceCard.choiceA),
            mutableStateOf(multiChoiceCard.choiceB),
            mutableStateOf(multiChoiceCard.choiceC),
            mutableStateOf(multiChoiceCard.choiceD)
        ),
        correct = mutableStateOf(multiChoiceCard.correct)
    )

    is CT.Notation -> CardDetails(
        question = mutableStateOf(notationCard.question),
        answer = mutableStateOf(notationCard.answer),
        stringList = notationCard.steps.map {
            val thisString = mutableStateOf(it)
            thisString
        }.toMutableList(),
    )
}

fun createChoiceCardDetails(multiChoiceCard: MultiChoiceCard): CardDetails {
    return CardDetails(
        question = mutableStateOf(multiChoiceCard.question),
        choices = mutableListOf(
            mutableStateOf(multiChoiceCard.choiceA),
            mutableStateOf(multiChoiceCard.choiceB),
            mutableStateOf(multiChoiceCard.choiceC),
            mutableStateOf(multiChoiceCard.choiceD),
        ),
        correct = mutableStateOf(multiChoiceCard.correct)
    )
}

fun createBasicCardDetails(basicCard: BasicCard): CardDetails {
    return CardDetails(
        question = mutableStateOf(basicCard.question),
        answer = mutableStateOf(basicCard.answer)
    )
}

fun createThreeOrHintCardDetails(
    question: String,
    middleField: String,
    answer: String
): CardDetails {
    return CardDetails(
        question = mutableStateOf(question),
        middleField = mutableStateOf(middleField),
        answer = mutableStateOf(answer)
    )
}

fun createNotationCardDetails(
    question: String,
    stringList: List<String>,
    answer: String
): CardDetails {
    return CardDetails(
        question = mutableStateOf(question),
        stringList = stringList.map {
            val thisString = mutableStateOf(it)
            thisString
        }.toMutableList(),
        answer = mutableStateOf(answer)
    )
}


sealed class CDetails {
    data class BasicCD(val question: String, val answer: String) : CDetails()

    data class ThreeHintCD(
        val question: String, val middle: String, val answer: String
    ) : CDetails()

    data class MultiCD(
        val question: String, val choiceA: String,
        val choiceB: String, val choiceC: String = "",
        val choiceD: String = "", val correct: Char
    ) : CDetails()

    data class NotationCD(
        val question: String, val steps: List<String>, val answer: String
    ) : CDetails()


}