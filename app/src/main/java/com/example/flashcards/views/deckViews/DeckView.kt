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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.flashcards.controller.AppViewModelProvider
import com.example.flashcards.controller.viewModels.CardViewModel
import kotlinx.coroutines.launch
import com.example.flashcards.R
import com.example.flashcards.controller.viewModels.CardTypeViewModel
import com.example.flashcards.views.miscFunctions.AddCardButton
import com.example.flashcards.views.miscFunctions.BackButton
import com.example.flashcards.views.miscFunctions.ChoosingView
import com.example.flashcards.views.miscFunctions.SettingsButton
import com.example.flashcards.views.miscFunctions.View
import com.example.flashcards.views.miscFunctions.GetModifier


class DeckView(
    private var navController: NavController,
    private var cardTypeViewModel: CardTypeViewModel,
    private var getModifier: GetModifier
) {
    @Composable
    fun ViewEditDeck(deck: Deck, onNavigate: () -> Unit, whichView : View) {
        val cardViewModel: CardViewModel = viewModel(factory = AppViewModelProvider.Factory)
        val view = remember {whichView}
        val choosingView = ChoosingView(navController)
        val coroutineScope = rememberCoroutineScope()
        when (view.whichView.intValue) {
            0 -> {
                view.onView.value = false
            }
            1 -> {
                choosingView.WhichScreen(deck,view)
            }
            2 -> {
                choosingView.WhichScreen(deck,view)
            }
            3 -> {
                choosingView.WhichScreen(deck,view)
            }
            4 -> {
                choosingView.WhichScreen(deck,view)
            }
        }
        Box(
            modifier = getModifier.boxViewsModifier()
        ) {
            BackButton(
                onBackClick = { onNavigate() },
                modifier =  getModifier.backButtonModifier(),
                getModifier = getModifier
            )
            SettingsButton(
                onNavigateToEditDeck = { view.whichView.intValue = 3 },
                onNavigateToEditCards = { view.whichView.intValue = 4 } ,
                modifier = getModifier.settingsButtonModifier()
                    .align(Alignment.TopEnd),
                getModifier = getModifier
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
                        fontSize = 40.sp,
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
                                            cardViewModel.getDueCards(deck.id,
                                                cardTypeViewModel)
                                        }
                                        view.whichView.intValue = 2
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
                                view.whichView.intValue = 1
                            }
                        )
                    }
                }
            }
        }
    }
}