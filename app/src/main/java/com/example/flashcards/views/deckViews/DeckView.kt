package com.example.flashcards.views.deckViews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcards.model.tablesAndApplication.Deck
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import com.example.flashcards.R
import com.example.flashcards.controller.viewModels.cardViewsModels.CardDeckViewModel
import com.example.flashcards.model.uiModels.Fields
import com.example.flashcards.views.miscFunctions.AddCardButton
import com.example.flashcards.views.miscFunctions.BackButton
import com.example.flashcards.views.miscFunctions.SettingsButton
import com.example.flashcards.model.uiModels.View
import com.example.flashcards.ui.theme.GetModifier


class DeckView(
    private var fields : Fields,
    private var dueCardsViewModel: CardDeckViewModel,
    private var getModifier: GetModifier
) {
    @Composable
    fun ViewEditDeck(
        deck: Deck,
        onNavigate: () -> Unit,
        whichView : View,
        onNavigateToWhichView: () -> Unit) {
        val view = remember {whichView}
        val coroutineScope = rememberCoroutineScope()
        LaunchedEffect(view.whichView.intValue) {
                when (view.whichView.intValue) {
                    0 -> {
                        view.onView.value = false
                    }
                    else -> {
                        fields.inDeckClicked.value = true
                        onNavigateToWhichView()
                    }
                }
        }
        Box(
            modifier = getModifier
                .boxViewsModifier()
        ) {
            BackButton(
                onBackClick = { onNavigate() },
                modifier =  getModifier.backButtonModifier(),
                getModifier = getModifier
            )
            SettingsButton(
                onNavigateToEditDeck = {
                    if (!fields.inDeckClicked.value) {
                        view.whichView.intValue = 3
                        fields.inDeckClicked.value = true
                    }
                                       },
                onNavigateToEditCards = {
                    if (!fields.inDeckClicked.value) {
                        view.whichView.intValue = 4
                        fields.inDeckClicked.value = true
                    }
                                        } ,
                modifier = getModifier.settingsButtonModifier()
                    .align(Alignment.TopEnd),
                getModifier = getModifier,
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
                        color = getModifier.titleColor(),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 50.dp)
                    )
                }
                    Column(
                        modifier = Modifier
                        .weight(2f)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 48.dp, start = 20.dp, end = 20.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier.weight(1f)
                            ) {
                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            dueCardsViewModel.getDueCards(deck.id)
                                        }
                                        if (!fields.inDeckClicked.value) {
                                            view.whichView.intValue = 2
                                            fields.inDeckClicked.value = true
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth(fraction = 0.55f)
                                        .align(Alignment.Center),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = getModifier.secondaryButtonColor(),
                                        contentColor = getModifier.buttonTextColor()
                                    )
                                ) {
                                    Text(stringResource(R.string.start_deck))
                                }
                            }
                        }
                    }
                Column(
                    modifier = getModifier.addButtonModifier()
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
                                    view.whichView.intValue = 1
                                    fields.inDeckClicked.value = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}