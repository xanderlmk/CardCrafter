package com.belmontCrest.cardCrafter.views.cardViews.editCardViews

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.controller.cardHandlers.getCardId
import com.belmontCrest.cardCrafter.controller.cardHandlers.getCardType
import com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels.EditingCardListViewModel
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.model.TAProp
import com.belmontCrest.cardCrafter.model.toTextProp
import com.belmontCrest.cardCrafter.model.ui.Fields
import com.belmontCrest.cardCrafter.navigation.NavViewModel
import com.belmontCrest.cardCrafter.views.miscFunctions.CardSelector
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.boxViewsModifier
import com.belmontCrest.cardCrafter.uiFunctions.CustomText
import com.belmontCrest.cardCrafter.views.miscFunctions.details.toQuestion

class EditCardsList(
    private var editingCardListVM: EditingCardListViewModel,
    private var fields: Fields,
    private var listState: LazyListState,
    private var getUIStyle: GetUIStyle
) {
    @Composable
    fun ViewFlashCards(
        navVM: NavViewModel, goToEditCard: (Int) -> Unit
    ) {
        val sealedCardsList by editingCardListVM.sealedAllCTs.collectAsStateWithLifecycle()
        val searchQuery by editingCardListVM.searchQuery.collectAsStateWithLifecycle()
        val middleCard = rememberSaveable { mutableIntStateOf(0) }
        var enabled by remember { mutableStateOf(true) }
        val isSelecting by navVM.isSelecting.collectAsStateWithLifecycle()
        val selectedCTL by editingCardListVM.selectedCards.collectAsStateWithLifecycle()
        val filtered = sealedCardsList.allCTs.filter { ct ->
            if (searchQuery.isBlank()) return@filter true

            ct.toQuestion().contains(searchQuery, ignoreCase = true)
        }

        // Restore the scroll position when returning from editing
        LaunchedEffect(Unit) {
            snapshotFlow { listState.layoutInfo.visibleItemsInfo }
                .collect { visibleItems -> middleCard.intValue = visibleItems.size / 2 }
            getListState(listState, middleCard.intValue)
        }
        Column(
            modifier = Modifier
                .boxViewsModifier(getUIStyle.getColorScheme()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val text =
                if (isSelecting) stringResource(R.string.keyboard_disabled)
                else stringResource(R.string.search)
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { editingCardListVM.updateQuery(it) },
                placeholder = { Text(text, color = getUIStyle.defaultIconColor()) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                enabled = !isSelecting
            )
            if (searchQuery.isNotEmpty() && filtered.isEmpty()) {
                CustomText(
                    "No Results Found.", getUIStyle,
                    Modifier.padding(vertical = 10.dp),
                    TAProp.Center.toTextProp()
                )
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState
            ) {
                val selectedCardIds = selectedCTL.map { it.getCardId() }.toSet()
                items(filtered.size, key = { index -> filtered[index].getCardId() }) { index ->
                    val selected = filtered[index].getCardId() in selectedCardIds
                    CardItem(
                        filtered, index, isSelecting = isSelecting, selected = selected,
                        onTap = {
                            if (enabled) {
                                if (isSelecting) {
                                    editingCardListVM.toggleCard(filtered[index])
                                } else {
                                    enabled = false
                                    fields.scrollPosition.value = index
                                    fields.isEditing.value = true
                                    navVM.updateType(filtered[index].getCardType())
                                    goToEditCard(filtered[index].getCardId())
                                    enabled = true
                                }
                            }
                        },
                        onLongPress = {
                            if (enabled) {
                                editingCardListVM.toggleCard(filtered[index])
                                navVM.isSelectingTrue()
                            }
                        })
                }
            }
        }
    }

    @Composable
    fun CardItem(
        filtered: List<CT>, index: Int, onTap: () -> Unit, onLongPress: () -> Unit,
        isSelecting: Boolean, selected: Boolean
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(getUIStyle.secondaryButtonColor(), RoundedCornerShape(16.dp))
                .padding(4.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { onTap() },
                        onLongPress = { onLongPress() }
                    )
                }
        ) {
            CardSelector(filtered, index, getUIStyle, isSelecting, selected)
        }
    }

    private suspend fun getListState(listState: LazyListState, middleCard: Int) {
        if (fields.scrollPosition.value == 0) {
            Log.d("scrollPosition", "${fields.scrollPosition.value}")
            listState.scrollToItem(0)
        } else if (listState.firstVisibleItemScrollOffset == 0 &&
            fields.scrollPosition.value > 0
        ) {
            listState.scrollToItem(listState.firstVisibleItemIndex)
        } else if (fields.scrollPosition.value >
            listState.layoutInfo.visibleItemsInfo.lastIndex - middleCard ||
            fields.scrollPosition.value <=
            listState.layoutInfo.visibleItemsInfo.lastIndex - middleCard
        ) {
            listState.scrollToItem(
                listState.layoutInfo.visibleItemsInfo.lastIndex - middleCard
            )
        } else {
            listState.scrollToItem(fields.scrollPosition.value)
        }
    }
}