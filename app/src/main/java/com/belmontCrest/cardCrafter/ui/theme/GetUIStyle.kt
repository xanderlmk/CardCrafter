package com.belmontCrest.cardCrafter.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.ColorScheme

/** Our UI class that will return the color
 * scheme as well as some custom colors
 * */
class GetUIStyle(
    private var cS: ColorSchemeClass,
    private var isDarkTheme: Boolean,
    private var isDynamicTheme: Boolean,
    private var isCuteTheme: Boolean,
) {
    fun getIsDarkTheme(): Boolean = isDarkTheme

    fun getIsCuteTheme(): Boolean = isCuteTheme

    /** if (isDarkTheme && isCuteTheme) return true */
    private fun ifDarkAndCute(): Boolean = isDarkTheme && isCuteTheme

    /** if (isDarkTheme && !isCuteTheme) return true */
    private fun ifDarkAndNotCute(): Boolean = isDarkTheme && !isCuteTheme

    /** if (!isDarkTheme && isCuteTheme) return true */
    private fun ifNotDarkAndCute(): Boolean = !isDarkTheme && isCuteTheme

    fun getColorScheme(): ColorScheme = cS.colorScheme

    fun buttonColor(): Color = cS.colorScheme.primaryContainer

    fun semiTransButtonColor(): Color = when {
        ifDarkAndCute() -> semiTransDarkCuteButton
        ifDarkAndNotCute() -> semiTransDarkButton
        ifNotDarkAndCute() -> semiTransCuteButton
        else -> semiTransButtonColor
    }

    fun iconColor(): Color = cS.colorScheme.onPrimaryContainer

    fun secondaryButtonColor(): Color = cS.colorScheme.secondaryContainer

    fun buttonTextColor(): Color = cS.colorScheme.onSecondaryContainer

    fun titleColor(): Color = cS.colorScheme.onBackground


    fun disabledTextColor(): Color = if (isDarkTheme) darkDisabled else lightDisabled

    fun tertiaryButtonColor(): Color = cS.colorScheme.tertiaryContainer

    fun onTertiaryButtonColor(): Color = cS.colorScheme.onTertiaryContainer

    fun choiceColor(): Color = when {
        isDarkTheme -> {
            if (isDynamicTheme) {
                blendColors(cS.colorScheme.onTertiary, darkChoiceColor, 0.25f)
            } else {
                if (isCuteTheme) {
                    darkCuteChoiceColor
                } else {
                    darkChoiceColor
                }
            }
        }

        else -> {
            if (isDynamicTheme) {
                blendColors(cS.colorScheme.onTertiary, choiceColor, 0.25f)
            } else {
                if (isCuteTheme) {
                    cuteChoiceColor
                } else {
                    choiceColor
                }
            }
        }
    }

    fun pickedChoice(): Color = when {
        ifDarkAndCute() -> darkCutePickedChoice
        ifDarkAndNotCute() -> darkPickedChoice
        ifNotDarkAndCute() -> cutePickedChoice
        else -> pickedChoice
    }

    fun correctChoice(): Color = when {
        ifDarkAndCute() -> darkCuteCorrectChoice
        ifDarkAndNotCute() -> darkCorrectChoice
        ifNotDarkAndCute() -> cuteCorrectChoice
        else -> correctChoice
    }


    fun onCorrectChoice(): Color = when {
        ifDarkAndCute() -> onDarkCuteCorrectChoice
        ifNotDarkAndCute() -> onCuteCorrectChoice
        else -> cS.colorScheme.onSurfaceVariant
    }

    fun isThemeOn(): Color = if (isDarkTheme) {
        Color.White
    } else {
        darkBackground
    }

    fun altBackground(): Color = when {
        isDarkTheme -> {
            if (isCuteTheme) {
                darkCuteSecondaryBC
            } else {
                secondaryDBC
            }
        }

        else -> {
            if (isCuteTheme) {
                cuteSecondaryBC
            } else {
                secondaryBC
            }
        }
    }

    fun background(): Color = cS.colorScheme.background

    fun navBarColor(): Color = when {
        isDarkTheme -> {
            if (!isDynamicTheme) {
                if (isCuteTheme) {
                    darkCuteButton
                } else {
                    darkNavBar
                }
            } else {
                cS.colorScheme.primaryContainer
            }
        }

        else -> {
            if (isCuteTheme && !isDynamicTheme) {
                cuteButton
            } else {
                cS.colorScheme.primaryContainer
            }
        }
    }

    fun dialogColor(): Color = when {
        ifDarkAndCute() -> Color(70, 20, 50, 240)
        ifDarkAndNotCute() -> dialogDarkBackground
        ifNotDarkAndCute() -> Color(255, 230, 240, 240)
        else -> dialogLightBackground
    }

    fun importingDeckColor(): Color = when {
        isDarkTheme -> {
            if (isCuteTheme) {
                Color(100, 40, 80)
            } else {
                Color.DarkGray
            }
        }

        else -> {
            if (isCuteTheme) {
                Color(255, 192, 203, 180)
            } else {
                Color.Gray
            }
        }
    }

    fun defaultIconColor(): Color = if (isDarkTheme) {
        Color.White
    } else {
        Color.Black
    }

    fun katexMenuBGColor(): Color = when {
        ifDarkAndCute() -> darkCuteSTBG
        ifDarkAndNotCute() -> darkSTBG
        ifNotDarkAndCute() -> cuteSTBG
        else -> semiTransBG
    }

    fun katexMenuHeaderColor(): Color = when {
        ifDarkAndCute() -> darkCuteSTHeader
        ifDarkAndNotCute() -> darkSTHeader
        ifNotDarkAndCute() -> cuteSTHeader
        else -> semiTransHeader
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