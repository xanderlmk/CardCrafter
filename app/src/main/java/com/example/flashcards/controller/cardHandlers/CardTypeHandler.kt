package com.example.flashcards.controller.cardHandlers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.flashcards.model.tablesAndApplication.CT
import com.example.flashcards.model.uiModels.Fields
import com.example.flashcards.views.cardViews.editCardViews.EditBasicCard
import com.example.flashcards.views.cardViews.editCardViews.EditHintCard
import com.example.flashcards.views.cardViews.editCardViews.EditThreeCard
import com.example.flashcards.ui.theme.GetUIStyle
import com.example.flashcards.views.cardViews.editCardViews.EditChoiceCard
import com.example.flashcards.views.cardViews.editCardViews.EditMathCard
import com.example.flashcards.views.miscFunctions.details.createBasicCardDetails
import com.example.flashcards.views.miscFunctions.details.createChoiceCardDetails
import com.example.flashcards.views.miscFunctions.details.createMathCardDetails
import com.example.flashcards.views.miscFunctions.details.createThreeOrHintCardDetails

interface CardTypeHandler {
    @Composable
    fun HandleCardEdit(
        cardId: Int,
        fields: Fields,
        ct : CT,
        changed : Boolean,
        getUIStyle: GetUIStyle
    )
}

class BasicCardTypeHandler : CardTypeHandler {
    @Composable
    override fun HandleCardEdit(
        cardId: Int,
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
            } else if (ct is CT.Math) {
                EditBasicCard(fields)
            }
        }
    }
}

class ThreeCardTypeHandler : CardTypeHandler {
    @Composable
    override fun HandleCardEdit(
        cardId: Int,
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
            } else if (ct is CT.Math) {
                EditThreeCard(fields)
            }
        }
    }
}

class HintCardTypeHandler : CardTypeHandler {
    @Composable
    override fun HandleCardEdit(
        cardId: Int,
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
            } else if (ct is CT.Math) {
                EditHintCard(fields)
            }
        }
    }
}

class ChoiceCardTypeHandler : CardTypeHandler {
    @Composable
    override fun HandleCardEdit(
        cardId: Int,
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
                fields.choices[0] = rememberSaveable {
                    mutableStateOf(cardDetails.choices[0].value) }
                fields.choices[1] = rememberSaveable {
                    mutableStateOf(cardDetails.choices[1].value) }
                fields.choices[2] = rememberSaveable {
                    mutableStateOf(cardDetails.choices[2].value) }
                fields.choices[3] = rememberSaveable {
                    mutableStateOf(cardDetails.choices[3].value) }
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
            } else if (ct is CT.Math) {
                EditChoiceCard(fields, getUIStyle)
            }
        }
    }
}

class MathCardTypeHandler : CardTypeHandler {
    @Composable
    override fun HandleCardEdit(
        cardId: Int,
        fields: Fields,
        ct: CT,
        changed : Boolean,
        getUIStyle: GetUIStyle
    ) {
        if (ct is CT.Math){
            if (!changed){
                val cardDetails by remember {
                    mutableStateOf(
                        createMathCardDetails(
                            ct.mathCard.question,
                            ct.mathCard.steps,
                            ct.mathCard.answer
                        )
                    )
                }
                fields.question = rememberSaveable { mutableStateOf(cardDetails.question.value) }
                fields.stringList = rememberSaveable { cardDetails.stringList }
                fields.answer = rememberSaveable { mutableStateOf(cardDetails.answer.value) }
            }
            EditMathCard(fields, getUIStyle)
        } else {
            if (ct is CT.Basic) {
                EditMathCard(fields, getUIStyle)
            } else if (ct is CT.ThreeField) {
                EditMathCard(fields, getUIStyle)
            } else if (ct is CT.Hint) {
                EditMathCard(fields, getUIStyle)
            } else if (ct is CT.MultiChoice) {
                EditMathCard(fields, getUIStyle)
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
            "math" -> {
                MathCardTypeHandler()
            }
            else -> {
                println("NULL")
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
            "math" -> {
                MathCardTypeHandler()
            }
            else -> {
                println("NULL")
                null
            }
        }
    }
}



