package com.example.flashcards.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcards.model.Card
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import com.example.flashcards.R
import com.example.flashcards.ui.theme.buttonColor
import com.example.flashcards.ui.theme.textColor
import kotlinx.coroutines.delay


@Composable
fun EditTextField(
    value: String,
    onValueChanged: (String) -> Unit,
    labelStr : String ,
    modifier: Modifier,
) {
    TextField(
        value = value,
        singleLine = true,
        modifier = modifier,
        onValueChange = onValueChanged,
        label = { Text(labelStr, color = textColor) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
    )
}


@Composable
fun frontCard(card: Card) : Boolean {
    var clicked by remember { mutableStateOf(false ) }
    Box(
        modifier = Modifier
            .fillMaxSize() // Fill the entire available space
            .padding(16.dp)
    ) {
        Text(
            text = card.question ,
            fontSize = 30.sp,
            color = textColor,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(vertical = 80.dp)
                .align(Alignment.TopCenter)
        )
        Button(
            onClick = {
                clicked = true
            },
            modifier = Modifier
                .align(Alignment.BottomCenter) // Align to the bottom center
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor,
                contentColor = textColor
            )
        ) {
            Text(stringResource(R.string.show_answer))
        }
    }
    return clicked
}

@Composable
fun BackCard(card: Card) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column {
                Text(
                    text = card.question,
                    fontSize = 30.sp,
                    color = textColor,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 80.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Text(
                    text = card.answer,
                    fontSize = 30.sp,
                    color = textColor,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }

}

@Composable
fun LoadingText() {
    Text(
        stringResource(R.string.loading),
        fontSize = 35.sp,
        textAlign = TextAlign.Center,
        color = textColor,
        style = MaterialTheme.typography.titleLarge
    )
}

@Composable
fun NoDueCards() {
    Text(
        stringResource(R.string.no_due_cards),
        fontSize = 25.sp,
        lineHeight = 26.sp,
        textAlign = TextAlign.Center,
        color = textColor,
        style = MaterialTheme.typography.titleLarge
    )
}
suspend fun loading() : Boolean{
    delay(60)
    return false
}
