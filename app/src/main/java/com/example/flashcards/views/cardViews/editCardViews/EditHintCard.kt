package com.example.flashcards.views.cardViews.editCardViews

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.flashcards.R
import com.example.flashcards.model.uiModels.Fields
import com.example.flashcards.model.tablesAndApplication.HintCard
import com.example.flashcards.ui.theme.GetModifier
import com.example.flashcards.views.miscFunctions.EditTextField

@Composable
fun EditHintCard(hintCard: HintCard,
                  fields: Fields,
                 getModifiers: GetModifier) {
    fields.question = remember { mutableStateOf(hintCard.question) }
    fields.middleField = remember { mutableStateOf(hintCard.hint) }
    fields.answer = remember { mutableStateOf(hintCard.answer) }

    Text(
        text = stringResource(R.string.edit_flashcard),
        fontSize = 35.sp,
        lineHeight = 40.sp,
        textAlign = TextAlign.Center,
        color = getModifiers.titleColor(),
        modifier = getModifiers.editCardModifier()
    )
    EditTextField(
        value = fields.question.value,
        onValueChanged = { fields.question.value = it },
        labelStr =  stringResource(R.string.question),
        modifier = Modifier.fillMaxWidth()
    )
    EditTextField(
        value = fields.middleField.value,
        onValueChanged = { fields.middleField.value = it },
        labelStr = stringResource(R.string.hint_field),
        modifier = Modifier.fillMaxWidth()
    )
    EditTextField(
        value = fields.answer.value,
        onValueChanged = { fields.answer.value = it },
        labelStr = stringResource(R.string.answer),
        modifier = Modifier.fillMaxWidth()
    )
}