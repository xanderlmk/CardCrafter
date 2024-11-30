package com.example.flashcards.views

//import androidx.compose.foundation.layout.ColumnScopeInstance.weight
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcards.controller.CardViewModel
import com.example.flashcards.controller.MainViewModel
import com.example.flashcards.model.Card
import com.example.flashcards.model.Deck
import com.example.flashcards.model.DeckWithCards
import com.example.flashcards.ui.theme.backgroundColor
import com.example.flashcards.ui.theme.buttonColor
import com.example.flashcards.ui.theme.textColor
import com.example.flashcards.ui.theme.titleColor
import kotlinx.coroutines.launch

class DeckEditView(private var viewModel: CardViewModel){

    @SuppressLint("CoroutineCreationDuringComposition")

    @Composable
    fun ViewFlashCards(deckId: Int, onNavigate: () -> Unit) {
        val coroutineScope = rememberCoroutineScope()
        var deckWithCards by remember { mutableStateOf(DeckWithCards(Deck(0, "Loading..."), emptyList())) }
        var selectedCard by remember { mutableStateOf<Card?>(null) }
        var isEditing by remember { mutableStateOf(false) }

        coroutineScope.launch {
            viewModel.getDeckWithCards(deckId).collect { data ->
                deckWithCards = data
            }
        }
        val presetModifier = Modifier
            .padding(top = 16.dp,start = 16.dp, end = 16.dp)
            .size(54.dp)

        if (isEditing && selectedCard != null) {
            EditFlashCardView(card = selectedCard!!, onDismiss = {
                isEditing = false
                selectedCard = null
            })
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    item {
                        BackButton(
                            onBackClick = { onNavigate() },
                            modifier = presetModifier
                        )
                    }
                    item {
                        Text(
                            text = "Deck: ${deckWithCards.deck.name}",
                            fontSize = 35.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 25.dp,start = 10.dp, end = 10.dp),
                            textAlign = TextAlign.Center,
                            color = titleColor
                        )
                    }
                    items(deckWithCards.cards) { card ->
                        Button(
                            onClick = {
                                selectedCard = card // Set selected card to edit
                                isEditing = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = buttonColor,
                                contentColor = textColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Text(text = "Question: ${card.question}")
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun EditFlashCardView(card: Card, onDismiss: () -> Unit) {
        var question by remember { mutableStateOf(TextFieldValue(card.question)) }
        var answer by remember { mutableStateOf(TextFieldValue(card.answer)) }
        var errorMessage by remember { mutableStateOf("")}
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .background(backgroundColor)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(50.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text(
                    text = "Edit Flashcard",
                    fontSize = 40.sp,
                    textAlign = TextAlign.Center,
                    color = titleColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp)
                        .wrapContentHeight(Alignment.CenterVertically)
                )


                TextField(
                    value = question,
                    onValueChange = { question = it },
                    label = { Text("Question") },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = answer,
                    onValueChange = { answer = it },
                    label = { Text("Answer") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = androidx.compose.ui.graphics.Color.Red,
                        modifier = Modifier.padding(8.dp),
                        fontSize = 16.sp
                    )
                }


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            // Handle updating the flashcard logic here
                            // e.g., viewModel.updateCard(card.copy(question = question.text, answer = answer.text))
                            if (question.text.isNotBlank() && answer.text.isNotBlank()) {
                                viewModel.updateCardDetails(card.id, question.text, answer.text)
                                onDismiss()
                            } else {
                                errorMessage = "please fill out all field"
                            }


                        },
                        modifier = Modifier
                            .weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttonColor,
                            contentColor = textColor
                        )
                    ) {
                        Text("Save")
                    }



                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttonColor,
                            contentColor = textColor
                        )
                    ) {
                        Text("Cancel")
                    }


                }
            }
        }

    }




}