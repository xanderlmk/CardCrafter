package com.belmontCrest.cardCrafter.views.cardViews.cardDeckViews

import android.annotation.SuppressLint
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import com.belmontCrest.cardCrafter.controller.cardHandlers.showReviewsLeft
import com.belmontCrest.cardCrafter.controller.cardHandlers.toCard
import com.belmontCrest.cardCrafter.controller.cardHandlers.updateCTCard
import com.belmontCrest.cardCrafter.controller.view.models.cardViewsModels.CardDeckViewModel
import com.belmontCrest.cardCrafter.model.ui.states.CardState
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.model.ui.Fields
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.boxViewsModifier
import com.belmontCrest.cardCrafter.uiFunctions.buttons.SubmitButton
import com.belmontCrest.cardCrafter.views.misc.AgainText
import com.belmontCrest.cardCrafter.views.misc.CARD_CRAFTER
import com.belmontCrest.cardCrafter.views.misc.GoodText
import com.belmontCrest.cardCrafter.views.misc.HardText
import com.belmontCrest.cardCrafter.views.misc.NoDueCards
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
        val errorState by cardDeckVM.errorState.collectAsState()
        var show by rememberSaveable { mutableStateOf(false) }
        var index by rememberSaveable { mutableIntStateOf(0) }
        val coroutineScope = rememberCoroutineScope()
        var clicked by rememberSaveable { mutableStateOf(false) }
        val clickedChoice = rememberSaveable { mutableStateOf('?') }
        val scrollState = rememberScrollState()
        val focusManager = LocalFocusManager.current
        val redoClicked by cardDeckVM.redoClicked.collectAsStateWithLifecycle()
        val cardState by cardDeckVM.cardState.collectAsStateWithLifecycle()
        var enabled by rememberSaveable { mutableStateOf(true) }
        if (redoClicked && enabled) {
            coroutineScope.launch {
                val beforeSize = sealedCL.allCTs.size
                enabled = false
                try {
                    val result = cardDeckVM.getRedoCardType(deck)
                    delay(50)
                    val afterSize = sealedCL.allCTs.size
                    if (result) {
                        if ((beforeSize == afterSize) && sealedCL.allCTs.isNotEmpty()) {
                            if (index > 0) {
                                index -= 1
                            } else {
                                index = sealedCL.allCTs.size - 1
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(CARD_CRAFTER, "Failed to redo: $e")
                } finally {
                    cardDeckVM.updateTime()
                    clickedChoice.value = '?'
                    show = false
                    focusManager.clearFocus()
                    cardDeckVM.updateRedoClicked(false)
                    enabled = true
                }
            }
        }

        Box(
            contentAlignment =
                if (sealedCL.allCTs.isEmpty()) {
                    Alignment.Center
                } else {
                    Alignment.TopCenter
                },
            modifier = Modifier
                .boxViewsModifier(getUIStyle.getColorScheme())
                .verticalScroll(scrollState)
        ) {
            if (!fields.leftDueCardView.value) {
                if (sealedCL.allCTs.isEmpty() ||
                    deck.nextReview > Date()
                ) {
                    if (cardState == CardState.Finished) NoDueCards(getUIStyle)
                } else {
                    if (index < sealedCL.allCTs.size) {
                        Text(
                            text = stringResource(R.string.reviews_left) +
                                    showReviewsLeft(sealedCL.allCTs[index]),
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
                            if (cardState == CardState.Finished) {
                                FrontCard(
                                    sealedCL.allCTs[index],
                                    getUIStyle,
                                    clickedChoice,
                                    Modifier
                                        .align(Alignment.TopCenter)
                                        .padding(bottom = 62.dp, top = 80.dp)
                                )
                                Button(
                                    onClick = {
                                        show = true; focusManager.clearFocus()
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
                                sealedCL.allCTs[index],
                                getUIStyle, Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(bottom = 62.dp, top = 80.dp),
                                clickedChoice
                            )
                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.Bottom,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .align(Alignment.BottomCenter)
                            ) {
                                val good =
                                    ((sealedCL.allCTs[index].toCard().passes + 1) *
                                            deck.goodMultiplier).toInt()
                                val hard = if (sealedCL.allCTs[index].toCard().passes > 0)
                                    ((sealedCL.allCTs[index].toCard().passes + 1) *
                                            deck.badMultiplier).toInt()
                                else (sealedCL.allCTs[index].toCard().passes *
                                        deck.badMultiplier).toInt()
                                ResultButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            clicked = true
                                            cardDeckVM.transitionTo(CardState.Loading)
                                            updateCTCard(
                                                sealedCL.allCTs[index],
                                                deck, cardDeckVM,
                                                success = false, again = true
                                            )
                                            clickedChoice.value = '?'
                                            show = false
                                            scrollState.animateScrollTo(0)
                                            clicked = false
                                            cardDeckVM.transitionTo(CardState.Finished)
                                        }
                                    }, enabled = !clicked, getUIStyle = getUIStyle,
                                    string = stringResource(R.string.again)
                                ) { AgainText(getUIStyle) }
                                ResultButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            clicked = true
                                            val beforeSize = sealedCL.allCTs.size
                                            cardDeckVM.transitionTo(CardState.Loading)
                                            val result = updateCTCard(
                                                sealedCL.allCTs[index],
                                                deck, cardDeckVM,
                                                success = false, again = false
                                            )
                                            val afterSize = sealedCL.allCTs.size
                                            if (result) {
                                                if (beforeSize == afterSize) {
                                                    index =
                                                        ((index + 1))
                                                }
                                            }
                                            clickedChoice.value = '?'
                                            show = false
                                            scrollState.animateScrollTo(0)
                                            clicked = false
                                            cardDeckVM.transitionTo(CardState.Finished)
                                        }
                                    }, enabled = !clicked, getUIStyle = getUIStyle,
                                    string = stringResource(R.string.hard)
                                ) { HardText(sealedCL.allCTs[index], hard, getUIStyle) }
                                ResultButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            clicked = true
                                            val beforeSize = sealedCL.allCTs.size
                                            cardDeckVM.transitionTo(CardState.Loading)
                                            val result = updateCTCard(
                                                sealedCL.allCTs[index],
                                                deck, cardDeckVM,
                                                success = true, again = false
                                            )
                                            val afterSize = sealedCL.allCTs.size
                                            if (result) {
                                                if (beforeSize == afterSize) {
                                                    index =
                                                        ((index + 1))
                                                }
                                            }
                                            clickedChoice.value = '?'
                                            show = false
                                            scrollState.animateScrollTo(0)
                                            clicked = false
                                            cardDeckVM.transitionTo(CardState.Finished)
                                        }
                                    }, enabled = !clicked,
                                    string = stringResource(R.string.good), getUIStyle = getUIStyle
                                ) { GoodText(sealedCL.allCTs[index], good, getUIStyle) }
                            }
                        }
                    } else {
                        LaunchedEffect(Unit) {
                            coroutineScope.launch {
                                if (!errorState?.message.isNullOrEmpty())
                                    println(errorState?.message)
                                /** Forcefully updating the decks */
                                index = 0
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultButton(
    onClick: () -> Unit, string: String, enabled: Boolean,
    getUIStyle: GetUIStyle, content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SubmitButton(
            onClick = onClick, enabled = enabled, getUIStyle = getUIStyle, string = string
        )
        content()
    }
}
