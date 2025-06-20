package com.belmontCrest.cardCrafter.uiFunctions

import android.util.Log
import androidx.compose.foundation.focusable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.model.ui.states.MyTextRange
import com.belmontCrest.cardCrafter.model.ui.states.SelectedKeyboard
import com.belmontCrest.cardCrafter.model.ui.states.toMyTextRange
import com.belmontCrest.cardCrafter.model.ui.states.toTextRange
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.textColor
import com.belmontCrest.cardCrafter.uiFunctions.katex.menu.KaTeXMenu
import com.belmontCrest.cardCrafter.uiFunctions.katex.isInside
import com.belmontCrest.cardCrafter.uiFunctions.katex.katexMapper
import com.belmontCrest.cardCrafter.uiFunctions.katex.menu.IsInsideException
import com.belmontCrest.cardCrafter.uiFunctions.katex.menu.SelectedAnnotation
import com.belmontCrest.cardCrafter.uiFunctions.katex.menu.updateCursor
import com.belmontCrest.cardCrafter.uiFunctions.katex.menu.updateNotation

private object KeyboardInputs {
    const val KK = "KatexKeyBoard"
}

/** This more for the MultiChoiceCard Choice inputs */
@Composable
fun EditTextField(
    value: String,
    onValueChanged: (String) -> Unit,
    labelStr: String,
    modifier: Modifier,
    inputColor: Color = Color.Transparent,
) {
    val focusManager = LocalFocusManager.current
    val colors = if (inputColor == Color.Transparent) {
        TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
        )
    } else {
        TextFieldDefaults.colors(
            unfocusedTextColor = inputColor,
            focusedTextColor = inputColor,
        )
    }
    TextField(
        value = value,
        singleLine = false,
        modifier = modifier,
        onValueChange = onValueChanged,
        label = { Text(labelStr, color = textColor) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
            }
        ),
        colors = colors,
        textStyle =
            if (inputColor == Color.Transparent) {
                TextStyle.Default
            } else {
                TextStyle(
                    fontWeight = FontWeight.ExtraBold,
                    fontStyle = FontStyle.Italic,
                    background = MaterialTheme.colorScheme.surface
                )
            }
    )
}

/** Rather than clicking done (enter) it'll go to the next line */
@Composable
fun EditTextFieldNonDone(
    value: String,
    onValueChanged: (String) -> Unit,
    labelStr: String,
    modifier: Modifier,
) {
    val focusManager = LocalFocusManager.current
    TextField(
        value = value,
        singleLine = false,
        modifier = modifier,
        onValueChange = onValueChanged,
        label = { Text(labelStr, color = textColor) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        ),
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() }
        ),
        textStyle = TextStyle.Default
    )
}

/** Special Keyboard which auto corrects special words in their latex form to
 *  be able to display on the screen. Accounts for the custom KaTeXMenu.kt
 */
