package com.example.flashcards.views.cardViews.cardDeckViews

import android.annotation.SuppressLint
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.flashcards.R
import com.example.flashcards.controller.cardHandlers.redoACard
import com.example.flashcards.controller.cardHandlers.returnCard
import com.example.flashcards.controller.cardHandlers.showReviewsLeft
import com.example.flashcards.controller.cardHandlers.updateCTCard
import com.example.flashcards.controller.cardHandlers.updateDecksCardList
import com.example.flashcards.controller.viewModels.cardViewsModels.CardDeckViewModel
import com.example.flashcards.model.uiModels.CardState
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.model.uiModels.Fields
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
    private var cardDeckVM: CardDeckViewModel,
    private var getModifier: GetModifier,
    private var fields: Fields
) {
    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    fun ViewCard(
        deck: Deck, onNavigate: () -> Unit
    ) {
        val backupList by cardDeckVM.backupCardList.collectAsState()
        val sealedCL by cardDeckVM.cardListUiState.collectAsState()

        val cardsToUpdate by cardDeckVM.cardListToUpdate.collectAsState()
        val errorState by cardDeckVM.errorState.collectAsState()

        var dueCTs = remember {
            derivedStateOf { sealedCL.allCTs.toMutableList() }
        }
        var show by rememberSaveable { mutableStateOf(false) }
        val index = rememberSaveable { mutableIntStateOf(0) }
        val coroutineScope = rememberCoroutineScope()
        var clicked by remember { mutableStateOf(false) }
        var started by rememberSaveable { mutableStateOf(false) }

        val scrollState = rememberScrollState()
        Box(
            contentAlignment =
            if (sealedCL.allCTs.isEmpty() || dueCTs.value.isEmpty()) {
                Alignment.Center
            } else {
                Alignment.TopCenter
            },
            modifier = getModifier
                .boxViewsModifier()
                .verticalScroll(scrollState)
        ) {
            if (!fields.leftDueCardView.value) {
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
                                val ct = sealedCL.allCTs[index.intValue]
                                redoACard(ct, cardDeckVM, index.intValue, dueCTs.value)
                                show = false
                            } else {
                                if (sealedCL.allCTs.isNotEmpty() && started) {
                                    index.intValue = sealedCL.allCTs.size - 1
                                    val ct = sealedCL.allCTs[index.intValue]
                                    redoACard(ct, cardDeckVM, index.intValue, dueCTs.value)
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
                if (sealedCL.allCTs.isEmpty() || dueCTs.value.isEmpty() ||
                    deck.nextReview > Date()
                ) {
                    dueCTs.value.clear()
                    NoDueCards(getModifier)

                } else {
                    if (index.intValue < dueCTs.value.size) {
                        Text(
                            text = stringResource(R.string.reviews_left) +
                                    showReviewsLeft(sealedCL.savedCTs[index.intValue]),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(start = 46.dp, end = 46.dp, top = 8.dp)
                        )
                        if (!show) {
                            if (cardDeckVM.getState() == CardState.Finished) {
                                FrontCard(
                                    dueCTs.value[index.intValue],
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
                                        .align(Alignment.BottomCenter)
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
                                dueCTs.value[index.intValue],
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
                                    ((returnCard(dueCTs.value[index.intValue]).passes + 1) *
                                            deck.goodMultiplier).toInt()
                                val hard = if (returnCard(dueCTs.value[index.intValue]).passes > 0)
                                    ((returnCard(dueCTs.value[index.intValue]).passes + 1) *
                                            deck.badMultiplier).toInt()
                                else (returnCard(dueCTs.value[index.intValue]).passes *
                                        deck.badMultiplier).toInt()
                                Column(
                                    verticalArrangement = Arrangement.Top,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Button(
                                        onClick = {
                                            if (!clicked) {
                                                coroutineScope.launch {
                                                    cardDeckVM.transitionTo(CardState.Loading)
                                                    clicked = true
                                                    sealedCL.savedCTs[index.intValue] =
                                                        updateCTCard(
                                                            sealedCL.savedCTs[index.intValue],
                                                            dueCTs.value[index.intValue],
                                                            deck,
                                                            cardDeckVM,
                                                            success = false,
                                                            again = true
                                                        )
                                                    show = !show
                                                }
                                                coroutineScope.launch {
                                                    while (cardDeckVM.getState() ==
                                                        CardState.Loading
                                                    ) {
                                                        delay(36)
                                                    }
                                                    cardDeckVM.addCardToTheUpdateCardsList(
                                                        returnCard(
                                                            sealedCL.savedCTs[index.intValue]
                                                        )
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
                                                    cardDeckVM.transitionTo(CardState.Loading)
                                                    clicked = true
                                                    sealedCL.savedCTs[index.intValue] =
                                                        updateCTCard(
                                                            sealedCL.savedCTs[index.intValue],
                                                            dueCTs.value[index.intValue],
                                                            deck,
                                                            cardDeckVM,
                                                            success = false,
                                                            again = false
                                                        )
                                                    getModifier.clickedChoice.value = '?'
                                                    show = !show
                                                }
                                                coroutineScope.launch {
                                                    while (cardDeckVM.getState() ==
                                                        CardState.Loading
                                                    ) {
                                                        delay(36)
                                                    }
                                                    cardDeckVM.addCardToTheUpdateCardsList(
                                                        returnCard(
                                                            sealedCL.savedCTs[index.intValue]
                                                        )
                                                    )
                                                    index.intValue =
                                                        ((index.intValue + 1))
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
                                    HardText(sealedCL, index, hard, getModifier)

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
                                                    cardDeckVM.transitionTo(CardState.Loading)
                                                    sealedCL.savedCTs[index.intValue] =
                                                        updateCTCard(
                                                            sealedCL.savedCTs[index.intValue],
                                                            dueCTs.value[index.intValue],
                                                            deck,
                                                            cardDeckVM,
                                                            success = true,
                                                            again = false
                                                        )
                                                    getModifier.clickedChoice.value = '?'
                                                    show = !show
                                                }
                                                coroutineScope.launch {
                                                    while (cardDeckVM.getState() ==
                                                        CardState.Loading
                                                    ) {
                                                        delay(36)
                                                    }
                                                    cardDeckVM.addCardToTheUpdateCardsList(
                                                        returnCard(
                                                            sealedCL.savedCTs[index.intValue]
                                                        )
                                                    )
                                                    index.intValue =
                                                        ((index.intValue + 1))
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
                                    GoodText(sealedCL, index, good, getModifier)
                                }
                            }
                        }
                    } else {
                        LaunchedEffect(Unit) {
                            coroutineScope.launch {
                                cardDeckVM.transitionTo(CardState.Loading)
                                updateDecksCardList(
                                    deck,
                                    cardsToUpdate,
                                    cardDeckVM
                                )
                                while (cardDeckVM.getState() == CardState.Loading) {
                                    delay(30)
                                }
                                if (
                                    (sealedCL.allCTs.isEmpty() || sealedCL.savedCTs.isEmpty()) &&
                                    deck.cardsLeft == 0
                                ) {
                                    dueCTs.value.clear()
                                }
                                if (!errorState?.message.isNullOrEmpty()) {
                                    println(errorState?.message)
                                }
                                started = true
                                index.intValue = 0
                            }
                        }
                    }
                }
            }
        }
    }
}

