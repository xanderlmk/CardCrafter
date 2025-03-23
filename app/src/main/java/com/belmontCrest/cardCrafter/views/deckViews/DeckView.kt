package com.belmontCrest.cardCrafter.views.deckViews

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import com.belmontCrest.cardCrafter.model.tablesAndApplication.Deck
import androidx.compose.material3.Surface
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.controller.AppViewModelProvider
import com.belmontCrest.cardCrafter.controller.viewModels.deckViewsModels.DeckViewModel
import com.belmontCrest.cardCrafter.model.uiModels.Fields
import com.belmontCrest.cardCrafter.views.miscFunctions.AddCardButton
import com.belmontCrest.cardCrafter.views.miscFunctions.BackButton
import com.belmontCrest.cardCrafter.views.miscFunctions.SettingsButton
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.addButtonModifier
import com.belmontCrest.cardCrafter.ui.theme.backButtonModifier
import com.belmontCrest.cardCrafter.ui.theme.boxViewsModifier
import com.belmontCrest.cardCrafter.ui.theme.settingsButtonModifier


class DeckView(
    private var fields: Fields,
    private var getUIStyle: GetUIStyle,
) {
    @Composable
    fun ViewEditDeck(
        deck: Deck,
        onNavigate: () -> Unit,
        goToAddCard: (Int) -> Unit,
        goToDueCards: (Int) -> Unit,
        goToEditDeck: (Int, String) -> Unit,
        goToViewCards: (Int) -> Unit
    ) {
        val deckVM: DeckViewModel = viewModel(factory = AppViewModelProvider.Factory)
        var pressed = rememberSaveable { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .boxViewsModifier(getUIStyle.getColorScheme())
        ) {
            ResetDeckDueDate(pressed, deckVM, deck.id, deck.cardAmount)
            BackButton(
                onBackClick = { onNavigate() },
                modifier = Modifier
                    .backButtonModifier(),
                getUIStyle = getUIStyle
            )
            SettingsButton(
                onNavigateToEditDeck = {
                    if (!fields.inDeckClicked.value) {
                        fields.inDeckClicked.value = true
                        goToEditDeck(deck.id, deck.name)
                    }
                },
                onNavigateToEditCards = {
                    if (!fields.inDeckClicked.value) {
                        fields.inDeckClicked.value = true
                        goToViewCards(deck.id)
                    }
                },
                modifier = Modifier
                    .settingsButtonModifier()
                    .align(Alignment.TopEnd),
                getUIStyle = getUIStyle,
                fields = fields
            )
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = deck.name,
                        lineHeight = 42.sp,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                        color = getUIStyle.titleColor(),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 50.dp)
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(2f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp, start = 20.dp, end = 20.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth(0.55f)
                                .fillMaxHeight(.125f)
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
                                    shape = RoundedCornerShape(24.dp)
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
        deckId: Int, cardAmount: Int
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
                                    deckVM.updateDueDate(deckId, cardAmount).also {
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

