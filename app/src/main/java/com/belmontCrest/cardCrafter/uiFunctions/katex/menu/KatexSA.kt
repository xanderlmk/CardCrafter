package com.belmontCrest.cardCrafter.uiFunctions.katex.menu

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
    val startIndex = (textFieldValue.selection.start).coerceAtLeast(0)
    val replaced = buildString {
        append(text.substring(0, startIndex))
        append(notation)
        append(text.substring(textFieldValue.selection.start))
    }
    val insertionPoint = startIndex + notation.length + offset
    Log.d(kk, "insertionPoint: $insertionPoint")
    onValueChanged(replaced)
    return TextFieldValue(replaced, TextRange(insertionPoint), TextRange(insertionPoint))
}

fun updateNotation(
    sa: SelectedAnnotation, notation: String, text: String, kk: String,
    textFieldValue: TextFieldValue, onValueChanged: (String) -> Unit
): TextFieldValue {
    when (sa) {
        is SelectedAnnotation.Letter -> {
            return updateNotation(textFieldValue, text, kk, notation, 0) { onValueChanged(it) }
        }

        is SelectedAnnotation.Accent -> {
            return updateNotation(textFieldValue, text, kk, notation, -1) { onValueChanged(it) }
        }

        is SelectedAnnotation.EQ -> {
            val substring = notation.substringAfter('{')
            return updateNotation(
                textFieldValue, text, kk, notation, (-substring.length) + 1
            ) { onValueChanged(it) }
        }

        is SelectedAnnotation.OP -> {
            val us = notation.firstOrNull { it == '_' }
            if (us != null) {
                val substring = if (notation.startsWith("\\\\sum")) {
                    if (notation.endsWith("}^n")) {
                        notation.substringAfter('0')
                    } else {
                        notation.substringAfter("{n")
                    }
                } else {
                    notation.substringAfter("x_1")
                }
                return updateNotation(textFieldValue, text, kk, notation, -(substring.length)) {
                    onValueChanged(it)
                }
            } else {
                return updateNotation(textFieldValue, text, kk, notation, 0) { onValueChanged(it) }
            }
        }

        is SelectedAnnotation.NORM -> {
            val us = notation.firstOrNull { it == '^' }
            val obs = notation.firstOrNull { it == '|' }
            if (us != null) {
                val idx = (textFieldValue.selection.start).coerceAtLeast(0)
                if (idx == 0) return textFieldValue
                val prev = text.getOrNull(idx - 1)
                if (prev?.isLetterOrDigit() != true) return textFieldValue

                val offset = if (notation.endsWith('}')) -1 else 0
                return updateNotation(textFieldValue, text, kk, notation, offset) {
                    onValueChanged(it)
                }
            } else if (obs != null) {
                return updateNotation(textFieldValue, text, kk, notation, -1) { onValueChanged(it) }
            } else if (notation.startsWith("\\\\sqrt")) {
                val offset =
                    if (notation.firstOrNull { it == '[' } != null) -3
                    else -1
                return updateNotation(textFieldValue, text, kk, notation, offset) {
                    onValueChanged(it)
                }
            } else {
                return updateNotation(textFieldValue, text, kk, notation, 0) { onValueChanged(it) }
            }
        }

        else -> {
            throw IllegalStateException(
                "SelectedAnnotation is either Idle or there was a cursor change\n" +
                        "Notation is not null or the function somehow reached this far."
            )
        }
    }
}

fun updateCursor(
    cursor: SelectedAnnotation.CursorChange, textFieldValue: TextFieldValue, text: String
): TextFieldValue {
    when (cursor) {
        SelectedAnnotation.CursorChange.Backward -> {
            val textRange = TextRange((textFieldValue.selection.start - 1).coerceAtLeast(0))
            return TextFieldValue(text, textRange, textRange)
        }

        SelectedAnnotation.CursorChange.Forward -> {
            val textRange =
                TextRange((textFieldValue.selection.start + 1).coerceAtMost(text.length))
            return TextFieldValue(text, textRange, textRange)
        }
    }
}