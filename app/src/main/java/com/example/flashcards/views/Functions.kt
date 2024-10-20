package com.example.flashcards.views

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp


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
fun BackButton(onBackClick: () -> Unit, modifier:
 Modifier = Modifier) {
    IconButton(onClick = onBackClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back"
        )
    }
}
