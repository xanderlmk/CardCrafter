package com.belmontCrest.cardCrafter.uiFunctions.katex

import android.annotation.SuppressLint
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle

@OptIn(ExperimentalStdlibApi::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun KaTeXWebView(latexExpression: String, getUIStyle: GetUIStyle, modifier: Modifier) {
    val context = LocalContext.current
    val webView = remember { WebView(context) }

    val textToHex = getUIStyle.titleColor().toShortHex()

    DisposableEffect(webView) {
        onDispose {
            try {
                webView.destroy()
            } catch (e: Exception) {
                Log.w("KatexMenu", "Failed to destroy WebView: $e")
            }
        }
    }

    AndroidView(
        factory = {
            webView.apply {
                settings.javaScriptEnabled = true
                setBackgroundColor(getUIStyle.background().toArgb())
            }
        },
        modifier = modifier,
    ) { view ->
        view.loadUrl("file:///android_asset/katex.html")
        view.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                val themeJs = "setTheme('$textToHex');"
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

