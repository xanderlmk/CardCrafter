package com.example.flashcards.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcards.controller.viewModels.deckViewsModels.MainViewModel
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flashcards.R
import com.example.flashcards.model.uiModels.Fields
import com.example.flashcards.ui.theme.GetModifier
import com.example.flashcards.views.miscFunctions.MainSettingsButton
import com.example.flashcards.views.miscFunctions.SmallAddButton
import com.example.flashcards.views.miscFunctions.delayNavigate

class MainView(
    private var getModifier: GetModifier,
    private var fields: Fields
) {

    @Composable
    fun DeckList(
        viewModel: MainViewModel,
        onNavigateToDeck: (Int) -> Unit,
        onNavigateToAddDeck: () -> Unit,
        onNavigateToSettings: () -> Unit
    ) {

        val lineModifier = getModifier.mainViewModifier()
        val deckUiState by viewModel.deckUiState.collectAsStateWithLifecycle()
        val cardCount by viewModel.cardCountUiState.collectAsStateWithLifecycle()
        val coroutineScope = rememberCoroutineScope()
        val settingsModifier = getModifier.mainSettingsButtonModifier()

        Box(
            modifier = getModifier.boxViewsModifier()
        ) {
            MainSettingsButton(
                onNavigateToSettings,
                settingsModifier
                    .align(Alignment.TopEnd),
                getModifier, fields
            )
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
                        color = getModifier.titleColor()
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
                                    .clickable {
                                        if (!fields.mainClicked.value) {
                                            fields.mainClicked.value = true
                                            onNavigateToDeck(
                                                deckUiState.deckList[index].id
                                            )
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = lineModifier
                                ) {
                                    Row(
                                        Modifier.align(Alignment.TopStart)
                                    ) {
                                        Text(
                                            text = deckUiState.deckList[index].name + " ",
                                            textAlign = TextAlign.Start,
                                            fontSize = 20.sp,
                                            lineHeight = 22.sp,
                                            color = getModifier.titleColor(),
                                            style = MaterialTheme.typography.titleLarge,
                                            modifier = Modifier.fillMaxWidth(.85f),
                                            softWrap = true
                                        )
                                        Text(
                                            text =
                                            if (cardCount.cardListCount.isNotEmpty()) {
                                                cardCount.cardListCount[
                                                    index % cardCount.cardListCount.size
                                                ].toString()
                                            } else {
                                                "0"
                                            },
                                            textAlign = TextAlign.End,
                                            fontSize = 14.sp,
                                            color = getModifier.titleColor(),
                                            style = MaterialTheme.typography.titleLarge,
                                            modifier = Modifier.fillMaxWidth(),
                                            softWrap = false
                                        )
                                    }
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
                                if (!fields.mainClicked.value) {
                                    fields.mainClicked.value = true
                                    coroutineScope.launch {
                                        delayNavigate()
                                        onNavigateToAddDeck()
                                    }
                                }
                            },
                            getModifier = getModifier
                        )
                    }
                }
            }
        }
    }
}