package com.example.flashcards.ui.theme

import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color.LightGray,
    secondary = Color.LightGray,
    tertiary = Color.LightGray,
    outline = darkBorder,
    background = darkBackground,
    surface = darkEditingBackground,
    primaryContainer = darkButton,
    secondaryContainer = darkSecondaryButton,
    tertiaryContainer = darkTertiaryButton,

    onPrimary = darkIcon,
    onSecondary = darkIcon,
    onTertiary = darkIcon,
    onBackground = darkOnBackground,

    onPrimaryContainer = darkIcon,
    onSecondaryContainer = darkText,
    onTertiaryContainer = onDarkTertiaryButton
)

private val LightColorScheme = lightColorScheme(
    primary = Color.Blue,
    secondary = Color.Blue,
    tertiary = Color.Blue,
    background = backgroundColor,
    outline = borderColor,
    surface = editingBackGroundColor,
    primaryContainer = buttonColor,
    secondaryContainer = secondaryButtonColor,
    tertiaryContainer = tertiaryButtonColor,

    onBackground = onBackgroundColor,
    onPrimary = textColor,
    onSecondary = textColor,
    onTertiary = iconColor,

    onPrimaryContainer = iconColor,
    onSecondaryContainer = textColor,
    onTertiaryContainer = onTertiaryButtonColor
)

class ColorSchemeClass(
    var colorScheme: ColorScheme = LightColorScheme
)

@Composable
fun FlashcardsTheme(
    darkTheme: Boolean,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) {dynamicDarkColorScheme(context)}
            else {dynamicLightColorScheme(context)}

        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}