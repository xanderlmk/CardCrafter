package com.belmontCrest.cardCrafter.uiFunctions.katex

import android.util.Log
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

/**
 * Add the notation to the given text field and update the textFieldValue
 * @param textFieldValue The current textFieldValue
 * @param text The current text in the textFieldValue
 * @param kk Just the `const val` of the KatexKeyboard for debugging
 * @param notation The notation to add
 * @param offset How far back to move the cursor
 * @param onValueChanged the value to change
 */
fun updateNotation(
    textFieldValue: TextFieldValue, text: String, kk: String,
    notation: String, offset: Int, onValueChanged: (String) -> Unit
): TextFieldValue {
    Log.d(kk, "insertionPoint: ${textFieldValue.selection.start}")
    val startIndex =
        (textFieldValue.selection.start).coerceAtLeast(0)
    val replaced = buildString {
        append(text.substring(0, startIndex))
        append(notation)
        append(text.substring(textFieldValue.selection.start))
    }
    val insertionPoint = startIndex + notation.length - offset
    Log.d(kk, "insertionPoint: $insertionPoint")
    onValueChanged(replaced)
    return TextFieldValue(replaced, TextRange(insertionPoint))
}