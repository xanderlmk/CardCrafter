package com.belmontCrest.cardCrafter.views.cardViews.editCardViews

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.controller.view.models.cardViewsModels.EditCardViewModel
import com.belmontCrest.cardCrafter.model.ui.states.SelectedKeyboard
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.uiFunctions.customParameters.CustomAnswerParamInput
import com.belmontCrest.cardCrafter.uiFunctions.customParameters.CustomMiddleParamInput
import com.belmontCrest.cardCrafter.uiFunctions.customParameters.CustomParamInputs
import com.belmontCrest.cardCrafter.uiFunctions.katex.menu.KaTeXMenu
import com.belmontCrest.cardCrafter.uiFunctions.katex.menu.SelectedAnnotation
import com.belmontCrest.cardCrafter.uiFunctions.showToastMessage
import com.belmontCrest.cardCrafter.views.misc.collectNotationFieldsAsStates

@Composable
fun EditCustomCard(vm: EditCardViewModel, getUIStyle: GetUIStyle, onUpdate: () -> KaTeXMenu) {
    val (fields, _, selectedKB) = collectNotationFieldsAsStates(vm)
    var selectedSymbol by rememberSaveable {
        mutableStateOf(KaTeXMenu(null, SelectedAnnotation.Idle))
    }
    var enabled by rememberSaveable { mutableStateOf(true) }
    val context = LocalContext.current
    LaunchedEffect(onUpdate()) {
        if (selectedKB != null) {
            selectedSymbol = onUpdate()
        } else {
            if (onUpdate().notation != null)
                showToastMessage(context, "Please select a text field first.")
        }
    }

    CustomParamInputs(
        param = fields.customQuestion, getUIStyle = getUIStyle,
        onChangeParam = { vm.updateQ(it) },
        onIdle = { selectedSymbol = it },
        onSelectKeyboard = { vm.updateSelectedKB(it) },
        selectedSymbol = selectedSymbol, isNew = false,
        field = stringResource(R.string.question),
        onResetKB = { vm.resetKBStuff() },
        actualKB = SelectedKeyboard.Question, selectedKB = selectedKB,
    )
    CustomMiddleParamInput(
        param = fields.customMiddle, getUIStyle = getUIStyle,
        onChangeParam = { vm.updateM(it) },
        onIdle = { selectedSymbol = it },
        onSelectKeyboard = { vm.updateSelectedKB(it) },
        onResetKB = { vm.resetKBStuff() },
        selectedSymbol = selectedSymbol, isNew = false,
        field = stringResource(R.string.middle_field), editing = true,
        actualKB = SelectedKeyboard.Middle, selectedKB = selectedKB
    )
    CustomAnswerParamInput(
        param = fields.customAnswer, getUIStyle = getUIStyle,
        onChangeParam = { vm.updateA(it) },
        onIdle = { selectedSymbol = it },
        onSelectKeyboard = { vm.updateSelectedKB(it) },
        onResetKB = { vm.resetKBStuff() },
        selectedSymbol = selectedSymbol, isNew = false,
        field = stringResource(R.string.answer),
        actualKB = SelectedKeyboard.Answer,
        selectedKB = selectedKB, enabled = enabled
    )
}