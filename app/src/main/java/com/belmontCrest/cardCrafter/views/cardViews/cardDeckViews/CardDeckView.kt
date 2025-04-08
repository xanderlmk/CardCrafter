package com.belmontCrest.cardCrafter.views.cardViews.cardDeckViews

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.controller.cardHandlers.redoACard
import com.belmontCrest.cardCrafter.controller.cardHandlers.returnCard
import com.belmontCrest.cardCrafter.controller.cardHandlers.showReviewsLeft
import com.belmontCrest.cardCrafter.controller.cardHandlers.updateCTCard
import com.belmontCrest.cardCrafter.controller.cardHandlers.updateDecksCardList
import com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels.CardDeckViewModel
import com.belmontCrest.cardCrafter.model.uiModels.CardState
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.model.uiModels.Fields
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.boxViewsModifier
import com.belmontCrest.cardCrafter.views.miscFunctions.AgainText
import com.belmontCrest.cardCrafter.views.miscFunctions.GoodText
import com.belmontCrest.cardCrafter.views.miscFunctions.HardText
import com.belmontCrest.cardCrafter.views.miscFunctions.NoDueCards
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import java.util.Date

class CardDeckView(
    private var cardDeckVM: CardDeckViewModel,
    private var getUIStyle: GetUIStyle,
    private var fields: Fields
) {
    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    fun ViewCard(
        deck: Deck
    ) {
        val sealedCL by cardDeckVM.cardListUiState.collectAsStateWithLifecycle()

        val backupList by cardDeckVM.backupCardList.collectAsState()
        val errorState by cardDeckVM.errorState.collectAsState()

        var dueCTs = remember {
            derivedStateOf { sealedCL.allCTs.toMutableList() }
        }
        var show by rememberSaveable { mutableStateOf(false) }
        val index = rememberSaveable { mutableIntStateOf(0) }
        val coroutineScope = rememberCoroutineScope()
        var clicked by remember { mutableStateOf(false) }
        var started by rememberSaveable { mutableStateOf(false) }
        val clickedChoice = rememberSaveable { mutableStateOf('?') }


        val scrollState = rememberScrollState()
        val focusManager = LocalFocusManager.current

        val redoClicked by cardDeckVM.redoClicked.collectAsStateWithLifecycle()

        if (redoClicked) {
            coroutineScope.launch(Dispatchers.Default) {
                cardDeckVM.updateRedoClicked(false)
                if (index.intValue > 0) {
                    index.intValue -= 1
                    cardDeckVM.updateIndex(index.intValue)
                    val ct = sealedCL.allCTs[index.intValue]
                    redoACard(ct, cardDeckVM, index.intValue, dueCTs.value)
                    clickedChoice.value = '?'
                    show = false
                } else {
                    if (sealedCL.allCTs.isNotEmpty() && started) {
                        index.intValue = sealedCL.allCTs.size - 1
                        val ct = sealedCL.allCTs[index.intValue]
                        redoACard(ct, cardDeckVM, index.intValue, dueCTs.value)
                        clickedChoice.value = '?'
                        show = false
                    } else if (backupList.isNotEmpty() && started) {
                        clickedChoice.value = '?'
                        Log.d("CardDeckView", "Backup logic not implemented yet.")
                    }
                }
                focusManager.clearFocus()
            }
        }

        Box(
            contentAlignment =
                if (sealedCL.allCTs.isEmpty() || dueCTs.value.isEmpty()) {
                    Alignment.Center
                } else {
                    Alignment.TopCenter
                },
            modifier = Modifier
                .boxViewsModifier(getUIStyle.getColorScheme())
                .verticalScroll(scrollState)
        ) {
            if (!fields.leftDueCardView.value) {
                if (sealedCL.allCTs.isEmpty() || dueCTs.value.isEmpty() ||
                    deck.nextReview > Date()
                ) {
                    if (cardDeckVM.getState() == CardState.Loading){

                    } else {
                        NoDueCards(getUIStyle)
                    }

                } else {
                    if (index.intValue < dueCTs.value.size) {
                        Text(
                            text = stringResource(R.string.reviews_left) +
                                    showReviewsLeft(sealedCL.savedCTs[index.intValue]),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(start = 48.dp, end = 48.dp, top = 8.dp)
                                .clickable(interactionSource = null, indication = null) {
                                    focusManager.clearFocus()
                                }
                                .zIndex(1f)
                        )
                        if (!show) {
                            if (cardDeckVM.getState() == CardState.Finished) {
                                FrontCard(
                                    dueCTs.value[index.intValue],
                                    getUIStyle,
                                    clickedChoice,
                                    Modifier
                                        .align(Alignment.TopCenter)
                                        .padding(bottom = 62.dp, top = 80.dp)
                                )
                                Button(
                                    onClick = {
                                        show = true
                                        focusManager.clearFocus()
                                    },
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = getUIStyle.secondaryButtonColor(),
                                        contentColor = getUIStyle.buttonTextColor()
                                    )
                                ) {
                                    Text(stringResource(R.string.show_answer))
                                }
                                clicked = false
                            }
                        } else {
                            BackCard(
                                dueCTs.value[index.intValue],
                                getUIStyle, Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(bottom = 62.dp, top = 80.dp),
                                clickedChoice.value
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
                                                    scrollState.animateScrollTo(0)
                                                    clicked = false
                                                }
                                            }
                                        },
                                        modifier = Modifier,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = getUIStyle.secondaryButtonColor(),
                                            contentColor = getUIStyle.buttonTextColor()
                                        )
                                    ) { Text(stringResource(R.string.again)) }
                                    AgainText(getUIStyle)
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
                                                    clickedChoice.value = '?'
                                                    show = !show
                                                }
                                                coroutineScope.launch {
                                                    while (cardDeckVM.getState() ==
                                                        CardState.Loading
                                                    ) {
                                                        delay(36)
                                                    }
                                                    index.intValue =
                                                        ((index.intValue + 1))
                                                    cardDeckVM.updateIndex(index.intValue)
                                                    scrollState.animateScrollTo(0)
                                                    clicked = false
                                                }
                                            }
                                        },
                                        modifier = Modifier,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = getUIStyle.secondaryButtonColor(),
                                            contentColor = getUIStyle.buttonTextColor()
                                        )
                                    ) { Text(stringResource(R.string.hard)) }
                                    HardText(sealedCL, index.intValue, hard, getUIStyle)

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
                                                    clickedChoice.value = '?'
                                                    show = !show
                                                }
                                                coroutineScope.launch {
                                                    while (cardDeckVM.getState() ==
                                                        CardState.Loading
                                                    ) {
                                                        delay(36)
                                                    }
                                                    index.intValue =
                                                        ((index.intValue + 1))
                                                    cardDeckVM.updateIndex(index.intValue)
                                                    scrollState.animateScrollTo(0)
                                                    clicked = false
                                                }
                                            }
                                        },
                                        modifier = Modifier,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = getUIStyle.secondaryButtonColor(),
                                            contentColor = getUIStyle.buttonTextColor()
                                        )
                                    ) { Text(stringResource(R.string.good)) }
                                    GoodText(sealedCL, index.intValue, good, getUIStyle)
                                }
                            }
                        }
                    } else {
                        LaunchedEffect(Unit) {
                            if (cardDeckVM.getState() != CardState.Loading) {
                                coroutineScope.launch {
                                    cardDeckVM.updateIndex(0)
                                    cardDeckVM.transitionTo(CardState.Loading)
                                    updateDecksCardList(
                                        deck,
                                        cardDeckVM
                                    )
                                    while (cardDeckVM.getState() == CardState.Loading) {
                                        delay(50)
                                    }
                                    cardDeckVM.updateWhichDeck(0)
                                    if ((sealedCL.allCTs.isEmpty()) && deck.nextReview >= Date()) {
                                        dueCTs.value.clear()
                                    }
                                    if (!errorState?.message.isNullOrEmpty()) {
                                        println(errorState?.message)
                                    }
                                    /** Forcefully updating the decks */
                                    started = true
                                    cardDeckVM.updateWhichDeck(deck.id)
                                    index.intValue = 0
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
