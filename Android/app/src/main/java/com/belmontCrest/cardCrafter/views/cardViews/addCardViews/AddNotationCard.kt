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
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.controller.view.models.cardViewsModels.AddCardViewModel
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.model.ui.states.SelectedKeyboard
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.uiFunctions.katex.menu.KaTeXMenu
import com.belmontCrest.cardCrafter.uiFunctions.LatexKeyboard
import com.belmontCrest.cardCrafter.uiFunctions.buttons.SubmitButton
import com.belmontCrest.cardCrafter.uiFunctions.katex.menu.SelectedAnnotation
import com.belmontCrest.cardCrafter.uiFunctions.katex.menu.getWebView
import com.belmontCrest.cardCrafter.uiFunctions.showToastMessage
import com.belmontCrest.cardCrafter.views.misc.StepListView
import com.belmontCrest.cardCrafter.views.misc.collectNotationFieldsAsStates
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun AddNotationCard(
    vm: AddCardViewModel, deck: Deck, height: Int, width: Int,
    getUIStyle: GetUIStyle, modifier: Modifier
) {
    val errorMessage by vm.errorMessage.collectAsStateWithLifecycle()
    var successMessage by remember { mutableStateOf("") }
    val fillOutFields = stringResource(R.string.fill_out_all_fields)
    val cardAdded = stringResource(R.string.card_added)
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val (fields, showKB, selectedKB) = collectNotationFieldsAsStates(vm)
    var selectedSymbol by rememberSaveable {
        mutableStateOf(KaTeXMenu(null, SelectedAnnotation.Idle))
    }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val context = LocalContext.current
    var enabled by rememberSaveable { mutableStateOf(true) }
    val resetOffset by vm.resetOffset.collectAsStateWithLifecycle()

    val kk = KeyboardInputs.KK
    val webScrollState = rememberScrollState()

    val webView = remember(selectedKB) {
        getWebView(getUIStyle, context) { notation, sa ->
            if (selectedKB is SelectedKeyboard.Question || selectedKB is SelectedKeyboard.Answer ||
                selectedKB is SelectedKeyboard.Step
            ) selectedSymbol = KaTeXMenu(notation, sa)
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
                if (selectedKB is SelectedKeyboard.Question || selectedKB is SelectedKeyboard.Answer ||
                    selectedKB is SelectedKeyboard.Step
                ) selectedSymbol = KaTeXMenu("null", it)
                else showToastMessage(context, "Please select a text field first.")
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .verticalScroll(scrollState)
            .zIndex(-1f)
    ) {
        Text(
            text = stringResource(R.string.question),
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            lineHeight = 30.sp,
            color = getUIStyle.titleColor()
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            LatexKeyboard(
                value = fields.question,
                onValueChanged = { newText -> vm.updateQ(newText) },
                labelStr = stringResource(R.string.question),
                modifier = Modifier.fillMaxWidth(),
                onFocusChanged = {
                    Log.d(kk, "Focused on Question")
                    vm.updateSelectedKB(SelectedKeyboard.Question)
                }, kt = selectedSymbol,
                onIdle = { selectedSymbol = KaTeXMenu(null, SelectedAnnotation.Idle) },
                selectedKB = selectedKB,
                actualKB = SelectedKeyboard.Question
            )
        }
        Text(
            text = "Steps",
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            lineHeight = 30.sp,
            color = getUIStyle.titleColor(),
            modifier = Modifier.padding(top = 8.dp)
        )
        StepListView(
            steps = fields.steps, onAddStep = { vm.addStep() }, selectedKB = selectedKB,
            onIdle = { selectedSymbol = KaTeXMenu(null, SelectedAnnotation.Idle) },
            onRemoveStep = {
                if (fields.steps.isNotEmpty()) {
                    val current = selectedKB
                    if (current is SelectedKeyboard.Step &&
                        current.index == fields.steps.lastIndex
                    ) {
                        vm.resetSelectedKB()
                        Log.d(kk, "Reset since Step #${current.index} lost focus")
                    }
                    vm.removeStep()
                }
            },
            onFocusChanged = { index ->
                vm.updateSelectedKB(SelectedKeyboard.Step(index))
                Log.d(kk, "Focused on Step #${index + 1}")
            },
            onValueChanged = { index, newText -> vm.updateStep(newText, index) },
            getUIStyle = getUIStyle, selectedSymbol = selectedSymbol, enabled = enabled
        )
        Text(
            text = stringResource(R.string.answer),
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            lineHeight = 30.sp,
            color = getUIStyle.titleColor(),
            modifier = Modifier.padding(top = 8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            LatexKeyboard(
                value = fields.answer,
                onValueChanged = { newText ->
                    vm.updateA(newText)
                },
                labelStr = stringResource(R.string.answer),
                modifier = Modifier.fillMaxWidth(),
                onFocusChanged = {
                    vm.updateSelectedKB(SelectedKeyboard.Answer)
                    //lastSelectedKB = SelectedKeyboard.Answer
                    Log.d(kk, "Focused on Answer")
                },
                kt = selectedSymbol,
                onIdle = { selectedSymbol = KaTeXMenu(null, SelectedAnnotation.Idle) },
                selectedKB = selectedKB, actualKB = SelectedKeyboard.Answer
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
        ) {
            SubmitButton(
                onClick = {
                    if (fields.question.isBlank() || fields.answer.isBlank()) {
                        vm.setErrorMessage(fillOutFields)
                        successMessage = ""
                    } else if (fields.steps.isNotEmpty() && fields.steps.all { it.isBlank() }) {
                        vm.setErrorMessage("Steps can't be blank")
                        successMessage = ""
                    } else {
                        coroutineScope.launch {
                            enabled = false
                            vm.addNotationCard(
                                deck, fields.question, fields.steps, fields.answer
                            )
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