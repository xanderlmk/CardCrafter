package com.example.flashcards.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box


import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalProvider
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcards.controller.MainController

class MainView(controller : MainController) {
    private var controller = controller
    @Composable
    fun DeckList(name: String, modifier: Modifier = Modifier) {
        var addDeckBool by remember { mutableStateOf(false) }
        val addDeckView = remember { AddDeckView(controller) }
        var deckList = remember { mutableStateOf(controller.getDeckList()) }


        if (addDeckBool) {
            addDeckView.addDeck() {
                    addDeckBool = false
                }
        }

        else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .background(Color.LightGray),
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
                        fontSize = 30.sp,
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
                        items(deckList.value) { deck ->
                            Text(
                                text = "${deck.name} , ${deck.id}",
                                fontSize = 25.sp,
                                color = Color.Black,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .align(Alignment.Center)
                                    .clickable {
                                    }
                                    .border(width = 1.dp, color = Color.Blue)
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
                        Example(
                            onClick = { addDeckBool = true },
                        )
                    }
                }

            }
        }
    }

    @Composable
    fun Example(onClick:() -> Unit) {
            FloatingActionButton(
                onClick = {
                    onClick()
                },
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Icon(Icons.Filled.Add, "Floating action button.")
            }
    }

}