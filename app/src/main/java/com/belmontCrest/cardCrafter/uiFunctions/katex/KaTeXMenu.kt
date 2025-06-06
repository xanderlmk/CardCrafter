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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.belmontCrest.cardCrafter.model.getKatexMenuWidth
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import kotlinx.parcelize.Parcelize
import kotlin.math.roundToInt


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

//private const val KATEX_MENU = "KatexMenu"

@OptIn(ExperimentalStdlibApi::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun KaTeXMenu(
    modifier: Modifier, offsetProvider: () -> Offset, onDismiss: () -> Unit,
    onOffset: (Offset) -> Unit, getUIStyle: GetUIStyle, //initialPos: Offset?,
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
                    val list = buildKeyboardHtml()
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
    /*
        val maxY = getMaxHeight().value
        val maxX = getMaxWidth().value
        val isLandscape = getIsLandScape()
        val midXPoint = getKatexMenuWidth().value / 2

        LaunchedEffect(Unit) {
            Log.i(KATEX_MENU, "max width: $maxX")
            Log.i(KATEX_MENU, "max height: $maxY")
        }*/


    Box(
        modifier = modifier
            .offset { IntOffset(offsetProvider().x.roundToInt(), offsetProvider().y.roundToInt()) }
    ) {
        Box(
            modifier = Modifier
                .width(getKatexMenuWidth())
                .height(26.dp)
                .background(getUIStyle.katexMenuHeaderColor())
                // 4. Only after a long‐press on this bar do we start dragging
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        /* val pos = initialPos
                         if (pos != null) {
                             val currentY = offsetProvider().y - pos.y + dragAmount.y
                             val currentX = offsetProvider().x - pos.x + dragAmount.x
                             if (isOutsideOfBounds(
                                     isLandscape, pos = pos, midXPoint = midXPoint,
                                     maxY = maxY, maxX = maxX,
                                     currentY = currentY, currentX = currentX
                                 )
                             ) {
                                 Log.w(KATEX_MENU, "Out of bounds")
                                 return@detectDragGestures
                             }
                             onOffset(dragAmount)
                         }*/
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
            ) { Icon(Icons.Filled.Close, contentDescription = null) }

        }
        AndroidView(
            factory = { webView },
            modifier = Modifier
                .width(getKatexMenuWidth())
                .padding(top = 26.dp)
                .height(200.dp)
                .background(getUIStyle.katexMenuBGColor())
                .border(1.5.dp, getUIStyle.defaultIconColor())
                .verticalScroll(rememberScrollState()),
        )
    }

}

/**
 * Check whether the box is out of bounds
 * @param isLandScape Whether or not the device is in portrait or landscape mode
 * @param pos The initial global position offset relative to window
 * @param maxX The max X value of the screen
 * @param maxY The max Y value of the screen
 * @param currentY Current Y value of the offset in the Katex Menu.
 * @param currentX Current X value of the offset in the Katex Menu
 * @param midXPoint Midpoint of the width in the Katex Menu
 */
@Suppress("unused")
private fun isOutsideOfBounds(
    isLandScape: Boolean,
    pos: Offset, maxY: Float, maxX: Float,
    currentY: Float, currentX: Float,
    midXPoint: Float
): Boolean {
    return if (!isLandScape) {
        val bottom = -pos.y - 26.dp.value - (pos.y / 3)
        val top = maxY - (200.dp.value)
        val right = maxX - midXPoint
        val left = -(pos.x * 2) - 6.dp.value
        /*Log.i(
            KATEX_MENU,
            "PositionY: $currentY :: LowerBoundY: $bottom :: HigherBoundY: $top"
        )*/
        currentY >= top || // ✅
                currentY <= bottom || // ✅
                currentX >= right || // ✅
                currentX <= left // ✅

    } else {
        val bottom = -pos.y - (pos.y / 2)
        val top = maxY - (200.dp.value) - pos.y
        val right = maxX - pos.x
        val left = -(pos.x * 2) - 6.dp.value
        /*Log.i(
            KATEX_MENU,
            "PositionY: $currentY :: LowerBoundY: $bottom :: HigherBoundY: $top"
        )*/
        currentY >= top || // ✅
                currentY <= bottom || // ✅
                currentX >= right || // ✅
                currentX <= left // ✅
    }
}