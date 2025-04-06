package com.belmontCrest.cardCrafter.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.ColorScheme

/** Our UI class that will return the color
 * scheme as well as some custom colors
 * */
class GetUIStyle(
    private var cS: ColorSchemeClass,
    private var isDarkTheme: Boolean,
    private var isCustomTheme: Boolean
) {
    fun getIsDarkTheme(): Boolean {
        return isDarkTheme
    }

    fun getColorScheme(): ColorScheme {
        return cS.colorScheme
    }

    fun buttonColor(): Color {
        return cS.colorScheme.primaryContainer
    }

    fun iconColor(): Color {
        return cS.colorScheme.onPrimaryContainer
    }

    fun secondaryButtonColor(): Color {
        return cS.colorScheme.secondaryContainer
    }

    fun buttonTextColor(): Color {
        return cS.colorScheme.onSecondaryContainer
    }

    fun titleColor(): Color {
        return cS.colorScheme.onBackground
    }

    fun tertiaryButtonColor(): Color {
        return cS.colorScheme.tertiaryContainer
    }

    fun onTertiaryButtonColor(): Color {
        return cS.colorScheme.onTertiaryContainer
    }

    fun choiceColor(): Color {
        return if (isDarkTheme) {
            if (isCustomTheme) {
                blendColors(cS.colorScheme.onTertiary, darkChoiceColor, 0.25f)
            } else {
                darkChoiceColor
            }
        } else {
            if (isCustomTheme) {
                blendColors(cS.colorScheme.onTertiary, choiceColor, 0.25f)
            } else {
                choiceColor
            }
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
        return cS.colorScheme.onSurfaceVariant
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
        return cS.colorScheme.background
    }

    fun navBarColor(): Color {
        return if (isDarkTheme) {
            if (!isCustomTheme) {
                darkNavBar
            } else {
                cS.colorScheme.primaryContainer
            }
        } else {
            cS.colorScheme.primaryContainer
        }
    }

    fun dialogColor() : Color {
        return if(isDarkTheme){
            dialogDarkBackground
        } else {
            dialogLightBackground
        }
    }

    fun importingDeckColor() : Color {
        return if (isDarkTheme) {
            Color.DarkGray
        } else {
            Color.Gray
        }
    }

    fun blendColors(base: Color, overlay: Color, overlayAlpha: Float): Color {
        // overlayAlpha in [0..1]
        val r = overlay.red * overlayAlpha + base.red * (1 - overlayAlpha)
        val g = overlay.green * overlayAlpha + base.green * (1 - overlayAlpha)
        val b = overlay.blue * overlayAlpha + base.blue * (1 - overlayAlpha)
        val a = overlay.alpha * overlayAlpha + base.alpha * (1 - overlayAlpha)
        return Color(r, g, b, a)
    }
}


