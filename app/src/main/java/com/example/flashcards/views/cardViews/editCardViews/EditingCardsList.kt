package com.example.flashcards.views.cardViews.editCardViews

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.flashcards.controller.viewModels.CardViewModel
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.controller.viewModels.CardTypeViewModel
import com.example.flashcards.model.Fields
import com.example.flashcards.views.miscFunctions.CardSelector
import com.example.flashcards.views.miscFunctions.GetModifier
import com.example.flashcards.views.miscFunctions.LoadingText
import com.example.flashcards.views.miscFunctions.ShowBackButtonAndDeckName
import com.example.flashcards.views.miscFunctions.delayNavigate
import kotlinx.coroutines.launch

class DeckEditView(
    private var viewModel: CardViewModel,
    private var cardTypeViewModel: CardTypeViewModel,
    private var fields : Fields,
    private var listState : LazyListState,
    private var getModifier: GetModifier){
    var selectedCard = mutableStateOf<Card?>(null)
    var isEditing =  mutableStateOf(false)
    var navigate = mutableStateOf(false)

    @SuppressLint("CoroutineCreationDuringComposition")

    @Composable
    fun ViewFlashCards(deck : Deck, onNavigate: () -> Unit
    , goToEditCard: (Int) -> Unit) {

        //var deckWithCards by remember { mutableStateOf(DeckWithCards(Deck(0, loading.toString() ), emptyList())) }
        val cardListUiState by cardTypeViewModel.cardListUiState.collectAsState()
        val cardUiState by viewModel.cardUiState.collectAsState()
        val coroutineScope = rememberCoroutineScope()
        var middleCard = remember { mutableIntStateOf(0) }

        coroutineScope.launch {
            viewModel.getDeckWithCards(deck.id, cardTypeViewModel)
        }

        val presetModifier = getModifier.backButtonModifier()
        if (isEditing.value && selectedCard.value != null) {
            Box(
                modifier = getModifier.boxViewsModifier(),
                contentAlignment = Alignment.Center
            ) {
                LoadingText()
            }
            LaunchedEffect(navigate.value) {
                if (!navigate.value) {
                    delayNavigate()
                    goToEditCard(selectedCard.value?.id?: 0)
                }
            }
        } else {
            navigate.value = false
            // Restore the scroll position when returning from editing
            LaunchedEffect(Unit) {
                snapshotFlow { listState.layoutInfo.visibleItemsInfo }
                    .collect { visibleItems ->
                        middleCard.intValue = visibleItems.size/2
                    }
                getListState(listState,middleCard.intValue)
            }
            Box(
                modifier = getModifier.boxViewsModifier(),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    Box(
                        modifier = getModifier.bottomLineModifier()
                    ) {
                    ShowBackButtonAndDeckName(
                        onNavigate, deck, presetModifier, getModifier
                    )
                }
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        state = listState
                    ) {
                        items(cardUiState.cardList.size) { index ->
                            Button(
                                onClick = {
                                    selectedCard.value =
                                        cardUiState.cardList[index]
                                    isEditing.value = true
                                    fields.scrollPosition.value = index
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = getModifier.secondaryButtonColor(),
                                    contentColor = getModifier.buttonTextColor()
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                CardSelector(cardListUiState,
                                    cardUiState, index)
                            }
                        }
                    }
                }
            }
        }
    }
    private suspend fun getListState(listState: LazyListState, middleCard : Int){
        if (listState.firstVisibleItemScrollOffset == 0 &&
            fields.scrollPosition.value == 0){
            listState.scrollToItem(0)
        } else if(fields.scrollPosition.value == 0) {
            listState.scrollToItem(0)
        } else if (listState.firstVisibleItemScrollOffset == 0 &&
            fields.scrollPosition.value > 0) {
            listState.scrollToItem(listState.firstVisibleItemIndex)
        } else if (fields.scrollPosition.value >
            listState.layoutInfo.
            visibleItemsInfo.lastIndex - middleCard ||
            fields.scrollPosition.value <=
            listState.layoutInfo.
            visibleItemsInfo.lastIndex - middleCard) {
            listState.scrollToItem(listState.layoutInfo.
            visibleItemsInfo.lastIndex - middleCard)
        }
        else {
            listState.scrollToItem(fields.scrollPosition.value)
        }
    }
}