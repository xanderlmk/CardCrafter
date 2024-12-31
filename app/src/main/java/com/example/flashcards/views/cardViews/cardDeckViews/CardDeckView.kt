package com.example.flashcards.views.cardViews.cardDeckViews


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.flashcards.controller.viewModels.CardViewModel
import com.example.flashcards.controller.handleCardUpdate
import kotlinx.coroutines.launch
import androidx.compose.ui.res.stringResource
import com.example.flashcards.R
import com.example.flashcards.controller.viewModels.CardTypeViewModel
import com.example.flashcards.model.uiModels.CardState
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.views.miscFunctions.BackButton
import com.example.flashcards.ui.theme.GetModifier
import com.example.flashcards.views.miscFunctions.NoDueCards
import kotlinx.coroutines.delay


class CardDeckView(
    private var viewModel: CardViewModel,
    private var cardTypeViewModel: CardTypeViewModel,
    private var getModifier: GetModifier
) {
    @Composable
    fun ViewCard(deck: Deck, onNavigate: () -> Unit) {
        val cardList by cardTypeViewModel.cardListUiState.collectAsState()
        var show by remember { mutableStateOf(false) }
        val index = remember { mutableIntStateOf(0) }
        val coroutineScope = rememberCoroutineScope()
        var clicked by remember { mutableStateOf(false) }
        val loading = remember { mutableStateOf(false) }
        var dueCards by remember { mutableStateOf(cardList.allCards) }
        val isCardUpdated = remember { mutableStateOf(false) }
        val scrollState = rememberScrollState()

        Box(
            contentAlignment =
            if (cardList.allCards.isEmpty()) {
                Alignment.Center
            } else {
                Alignment.TopCenter
            },
            modifier = getModifier
                .boxViewsModifier()
                .verticalScroll(scrollState)
        ) {
            BackButton(
                onBackClick = {
                    getModifier.clickedChoice.value = '?'
                    onNavigate()
                },
                modifier = getModifier
                    .backButtonModifier()
                    .align(Alignment.TopStart),
                getModifier = getModifier
            )
            if (cardList.allCards.isEmpty()) {
                LaunchedEffect(Unit) {
                    viewModel.getDueCards(deck.id, cardTypeViewModel)
                    if (viewModel.getState() == CardState.Finished) {
                        dueCards = cardList.allCards
                    }
                }
                if (cardList.allCards.isEmpty()) {
                    NoDueCards(getModifier)
                }
            } else {
                if (index.intValue < dueCards.size) {
                    if (!show) {
                        if (!loading.value && viewModel.getState() == CardState.Finished) {
                            FrontCard(
                                Pair(
                                    dueCards[index.intValue].card,
                                    dueCards[index.intValue]
                                ),
                                getModifier
                            )
                            Button(
                                onClick = {
                                    show = true
                                },
                                modifier = Modifier
                                    .align(Alignment.BottomCenter) // Align to the bottom center
                                    .padding(bottom = 16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = getModifier.secondaryButtonColor(),
                                    contentColor = getModifier.buttonTextColor()
                                )
                            ) {
                                Text(stringResource(R.string.show_answer))
                            }
                            clicked = false
                        } else {
                            LaunchedEffect(Unit) {
                                loading.value = viewModel.getDueCards(
                                    deck.id,
                                    cardTypeViewModel
                                )
                                dueCards = cardList.allCards
                                viewModel.transitionTo(CardState.Finished)
                            }
                        }
                    } else {
                        LaunchedEffect(loading.value) {
                            loading.value = true
                        }
                        LaunchedEffect(isCardUpdated.value) {
                            isCardUpdated.value = false
                        }
                        BackCard(
                            Pair(
                                dueCards[index.intValue].card,
                                dueCards[index.intValue]
                            ),
                            getModifier
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.BottomCenter)
                        ) {
                            val good =
                                ((dueCards[index.intValue].card.passes + 1) * deck.goodMultiplier).toInt()
                            val hard = if (dueCards[index.intValue].card.passes > 0)
                                ((dueCards[index.intValue].card.passes + 1) * deck.badMultiplier).toInt()
                            else (dueCards[index.intValue].card.passes * deck.badMultiplier).toInt()
                            Button(
                                onClick = {
                                    if (!clicked) {
                                        coroutineScope.launch {
                                            viewModel.transitionTo(CardState.Loading)
                                            clicked = true
                                            //cardUiState.cardList[index.intValue].passes = 0
                                            dueCards[index.intValue].card.passes = 0
                                            isCardUpdated.value = handleCardUpdate(
                                                //cardUiState.cardList[index.intValue],
                                                dueCards[index.intValue].card,
                                                false, viewModel, deck.goodMultiplier,
                                                deck.badMultiplier
                                            )
                                            getModifier.clickedChoice.value = '?'
                                            show = !show
                                        }
                                        coroutineScope.launch {
                                            while (!isCardUpdated.value) {
                                                delay(25)
                                            }
                                            scrollState.animateScrollTo(0)
                                            clicked = false
                                        }
                                    }
                                },
                                modifier = Modifier.padding(top = 48.dp, bottom = 24.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = getModifier.secondaryButtonColor(),
                                    contentColor = getModifier.buttonTextColor()
                                )
                            ) { Text(stringResource(R.string.again)) }

                            Column(
                                verticalArrangement = Arrangement.Top,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Button(
                                    onClick = {
                                        if (!clicked) {
                                            coroutineScope.launch {
                                                viewModel.transitionTo(CardState.Loading)
                                                clicked = true
                                                isCardUpdated.value = handleCardUpdate(
                                                    //cardUiState.cardList[index.intValue],
                                                    dueCards[index.intValue].card,
                                                    false,
                                                    viewModel,
                                                    deck.goodMultiplier,
                                                    deck.badMultiplier
                                                )
                                                getModifier.clickedChoice.value = '?'
                                                show = !show
                                            }
                                            coroutineScope.launch {
                                                while (!isCardUpdated.value) {
                                                    delay(36)
                                                }
                                                index.intValue =
                                                    if (dueCards.isNotEmpty()) {
                                                        ((index.intValue + 1) % dueCards.size)
                                                    } else {
                                                        0
                                                    }
                                                scrollState.animateScrollTo(0)
                                                clicked = false
                                            }
                                        }
                                    },
                                    modifier = Modifier.padding(top = 48.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = getModifier.secondaryButtonColor(),
                                        contentColor = getModifier.buttonTextColor()
                                    )
                                ) { Text(stringResource(R.string.hard)) }
                                Text(
                                    "$hard " + stringResource(R.string.days),
                                    color = getModifier.titleColor()
                                )

                            }
                            Column(
                                verticalArrangement = Arrangement.Top,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Button(
                                    onClick = {
                                        if (!clicked) {
                                            clicked = true
                                            coroutineScope.launch {
                                                viewModel.transitionTo(CardState.Loading)
                                                isCardUpdated.value = handleCardUpdate(
                                                    //cardUiState.cardList[index.intValue],
                                                    dueCards[index.intValue].card,
                                                    true,
                                                    viewModel,
                                                    deck.goodMultiplier,
                                                    deck.badMultiplier
                                                )
                                                getModifier.clickedChoice.value = '?'
                                                show = !show
                                            }
                                            coroutineScope.launch {
                                                while (!isCardUpdated.value) {
                                                    delay(36)
                                                }
                                                index.intValue =
                                                    if (dueCards.isNotEmpty()) {
                                                        ((index.intValue + 1) % dueCards.size)
                                                    } else {
                                                        0
                                                    }
                                                scrollState.animateScrollTo(0)
                                                clicked = false
                                            }
                                        }
                                    },
                                    modifier = Modifier.padding(top = 48.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = getModifier.secondaryButtonColor(),
                                        contentColor = getModifier.buttonTextColor()
                                    )
                                ) { Text(stringResource(R.string.good)) }
                                Text(
                                    "$good " + stringResource(R.string.days),
                                    color = getModifier.titleColor()
                                )
                            }
                        }
                    }
                } else {
                    LaunchedEffect(Unit) {
                        index.intValue = 0
                        viewModel.getDueCards(deck.id, cardTypeViewModel)
                        dueCards = cardList.allCards
                    }
                    LaunchedEffect(loading.value) {
                        loading.value = true
                    }
                    LaunchedEffect(isCardUpdated.value) {
                        isCardUpdated.value = false
                    }
                }
            }
        }
    }
}