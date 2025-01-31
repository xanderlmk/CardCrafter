package com.example.flashcards.views.miscFunctions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.flashcards.R

import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.ui.theme.textColor
import kotlinx.coroutines.delay
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import com.example.flashcards.model.uiModels.CardDeckCardLists
import com.example.flashcards.model.uiModels.Fields
import com.example.flashcards.ui.theme.GetModifier


@Composable
fun AgainText(getModifier: GetModifier) {
    Text(
        "-----",
        color = getModifier.titleColor(),
        fontSize = 12.sp,
        lineHeight = 14.sp
    )
}

@Composable
fun HardText(
    updatedDueCards: CardDeckCardLists,
    index: MutableIntState, hard: Int,
    getModifier: GetModifier
) {
    Text(
        text =
        if (updatedDueCards.allCards[index.intValue].card.reviewsLeft == 1) {
            "$hard " + stringResource(R.string.days)
        } else {
            "${updatedDueCards.allCards[index.intValue].card.reviewsLeft} " + "reviews left"
        },
        color = getModifier.titleColor(),
        fontSize = 12.sp,
        lineHeight = 14.sp
    )
}

@Composable
fun GoodText(
    updatedDueCards: CardDeckCardLists,
    index: MutableIntState, good: Int,
    getModifier: GetModifier
) {
    Text(
        text =
        if (updatedDueCards.allCards[index.intValue].card.reviewsLeft == 1) {
            "$good " + stringResource(R.string.days)
        } else {
            "${
                updatedDueCards.allCards[index.intValue].card.reviewsLeft - 1
            } " +
                    "reviews left"
        },
        color = getModifier.titleColor(),
        fontSize = 12.sp,
        lineHeight = 14.sp
    )
}

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


@Composable
fun NoDueCards(getModifier: GetModifier) {
    var delay by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(150)
        delay = true
    }
    if (delay) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                stringResource(R.string.no_due_cards),
                fontSize = 25.sp,
                lineHeight = 26.sp,
                textAlign = TextAlign.Center,
                color = getModifier.titleColor(),
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Composable
fun ShowBackButtonAndDeckName(
    onNavigate: () -> Unit,
    deck: Deck,
    presetModifier: Modifier,
    getModifier: GetModifier
) {
    Row {
        BackButton(
            onBackClick = {
                onNavigate()
            },
            modifier = presetModifier
                .fillMaxSize(),
            getModifier = getModifier
        )
        Text(
            text = stringResource(R.string.deck) + ": ${deck.name}",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 35.sp,
            modifier = Modifier
                .padding(top = 16.dp, start = 8.dp, end = 8.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = getModifier.buttonTextColor()
        )
    }
}

@Composable
fun PickAnswerChar(fields: Fields, getModifier: GetModifier) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = { expanded = true },
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
        ) {
            Row {
                Text(
                    text = stringResource(R.string.answer) +
                            ": ${fields.correct.value.uppercase()}",
                    modifier = Modifier.padding(2.dp)
                )
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Answer",
                    tint = getModifier.titleColor(),
                    modifier = Modifier.padding(2.dp)
                )
            }
        }
        Box(
            Modifier.fillMaxWidth(.25f),
            contentAlignment = Alignment.BottomEnd
        ) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    onClick = {
                        fields.correct.value = 'a'
                        expanded = false
                    },
                    text = { Text("A") },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
                DropdownMenuItem(
                    onClick = {
                        fields.correct.value = 'b'
                        expanded = false
                    },
                    text = { Text("B") },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
                if (fields.choices[2].value.isNotBlank()) {
                    DropdownMenuItem(
                        onClick = {
                            fields.correct.value = 'c'
                            expanded = false
                        },
                        text = { Text("C") },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    )
                }
                if (fields.choices[3].value.isNotBlank()) {
                    DropdownMenuItem(
                        onClick = {
                            fields.correct.value = 'd'
                            expanded = false
                        },
                        text = { Text("D") },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Composable
fun returnReviewError(): List<String> {
    return listOf(
        stringResource(R.string.review_amount_0).toString(),
        stringResource(R.string.review_amount_10).toString(),
        stringResource(R.string.review_amount_same).toString(),
        stringResource(R.string.failed_review).toString()
    )
}

@Composable
fun returnMultiplierError(): List<String> {
    return listOf(
        stringResource(R.string.good_multiplier_1).toString(),
        stringResource(R.string.bad_multiplier_1).toString(),
        stringResource(R.string.multipliers_same).toString(),
        stringResource(R.string.failed_multiplier).toString()
    )
}

@Composable
fun returnDeckError(): List<String> {
    return listOf(
        stringResource(R.string.empty_deck_name).toString(),
        stringResource(R.string.deck_name_exists).toString(),
        stringResource(R.string.deck_name_failed).toString()
    )
}

@Composable
fun returnCardAmountError() : List<String>{
    return listOf(
        "daily card Amount must be at least 5",
        "only 1000 cards a day are allowed",
        "card amount is the same",
        "failed to update card amount")
}

@Composable
fun getSavableFields(fields: Fields): Fields {
    return Fields(
        question = rememberSaveable { mutableStateOf("") },
        middleField = rememberSaveable { mutableStateOf("") },
        answer = rememberSaveable { mutableStateOf("") },
        choices = rememberSaveable { MutableList(4) { mutableStateOf("") } },
        correct = rememberSaveable { mutableStateOf('?') },
        scrollPosition = fields.scrollPosition,
        inDeckClicked = fields.inDeckClicked,
        mainClicked = fields.mainClicked
    )
}

suspend fun delayNavigate() {
    delay(85)
}
