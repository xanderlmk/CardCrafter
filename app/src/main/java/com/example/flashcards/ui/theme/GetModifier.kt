package com.example.flashcards.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp

class GetModifier(
    private var colorScheme: ColorSchemeClass,
    val clickedChoice : MutableState<Char> = mutableStateOf('?'),
) {
    fun mainViewModifier(): Modifier {
        return Modifier
            .fillMaxSize()
            .drawBehind {
                val strokeWidth = 3.dp.toPx()

                // Top border
                drawLine(
                    color = colorScheme.colorScheme.outline,
                    start = Offset(0f, strokeWidth / 2),
                    end = Offset(size.width, strokeWidth / 2),
                    strokeWidth = strokeWidth
                )

                // Bottom border
                drawLine(
                    color = colorScheme.colorScheme.outline,
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
            .padding(horizontal = 2.dp)
    }

    fun backButtonModifier(): Modifier {
        return Modifier
            .padding(16.dp)
            .size(50.dp)
    }

    fun addButtonModifier(): Modifier {
        return Modifier
            .fillMaxWidth()
            .background(colorScheme.colorScheme.background)
    }

    fun settingsButtonModifier(): Modifier {
        return Modifier
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            .size(40.dp)
    }
    fun mainSettingsButtonModifier(): Modifier {
        return Modifier
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            .size(24.dp)
    }

    fun boxViewsModifier(): Modifier {
        return Modifier
            .fillMaxSize()
            .padding(8.dp)
            .background(color = colorScheme.colorScheme.background)
    }

    fun bottomLineModifier(): Modifier {
        return Modifier
            .background(color = colorScheme.colorScheme.surface)
            .wrapContentWidth()
            .drawBehind {
                val strokeWidth = 3.dp.toPx()
                val y = size.height - strokeWidth / 2
                drawLine(
                    color = colorScheme.colorScheme.outline,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = strokeWidth
                )
            }

    }

    fun editCardModifier(): Modifier {
        return Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp)
            .wrapContentHeight(Alignment.CenterVertically)
            .wrapContentWidth(Alignment.CenterHorizontally)
    }

    fun buttonColor(): Color {
        return colorScheme.colorScheme.primaryContainer
    }
    fun iconColor() : Color {
        return colorScheme.colorScheme.onPrimaryContainer
    }
    fun secondaryButtonColor() : Color{
        return colorScheme.colorScheme.secondaryContainer
    }
    fun buttonTextColor() : Color {
        return colorScheme.colorScheme.onSecondaryContainer
    }
    fun titleColor() : Color{
        return colorScheme.colorScheme.onBackground
    }
    fun tertiaryButtonColor(): Color{
        return colorScheme.colorScheme.tertiaryContainer
    }
    fun onTertiaryButtonColor(): Color{
        return colorScheme.colorScheme.onTertiaryContainer
    }
    /**fun tertiaryColor(): Color{
        return colorScheme.colorScheme.tertiary
    }*/
    fun onTertiaryColor(): Color{
        return colorScheme.colorScheme.onTertiary
    }
    fun pickedChoice(): Color {
        return colorScheme.colorScheme.onSecondary
    }
    fun correctChoice(): Color {
        return colorScheme.colorScheme.onPrimary
    }
    fun onCorrectChoice(): Color {
        return colorScheme.colorScheme.onSurfaceVariant
    }
}