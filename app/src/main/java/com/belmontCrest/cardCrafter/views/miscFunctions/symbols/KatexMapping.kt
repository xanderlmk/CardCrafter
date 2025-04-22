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
        !isThereEvenDD(newText, newText.length) &&
        !isInsideInline(newText, newText.length, textFieldValue.selection)
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
        isInside(newText, newText.length, textFieldValue.selection)
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
        isInside(newText, newText.length, textFieldValue.selection)
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

    } else if (
        newText.startsWith("alpha", (newValue.selection.start - 5)) &&
        !newText.startsWith("\\alpha", (newValue.selection.start - 7)) &&
        isInside(newText, newText.length, textFieldValue.selection)
    ) {
        val replacement = "\\alpha"
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


    } else if (
        newText.startsWith("beta", (newValue.selection.start - 4)) &&
        !newText.startsWith("\\beta", (newValue.selection.start - 6)) &&
        isInside(newText, newText.length, textFieldValue.selection)
    ) {
        val replacement = "\\beta"
        val startIndex = newValue.selection.start - 4
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
        newText.startsWith("gamma", (newValue.selection.start - 5)) &&
        !newText.startsWith("\\gamma", (newValue.selection.start - 7)) &&
        isInside(newText, newText.length, textFieldValue.selection)
    ) {
        val replacement = "\\gamma"
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


    } else if (
        newText.startsWith("delta", (newValue.selection.start - 5)) &&
        !newText.startsWith("\\delta", (newValue.selection.start - 7)) &&
        isInside(newText, newText.length, textFieldValue.selection)
    ) {
        val replacement = "\\delta"
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


    } else if (
        newText.startsWith("epsilon", (newValue.selection.start - 7)) &&
        !newText.startsWith("\\epsilon", (newValue.selection.start - 9)) &&
        isInside(newText, newText.length, textFieldValue.selection)
    ) {
        val replacement = "\\epsilon"
        val startIndex = newValue.selection.start - 7
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
        newText.startsWith("zeta", (newValue.selection.start - 4)) &&
        !newText.startsWith("\\zeta", (newValue.selection.start - 6)) &&
        isInside(newText, newText.length, textFieldValue.selection)
    ) {
        val replacement = "\\zeta"
        val startIndex = newValue.selection.start - 4
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
        newText.startsWith("eta", (newValue.selection.start - 3)) &&
        !newText.startsWith("\\eta", (newValue.selection.start - 5)) &&
        isInside(newText, newText.length, textFieldValue.selection)
    ) {
        val replacement = "\\eta"
        val startIndex = newValue.selection.start - 3
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
        newText.startsWith("theta", (newValue.selection.start - 5)) &&
        !newText.startsWith("\\theta", (newValue.selection.start - 7)) &&
        isInside(newText, newText.length, textFieldValue.selection)
    ) {
        val replacement = "\\theta"
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


    } else if (
        newText.startsWith("iota", (newValue.selection.start - 4)) &&
        !newText.startsWith("\\iota", (newValue.selection.start - 6)) &&
        isInside(newText, newText.length, textFieldValue.selection)
    ) {
        val replacement = "\\iota"
        val startIndex = newValue.selection.start - 4
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
        newText.startsWith("kappa", (newValue.selection.start - 5)) &&
        !newText.startsWith("\\kappa", (newValue.selection.start - 7)) &&
        isInside(newText, newText.length, textFieldValue.selection)
    ) {
        val replacement = "\\kappa"
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


    } else if (
        newText.startsWith("lambda", (newValue.selection.start - 6)) &&
        !newText.startsWith("\\lambda", (newValue.selection.start - 8)) &&
        isInside(newText, newText.length, textFieldValue.selection)
    ) {
        val replacement = "\\lambda"
        val startIndex = newValue.selection.start - 6
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
        newText.startsWith("mu", (newValue.selection.start - 2)) &&
        !newText.startsWith("\\mu", (newValue.selection.start - 4)) &&
        isInside(newText, newText.length, textFieldValue.selection)
    ) {
        val replacement = "\\mu"
        val startIndex = newValue.selection.start - 2
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
        newText.startsWith("nu", (newValue.selection.start - 2)) &&
        !newText.startsWith("\\nu", (newValue.selection.start - 4)) &&
        isInside(newText, newText.length, textFieldValue.selection)
    ) {
        val replacement = "\\nu"
        val startIndex = newValue.selection.start - 2
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
        newText.startsWith("xi", (newValue.selection.start - 2)) &&
        !newText.startsWith("\\xi", (newValue.selection.start - 4)) &&
        isInside(newText, newText.length, textFieldValue.selection)
    ) {
        val replacement = "\\xi"
        val startIndex = newValue.selection.start - 2
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
        newText.startsWith("omicron", (newValue.selection.start - 7)) &&
        !newText.startsWith("\\omicron", (newValue.selection.start - 9)) &&
        isInside(newText, newText.length, textFieldValue.selection)
    ) {
        val replacement = "\\omicron"
        val startIndex = newValue.selection.start - 7
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
        newText.startsWith("pi", (newValue.selection.start - 2)) &&
        !newText.startsWith("\\pi", (newValue.selection.start - 4)) &&
        isInside(newText, newText.length, textFieldValue.selection)
    ) {
        val replacement = "\\pi"
        val startIndex = newValue.selection.start - 2
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
        newText.startsWith("rho", (newValue.selection.start - 3)) &&
        !newText.startsWith("\\rho", (newValue.selection.start - 5)) &&
        isInside(newText, newText.length, textFieldValue.selection)
    ) {
        val replacement = "\\rho"
        val startIndex = newValue.selection.start - 3
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
        newText.startsWith("sigma", (newValue.selection.start - 5)) &&
        !newText.startsWith("\\sigma", (newValue.selection.start - 7)) &&
        isInside(newText, newText.length, textFieldValue.selection)
    ) {
        val replacement = "\\sigma"
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


    } else if (
        newText.startsWith("tau", (newValue.selection.start - 3)) &&
        !newText.startsWith("\\tau", (newValue.selection.start - 5)) &&
        isInside(newText, newText.length, textFieldValue.selection)
    ) {
        val replacement = "\\tau"
        val startIndex = newValue.selection.start - 3
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
        newText.startsWith("upsilon", (newValue.selection.start - 7)) &&
        !newText.startsWith("\\upsilon", (newValue.selection.start - 9)) &&
        isInside(newText, newText.length, textFieldValue.selection)
    ) {
        val replacement = "\\upsilon"
        val startIndex = newValue.selection.start - 7
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
        newText.startsWith("phi", (newValue.selection.start - 3)) &&
        !newText.startsWith("\\phi", (newValue.selection.start - 5)) &&
        isInside(newText, newText.length, textFieldValue.selection)
    ) {
        val replacement = "\\phi"
        val startIndex = newValue.selection.start - 3
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
        newText.startsWith("chi", (newValue.selection.start - 3)) &&
        !newText.startsWith("\\chi", (newValue.selection.start - 5)) &&
        isInside(newText, newText.length, textFieldValue.selection)
    ) {
        val replacement = "\\chi"
        val startIndex = newValue.selection.start - 3
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
        newText.startsWith("psi", (newValue.selection.start - 3)) &&
        !newText.startsWith("\\psi", (newValue.selection.start - 5)) &&
        isInside(newText, newText.length, textFieldValue.selection)
    ) {
        val replacement = "\\psi"
        val startIndex = newValue.selection.start - 3
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
        newText.startsWith("omega", (newValue.selection.start - 5)) &&
        !newText.startsWith("\\omega", (newValue.selection.start - 7)) &&
        isInside(newText, newText.length, textFieldValue.selection)
    ) {
        val replacement = "\\omega"
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


    } else if (
        newText.startsWith("varepsilon", (newValue.selection.start - 10)) &&
        !newText.startsWith("\\varepsilon", (newValue.selection.start - 12)) &&
        isInside(newText, newText.length, textFieldValue.selection)
    ) {
        val replacement = "\\varepsilon"
        val startIndex = newValue.selection.start - 10
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
        newText.startsWith("varkappa", (newValue.selection.start - 8)) &&
        !newText.startsWith("\\varkappa", (newValue.selection.start - 10)) &&
        isInside(newText, newText.length, textFieldValue.selection)
    ) {
        val replacement = "\\varkappa"
        val startIndex = newValue.selection.start - 8
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
        newText.startsWith("vartheta", (newValue.selection.start - 8)) &&
        !newText.startsWith("\\vartheta", (newValue.selection.start - 10)) &&
        isInside(newText, newText.length, textFieldValue.selection)
    ) {
        val replacement = "\\vartheta"
        val startIndex = newValue.selection.start - 8
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
        newText.startsWith("thetasym", (newValue.selection.start - 8)) &&
        !newText.startsWith("\\thetasym", (newValue.selection.start - 10)) &&
        isInside(newText, newText.length, textFieldValue.selection)
    ) {
        val replacement = "\\thetasym"
        val startIndex = newValue.selection.start - 8
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
        newText.startsWith("varpi", (newValue.selection.start - 5)) &&
        !newText.startsWith("\\varpi", (newValue.selection.start - 7)) &&
        isInside(newText, newText.length, textFieldValue.selection)
    ) {
        val replacement = "\\varpi"
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


    } else if (
        newText.startsWith("varrho", (newValue.selection.start - 6)) &&
        !newText.startsWith("\\varrho", (newValue.selection.start - 8)) &&
        isInside(newText, newText.length, textFieldValue.selection)
    ) {
        val replacement = "\\varrho"
        val startIndex = newValue.selection.start - 6
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
        newText.startsWith("varsigma", (newValue.selection.start - 8)) &&
        !newText.startsWith("\\varsigma", (newValue.selection.start - 10)) &&
        isInside(newText, newText.length, textFieldValue.selection)
    ) {
        val replacement = "\\varsigma"
        val startIndex = newValue.selection.start - 8
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
        newText.startsWith("varphi", (newValue.selection.start - 6)) &&
        !newText.startsWith("\\varphi", (newValue.selection.start - 8)) &&
        isInside(newText, newText.length, textFieldValue.selection)
    ) {
        val replacement = "\\varphi"
        val startIndex = newValue.selection.start - 6
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

} else if (newText.startsWith("INLINE", (newValue.selection.start - 6)) &&
        !isInsideDoubleDollars(newText, newText.length, textFieldValue.selection)) {
        val replacement = "\\\\(\\\\)"
        val startIndex = newValue.selection.start - 6
        val replaced = buildString {
            append(newText.substring(0, startIndex))
            append(replacement)
            append(newText.substring(newValue.selection.start))
        }
        val insertionPoint = startIndex + replacement.length - 3
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

/** Our inline content \\( detect here. \\) */
fun isInsideInline(text: String, position: Int, textRange: TextRange): Boolean {
    var count = 0
    var searchStart = 0
    var cursor = textRange.start
    var found = false
    var state = "\\\\("
    var delimiter = text.indexOf(state, searchStart)
    if (delimiter == -1) {
        return false
    }
    count += 1
    // Now go to that index + 3.
    searchStart = delimiter + 3
    while (true) {
        state =
            if (state == "\\\\(") {
                "\\\\)"
            } else {
                "\\\\("
            }
        // Find the next occurrence of `the one to be found`
        val nextIndex = text.indexOf(state, searchStart)

        // If none found or it's beyond the cursor position, stop
        if (nextIndex == -1 || delimiter >= position) break

        // We found one occurrence an occurrence before `position`
        count++
        // if the cursor is in between \\( \\) (count will be even )
        // break and return true!
        if ((count % 2 == 0) && (cursor in delimiter..nextIndex)) {
            found = true
            break
        }
        // set the delimiter to this index.
        delimiter = nextIndex
        // Move past this occurrence
        searchStart = nextIndex + 3
    }
    return found
}


fun isInside(text: String, position: Int, textRange: TextRange): Boolean {
    return isInsideInline(text, position, textRange) ||
            isInsideDoubleDollars(text, position, textRange)
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