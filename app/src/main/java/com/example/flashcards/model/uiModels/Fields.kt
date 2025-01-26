package com.example.flashcards.model.uiModels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf

class Fields(
    var question : MutableState<String> = mutableStateOf(""),
    var middleField : MutableState<String> = mutableStateOf(""),
    var answer : MutableState<String> = mutableStateOf(""),
    var choices: MutableList<MutableState<String>> = MutableList(4) { mutableStateOf("") },
    var correct: MutableState<Char> = mutableStateOf('?'),
    var scrollPosition : MutableState<Int> = mutableIntStateOf(0),
    val mainClicked : MutableState<Boolean> = mutableStateOf(false),
    val inDeckClicked : MutableState<Boolean> = mutableStateOf(false),
) {
    fun resetFields() {
        question.value = ""
        middleField.value = ""
        answer.value = ""
        choices[0].value = ""
        choices[1].value = ""
        choices[2].value = ""
        choices[3].value = ""
        correct.value = '?'
    }
}