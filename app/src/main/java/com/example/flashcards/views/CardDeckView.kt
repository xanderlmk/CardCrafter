package com.example.flashcards.views


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flashcards.controller.AppViewModelProvider
import com.example.flashcards.controller.CardViewModel
import com.example.flashcards.controller.MainViewModel
import com.example.flashcards.controller.handleCardUpdate
import com.example.flashcards.controller.moveToNextCard
import com.example.flashcards.controller.updateCard
import com.example.flashcards.model.Card
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class CardDeckView() {
    @Composable
    fun ViewCard(deckId: Int) {
        val viewModel : CardViewModel = viewModel(factory = AppViewModelProvider.Factory)
        val cardUiState by viewModel.cardUiState.collectAsState()
        var show by remember { mutableStateOf(false) }
        var currentCard by remember { mutableStateOf<Card?>(null) }

        if (cardUiState.cardList.isEmpty()) {
            viewModel.getDueCards(deckId)
        }
            currentCard = cardUiState.cardList.firstOrNull() // Start with the first card

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            if (currentCard == null && cardUiState.cardList.size == 0) {
                LaunchedEffect(currentCard == null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(200) // Delay for smooth transition
                        viewModel.getDueCards(deckId)
                    }
                }
                Text("No Due Cards")
            } else {
                if (!show) {
                    show = frontCard(currentCard!!)
                } else {
                    val good = ((currentCard!!.passes+1) * 1.5).toInt()
                    val hard = (currentCard!!.passes * 0.5).toInt()
                    BackCard(currentCard!!)
                    Row (
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                currentCard!!.passes = 0
                                handleCardUpdate(currentCard!!, false, viewModel)
                                CoroutineScope(Dispatchers.Main).launch {
                                    show = !show
                                }

                            },
                            modifier = Modifier.padding(top = 48.dp)
                        ) { Text("Again") }
                        Column(
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                        Button(
                            onClick = {
                                handleCardUpdate(currentCard!!, false, viewModel)
                                moveToNextCard(
                                    cardUiState.cardList,
                                    onNextCard = { nextCard ->
                                        currentCard = nextCard
                                        show = !show // Reset show state for new card
                                    }
                                )
                            },
                            modifier = Modifier.padding(top = 48.dp)
                        ) { Text("Hard") }
                            Text(
                                "$hard days"
                            )

                        }
                        Column(
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Button(
                                onClick = {
                                    handleCardUpdate(currentCard!!, true, viewModel)
                                    moveToNextCard(
                                        cardUiState.cardList,
                                        onNextCard = { nextCard ->
                                            currentCard = nextCard
                                            show = !show // Toggle show after delay
                                        })
                                },
                                modifier = Modifier.padding(top = 48.dp)
                            ) { Text("Good") }
                            Text(
                                "$good days"
                            )
                        }
                    }
                }
            }
        }
    }
}