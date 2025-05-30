package com.belmontCrest.cardCrafter.uiFunctions.symbols

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
    } else if (wordChecker("frac", textFieldValue, newValue, newText)) {
        return katexWord("frac", newValue, newText)

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
    } else if (wordChecker("alpha", textFieldValue, newValue, newText)) {
        return katexWord("alpha", newValue, newText)

    } else if (wordChecker("beta", textFieldValue, newValue, newText)) {
        return katexWord("beta", newValue, newText)

    } else if (wordChecker("gamma", textFieldValue, newValue, newText)) {
        return katexWord("gamma", newValue, newText)

    } else if (wordChecker("delta", textFieldValue, newValue, newText)) {
        return katexWord("delta", newValue, newText)

    } else if (wordChecker("epsilon", textFieldValue, newValue, newText)) {
        return katexWord("epsilon", newValue, newText)

    } else if (wordChecker("zeta", textFieldValue, newValue, newText)) {
        return katexWord("zeta", newValue, newText)

    } else if (wordChecker("eta", textFieldValue, newValue, newText)) {
        return katexWord("eta", newValue, newText)

    } else if (wordChecker("theta", textFieldValue, newValue, newText)) {
        return katexWord("theta", newValue, newText)

    } else if (wordChecker("iota", textFieldValue, newValue, newText)) {
        return katexWord("iota", newValue, newText)

    } else if (wordChecker("kappa", textFieldValue, newValue, newText)) {
        return katexWord("kappa", newValue, newText)

    } else if (wordChecker("lambda", textFieldValue, newValue, newText)) {
        return katexWord("lambda", newValue, newText)

    } else if (wordChecker("mu", textFieldValue, newValue, newText)) {
        return katexWord("mu", newValue, newText)

    } else if (wordChecker("nu", textFieldValue, newValue, newText)) {
        return katexWord("nu", newValue, newText)

    } else if (wordChecker("xi", textFieldValue, newValue, newText)) {
        return katexWord("xi", newValue, newText)

    } else if (wordChecker("omicron", textFieldValue, newValue, newText)) {
        return katexWord("omicron", newValue, newText)

    } else if (wordChecker("pi", textFieldValue, newValue, newText)) {
        return katexWord("pi", newValue, newText)

    } else if (wordChecker("rho", textFieldValue, newValue, newText)) {
        return katexWord("rho", newValue, newText)

    } else if (wordChecker("sigma", textFieldValue, newValue, newText)) {
        return katexWord("sigma", newValue, newText)

    } else if (wordChecker("tau", textFieldValue, newValue, newText)) {
        return katexWord("tau", newValue, newText)

    } else if (wordChecker("upsilon", textFieldValue, newValue, newText)) {
        return katexWord("upsilon", newValue, newText)

    } else if (wordChecker("phi", textFieldValue, newValue, newText)) {
        return katexWord("phi", newValue, newText)

    } else if (wordChecker("chi", textFieldValue, newValue, newText)) {
        return katexWord("chi", newValue, newText)

    } else if (wordChecker("psi", textFieldValue, newValue, newText)) {
        return katexWord("psi", newValue, newText)

    } else if (wordChecker("omega", textFieldValue, newValue, newText)) {
        return katexWord("omega", newValue, newText)

    } else if (wordChecker("varepsilon", textFieldValue, newValue, newText)) {
        return katexWord("varepsilon", newValue, newText)

    } else if (wordChecker("varkappa", textFieldValue, newValue, newText)) {
        return katexWord("varkappa", newValue, newText)

    } else if (wordChecker("vartheta", textFieldValue, newValue, newText)) {
        return katexWord("vartheta", newValue, newText)

    } else if (wordChecker("thetasym", textFieldValue, newValue, newText)) {
        return katexWord("thetasym", newValue, newText)

    } else if (wordChecker("varpi", textFieldValue, newValue, newText)) {
        return katexWord("varpi", newValue, newText)

    } else if (wordChecker("varrho", textFieldValue, newValue, newText)) {
        return katexWord("varrho", newValue, newText)

    } else if (wordChecker("varsigma", textFieldValue, newValue, newText)) {
        return katexWord("varsigma", newValue, newText)

    } else if (wordChecker("varphi", textFieldValue, newValue, newText)) {
        return katexWord("varphi", newValue, newText)

    } else if (wordChecker("Alpha", textFieldValue, newValue, newText)) {
        return katexWord("Alpha", newValue, newText)

    } else if (wordChecker("Beta", textFieldValue, newValue, newText)) {
        return katexWord("Beta", newValue, newText)

    } else if (wordChecker("Gamma", textFieldValue, newValue, newText)) {
        return katexWord("Gamma", newValue, newText)

    } else if (wordChecker("Delta", textFieldValue, newValue, newText)) {
        return katexWord("Delta", newValue, newText)

    } else if (wordChecker("Epsilon", textFieldValue, newValue, newText)) {
        return katexWord("Epsilon", newValue, newText)

    } else if (wordChecker("Zeta", textFieldValue, newValue, newText)) {
        return katexWord("Zeta", newValue, newText)

    } else if (wordChecker("Eta", textFieldValue, newValue, newText)) {
        return katexWord("Eta", newValue, newText)

    } else if (wordChecker("Theta", textFieldValue, newValue, newText)) {
        return katexWord("Theta", newValue, newText)

    } else if (wordChecker("Iota", textFieldValue, newValue, newText)) {
        return katexWord("Iota", newValue, newText)

    } else if (wordChecker("Kappa", textFieldValue, newValue, newText)) {
        return katexWord("Kappa", newValue, newText)

    } else if (wordChecker("Lambda", textFieldValue, newValue, newText)) {
        return katexWord("Lambda", newValue, newText)

    } else if (wordChecker("Mu", textFieldValue, newValue, newText)) {
        return katexWord("Mu", newValue, newText)

    } else if (wordChecker("Nu", textFieldValue, newValue, newText)) {
        return katexWord("Nu", newValue, newText)

    } else if (wordChecker("Xi", textFieldValue, newValue, newText)) {
        return katexWord("Xi", newValue, newText)

    } else if (wordChecker("Omicron", textFieldValue, newValue, newText)) {
        return katexWord("Omicron", newValue, newText)

    } else if (wordChecker("Pi", textFieldValue, newValue, newText)) {
        return katexWord("Pi", newValue, newText)

    } else if (wordChecker("Rho", textFieldValue, newValue, newText)) {
        return katexWord("Rho", newValue, newText)

    } else if (wordChecker("Sigma", textFieldValue, newValue, newText)) {
        return katexWord("Sigma", newValue, newText)

    } else if (wordChecker("Tau", textFieldValue, newValue, newText)) {
        return katexWord("Tau", newValue, newText)

    } else if (wordChecker("Upsilon", textFieldValue, newValue, newText)) {
        return katexWord("Upsilon", newValue, newText)

    } else if (wordChecker("Phi", textFieldValue, newValue, newText)) {
        return katexWord("Phi", newValue, newText)

    } else if (wordChecker("Chi", textFieldValue, newValue, newText)) {
        return katexWord("Chi", newValue, newText)

    } else if (wordChecker("Psi", textFieldValue, newValue, newText)) {
        return katexWord("Psi", newValue, newText)

    } else if (wordChecker("Omega", textFieldValue, newValue, newText)) {
        return katexWord("Omega", newValue, newText)

    } else if (newText.startsWith("INLINE", (newValue.selection.start - 6)) &&
        !isInsideDoubleDollars(newText, newText.length, textFieldValue.selection)
    ) {
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

/** Check if the cursor is inside either the `$$ $$` or `\\( \\)` */
fun isInside(text: String, position: Int, textRange: TextRange): Boolean {
    return isInsideInline(text, position, textRange) ||
            isInsideDoubleDollars(text, position, textRange)
}

/**
 * If there's an even amount of `$$` and the user tries to add
 * either the one on the start or end of the `$$`, it will not be added because
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


fun wordChecker(
    string: String, textFieldValue: TextFieldValue,
    newValue: TextFieldValue, newText: String
): Boolean {
    return newText.startsWith(string, (newValue.selection.start - string.length)) &&
            !newText.startsWith(
                "\\\\$string", (newValue.selection.start - string.length + 2)
            ) &&
            isInside(newText, newText.length, textFieldValue.selection)
}

fun katexWord(
    string: String, newValue: TextFieldValue, newText: String
): Pair<TextFieldValue, String> {
    val replacement = "\\\\$string"
    val startIndex = newValue.selection.start - string.length
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
}