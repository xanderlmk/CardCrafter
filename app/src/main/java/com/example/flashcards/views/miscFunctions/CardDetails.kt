package com.example.flashcards.views.miscFunctions

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.flashcards.model.tablesAndApplication.BasicCard
import com.example.flashcards.model.tablesAndApplication.MultiChoiceCard

data class CardDetails(
    val question: MutableState<String> = mutableStateOf(""),
    val middleField: MutableState<String> = mutableStateOf(""),
    val answer: MutableState<String> = mutableStateOf(""),
    val choices: MutableList<MutableState<String>> = MutableList(4) { mutableStateOf("") },
    val correct: MutableState<Char> = mutableStateOf('?'),
    val stringList : MutableList<MutableState<String>> = mutableListOf()
)

fun createChoiceCardDetails(multiChoiceCard: MultiChoiceCard): CardDetails {
    return CardDetails(
        question = mutableStateOf(multiChoiceCard.question),
        choices = MutableList(4) {
                mutableStateOf(multiChoiceCard.choiceA)
                mutableStateOf(multiChoiceCard.choiceB)
                mutableStateOf(multiChoiceCard.choiceC)
                mutableStateOf(multiChoiceCard.choiceD)
        },
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

fun createMathCardDetails(
    question : String,
    stringList : List<String>,
    answer: String
) : CardDetails {
    return CardDetails(
        question = mutableStateOf(question),
        stringList = stringList.map {
            val thisString = mutableStateOf(it)
            thisString
        }.toMutableList(),
        answer = mutableStateOf(answer)
    )
}