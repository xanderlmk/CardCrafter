package com.example.flashcards.views.cardViews.cardDeckViews


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.example.flashcards.model.CardState
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.views.miscFunctions.BackButton
import com.example.flashcards.views.miscFunctions.GetModifier
import com.example.flashcards.views.miscFunctions.NoDueCards
import com.example.flashcards.views.miscFunctions.loading
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
        val dueCards = remember {mutableStateOf(cardList.allCards)}

        Box(
            modifier = getModifier.boxViewsModifier()
        ) {
            BackButton(
                onBackClick = {
                    onNavigate()
                },
                modifier = getModifier.backButtonModifier(),
                getModifier = getModifier
            )
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                if (cardList.allCards.isEmpty()) {
                    LaunchedEffect(Unit) {
                        viewModel.getDueCards(deck.id, cardTypeViewModel)
                        if (viewModel.getState() == CardState.Finished) {
                            dueCards.value = cardList.allCards
                        }
                    }
                    if (cardList.allCards.isEmpty()) {
                        NoDueCards(getModifier)
                    }
                } else {
                    val isCardUpdated = remember { mutableStateOf(false) }
                    if (index.intValue < dueCards.value.size
                    ) {
                        if (!show) {
                            if (!loading.value && viewModel.getState() == CardState.Finished) {
                                show = frontCard(
                                    Pair(
                                        dueCards.value[index.intValue].card,
                                        dueCards.value[index.intValue]
                                    ),
                                    getModifier
                                )
                                clicked = false
                            } else {
                                LaunchedEffect(Unit) {
                                    loading.value = viewModel.getDueCards(
                                        deck.id,
                                        cardTypeViewModel
                                    )
                                    dueCards.value = cardList.allCards
                                    viewModel.transitionTo(CardState.Finished)
                                }
                            }
                        } else {
                            val good =
                                ((dueCards.value[index.intValue].card.passes + 1) * deck.goodMultiplier).toInt()
                            val hard = if (dueCards.value[index.intValue].card.passes > 0)
                                ((dueCards.value[index.intValue].card.passes + 1) * deck.badMultiplier).toInt()
                            else (dueCards.value[index.intValue].card.passes * deck.badMultiplier).toInt()
                            LaunchedEffect(loading.value) {
                                loading.value = true
                            }
                            LaunchedEffect(isCardUpdated.value) {
                                isCardUpdated.value = false
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.BottomStart
                            ) {
                                BackCard(
                                    Pair(
                                        dueCards.value[index.intValue].card,
                                        dueCards.value[index.intValue]
                                    ),
                                    getModifier
                                )
                                println("index: ${index.intValue}, size: ${dueCards.value.size}")
                                Row(
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = {
                                            if (!clicked) {
                                                coroutineScope.launch {
                                                    viewModel.transitionTo(CardState.Loading)
                                                    clicked = true
                                                    //cardUiState.cardList[index.intValue].passes = 0
                                                    dueCards.value[index.intValue].card.passes = 0
                                                    isCardUpdated.value = handleCardUpdate(
                                                        //cardUiState.cardList[index.intValue],
                                                        dueCards.value[index.intValue].card,
                                                        false, viewModel, deck.goodMultiplier,
                                                        deck.badMultiplier
                                                    )
                                                    show = !show
                                                }
                                                coroutineScope.launch {
                                                    while (!isCardUpdated.value) {
                                                        delay(36)
                                                    }
                                                    loading()
                                                    clicked = false
                                                }
                                            }
                                        },
                                        modifier = Modifier.padding(top = 48.dp),
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
                                                            dueCards.value[index.intValue].card,
                                                            false,
                                                            viewModel,
                                                            deck.goodMultiplier,
                                                            deck.badMultiplier
                                                        )
                                                        show = !show
                                                    }
                                                    coroutineScope.launch {
                                                        while (!isCardUpdated.value) {
                                                            delay(36)
                                                        }
                                                        index.intValue = if (dueCards.value.isNotEmpty()) {
                                                            ((index.intValue + 1) % dueCards.value.size)
                                                        } else { 0}
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
                                                            dueCards.value[index.intValue].card,
                                                            true,
                                                            viewModel,
                                                            deck.goodMultiplier,
                                                            deck.badMultiplier
                                                        )
                                                        show = !show
                                                    }
                                                    coroutineScope.launch {
                                                        while (!isCardUpdated.value) {
                                                            delay(36)
                                                        }
                                                        index.intValue = if (dueCards.value.isNotEmpty()) {
                                                            ((index.intValue + 1) % dueCards.value.size)
                                                        } else { 0}
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
                        }
                    } else {
                        LaunchedEffect(Unit) {
                            index.intValue = 0
                            viewModel.getDueCards(deck.id, cardTypeViewModel)
                            dueCards.value = cardList.allCards
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
}