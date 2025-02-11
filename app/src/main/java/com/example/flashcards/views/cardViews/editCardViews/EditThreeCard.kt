package com.example.flashcards.views.cardViews.editCardViews

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.flashcards.R
import com.example.flashcards.model.uiModels.Fields
import com.example.flashcards.model.tablesAndApplication.ThreeFieldCard
import com.example.flashcards.views.miscFunctions.EditTextFieldNonDone

@Composable
fun EditThreeCard(
    threeCard: ThreeFieldCard,
    fields: Fields
) {
    fields.question = rememberSaveable { mutableStateOf(threeCard.question) }
    fields.middleField = rememberSaveable { mutableStateOf(threeCard.middle) }
    fields.answer = rememberSaveable { mutableStateOf(threeCard.answer) }

    EditTextFieldNonDone(
        value = fields.question.value,
        onValueChanged = { fields.question.value = it },
        labelStr = stringResource(R.string.question),
        modifier = Modifier.fillMaxWidth()
    )
    EditTextFieldNonDone(
        value = fields.middleField.value,
        onValueChanged = { fields.middleField.value = it },
        labelStr = stringResource(R.string.middle_field),
        modifier = Modifier.fillMaxWidth()
    )
    EditTextFieldNonDone(
        value = fields.answer.value,
        onValueChanged = { fields.answer.value = it },
        labelStr = stringResource(R.string.answer),
        modifier = Modifier.fillMaxWidth()
    )
}

