package com.example.flashcards.controller.cardHandlers

import androidx.compose.runtime.Composable
import com.example.flashcards.model.tablesAndApplication.CT
import com.example.flashcards.model.tablesAndApplication.CT.Basic
import com.example.flashcards.model.uiModels.Fields
import com.example.flashcards.views.cardViews.editCardViews.EditBasicCard
import com.example.flashcards.views.cardViews.editCardViews.EditHintCard
import com.example.flashcards.views.cardViews.editCardViews.EditThreeCard
import com.example.flashcards.ui.theme.GetModifier
import com.example.flashcards.views.cardViews.editCardViews.EditChoiceCard
import com.example.flashcards.views.cardViews.editCardViews.EditMathCard

interface CardTypeHandler {
    @Composable
    fun HandleCardEdit(
        cardId: Int,
        fields: Fields,
        ct : CT,
        getModifier: GetModifier
    )
}

class BasicCardTypeHandler : CardTypeHandler {
    @Composable
    override fun HandleCardEdit(
        cardId: Int,
        fields: Fields,
        ct : CT,
        getModifier: GetModifier
    ) {
        if (ct is Basic) {
            EditBasicCard(ct.basicCard, fields)
        }
    }
}

class ThreeCardTypeHandler : CardTypeHandler {
    @Composable
    override fun HandleCardEdit(
        cardId: Int,
        fields: Fields,
        ct : CT,
        getModifier: GetModifier
    ) {
        if (ct is CT.ThreeField){
            EditThreeCard(ct.threeFieldCard, fields)
        }
    }
}

class HintCardTypeHandler : CardTypeHandler {
    @Composable
    override fun HandleCardEdit(
        cardId: Int,
        fields: Fields,
        ct : CT,
        getModifier: GetModifier
    ) {
        if (ct is CT.Hint){
            EditHintCard(ct.hintCard, fields)
        }
    }
}

class ChoiceCardTypeHandler : CardTypeHandler {
    @Composable
    override fun HandleCardEdit(
        cardId: Int,
        fields: Fields,
        ct : CT,
        getModifier: GetModifier
    ) {
        if (ct is CT.MultiChoice){
            EditChoiceCard(ct.multiChoiceCard, fields, getModifier)
        }
    }
}

class MathCardTypeHandler : CardTypeHandler {
    @Composable
    override fun HandleCardEdit(
        cardId: Int,
        fields: Fields,
        ct: CT,
        getModifier: GetModifier
    ) {
        if (ct is CT.Math){
            EditMathCard(ct.mathCard, fields, getModifier)
        }
    }
}





