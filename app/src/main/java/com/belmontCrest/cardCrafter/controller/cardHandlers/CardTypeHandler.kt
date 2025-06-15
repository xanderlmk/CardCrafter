package com.belmontCrest.cardCrafter.controller.cardHandlers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.model.ui.Fields
import com.belmontCrest.cardCrafter.navigation.NavViewModel
import com.belmontCrest.cardCrafter.views.cardViews.editCardViews.EditBasicCard
import com.belmontCrest.cardCrafter.views.cardViews.editCardViews.EditHintCard
import com.belmontCrest.cardCrafter.views.cardViews.editCardViews.EditThreeCard
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.uiFunctions.katex.menu.KaTeXMenu
import com.belmontCrest.cardCrafter.views.cardViews.editCardViews.EditChoiceCard
import com.belmontCrest.cardCrafter.views.cardViews.editCardViews.EditNotationCard
import com.belmontCrest.cardCrafter.views.miscFunctions.details.toCardDetails

interface CardTypeHandler {
    @Composable
    fun HandleCardEdit(
        fields: Fields, ct: CT, vm: NavViewModel,
        changed: Boolean, getUIStyle: GetUIStyle, onUpdate: () -> KaTeXMenu
    )
}

class BasicCardTypeHandler : CardTypeHandler {
    @Composable
    override fun HandleCardEdit(
        fields: Fields, ct: CT, vm: NavViewModel,
        changed: Boolean, getUIStyle: GetUIStyle, onUpdate: () -> KaTeXMenu
    ) {

        if (ct is CT.Basic) {
            if (!changed) {
                val cardDetails by remember { mutableStateOf(ct.toCardDetails()) }
                fields.question = rememberSaveable { mutableStateOf(cardDetails.question.value) }
                fields.answer = rememberSaveable { mutableStateOf(cardDetails.answer.value) }
            }
            EditBasicCard(fields)
        } else {
            EditBasicCard(fields)
        }
    }
}

class ThreeCardTypeHandler : CardTypeHandler {
    @Composable
    override fun HandleCardEdit(
        fields: Fields, ct: CT, vm: NavViewModel,
        changed: Boolean, getUIStyle: GetUIStyle, onUpdate: () -> KaTeXMenu
    ) {
        if (ct is CT.ThreeField) {
            if (!changed) {
                val cardDetails by remember { mutableStateOf(ct.toCardDetails()) }
                fields.question = rememberSaveable {
                    mutableStateOf(cardDetails.question.value)
                }
                fields.middleField = rememberSaveable {
                    mutableStateOf(cardDetails.middleField.value)
                }
                fields.answer = rememberSaveable {
                    mutableStateOf(cardDetails.answer.value)
                }
                fields.isQOrA = rememberSaveable {
                    mutableStateOf(cardDetails.isQorA.value)
                }
            }
            EditThreeCard(fields, getUIStyle)
        } else {
            EditThreeCard(fields, getUIStyle)
        }
    }
}

class HintCardTypeHandler : CardTypeHandler {
    @Composable
    override fun HandleCardEdit(
        fields: Fields, ct: CT, vm: NavViewModel,
        changed: Boolean, getUIStyle: GetUIStyle, onUpdate: () -> KaTeXMenu
    ) {
        if (ct is CT.Hint) {
            if (!changed) {
                val cardDetails by remember { mutableStateOf(ct.toCardDetails()) }
                fields.question = rememberSaveable {
                    mutableStateOf(cardDetails.question.value)
                }
                fields.middleField = rememberSaveable {
                    mutableStateOf(cardDetails.middleField.value)
                }
                fields.answer = rememberSaveable {
                    mutableStateOf(cardDetails.answer.value)
                }
            }
            EditHintCard(fields)
        } else {
            EditHintCard(fields)
        }
    }
}

class ChoiceCardTypeHandler : CardTypeHandler {
    @Composable
    override fun HandleCardEdit(
        fields: Fields, ct: CT, vm: NavViewModel,
        changed: Boolean, getUIStyle: GetUIStyle, onUpdate: () -> KaTeXMenu
    ) {
        if (ct is CT.MultiChoice) {
            if (!changed) {
                val cardDetails by remember { mutableStateOf(ct.toCardDetails()) }
                fields.question = rememberSaveable { mutableStateOf(cardDetails.question.value) }
                fields.choices[0].value = rememberSaveable {
                    cardDetails.choices[0].value
                }
                fields.choices[1].value = rememberSaveable {
                    cardDetails.choices[1].value
                }
                fields.choices[2].value = rememberSaveable {
                    cardDetails.choices[2].value
                }
                fields.choices[3].value = rememberSaveable {
                    cardDetails.choices[3].value
                }
                fields.correct = rememberSaveable { mutableStateOf(cardDetails.correct.value) }
            }
            EditChoiceCard(fields, getUIStyle)
        } else {
            EditChoiceCard(fields, getUIStyle)
        }
    }
}

class NotationCardTypeHandler : CardTypeHandler {
    @Composable
    override fun HandleCardEdit(
        fields: Fields, ct: CT, vm: NavViewModel,
        changed: Boolean, getUIStyle: GetUIStyle, onUpdate: () -> KaTeXMenu
    ) {
        if (ct is CT.Notation) {
            if (!changed) {
                val cardDetails by remember { mutableStateOf(ct.toCardDetails()) }
                fields.question = rememberSaveable { mutableStateOf(cardDetails.question.value) }
                fields.stringList = rememberSaveable { cardDetails.stringList }
                fields.answer = rememberSaveable { mutableStateOf(cardDetails.answer.value) }
            }
            EditNotationCard(fields, vm, getUIStyle, onUpdate)
        } else {
            EditNotationCard(fields, vm, getUIStyle, onUpdate)
        }

    }
}

fun returnCardTypeHandler(newType: String, currentType: String): CardTypeHandler? {
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



