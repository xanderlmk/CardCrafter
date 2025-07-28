package com.belmontCrest.cardCrafter.supabase.view.importDeck

import android.annotation.SuppressLint
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import com.belmontCrest.cardCrafter.model.Type
import com.belmontCrest.cardCrafter.model.ui.states.CDetails
import com.belmontCrest.cardCrafter.supabase.model.tables.SBDeckDto
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.uiFunctions.katex.toShortHex


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun DisplayCardDetails(
    allCardDetails: List<Pair<CDetails, String>>, getUIStyle: GetUIStyle,
    deck: SBDeckDto, onLoading: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val webView = remember { WebView(context) }
    val backgroundToHex = getUIStyle.background().toShortHex()
    val textToHex = getUIStyle.titleColor().toShortHex()
    val borderColor = getUIStyle.themedColor().toShortHex()
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
                loadUrl("file:///android_asset/deck_content.html")
                setBackgroundColor(getUIStyle.background().toArgb())
                settings.javaScriptEnabled = true
                settings.allowFileAccess = true
                this.webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView, url: String) {
                        super.onPageFinished(view, url)
                        view.evaluateJavascript("setTheme('$backgroundToHex','$textToHex');", null)

                        // deck title
                        view.evaluateJavascript(
                            """
                        |document.getElementById('deckTitle').innerText = `${deck.name}`;
                        """.trimMargin(), null
                        )

                        // description
                        view.evaluateJavascript(
                            """
                        |document.getElementById('description').innerHTML = `${deck.description}`;
                        |renderNotation(document.getElementById('description'));
                        """.trimMargin(), null
                        )

                        // up to four cards
                        allCardDetails.forEachIndexed { i, (cd, type) ->
                            val html = cd.toHTML(type)
                            view.evaluateJavascript(
                                """
                            |document.getElementById('card$i').innerHTML = `$html`;
                            |renderNotation(document.getElementById('card$i'));
                            """.trimMargin(), null
                            )
                        }
                        view.evaluateJavascript("setBorderColor('$borderColor');", null)
                        onLoading(false)
                    }
                }
            }
        },
        update = {
            it.loadUrl("file:///android_asset/deck_content.html")
        },
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(-1f),
    )
}

private const val line = "$$\\\\text{---------------------------}$$"

fun CDetails.toHTML(type: String): String {
    val cd = this
    val charMap = mapOf(0 to 'a', 1 to 'b', 2 to 'c', 3 to 'd')
    return when (type) {
        Type.BASIC -> {
            val longString = buildString {
                append("<p>Question: ${cd.question}</p>")
                append(line)
                append("<p>Answer: ${cd.answer}</p>")
            }
            longString
        }

        Type.THREE -> {
            val longString = buildString {
                append("<p>Question: ${cd.question}</p>")
                append(line)
                append("<p>Middle: ${cd.middle}</p>")
                append("<p>Answer: ${cd.answer}</p>")
            }
            longString
        }

        Type.HINT -> {
            val longString = buildString {
                append("<p>Question: ${cd.question}</p>")
                append(line)
                append("<p>Hint: ${cd.middle}</p>")
                append("<p>Answer: ${cd.answer}</p>")
            }
            longString
        }

        Type.MULTI -> {
            val longString = buildString {
                append("<p>Question: ${cd.question}</p>")
                append(line)
                cd.choices.mapIndexed { index, it ->
                    if (it.isNotBlank()) {
                        val letter = charMap[index] ?: '?'
                        append("<p>$letter. ${it}</p>")
                    }
                    if (index == cd.choices.lastIndex) {
                        append(line)
                    }
                }
                append("<p>Answer: ${cd.correct}</p>")
            }
            longString
        }

        Type.NOTATION -> {
            val longString = buildString {
                append("<p>Question: ${cd.question}</p>")
                append(line)
                if (cd.steps.isNotEmpty()) {
                    cd.steps.mapIndexed { index, step ->
                        append("<p>Step ${index + 1}: ${step}</p>")
                        if (index == cd.steps.lastIndex) {
                            append(line)
                        }
                    }
                }
                append("<p>Answer: ${cd.answer}</p>")
            }
            longString
        }

        else -> {
            throw IllegalStateException("Not a valid card type.")
        }
    }
}