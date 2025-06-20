package com.belmontCrest.cardCrafter.controller.cardHandlers

import androidx.compose.runtime.Composable
import com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels.EditCardViewModel
import com.belmontCrest.cardCrafter.views.cardViews.editCardViews.EditBasicCard
import com.belmontCrest.cardCrafter.views.cardViews.editCardViews.EditHintCard
import com.belmontCrest.cardCrafter.views.cardViews.editCardViews.EditThreeCard
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.uiFunctions.katex.menu.KaTeXMenu
import com.belmontCrest.cardCrafter.views.cardViews.editCardViews.EditChoiceCard
import com.belmontCrest.cardCrafter.views.cardViews.editCardViews.EditNotationCard

interface CardTypeHandler {
    @Composable
    fun HandleCardEdit(
        vm: EditCardViewModel, getUIStyle: GetUIStyle, onUpdate: () -> KaTeXMenu
    )
}

class BasicCardTypeHandler : CardTypeHandler {
    @Composable
    override fun HandleCardEdit(
        vm: EditCardViewModel, getUIStyle: GetUIStyle, onUpdate: () -> KaTeXMenu
    ) {
        EditBasicCard(vm)
    }
}

class ThreeCardTypeHandler : CardTypeHandler {
    @Composable
    override fun HandleCardEdit(
        vm: EditCardViewModel, getUIStyle: GetUIStyle, onUpdate: () -> KaTeXMenu
    ) {
        EditThreeCard(vm, getUIStyle)
    }
}

class HintCardTypeHandler : CardTypeHandler {
    @Composable
    override fun HandleCardEdit(
        vm: EditCardViewModel, getUIStyle: GetUIStyle, onUpdate: () -> KaTeXMenu
    ) {
        EditHintCard(vm)
    }
}

class ChoiceCardTypeHandler : CardTypeHandler {
    @Composable
    override fun HandleCardEdit(
        vm: EditCardViewModel, getUIStyle: GetUIStyle, onUpdate: () -> KaTeXMenu
    ) {
        EditChoiceCard(vm, getUIStyle)
    }
}

class NotationCardTypeHandler : CardTypeHandler {
    @Composable
    override fun HandleCardEdit(
        vm: EditCardViewModel, getUIStyle: GetUIStyle, onUpdate: () -> KaTeXMenu
    ) {
        EditNotationCard(vm, getUIStyle, onUpdate)
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



