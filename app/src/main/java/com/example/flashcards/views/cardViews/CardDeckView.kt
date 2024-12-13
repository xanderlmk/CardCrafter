package com.example.flashcards.views.cardViews


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.example.flashcards.ui.theme.backgroundColor
import com.example.flashcards.ui.theme.buttonColor
import com.example.flashcards.ui.theme.textColor
import kotlinx.coroutines.launch
import androidx.compose.ui.res.stringResource
import com.example.flashcards.R
import com.example.flashcards.controller.viewModels.CardTypeViewModel
import com.example.flashcards.views.miscFunctions.BackButton
import com.example.flashcards.views.miscFunctions.LoadingText
import com.example.flashcards.views.miscFunctions.NoDueCards
import com.example.flashcards.views.miscFunctions.loading


class CardDeckView(private val viewModel: CardViewModel,
    var cardTypeViewModel: CardTypeViewModel,
    ){
    @Composable
    fun ViewCard(deckId: Int, onNavigate: () -> Unit) {
        //val viewModel : CardViewModel = viewModel(factory = AppViewModelProvider.Factory)
        val cardUiState by viewModel.cardUiState.collectAsState()
        val cardList by cardTypeViewModel.cardListUiState.collectAsState()
        var show by remember { mutableStateOf(false) }
       //var currentCard by remember { mutableStateOf<Card?>(null) }
        val index = remember { mutableIntStateOf(0) }
        //var index.intValue = 0
        val coroutineScope = rememberCoroutineScope()
        val presetModifier = Modifier
            .padding(top = 16.dp,start = 16.dp, end = 16.dp)
            .size(54.dp)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .background(backgroundColor)
        ){
            BackButton(
                onBackClick = {
                    onNavigate()
                },
                modifier = presetModifier
            )
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                /*LaunchedEffect(Unit) {
                    coroutineScope.launch {
                        viewModel.getDueCards(deckId)
                    }
                }*/
                //if (currentCard == null && cardUiState.cardList.isEmpty()) {
                if (cardUiState.cardList.isEmpty() || cardList.allCards.isEmpty()) {
                    LaunchedEffect(Unit) {
                        viewModel.getDueCards(deckId, cardTypeViewModel)
                    }
                    if (cardUiState.cardList.isEmpty() || cardList.allCards.isEmpty()) {
                        NoDueCards()
                   }
                } else {
                    val loading = remember { mutableStateOf(false) }
                    // any changes in size will make this launch.
                    LaunchedEffect(cardUiState.cardList.size) {
                        coroutineScope.launch {
                                viewModel.getDueCards(deckId,cardTypeViewModel)
                        }
                    }
                    LaunchedEffect(cardList.allCards.size) {
                        coroutineScope.launch {
                            viewModel.getDueCards(deckId,cardTypeViewModel)
                        }
                    }
                    if (index.intValue < cardUiState.cardList.size &&
                        index.intValue < cardList.allCards.size) {
                        // make sure they are the same size or else
                        // crash lol
                        if(cardList.allCards.size != cardUiState.cardList.size){
                            viewModel.getDueCards(deckId,cardTypeViewModel)
                        }
                        if (!show) {
                            if (!loading.value) {
                                //currentCard = cardUiState.cardList[index.intValue]
                                show = frontCard(
                                    Pair(
                                        cardUiState.cardList[index.intValue],
                                        cardList.allCards[index.intValue]
                                    )
                                )
                                //show = frontCard(currentCard!!)

                            }
                            else {
                                LoadingText()
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
                                    )
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
                                                    false, viewModel
                                                )
                                                    viewModel.getDueCards(deckId,cardTypeViewModel)
                                                show = !show
                                            }
                                            coroutineScope.launch {
                                                loading.value = loading()
                                            }
                                        },
                                        modifier = Modifier.padding(top = 48.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = buttonColor,
                                            contentColor = textColor
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
                                                        false, viewModel
                                                    )
                                                    index.intValue = ((index.intValue + 1) % cardUiState.cardList.size)

                                                    viewModel.getDueCards(deckId,cardTypeViewModel)
                                                    show = !show
                                                }
                                                coroutineScope.launch {
                                                    loading.value = loading()
                                                }
                                            },
                                            modifier = Modifier.padding(top = 48.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = buttonColor,
                                                contentColor = textColor
                                            )
                                        ) { Text(stringResource(R.string.hard)) }
                                        Text(
                                            "$hard " + stringResource(R.string.days),
                                            color = textColor
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
                                                        true, viewModel
                                                    )
                                                        viewModel.getDueCards(deckId,cardTypeViewModel)
                                                    show = !show
                                                }
                                                coroutineScope.launch {
                                                    loading.value = loading()
                                                }
                                            },
                                            modifier = Modifier.padding(top = 48.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = buttonColor,
                                                contentColor = textColor
                                            )
                                        ) { Text(stringResource(R.string.good)) }
                                        Text(
                                            "$good " + stringResource(R.string.days),
                                            color = textColor
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