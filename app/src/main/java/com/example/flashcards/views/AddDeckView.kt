package com.example.flashcards.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcards.controller.MainController
import androidx.compose.ui.text.style.TextAlign


class AddDeckView(controller : MainController) {
    private var controller = controller

    @Composable
    fun addDeck(onDismiss: () -> Unit) {
        var deckName by remember { mutableStateOf(controller.getModelDeckName()) }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Add deck",
                fontSize = 50.sp,
                textAlign = TextAlign.Center,
                lineHeight = 116.sp
            )
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                controller.EditTextField(
                    value = deckName,
                    onValueChanged = { newText ->
                        deckName = newText
                    },
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .weight(1f)
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
                        if (controller.addDeck(
                                deckName
                            )
                        ) {
                            deckName = ""
                            controller.emptyDecision()
                        }
                    },
                    modifier = Modifier.padding(top = 48.dp)
                ) {
                    Text("Submit")
                }
            }
        }
    }
}