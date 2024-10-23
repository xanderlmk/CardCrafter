package com.example.flashcards.views

import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flashcards.controller.AppViewModelProvider
import com.example.flashcards.controller.MainViewModel
import com.example.flashcards.model.Card

import com.example.flashcards.model.Deck


class DeckView(private var mainViewModel: MainViewModel) {


    @Composable
    fun ViewEditDeck(deck: Deck, onDismiss: () -> Unit) {
        mainViewModel.getDueCards(deck.id)
        val cardUiState by mainViewModel.cardUiState.collectAsState()
        val addCardView = remember { AddCardView(mainViewModel) }
        val cardView = remember { CardDeckView(mainViewModel) }
        var whichView by remember { mutableIntStateOf(0) }
        val presetModifier = Modifier
            .padding(top = 16.dp,start = 16.dp, end = 16.dp)
            .size(54.dp)
            .background(Color.Cyan)
        when (whichView) {
            1 -> {
                BackButton(
                    onBackClick = { whichView = 0 },
                    modifier = presetModifier
                )
                addCardView.AddCard(deck.id) {
                    whichView = 0
                }
            }
            2 -> {
                BackButton(
                    onBackClick = { whichView = 0 },
                    modifier = presetModifier
                )
                var cardList = remember { mutableListOf<Card>() }
                var size by remember { mutableIntStateOf(0) }
                if (cardUiState.cardList.isNotEmpty()) {
                    cardList = cardUiState.cardList
                    size = cardUiState.cardList.size
                }
                cardView.ViewCard(deck.id, cardUiState, cardList, size)

            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = deck.name,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Blue,
                            textAlign = TextAlign.Center,
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                mainViewModel.deleteDeck(deck)
                                onDismiss()
                            },
                            modifier = Modifier.padding(top = 48.dp)
                        ) {
                            Text("Delete Deck")
                        }
                        Button(
                            onClick = {
                                whichView = 1
                            },
                            modifier = Modifier.padding(top = 48.dp)
                        ) {
                            Text("Add Cards")
                        }
                        Button(
                            onClick = {
                                whichView = 2
                            },
                            modifier = Modifier.padding(top = 48.dp)
                        ) {
                            Text("Start Deck")
                        }
                    }
                }
            }
        }
    }
}