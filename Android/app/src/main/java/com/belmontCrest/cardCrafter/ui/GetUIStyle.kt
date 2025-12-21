package com.belmontCrest.cardCrafter.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.belmontCrest.cardCrafter.model.ui.CCTheme
import com.belmontCrest.cardCrafter.ui.theme.ColorSchemeClass
import com.belmontCrest.cardCrafter.ui.theme.choiceColor
import com.belmontCrest.cardCrafter.ui.theme.correctChoice
import com.belmontCrest.cardCrafter.ui.theme.cuteButton
import com.belmontCrest.cardCrafter.ui.theme.cuteChoiceColor
import com.belmontCrest.cardCrafter.ui.theme.cuteCorrectChoice
import com.belmontCrest.cardCrafter.ui.theme.cutePickedChoice
import com.belmontCrest.cardCrafter.ui.theme.cuteSTBG
import com.belmontCrest.cardCrafter.ui.theme.cuteSTHeader
import com.belmontCrest.cardCrafter.ui.theme.cuteSecondaryBC
import com.belmontCrest.cardCrafter.ui.theme.darkBackground
import com.belmontCrest.cardCrafter.ui.theme.darkChoiceColor
import com.belmontCrest.cardCrafter.ui.theme.darkCorrectChoice
import com.belmontCrest.cardCrafter.ui.theme.darkCuteButton
import com.belmontCrest.cardCrafter.ui.theme.darkCuteChoiceColor
import com.belmontCrest.cardCrafter.ui.theme.darkCuteCorrectChoice
import com.belmontCrest.cardCrafter.ui.theme.darkCutePickedChoice
import com.belmontCrest.cardCrafter.ui.theme.darkCuteSTBG
import com.belmontCrest.cardCrafter.ui.theme.darkCuteSTHeader
import com.belmontCrest.cardCrafter.ui.theme.darkCuteSecondaryBC
import com.belmontCrest.cardCrafter.ui.theme.darkDisabled
import com.belmontCrest.cardCrafter.ui.theme.darkNavBar
import com.belmontCrest.cardCrafter.ui.theme.darkPickedChoice
import com.belmontCrest.cardCrafter.ui.theme.darkSTBG
import com.belmontCrest.cardCrafter.ui.theme.darkSTHeader
import com.belmontCrest.cardCrafter.ui.theme.dialogDarkBackground
import com.belmontCrest.cardCrafter.ui.theme.dialogLightBackground
import com.belmontCrest.cardCrafter.ui.theme.lightDisabled
import com.belmontCrest.cardCrafter.ui.theme.onCuteCorrectChoice
import com.belmontCrest.cardCrafter.ui.theme.onDarkCuteCorrectChoice
import com.belmontCrest.cardCrafter.ui.theme.pickedChoice
import com.belmontCrest.cardCrafter.ui.theme.secondaryBC
import com.belmontCrest.cardCrafter.ui.theme.secondaryDBC
import com.belmontCrest.cardCrafter.ui.theme.semiTransBG
import com.belmontCrest.cardCrafter.ui.theme.semiTransButtonColor
import com.belmontCrest.cardCrafter.ui.theme.semiTransCuteButton
import com.belmontCrest.cardCrafter.ui.theme.semiTransDarkButton
import com.belmontCrest.cardCrafter.ui.theme.semiTransDarkCuteButton
import com.belmontCrest.cardCrafter.ui.theme.semiTransHeader

/** Our UI class that will return the color
 * scheme as well as some custom colors
 * */
