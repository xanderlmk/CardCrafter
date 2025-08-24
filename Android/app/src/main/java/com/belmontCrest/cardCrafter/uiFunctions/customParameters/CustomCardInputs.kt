package com.belmontCrest.cardCrafter.uiFunctions.customParameters

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.AnswerParam
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.MiddleParam
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.Param
import com.belmontCrest.cardCrafter.model.ui.states.SelectedKeyboard
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.uiFunctions.CustomText
import com.belmontCrest.cardCrafter.uiFunctions.EditTextField
import com.belmontCrest.cardCrafter.uiFunctions.EditTextFieldNonDone
import com.belmontCrest.cardCrafter.uiFunctions.LatexKeyboard
import com.belmontCrest.cardCrafter.uiFunctions.buttons.SubmitButton
import com.belmontCrest.cardCrafter.uiFunctions.katex.menu.KaTeXMenu
import com.belmontCrest.cardCrafter.uiFunctions.katex.menu.SelectedAnnotation
import com.belmontCrest.cardCrafter.views.cardViews.cardDeckViews.cardTypeView.customCardParams.AudioPlayerButton
import com.belmontCrest.cardCrafter.views.misc.AudioPicker
import com.belmontCrest.cardCrafter.views.misc.ImagePicker
import com.belmontCrest.cardCrafter.views.misc.PickAnswerChar
import com.belmontCrest.cardCrafter.views.misc.StepListView
import com.belmontCrest.cardCrafter.views.misc.toBitmap

