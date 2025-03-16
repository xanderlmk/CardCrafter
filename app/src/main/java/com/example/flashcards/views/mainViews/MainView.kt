package com.example.flashcards.views.mainViews

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
import androidx.compose.material3.MaterialTheme
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
import com.example.flashcards.controller.viewModels.deckViewsModels.MainViewModel
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flashcards.R
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.model.uiModels.Fields
import com.example.flashcards.ui.theme.GetUIStyle
import com.example.flashcards.ui.theme.boxViewsModifier
import com.example.flashcards.ui.theme.mainSettingsButtonModifier
import com.example.flashcards.ui.theme.mainViewModifier
import com.example.flashcards.views.miscFunctions.MainSettingsButton
import com.example.flashcards.views.miscFunctions.SmallAddButton
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
        onNavigateToSettings: () -> Unit,
        onNavigateToSBDeckList: () -> Unit
    ) {
        val deckUiState by viewModel.deckUiState.collectAsStateWithLifecycle()
        val cardCount by viewModel.cardCountUiState.collectAsStateWithLifecycle()
        var deckIndex by rememberSaveable { mutableIntStateOf(0) }
        var pressed = rememberSaveable { mutableStateOf(false) }
        var expanded by rememberSaveable { mutableStateOf(false) }


        Box(
            modifier = Modifier
                .boxViewsModifier(getUIStyle.getColorScheme())
        ) {
            MainSettingsButton(
                onNavigateToSettings,
                Modifier
                    .mainSettingsButtonModifier()
                    .align(Alignment.TopEnd),
                getUIStyle, fields
            )
            if (deckIndex in deckUiState.deckList.indices) {
                ShowNextReview(pressed, deckUiState.deckList[deckIndex])
            }
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, start = 20.dp, end = 20.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.deck_list),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = getUIStyle.titleColor()
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(2f)
                        .padding(16.dp)
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
                                            }
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    modifier = Modifier
                                        .mainViewModifier(getUIStyle.getColorScheme())
                                        .align(Alignment.TopStart)
                                ) {
                                    Text(
                                        text = deckUiState.deckList[index].name + " ",
                                        textAlign = TextAlign.Start,
                                        fontSize = 20.sp,
                                        lineHeight = 22.sp,
                                        color = getUIStyle.titleColor(),
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.fillMaxWidth(.85f),
                                        softWrap = true
                                    )
                                    Text(
                                        text =
                                        if (index in 0..cardCount.cardListCount.lastIndex) {
                                            cardCount.cardListCount[index].toString()
                                        } else {
                                            "0"
                                        },
                                        textAlign = TextAlign.End,
                                        fontSize = 14.sp,
                                        color = getUIStyle.titleColor(),
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.fillMaxWidth(),
                                        softWrap = false
                                    )
                                }

                            }
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    val bottomLeftModifier = Modifier
                        .padding(bottom = 12.dp)
                        .align(Alignment.End)

                    Box(
                        modifier = bottomLeftModifier,
                    ) {
                        SmallAddButton(
                            onClick = {
                                expanded = true
                            },
                            getUIStyle = getUIStyle
                        )

                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
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
                                text = { Text("Import deck") }

                            )
                        }
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
                            .padding(vertical = 6.dp, horizontal = 0.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                }
            }
        }
    }
}