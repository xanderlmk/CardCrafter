package com.example.flashcards.views.cardViews.editCardViews

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.flashcards.R
import com.example.flashcards.model.uiModels.Fields
import com.example.flashcards.model.tablesAndApplication.HintCard
import com.example.flashcards.views.miscFunctions.EditTextField
import com.example.flashcards.views.miscFunctions.createThreeOrHintCardDetails

@Composable
fun EditHintCard(
    hintCard: HintCard,
    fields: Fields
) {
    val cardDetails by remember {
        mutableStateOf(
                createThreeOrHintCardDetails(
                    hintCard.question, hintCard.hint, hintCard.answer
                )
        )
    }

    fields.question = rememberSaveable { mutableStateOf(cardDetails.question.value) }
    fields.middleField = rememberSaveable { mutableStateOf(cardDetails.middleField.value) }
    fields.answer = rememberSaveable { mutableStateOf(cardDetails.answer.value) }


    EditTextField(
        value = fields.question.value,
        onValueChanged = { fields.question.value = it },
        labelStr = stringResource(R.string.question),
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