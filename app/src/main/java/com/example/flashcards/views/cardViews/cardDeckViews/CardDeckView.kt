package com.example.flashcards.views.cardViews.cardDeckViews

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.flashcards.model.tablesAndApplication.CT
import com.example.flashcards.model.tablesAndApplication.Card
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
    private var cardDeckVM: CardDeckViewModel,
    private var getModifier: GetModifier,
) {
    @SuppressLint("MutableCollectionMutableState")
    @Composable
    fun ViewCard(
        deck: Deck, onNavigate: () -> Unit
    ) {
        val backupList by cardDeckVM.backupCardList.collectAsState()
        val sealedCL by cardDeckVM.sealedDueCTs.collectAsState()

        var dueCTs = remember { sealedCL.allCTs }

        val errorState by cardDeckVM.errorState.collectAsState()
        var show by rememberSaveable { mutableStateOf(false) }
        val index = rememberSaveable { mutableIntStateOf(0) }
        val coroutineScope = rememberCoroutineScope()
        var clicked by remember { mutableStateOf(false) }
        var started by rememberSaveable { mutableStateOf(false) }

        /** These are the cards that will be updated and only changed
         *  at the start and once you traverse through the whole cardList */
        val updatedDueCTs by cardDeckVM.sealedDueCTs.collectAsState()

        val cardsToUpdate by cardDeckVM.cardListToUpdate.collectAsState()

        val scrollState = rememberScrollState()
        Box(
            contentAlignment =
            if (sealedCL.allCTs.isEmpty() || dueCTs.isEmpty()) {
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
                            val ct = updatedDueCTs.allCTs[index.intValue]
                            redoACard(ct, cardDeckVM,index.intValue,dueCTs)
                            show = false
                        } else {
                            if (sealedCL.allCTs.isNotEmpty() && started) {
                                index.intValue = sealedCL.allCTs.size - 1
                                val ct = updatedDueCTs.allCTs[index.intValue]
                                redoACard(ct, cardDeckVM,index.intValue,dueCTs)
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
            if (sealedCL.allCTs.isEmpty() || dueCTs.isEmpty()) {
                if(deck.nextReview <= Date()) {
                    LaunchedEffect(Unit) {
                        coroutineScope.launch {
                            cardDeckVM.transitionTo(CardState.Loading)
                            cardDeckVM.getDueCards(deck.id)
                            while (cardDeckVM.getState() == CardState.Loading) {
                                delay(30)
                            }
                            dueCTs = sealedCL.allCTs
                        }
                    }
                }
                NoDueCards(getModifier)

            } else {
                if (index.intValue < dueCTs.size) {
                    Text(
                        text = stringResource(R.string.reviews_left) +
                                showReviewsLeft(updatedDueCTs.savedCTs[index.intValue]),
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
                                dueCTs[index.intValue],
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
                            dueCTs[index.intValue],
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
                                ((returnCard(dueCTs[index.intValue]).passes + 1) *
                                        deck.goodMultiplier).toInt()
                            val hard = if (returnCard(dueCTs[index.intValue]).passes  > 0)
                                ((returnCard(dueCTs[index.intValue]).passes  + 1) *
                                        deck.badMultiplier).toInt()
                            else (returnCard(dueCTs[index.intValue]).passes  *
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
                                                updatedDueCTs.savedCTs[index.intValue] =
                                                    updateCTCard(
                                                        updatedDueCTs.savedCTs[index.intValue],
                                                        dueCTs[index.intValue],
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
                                                        updatedDueCTs.savedCTs[index.intValue]
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
                                                updatedDueCTs.savedCTs[index.intValue] =
                                                    updateCTCard(
                                                        updatedDueCTs.savedCTs[index.intValue],
                                                        dueCTs[index.intValue],
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
                                                        updatedDueCTs.savedCTs[index.intValue]
                                                    )
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
                                HardText(updatedDueCTs, index, hard, getModifier)

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
                                                updatedDueCTs.savedCTs[index.intValue] =
                                                    updateCTCard(
                                                        updatedDueCTs.savedCTs[index.intValue],
                                                        dueCTs[index.intValue],
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
                                                        updatedDueCTs.savedCTs[index.intValue]
                                                    )
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
                                GoodText(updatedDueCTs, index, good, getModifier)
                            }
                        }
                    }
                } else {
                    LaunchedEffect(Unit) {
                        coroutineScope.launch {
                            cardDeckVM.transitionTo(CardState.Loading)
                            /** This function also gets the due cards */
                            updateDecksCardList(
                                deck,
                                cardsToUpdate,
                                cardDeckVM
                            )
                            while (cardDeckVM.getState() == CardState.Loading) {
                                delay(30)
                            }
                            if ((sealedCL.allCTs.isEmpty() || sealedCL.savedCTs.isEmpty()) &&
                                deck.cardsLeft == 0){
                                cardDeckVM.updateNextReview(deck)
                            }
                            if (!errorState?.message.isNullOrEmpty()) {
                                println(errorState?.message)
                            }
                            started = true
                            dueCTs = sealedCL.allCTs
                            index.intValue = 0
                        }
                    }
                }
            }
        }
    }
}


fun returnCard(ct: CT) : Card{
    return when (ct) {
        is CT.Basic -> {
            ct.card
        }
        is CT.Hint -> {
            ct.card
        }
        is CT.ThreeField -> {
            ct.card
        }
        is CT.MultiChoice->{
            ct.card
        }
    }
}

fun updateCTCard(ct: CT, dueCT: CT,
                 deck: Deck, vm: CardDeckViewModel,
                 success : Boolean, again : Boolean) : CT {
    return when (ct) {
        is CT.Basic -> {
            ct.copy(
                card = handleCardUpdate(
                    returnCard(dueCT),
                    success = success,
                    vm,
                    deck.goodMultiplier,
                    deck.badMultiplier,
                    deck.reviewAmount,
                    again = again
                ),
                basicCard = ct.basicCard
            )
        }
        is CT.Hint -> {
            ct.copy(
                card = handleCardUpdate(
                    returnCard(dueCT),
                    success = success,
                    vm,
                    deck.goodMultiplier,
                    deck.badMultiplier,
                    deck.reviewAmount,
                    again = again
                ),
                hintCard = ct.hintCard
            )
        }
        is CT.ThreeField -> {
            ct.copy(
                card = handleCardUpdate(
                    returnCard(dueCT),
                    success = success,
                    vm,
                    deck.goodMultiplier,
                    deck.badMultiplier,
                    deck.reviewAmount,
                    again = again
                ),
                threeFieldCard = ct.threeFieldCard
            )
        }
        is CT.MultiChoice->{
            ct.copy(
                card = handleCardUpdate(
                    returnCard(dueCT),
                    success = success,
                    vm,
                    deck.goodMultiplier,
                    deck.badMultiplier,
                    deck.reviewAmount,
                    again = again
                ),
                multiChoiceCard = ct.multiChoiceCard
            )
        }
    }
}

suspend fun redoACard(ct : CT, cardDeckVM : CardDeckViewModel, index : Int,
                     dueCTs : MutableList<CT>){
    when (ct) {
        is CT.Basic -> {
            ct.card = cardDeckVM.getRedoCard(ct.card.id)
            cardDeckVM.getRedoCardType(
                ct.card.id,
                index
            )
        }
        is CT.Hint -> {
            ct.card = cardDeckVM.getRedoCard(ct.card.id)
            cardDeckVM.getRedoCardType(
                ct.card.id,
                index
            )
        }
        is CT.ThreeField -> {
            ct.card = cardDeckVM.getRedoCard(ct.card.id)
            cardDeckVM.getRedoCardType(
                ct.card.id,
                index
            )
        }
        is CT.MultiChoice->{
            ct.card = cardDeckVM.getRedoCard(ct.card.id)
            cardDeckVM.getRedoCardType(
                ct.card.id,
                index
            ).also {
                dueCTs[index] = ct
            }
        }
    }
}

fun showReviewsLeft(ct : CT): String{
    return when (ct) {
        is CT.Basic -> {
            ct.card.reviewsLeft.toString()
        }
        is CT.Hint -> {
            ct.card.reviewsLeft.toString()
        }
        is CT.ThreeField -> {
            ct.card.reviewsLeft.toString()
        }
        is CT.MultiChoice->{
            ct.card.reviewsLeft.toString()
        }
    }
}

fun returnReviewsLeft(ct: CT): Int{
    return when (ct) {
        is CT.Basic -> {
            ct.card.reviewsLeft
        }
        is CT.Hint -> {
            ct.card.reviewsLeft
        }
        is CT.ThreeField -> {
            ct.card.reviewsLeft
        }
        is CT.MultiChoice->{
            ct.card.reviewsLeft
        }
    }
}
