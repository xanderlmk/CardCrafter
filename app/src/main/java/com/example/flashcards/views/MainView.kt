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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcards.controller.viewModels.DeckViewModel
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.flashcards.R
import com.example.flashcards.model.Fields
import com.example.flashcards.views.miscFunctions.GetModifier
import com.example.flashcards.views.miscFunctions.MainSettingsButton
import com.example.flashcards.views.miscFunctions.SmallAddButton
import com.example.flashcards.views.miscFunctions.delayNavigate
import java.util.Date

class MainView(
    private var getModifier: GetModifier,
    private var fields: Fields) {

    @Composable
    fun DeckList(viewModel: DeckViewModel,
                 onNavigateToDeck : (Int) -> Unit,
                 onNavigateToAddDeck  : () -> Unit,
                 onNavigateToSettings : () -> Unit) {

        val lineModifier = getModifier.mainViewModifier()
        val uiState by viewModel.deckUiState.collectAsState()
        val coroutineScope = rememberCoroutineScope()
        val settingsModifier = getModifier.mainSettingsButtonModifier()
        Box(
            modifier = getModifier.boxViewsModifier()
        ) {
            MainSettingsButton(
                onNavigateToSettings,
                settingsModifier
                    .align(Alignment.TopEnd),
                getModifier, fields)
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
                            items(uiState.deckList.size) { index ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            if (!fields.mainClicked.value) {
                                                fields.mainClicked.value = true
                                                onNavigateToDeck(uiState.deckList[index].id)
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = lineModifier
                                    ) {
                                        Text(
                                            text = uiState.deckList[index].name,
                                            textAlign = TextAlign.Center,
                                            fontSize = 30.sp,
                                            color = getModifier.titleColor(),
                                            style = MaterialTheme.typography.titleLarge,
                                            modifier = Modifier
                                                .align(Alignment.Center)
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