package com.example.flashcards.views.miscFunctions

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.example.flashcards.ui.theme.ColorSchemeClass

class GetModifier(
    private var colorScheme: ColorSchemeClass) {
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
            .size(54.dp)
    }

    fun addButtonModifier(): Modifier {
        return Modifier
            .fillMaxWidth()
            .background(colorScheme.colorScheme.background)
    }

    fun settingsButtonModifier(): Modifier {
        return Modifier
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            .size(44.dp)
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
}