package com.belmontCrest.cardCrafter.uiFunctions

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.uiFunctions.symbols.toShortHex
import kotlin.math.roundToInt


private val GREEK_LETTERS = listOf(
    "Alpha", "Beta", "Epsilon",
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

private val OTHER_LETTERS = listOf(
    "imath", "nabla", "Im", "Reals", "text{\\\\OE}",
    "jmath", "partial", "image", "wp", "text{\\\\o}",
    "aleph", "Game", "Bbbk", "weierp", "text{\\\\O}",
    "alef", "Finv"
)


@OptIn(ExperimentalStdlibApi::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun KaTeXMenu(
    modifier: Modifier, offset: Offset,
    onOffset: (Offset) -> Unit, getUIStyle: GetUIStyle,
    onSelectNotation: (String) -> Unit
) {
    val context = LocalContext.current
    val textToHex = getUIStyle.titleColor().toShortHex()
    val webView = remember {
        WebView(context).apply {
            setBackgroundColor(getUIStyle.katexMenuBGColor().toArgb())
            settings.javaScriptEnabled = true
            // expose a Kotlin callback under “Android” in JS
            addJavascriptInterface(object {
                @Suppress("unused")
                @JavascriptInterface
                fun onSymbolSelected(symbol: String) {
                    Handler(Looper.getMainLooper()).post {
                        onSelectNotation("\\\\$symbol")
                    }
                }
            }, "Android")
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    val themeJs = "setTheme('$textToHex');"
                    view.evaluateJavascript(themeJs, null)
                    val list = buildString {
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
                        |</div>""".trimMargin()
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
                        |</div>""".trimMargin()
                        )
                    }
                    val insertLatexJs = """
                    | document.getElementById('list').innerHTML = `$list`;
                    | renderMathInElement(document.body);
                """.trimMargin()
                    view.evaluateJavascript(insertLatexJs, null)
                }
            }
            loadUrl("file:///android_asset/katex-menu.html")
        }
    }


    Box(
        modifier = modifier
            .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(26.dp)
                .background(getUIStyle.katexMenuHeaderColor())
                // 4. Only after a long‐press on this bar do we start dragging
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        onOffset(dragAmount)
                    }
                }
                .border(1.5.dp, getUIStyle.defaultIconColor())
        ) {
            Text(
                text = "Drag here",
                modifier = Modifier.align(Alignment.Center),
                fontSize = 16.sp,
                color = getUIStyle.titleColor()
            )
        }
        AndroidView(
            factory = { webView },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 26.dp)
                .height(200.dp)
                .background(getUIStyle.katexMenuBGColor())
                .border(1.5.dp, getUIStyle.defaultIconColor())
                .verticalScroll(rememberScrollState()),
        )
    }
}
