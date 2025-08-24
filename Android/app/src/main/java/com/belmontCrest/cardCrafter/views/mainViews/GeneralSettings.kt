package com.belmontCrest.cardCrafter.views.mainViews

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.model.application.preferences.PreferencesManager
import com.belmontCrest.cardCrafter.model.application.preferences.setPreferenceValues
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.scrollableBoxViewModifier
import kotlinx.coroutines.delay

class GeneralSettings(
    private var getUIStyle: GetUIStyle,
    private var preferences: PreferencesManager
) {
    @Composable
    fun SettingsView() {
        val pc = setPreferenceValues(preferences)
        val darkToggled = if (pc.darkTheme)
            painterResource(R.drawable.toggle_on) else
            painterResource(R.drawable.toggle_off)
        val customToggled = if (!pc.dynamicTheme)
            painterResource(R.drawable.toggle_on) else
            painterResource(R.drawable.toggle_off)
        val cuteToggled = if (pc.cuteTheme)
            painterResource(R.drawable.toggle_on) else
            painterResource(R.drawable.toggle_off)
        val reviewAmount = rememberSaveable { mutableStateOf(pc.reviewAmount.toString()) }
        val cardAmount = rememberSaveable { mutableStateOf(pc.cardAmount.toString()) }
        val height = rememberSaveable { mutableStateOf(pc.height.toString()) }
        val width = rememberSaveable {
            mutableStateOf(if (pc.width == Int.MIN_VALUE) "200" else pc.width.toString())
        }
        val invalid = rememberSaveable { mutableStateOf(false) }
        val errorMessage = rememberSaveable { mutableStateOf("") }
        val invalidReview = stringResource(R.string.review_amount_1_40)
        val invalidCards = stringResource(R.string.card_amount_5_1000)

        val (cardSuccess, reviewSuccess, heightSuccess, widthSuccess) = Infinite(
            remember { mutableStateOf(false) }, remember { mutableStateOf(false) },
            remember { mutableStateOf(false) }, remember { mutableStateOf(false) }
        )
        LaunchedEffect(cardSuccess.value) { delay(1500); cardSuccess.value = false }
        LaunchedEffect(reviewSuccess.value) { delay(1500); reviewSuccess.value = false }
        LaunchedEffect(heightSuccess.value) { delay(1500); heightSuccess.value = false }
        LaunchedEffect(widthSuccess.value) { delay(1500); widthSuccess.value = false }
        Box(
            modifier = Modifier
                .scrollableBoxViewModifier(rememberScrollState(), getUIStyle.getColorScheme())
        ) {
            InvalidXXAmount(invalid, getUIStyle, errorMessage.value)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SystemThemeOptions(
                    toggleDynamicTheme = {
                        if (!pc.dynamicTheme) {
                            preferences.saveCuteTheme(false)
                        }
                        preferences.saveDynamicTheme()
                    },
                    toggleDarkTheme = { preferences.saveDarkTheme() },
                    toggleCuteTheme = { preferences.saveCuteTheme() },
                    customToggled = customToggled,
                    darkToggled = darkToggled,
                    cuteToggled = cuteToggled,
                    isDynamicTheme = pc.dynamicTheme,
                    getUIStyle = getUIStyle
                )
                DefaultDeckOptions(
                    changeReviewAmount = {
                        if ((reviewAmount.value.toIntOrNull() ?: 0) in 1..40) {
                            preferences.saveReviewAmount(reviewAmount.value.toInt())
                            reviewSuccess.value = true
                        } else {
                            errorMessage.value = invalidReview
                            invalid.value = true
                        }
                    },
                    changeCardAmount = {
                        if ((cardAmount.value.toIntOrNull() ?: 1) in 5..1000) {
                            preferences.saveCardAmount(cardAmount.value.toInt())
                            cardSuccess.value = true
                        } else {
                            errorMessage.value = invalidCards
                            invalid.value = true
                        }
                    },
                    reviewAmount = reviewAmount,
                    cardAmount = cardAmount,
                    reviewSuccess = reviewSuccess.value,
                    cardSuccess = cardSuccess.value,
                    getUIStyle = getUIStyle
                )
                KatexMenuOptions(
                    changeMenuHeight = {
                        if ((height.value.toIntOrNull() ?: 0) in 150..350) {
                            preferences.saveKatexMenuHeight(height.value.toInt())
                            heightSuccess.value = true
                        } else {
                            errorMessage.value = "Height must be between 150 and 350"
                            invalid.value = true
                        }
                    },
                    changeMenuWidth = {
                        if ((width.value.toIntOrNull() ?: 0) in 150..700) {
                            preferences.saveKatexMenuWidth(width.value.toInt())
                            widthSuccess.value = true
                        } else {
                            errorMessage.value = "Width must be between 150 and 700"
                            invalid.value = true
                        }
                    },
                    height = height, width = width,
                    heightSuccess = heightSuccess.value,
                    widthSuccess = widthSuccess.value,
                    getUIStyle = getUIStyle
                )
            }
        }
    }
}


@Composable
private fun InvalidXXAmount(
    pressed: MutableState<Boolean>,
    getUIStyle: GetUIStyle,
    error: String
) {
    if (pressed.value) {
        AlertDialog(
            onDismissRequest = { pressed.value = false },
            title = { Text("Invalid") },
            text = {
                Text(
                    text = error,
                    color = getUIStyle.titleColor()
                )
            },
            confirmButton = {},
            dismissButton = {
                Button(
                    onClick = { pressed.value = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = getUIStyle.secondaryButtonColor(),
                        contentColor = getUIStyle.buttonTextColor()
                    )
                ) {
                    Text(stringResource(R.string.okay))
                }
            }
        )
    }
}


private data class Infinite<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)