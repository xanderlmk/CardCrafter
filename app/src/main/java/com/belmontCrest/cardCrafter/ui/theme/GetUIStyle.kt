package com.belmontCrest.cardCrafter.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.ColorScheme

/** Our UI class that will return the color
 * scheme as well as some custom colors
 * */
class GetUIStyle(
    private var cS: ColorSchemeClass,
    private var isDarkTheme: Boolean,
    private var isCustomTheme: Boolean,
    private var isCuteTheme: Boolean,
    private var isDarkCuteTheme: Boolean
) {
    fun getIsDarkTheme(): Boolean {
        return isDarkTheme
    }

    fun getIsCuteTheme(): Boolean {
        return isCuteTheme
    }

    private fun isDarkCuteTheme(): Boolean {
        return isDarkCuteTheme
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
        return when {
            isDarkCuteTheme -> darkCuteChoiceColor
            isCuteTheme -> cuteChoiceColor
            isDarkTheme -> {
                if (isCustomTheme) {
                    blendColors(cS.colorScheme.onTertiary, darkChoiceColor, 0.25f)
                } else {
                    darkChoiceColor
                }
            }
            else -> {
                if (isCustomTheme) {
                    blendColors(cS.colorScheme.onTertiary, choiceColor, 0.25f)
                } else {
                    choiceColor
                }
            }
        }
    }

    fun pickedChoice(): Color {
        return when {
            isDarkCuteTheme() -> darkCutePickedChoice
            isCuteTheme -> cutePickedChoice
            isDarkTheme -> darkPickedChoice
            else -> pickedChoice
        }
    }

    fun correctChoice(): Color {
        return when {
            isDarkCuteTheme() -> darkCuteCorrectChoice
            isCuteTheme -> cuteCorrectChoice
            isDarkTheme -> darkCorrectChoice
            else -> correctChoice
        }
    }

    fun onCorrectChoice(): Color {
        return when {
            isDarkCuteTheme() -> onDarkCuteCorrectChoice
            isCuteTheme -> onCuteCorrectChoice
            else -> cS.colorScheme.onSurfaceVariant
        }
    }

    fun isThemeOn(): Color {
        return if (isDarkTheme) {
            Color.White
        } else {
            darkBackground
        }
    }

    fun altBackground(): Color {
        return when {
            isDarkCuteTheme() -> darkCuteSecondaryBC
            isCuteTheme -> cuteSecondaryBC
            isDarkTheme -> secondaryDBC
            else -> secondaryBC
        }
    }

    fun background(): Color {
        return cS.colorScheme.background
    }

    fun navBarColor(): Color {
        return when {
            isDarkCuteTheme() -> darkCuteButton
            isCuteTheme -> cuteButton
            isDarkTheme -> {
                if (!isCustomTheme) {
                    darkNavBar
                } else {
                    cS.colorScheme.primaryContainer
                }
            }
            else -> cS.colorScheme.primaryContainer
        }
    }

    fun dialogColor(): Color {
        return when {
            isDarkCuteTheme() -> Color(70, 20, 50, 240)
            isCuteTheme -> Color(255, 230, 240, 240)
            isDarkTheme -> dialogDarkBackground
            else -> dialogLightBackground
        }
    }

    fun importingDeckColor(): Color {
        return when {
            isDarkCuteTheme() -> Color(100, 40, 80)
            isCuteTheme -> Color(255, 192, 203, 180)
            isDarkTheme -> Color.DarkGray
            else -> Color.Gray
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