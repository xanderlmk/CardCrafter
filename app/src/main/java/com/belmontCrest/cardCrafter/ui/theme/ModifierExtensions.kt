package com.belmontCrest.cardCrafter.ui.theme

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp

/**
 *
 * Creating extension functions for Modifier variable
 *
 * */

fun Modifier.borderedModifier(getUIStyle: GetUIStyle): Modifier {
    return this
            .border(2.dp, getUIStyle.defaultIconColor(), RoundedCornerShape(12.dp))
}

fun Modifier.mainViewModifier(colorScheme : ColorScheme): Modifier {
    return this
        .fillMaxSize()
        .drawBehind {
            val strokeWidth = 3.dp.toPx()
            // Top border
            drawLine(
                color = colorScheme.outline,
                start = Offset(0f, strokeWidth / 2),
                end = Offset(size.width, strokeWidth / 2),
                strokeWidth = strokeWidth
            )
            // Bottom border
            drawLine(
                color = colorScheme.outline,
                start = Offset(
                    0f,
                    size.height - strokeWidth / 2
                ),
                end = Offset(
                    size.width,
                    size.height - strokeWidth / 2
                ),
                strokeWidth = strokeWidth
            )
        }
        .padding(vertical = 4.dp)
}

fun Modifier.backButtonModifier(): Modifier {
    return this
        .size(40.dp)
}

fun Modifier.redoButtonModifier(): Modifier {
    return this
        .padding(10.dp)
        .size(40.dp)
}

fun Modifier.addButtonModifier(colorScheme: ColorScheme): Modifier {
    return this
        .fillMaxWidth()
        .background(colorScheme.background)
}

fun Modifier.settingsButtonModifier(): Modifier {
    return this
        .size(40.dp)
}

fun Modifier.boxViewsModifier(colorScheme: ColorScheme): Modifier {
    return this
        .fillMaxSize()
        .padding(8.dp)
        .background(color = colorScheme.background)
}

fun Modifier.scrollableBoxViewModifier(scrollState: ScrollState, colorScheme: ColorScheme): Modifier {
    return this
        .fillMaxSize()
        .verticalScroll(scrollState)
        .fillMaxSize()
        .padding(8.dp)
        .background(color = colorScheme.background)
}


fun Modifier.editCardModifier(): Modifier {
    return this
        .fillMaxWidth()
        .padding(start = 10.dp, end = 10.dp)
        .wrapContentHeight(Alignment.CenterVertically)
        .wrapContentWidth(Alignment.CenterHorizontally)
}

fun Modifier.generalSettingsOptionsModifier(colorScheme: ColorScheme) : Modifier{
    return this
        .fillMaxWidth()
        .padding(horizontal = 8.dp)
        .drawBehind {
            val strokeWidth = 2.dp.toPx()
            // Top border
            drawLine(
                color = colorScheme.outline,
                start = Offset(0f, strokeWidth / 2),
                end = Offset(size.width, strokeWidth / 2),
                strokeWidth = strokeWidth
            )
            // Bottom border
            drawLine(
                color = colorScheme.outline,
                start = Offset(
                    0f,
                    size.height - strokeWidth / 2
                ),
                end = Offset(
                    size.width,
                    size.height - strokeWidth / 2
                ),
                strokeWidth = strokeWidth
            )
        }
        .padding(vertical = 4.dp)
}