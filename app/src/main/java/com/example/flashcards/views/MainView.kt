package com.example.flashcards.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.flashcards.controller.MainController

class MainView(controller : MainController) {
    private var controller = controller
    @Composable
    fun DeckList(name: String, modifier: Modifier = Modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn {
                items(controller.getDeckList()) { deck ->
                    Text(
                        text = "Deck: ${deck.name}",
                        color = Color.Black,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .clickable {

                            }
                    )
                }
            }
        }
    }
}