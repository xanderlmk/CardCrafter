package com.example.flashcards.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcards.controller.MainViewModel
import com.example.flashcards.model.Decks

class DeckView(viewModel: MainViewModel ) {
    private val viewModel = viewModel


    @Composable
    fun viewEditDeck( deck: Decks, onDismiss: () -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                    color = Color.Blue
                )
            }

            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                Button(
                    onClick = {
                        onDismiss()
                    },
                    modifier = Modifier.padding(top = 48.dp)
                ) {
                    Text("Return")
                }
                Button(
                    onClick = {
                        viewModel.deleteDeck(deck)
                        onDismiss()
                    },
                    modifier = Modifier.padding(top = 48.dp)
                ) {
                    Text("Delete Deck")
                }
            }
        }
    }
}