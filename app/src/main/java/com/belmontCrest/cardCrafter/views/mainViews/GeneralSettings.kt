package com.belmontCrest.cardCrafter.views.mainViews

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.model.uiModels.PreferencesManager
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.boxViewsModifier
import kotlinx.coroutines.delay

class GeneralSettings(
    private var getUIStyle: GetUIStyle,
    private var preferences: PreferencesManager
) {
    @Composable
    fun SettingsView() {
        var themeClicked by remember { mutableStateOf(false) }
        var deckOptionsClicked by remember { mutableStateOf(false) }

        val darkToggled = if(preferences.darkTheme.value)
            painterResource(R.drawable.toggle_on) else
            painterResource(R.drawable.toggle_off)
        val customToggled = if (!preferences.customScheme.value)
            painterResource(R.drawable.toggle_on) else
            painterResource(R.drawable.toggle_off)
        val cuteToggled = if(preferences.cuteTheme.value)
            painterResource(R.drawable.toggle_on) else
            painterResource(R.drawable.toggle_off)
        val darkCuteToggled = if(preferences.darkCuteTheme.value)
            painterResource(R.drawable.toggle_on) else
            painterResource(R.drawable.toggle_off)
        val reviewAmount = rememberSaveable {
            mutableStateOf(preferences.reviewAmount.intValue.toString())  }
        val cardAmount = rememberSaveable {
            mutableStateOf(preferences.cardAmount.intValue.toString()) }
        val invalid = rememberSaveable { mutableStateOf(false) }
        val errorMessage = rememberSaveable { mutableStateOf("") }
        val invalidReview = stringResource(R.string.review_amount_1_40).toString()
        val invalidCards = stringResource(R.string.card_amount_5_1000).toString()
        //val scrollState = rememberScrollState()
        var reviewSuccess by remember { mutableStateOf(false) }
        var cardSuccess by remember { mutableStateOf(false) }

        LaunchedEffect(cardSuccess) {
            delay(1500)
            cardSuccess = false
        }
        LaunchedEffect(reviewSuccess) {
            delay(1500)
            reviewSuccess = false
        }
        Box(
            modifier = Modifier
                .boxViewsModifier(getUIStyle.getColorScheme())
        ) {
            InvalidXXAmount(invalid, getUIStyle, errorMessage.value)
            Column {
                SystemThemeOptions(
                    customScheme = {
                        preferences.customScheme.value =
                            !preferences.customScheme.value // Toggle custom theme
                        preferences.saveCustomScheme()
                    },
                    darkTheme = {
                        preferences.darkTheme.value = !preferences.darkTheme.value
                        if (preferences.darkTheme.value) {
                            preferences.cuteTheme.value = false
                            preferences.darkCuteTheme.value = false
                            preferences.saveCuteTheme()
                            preferences.saveDarkCuteTheme()
                        }
                        preferences.saveDarkTheme()
                    },
                    cuteTheme = {
                        preferences.cuteTheme.value = !preferences.cuteTheme.value
                        if (preferences.cuteTheme.value) {
                            preferences.darkTheme.value = false
                            preferences.darkCuteTheme.value = false
                            preferences.saveDarkTheme()
                            preferences.saveDarkCuteTheme()
                        }
                        preferences.saveCuteTheme()
                    },
                    darkCuteTheme = {
                        preferences.darkCuteTheme.value = !preferences.darkCuteTheme.value
                        if (preferences.darkCuteTheme.value) {
                            preferences.darkTheme.value = false
                            preferences.cuteTheme.value = false
                            preferences.saveDarkTheme()
                            preferences.saveCuteTheme()
                        }
                        preferences.saveDarkCuteTheme()
                    },
                    customToggled = customToggled,
                    darkToggled = darkToggled,
                    cuteToggled = cuteToggled,
                    darkCuteToggled = darkCuteToggled,
                    clicked = themeClicked,
                    getUIStyle = getUIStyle
                )
                DefaultDeckOptions(
                    changeReviewAmount = {
                        if((reviewAmount.value.toIntOrNull() ?: 0) in 1..40)
                        {
                            preferences.reviewAmount.intValue =
                                reviewAmount.value.toInt()
                            preferences.saveReviewAmount()
                            reviewSuccess = true
                        } else {
                            errorMessage.value = invalidReview
                            invalid.value = true
                        }
                    },
                    changeCardAmount = {
                        if((cardAmount.value.toIntOrNull() ?: 1) in 5..1000) {
                            preferences.cardAmount.intValue =
                                cardAmount.value.toInt()
                            preferences.saveCardAmount()
                            cardSuccess = true
                        } else {
                            errorMessage.value = invalidCards
                            invalid.value = true
                        }
                    },
                    reviewAmount = reviewAmount,
                    cardAmount = cardAmount,
                    reviewSuccess = reviewSuccess,
                    cardSuccess = cardSuccess,
                    clicked = deckOptionsClicked,
                    getUIStyle = getUIStyle
                )
            }
        }
    }
}