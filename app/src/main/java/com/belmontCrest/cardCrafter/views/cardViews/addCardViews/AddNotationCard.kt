package com.belmontCrest.cardCrafter.views.cardViews.addCardViews

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels.AddCardViewModel
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.model.ui.states.SelectedKeyboard
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.uiFunctions.katex.menu.KaTeXMenu
import com.belmontCrest.cardCrafter.uiFunctions.LatexKeyboard
import com.belmontCrest.cardCrafter.uiFunctions.buttons.SubmitButton
import com.belmontCrest.cardCrafter.uiFunctions.katex.menu.SelectedAnnotation
import com.belmontCrest.cardCrafter.uiFunctions.katex.menu.getWebView
import com.belmontCrest.cardCrafter.uiFunctions.showToastMessage
import com.belmontCrest.cardCrafter.views.miscFunctions.collectNotationFieldsAsStates
import com.belmontCrest.cardCrafter.views.miscFunctions.collectTextRangesAsStates
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private object KeyboardInputs {
    const val KK = "KatexKeyBoard"
}

@Composable
fun AddNotationCard(
    vm: AddCardViewModel, deck: Deck, height: Int, width: Int,
    getUIStyle: GetUIStyle, modifier: Modifier
) {
    val errorMessage by vm.errorMessage.collectAsStateWithLifecycle()
    var successMessage by remember { mutableStateOf("") }
    val fillOutFields = stringResource(R.string.fill_out_all_fields).toString()
    val cardAdded = stringResource(R.string.card_added).toString()
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val (fields, showKB, selectedKB) = collectNotationFieldsAsStates(vm)
    val (selection, composition) = collectTextRangesAsStates(vm)
    var selectedQSymbol by rememberSaveable {
        mutableStateOf(KaTeXMenu(null, SelectedAnnotation.Idle))
    }
    var selectedASymbol by rememberSaveable {
        mutableStateOf(KaTeXMenu(null, SelectedAnnotation.Idle))
    }
    var selectedSLSymbols = rememberSaveable { mutableListOf<KaTeXMenu>() }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val context = LocalContext.current
    var enabled by rememberSaveable { mutableStateOf(true) }
    val resetOffset by vm.resetOffset.collectAsStateWithLifecycle()

    val kk = KeyboardInputs.KK
    val webScrollState = rememberScrollState()
    val webView = getWebView(getUIStyle) { notation, sa ->
        when (val sel = selectedKB) {
            is SelectedKeyboard.Question -> {
                selectedQSymbol = KaTeXMenu(notation, sa)
            }

            is SelectedKeyboard.Step -> {
                selectedSLSymbols[sel.index] = KaTeXMenu(notation, sa)
            }

            is SelectedKeyboard.Answer -> {
                selectedASymbol = KaTeXMenu(notation, sa)
            }

            else -> {
                showToastMessage(context, "Please select a text field first.")
                /* No Keyboard, Do Nothing */
            }
        }
    }
    LaunchedEffect(Unit) { vm.onCreate() }
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
                modifier.fillMaxSize(), offsetProvider = { offset },
                height = height, width = width,
                onDismiss = { vm.toggleKeyboard() },
                onOffset = { offset += it }, getUIStyle = getUIStyle, //initialPos,
                webView = webView, scrollState = webScrollState
            ) {
                when (val sel = selectedKB) {
                    is SelectedKeyboard.Question -> {
                        selectedQSymbol = KaTeXMenu("null", it)
                    }

                    is SelectedKeyboard.Step -> {
                        selectedSLSymbols[sel.index] = KaTeXMenu("null", it)
                    }

                    is SelectedKeyboard.Answer -> {
                        selectedASymbol = KaTeXMenu("null", it)
                    }

                    else -> {
                        showToastMessage(context, "Please select a text field first.")
                        /* No Keyboard, Do Nothing */
                    }
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .verticalScroll(scrollState)
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
                    onValueChanged = { newText ->
                        vm.updateQ(newText)
                    },
                    labelStr = stringResource(R.string.question),
                    modifier = Modifier
                        .fillMaxWidth(),
                    kt = selectedQSymbol,
                    onFocusChanged = {
                        Log.d(kk, "Focused on Question")
                        vm.updateSelectedKB(SelectedKeyboard.Question)
                    },
                    selection = selection, composition = composition,
                    onUpdateTR = { sel, com -> vm.updateTRs(sel, com) },
                    selectedKeyboard = selectedKB, actualKeyboard = SelectedKeyboard.Question,
                    onIdle = { selectedQSymbol = KaTeXMenu(null, SelectedAnnotation.Idle) },
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (fields.steps.isEmpty()) {
                    Button(
                        onClick = {
                            vm.addStep()
                            selectedSLSymbols.add(KaTeXMenu(null, SelectedAnnotation.Idle))
                        }, modifier = Modifier
                            .padding(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = getUIStyle.secondaryButtonColor(),
                            contentColor = getUIStyle.buttonTextColor()
                        ), enabled = enabled
                    ) {
                        Text(
                            text = "Add a step",
                            textAlign = TextAlign.Center,
                        )
                    }
                } else {
                    fields.steps.forEachIndexed { index, it ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            LatexKeyboard(
                                value = it,
                                onValueChanged = { newText ->
                                    vm.updateStep(newText, index)
                                },
                                labelStr = "Step: ${index + 1}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                onFocusChanged = {
                                    vm.updateSelectedKB(SelectedKeyboard.Step(index))
                                    Log.d(kk, "Focused on Step #${index + 1}")
                                },
                                selection = selection, composition = composition,
                                onUpdateTR = { sel, com -> vm.updateTRs(sel, com) },
                                kt = selectedSLSymbols[index],
                                selectedKeyboard = selectedKB,
                                actualKeyboard = SelectedKeyboard.Step(index),
                                onIdle = {
                                    selectedSLSymbols[index] =
                                        KaTeXMenu(null, SelectedAnnotation.Idle)
                                },
                            )
                        }
                    }

                    Button(
                        onClick = {
                            vm.addStep()
                            selectedSLSymbols.add(KaTeXMenu(null, SelectedAnnotation.Idle))
                            /*val last = lastSelectedKB
                            if (last != null) {

                               vm.updateSelectedKB(last)
                                Log.d("AddCardView", "updated.")
                            }*/
                        }, modifier = Modifier
                            .padding(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = getUIStyle.secondaryButtonColor(),
                            contentColor = getUIStyle.buttonTextColor()
                        ), enabled = enabled
                    ) {
                        Text(
                            text = "Add a step",
                            textAlign = TextAlign.Center,
                        )
                    }

                    Button(
                        onClick = {
                            if (fields.steps.isNotEmpty()) {
                                val currently = selectedKB
                                if (currently is SelectedKeyboard.Step &&
                                    (currently.index - 1) == fields.steps.lastIndex
                                ) {
                                    vm.resetSelectedKB()
                                    Log.d(kk, "Reset since Step #${currently.index} lost focus")
                                }
                                selectedSLSymbols.removeAt(selectedSLSymbols.lastIndex)
                                vm.removeStep()
                            }
                        },
                        modifier = Modifier.padding(top = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = getUIStyle.secondaryButtonColor(),
                            contentColor = getUIStyle.buttonTextColor()
                        )
                    ) {
                        Text("Remove Step")
                    }
                }
            }
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
                    modifier = Modifier
                        .fillMaxWidth(),
                    onFocusChanged = {
                        vm.updateSelectedKB(SelectedKeyboard.Answer)
                        //lastSelectedKB = SelectedKeyboard.Answer
                        Log.d(kk, "Focused on Answer")
                    }, kt = selectedASymbol, selection = selection, composition = composition,
                    onUpdateTR = { sel, com -> vm.updateTRs(sel, com) },
                    selectedKeyboard = selectedKB, actualKeyboard = SelectedKeyboard.Answer,
                    onIdle = { selectedASymbol = KaTeXMenu(null, SelectedAnnotation.Idle) }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                SubmitButton(
                    onClick = {
                        if (
                            fields.question.isBlank() ||
                            fields.answer.isBlank()
                        ) {
                            vm.setErrorMessage(fillOutFields)
                            successMessage = ""
                        } else if (
                            fields.steps.isNotEmpty() &&
                            fields.steps.all { it.isBlank() }
                        ) {
                            vm.setErrorMessage("Steps can't be blank")
                            successMessage = ""
                        } else {
                            coroutineScope.launch {
                                enabled = false
                                vm.addNotationCard(
                                    deck, fields.question,
                                    fields.steps, fields.answer
                                )
                                successMessage = cardAdded
                                enabled = true
                            }
                        }
                    },
                    enabled = enabled, string = stringResource(R.string.submit),
                    modifier = Modifier.padding(top = 4.dp), getUIStyle = getUIStyle
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
}