package com.example.flashcards.views

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.flashcards.controller.MainViewModel
import com.example.flashcards.model.Card



class CardDeckView(private var viewModel: MainViewModel) {

    @Composable
    fun ViewCard(deckId : Int) {
        viewModel.getDueCards(deckId)
        val cardUiState by viewModel.cardUiState.collectAsState()
        var currentIndex by remember { mutableIntStateOf(0) }

        /*
        Add single card view of the deck
        Make sure the card belongs to the deck
        Randomize the card order
        Show only the question(front) and once they click
        (for now just "show answer")
        it'll show the answer(back)
         */
        Column (
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize(
                )
        ){
            LazyColumn {
                //cardUiState.cardList are already randomized,
                // just have to get each individual card
                items(cardUiState.cardList) { card ->
                    Text(
                        text = "${card.question} , ${card.answer}",
                        fontSize = 30.sp,
                        color = Color.Black,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .align(Alignment.CenterHorizontally)
                            /*.clickable {
                                selectedDeck = deck
                                whichView = 2
                            }*/
                            .border(width = 1.dp, color = Color.Blue)
                            .fillMaxWidth()
                    )
                }
            }
        }

    }
    // Try to just show one card at t time, totalcards, and index will not be needed

    @Composable
    fun FrontCard(card: Card,index : Int, totalCards : Int)
    {
        var index = index
        Column {
            Column {
                Text(
                    text = card.question
                )
                Text(
                    text = card.answer
                )
            }
        }
    }
}