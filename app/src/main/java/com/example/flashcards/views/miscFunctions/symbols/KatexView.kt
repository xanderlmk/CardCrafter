package com.example.flashcards.views.miscFunctions.symbols

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import com.example.flashcards.ui.theme.GetUIStyle

@OptIn(ExperimentalStdlibApi::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun KaTeXWebView(latexExpression: String, getUIStyle: GetUIStyle) {
    val context = LocalContext.current
    val webView = remember { WebView(context) }

    val backgroundToHex = getUIStyle.background().toShortHex()
    val textToHex = getUIStyle.titleColor().toShortHex()
    AndroidView(
        factory = {
            webView.apply {
                setBackgroundColor(getUIStyle.background().toArgb())
            }
        }, modifier = Modifier
            .fillMaxWidth()
            .zIndex(-1f)
    ) { view ->
        view.loadUrl("file:///android_asset/katex.html")
        with(view.settings) {
            javaScriptEnabled = true
        }
        view.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                val themeJs = "setTheme('$backgroundToHex','$textToHex');"
                view.evaluateJavascript(themeJs, null)
                val insertLatexJs = """
                    | document.getElementById('katex-render').innerHTML = `$latexExpression`;
                    | renderMathInElement(document.body);
                """.trimMargin()
                view.evaluateJavascript(insertLatexJs, null)
            }
        }
    }
}


fun Color.toShortHex(): String {
    val argb = this.toArgb() // ARGB as 0xAARRGGBB
    val alpha = (argb shr 24) and 0xFF
    val red = (argb shr 16) and 0xFF
    val green = (argb shr 8) and 0xFF
    val blue = argb and 0xFF
    // If alpha is fully opaque, omit it from the hex
    return if (alpha < 255) {
        "#%02X%02X%02X%02X".format(alpha, red, green, blue)
    } else {
        "#%02X%02X%02X".format(red, green, blue)
    }
}

