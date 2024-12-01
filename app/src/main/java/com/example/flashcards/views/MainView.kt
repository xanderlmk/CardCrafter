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
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.flashcards.ui.theme.backgroundColor
import com.example.flashcards.ui.theme.borderColor
import com.example.flashcards.ui.theme.buttonColor
import com.example.flashcards.ui.theme.textColor
import com.example.flashcards.ui.theme.titleColor

class MainView {
    @Composable
    fun DeckList(viewModel: MainViewModel,
                 onNavigateToDeck : (Int) -> Unit,
                 onNavigateToAddDeck  : () -> Unit) {

        val uiState by viewModel.mainUiState.collectAsState()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .background(backgroundColor)
        ) {
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
                            color = titleColor
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(2f)
                            .background(backgroundColor)
                            .padding(16.dp)
                    ) {
                        LazyColumn {
                            items(uiState.deckList) { deck ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .background(backgroundColor)
                                        .clickable {
                                            onNavigateToDeck(deck.id)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .wrapContentWidth()
                                            .border(
                                                width = 2.dp,
                                                color = borderColor,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 8.dp)
                                    ) {
                                        Text(
                                            text = "${deck.name}",
                                            fontSize = 30.sp,
                                            color = textColor,
                                            style = MaterialTheme.typography.titleLarge
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(backgroundColor)
                    ) {
                        val bottomLeftModifier = Modifier
                            .padding(bottom = 12.dp)
                            .align(Alignment.End)

                        Box(
                            modifier = bottomLeftModifier,
                        ) {
                            SmallAddButton(
                                onClick = { onNavigateToAddDeck() },
                            )
                        }
                    }
                }
            }
    }

}