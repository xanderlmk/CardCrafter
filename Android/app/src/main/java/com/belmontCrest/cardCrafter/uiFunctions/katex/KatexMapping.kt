package com.belmontCrest.cardCrafter.uiFunctions.katex

import android.util.Log
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

private val SPECIAL_WORDS = listOf(
    "frac", "Alpha", "Beta", "Epsilon",
    "Zeta", "Eta", "Iota", "Kappa", "Mu",
    "Nu", "Omicron", "Rho", "digamma", "Tau",
    "Chi", "varGamma", "varDelta", "varTheta",
    "varLambda", "varXi", "varPi", "varSigma",
    "varUpsilon", "varPhi", "varPsi", "varOmega",
    "alpha", "beta", "gamma", "delta", "zeta",
    "iota", "lambda", "mu", "nu", "xi",
    "omicron", "tau", "upsilon", "chi", "psi",
    "omega", "varepsilon", "varkappa", "vartheta",
    "varpi", "varrho", "varsigma", "varphi",
    "Gamma", "Delta", "Theta", "Lambda", "Xi", "Pi",
    "Sigma", "Upsilon", "Phi", "Psi", "Omega", "kappa",
    "epsilon", "theta", "pi", "rho", "sigma", "phi", "eta",
)


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
                selection = TextRange(insertionPoint),
                composition = TextRange(insertionPoint)
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
                selection = TextRange(insertionPoint),
                composition = TextRange(insertionPoint)
            ),
            replaced
        )
    } else if (checkForSpecialWord(textFieldValue, newValue, newText)) {
        val result = findSpecialWord(newValue, newText)
        if (result === null) {
            Log.e("KaTeXMapper", "Something went wrong trying to get the special word.")
            return Pair(newValue, newText)

        }
        return katexWord(result, newValue, newText)

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
                selection = TextRange(insertionPoint),
                composition = TextRange(insertionPoint)
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

private fun katexWord(
    string: String, newValue: TextFieldValue, newText: String
): Pair<TextFieldValue, String> {
    val replacement = "\\\\$string"
    val startIndex = newValue.selection.start - string.length
    val replaced = buildString {
        append(newText.substring(0, startIndex))
        append(replacement)
        append(newText.substring(newValue.selection.start))
    }
    Log.d("KatexMapping", replacement.length.toString())

    val insertionPoint = startIndex + replacement.length
    return Pair(
        TextFieldValue(
            text = replaced,
            selection = TextRange(insertionPoint),
            composition = TextRange(insertionPoint)
        ),
        replaced
    )
}

private fun checkForSpecialWord(
    textFieldValue: TextFieldValue,
    newValue: TextFieldValue, newText: String
): Boolean {
    for (word in SPECIAL_WORDS) {
        if (newText.startsWith(word, (newValue.selection.start - word.length)) &&
            !newText.startsWith(
                "\\\\$word", (newValue.selection.start - word.length + 2)
            ) &&
            isInside(newText, newText.length, textFieldValue.selection)
        ) {
            return true
        }
    }
    return false
}

private fun findSpecialWord(
    newValue: TextFieldValue,
    newText: String
): String? {
    return SPECIAL_WORDS.firstOrNull { word ->
        // where in the string would this word have to start?
        val start = newValue.selection.start - word.length
        // sanity check
        start >= 0 && newText.startsWith(word, start)
    }
}