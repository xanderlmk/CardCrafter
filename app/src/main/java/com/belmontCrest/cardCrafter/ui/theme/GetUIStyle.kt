package com.belmontCrest.cardCrafter.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.ColorScheme

/** Our UI class that will return the color
 * scheme as well as some custom colors
 * */
class GetUIStyle(
    private var colorScheme: ColorSchemeClass,
    private var isDarkTheme: Boolean,
) {
    fun getIsDarkTheme(): Boolean {
        return isDarkTheme
    }

    fun getColorScheme(): ColorScheme {
        return colorScheme.colorScheme
    }

    fun buttonColor(): Color {
        return colorScheme.colorScheme.primaryContainer
    }

    fun iconColor(): Color {
        return colorScheme.colorScheme.onPrimaryContainer
    }

    fun secondaryButtonColor(): Color {
        return colorScheme.colorScheme.secondaryContainer
    }

    fun buttonTextColor(): Color {
        return colorScheme.colorScheme.onSecondaryContainer
    }

    fun titleColor(): Color {
        return colorScheme.colorScheme.onBackground
    }

    fun tertiaryButtonColor(): Color {
        return colorScheme.colorScheme.tertiaryContainer
    }

    fun onTertiaryButtonColor(): Color {
        return colorScheme.colorScheme.onTertiaryContainer
    }
    fun choiceColor(): Color {
        return if (isDarkTheme) {
            darkChoiceColor
        } else {
            choiceColor
        }
    }
    fun pickedChoice(): Color {
        return if (isDarkTheme) {
            darkPickedChoice
        } else {
            pickedChoice
        }
    }

    fun correctChoice(): Color {
        return if (isDarkTheme) {
            darkCorrectChoice
        } else {
            correctChoice
        }
    }

    fun onCorrectChoice(): Color {
        return colorScheme.colorScheme.onSurfaceVariant
    }

    fun isThemeOn(): Color {
        return if (isDarkTheme) {
            Color.White
        } else {
            darkBackground
        }
    }

    fun altBackground(): Color {
        return if (isDarkTheme) {
            secondaryDBC
        } else {
            secondaryBC
        }
    }

    fun background(): Color {
        return colorScheme.colorScheme.background
    }
}


