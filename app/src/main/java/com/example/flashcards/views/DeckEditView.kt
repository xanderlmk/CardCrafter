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
import com.example.flashcards.controller.MainViewModel
import com.example.flashcards.model.Card
import com.example.flashcards.model.Deck
import com.example.flashcards.model.DeckWithCards
import com.example.flashcards.ui.theme.backgroundColor
import com.example.flashcards.ui.theme.buttonColor
import com.example.flashcards.ui.theme.textColor
import com.example.flashcards.ui.theme.titleColor
import kotlinx.coroutines.launch

class DeckEditView(private var viewModel: MainViewModel){
        @Composable
        fun ChangeDeckName(currentName: String, deckID: Int, onDismiss: () -> Unit) {
            var newDeckName by remember { mutableStateOf(currentName) }
            var errorMessage by remember { mutableStateOf("") }
            var isSubmitting by remember { mutableStateOf(false) }
            val coroutineScope = rememberCoroutineScope()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Change Deck Name $deckID",
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                    color = titleColor
                )

                EditTextField(
                    value = newDeckName,
                    onValueChanged = {
                        newDeckName = it
                        errorMessage = "" // Clear error when user types
                    },
                    labelStr = "Deck Name",
                    modifier = Modifier.fillMaxWidth(),
                   // isError = errorMessage.isNotEmpty()
                )

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = androidx.compose.ui.graphics.Color.Red,
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 16.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttonColor,
                            contentColor = textColor
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                if (newDeckName.isBlank()) {
                                    errorMessage = "Deck name cannot be empty"
                                    return@launch
                                }

                                if (newDeckName == currentName) {
                                    onDismiss()
                                   return@launch
                                }

                                isSubmitting = true
                                try {
                                    // First check if the deck name exists
                                    val exists = viewModel.checkIfDeckExists(newDeckName)
                                    if (exists > 0) {
                                        errorMessage = "A deck with this name already exists"
                                      return@launch
                                    }

                                    val result = viewModel.updateDeckName(newDeckName, deckID)
                                    if (result > 0) {
                                        onDismiss()
                                    } else {
                                        errorMessage = "Failed to update deck name"
                                    }
                                } catch (e: Exception) {
                                    errorMessage = e.message ?: "An error occurred"
                                } finally {
                                    isSubmitting = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttonColor,
                            contentColor = textColor
                        ),
                        enabled = !isSubmitting,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                color = textColor,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text("Submit")
                        }
                    }
                }
            }
        }

    @SuppressLint("CoroutineCreationDuringComposition")

    @Composable
    fun ViewFlashCards(deckID: Int, onDismiss: () -> Unit) {
        val coroutineScope = rememberCoroutineScope()
        var deckWithCards by remember { mutableStateOf(DeckWithCards(Deck(0, "Loading..."), emptyList())) }
        var selectedCard by remember { mutableStateOf<Card?>(null) }
        var isEditing by remember { mutableStateOf(false) }

        coroutineScope.launch {
            viewModel.getDeckWithCards(deckID).collect { data ->
                deckWithCards = data
            }
        }

        if (isEditing && selectedCard != null) {
            EditFlashCardView(card = selectedCard!!, onDismiss = {
                isEditing = false
                selectedCard = null
            })
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    item {
                        Text(
                            text = "Deck: ${deckWithCards.deck.name}",
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
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

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonColor,
                        contentColor = textColor),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text("Dismiss")
                }
            }
        }
    }

    @Composable
    fun EditFlashCardView(card: Card, onDismiss: () -> Unit) {
        var question by remember { mutableStateOf(TextFieldValue(card.question)) }
        var answer by remember { mutableStateOf(TextFieldValue(card.answer)) }
        var errorMessage by remember { mutableStateOf("")}

        /*
        BackButton(
            onBackClick = onDismiss,
            modifier = Modifier
        )
*/
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
                        if(question.text.isNotBlank() && answer.text.isNotBlank()) {
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
                ) ){
                    Text("Cancel")
                }



            }
        }

    }




}