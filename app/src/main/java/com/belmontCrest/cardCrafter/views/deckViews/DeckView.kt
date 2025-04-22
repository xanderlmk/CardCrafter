package com.belmontCrest.cardCrafter.views.deckViews

import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import androidx.compose.material3.Surface
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.model.application.AppViewModelProvider
import com.belmontCrest.cardCrafter.controller.viewModels.deckViewsModels.DeckViewModel
import com.belmontCrest.cardCrafter.model.uiModels.Fields
import com.belmontCrest.cardCrafter.uiFunctions.AddCardButton
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.addButtonModifier
import com.belmontCrest.cardCrafter.ui.theme.boxViewsModifier


class DeckView(
    private var fields: Fields,
    private var getUIStyle: GetUIStyle,
) {
    @RequiresApi(Build.VERSION_CODES.Q)
    @Composable
    fun ViewEditDeck(
        deck: Deck,
        goToAddCard: (Int) -> Unit,
        goToDueCards: (Int) -> Unit,
    ) {
        val deckVM: DeckViewModel = viewModel(factory = AppViewModelProvider.Factory)
        var pressed = rememberSaveable { mutableStateOf(false) }
        val isLandscape =
            LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
        val widthHeight = if (isLandscape) {
            Pair(0.25f, 0.45f)
        } else {
            Pair(0.55f, 0.15f)
        }
        Box(
            modifier = Modifier
                .boxViewsModifier(getUIStyle.getColorScheme())
        ) {
            ResetDeckDueDate(pressed, deckVM, deck)
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = deck.name,
                    lineHeight = 42.sp,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    color = getUIStyle.titleColor(),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                )
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .padding(start = 20.dp, end = 20.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth(widthHeight.first)
                            .fillMaxHeight(widthHeight.second)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = {
                                        if (!fields.inDeckClicked.value) {
                                            fields.inDeckClicked.value = true
                                            goToDueCards(deck.id)
                                        }
                                    },
                                    onLongPress = {
                                        pressed.value = true
                                    }
                                )
                            }
                            .background(
                                color = getUIStyle.secondaryButtonColor(),
                                shape = RoundedCornerShape(28.dp)
                            ),
                    ) {
                        Text(
                            text = stringResource(R.string.start_deck),
                            color = getUIStyle.buttonTextColor(),
                            modifier = Modifier
                                .align(Alignment.Center),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .addButtonModifier(getUIStyle.getColorScheme())
                ) {
                    val bottomLeftModifier = Modifier
                        .padding(bottom = 12.dp)
                        .align(Alignment.End)
                    Box(
                        modifier = bottomLeftModifier,
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        AddCardButton(
                            onClick = {
                                if (!fields.inDeckClicked.value) {
                                    fields.inDeckClicked.value = true
                                    goToAddCard(deck.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun ResetDeckDueDate(
        pressed: MutableState<Boolean>,
        deckVM: DeckViewModel,
        deck: Deck
    ) {
        if (pressed.value) {
            Dialog(onDismissRequest = { pressed.value = false }) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.98f)

                ) {
                    Column(
                        modifier = Modifier
                            .padding(vertical = 4.dp, horizontal = 20.dp)
                    ) {
                        Text(
                            text = "Would you like to reset the due date to today?",
                            color = getUIStyle.titleColor(),
                            modifier = Modifier
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            softWrap = true
                        )
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    pressed.value = false
                                },
                                modifier = Modifier.padding(horizontal = 10.dp)
                            ) {
                                Text(stringResource(R.string.cancel))
                            }
                            Button(
                                onClick = {
                                    deckVM.updateDueDate(deck.id, deck.cardAmount, deck.cardsDone)
                                        .also {
                                            pressed.value = false
                                        }
                                },
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                            ) {
                                Text("Ok")
                            }
                        }
                    }
                }
            }
        }
    }
}

