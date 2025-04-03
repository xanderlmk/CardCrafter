package com.belmontCrest.cardCrafter.controller.cardHandlers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import com.belmontCrest.cardCrafter.model.tablesAndApplication.CT
import com.belmontCrest.cardCrafter.model.uiModels.Fields
import com.belmontCrest.cardCrafter.views.cardViews.editCardViews.EditBasicCard
import com.belmontCrest.cardCrafter.views.cardViews.editCardViews.EditHintCard
import com.belmontCrest.cardCrafter.views.cardViews.editCardViews.EditThreeCard
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.views.cardViews.editCardViews.EditChoiceCard
import com.belmontCrest.cardCrafter.views.cardViews.editCardViews.EditNotationCard
import com.belmontCrest.cardCrafter.views.miscFunctions.details.createBasicCardDetails
import com.belmontCrest.cardCrafter.views.miscFunctions.details.createChoiceCardDetails
import com.belmontCrest.cardCrafter.views.miscFunctions.details.createNotationCardDetails
import com.belmontCrest.cardCrafter.views.miscFunctions.details.createThreeOrHintCardDetails

interface CardTypeHandler {
    @Composable
    fun HandleCardEdit(
        fields: Fields,
        ct : CT,
        changed : Boolean,
        getUIStyle: GetUIStyle
    )
}

class BasicCardTypeHandler : CardTypeHandler {
    @Composable
    override fun HandleCardEdit(
        fields: Fields,
        ct : CT,
        changed : Boolean,
        getUIStyle: GetUIStyle
    ) {

        if (ct is CT.Basic) {
            if (!changed) {
                val cardDetails by remember {
                    mutableStateOf(
                        createBasicCardDetails(ct.basicCard)
                    )
                }
                fields.question = rememberSaveable { mutableStateOf(cardDetails.question.value) }
                fields.answer = rememberSaveable { mutableStateOf(cardDetails.answer.value) }
            }
            EditBasicCard(fields)
        } else {
            if (ct is CT.ThreeField) {
                EditBasicCard(fields)
            } else if (ct is CT.Hint) {
                EditBasicCard(fields)
            } else if (ct is CT.MultiChoice) {
                EditBasicCard(fields)
            } else if (ct is CT.Notation) {
                EditBasicCard(fields)
            }
        }
    }
}

class ThreeCardTypeHandler : CardTypeHandler {
    @Composable
    override fun HandleCardEdit(
        fields: Fields,
        ct : CT,
        changed : Boolean,
        getUIStyle: GetUIStyle
    ) {
        if (ct is CT.ThreeField){
            if (!changed) {
                val cardDetails by remember {
                    mutableStateOf(
                        createThreeOrHintCardDetails(
                            ct.threeFieldCard.question,
                            ct.threeFieldCard.middle,
                            ct.threeFieldCard.answer
                        )
                    )
                }
                fields.question = rememberSaveable {
                    mutableStateOf(cardDetails.question.value) }
                fields.middleField = rememberSaveable {
                    mutableStateOf(cardDetails.middleField.value) }
                fields.answer = rememberSaveable {
                    mutableStateOf(cardDetails.answer.value) }
            }
            EditThreeCard(fields)
        } else {
            if (ct is CT.Basic) {
                EditThreeCard(fields)
            } else if (ct is CT.Hint) {
                EditThreeCard(fields)
            } else if (ct is CT.MultiChoice) {
                EditThreeCard(fields)
            } else if (ct is CT.Notation) {
                EditThreeCard(fields)
            }
        }
    }
}

class HintCardTypeHandler : CardTypeHandler {
    @Composable
    override fun HandleCardEdit(
        fields: Fields,
        ct : CT,
        changed : Boolean,
        getUIStyle: GetUIStyle
    ) {
        if (ct is CT.Hint) {
            if (!changed){
                val cardDetails by remember {
                    mutableStateOf(
                        createThreeOrHintCardDetails(
                            ct.hintCard.question, ct.hintCard.hint, ct.hintCard.answer
                        )
                    )
                }
                fields.question = rememberSaveable {
                    mutableStateOf(cardDetails.question.value) }
                fields.middleField = rememberSaveable {
                    mutableStateOf(cardDetails.middleField.value) }
                fields.answer = rememberSaveable {
                    mutableStateOf(cardDetails.answer.value) }
            }
            EditHintCard(fields)
        } else {
            if (ct is CT.Basic) {
                EditHintCard(fields)
            } else if (ct is CT.ThreeField) {
                EditHintCard(fields)
            } else if (ct is CT.MultiChoice) {
                EditHintCard(fields)
            } else if (ct is CT.Notation) {
                EditHintCard(fields)
            }
        }
    }
}

