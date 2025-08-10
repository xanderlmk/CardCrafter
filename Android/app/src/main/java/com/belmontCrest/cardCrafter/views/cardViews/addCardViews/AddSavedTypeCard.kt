package com.belmontCrest.cardCrafter.views.cardViews.addCardViews

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.controller.view.models.cardViewsModels.AddCardViewModel
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.isBlank
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.saveFiles
import com.belmontCrest.cardCrafter.model.ui.states.SelectedKeyboard
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.uiFunctions.buttons.SubmitButton
import com.belmontCrest.cardCrafter.uiFunctions.customParameters.CustomAnswerParamInput
import com.belmontCrest.cardCrafter.uiFunctions.customParameters.CustomMiddleParamInput
import com.belmontCrest.cardCrafter.uiFunctions.customParameters.CustomParamInputs
import com.belmontCrest.cardCrafter.uiFunctions.katex.menu.KaTeXMenu
import com.belmontCrest.cardCrafter.uiFunctions.katex.menu.SelectedAnnotation
import com.belmontCrest.cardCrafter.uiFunctions.katex.menu.getWebView
import com.belmontCrest.cardCrafter.uiFunctions.showToastMessage
import com.belmontCrest.cardCrafter.views.misc.collectNotationFieldsAsStates
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AddSavedTypeCard(
    vm: AddCardViewModel, deck: Deck,  height: Int, width: Int,
    getUIStyle: GetUIStyle, modifier: Modifier
) {
    val errorMessage by vm.errorMessage.collectAsStateWithLifecycle()
    val (fields, showKB, selectedKB) = collectNotationFieldsAsStates(vm)
    val type by vm.type.collectAsStateWithLifecycle()
    var successMessage by remember { mutableStateOf("") }
    val fillOutFields = stringResource(R.string.fill_out_all_fields)
    val cardAdded = stringResource(R.string.card_added)
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    var selectedSymbol by rememberSaveable {
        mutableStateOf(KaTeXMenu(null, SelectedAnnotation.Idle))
    }
    var enabled by rememberSaveable { mutableStateOf(true) }
    val context = LocalContext.current
    val resetOffset by vm.resetOffset.collectAsStateWithLifecycle()
    val webScrollState = rememberScrollState()
    var offset by remember { mutableStateOf(Offset.Zero) }

    val webView = remember(selectedKB) {
        getWebView(getUIStyle, context) { notation, sa ->
            if (selectedKB != null) selectedSymbol = KaTeXMenu(notation, sa)
            else showToastMessage(context, "Please select a text field first.")
        }
    }
    DisposableEffect(webView) {
        onDispose {
            try {
                webView.destroy()
            } catch (e: Exception) {
                Log.w("KatexMenu", "Failed to destroy WebView: $e")
            }
        }
    }
    LaunchedEffect(resetOffset) {
        if (resetOffset) {
            offset = Offset.Zero
            vm.resetDone()
        }
    }
    Box {
        if (showKB) {
            KaTeXMenu(
                modifier,
                offsetProvider = { offset },
                height = height,
                width = width,
                onDismiss = { vm.toggleKeyboardIcon() },
                onOffset = { offset += it },
                getUIStyle = getUIStyle,
                webView = webView,
                scrollState = webScrollState
            ) {
                if (selectedKB != null) selectedSymbol = KaTeXMenu("null", it)
                else showToastMessage(context, "Please select a text field first.")
            }
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.verticalScroll(scrollState)
    ) {
        Text(
            text = stringResource(R.string.question),
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            lineHeight = 30.sp,
            color = getUIStyle.titleColor(),
            modifier = Modifier.fillMaxWidth()
        )
        CustomParamInputs(
            param = fields.customQuestion, getUIStyle = getUIStyle,
            onChangeParam = { vm.updateQ(it) },
            onIdle = { selectedSymbol = it },
            onSelectKeyboard = { vm.updateSelectedKB(it) },
            onResetKB = { vm.resetSelectedKB() },
            selectedSymbol = selectedSymbol, isNew = false,
            field = stringResource(R.string.question),
            actualKB = SelectedKeyboard.Question, selectedKB = selectedKB,
        )
        Text(
            text = stringResource(R.string.middle_field),
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            lineHeight = 30.sp,
            color = getUIStyle.titleColor(),
            modifier = Modifier.fillMaxWidth()
        )
        CustomMiddleParamInput(
            param = fields.customMiddle, getUIStyle = getUIStyle,
            onChangeParam = { vm.updateM(it) },
            onIdle = { selectedSymbol = it },
            onSelectKeyboard = { vm.updateSelectedKB(it) },
            onResetKB = { vm.resetSelectedKB() },
            selectedSymbol = selectedSymbol, isNew = false,
            field = stringResource(R.string.middle_field), editing = false,
            actualKB = SelectedKeyboard.Middle, selectedKB = selectedKB
        )
        Text(
            text = stringResource(R.string.answer),
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            lineHeight = 30.sp,
            color = getUIStyle.titleColor(),
            modifier = Modifier.fillMaxWidth()
        )
        CustomAnswerParamInput(
            param = fields.customAnswer, getUIStyle = getUIStyle,
            onChangeParam = { vm.updateA(it) },
            onIdle = { selectedSymbol = it },
            onSelectKeyboard = { vm.updateSelectedKB(it) },
            onResetKB = { vm.resetSelectedKB() },
            selectedSymbol = selectedSymbol, isNew = false,
            field = stringResource(R.string.answer),
            actualKB = SelectedKeyboard.Answer,
            selectedKB = selectedKB, enabled = enabled
        )

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
        ) {
            SubmitButton(
                onClick = {
                    if (fields.customQuestion.isBlank() ||
                        fields.customMiddle.isBlank() ||
                        fields.customAnswer.isBlank() ||
                        type.isBlank()
                    ) {
                        vm.setErrorMessage(fillOutFields); successMessage = ""
                    } else {
                        coroutineScope.launch {
                            enabled = false
                            val (qFilesSuccess, newQ) = fields.customQuestion.saveFiles(context)
                            val (mFilesSuccess, newM) = fields.customMiddle.saveFiles(context)
                            val (aFilesSuccess, newA) = fields.customAnswer.saveFiles(context)
                            if (!qFilesSuccess || !mFilesSuccess || !aFilesSuccess) {
                                showToastMessage(context, "Failed to upload files.")
                                return@launch
                            }
                            vm.addSavedTypeCard(deck, newQ, newM, newA, type)
                            successMessage = cardAdded
                            enabled = true
                        }
                    }
                },
                enabled = enabled,
                string = stringResource(R.string.submit),
                modifier = Modifier.padding(top = 4.dp),
                getUIStyle = getUIStyle
            )
        }
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier.padding(4.dp),
                fontSize = 16.sp
            )
        }
        if (successMessage.isNotEmpty()) {
            Text(
                text = successMessage,
                color = getUIStyle.titleColor(),
                modifier = Modifier.padding(4.dp),
                fontSize = 16.sp
            )
        }
        LaunchedEffect(successMessage) {
            delay(1500)
            successMessage = ""
            coroutineScope.launch {
                scrollState.animateScrollTo(0)
            }
        }
        LaunchedEffect(errorMessage) {
            delay(1500)
            vm.clearErrorMessage()
        }
    }
}