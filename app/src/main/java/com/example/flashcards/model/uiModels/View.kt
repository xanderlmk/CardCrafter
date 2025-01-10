package com.example.flashcards.model.uiModels

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf

class View(
    var whichView : MutableIntState = mutableIntStateOf(0),
    var onView : MutableState<Boolean> = mutableStateOf(false)
)

sealed class CardUpdateError(val exception: Exception) : Exception(exception) {
    class NetworkError(exception: Exception) : CardUpdateError(exception)
    class DatabaseError(exception: Exception) : CardUpdateError(exception)
    class UnknownError(exception: Exception) : CardUpdateError(exception)
}