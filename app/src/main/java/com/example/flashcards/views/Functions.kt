package com.example.flashcards.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcards.model.Card
import com.example.flashcards.ui.theme.borderColor
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import com.example.flashcards.ui.theme.buttonColor
import com.example.flashcards.ui.theme.iconColor
import com.example.flashcards.ui.theme.textColor
import com.example.flashcards.ui.theme.titleColor
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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
fun SmallAddButton(
    onClick:() -> Unit,
    backgroundColor: Color = titleColor,
    iconSize: Int = 45
) {
    FloatingActionButton(
        onClick = {
            onClick()
        },
        modifier = Modifier
            .padding(16.dp)

    ) {
        Icon(
            Icons.Outlined.Add,
            "Add Deck",
            modifier = Modifier.size(iconSize.dp),
            tint = borderColor,
            )
    }
}

@Composable
fun AddCardButton (
    onClick:() -> Unit,
    backgroundColor: Color = titleColor,
    iconSize: Int = 45
){
    ExtendedFloatingActionButton(
        onClick = { onClick()},
        modifier = Modifier
            .padding(16.dp)
    ) {
        Icon(Icons.Filled.Add, "Add Card")
        Text(text = "Add Card")
    }

}


@Composable
fun BackButton(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = titleColor
) {
    val coroutineScope = rememberCoroutineScope()
    IconButton(
        onClick = {
            coroutineScope.launch {
                delayNavigate()
                onBackClick()
            }
        },
        modifier = modifier
            .background(color= backgroundColor, shape = RoundedCornerShape(16.dp))
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            modifier = Modifier
                .size(36.dp),
            contentDescription = "Back",
            tint = iconColor

        )
    }
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
            Text("Show Answer")
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


suspend fun loading() : Boolean{
    delay(50)
    return false
}