class GetUIStyle(
    private var cS: ColorSchemeClass, private var theme: CCTheme
) {
    @Composable
    fun getIsDarkTheme(): Boolean = theme.isDark() || (theme.isSystem() && isSystemInDarkTheme())

    fun getIsCuteTheme(): Boolean = theme.isCute()

    /** if (isDarkTheme && isCuteTheme) return true */
    private fun ifDarkAndCute(): Boolean = theme.isDarkAndCute()

    /** if (isDarkTheme && !isCuteTheme) return true */
    private fun ifDarkAndNotCute(): Boolean = theme.isDarkAndDynamic() || theme.isDarkAndOriginal()

    /** if (!isDarkTheme && isCuteTheme) return true */
    private fun ifLightAndCute(): Boolean = theme.isLightAndCute()

    /** if (isDarkTheme && isDynamicTheme) return true */
    private fun ifDarkAndDynamic(): Boolean = theme.isDarkAndDynamic()

    /** if (!isDarkTheme && isDynamicTheme) return true */
    private fun ifLightAndDynamic(): Boolean = theme.isLightAndDynamic()

    fun getColorScheme(): ColorScheme = cS.colorScheme

    fun buttonColor(): Color = cS.colorScheme.primaryContainer

    fun semiTransButtonColor(): Color = when {
        ifDarkAndCute() -> semiTransDarkCuteButton
        ifDarkAndNotCute() -> semiTransDarkButton
        ifLightAndCute() -> semiTransCuteButton
        else -> semiTransButtonColor
    }

    fun iconColor(): Color = cS.colorScheme.onPrimaryContainer

    fun secondaryButtonColor(): Color = cS.colorScheme.secondaryContainer

    fun buttonTextColor(): Color = cS.colorScheme.onSecondaryContainer

    /** Text Title color (colorScheme.onBackground) */
    fun titleColor(): Color = cS.colorScheme.onBackground

    @Composable
    fun disabledTextColor(): Color = if (getIsDarkTheme()) darkDisabled else lightDisabled

    fun tertiaryButtonColor(): Color = cS.colorScheme.tertiaryContainer

    fun onTertiaryButtonColor(): Color = cS.colorScheme.onTertiaryContainer

    fun choiceColor(): Color = when {
        ifDarkAndDynamic() -> blendColors(cS.colorScheme.onTertiary, darkChoiceColor, 0.25f)
        ifDarkAndCute() -> darkCuteChoiceColor
        ifDarkAndNotCute() -> darkChoiceColor
        ifLightAndDynamic() -> blendColors(cS.colorScheme.onTertiary, choiceColor, 0.25f)
        ifLightAndCute() -> cuteChoiceColor
        else -> choiceColor
    }

    fun pickedChoice(): Color = when {
        ifDarkAndCute() -> darkCutePickedChoice
        ifDarkAndNotCute() -> darkPickedChoice
        ifLightAndCute() -> cutePickedChoice
        else -> pickedChoice
    }

    fun correctChoice(): Color = when {
        ifDarkAndCute() -> darkCuteCorrectChoice
        ifDarkAndNotCute() -> darkCorrectChoice
        ifLightAndCute() -> cuteCorrectChoice
        else -> correctChoice
    }

    fun onCorrectChoice(): Color = when {
        ifDarkAndCute() -> onDarkCuteCorrectChoice
        ifLightAndCute() -> onCuteCorrectChoice
        else -> cS.colorScheme.onSurfaceVariant
    }

    @Composable
    fun isThemeOn(): Color = if (getIsDarkTheme()) Color.White else darkBackground

    @Composable
    fun themedColor(): Color = if (getIsDarkTheme()) Color.White else Color.Black

    fun altBackground(): Color = when {
        ifDarkAndCute() -> darkCuteSecondaryBC
        ifDarkAndNotCute() -> secondaryDBC
        ifLightAndCute() -> cuteSecondaryBC
        else -> secondaryBC
    }

    fun background(): Color = cS.colorScheme.background

    fun navBarColor(): Color = when {
        ifDarkAndDynamic() -> cS.colorScheme.primaryContainer
        ifDarkAndCute() -> darkCuteButton
        ifDarkAndNotCute() -> darkNavBar
        ifLightAndCute() -> cuteButton
        else -> cS.colorScheme.primaryContainer
    }

    fun dialogColor(): Color = when {
        ifDarkAndCute() -> Color(70, 20, 50, 240)
        ifDarkAndNotCute() -> dialogDarkBackground
        ifLightAndCute() -> Color(255, 230, 240, 240)
        else -> dialogLightBackground
    }

    fun importingDeckColor(): Color = when {
        ifDarkAndCute() -> Color(100, 40, 80)
        ifDarkAndNotCute() -> Color.DarkGray
        ifLightAndCute() -> Color(255, 192, 203, 180)
        else -> Color.Gray
    }


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
        ifLightAndDynamic() -> {
            val red = cS.colorScheme.onTertiary.red
            val blue = cS.colorScheme.onTertiary.blue
            val green = cS.colorScheme.onTertiary.green
            val color = Color(red, blue, green, 0.8509804f)
            blendColors(semiTransBG, color, 0.9f)
        }

        ifLightAndCute() -> cuteSTBG
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
        ifLightAndDynamic() -> {
            val red = cS.colorScheme.primaryContainer.red
            val blue = cS.colorScheme.primaryContainer.blue
            val green = cS.colorScheme.primaryContainer.green
            val color = Color(red, blue, green, 0.8509804f)
            blendColors(semiTransHeader, color, 0.25f)
        }

        ifLightAndCute() -> cuteSTHeader
        else -> semiTransHeader
    }

    fun redColor(): Color = when {
        ifDarkAndCute() -> Color(115, 66, 66, 255)
        ifDarkAndNotCute() -> Color(157, 2, 2, 255)
        ifLightAndCute() -> Color(112, 1, 1, 255)
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