package com.belmontCrest.cardCrafter.supabase.view

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState


fun showToastMessage(
    context : Context,
    message : String,
    onNavigate : (() -> Unit)? = null,
    dismiss : MutableState<Boolean>? = null
) {
    Toast.makeText(
        context,
        message,
        Toast.LENGTH_SHORT
    ).show()
    onNavigate?.invoke()
    dismiss?.value = false
}