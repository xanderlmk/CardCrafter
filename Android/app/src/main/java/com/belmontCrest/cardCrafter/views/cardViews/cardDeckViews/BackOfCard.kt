package com.belmontCrest.cardCrafter.views.cardViews.cardDeckViews

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.views.cardViews.cardDeckViews.cardTypeView.BasicBackCard
import com.belmontCrest.cardCrafter.views.cardViews.cardDeckViews.cardTypeView.ChoiceBackCard
import com.belmontCrest.cardCrafter.views.cardViews.cardDeckViews.cardTypeView.CustomBackCard
import com.belmontCrest.cardCrafter.views.cardViews.cardDeckViews.cardTypeView.HintBackCard
import com.belmontCrest.cardCrafter.views.cardViews.cardDeckViews.cardTypeView.NotationBackCard
import com.belmontCrest.cardCrafter.views.cardViews.cardDeckViews.cardTypeView.ThreeBackCard

@Composable
fun BackCard(
    ct: CT, getUIStyle: GetUIStyle, modifier: Modifier, clickedChoice: MutableState<Char>
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(10.dp), contentAlignment = Alignment.TopCenter
    ) {
        when (ct) {
            is CT.Basic -> BasicBackCard(ct.basicCard, getUIStyle)
            is CT.ThreeField -> ThreeBackCard(ct.threeFieldCard, getUIStyle)
            is CT.Hint -> HintBackCard(ct.hintCard, getUIStyle)
            is CT.MultiChoice -> ChoiceBackCard(ct.multiChoiceCard, clickedChoice.value, getUIStyle)
            is CT.Notation -> NotationBackCard(ct.notationCard, getUIStyle)
            is CT.Custom -> CustomBackCard(ct.customCard, getUIStyle, clickedChoice)
        }
    }
}