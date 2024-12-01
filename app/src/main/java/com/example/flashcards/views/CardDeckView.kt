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
       // var currentCard by remember { mutableStateOf<Card?>(null) }
        var index by remember { mutableIntStateOf(0) }
        //var index = 0
        val coroutineScope = rememberCoroutineScope()
        val presetModifier = Modifier
            .padding(top = 16.dp,start = 16.dp, end = 16.dp)
            .size(54.dp)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
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
                //if (currentCard == null && cardUiState.cardList.isEmpty()) {
                if (cardUiState.cardList.isEmpty()) {
                    // viewModel.getDueCards(deckId)
                    LaunchedEffect(Unit) {
                        coroutineScope.launch {
                            viewModel.getDueCards(deckId)
                        }
                    }
                    if (cardUiState.cardList.isEmpty()) {
                        Text(
                            "No Due Cards",
                            color = textColor
                        )
                    }
                } else {
                    val loading = remember { mutableStateOf(false) }
                    if (index < cardUiState.cardList.size) {
                        if (!show) {
                            if (!loading.value) {
                                show = frontCard(cardUiState.cardList[index])
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
                            val good = ((cardUiState.cardList[index].passes + 1) * 1.5).toInt()
                            val hard = (cardUiState.cardList[index].passes * 0.5).toInt()
                            loading.value = true
                            Box(modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                                contentAlignment = Alignment.BottomStart) {
                                Column {
                                    BackCard(cardUiState.cardList[index])
                                }
                                Row(
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                cardUiState.cardList[index].passes = 0
                                                handleCardUpdate(
                                                    cardUiState.cardList[index],
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
                                                        cardUiState.cardList[index],
                                                        false, viewModel
                                                    )
                                                    index = (index + 1) % cardUiState.cardList.size
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
                                                        cardUiState.cardList[index],
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
                        index = 0
                    }
                }
            }
        }
    }
}