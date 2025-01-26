package com.example.flashcards.views.cardViews.cardDeckViews

import android.util.Log
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.flashcards.R
import com.example.flashcards.controller.handleCardUpdate
import com.example.flashcards.controller.updateDecksCardList
import com.example.flashcards.controller.viewModels.cardViewsModels.CardDeckViewModel
import com.example.flashcards.model.uiModels.CardState
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.views.miscFunctions.BackButton
import com.example.flashcards.ui.theme.GetModifier
import com.example.flashcards.views.miscFunctions.AgainText
import com.example.flashcards.views.miscFunctions.GoodText
import com.example.flashcards.views.miscFunctions.HardText
import com.example.flashcards.views.miscFunctions.NoDueCards
import com.example.flashcards.views.miscFunctions.RedoCardButton
import kotlinx.coroutines.delay
import java.util.Date

class CardDeckView(
    private var cardDeckViewModel: CardDeckViewModel,
    private var getModifier: GetModifier,
) {
    @Composable
    fun ViewCard(
        deck: Deck, onNavigate: () -> Unit
    ) {
        val cardList by cardDeckViewModel.cardDeckUiState.collectAsState()
        val backupList by cardDeckViewModel.backupCardList.collectAsState()
        var dueCards by rememberSaveable { mutableStateOf(cardList.allCards) }

        val errorState by cardDeckViewModel.errorState.collectAsState()
        var show by rememberSaveable { mutableStateOf(false) }
        val index = rememberSaveable { mutableIntStateOf(0) }
        val coroutineScope = rememberCoroutineScope()
        var clicked by remember { mutableStateOf(false) }
        var started by rememberSaveable { mutableStateOf(false) }

        /** These are the cards that will be updated and only changed
         *  at the start and once you traverse through the whole cardList */
        val updatedDueCards by cardDeckViewModel.cardDeckUiState.collectAsState()

        val cardsToUpdate by cardDeckViewModel.cardListToUpdate.collectAsState()

        val scrollState = rememberScrollState()
        Box(
            contentAlignment =
            if (cardList.allCards.isEmpty() || dueCards.isEmpty()) {
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
            RedoCardButton(
                onRedoClick = {
                    coroutineScope.launch {
                        if (index.intValue > 0) {
                            index.intValue -= 1
                            updatedDueCards.savedCardList[index.intValue].card =
                                cardDeckViewModel.getRedoCard(dueCards[index.intValue].card.id)
                            cardList.allCards[index.intValue].card =
                                cardDeckViewModel.getRedoCard(dueCards[index.intValue].card.id)
                                    .also { dueCards[index.intValue].card = it }
                            show = false
                        } else {
                            if (cardList.allCards.isNotEmpty() && started) {
                                index.intValue = cardList.allCards.size - 1
                                updatedDueCards.savedCardList[index.intValue].card =
                                    cardDeckViewModel.getRedoCard(dueCards[index.intValue].card.id)
                                cardList.allCards[index.intValue].card =
                                    cardDeckViewModel.getRedoCard(dueCards[index.intValue].card.id)
                                        .also { dueCards[index.intValue].card = it }
                                show = false
                            } else {
                                if (backupList.isNotEmpty() && started) {
                                    Log.d("CardDeckView", "Backup logic not implemented yet.")
                                }
                            }
                        }
                    }
                },
                modifier = getModifier
                    .redoButtonModifier()
                    .align(Alignment.TopEnd),
                getModifier = getModifier
            )
            if (cardList.allCards.isEmpty() ||
                dueCards.isEmpty()) {
                if(deck.nextReview <= Date()) {
                    LaunchedEffect(Unit) {
                        coroutineScope.launch {
                            cardDeckViewModel.transitionTo(CardState.Loading)
                            cardDeckViewModel.getDueCards(deck)
                            while (cardDeckViewModel.getState() == CardState.Loading) {
                                delay(30)
                            }
                            dueCards = cardList.allCards
                        }
                    }
                }
                NoDueCards(getModifier)

            } else {
                if (index.intValue < dueCards.size) {
                    Text(
                        text = stringResource(R.string.reviews_left) +
                                "${
                                    updatedDueCards
                                        .allCards[index.intValue]
                                        .card.reviewsLeft
                                }",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(start = 46.dp, end = 46.dp, top = 8.dp)
                    )
                    if (!show) {
                        if (cardDeckViewModel.getState() == CardState.Finished) {
                            FrontCard(
                                Pair(
                                    dueCards[index.intValue].card,
                                    dueCards[index.intValue]
                                ),
                                getModifier,
                                Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(bottom = 62.dp)
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
                        }
                    } else {
                        BackCard(
                            Pair(
                                dueCards[index.intValue].card,
                                dueCards[index.intValue]
                            ),
                            getModifier, Modifier
                                .align(Alignment.TopCenter)
                                .padding(bottom = 62.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.BottomCenter)
                        ) {
                            val good =
                                ((dueCards[index.intValue].card.passes + 1) *
                                        deck.goodMultiplier).toInt()
                            val hard = if (dueCards[index.intValue].card.passes > 0)
                                ((dueCards[index.intValue].card.passes + 1) *
                                        deck.badMultiplier).toInt()
                            else (dueCards[index.intValue].card.passes *
                                    deck.badMultiplier).toInt()
                            Column(
                                verticalArrangement = Arrangement.Top,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Button(
                                    onClick = {
                                        if (!clicked) {
                                            coroutineScope.launch {
                                                cardDeckViewModel.transitionTo(CardState.Loading)
                                                clicked = true
                                                updatedDueCards.savedCardList[index.intValue].card =
                                                    handleCardUpdate(
                                                        dueCards[index.intValue].card,
                                                        success = false,
                                                        cardDeckViewModel,
                                                        deck.goodMultiplier,
                                                        deck.badMultiplier,
                                                        deck.reviewAmount,
                                                        again = true
                                                    )
                                                show = !show
                                            }
                                            coroutineScope.launch {
                                                while (cardDeckViewModel.getState() ==
                                                    CardState.Loading
                                                ) {
                                                    delay(36)
                                                }
                                                cardDeckViewModel.addCardToTheUpdateCardsList(
                                                    updatedDueCards.savedCardList
                                                        [index.intValue].card
                                                )
                                                scrollState.animateScrollTo(0)
                                                clicked = false
                                            }
                                        }
                                    },
                                    modifier = Modifier,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = getModifier.secondaryButtonColor(),
                                        contentColor = getModifier.buttonTextColor()
                                    )
                                ) { Text(stringResource(R.string.again)) }
                                AgainText(getModifier)
                            }
                            Column(
                                verticalArrangement = Arrangement.Top,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Button(
                                    onClick = {
                                        if (!clicked) {
                                            coroutineScope.launch {
                                                cardDeckViewModel.transitionTo(CardState.Loading)
                                                clicked = true
                                                updatedDueCards.savedCardList[index.intValue].card =
                                                    handleCardUpdate(
                                                        dueCards[index.intValue].card,
                                                        success = false,
                                                        cardDeckViewModel,
                                                        deck.goodMultiplier,
                                                        deck.badMultiplier,
                                                        deck.reviewAmount,
                                                        again = false
                                                    )
                                                getModifier.clickedChoice.value = '?'
                                                show = !show
                                            }
                                            coroutineScope.launch {
                                                while (cardDeckViewModel.getState() ==
                                                    CardState.Loading
                                                ) {
                                                    delay(36)
                                                }
                                                cardDeckViewModel.addCardToTheUpdateCardsList(
                                                    updatedDueCards.savedCardList
                                                        [index.intValue].card
                                                )
                                                index.intValue = ((index.intValue + 1))
                                                scrollState.animateScrollTo(0)
                                                clicked = false
                                            }
                                        }
                                    },
                                    modifier = Modifier,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = getModifier.secondaryButtonColor(),
                                        contentColor = getModifier.buttonTextColor()
                                    )
                                ) { Text(stringResource(R.string.hard)) }
                                HardText(updatedDueCards, index, hard, getModifier)

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
                                                cardDeckViewModel.transitionTo(CardState.Loading)
                                                updatedDueCards.savedCardList[index.intValue].card =
                                                    handleCardUpdate(
                                                        dueCards[index.intValue].card,
                                                        success = true,
                                                        cardDeckViewModel,
                                                        deck.goodMultiplier,
                                                        deck.badMultiplier,
                                                        deck.reviewAmount,
                                                        again = false
                                                    )
                                                getModifier.clickedChoice.value = '?'
                                                show = !show
                                            }
                                            coroutineScope.launch {
                                                while (cardDeckViewModel.getState() ==
                                                    CardState.Loading
                                                ) {
                                                    delay(36)
                                                }
                                                cardDeckViewModel.addCardToTheUpdateCardsList(
                                                    updatedDueCards.savedCardList
                                                        [index.intValue].card
                                                )
                                                index.intValue = ((index.intValue + 1))
                                                scrollState.animateScrollTo(0)
                                                clicked = false
                                            }
                                        }
                                    },
                                    modifier = Modifier,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = getModifier.secondaryButtonColor(),
                                        contentColor = getModifier.buttonTextColor()
                                    )
                                ) { Text(stringResource(R.string.good)) }
                                GoodText(updatedDueCards, index, good, getModifier)
                            }
                        }
                    }
                } else {
                    LaunchedEffect(Unit) {
                        coroutineScope.launch {
                            cardDeckViewModel.transitionTo(CardState.Loading)

                            /** This function also gets the due cards */
                            updateDecksCardList(
                                deck,
                                //updatedDueCards.savedCardList
                                // .map { cardTypes ->
                                //    cardTypes.card
                                //},
                                cardsToUpdate,
                                cardDeckViewModel
                            )
                            while (cardDeckViewModel.getState() == CardState.Loading) {
                                delay(30)
                            }
                            if (cardList.allCards.isEmpty() || cardList.savedCardList.isEmpty()){
                                cardDeckViewModel.updateNextReview(deck)
                            }
                            if (!errorState?.message.isNullOrEmpty()) {
                                println(errorState?.message)
                            }
                            started = true
                            dueCards = cardList.allCards
                            index.intValue = 0
                        }
                    }
                }
            }
        }
    }
}