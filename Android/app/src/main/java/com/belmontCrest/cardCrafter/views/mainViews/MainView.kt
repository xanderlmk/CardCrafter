package com.belmontCrest.cardCrafter.views.mainViews

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.belmontCrest.cardCrafter.controller.viewModels.deckViewsModels.MainViewModel
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.model.FSProp
import com.belmontCrest.cardCrafter.model.MLProp
import com.belmontCrest.cardCrafter.model.TAProp
import com.belmontCrest.cardCrafter.model.TSProp
import com.belmontCrest.cardCrafter.model.TextProps
import com.belmontCrest.cardCrafter.model.ui.Fields
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.boxViewsModifier
import com.belmontCrest.cardCrafter.ui.theme.mainViewModifier
import com.belmontCrest.cardCrafter.uiFunctions.CustomText
import com.belmontCrest.cardCrafter.uiFunctions.buttons.SmallAddButton
import java.text.SimpleDateFormat
import java.util.Locale

class MainView(
    private var getUIStyle: GetUIStyle,
    private var fields: Fields
) {
    @Composable
    fun DeckList(
        viewModel: MainViewModel,
        onNavigateToDeck: (Int) -> Unit,
        onNavigateToAddDeck: () -> Unit,
        onNavigateToSBDeckList: () -> Unit,
        goToDueCards: (Int) -> Unit,
    ) {
        val deckUiState by viewModel.deckUiState.collectAsStateWithLifecycle()
        var deckIndex by rememberSaveable { mutableIntStateOf(0) }
        val pressed = rememberSaveable { mutableStateOf(false) }
        var expanded by rememberSaveable { mutableStateOf(false) }


        Box(
            modifier = Modifier
                .boxViewsModifier(getUIStyle.getColorScheme())
        ) {
            if (deckIndex in deckUiState.deckList.indices) {
                ShowNextReview(pressed, deckUiState.deckList[deckIndex])
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 4.dp)
            ) {
                LazyColumn {
                    items(deckUiState.deckList.size) { index ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = {
                                            if (!fields.mainClicked.value) {
                                                fields.mainClicked.value = true
                                                onNavigateToDeck(deckUiState.deckList[index].id)
                                            }
                                        },
                                        onLongPress = {
                                            deckIndex = index
                                            pressed.value = true
                                        },
                                        onDoubleTap = {
                                            if (!fields.mainClicked.value) {
                                                fields.mainClicked.value = true
                                                goToDueCards(deckUiState.deckList[index].id)
                                            }
                                        }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier
                                    .mainViewModifier(getUIStyle.getColorScheme())
                                    .align(Alignment.TopStart),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CustomText(
                                    text = deckUiState.deckList[index].name + " ",
                                    getUIStyle = getUIStyle,
                                    props = TextProps(
                                        fs = FSProp.Font22, ml = MLProp.Two, ts = TSProp.LargeTitle,
                                        ta = TAProp.Start
                                    ), modifier = Modifier.fillMaxWidth(.85f)
                                )
                                CustomText(
                                    text =
                                        if (index in 0..deckUiState.cardAmount.lastIndex) {
                                            deckUiState.cardAmount[index].toString()
                                        } else {
                                            "0"
                                        },
                                    getUIStyle = getUIStyle,
                                    props = TextProps(
                                        fs = FSProp.Font14, ta = TAProp.End, ts = TSProp.LargeTitle
                                    ), softWrap = false, modifier = Modifier.fillMaxWidth()
                                )
                            }

                        }
                    }
                }
            }
            SmallAddButton(
                onClick = {
                    expanded = true
                },
                getUIStyle = getUIStyle,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Bottom
            ) {
                val bottomLeftModifier = Modifier
                    .padding(bottom = 12.dp)
                    .align(Alignment.End)
                Box(
                    modifier = bottomLeftModifier,
                ) {
                    DropdownMenu(
                        expanded = expanded, onDismissRequest = { expanded = false },
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                if (!fields.mainClicked.value) {
                                    fields.mainClicked.value = true
                                    onNavigateToAddDeck()
                                    expanded = false
                                }
                            },
                            text = { Text(stringResource(R.string.add_deck)) })
                        DropdownMenuItem(
                            onClick = {
                                if (!fields.mainClicked.value) {
                                    fields.mainClicked.value = true
                                    onNavigateToSBDeckList()
                                    expanded = false
                                }
                            },
                            text = { Text("Online Decks") }

                        )
                    }
                }
            }
        }
    }

    @Composable
    fun ShowNextReview(pressed: MutableState<Boolean>, deck: Deck) {
        if (pressed.value) {
            val dateFormat = SimpleDateFormat("EEE MMM dd HH:mm yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(deck.nextReview)
            Dialog(onDismissRequest = { pressed.value = false }) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.98f)
                ) {
                    Text(
                        text = "Next Review: $formattedDate",
                        color = getUIStyle.titleColor(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                }
            }
        }
    }
}