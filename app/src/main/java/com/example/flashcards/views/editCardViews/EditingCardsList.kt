package com.example.flashcards.views.editCardViews

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.flashcards.controller.viewModels.CardViewModel
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.ui.theme.backgroundColor
import com.example.flashcards.ui.theme.buttonColor
import com.example.flashcards.ui.theme.textColor
import com.example.flashcards.ui.theme.titleColor
import com.example.flashcards.R
import com.example.flashcards.controller.viewModels.CardTypeViewModel
import com.example.flashcards.views.miscFunctions.BackButton
import com.example.flashcards.views.miscFunctions.BasicCardQuestion
import com.example.flashcards.views.miscFunctions.HintCardQuestion
import com.example.flashcards.views.miscFunctions.LoadingText
import com.example.flashcards.views.miscFunctions.ThreeCardQuestion
import com.example.flashcards.views.miscFunctions.delayNavigate
import kotlinx.coroutines.launch

class DeckEditView(private var viewModel: CardViewModel,
                   var navController: NavController,
    var cardTypeViewModel: CardTypeViewModel){
    var selectedCard = mutableStateOf<Card?>(null)
    var isEditing =  mutableStateOf(false)
    var navigate = mutableStateOf(false)

    @SuppressLint("CoroutineCreationDuringComposition")

    @Composable
    fun ViewFlashCards(deck : Deck, onNavigate: () -> Unit) {
        //val loading = stringResource(R.string.loading)
        //var deckWithCards by remember { mutableStateOf(DeckWithCards(Deck(0, loading.toString() ), emptyList())) }
        val cardListUiState by cardTypeViewModel.cardListUiState.collectAsState()
        val cardUiState by viewModel.cardUiState.collectAsState()
        val coroutineScope = rememberCoroutineScope()
        coroutineScope.launch {
            viewModel.getDeckWithCards(deck.id, cardTypeViewModel)
        }

        val presetModifier = Modifier
            .padding(top = 16.dp,start = 16.dp, end = 16.dp)
            .size(54.dp)
        if (isEditing.value && selectedCard.value != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                LoadingText()
            }
            LaunchedEffect(navigate.value) {
                if (!navigate.value) {
                    delayNavigate()
                    navController.navigate("EditingCard/${selectedCard.value?.id}/${deck.id}")
                }
            }
        } else {
            navigate.value = false
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    item {
                        BackButton(
                            onBackClick = { onNavigate() },
                            modifier = presetModifier
                        )
                    }
                    item {
                        Text(
                            text = stringResource(R.string.deck) + ": ${deck.name}",
                            fontSize = 35.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 25.dp,start = 10.dp, end = 10.dp),
                            textAlign = TextAlign.Center,
                            color = titleColor
                        )
                    }
                    items(cardUiState.cardList.size) { index ->
                        Button(
                            onClick = {
                                selectedCard.value = cardUiState.cardList[index] // Set selected card to edit
                                isEditing.value = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = buttonColor,
                                contentColor = textColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            if (cardListUiState.allCards.size == cardUiState.cardList.size){
                                when(cardUiState.cardList[index].type){
                                    "basic" -> {
                                        val basicCard =
                                            cardListUiState.allCards[index].basicCard
                                        basicCard?.let { BasicCardQuestion(basicCard) }
                                    }
                                    "three" ->{
                                        val threeCard =
                                            cardListUiState.allCards[index].threeFieldCard
                                        threeCard?.let { ThreeCardQuestion(threeCard) }
                                    }
                                    "hint" -> {
                                        val hintCard =
                                            cardListUiState.allCards[index].hintCard
                                        hintCard?.let{ HintCardQuestion(hintCard) }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}