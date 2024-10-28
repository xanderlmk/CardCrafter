package com.example.flashcards.views


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flashcards.controller.AppViewModelProvider
import com.example.flashcards.controller.CardViewModel
import com.example.flashcards.controller.handleCardUpdate
import com.example.flashcards.controller.moveToNextCard
import com.example.flashcards.model.Card
import com.example.flashcards.ui.theme.buttonColor
import com.example.flashcards.ui.theme.textColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class CardDeckView{
    @Composable
    fun ViewCard(deckId: Int) {
        val viewModel : CardViewModel = viewModel(factory = AppViewModelProvider.Factory)
        val cardUiState by viewModel.cardUiState.collectAsState()
        var show by remember { mutableStateOf(false) }
       // var currentCard by remember { mutableStateOf<Card?>(null) }
        var index by remember { mutableIntStateOf(0) }
        //var index = 0

       //if (cardUiState.cardList.isEmpty()) {
            viewModel.getDueCards(deckId)
        //}
            //currentCard = cardUiState.cardList.firstOrNull() // Start with the first card

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            //if (currentCard == null && cardUiState.cardList.isEmpty()) {
            if (cardUiState.cardList.isEmpty()) {
                //viewModel.getDueCards(deckId)
                LaunchedEffect(cardUiState.cardList.isEmpty()) {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(250) // Delay for smooth transition
                    }
                }
                Text(
                    "No Due Cards",
                    color= textColor
                )
            } else {
                if (index < cardUiState.cardList.size) {
                    if (!show) {
                        //show = frontCard(currentCard!!)
                        show = frontCard(cardUiState.cardList[index])
                    } else {
                        //val good = ((currentCard!!.passes + 1) * 1.5).toInt()
                        //val hard = (currentCard!!.passes * 0.5).toInt()

                        val good = ((cardUiState.cardList[index].passes + 1) * 1.5).toInt()
                        val hard = (cardUiState.cardList[index].passes * 0.5).toInt()
                        //BackCard(currentCard!!)
                        BackCard(cardUiState.cardList[index])
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    //currentCard!!.passes = 0
                                    cardUiState.cardList[index].passes = 0
                                    //handleCardUpdate(currentCard!!, false, viewModel)
                                    handleCardUpdate(cardUiState.cardList[index],
                                        false, viewModel)
                                    println(" index : $index")
                                    CoroutineScope(Dispatchers.Main).launch {
                                        show = !show
                                    }

                                },
                                modifier = Modifier.padding(top = 48.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = buttonColor,
                                            contentColor = textColor
                                        )
                            ) { Text("Again") }
                            Column(
                                verticalArrangement = Arrangement.Top,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Button(
                                    onClick = {
                                        //handleCardUpdate(currentCard!!, false, viewModel)
                                        handleCardUpdate(cardUiState.cardList[index],
                                            false, viewModel)
                                        //index = (index + 1) % cardUiState.cardList.size
                                        show = !show
                                        CoroutineScope(Dispatchers.Main).launch {
                                            delay(100) // Adjust as needed
                                            index = (index + 1) % cardUiState.cardList.size // Move to next card after flip
                                        }
                                        /*moveToNextCard(
                                            cardUiState.cardList,
                                            onNextCard = { nextCard ->
                                                currentCard = nextCard
                                                show = !show // Reset show state for new card
                                            }
                                        )*/
                                    },
                                    modifier = Modifier.padding(top = 48.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = buttonColor,
                                        contentColor = textColor
                                    )
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
                                        //handleCardUpdate(currentCard!!, true, viewModel)
                                        handleCardUpdate(cardUiState.cardList[index],
                                            true, viewModel)
                                        //index = (index + 1) % cardUiState.cardList.size
                                        show = !show
                                        CoroutineScope(Dispatchers.Main).launch {
                                            delay(200)
                                            index = (index + 1) % cardUiState.cardList.size
                                        }
                                        /*moveToNextCard(
                                            cardUiState.cardList,
                                            onNextCard = { nextCard ->
                                                currentCard = nextCard
                                                show = !show // Toggle show after delay
                                            }
                                            )*/
                                    },
                                    modifier = Modifier.padding(top = 48.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = buttonColor,
                                        contentColor = textColor
                                    )
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
}