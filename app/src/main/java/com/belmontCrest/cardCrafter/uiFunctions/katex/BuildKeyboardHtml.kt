@file:Suppress("SpellCheckingInspection")

package com.belmontCrest.cardCrafter.uiFunctions.katex


private val ACCENTS = listOf(
    "tilde{a}", "mathring{g}", "widetilde{ac}", "utilde{AB}", "vec{F}",

    // Arrow variants
    "overleftarrow{AB}", "underleftarrow{AB}",
    "overrightarrow{AB}", "underrightarrow{AB}",
    "overleftharpoon{ac}", "overrightharpoon{ac}",
    "overleftrightarrow{AB}", "underleftrightarrow{AB}",
    "Overrightarrow{AB}"
)

private val GREEK_LETTERS = listOf(
    // Alpha variants
    "Alpha", "alpha",
    // Beta variants
    "Beta", "beta",
    // Gamma variants
    "Gamma", "varGamma", "gamma",
    // Delta variants
    "Delta", "varDelta", "delta",
    // Epsilon variants
    "Epsilon", "varepsilon", "epsilon",
    // Zeta variants
    "Zeta", "zeta",
    // Eta variants
    "Eta", "eta",
    // Theta variants
    "Theta", "varTheta", "theta", "vartheta",
    // Iota variants
    "Iota", "iota",
    // Kappa variants
    "Kappa", "kappa", "varkappa",
    // Lambda variants
    "Lambda", "varLambda", "lambda",
    // Mu variants
    "Mu", "mu",
    // Nu variants
    "Nu", "nu",
    // Xi variants
    "Xi", "varXi", "xi",
    // Omicron variants
    "Omicron", "omicron",
    // Pi variants
    "Pi", "varPi", "pi",
    // Rho variants
    "Rho", "varrho", "rho",
    // Sigma variants
    "Sigma", "varSigma", "sigma", "varsigma",
    // Tau variants
    "Tau", "tau",
    // Upsilon variants
    "Upsilon", "varUpsilon", "upsilon",
    // Phi variants
    "Phi", "varPhi", "phi", "varphi",
    // Chi variants
    "Chi", "chi",
    // Psi variants
    "Psi", "varPsi", "psi",
    // Omega variants
    "Omega", "varOmega", "omega",
    // Archaic/other
    "digamma"
)

private val OTHER_LETTERS = listOf(
    "imath", "nabla", "Im", "Reals", "text{\\\\OE}",
    "jmath", "partial", "image", "wp", "text{\\\\o}",
    "aleph", "Game", "Bbbk", "text{\\\\O}",
    "Finv", "N", "Z", "text{\\\\ss}",
    "cnums", "text{\\\\aa}", "text{\\\\i}", "beth",
    "R", "text{\\\\AA}", "text{\\\\j}", "gimel", "ell",
    "text{\\\\ae}", "daleth", "hbar", "text{\\\\AE}",
    "eth", "hslash", "text{\\\\oe}"
)

fun buildKeyboardHtml(): String {
    return buildString {
        append(
            """
            |<div class="section">
            |<div class="section-header" onclick="toggleSection('greek')">
            |Greek Letters
            |</div>
            |<div id="greek" class="symbols-container">
            """.trimMargin()
        )
        for (greekLetter in GREEK_LETTERS) {
            val escaped = greekLetter.replace("'", "\\'")
            append(
                """
                |<div class="symbol-item" onclick="Android.onSymbolSelected('$escaped')">
                |\\(\\$greekLetter\\)
                |</div>
                """.trimMargin()
            )
        }
        append(
            """
            |</div>
            |</div>
            """.trimMargin()
        )
        append(
            """
            |<div class="section">
            |<div class="section-header" onclick="toggleSection('other')">
            |Other Letters
            |</div>
            |<div id="other" class="symbols-container">
            """.trimMargin()
        )
        for (letter in OTHER_LETTERS) {
            val escaped = letter.replace("'", "\\'")
            append(
                """
                |<div class="symbol-item" onclick="Android.onSymbolSelected('$escaped')">
                |\\(\\$letter\\)
                |</div>
                """.trimMargin()
            )
        }
        append(
            """
            |</div>
            |</div>
            """.trimMargin()
        )
        append(
            """
            |<div class="section">
            |<div class="section-header" onclick="toggleSection('accent')">
            |Accents
            |</div>
            |<div id="accent" class="symbols-container">
            """.trimMargin()
        )
        for (accent in ACCENTS) {
            val escaped = accent.replace("'", "\\'")
            append(
                """
                |<div class="symbol-item" onclick="Accent.onAccentSelected('$escaped')">
                |\\(\\$accent\\)
                |</div>
                """.trimMargin()
            )
        }
        append(
            """
            |</div>
            |</div>
            """.trimMargin()
        )
    }
}