@Composable
fun CustomMiddleParamInput(
    param: MiddleParam, getUIStyle: GetUIStyle, onChangeParam: (MiddleParam) -> Unit,
    onIdle: (KaTeXMenu) -> Unit, onSelectKeyboard: (SelectedKeyboard) -> Unit,
    selectedSymbol: KaTeXMenu, field: String, actualKB: SelectedKeyboard,
    onResetKB: () -> Unit, selectedKB: SelectedKeyboard?, isNew: Boolean, editing: Boolean
) {
    when (val p = param) {
        is MiddleParam.Choice -> {
            p.choices.forEachIndexed { index, string ->
                EditTextField(
                    value = string,
                    onValueChanged = { newText ->
                        val new = p.choices.toMutableList()
                        new[index] = newText
                        onChangeParam(MiddleParam.Choice(new, p.correct))
                    },
                    labelStr = "Choice: $index",
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                        .fillMaxWidth(),
                    inputColor =
                        if (p.correct == index.toString()[0]) getUIStyle.onCorrectChoice()
                        else Color.Transparent

                )
            }
            SubmitButton(
                onClick = {
                    val new = p.choices.toMutableList()
                    new.add("")
                    onChangeParam(MiddleParam.Choice(new, p.correct))
                }, enabled = true, getUIStyle = getUIStyle, string = "Add choice"
            )
            if (p.choices.size > 2) {
                SubmitButton(onClick = {
                    val new = p.choices.toMutableList()
                    val correct =
                        if (p.correct == p.choices.lastIndex.toString()[0])
                            Int.MIN_VALUE.toString()[0]
                        else p.correct
                    new.removeAt(p.choices.lastIndex)
                    onChangeParam(MiddleParam.Choice(new, correct))
                }, enabled = true, getUIStyle = getUIStyle, string = "Remove choice")
            }
            PickAnswerChar(p, getUIStyle) { onChangeParam(MiddleParam.Choice(p.choices, it)) }
        }

        MiddleParam.Empty -> {
            if (editing) {
                Spacer(Modifier.padding(vertical = 4.dp))
            } else {
                Text(
                    text = "Empty",
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp,
                    color = getUIStyle.titleColor(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        is MiddleParam.Hint -> {
            EditTextFieldNonDone(
                value = p.h,
                onValueChanged = { onChangeParam(MiddleParam.Hint(it)) },
                labelStr = stringResource(R.string.hint),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )
        }

        is MiddleParam.WithParam -> {
            CustomParamInputs(
                param = p.param, getUIStyle = getUIStyle, onChangeParam = {
                    onChangeParam(MiddleParam.WithParam(it))
                }, onIdle = onIdle, onSelectKeyboard = onSelectKeyboard,
                selectedSymbol = selectedSymbol, field = field, actualKB = actualKB,
                selectedKB = selectedKB, isNew = isNew, onResetKB = onResetKB
            )
        }
    }
}

@Composable
fun CustomAnswerParamInput(
    param: AnswerParam, getUIStyle: GetUIStyle, onChangeParam: (AnswerParam) -> Unit,
    onIdle: (KaTeXMenu) -> Unit, onSelectKeyboard: (SelectedKeyboard) -> Unit,
    selectedSymbol: KaTeXMenu, field: String, actualKB: SelectedKeyboard,
    onResetKB: () -> Unit, selectedKB: SelectedKeyboard?, enabled: Boolean, isNew: Boolean
) {
    when (val p = param) {
        is AnswerParam.NotationList -> {
            val steps = rememberSaveable { mutableListOf("") }
            StepListView(
                steps = steps, isCustomCard = true,
                onAddStep = {
                    steps.add("")
                    onChangeParam(AnswerParam.NotationList(steps.toList(), p.a))
                },
                onRemoveStep = {
                    if (steps.isNotEmpty()) {
                        val current = selectedKB
                        if (current is SelectedKeyboard.Step && current.index == p.steps.lastIndex) {
                            onResetKB()
                        }
                        steps.removeAt(steps.lastIndex)
                        onChangeParam(AnswerParam.NotationList(steps.toList(), p.a))
                    }
                },
                onIdle = { onIdle(KaTeXMenu(null, SelectedAnnotation.Idle)) },
                onValueChanged = { idx, s ->
                    val newSteps = steps.mapIndexed { index, string ->
                        val value = if (index == idx) s else string
                        value
                    }
                    if (idx in 0..steps.lastIndex) steps[idx] = s
                    onChangeParam(AnswerParam.NotationList(newSteps, p.a))
                }, onLostFocus = onResetKB,
                onFocusChanged = { index -> onSelectKeyboard(SelectedKeyboard.Step(index)) },
                enabled = enabled, getUIStyle = getUIStyle,
                selectedSymbol = selectedSymbol, selectedKB = selectedKB
            )
            Spacer(Modifier.padding(vertical = 4.dp))
            LatexKeyboard(
                value = p.a, labelStr = stringResource(R.string.answer),
                onValueChanged = { onChangeParam(AnswerParam.NotationList(steps.toList(), it)) },
                onIdle = { onIdle(KaTeXMenu(null, SelectedAnnotation.Idle)) },
                modifier = Modifier.fillMaxWidth(),
                onFocusChanged = { onSelectKeyboard(SelectedKeyboard.Answer) },
                onLostFocus = onResetKB, selectedKB = selectedKB,
                actualKB = SelectedKeyboard.Answer, kt = selectedSymbol
            )
        }

        is AnswerParam.WithParam -> {
            CustomParamInputs(
                param = p.param, getUIStyle = getUIStyle, onChangeParam = {
                    onChangeParam(AnswerParam.WithParam(it))
                }, onIdle = onIdle, onSelectKeyboard = onSelectKeyboard,
                selectedSymbol = selectedSymbol, field = field, actualKB = actualKB,
                selectedKB = selectedKB, isNew = isNew, onResetKB = onResetKB
            )
        }
    }
}

@Composable
fun CustomParamInputs(
    param: Param, getUIStyle: GetUIStyle, onChangeParam: (Param) -> Unit,
    onIdle: (KaTeXMenu) -> Unit, onSelectKeyboard: (SelectedKeyboard) -> Unit,
    selectedSymbol: KaTeXMenu, field: String, actualKB: SelectedKeyboard,
    onResetKB: () -> Unit, selectedKB: SelectedKeyboard?, isNew: Boolean,
) {
    val context = LocalContext.current
    when (val p = param) {
        is Param.Pair -> {
            val firstKB = when (actualKB) {
                SelectedKeyboard.Question -> SelectedKeyboard.PairOfQuestion.First
                SelectedKeyboard.Answer -> SelectedKeyboard.PairOfAnswer.First
                else -> actualKB
            }

            val secondKB = when (actualKB) {
                SelectedKeyboard.Question -> SelectedKeyboard.PairOfQuestion.Second
                SelectedKeyboard.Answer -> SelectedKeyboard.PairOfAnswer.Second
                else -> actualKB
            }
            val expanded = rememberSaveable { MutableList(2) { false } }
            if (isNew) {
                PairedParamChooser(
                    expanded = expanded[0], onExpanded = { expanded[0] = it },
                    string = "First",
                    onClick = { onChangeParam(Param.Pair(it, p.second)) }, getUIStyle = getUIStyle
                )
            } else {
                Text(
                    text = "First",
                    fontSize = 23.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 26.sp,
                    color = getUIStyle.titleColor(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            CustomParamType(
                type = p.first, getUIStyle = getUIStyle,
                onChangeParam = { onChangeParam(Param.Pair(it, p.second)) },
                onIdle = { onIdle(it) }, onSelectKeyboard = { onSelectKeyboard(it) },
                selectedSymbol = selectedSymbol, field = field, actualKB = firstKB,
                selectedKB = selectedKB
            )
            if (isNew) {
                PairedParamChooser(
                    expanded = expanded[1], onExpanded = { expanded[1] = it },
                    string = "Second",
                    onClick = { onChangeParam(Param.Pair(p.first, it)) }, getUIStyle = getUIStyle
                )
            } else {
                Text(
                    text = "Second",
                    fontSize = 23.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 26.sp,
                    color = getUIStyle.titleColor(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            CustomParamType(
                type = p.second, getUIStyle = getUIStyle,
                onChangeParam = { onChangeParam(Param.Pair(p.first, it)) },
                onIdle = { onIdle(it) }, onSelectKeyboard = { onSelectKeyboard(it) },
                selectedSymbol = selectedSymbol, field = field, actualKB = secondKB,
                selectedKB = selectedKB
            )
        }

        is Param.Type.Audio -> {
            if (p.uri.isNotBlank()) {
                CustomText(p.uri, getUIStyle, Modifier.fillMaxWidth())
                AudioPlayerButton(p.uri, getUIStyle)
            } else {
                Text(
                    text = "Please select an audio file",
                    fontSize = 25.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 30.sp,
                    color = getUIStyle.titleColor(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, getUIStyle.themedColor(), RoundedCornerShape(4.dp))
                )
            }
            AudioPicker(getUIStyle) {
                onChangeParam(Param.Type.Audio(it.toString()))
            }
        }

        is Param.Type.Image -> {
            var image by remember {
                mutableStateOf(
                    if (p.uri.isBlank()) null
                    else p.uri.toUri().toBitmap(context)?.asImageBitmap()
                )
            }
            if (p.uri.isNotBlank()) {
                image?.let {
                    Image(
                        it, null, Modifier.fillMaxWidth()
                    )
                }

            } else {
                Text(
                    text = "Please select an image",
                    fontSize = 25.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 30.sp,
                    color = getUIStyle.titleColor(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, getUIStyle.themedColor(), RoundedCornerShape(4.dp))
                )
            }

            ImagePicker(getUIStyle) {
                image = it.toBitmap(context)?.asImageBitmap()
                onChangeParam(Param.Type.Image(it.toString()))
            }

        }

        is Param.Type.Notation -> {
            LatexKeyboard(
                value = p.s,
                onValueChanged = { newText ->
                    onChangeParam(Param.Type.Notation(newText))
                },
                labelStr = field,
                modifier = Modifier
                    .fillMaxWidth(),
                onFocusChanged = { onSelectKeyboard(actualKB); Log.d("CustomParams", "$actualKB") },
                onLostFocus = onResetKB,
                kt = selectedSymbol,
                onIdle = { onIdle(KaTeXMenu(null, SelectedAnnotation.Idle)) },
                selectedKB = selectedKB, actualKB = actualKB
            )
        }

        is Param.Type.String -> {
            EditTextFieldNonDone(
                value = p.s,
                onValueChanged = { newText ->
                    onChangeParam(Param.Type.String(newText))
                },
                labelStr = field,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp)
            )
        }

        is Param.Type.StringList -> {
            p.list.forEachIndexed { index, string ->
                EditTextFieldNonDone(
                    value = string,
                    onValueChanged = { newText ->
                        val new = p.list.toMutableList()
                        new[index] = newText
                        onChangeParam(Param.Type.StringList(new))
                    },
                    labelStr = "Field: ${index + 1}",
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                        .fillMaxWidth()
                )
            }
            SubmitButton(
                onClick = {
                    val new = p.list.toMutableList()
                    new.add("")
                    onChangeParam(Param.Type.StringList(new))
                }, enabled = true, getUIStyle = getUIStyle, string = "Add field"
            )
            if (p.list.size > 1) {
                SubmitButton(onClick = {
                    val new = p.list.toMutableList()
                    new.removeAt(p.list.lastIndex)
                    onChangeParam(Param.Type.StringList(new))
                }, enabled = true, getUIStyle = getUIStyle, string = "Remove field")
            }
        }
    }
}

@Composable
fun CustomParamType(
    type: Param.Type, getUIStyle: GetUIStyle, onChangeParam: (Param.Type) -> Unit,
    onIdle: (KaTeXMenu) -> Unit, onSelectKeyboard: (SelectedKeyboard) -> Unit,
    selectedSymbol: KaTeXMenu, field: String, actualKB: SelectedKeyboard,
    selectedKB: SelectedKeyboard?
) {
    val context = LocalContext.current
    when (val p = type) {
        is Param.Type.Audio -> {
            if (p.uri.isNotBlank()) {
                CustomText(p.uri, getUIStyle, Modifier.fillMaxWidth())
                AudioPlayerButton(p.uri, getUIStyle)
            } else {
                Text(
                    text = "Please select an audio file",
                    fontSize = 25.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 30.sp,
                    color = getUIStyle.titleColor(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, getUIStyle.themedColor(), RoundedCornerShape(4.dp))
                )
            }
            AudioPicker(getUIStyle) {
                onChangeParam(Param.Type.Audio(it.toString()))
            }
        }

        is Param.Type.Image -> {
            var image by remember {
                mutableStateOf(
                    if (p.uri.isBlank()) null
                    else p.uri.toUri().toBitmap(context)?.asImageBitmap()
                )
            }
            if (p.uri.isNotBlank()) {
                image?.let {
                    Image(
                        it, null, Modifier.fillMaxWidth()
                    )
                }

            } else {
                Text(
                    text = "Please select an image",
                    fontSize = 25.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 30.sp,
                    color = getUIStyle.titleColor(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, getUIStyle.themedColor(), RoundedCornerShape(4.dp))
                )
            }

            ImagePicker(getUIStyle) {
                image = it.toBitmap(context)?.asImageBitmap()
                onChangeParam(Param.Type.Image(it.toString()))
            }

        }

        is Param.Type.Notation -> {
            LatexKeyboard(
                value = p.s,
                onValueChanged = { newText ->
                    onChangeParam(Param.Type.Notation(newText))
                },
                labelStr = field,
                modifier = Modifier
                    .fillMaxWidth(),
                onFocusChanged = { onSelectKeyboard(actualKB) },
                kt = selectedSymbol, selectedKB = selectedKB, actualKB = actualKB,
                onIdle = { onIdle(KaTeXMenu(null, SelectedAnnotation.Idle)) }
            )
        }

        is Param.Type.String -> {
            EditTextFieldNonDone(
                value = p.s,
                onValueChanged = { newText ->
                    onChangeParam(Param.Type.String(newText))
                },
                labelStr = field,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp)
            )
        }

        is Param.Type.StringList -> {
            p.list.forEachIndexed { index, string ->
                EditTextFieldNonDone(
                    value = string,
                    onValueChanged = { newText ->
                        val new = p.list.toMutableList()
                        new[index] = newText
                        onChangeParam(Param.Type.StringList(new))
                    },
                    labelStr = "Field: ${index + 1}",
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                        .fillMaxWidth()
                )
            }
            SubmitButton(
                onClick = {
                    val new = p.list.toMutableList()
                    new.add("")
                    onChangeParam(Param.Type.StringList(new))
                }, enabled = true, getUIStyle = getUIStyle, string = "Add field"
            )
            if (p.list.size > 1) {
                SubmitButton(onClick = {
                    val new = p.list.toMutableList()
                    new.removeAt(p.list.lastIndex)
                    onChangeParam(Param.Type.StringList(new))
                }, enabled = true, getUIStyle = getUIStyle, string = "Remove field")
            }
        }
    }
}
