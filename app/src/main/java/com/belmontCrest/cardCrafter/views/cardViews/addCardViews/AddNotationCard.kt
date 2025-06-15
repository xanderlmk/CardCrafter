package com.belmontCrest.cardCrafter.views.cardViews.addCardViews

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.focusable
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
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
import com.belmontCrest.cardCrafter.model.ui.Fields
import com.belmontCrest.cardCrafter.model.ui.SelectedKeyboard
import com.belmontCrest.cardCrafter.navigation.NavViewModel
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.uiFunctions.katex.menu.KaTeXMenu
import com.belmontCrest.cardCrafter.uiFunctions.LatexKeyboard
import com.belmontCrest.cardCrafter.uiFunctions.buttons.SubmitButton
import com.belmontCrest.cardCrafter.uiFunctions.katex.menu.SelectedAnnotation
import com.belmontCrest.cardCrafter.uiFunctions.katex.menu.getWebView
import com.belmontCrest.cardCrafter.uiFunctions.showToastMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun AddNotationCard(
    vm: AddCardViewModel, deck: Deck, navVM: NavViewModel,
    fields: Fields, getUIStyle: GetUIStyle, modifier: Modifier
) {
    val errorMessage by vm.errorMessage.collectAsStateWithLifecycle()
    var successMessage by remember { mutableStateOf("") }
    val fillOutFields = stringResource(R.string.fill_out_all_fields).toString()
    val cardAdded = stringResource(R.string.card_added).toString()
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    var steps by rememberSaveable { mutableIntStateOf(0) }
    fields.stringList = rememberSaveable { mutableListOf() }
    val showKB by navVM.showKatexKeyboard.collectAsStateWithLifecycle()
    val selectedKB by navVM.selectedKB.collectAsStateWithLifecycle()
    //var lastSelectedKB by rememberSaveable { mutableStateOf<SelectedKeyboard?>(null) }
    // val focusQRequester = remember { FocusRequester() }
    // val focusARequester = remember { FocusRequester() }
    val focusRequester = remember { FocusRequester() }
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
    val resetOffset by navVM.resetOffset.collectAsStateWithLifecycle()
    // var initialPos by remember { mutableStateOf<Offset?>(null) }
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
            navVM.resetDone()
        }
    }
    Box {
        if (showKB) {
            BackHandler {
                navVM.toggleKeyboard()
                navVM.resetOffset()
            }
            KaTeXMenu(
                modifier
                    .fillMaxSize(),
                /**.onGloballyPositioned { coordinates ->
                initialPos = coordinates.localToWindow(Offset.Zero)

                }*/
                { offset },
                onDismiss = { navVM.toggleKeyboard() },
                onOffset = { offset += it }, getUIStyle, //initialPos,
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
                color = getUIStyle.titleColor(),
                modifier = Modifier
                    .padding(top = 15.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                LatexKeyboard(
                    value = fields.question.value,
                    onValueChanged = { newText ->
                        fields.question.value =
                            newText
                    },
                    labelStr = stringResource(R.string.question),
                    modifier = Modifier
                        .fillMaxWidth()
                        //.focusRequester(focusQRequester)
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState ->
                            if (focusState.hasFocus) {
                                navVM.updateSelectedKB(SelectedKeyboard.Question)
                                //lastSelectedKB = SelectedKeyboard.Question
                                Log.d("AddCardVM", "Focused on Question")
                            }
                        }
                        .focusable(),
                    //focusRequester = focusQRequester,
                    focusRequester = focusRequester,
                    kt = selectedQSymbol,
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
                if (steps == 0) {
                    Button(
                        onClick = {
                            fields.stringList.add(mutableStateOf(""))
                            selectedSLSymbols.add(KaTeXMenu(null, SelectedAnnotation.Idle))
                            steps += 1
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
                    fields.stringList.forEachIndexed { index, it ->
                        //val focusRequester = remember { FocusRequester() }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            LatexKeyboard(
                                value = it.value,
                                onValueChanged = { newText ->
                                    it.value = newText
                                },
                                labelStr = "Step: ${index + 1}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(1.dp)
                                    .focusRequester(focusRequester)
                                    .onFocusChanged { focusState ->
                                        if (focusState.hasFocus) {
                                            navVM.updateSelectedKB(SelectedKeyboard.Step(index))
                                            //lastSelectedKB = SelectedKeyboard.Step(index)
                                            Log.d("AddCardVM", "Focused on Step #${index + 1}")
                                        }
                                    }
                                    .focusable(),
                                focusRequester = focusRequester,
                                kt = selectedSLSymbols[index],
                                onIdle = {
                                    selectedSLSymbols[index] =
                                        KaTeXMenu(null, SelectedAnnotation.Idle)
                                },
                            )
                        }
                    }

                    Button(
                        onClick = {
                            fields.stringList.add(mutableStateOf(""))
                            selectedSLSymbols.add(KaTeXMenu(null, SelectedAnnotation.Idle))
                            steps += 1
                            /*val last = lastSelectedKB
                            if (last != null) {

                               navVM.updateSelectedKB(last)
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
                            if (steps > 0) {
                                val currently = selectedKB
                                if (currently is SelectedKeyboard.Step &&
                                    (currently.index - 1) == fields.stringList.lastIndex
                                ) {
                                    navVM.resetSelectedKB()
                                    Log.d(
                                        "AddCardVM",
                                        "Reset since Step #${currently.index} lost focus"
                                    )
                                }
                                steps -= 1
                                selectedSLSymbols.removeAt(steps)
                                fields.stringList.removeAt(steps)
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
                    value = fields.answer.value,
                    onValueChanged = { newText ->
                        fields.answer.value =
                            newText
                    },
                    labelStr = stringResource(R.string.answer),
                    modifier = Modifier
                        .weight(1f)
                        //.focusRequester(focusARequester)
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState ->
                            if (focusState.hasFocus) {
                                navVM.updateSelectedKB(SelectedKeyboard.Answer)
                                //lastSelectedKB = SelectedKeyboard.Answer
                                Log.d("AddCardVM", "Focused on Answer")
                            } else {
                                navVM.resetSelectedKB()
                                Log.d("AddCardVM", "Answer lost focus.")
                            }
                        }
                        .focusable(),
                    //focusRequester = focusARequester,
                    focusRequester = focusRequester,
                    kt = selectedASymbol,
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
                            fields.question.value.isBlank() ||
                            fields.answer.value.isBlank()
                        ) {
                            vm.setErrorMessage(fillOutFields)
                            successMessage = ""
                        } else if (
                            fields.stringList.isNotEmpty() &&
                            fields.stringList.all { it.value.isBlank() }
                        ) {
                            vm.setErrorMessage("Steps can't be blank")
                            successMessage = ""
                        } else {
                            coroutineScope.launch {
                                enabled = false
                                vm.addNotationCard(
                                    deck, fields.question.value,
                                    fields.stringList.map { it.value }, fields.answer.value
                                )
                                fields.question.value = ""
                                fields.stringList.clear()
                                steps = 0
                                fields.answer.value = ""
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