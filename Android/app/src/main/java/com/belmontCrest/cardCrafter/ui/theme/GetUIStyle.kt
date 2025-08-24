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

    /** if (isDarkTheme && isDynamicTheme) return true */
    private fun ifDarkAndDynamic(): Boolean = isDarkTheme && isDynamicTheme

    /** if (!isDarkTheme && isDynamicTheme) return true */
    private fun ifNotDarkAndDynamic(): Boolean = !isDarkTheme && isDynamicTheme

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

    /** Text Title color (colorScheme.onBackground) */
    fun titleColor(): Color = cS.colorScheme.onBackground

    fun disabledTextColor(): Color = if (isDarkTheme) darkDisabled else lightDisabled

    fun tertiaryButtonColor(): Color = cS.colorScheme.tertiaryContainer

    fun onTertiaryButtonColor(): Color = cS.colorScheme.onTertiaryContainer

    fun choiceColor(): Color = when {
        ifDarkAndDynamic() -> blendColors(cS.colorScheme.onTertiary, darkChoiceColor, 0.25f)
        ifDarkAndCute() -> darkCuteChoiceColor
        ifDarkAndNotCute() -> darkChoiceColor
        ifNotDarkAndDynamic() -> blendColors(cS.colorScheme.onTertiary, choiceColor, 0.25f)
        ifNotDarkAndCute() -> cuteChoiceColor
        else -> choiceColor
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

    fun isThemeOn(): Color = if (isDarkTheme) Color.White else darkBackground

    fun altBackground(): Color = when {
        ifDarkAndCute() -> darkCuteSecondaryBC
        ifDarkAndNotCute() -> secondaryDBC
        ifNotDarkAndCute() -> cuteSecondaryBC
        else -> secondaryBC
    }

    fun background(): Color = cS.colorScheme.background

    fun navBarColor(): Color = when {
        ifDarkAndDynamic() -> cS.colorScheme.primaryContainer
        ifDarkAndCute() -> darkCuteButton
        ifDarkAndNotCute() -> darkNavBar
        ifNotDarkAndCute() -> cuteButton
        else -> cS.colorScheme.primaryContainer
    }

    fun dialogColor(): Color = when {
        ifDarkAndCute() -> Color(70, 20, 50, 240)
        ifDarkAndNotCute() -> dialogDarkBackground
        ifNotDarkAndCute() -> Color(255, 230, 240, 240)
        else -> dialogLightBackground
    }

    fun importingDeckColor(): Color = when {
        ifDarkAndCute() -> Color(100, 40, 80)
        ifDarkAndNotCute() -> Color.DarkGray
        ifNotDarkAndCute() -> Color(255, 192, 203, 180)
        else -> Color.Gray
    }

    fun themedColor(): Color = if (isDarkTheme) Color.White else Color.Black

    fun katexMenuBGColor(): Color = when {
        ifDarkAndDynamic() -> {
            val red = cS.colorScheme.onTertiary.red
            val blue = cS.colorScheme.onTertiary.blue
            val green = cS.colorScheme.onTertiary.green
            val color = Color(red, blue, green, 0.8509804f)
            blendColors(darkSTBG, color, 0.9f)
        }

        ifDarkAndCute() -> darkCuteSTBG
        ifDarkAndNotCute() -> darkSTBG
        ifNotDarkAndDynamic() -> {
            val red = cS.colorScheme.onTertiary.red
            val blue = cS.colorScheme.onTertiary.blue
            val green = cS.colorScheme.onTertiary.green
            val color = Color(red, blue, green, 0.8509804f)
            blendColors(semiTransBG, color, 0.9f)
        }

        ifNotDarkAndCute() -> cuteSTBG
        else -> semiTransBG
    }

    fun katexMenuHeaderColor(): Color = when {
        ifDarkAndDynamic() -> {
            val red = cS.colorScheme.primaryContainer.red
            val blue = cS.colorScheme.primaryContainer.blue
            val green = cS.colorScheme.primaryContainer.green
            val color = Color(red, blue, green, 0.8509804f)
            blendColors(darkSTHeader, color, 0.25f)
        }

        ifDarkAndCute() -> darkCuteSTHeader
        ifDarkAndNotCute() -> darkSTHeader
        ifNotDarkAndDynamic() -> {
            val red = cS.colorScheme.primaryContainer.red
            val blue = cS.colorScheme.primaryContainer.blue
            val green = cS.colorScheme.primaryContainer.green
            val color = Color(red, blue, green, 0.8509804f)
            blendColors(semiTransHeader, color, 0.25f)
        }

        ifNotDarkAndCute() -> cuteSTHeader
        else -> semiTransHeader
    }

    fun redColor(): Color = when {
        ifDarkAndCute() -> Color(115, 66, 66, 255)
        ifDarkAndNotCute() -> Color(157, 2, 2, 255)
        ifNotDarkAndCute() -> Color(112, 1, 1, 255)
        else -> Color.Red
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