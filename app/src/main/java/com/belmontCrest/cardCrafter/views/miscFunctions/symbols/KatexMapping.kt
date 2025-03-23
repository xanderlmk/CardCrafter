package com.belmontCrest.cardCrafter.views.miscFunctions.symbols

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

fun katexMapper(
    newText: String, newValue: TextFieldValue,
    textFieldValue: TextFieldValue
): Pair<TextFieldValue, String> {
    return if (
        newText.startsWith("$$", (newValue.selection.start - 2)) &&
        !newText.startsWith("$$$$", (newValue.selection.start - 4)) &&
        !isThereEvenDD(newText, newText.length)
    ) {
        val replacement = "$$$$"
        val startIndex = newValue.selection.start - 2

        // Replace `$$` with `$$$$`
        val replaced = buildString {
            append(newText.substring(0, startIndex))
            // Our new replacement
            append(replacement)
            append(newText.substring(newValue.selection.start))
        }
        val insertionPoint = startIndex + replacement.length - 2
        // return the TextFieldValue and placed string
        return Pair(
            TextFieldValue(
                text = replaced,
                selection = TextRange(insertionPoint)
            ),
            replaced
        )
    } else if (
        newText.startsWith("frac", (newValue.selection.start - 4)) &&
        !newText.startsWith("\\\\frac", (newValue.selection.start - 4)) &&
        isInsideDoubleDollars(newText, newText.length, textFieldValue.selection)
    ) {
        val replacement = "\\\\frac"
        val startIndex = newValue.selection.start - 4
        // Replace frac with \\frac
        val replaced = buildString {
            append(newText.substring(0, startIndex))
            append(replacement)
            append(newText.substring(newValue.selection.start))
        }

        val insertionPoint = startIndex + replacement.length

        return Pair(
            TextFieldValue(
                text = replaced,
                selection = TextRange(insertionPoint)
            ),
            replaced
        )
    } else if (
        newText.startsWith("..", (newValue.selection.start - 2)) &&
        isInsideDoubleDollars(newText, newText.length, textFieldValue.selection)
    ) {
        val replacement = "{}"
        val startIndex = newValue.selection.start - 2
        val replaced = buildString {
            append(newText.substring(0, startIndex))
            append(replacement)
            append(newText.substring(newValue.selection.start))
        }
        val insertionPoint = startIndex + replacement.length - 1
        return Pair(
            TextFieldValue(
                text = replaced,
                selection = TextRange(insertionPoint)
            ),
            replaced
        )
    } else if(
        newText.startsWith("alpha", (newValue.selection.start - 5)) &&
        !newText.startsWith("\\\\alpha", (newValue.selection.start - 7)) &&
        isInsideDoubleDollars(newText, newText.length, textFieldValue.selection)) {
        val replacement = "\\\\alpha"
        val startIndex = newValue.selection.start - 5
        val replaced = buildString {
            append(newText.substring(0, startIndex))
            append(replacement)
            append(newText.substring(newValue.selection.start))
        }
        val insertionPoint = startIndex + replacement.length
        return Pair(
            TextFieldValue(
                text = replaced,
                selection = TextRange(insertionPoint)
            ),
            replaced
        )
    } else {
        Pair(newValue, newText)
    }
}

/**
 * Returns true if the cursor is in between two $$ $$
 * example $$ cursor here $$ = true
 * else is false.
 */
fun isInsideDoubleDollars(text: String, position: Int, textRange: TextRange): Boolean {
    var count = 0
    var searchStart = 0
    var cursor = textRange.start
    var found = false

    var delimiter = text.indexOf("$$", searchStart)
    if (delimiter == -1) {
        return false
    }
    count += 1
    // Now go to that index + 2.
    searchStart = delimiter + 2

    while (true) {
        // Find the next occurrence of `$$` starting from searchStart
        val nextIndex = text.indexOf("$$", searchStart)

        // If none found or it's beyond the cursor position, stop
        if (nextIndex == -1 || delimiter >= position) break

        // We found one occurrence of `$$` before `position`
        count++
        // if the cursor is in between $$ $$ (count will be even )
        // break and return true!
        if ((count % 2 == 0) && (cursor in delimiter..nextIndex)) {
            found = true
            break
        }
        // set the delimiter to this index.
        delimiter = nextIndex
        // Move past this occurrence
        searchStart = nextIndex + 2

    }
    return found
}

/**
 * If there's an even amount of '$$' and the user tries to add
 * either the one on the start or end of the '$$", it will not be added because
 * an equation expression already exist.
 */
fun isThereEvenDD(text: String, position: Int): Boolean {
    var isEven = true
    var searchStart = 0

    while (true) {
        // Find the next `$$` from searchStart
        val nextIndex = text.indexOf("$$", startIndex = searchStart)
        // If no more `$$` or it's beyond [position], stop
        if (nextIndex == -1 || nextIndex >= position) break

        // Toggle isInside
        isEven = !isEven

        // Move past this occurrence
        searchStart = nextIndex + 2
    }

    return isEven
}