class ChoiceCardTypeHandler : CardTypeHandler {
    @Composable
    override fun HandleCardEdit(
        fields: Fields,
        ct : CT,
        changed : Boolean,
        getUIStyle: GetUIStyle
    ) {
        if (ct is CT.MultiChoice) {
            if (!changed){
                val cardDetails by remember {
                    mutableStateOf(
                        createChoiceCardDetails(ct.multiChoiceCard)
                    )
                }
                fields.question = rememberSaveable { mutableStateOf(cardDetails.question.value) }
                fields.choices[0].value = rememberSaveable {
                    cardDetails.choices[0].value }
                fields.choices[1].value = rememberSaveable {
                   cardDetails.choices[1].value }
                fields.choices[2].value = rememberSaveable {
                    cardDetails.choices[2].value }
                fields.choices[3].value = rememberSaveable {
                    cardDetails.choices[3].value }
                fields.correct = rememberSaveable { mutableStateOf(cardDetails.correct.value) }
            }
            EditChoiceCard(fields, getUIStyle)
        } else {
            if (ct is CT.Basic) {
                EditChoiceCard(fields, getUIStyle)
            } else if (ct is CT.ThreeField) {
                EditChoiceCard(fields, getUIStyle)
            } else if (ct is CT.Hint) {
                EditChoiceCard(fields, getUIStyle)
            } else if (ct is CT.Notation) {
                EditChoiceCard(fields, getUIStyle)
            }
        }
    }
}

class NotationCardTypeHandler : CardTypeHandler {
    @Composable
    override fun HandleCardEdit(
        fields: Fields,
        ct: CT,
        changed : Boolean,
        getUIStyle: GetUIStyle
    ) {
        if (ct is CT.Notation){
            if (!changed){
                val cardDetails by remember {
                    mutableStateOf(
                        createNotationCardDetails(
                            ct.notationCard.question,
                            ct.notationCard.steps,
                            ct.notationCard.answer
                        )
                    )
                }
                fields.question = rememberSaveable { mutableStateOf(cardDetails.question.value) }
                fields.stringList = rememberSaveable { cardDetails.stringList }
                fields.answer = rememberSaveable { mutableStateOf(cardDetails.answer.value) }
            }
            EditNotationCard(fields, getUIStyle)
        } else {
            if (ct is CT.Basic) {
                EditNotationCard(fields, getUIStyle)
            } else if (ct is CT.ThreeField) {
                EditNotationCard(fields, getUIStyle)
            } else if (ct is CT.Hint) {
                EditNotationCard(fields, getUIStyle)
            } else if (ct is CT.MultiChoice) {
                EditNotationCard(fields, getUIStyle)
            }
        }
    }
}

fun returnCardTypeHandler(newType : String, currentType : String) : CardTypeHandler? {
    return if (newType == currentType) {
        when (currentType) {
            "basic" -> {
                BasicCardTypeHandler()
            }
            "three" -> {
                ThreeCardTypeHandler()
            }
            "hint" -> {
                HintCardTypeHandler()
            }
            "multi" -> {
                ChoiceCardTypeHandler()
            }
            "notation" -> {
                NotationCardTypeHandler()
            }
            else -> {
                null
            }
        }
    } else {
        when (newType) {
            "basic" -> {
                BasicCardTypeHandler()
            }
            "three" -> {
                ThreeCardTypeHandler()
            }
            "hint" -> {
                HintCardTypeHandler()
            }
            "multi" -> {
                ChoiceCardTypeHandler()
            }
            "notation" -> {
                NotationCardTypeHandler()
            }
            else -> {
                null
            }
        }
    }
}



