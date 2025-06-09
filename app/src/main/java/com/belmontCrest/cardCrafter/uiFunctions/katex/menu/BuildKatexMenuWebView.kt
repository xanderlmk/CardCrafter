package com.belmontCrest.cardCrafter.uiFunctions.katex.menu

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.uiFunctions.katex.toShortHex

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun getWebView(
    getUIStyle: GetUIStyle, onSelectNotation: (String, SelectedAnnotation) -> Unit
): WebView {
    val context = LocalContext.current
    val textToHex = getUIStyle.titleColor().toShortHex()
    return remember {
        @Suppress("unused")
        WebView(context).apply {
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
                        val replaced = sel.replace("l", "\\\\l").replace("gt", "\\\\gt")
                            .replace("sqrt", "\\\\sqrt").replace("pi", "\\\\pi")
                            .replace("ti", "\\\\ti").replace("div", "\\\\div").replace("a", "")
                            .replace("{x}", "{}").replace("b", "")
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
}