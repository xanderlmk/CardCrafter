package com.example.flashcards.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.flashcards.R
import com.example.flashcards.model.Preferences
import com.example.flashcards.views.miscFunctions.BackButton
import com.example.flashcards.views.miscFunctions.GetModifier

class GeneralSettings(
    private var getModifier: GetModifier,
    private var preferences: Preferences
) {
    @Composable
    fun SettingsView(onNavigate: () -> Unit) {
        var expanded by remember { mutableStateOf(false) }
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
                onBackClick = { onNavigate() },
                modifier = getModifier.backButtonModifier(),
                getModifier = getModifier
            )
            Column {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.TopEnd)) {
                    Button(
                        onClick = {
                            expanded = true
                        },
                        modifier = Modifier.padding(top = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = getModifier.secondaryButtonColor(),
                            contentColor = getModifier.buttonTextColor()
                        )
                    ) {
                        Text("System Theme")
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(
                                onClick = {
                                preferences.customScheme.value =
                                    !preferences.customScheme.value
                                preferences.saveCustomScheme()
                                          },
                                text = { Text("Dynamic Color") },
                                leadingIcon = {
                                    Icon(
                                        painter = customToggled,
                                        contentDescription = "Toggle Dynamic Theme"
                                    )
                                })
                            DropdownMenuItem(onClick = {
                                preferences.darkTheme.value =
                                    !preferences.darkTheme.value // Toggle dark theme
                                preferences.saveDarkTheme()
                            },
                                text = { Text("Dark Theme") },
                                leadingIcon = {
                                    Icon(
                                        painter = darkToggled,
                                        contentDescription = "Toggle Dynamic Theme"
                                    )
                                })
                        }
                    }
                }
            }
        }
    }
}