package com.belmontCrest.cardCrafter.supabase.view.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.belmontCrest.cardCrafter.model.MLProp
import com.belmontCrest.cardCrafter.model.TAProp
import com.belmontCrest.cardCrafter.model.Type
import com.belmontCrest.cardCrafter.model.toTextProp
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.UserExportedDecksViewModel
import com.belmontCrest.cardCrafter.supabase.model.tables.toList
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.boxViewsModifier
import com.belmontCrest.cardCrafter.uiFunctions.CustomText
import com.belmontCrest.cardCrafter.uiFunctions.SubmitButton
import com.belmontCrest.cardCrafter.views.miscFunctions.details.CardDetails
import com.belmontCrest.cardCrafter.views.miscFunctions.details.toCardDetails


@Composable
fun AllCards(getUIStyle: GetUIStyle, uEDVM: UserExportedDecksViewModel) {
    val cardList by uEDVM.userCards.collectAsStateWithLifecycle()
    val cardsToDisplay by uEDVM.cardsToDisplay.collectAsStateWithLifecycle()
    var showCards by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberLazyListState()
    val modifier = Modifier
        .padding(vertical = 10.dp, horizontal = 4.dp)
        .fillMaxWidth()
    val ctdModifier = Modifier
        .padding(4.dp)
        .fillMaxWidth()
    Column(
        modifier = Modifier
            .boxViewsModifier(getUIStyle.getColorScheme()),
        verticalArrangement = Arrangement.Top
    ) {
        if (showCards) {
            SubmitButton(
                onClick = { showCards = !showCards }, true, getUIStyle,
                "Hide cards", modifier
            )
            HorizontalDivider(thickness = 2.dp)
            LazyColumn(
                contentPadding = PaddingValues(
                    horizontal = 4.dp,
                    vertical = 10.dp
                ),
                state = scrollState,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                items(cardList.cts) { card ->
                    val cd = card.toCardDetails()
                    CardView(cd, card.type, getUIStyle)
                }
            }
        } else {
            SubmitButton(
                onClick = { showCards = !showCards }, true, getUIStyle,
                "Show all cards", modifier
            )
            HorizontalDivider(thickness = 2.dp)
            CustomText("Cards To Display", getUIStyle, ctdModifier, TAProp.Center.toTextProp())
            cardsToDisplay.toList().map {
                val cd = it.toCardDetails()
                CardView(cd, it.type, getUIStyle)
            }
        }
    }
}

@Composable
private fun CardView(cd: CardDetails, type: String, getUIStyle: GetUIStyle) {
    val modifier = Modifier.padding(6.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .background(
                color = getUIStyle.secondaryButtonColor(),
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        when (type) {
            Type.BASIC -> {
                CustomText(cd.question.value, getUIStyle, modifier, MLProp.Two.toTextProp())
            }

            Type.THREE -> {
                CustomText(cd.question.value, getUIStyle, modifier, MLProp.Two.toTextProp())
            }

            Type.HINT -> {
                CustomText(cd.question.value, getUIStyle, modifier, MLProp.Two.toTextProp())
            }

            Type.MULTI -> {
                CustomText(cd.question.value, getUIStyle, modifier, MLProp.Two.toTextProp())
            }

            Type.NOTATION -> {
                CustomText(cd.question.value, getUIStyle, modifier, MLProp.Two.toTextProp())
            }
        }
    }

}