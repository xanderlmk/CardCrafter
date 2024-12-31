package com.example.flashcards.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import com.example.flashcards.R
import com.example.flashcards.model.uiModels.Preferences
import com.example.flashcards.views.miscFunctions.BackButton
import com.example.flashcards.ui.theme.GetModifier
import com.example.flashcards.views.miscFunctions.SystemThemeButton

class GeneralSettings(
    private var getModifier: GetModifier,
    private var preferences: Preferences
) {
    @Composable
    fun SettingsView(onNavigate: () -> Unit) {
        var clicked by remember { mutableStateOf(false) }
        val darkToggled = if(preferences.darkTheme.value)
            painterResource(R.drawable.toggle_on) else
                painterResource(R.drawable.toggle_off)
        val customToggled = if (!preferences.customScheme.value)
            painterResource(R.drawable.toggle_on) else
                painterResource(R.drawable.toggle_off)
        Box(
            modifier =
            getModifier.boxViewsModifier()
        ) {
            BackButton(
                onBackClick = {
                    clicked = true
                    onNavigate()
                              },
                modifier = getModifier.backButtonModifier(),
                getModifier = getModifier
            )
            Column {
                SystemThemeButton(
                    customScheme = {
                        preferences.customScheme.value =
                            !preferences.customScheme.value // Toggle custom theme
                        preferences.saveCustomScheme()
                    },
                    darkTheme = {
                        preferences.darkTheme.value =
                            !preferences.darkTheme.value // Toggle dark theme
                        preferences.saveDarkTheme()
                    },
                    customToggled = customToggled,
                    darkToggled = darkToggled,
                    clicked = clicked,
                    getModifier = getModifier
                )
            }
        }
    }
}