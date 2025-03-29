package com.belmontCrest.cardCrafter.controller.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels.CardDeckViewModel
import com.belmontCrest.cardCrafter.model.tablesAndApplication.Deck
import com.belmontCrest.cardCrafter.model.uiModels.Fields
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.backButtonModifier
import com.belmontCrest.cardCrafter.ui.theme.redoButtonModifier
import com.belmontCrest.cardCrafter.ui.theme.settingsButtonModifier
import com.belmontCrest.cardCrafter.views.miscFunctions.BackButton
import com.belmontCrest.cardCrafter.views.miscFunctions.RedoCardButton
import com.belmontCrest.cardCrafter.views.miscFunctions.SettingsButton

@Composable
fun ActionIconButton(
    route: String,
    navController: NavHostController,
    getUIStyle: GetUIStyle,
    cardDeckVM : CardDeckViewModel,
    deck : Deck,
    fields: Fields,
) {
    when (route) {
        DeckViewDestination.route -> {
            SettingsButton(
                onNavigateToEditDeck = {
                    if (!fields.inDeckClicked.value) {
                        fields.inDeckClicked.value = true
                        fields.mainClicked.value = false
                        navController.navigate(
                            EditDeckDestination.createRoute(
                                deck.id,
                                deck.name
                            )
                        )
                    }
                },
                onNavigateToEditCards = {
                    if (!fields.inDeckClicked.value) {
                        fields.inDeckClicked.value = true
                        fields.mainClicked.value = false
                        DeckViewDestination.updateName(deck.name)
                        navController.navigate(
                            ViewAllCardsDestination.createRoute(
                                deck.id
                            )
                        )
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
                    navController.navigate(DeckViewDestination.route)
                },
                getUIStyle = getUIStyle,
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .backButtonModifier()

            )
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
    }
}