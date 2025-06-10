@file:Suppress("SpellCheckingInspection")

package com.belmontCrest.cardCrafter.uiFunctions.katex.menu


private val ACCENTS = listOf(
    // accent variants
    "acute{a}", "bar{y}", "breve{a}", "check{a}",
    "dot{a}", "ddot{a}", "dddot{a}", "ddddot{a}", "grave{a}", "hat{\\\\theta}",
    "tilde{a}", "mathring{g}", "widetilde{ac}", "utilde{AB}", "vec{F}",

    // Arrow variants
    "overleftarrow{AB}", "underleftarrow{AB}",
    "overrightarrow{AB}", "underrightarrow{AB}",
    "overleftharpoon{ac}", "overrightharpoon{ac}",
    "overleftrightarrow{AB}", "underleftrightarrow{AB}",
    "Overrightarrow{AB}",

    // Lined variants
    "overgroup{AB}", "undergroup{AB}", "overline{AB}", "underline{AB}",
    "widecheck{ac}", "widehat{ac}",
    "overbrace{AB}", "underbrace{AB}", "overlinesegment{AB}",
    "underlinesegment{AB}", "underbar{X}",
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

private val FRACS_AND_BINOS = listOf(
    "\\\\frac{a}{b}",
    "\\\\frac{a}{1 + \\\\frac{1}{b}}",
    "\\\\genfrac ( ] {1pt} {1}a{a+1}",
    "\\\\binom{n}{k}",
    "{n\\\\brace k}",
    "{n\\\\brack k}"
)

private val ARROWS = listOf(
    // Curved arrow variants
    "circlearrowleft", "circlearrowright", "curvearrowleft", "curvearrowright",
    // 'Normal' arrow variants
    "downarrow", "uparrow", "leftarrow", "rightarrow",
    "leftrightarrow", "updownarrow", "nwarrow", "nearrow", "searrow", "swarrow",
    // Two-headed arrow variants
    "twoheadleftarrow", "twoheadrightarrow",
    // Tailed arrow variants
    "leftarrowtail", "rightarrowtail", "longmapsto", "mapsto",
    // Outlined arrow variants
    "Downarrow", "Uparrow", "Leftarrow", "Rightarrow",
    "Leftrightarrow", "Updownarrow", "iff", "impliedby", "implies",
    "Lleftarrow", "Rrightarrow",
    // Dashed arrow variants
    "dashleftarrow", "dashrightarrow",
    // Doubled arrow variants
    "downdownarrows", "upuparrows", "leftleftarrows", "rightrightarrows",
    "leftrightarrows", "rightleftarrows",
    // Harpooned arrow variants
    "downharpoonleft", "downharpoonright", "upharpoonleft", "upharpoonright",
    "leftharpoondown", "leftharpoonup", "rightharpoondown", "rightharpoonup",
    // Not arrow variants
    "nleftarrow", "nrightarrow", "nleftrightarrow",
    "nLeftarrow", "nRightarrow", "nLeftrightarrow",
    // Squigel arrow variants
    "leftrightsquigarrow", "rightsquigarrow"
)
private val EXTENSIBLE_ARROWS = listOf(
    // `Normal` arrow variants
    "xleftarrow{abc}", "xrightarrow{abc}",
    "xLeftarrow{abc}", "xRightarrow{abc}",
    "xleftrightarrow{abc}", "xLeftrightarrow{abc}",
    // Special arrow variants
    "xhookleftarrow{abc}", "xhookrightarrow{abc}",
    "xtwoheadleftarrow{abc}", "xtwoheadrightarrow{abc}",
    "xleftharpoonup{abc}", "xrightharpoonup{abc}",
    "xleftharpoondown{abc}", "xrightharpoondown{abc}",
    "xleftrightharpoons{abc}", "xrightleftharpoons{abc}",
    "xtofrom{abc}", "xlongequal{abc}",
    // Under and over arrow variants (any can be done like this in this list)
    "xrightarrow[b]{a}", "xleftrightarrow[b]{a}"
)
private val BIG_OPERATORS = listOf(
    "sum", "int", "oint", "prod", "coprod",
    "sum_{i=0}^n", "int_{x_1}^{x_2}",
    "sum_{\\\\substack{0\\\\lt{i}\\\\lt{m}\\\\\\\\0\\\\lt{j}\\\\lt{n}}}",
    "bigotimes", "bigoplus", "bigodot", "biguplus",
    "bigvee", "bigwedge", "bigcap", "bigcup", "bigsqcup",
    "iint", "iiint", "oiint", "oiiint"
)

private val NORMAL_OPS_NUMS = listOf(
    "x", "y", "a^2", "a^{b}", "(", ")", "\\\\{\\\\}",
    "\\\\lt", "\\\\gt", "|a|", ",", "\\\\le", "\\\\ge",
    "\\\\sqrt{x}", "\\\\sqrt[a]{x}", "\\\\pi",
    "1", "2", "3", "4", "5", "6", "7", "8", "9", "0",
    ".", "=", "+", "-", "\\\\times", "\\\\div"
)

private const val ON_SYMBOL_SELECTED = "onSymbolSelected"
private const val ON_ACCENT_SELECTED = "onAccentSelected"
private const val SYMBOL = "symbol"
private const val OPERATOR = "operator"

fun buildKeyboardHtml(): String {
    return buildString {
        append(startSection("normal", "Normalized Keyboard"))
        for (eq in NORMAL_OPS_NUMS) {
            append(convertWord(eq, "onNormSelected", false, SYMBOL))
        }
        append(endDiv())
        append(startSection("frac_bino", "Fractions and Binomials"))
        for (eq in FRACS_AND_BINOS) {
            append(convertWord(eq, "onFracBinoSelected", false, SYMBOL))
        }
        append(endDiv())

        append(startSection("big_ops", "Big Operators"))
        for (op in BIG_OPERATORS) {
            append(convertWord(op, "onOPSelected", true, OPERATOR))
        }
        append(endDiv())

        append(startSection("greek", "Greek Letters"))
        for (greekLetter in GREEK_LETTERS) {
            append(convertWord(greekLetter, ON_SYMBOL_SELECTED, true, SYMBOL))
        }
        append(endDiv())

        append(startSection("other", "Other Letters"))
        for (letter in OTHER_LETTERS) {
            append(convertWord(letter, ON_SYMBOL_SELECTED, true, SYMBOL))
        }
        append(endDiv())

        append(startSection("accent", "Accents"))
        for (accent in ACCENTS) {
            append(convertWord(accent, ON_ACCENT_SELECTED, true, SYMBOL))
        }
        append(endDiv())

        append(startSection("arrows", "Arrows"))
        for (arrow in ARROWS) {
            append(convertWord(arrow, ON_SYMBOL_SELECTED, true, SYMBOL))
        }
        append(endDiv())

        append(startSection("extarrows", "Extensible Arrows"))
        for (arrow in EXTENSIBLE_ARROWS) {
            append(convertWord(arrow, ON_ACCENT_SELECTED, true, SYMBOL))
        }
        append(endDiv())

        append(startSection("logic_theory", "Logic and Set Theory"))
        append(endDiv())
    }
}

private fun endDiv(): String =
    """
    |</div>
    |</div>
    """.trimMargin()

private fun startSection(id: String, sectionName: String): String =
    """
    |<div class="section">
    |<div class="section-header" onclick="toggleSection('$id')">
    |$sectionName
    |</div>
    |<div id="$id" class="symbols-container">
    """.trimMargin()


private fun convertWord(
    word: String, function: String, backSlash: Boolean, containerType : String
    ): String {
    return if (backSlash) {
        val escaped = word.replace("\\\\", "")
        """
        |<div class="$containerType-item" onclick="Android.$function('$escaped')" data-code="$escaped" data-method="$function">
        |\\(\\$word\\)
        |</div>
        """.trimMargin()
    } else {
        val escaped = word.replace("\\\\", "")
        """
        |<div class="symbol-item" onclick="Android.$function('$escaped')" data-code="$escaped" data-method="$function">
        |\\($word\\)
        |</div>
        """.trimMargin()
    }
}
// onclick="Android.$function('$escaped')"