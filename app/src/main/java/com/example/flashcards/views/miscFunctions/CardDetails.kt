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
    val correct: MutableState<Char> = mutableStateOf('?')
)

fun createChoiceCardDetails(multiChoiceCard: MultiChoiceCard): CardDetails {
    val cD = mutableStateOf(CardDetails())
    cD.value.question.value = multiChoiceCard.question
    cD.value.choices[0].value = multiChoiceCard.choiceA
    cD.value.choices[1].value = multiChoiceCard.choiceB
    cD.value.choices[2].value = multiChoiceCard.choiceC
    cD.value.choices[3].value = multiChoiceCard.choiceD
    cD.value.correct.value = multiChoiceCard.correct
    return cD.value
}
fun createBasicCardDetails(basicCard: BasicCard): CardDetails {
    val cD = mutableStateOf(CardDetails())
    cD.value.question.value = basicCard.question
    cD.value.answer.value = basicCard.answer
    return cD.value
}
fun createThreeOrHintCardDetails(
    question: String,
    middleField: String,
    answer: String
): CardDetails {
    val cD = mutableStateOf(CardDetails())
    cD.value.question.value = question
    cD.value.middleField.value = middleField
    cD.value.answer.value = answer
    return cD.value
}