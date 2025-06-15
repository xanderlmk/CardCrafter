package com.belmontCrest.cardCrafter.views.cardViews.editCardViews

import android.util.Log
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.model.ui.Fields
import com.belmontCrest.cardCrafter.model.ui.SelectedKeyboard
import com.belmontCrest.cardCrafter.navigation.NavViewModel
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.uiFunctions.LatexKeyboard
import com.belmontCrest.cardCrafter.uiFunctions.katex.menu.KaTeXMenu
import com.belmontCrest.cardCrafter.uiFunctions.katex.menu.SelectedAnnotation
import com.belmontCrest.cardCrafter.uiFunctions.showToastMessage

@Composable
fun EditNotationCard(
    fields: Fields, vm: NavViewModel,
    getUIStyle: GetUIStyle, onUpdate: () -> KaTeXMenu
) {
    var steps by rememberSaveable { mutableIntStateOf(fields.stringList.size) }
    val focusRequester = remember { FocusRequester() }
    var selectedQSymbol by rememberSaveable {
        mutableStateOf(KaTeXMenu(null, SelectedAnnotation.Idle))
    }
    var selectedASymbol by rememberSaveable {
        mutableStateOf(KaTeXMenu(null, SelectedAnnotation.Idle))
    }
    var selectedSLSymbols = rememberSaveable {
        MutableList<KaTeXMenu>(fields.stringList.size) {
            KaTeXMenu(null, SelectedAnnotation.Idle)
        }
    }
    val selectedKB by vm.selectedKB.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(onUpdate()) {
        when (val sel = selectedKB) {
            is SelectedKeyboard.Question -> {
                selectedQSymbol = onUpdate()
            }

            is SelectedKeyboard.Step -> {
                selectedSLSymbols[sel.index] = onUpdate()
            }

            is SelectedKeyboard.Answer -> {
                selectedASymbol = onUpdate()
            }

            else -> {
                if (onUpdate().notation != null) {
                    showToastMessage(context, "Please select a text field first.")
                }
                /* No Keyboard, Do Nothing */
            }
        }
    }

    LatexKeyboard(
        value = fields.question.value,
        onValueChanged = { fields.question.value = it },
        labelStr = stringResource(R.string.question),
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
                if (focusState.hasFocus) {
                    vm.updateSelectedKB(SelectedKeyboard.Question)
                    //lastSelectedKB = SelectedKeyboard.Question
                    Log.d("AddCardVM", "Focused on Question")
                }
            }
            .focusable(),
        focusRequester = focusRequester,
        kt = selectedQSymbol,
        onIdle = { selectedQSymbol = KaTeXMenu(null, SelectedAnnotation.Idle) }
    )
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
            ), enabled = true
        ) {
            Text(
                text = "Add a step",
                textAlign = TextAlign.Center,
            )
        }
    } else {
        fields.stringList.forEachIndexed { index, string ->
            val indexedModifier = if (index == 0) {
                Modifier.padding(top = 5.dp, start = 0.5.dp, end = 0.5.dp, bottom = 1.dp)
            } else {
                Modifier.padding(1.dp)
            }
            LatexKeyboard(
                value = string.value,
                onValueChanged = { string.value = it },
                labelStr = "Step: ${index + 1}",
                modifier = indexedModifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        if (focusState.hasFocus) {
                            vm.updateSelectedKB(SelectedKeyboard.Step(index))
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
                }
            )
        }
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
            ), enabled = true
        ) {
            Text(
                text = "Add a step",
                textAlign = TextAlign.Center,
            )
        }
        Button(
            onClick = {
                if (steps > 0) {
                    steps -= 1
                    fields.stringList.removeAt(steps)
                    selectedSLSymbols.removeAt(steps)
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
    LatexKeyboard(
        value = fields.answer.value,
        onValueChanged = { fields.answer.value = it },
        labelStr = stringResource(R.string.answer),
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
                if (focusState.hasFocus) {
                    vm.updateSelectedKB(SelectedKeyboard.Answer)
                    //lastSelectedKB = SelectedKeyboard.Answer
                    Log.d("AddCardVM", "Focused on Answer")
                } else {
                    vm.resetSelectedKB()
                    Log.d("AddCardVM", "Answer lost focus.")
                }
            }
            .focusable(),
        focusRequester = focusRequester,
        kt = selectedASymbol,
        onIdle = { selectedASymbol = KaTeXMenu(null, SelectedAnnotation.Idle) }
    )
}
