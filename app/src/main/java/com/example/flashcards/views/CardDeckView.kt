package com.example.flashcards.views


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
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcards.controller.CardUiState
import com.example.flashcards.controller.CardViewModel
import com.example.flashcards.controller.handleCardUpdate
import com.example.flashcards.model.Card
import com.example.flashcards.ui.theme.backgroundColor
import com.example.flashcards.ui.theme.buttonColor
import com.example.flashcards.ui.theme.textColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class CardDeckView(private val viewModel: CardViewModel){
    @Composable
    fun ViewCard(deckId: Int, onNavigate: () -> Unit) {
        //val viewModel : CardViewModel = viewModel(factory = AppViewModelProvider.Factory)
        val cardUiState by viewModel.cardUiState.collectAsState()
        var show by remember { mutableStateOf(false) }
       var currentCard by remember { mutableStateOf<Card?>(null) }
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
                onBackClick = { onNavigate() },
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
                if (cardUiState.cardList.isEmpty()) {
                    viewModel.getDueCards(deckId)
                    if (cardUiState.cardList.isEmpty()) {
                        Text(
                            "No Due Cards",
                            color = textColor
                        )
                   }
                } else {
                    val loading = remember { mutableStateOf(false) }
                    // any changes in size will make this launch.
                    LaunchedEffect(cardUiState.cardList.size) {
                        coroutineScope.launch {
                            viewModel.getDueCards(deckId)
                        }
                    }
                    if (index.intValue < cardUiState.cardList.size) {
                        if (!show) {
                            if (!loading.value) {
                                //currentCard = cardUiState.cardList[index.intValue]
                                show = frontCard(cardUiState.cardList[index.intValue])
                                //show = frontCard(currentCard!!)

                            }
                            else {
                                Text(
                                    "...",
                                    fontSize = 35.sp,
                                    textAlign = TextAlign.Center,
                                    color = textColor,
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        } else {
                            val good = ((cardUiState.cardList[index.intValue].passes + 1) * 1.5).toInt()
                            val hard = (cardUiState.cardList[index.intValue].passes * 0.5).toInt()
                            loading.value = true
                            println("Index : ${index.intValue}")
                            println("${cardUiState.cardList.size}")

                            Box(modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                                contentAlignment = Alignment.BottomStart) {
                                Column {
                                    BackCard(cardUiState.cardList[index.intValue])
                                    //BackCard(currentCard!!)
                                }
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
                                                viewModel.getDueCards(deckId)
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
                                    ) { Text("Again") }

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
                                                    index.intValue = (index.intValue + 1) % cardUiState.cardList.size
                                                    viewModel.getDueCards(deckId)
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
                                        ) { Text("Hard") }
                                        Text(
                                            "$hard days",
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
                                                    viewModel.getDueCards(deckId)
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
                                        ) { Text("Good") }
                                        Text(
                                            "$good days",
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