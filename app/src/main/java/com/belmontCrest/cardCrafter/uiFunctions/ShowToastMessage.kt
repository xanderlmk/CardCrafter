package com.belmontCrest.cardCrafter.uiFunctions

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.core.text.buildSpannedString


fun showToastMessage(
    context: Context,
    message: String,
    onNavigate: (() -> Unit)? = null,
    dismiss: MutableState<Boolean>? = null
) {
    Toast.makeText(
        context,
        message,
        Toast.LENGTH_SHORT
    ).show()
    onNavigate?.invoke()
    dismiss?.value = false
}

fun showToastMessage(
    context: Context, message: String, param: String,
    onNavigate: (() -> Unit)? = null, dismiss: MutableState<Boolean>? = null
) {
    val subText = message.substringBefore("$$**")
    val spannable = SpannableString(param)
    spannable.setSpan(
        StyleSpan(Typeface.BOLD), 0, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    val text = buildSpannedString {
        append(subText)
        append(spannable)
        append(message.substringAfter("$$**"))
    }
    Toast.makeText(
        context,
        text,
        Toast.LENGTH_SHORT
    ).show()
    onNavigate?.invoke()
    dismiss?.value = false
}