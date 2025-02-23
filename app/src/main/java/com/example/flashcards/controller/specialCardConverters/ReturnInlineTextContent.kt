package com.example.flashcards.controller.specialCardConverters

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.unit.sp
import com.example.flashcards.ui.theme.GetModifier
import com.example.flashcards.views.miscFunctions.symbols.Fraction

fun inlineTextContentForFraction(
    numerator: String,
    denominator: String,
    getModifier: GetModifier
): InlineTextContent {
    return if (numerator.length > 8 || denominator.length > 8) {
        InlineTextContent(
            Placeholder(96.sp, 48.sp, PlaceholderVerticalAlign.TextCenter)
        ) {
            Fraction(numerator, denominator, getModifier)
        }
    } else if (numerator.length > 3 || denominator.length > 3) {
        InlineTextContent(
            Placeholder(64.sp, 48.sp, PlaceholderVerticalAlign.TextCenter)
        ) {
            Fraction(numerator, denominator, getModifier)
        }
    } else {
        InlineTextContent(
            Placeholder(32.sp, 48.sp, PlaceholderVerticalAlign.TextCenter)
        ) {
            Fraction(numerator, denominator, getModifier)
        }
    }
}

fun returnLineSize(numSize: Int, denoSize: Int): Int {
    return if (numSize > 8 || denoSize > 8) {
        2
    } else if (numSize > 3 || denoSize > 3) {
        1
    } else {
        0
    }
}