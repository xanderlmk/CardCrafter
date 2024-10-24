package com.example.flashcards.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcards.model.Card


@Composable
fun EditTextField(
    value: String,
    onValueChanged: (String) -> Unit,
    labelStr : String ,
    modifier: Modifier
) {
    TextField(
        value = value,
        singleLine = true,
        modifier = modifier,
        onValueChange = onValueChanged,
        label = { Text(labelStr) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
    )
}

@Composable
fun SmallAddButton(onClick:() -> Unit) {
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


@Composable
fun BackButton(onBackClick: () -> Unit,
               modifier: Modifier = Modifier) {
    IconButton(
        onClick = onBackClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            modifier = Modifier
                .size(36.dp),
            contentDescription = "Back"

        )
    }
}

@Composable
fun frontCard(card: Card) : Boolean {
    var clicked by remember { mutableStateOf(false ) }
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
    ){
        Text(
            text = card.question ,
            fontSize = 30.sp,
            color = Color.Black,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(vertical = 4.dp)
        )
        Button(
            onClick = {
                clicked = true
            },
            modifier = Modifier
                .padding(top = 48.dp)
        ) {
            Text("Show Answer")
        }
    }
    return clicked
}

@Composable
fun BackCard(card: Card) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = card.question ,
            fontSize = 30.sp,
            color = Color.Black,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(vertical = 4.dp)
        )
        Text(
            text = card.answer,
            fontSize = 30.sp,
            color = Color.Black,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(vertical = 4.dp)
        )
    }
}
