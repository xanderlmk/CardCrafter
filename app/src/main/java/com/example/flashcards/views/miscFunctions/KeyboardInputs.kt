package com.example.flashcards.views.miscFunctions

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.example.flashcards.ui.theme.textColor

/** This more for the MultiChoiceCard Choice inputs */
@Composable
fun EditTextField(
    value: String,
    onValueChanged: (String) -> Unit,
    labelStr: String,
    modifier: Modifier,
    inputColor: Color = Color.Transparent
) {
    val focusManager = LocalFocusManager.current
    val colors = if (inputColor == Color.Transparent) {
        TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer

        )
    } else {
        TextFieldDefaults.colors(
            unfocusedTextColor = inputColor,
            focusedTextColor = inputColor,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    }
    TextField(
        value = value,
        singleLine = false,
        modifier = modifier,
        onValueChange = onValueChanged,
        label = { Text(labelStr, color = textColor) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
            }
        ),
        colors = colors,
        textStyle =
        if (inputColor == Color.Transparent) {
            TextStyle.Default
        } else {
            TextStyle(
                fontWeight = FontWeight.ExtraBold,
                fontStyle = FontStyle.Italic,
                background = MaterialTheme.colorScheme.surface
            )
        }
    )
}

/** Rather than clicking done (enter) it'll go to the next line */
@Composable
fun EditTextFieldNonDone(
    value: String,
    onValueChanged: (String) -> Unit,
    labelStr: String,
    modifier: Modifier,
) {
    val focusManager = LocalFocusManager.current
    TextField(
        value = value,
        singleLine = false,
        modifier = modifier,
        onValueChange = onValueChanged,
        label = { Text(labelStr, color = textColor) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
            }
        )
    )
}

@Composable
fun EditDoubleField(
    value: String,
    onValueChanged: (String) -> Unit,
    labelStr: String,
    modifier: Modifier
) {
    val colors = TextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer

    )
    TextField(
        value = value,
        singleLine = true,
        modifier = modifier,
        onValueChange = onValueChanged,
        label = { Text(labelStr, color = textColor) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        colors = colors

    )
}

@Composable
fun EditIntField(
    value: String,
    onValueChanged: (String) -> Unit,
    labelStr: String,
    modifier: Modifier
) {
    val colors = TextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
    )
    TextField(
        value = value,
        singleLine = true,
        modifier = modifier,
        onValueChange = onValueChanged,
        label = {
            Text(
                labelStr, color = textColor, fontSize = 12.sp
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = colors,
    )
}