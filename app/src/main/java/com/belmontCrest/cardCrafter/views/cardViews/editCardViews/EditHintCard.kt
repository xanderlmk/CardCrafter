package com.belmontCrest.cardCrafter.views.cardViews.editCardViews

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.model.uiModels.Fields
import com.belmontCrest.cardCrafter.uiFunctions.EditTextFieldNonDone

@Composable
fun EditHintCard(
    fields: Fields
) {
    EditTextFieldNonDone(
        value = fields.question.value,
        onValueChanged = { fields.question.value = it },
        labelStr = stringResource(R.string.question),
        modifier = Modifier.fillMaxWidth()
    )
    EditTextFieldNonDone(
        value = fields.middleField.value,
        onValueChanged = { fields.middleField.value = it },
        labelStr = stringResource(R.string.hint_field),
        modifier = Modifier.fillMaxWidth()
    )
    EditTextFieldNonDone(
        value = fields.answer.value,
        onValueChanged = { fields.answer.value = it },
        labelStr = stringResource(R.string.answer),
        modifier = Modifier.fillMaxWidth()
    )
}