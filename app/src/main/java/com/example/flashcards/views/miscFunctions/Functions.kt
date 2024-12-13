package com.example.flashcards.views.miscFunctions

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.example.flashcards.R
import com.example.flashcards.model.tablesAndApplication.BasicCard
import com.example.flashcards.model.tablesAndApplication.HintCard
import com.example.flashcards.model.tablesAndApplication.ThreeFieldCard
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
fun LoadingText() {
    Text(
        stringResource(R.string.loading),
        fontSize = 35.sp,
        textAlign = TextAlign.Center,
        color = textColor,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.fillMaxWidth()
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

@Composable
fun BasicCardQuestion(basicCard: BasicCard) {
    Text(text = stringResource(R.string.question) + ": ${basicCard.question}")
}
@Composable
fun ThreeCardQuestion(threeFieldCard: ThreeFieldCard) {
    Text(text = stringResource(R.string.question) + ": ${threeFieldCard.question}")
}
@Composable
fun HintCardQuestion(hintCard: HintCard) {
    Text(text = stringResource(R.string.question) + ": ${hintCard.question}")
}

suspend fun loading() : Boolean{
    delay(60)
    return false
}

suspend fun delayNavigate() {
    delay(75)
}
