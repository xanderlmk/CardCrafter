package com.belmontCrest.cardCrafter.views.cardViews.editCardViews

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.controller.view.models.cardViewsModels.EditCardViewModel
import com.belmontCrest.cardCrafter.localDatabase.tables.PartOfQorA
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.uiFunctions.EditTextFieldNonDone
import com.belmontCrest.cardCrafter.uiFunctions.IsPartOfQOrA

@Composable
fun EditThreeCard(vm: EditCardViewModel, getUIStyle: GetUIStyle) {
    val fields by vm.fields.collectAsStateWithLifecycle()
    EditTextFieldNonDone(
        value = fields.question,
        onValueChanged = { vm.updateQ(it) },
        labelStr = stringResource(R.string.question),
        modifier = Modifier.fillMaxWidth()
    )
    IsPartOfQOrA(getUIStyle, fields.isQOrA is PartOfQorA.Q) {
        vm.updateQA(if (fields.isQOrA is PartOfQorA.Q) PartOfQorA.A else PartOfQorA.Q)
    }
    EditTextFieldNonDone(
        value = fields.middle,
        onValueChanged = { vm.updateM(it) },
        labelStr = stringResource(R.string.middle_field),
        modifier = Modifier.fillMaxWidth()
    )
    EditTextFieldNonDone(
        value = fields.answer,
        onValueChanged = { vm.updateA(it) },
        labelStr = stringResource(R.string.answer),
        modifier = Modifier.fillMaxWidth()
    )
}

