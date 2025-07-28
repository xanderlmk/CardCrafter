package com.belmontCrest.cardCrafter.uiFunctions.katex.menu

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient

import androidx.compose.ui.graphics.toArgb
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.uiFunctions.katex.toShortHex

@SuppressLint("SetJavaScriptEnabled")
@Suppress("unused")
fun getWebView(
    getUIStyle: GetUIStyle, context: Context, onSelectNotation: (String, SelectedAnnotation) -> Unit
): WebView {
    val textToHex = getUIStyle.titleColor().toShortHex()
    return WebView(context).apply {
        isFocusable = false
        isFocusableInTouchMode = false
        setBackgroundColor(getUIStyle.katexMenuBGColor().toArgb())
        settings.javaScriptEnabled = true
        // expose a Kotlin callback under “Android” in JS
        addJavascriptInterface(object {
            @JavascriptInterface
            fun onSymbolSelected(symbol: String) {
                Handler(Looper.getMainLooper()).postAtFrontOfQueue {
                    onSelectNotation("\\\\$symbol", SelectedAnnotation.Letter)
                }
            }

            @JavascriptInterface
            fun onAccentSelected(accent: String) {
                Handler(Looper.getMainLooper()).postAtFrontOfQueue {
                    val replaced = accent.replace("theta", "\\\\theta")
                    onSelectNotation("\\\\$replaced", SelectedAnnotation.Accent)
                }
            }

            @JavascriptInterface
            fun onFracBinoSelected(eq: String) {
                Handler(Looper.getMainLooper()).postAtFrontOfQueue {
                    val replaced = eq.replace("bra", "\\\\bra").replace("frac", "\\\\frac")
                        .replace("gen", "\\\\gen").replace("bin", "\\\\bin")
                    onSelectNotation(replaced, SelectedAnnotation.EQ)
                }
            }

            @JavascriptInterface
            fun onOPSelected(op: String) {
                Handler(Looper.getMainLooper()).postAtFrontOfQueue {
                    val replaced =
                        if (op == "sum_{substack{0lt{i}lt{m}0lt{j}lt{n}}}")
                            "sum_{\\\\substack{0\\\\lt{i}\\\\lt{m}\\\\\\\\0\\\\lt{j}\\\\lt{n}}}"
                        else op

                    onSelectNotation("\\\\$replaced", SelectedAnnotation.OP)
                }
            }

            @JavascriptInterface
            fun onNormSelected(sel: String) {
                Handler(Looper.getMainLooper()).postAtFrontOfQueue {
                    val replaced = sel.convertNormSel()
                    onSelectNotation(replaced, SelectedAnnotation.NORM)
                }
            }
        }, "Android")
        webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                val themeJs = "setTheme('$textToHex');"
                view.evaluateJavascript(themeJs, null)
                val list = buildKeyboardHtml()
                val insertLatexJs = """
                    | document.getElementById('list').innerHTML = `$list`;
                    | const list = document.getElementById('list');
                    | renderMathInElement(list, {
                    |   delimiters: [
                    |       {left: "$$", right: "$$", display: true},
                    |       {left: "$$", right: "$$", display: false},
                    |       {left: "\\(", right: "\\)", display: false},
                    |   ]
                    | });
                    | list.addEventListener('click', e => {
                    |   const item = e.target.closest('.symbol-item');
                    |   if (!item) return;
                    |       const code = item.dataset.code;
                    |       const method = item.dataset.method;
                    |       switch (method) {
                    |           case 'onSymbolSelected':
                    |               Android.onSymbolSelected(code); break;
                    |           case 'onAccentSelected':
                    |               Android.onAccentSelected(code); break;
                    |           case 'onFracBinoSelected':
                    |               Android.onFracBinoSelected(code); break;
                    |           case 'onOPSelected':
                    |               Android.onOPSelected(code); break;
                    |       }
                    |   });
                """.trimMargin()
                view.evaluateJavascript(insertLatexJs, null)
            }
        }
        loadUrl("file:///android_asset/katex-menu.html")
    }

}

private val latexNormMap = mapOf(
    // comparisons
    "lt" to "\\\\lt",
    "gt" to "\\\\gt",
    "le" to "\\\\le",
    "ge" to "\\\\ge",

    // fractions & roots
    "times" to "\\\\times",
    "div" to "\\\\div",
    "sqrt{x}" to "\\\\sqrt{}",
    "sqrt[a]{x}" to "\\\\sqrt[]{}",

    // constants
    "pi" to "\\\\pi",

    // exponents & letters
    "a^2" to "^2",
    "a^{b}" to "^{}",
    "|a|" to "||",

    // punctuation & parens
    "(" to "(",
    ")" to ")",
    "," to ",",
    "." to ".",
    "=" to "=",
    "{}" to "{}",

    // digits
    "0" to "0", "1" to "1", "2" to "2", "3" to "3", "4" to "4",
    "5" to "5", "6" to "6", "7" to "7", "8" to "8", "9" to "9"
)


private fun String.convertNormSel(): String = latexNormMap[this] ?: this