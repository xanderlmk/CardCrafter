package com.belmontCrest.cardCrafter.ui.theme


import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    //primary = darkCorrectChoice,
    //secondary = darkPickedChoice,
    //tertiary = darkTertiaryColor,
    outline = darkBorder,
    background = darkBackground,
    surface = darkEditingBackground,
    primaryContainer = darkButton,
    secondaryContainer = darkSecondaryButton,
    tertiaryContainer = darkTertiaryButton,

    onPrimary = darkCorrectChoice,
    onSecondary = darkPickedChoice,
    onTertiary = darkChoiceColor,
    onBackground = darkOnBackground,
    onSurfaceVariant = onDarkCorrectChoice,

    onPrimaryContainer = darkIcon,
    onSecondaryContainer = darkText,
    onTertiaryContainer = onDarkTertiaryButton
)

private val LightColorScheme = lightColorScheme(
    //primary = correctChoice,
    //secondary = pickedChoice,
    //tertiary = tertiaryColor,
    background = backgroundColor,
    outline = borderColor,
    surface = editingBackGroundColor,
    primaryContainer = buttonColor,
    secondaryContainer = secondaryButtonColor,
    tertiaryContainer = tertiaryButtonColor,

    onPrimary = correctChoice,
    onSecondary = pickedChoice,
    onTertiary = choiceColor,
    onBackground = onBackgroundColor,
    onSurfaceVariant = onCorrectChoice,

    onPrimaryContainer = iconColor,
    onSecondaryContainer = textColor,
    onTertiaryContainer = onTertiaryButtonColor
)

private val CuteColorScheme = lightColorScheme(
    background = cuteBackground,
    outline = cuteBorder,
    surface = cuteEditingBackground,
    primaryContainer = cuteButton,
    secondaryContainer = cuteSecondaryButton,
    tertiaryContainer = cuteTertiaryButton,

    onPrimary = cuteCorrectChoice,
    onSecondary = cutePickedChoice,
    onTertiary = cuteChoiceColor,
    onBackground = cuteOnBackground,
    onSurfaceVariant = onCuteCorrectChoice,

    onPrimaryContainer = cuteIcon,
    onSecondaryContainer = cuteText,
    onTertiaryContainer = onCuteTertiaryButton
)

private val DarkCuteColorScheme = darkColorScheme(
    background = darkCuteBackground,
    outline = darkCuteBorder,
    surface = darkCuteEditingBackground,
    primaryContainer = darkCuteButton,
    secondaryContainer = darkCuteSecondaryButton,
    tertiaryContainer = darkCuteTertiaryButton,

    onPrimary = darkCuteCorrectChoice,
    onSecondary = darkCutePickedChoice,
    onTertiary = darkCuteChoiceColor,
    onBackground = darkCuteOnBackground,
    onSurfaceVariant = onDarkCuteCorrectChoice,

    onPrimaryContainer = darkCuteIcon,
    onSecondaryContainer = darkCuteText,
    onTertiaryContainer = onDarkCuteTertiaryButton
)


class ColorSchemeClass(
    var colorScheme: ColorScheme = LightColorScheme
)

@Composable
fun FlashcardsTheme(
    darkTheme: Boolean,
    cuteTheme: Boolean,
    darkCuteTheme: Boolean,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) {dynamicDarkColorScheme(context)}
            else {dynamicLightColorScheme(context)}
        }
        darkCuteTheme -> DarkCuteColorScheme
        cuteTheme -> CuteColorScheme
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}