package com.example.flashcards.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavController
import com.example.flashcards.model.Deck
import kotlinx.coroutines.delay

class View( var whichView : MutableIntState = mutableIntStateOf(0),
    var onView : MutableState<Boolean> = mutableStateOf(false)
)

class ChoosingView(private var navController: NavController) {
    @Composable
    fun WhichScreen(
        deck: Deck,
        view: View
    ) {
        //println("${view.onView.value}")
        //println("${view.whichView.value}")
        if (!view.onView.value) {
            when (view.whichView.intValue) {
                1 -> {
                    // Navigate to AddCard screen
                    LaunchedEffect(Unit) {
                        delayNavigate()
                        navController.navigate("AddCard/${deck.id}")
                        view.onView.value = true
                    }
                }

                2 -> {
                    // Navigate to ViewCard screen
                    LaunchedEffect(Unit) {
                        delayNavigate()
                        navController.navigate("ViewCard/${deck.id}")
                        view.onView.value = true
                    }
                }

                3 -> {
                    // Navigate to ChangeDeckName screen
                    LaunchedEffect(Unit) {
                        delayNavigate()
                        navController.navigate("ChangeDeckName/${deck.id}/${deck.name}")
                        view.onView.value = true
                    }
                }

                4 -> {
                    LaunchedEffect(Unit) {
                        delayNavigate()
                        navController.navigate("ViewFlashCards/${deck.id}")
                        view.onView.value = true
                    }
                }
            }
        }
    }
}

suspend fun delayNavigate() {
    delay(75)
}
