package com.example.flashcards.views.cardViews


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
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.views.miscFunctions.BackButton
import com.example.flashcards.views.miscFunctions.GetModifier
import com.example.flashcards.views.miscFunctions.NoDueCards
import com.example.flashcards.views.miscFunctions.loading


class CardDeckView(private val viewModel: CardViewModel,
    private var cardTypeViewModel: CardTypeViewModel,
    private var getModifier: GetModifier
    ){
    @Composable
    fun ViewCard(deck: Deck, onNavigate: () -> Unit) {
        //val viewModel : CardViewModel = viewModel(factory = AppViewModelProvider.Factory)
        val cardUiState by viewModel.cardUiState.collectAsState()
        val cardList by cardTypeViewModel.cardListUiState.collectAsState()
        var show by remember { mutableStateOf(false) }
        val index = remember { mutableIntStateOf(0) }
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            coroutineScope.launch {
                viewModel.getDueCards(deck.id,cardTypeViewModel)
            }
        }
        Box(
            modifier = getModifier.boxViewsModifier()
        ){
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
                //if (currentCard == null && cardUiState.cardList.isEmpty()) {
                if (cardUiState.cardList.isEmpty() || cardList.allCards.isEmpty()) {
                    LaunchedEffect(Unit) {
                        viewModel.getDueCards(deck.id, cardTypeViewModel)
                    }
                    if (cardUiState.cardList.isEmpty() || cardList.allCards.isEmpty()) {
                        NoDueCards(getModifier)
                   }
                } else {
                    val loading = remember { mutableStateOf(false) }
                    // any changes in size will make this launch.
                    LaunchedEffect(cardUiState.cardList.size) {
                        coroutineScope.launch {
                                viewModel.getDueCards(deck.id,cardTypeViewModel)
                        }
                    }
                    LaunchedEffect(cardList.allCards.size) {
                        coroutineScope.launch {
                            viewModel.getDueCards(deck.id,cardTypeViewModel)
                        }
                    }
                    if (index.intValue < cardUiState.cardList.size &&
                        index.intValue < cardList.allCards.size) {
                        // make sure they are the same size or else
                        // crash lol
                        if(cardList.allCards.size != cardUiState.cardList.size){
                            viewModel.getDueCards(deck.id,cardTypeViewModel)
                        }
                        if (!show) {
                            if (!loading.value) {
                                //currentCard = cardUiState.cardList[index.intValue]
                                show = frontCard(
                                    Pair(
                                        cardUiState.cardList[index.intValue],
                                        cardList.allCards[index.intValue]
                                    ),
                                    getModifier
                                )
                                //show = frontCard(currentCard!!)

                            }
                        } else {
                            val good = ((cardUiState.cardList[index.intValue].passes + 1) * 1.5).toInt()
                            val hard = if(cardUiState.cardList[index.intValue].passes>0)
                                    ((cardUiState.cardList[index.intValue].passes + 1 ) * 0.5).toInt()
                            else (cardUiState.cardList[index.intValue].passes * 0.5).toInt()
                            loading.value = true

                            Box(modifier = Modifier
                                .fillMaxSize(),
                                contentAlignment = Alignment.BottomStart) {

                                BackCard(
                                    Pair(
                                        cardUiState.cardList[index.intValue],
                                        cardList.allCards[index.intValue]
                                    ),
                                    getModifier
                                )
                                    //BackCard(currentCard!!)
                                Row(
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                cardUiState.cardList[index.intValue].passes = 0
                                                handleCardUpdate(
                                                    cardUiState.cardList[index.intValue],
                                                    false, viewModel,deck.multiplier
                                                )
                                                    viewModel.getDueCards(deck.id,cardTypeViewModel)
                                                show = !show
                                            }
                                            coroutineScope.launch {
                                                loading.value = loading()
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
                                                coroutineScope.launch {
                                                    handleCardUpdate(
                                                        cardUiState.cardList[index.intValue],
                                                        false, viewModel, deck.multiplier
                                                    )
                                                    index.intValue = ((index.intValue + 1) % cardUiState.cardList.size)

                                                    viewModel.getDueCards(deck.id,cardTypeViewModel)
                                                    show = !show
                                                }
                                                coroutineScope.launch {
                                                    loading.value = loading()
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
                                                coroutineScope.launch {
                                                    handleCardUpdate(
                                                        cardUiState.cardList[index.intValue],
                                                        true, viewModel, deck.multiplier
                                                    )
                                                        viewModel.getDueCards(deck.id,cardTypeViewModel)
                                                    show = !show
                                                }
                                                coroutineScope.launch {
                                                    loading.value = loading()
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
                        index.intValue = 0
                    }
                }
            }
        }
    }
}