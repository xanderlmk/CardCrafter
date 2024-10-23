package com.example.flashcards.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box


import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcards.controller.MainViewModel
import com.example.flashcards.model.Deck

class MainView {
    @Composable
    fun DeckList(viewModel: MainViewModel,
                 modifier: Modifier = Modifier) {

        val uiState by viewModel.mainUiState.collectAsState()
        val addDeckView = remember { AddDeckView(viewModel) }
        val deckView = remember { DeckView(viewModel) }
        var whichView by remember { mutableIntStateOf(0) }
        var selectedDeck by remember { mutableStateOf<Deck?>(value = null) }
        val presetModifier = Modifier
            .padding(top = 16.dp,start = 16.dp,end = 16.dp)
            .size(54.dp)
            .background(Color.Cyan)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .background(Color.LightGray)
        ) {
        when (whichView) {
            1 -> {
                BackButton (
                    onBackClick = { whichView = 0},
                    modifier = presetModifier
                )
                addDeckView.AddDeck {
                    whichView = 0  // Go back to the main view after adding a deck
                }
            }

            2 -> {
                BackButton (
                    onBackClick = { whichView = 0},
                    modifier = presetModifier
                )
                selectedDeck?.let { deck ->
                    deckView.ViewEditDeck(deck) {
                        whichView = 0  // Go back after editing the deck
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Decks",
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Blue
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(2f)
                            .background(Color.LightGray)
                            .padding(16.dp)
                    ) {

                        LazyColumn {
                            items(uiState.deckList) { deck ->
                                Text(
                                    text = "${deck.name} , ${deck.id}",
                                    fontSize = 30.sp,
                                    color = Color.Black,
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier
                                        .padding(vertical = 4.dp)
                                        .align(Alignment.Center)
                                        .clickable {
                                            selectedDeck = deck
                                            whichView = 2
                                        }
                                        .border(width = 1.dp, color = Color.Blue)
                                        .fillMaxWidth()
                                )
                            }
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray)
                    ) {
                        val bottomLeftModifier = Modifier
                            .padding(bottom = 12.dp)
                            .align(Alignment.End)
                        Box(
                            modifier = bottomLeftModifier,
                        ) {
                            SmallAddButton(
                                onClick = { whichView = 1 },
                            )
                        }
                    }

                    }
                }
            }
        }
    }

}