package com.belmontCrest.cardCrafter.views.cardViews.editCardViews

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.localDatabase.tables.PartOfQorA
import com.belmontCrest.cardCrafter.model.ui.Fields
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.uiFunctions.EditTextFieldNonDone
import com.belmontCrest.cardCrafter.uiFunctions.IsPartOfQOrA

@Composable
fun EditThreeCard(
    fields: Fields, getUIStyle: GetUIStyle
) {
    EditTextFieldNonDone(
        value = fields.question.value,
        onValueChanged = { fields.question.value = it },
        labelStr = stringResource(R.string.question),
        modifier = Modifier.fillMaxWidth()
    )
    IsPartOfQOrA(getUIStyle, fields.isQOrA.value is PartOfQorA.Q) {
        fields.isQOrA.value =
            if (fields.isQOrA.value is PartOfQorA.Q) PartOfQorA.A else PartOfQorA.Q
    }
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

