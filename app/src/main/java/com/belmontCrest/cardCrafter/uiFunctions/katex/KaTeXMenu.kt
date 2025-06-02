package com.belmontCrest.cardCrafter.uiFunctions.katex

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.util.Log
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import kotlinx.parcelize.Parcelize
import kotlin.math.roundToInt


private val ACCENTS = listOf(
    "tilde{a}", "mathring{g}", "widetilde{ac}", "utilde{AB}", "vec{F}",
    "overleftarrow{AB}", "underleftarrow{AB}"
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

@Parcelize
data class KaTeXMenu(val notation: String?, val sa: SelectedAnnotation) : Parcelable

@Parcelize
sealed class SelectedAnnotation : Parcelable {
    @Parcelize
    data object Letter : SelectedAnnotation()
    @Parcelize
    data object Accent : SelectedAnnotation()
    @Parcelize
    data object Idle : SelectedAnnotation()
}

@OptIn(ExperimentalStdlibApi::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun KaTeXMenu(
    modifier: Modifier, offset: Offset, onDismiss: () -> Unit,
    onOffset: (Offset) -> Unit, getUIStyle: GetUIStyle,
    onSelectNotation: (String, SelectedAnnotation) -> Unit,
) {
    val context = LocalContext.current
    val textToHex = getUIStyle.titleColor().toShortHex()
    val webView = remember {
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
                    Handler(Looper.getMainLooper()).post {
                        onSelectNotation("\\\\$symbol", SelectedAnnotation.Letter)
                    }
                }
            }, "Android")
            addJavascriptInterface(object {
                @JavascriptInterface
                fun onAccentSelected(accent: String) {
                    Handler(Looper.getMainLooper()).post {
                        onSelectNotation("\\\\$accent", SelectedAnnotation.Accent)
                    }
                }
            }, "Accent")
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

    DisposableEffect(webView) {
        onDispose {
            try {
                webView.destroy()
            } catch (e: Exception) {
                Log.w("KatexMenu", "Failed to destroy WebView: $e")
            }
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
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                color = getUIStyle.titleColor()
            )
            IconButton(
                onClick = { onDismiss() },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = null
                )
            }

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