@Composable
fun LatexKeyboard(
    value: String, onValueChanged: (String) -> Unit, labelStr: String,
    modifier: Modifier, kt: KaTeXMenu, onIdle: () -> Unit,
    onFocusChanged: () -> Unit, selectedKeyboard: SelectedKeyboard?,
    actualKeyboard: SelectedKeyboard, composition: MyTextRange?, selection: MyTextRange,
    onUpdateTR: (MyTextRange, MyTextRange?) -> Unit
) {
    val kk = KeyboardInputs.KK
    val focusRequester = remember { FocusRequester() }
    val isSelected = actualKeyboard == selectedKeyboard
    var textFieldValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue(value, TextRange(value.length), TextRange(value.length))
        )
    }

    val context = LocalContext.current


    if (value.isEmpty()) {
        textFieldValue = TextFieldValue()
    }

    LaunchedEffect(Unit) {
        if (composition != null && isSelected) {
            focusRequester.requestFocus()
            Log.d(kk, "updating $actualKeyboard textFieldValue: $value, $selection, $composition")
            textFieldValue =
                TextFieldValue(value, selection.toTextRange(), composition.toTextRange())
        }
    }

    LaunchedEffect(kt) {
        val text = textFieldValue.text
        if (!textFieldValue.selection.collapsed) {
            Log.w(kk, "text field not collapsed.")
            return@LaunchedEffect
        }
        if (kt.sa is SelectedAnnotation.CursorChange) {
            try {
                val newTF = updateCursor(kt.sa, textFieldValue, text) { onValueChanged(it) }
                textFieldValue = newTF
                onUpdateTR(newTF.selection.toMyTextRange(), newTF.composition.toMyTextRange())
            } catch (e: IsInsideException) {
                Log.e(kk, "$e")
                showToastMessage(context, "Cannot put notation inside a notation")
            } finally {
                onIdle()
            }
            return@LaunchedEffect
        }
        if (!isInside(text, text.length, textFieldValue.selection)) {
            if (kt.notation != null) {
                showToastMessage(context, "Make sure the symbol is between the delimiters.")
                onIdle()
            }
            return@LaunchedEffect
        }
        if (!kt.notation.isNullOrEmpty()) {
            try {
                val newTF = updateNotation(kt.sa, kt.notation, text, kk, textFieldValue) {
                    onValueChanged(it)
                }
                textFieldValue = newTF
                onUpdateTR(newTF.selection.toMyTextRange(), newTF.composition.toMyTextRange())
            } catch (e: IllegalStateException) {
                Log.e(kk, "$e")
            } finally {
                onIdle()
            }
        }
    }

    TextField(
        value = textFieldValue,
        onValueChange = { newValue ->
            val oldText = textFieldValue.text
            val newText = newValue.text
            if (newText.length > oldText.length) {
                if (newValue.selection.collapsed) {
                    val (newTF, newText) = katexMapper(newText, newValue, textFieldValue)
                    textFieldValue = newTF
                    onUpdateTR(newTF.selection.toMyTextRange(), newTF.selection.toMyTextRange())
                    onValueChanged(newText)
                } else {
                    textFieldValue = newValue
                    onUpdateTR(
                        newValue.selection.toMyTextRange(),
                        newValue.selection.toMyTextRange()
                    )
                    onValueChanged(newText)
                }
            } else { // User deletes something.
                if (newText.endsWith("$$$") && !newText.endsWith("$$$$")) {
                    val insertionPoint = newValue.selection.end
                    val replaced = buildString {
                        append(newText.dropLast(2))
                    }
                    textFieldValue = TextFieldValue(
                        text = replaced,
                        selection = TextRange(insertionPoint),
                        composition = TextRange(insertionPoint)
                    )
                    val newTR = TextRange(insertionPoint).toMyTextRange()
                    onUpdateTR(newTR, newTR)

                    onValueChanged(replaced)
                } else {
                    textFieldValue = newValue
                    val newTR = newValue.selection.toMyTextRange()
                    onUpdateTR(newTR, newTR)
                    onValueChanged(newText)
                }
            }
        }, singleLine = false,
        label = { Text(labelStr, color = textColor) },
        keyboardOptions = KeyboardOptions(showKeyboardOnFocus = false),
        keyboardActions = KeyboardActions(
            onDone = { focusRequester.freeFocus() },
        ),
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
                if (focusState.hasFocus) onFocusChanged()
            }
            .focusable(), textStyle = TextStyle.Default
    )
}

@Composable
fun EditDoubleField(
    value: String,
    onValueChanged: (String) -> Unit,
    labelStr: String,
    modifier: Modifier
) {
    TextField(
        value = value,
        singleLine = true,
        modifier = modifier,
        onValueChange = onValueChanged,
        label = { Text(labelStr, color = textColor) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
    )
}

@Composable
fun EditIntField(
    value: String,
    onValueChanged: (String) -> Unit,
    labelStr: String,
    modifier: Modifier
) {
    TextField(
        value = value,
        singleLine = true,
        modifier = modifier,
        onValueChange = onValueChanged,
        label = {
            Text(
                labelStr, color = textColor, fontSize = 12.sp, maxLines = 1
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    )
}


@Composable
fun PasswordTextField(
    label: String,
    password: String,
    onPasswordChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    getUIStyle: GetUIStyle
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    TextField(
        value = password,
        onValueChange = onPasswordChange,
        label = {
            Text(label, color = textColor, maxLines = 1)
        },
        singleLine = true,
        modifier = modifier,
        visualTransformation = if (passwordVisible)
            VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val icon = if (passwordVisible) painterResource(R.drawable.visibility)
            else painterResource(R.drawable.visibility_off)
            val desc = if (passwordVisible) "Hide password" else "Show password"
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    painter = icon,
                    contentDescription = desc,
                    tint = getUIStyle.defaultIconColor()
                )
            }
        }, textStyle = TextStyle.Default,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    )
}