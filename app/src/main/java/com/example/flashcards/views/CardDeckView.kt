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
import com.example.flashcards.controller.CardUiState
import com.example.flashcards.controller.MainViewModel
import com.example.flashcards.controller.updateCard
import com.example.flashcards.model.Card



class CardDeckView(private var viewModel: MainViewModel) {
    @Composable
    fun ViewCard(deckId: Int,
                 cardUiState: CardUiState,
                 hasCards : Boolean) {
        var size by remember { mutableIntStateOf(0) }
        var index by remember { mutableIntStateOf(0) }
        var show by remember { mutableStateOf(false) }
        var cardList = remember { mutableListOf<Card>() }

        if (hasCards) {
            cardList = cardUiState.cardList
            size = cardList.size
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (size > 0) {
                if (index < size) {
                    val card: Card = cardList[index]

                    if (!show) {
                        show = frontCard(card)
                    } else {
                        BackCard(card)
                        Row {
                            Button(
                                onClick = {
                                    index += 1
                                    show = !show
                                },
                                modifier = Modifier.padding(top = 48.dp)
                            ) {
                                Text("Again")
                            }
                            Button(
                                onClick = {
                                    cardList[index] = updateCard(card,false)
                                    viewModel.updateCard(cardList[index])
                                    index += 1
                                    show = !show
                                          },
                                    modifier = Modifier.padding(top = 48.dp)
                            ) {
                                Text("Hard")
                            }
                            Button(
                                onClick = {
                                    cardList[index] = updateCard(card,true)
                                    viewModel.updateCard(cardList[index])
                                    index += 1
                                    show = !show
                                          },
                                    modifier = Modifier
                                        .padding(top = 48.dp)
                            ) {
                                Text("Good")
                            }
                        }
                    }
                }
                else  {
                    viewModel.getDueCards(deckId)
                    index = 0
                    if (cardUiState.cardList.isNotEmpty()) {
                        cardList = cardUiState.cardList
                        size = cardUiState.cardList.size
                    }
                }
            }
            else {
                Text("No Due Cards")
            }
        }
    }

    @Composable
    fun frontCard(card: Card) : Boolean {
        var clicked by remember { mutableStateOf(false ) }
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
        ){
                Text(
                    text = card.question ,
                    fontSize = 30.sp,
                    color = Color.Black,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                )
                Button(
                    onClick = {
                        clicked = true
                    },
                    modifier = Modifier
                        .padding(top = 48.dp)
                ) {
                    Text("Show Answer")
                }
        }
        return clicked
    }
    @Composable
    fun BackCard(card: Card) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = card.question ,
                fontSize = 30.sp,
                color = Color.Black,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(vertical = 4.dp)
            )
            Text(
                text = card.answer,
                fontSize = 30.sp,
                color = Color.Black,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(vertical = 4.dp)
            )
        }
    }
}