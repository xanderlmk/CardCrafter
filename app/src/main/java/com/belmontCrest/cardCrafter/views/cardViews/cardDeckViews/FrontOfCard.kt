package com.belmontCrest.cardCrafter.views.cardViews.cardDeckViews

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.views.cardViews.cardDeckViews.cardTypeView.BasicFrontCard
import com.belmontCrest.cardCrafter.views.cardViews.cardDeckViews.cardTypeView.ChoiceFrontCard
import com.belmontCrest.cardCrafter.views.cardViews.cardDeckViews.cardTypeView.CustomFrontCard
import com.belmontCrest.cardCrafter.views.cardViews.cardDeckViews.cardTypeView.HintFrontCard
import com.belmontCrest.cardCrafter.views.cardViews.cardDeckViews.cardTypeView.NotationFrontCard
import com.belmontCrest.cardCrafter.views.cardViews.cardDeckViews.cardTypeView.ThreeFrontCard

@Composable
fun FrontCard(
    ct: CT,
    getUIStyle: GetUIStyle,
    clickedChoice: MutableState<Char>,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        when (ct) {
            is CT.Basic -> BasicFrontCard(ct.basicCard, getUIStyle)
            is CT.ThreeField -> ThreeFrontCard(ct.threeFieldCard, getUIStyle)
            is CT.Hint -> HintFrontCard(ct.hintCard, getUIStyle)
            is CT.MultiChoice -> ChoiceFrontCard(ct.multiChoiceCard, getUIStyle, clickedChoice)
            is CT.Notation -> NotationFrontCard(ct.notationCard, getUIStyle)
            is CT.Custom -> CustomFrontCard(ct.customCard, getUIStyle, clickedChoice)
        }
    }
}

