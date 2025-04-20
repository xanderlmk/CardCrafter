package com.belmontCrest.cardCrafter.supabase.view.importDeck

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import com.belmontCrest.cardCrafter.model.Type
import com.belmontCrest.cardCrafter.supabase.model.tables.SBDeckDto
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.views.miscFunctions.details.CardDetails
import com.belmontCrest.cardCrafter.views.miscFunctions.symbols.toShortHex

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun DisplayCardDetails(
    allCardDetails: List<Pair<CardDetails, String>>, getUIStyle: GetUIStyle,
    deck: SBDeckDto, onLoading: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val webView = remember { WebView(context) }
    val backgroundToHex = getUIStyle.background().toShortHex()
    val textToHex = getUIStyle.titleColor().toShortHex()
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
                        val allCardJs = allCardDetails.mapIndexed { index, it ->
                            """
                                |setCard('$index','${it.first.toHTML(it.second)}');
                            """.trimMargin()
                        }
                        val cardJs = buildString {
                            allCardJs.map {
                                append(it)
                            }
                        }

                        val allJs = """
                            |setTheme('$backgroundToHex','$textToHex');
                            |setDeckTitle('${deck.name}');
                            |setDescription('<p>${deck.description}</p>');
                            |$cardJs
                        """.trimMargin()
                        view.evaluateJavascript(allJs, null)
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

private const val line = "$$\\\\text{---}\\\\text{---}\\\\text{---}\\\\text{---}\\\\text{---}" +
        "\\\\text{---}\\\\text{---}\\\\text{---}$$"

fun CardDetails.toHTML(type: String): String {
    val cd = this
    val charMap = mapOf<Int, Char>(0 to 'a', 1 to 'b', 2 to 'c', 3 to 'd')
    return when (type) {
        Type.BASIC -> {
            val longString = buildString {
                append("<p>Question: ${cd.question.value}</p>")
                append(line)
                append("<p>Answer: ${cd.answer.value}</p>")
            }
            longString
        }

        Type.THREE -> {
            val longString = buildString {
                append("<p>Question: ${cd.question.value}</p>")
                append(line)
                append("<p>Middle: ${cd.middleField.value}</p>")
                append("<p>Answer: ${cd.answer.value}</p>")
            }
            longString
        }

        Type.HINT -> {
            val longString = buildString {
                append("<p>Question: ${cd.question.value}</p>")
                append(line)
                append("<p>Hint: ${cd.middleField.value}</p>")
                append("<p>Answer: ${cd.answer.value}</p>")
            }
            longString
        }

        Type.MULTI -> {
            val longString = buildString {
                append("<p>Question: ${cd.question.value}</p>")
                append(line)
                cd.choices.mapIndexed { index, it ->
                    if (it.value.isNotBlank()) {
                        val letter = charMap[index] ?: '?'
                        append("<p>$letter. ${it.value}</p>")
                    }
                    if (index == cd.choices.lastIndex) {
                        append(line)
                    }
                }
                append("<p>Answer: ${cd.correct.value}</p>")
            }
            longString
        }

        Type.NOTATION -> {
            val longString = buildString {
                append("<p>Question: ${cd.question.value}</p>")
                append(line)
                if (cd.stringList.isNotEmpty()) {
                    cd.stringList.mapIndexed { index, step ->
                        append("<p>Step ${index + 1}: ${step.value}</p>")
                        if (index == cd.stringList.lastIndex) {
                            append(line)
                        }
                    }
                }
                append("<p>Answer: ${cd.answer.value}</p>")
            }
            longString
        }

        else -> {
            throw IllegalStateException("Not a valid card type.")
        }
    }
}