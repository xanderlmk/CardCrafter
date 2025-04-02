package com.belmontCrest.cardCrafter.controller.navigation.drawer

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.belmontCrest.cardCrafter.controller.navigation.AddCardDestination
import com.belmontCrest.cardCrafter.controller.navigation.DeckNavDestination
import com.belmontCrest.cardCrafter.controller.navigation.DeckViewDestination
import com.belmontCrest.cardCrafter.controller.navigation.EditDeckDestination
import com.belmontCrest.cardCrafter.controller.navigation.EditingCardDestination
import com.belmontCrest.cardCrafter.controller.navigation.NavViewModel
import com.belmontCrest.cardCrafter.controller.navigation.ViewAllCardsDestination
import com.belmontCrest.cardCrafter.controller.navigation.ViewDueCardsDestination
import com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels.CardDeckViewModel
import com.belmontCrest.cardCrafter.model.tablesAndApplication.Deck
import com.belmontCrest.cardCrafter.model.uiModels.Fields
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.backButtonModifier
import com.belmontCrest.cardCrafter.ui.theme.redoButtonModifier
import com.belmontCrest.cardCrafter.ui.theme.settingsButtonModifier
import com.belmontCrest.cardCrafter.views.miscFunctions.BackButton
import com.belmontCrest.cardCrafter.views.miscFunctions.CardOptionsButton
import com.belmontCrest.cardCrafter.views.miscFunctions.CardTypesButton
import com.belmontCrest.cardCrafter.views.miscFunctions.RedoCardButton
import com.belmontCrest.cardCrafter.views.miscFunctions.SettingsButton
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun ActionIconButton(
    getUIStyle: GetUIStyle,
    cardDeckVM: CardDeckViewModel,
    deck: Deck,
    fields: Fields,
    navViewModel: NavViewModel
) {
    val deckNavController by navViewModel.deckNav.collectAsStateWithLifecycle()
    val sc by navViewModel.card.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val onNavigateBack : () -> Unit = {
        fields.isEditing.value = false
        fields.inDeckClicked.value = false
        coroutineScope.launch {
            navViewModel.getCardById(0)
        }
        navViewModel.updateRoute(ViewAllCardsDestination.route)
        deckNavController?.navigate(ViewAllCardsDestination.route)
    }
    val cr = navViewModel.route.collectAsStateWithLifecycle().value

    when (cr.name) {
        DeckNavDestination.route -> {
            SettingsButton(
                onNavigateToEditDeck = {
                    if (!fields.inDeckClicked.value) {
                        fields.inDeckClicked.value = true
                        fields.mainClicked.value = false
                        navViewModel.updateRoute(
                            EditDeckDestination.createRoute(deck.name)
                        )
                        deckNavController?.navigate(
                            EditDeckDestination.createRoute(deck.name)
                        )
                    }
                },
                onNavigateToEditCards = {
                    if (!fields.inDeckClicked.value) {
                        fields.inDeckClicked.value = true
                        fields.mainClicked.value = false
                        navViewModel.updateRoute(ViewAllCardsDestination.route)
                        deckNavController?.navigate(ViewAllCardsDestination.route)
                    }
                },
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .settingsButtonModifier(),
                getUIStyle = getUIStyle,
                fields = fields
            )
        }

        DeckViewDestination.route -> {
            SettingsButton(
                onNavigateToEditDeck = {
                    if (!fields.inDeckClicked.value) {
                        fields.inDeckClicked.value = true
                        fields.mainClicked.value = false
                        navViewModel.updateRoute(
                            EditDeckDestination.createRoute(deck.name)
                        )
                        deckNavController?.navigate(
                            EditDeckDestination.createRoute(deck.name)
                        )
                    }
                },
                onNavigateToEditCards = {
                    if (!fields.inDeckClicked.value) {
                        fields.inDeckClicked.value = true
                        fields.mainClicked.value = false
                        navViewModel.updateRoute(ViewAllCardsDestination.route)
                        deckNavController?.navigate(ViewAllCardsDestination.route)
                    }
                },
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .settingsButtonModifier(),
                getUIStyle = getUIStyle,
                fields = fields
            )
        }

        ViewAllCardsDestination.route -> {
            BackButton(
                onBackClick = {
                    fields.inDeckClicked.value = false
                    navViewModel.updateRoute(DeckViewDestination.route)
                    deckNavController?.navigate(DeckViewDestination.route)
                },
                getUIStyle = getUIStyle,
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .backButtonModifier()

            )
        }

        AddCardDestination.route -> {
            CardTypesButton(getUIStyle, navViewModel)
        }
        ViewDueCardsDestination.route -> {
            RedoCardButton(
                onRedoClick = {
                    cardDeckVM.updateRedoClicked(true)
                },
                modifier = Modifier
                    .redoButtonModifier(),
                getUIStyle = getUIStyle
            )
        }

        EditingCardDestination.route -> {
            val expanded = rememberSaveable { mutableStateOf(false) }
            sc.card?.let {
                CardOptionsButton(
                    navViewModel, getUIStyle, it, fields,
                    expanded, Modifier, onNavigateBack
                )
            }
        }
    }
}
