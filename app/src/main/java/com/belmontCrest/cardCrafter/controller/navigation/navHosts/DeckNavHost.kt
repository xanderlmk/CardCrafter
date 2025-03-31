package com.belmontCrest.cardCrafter.controller.navigation.navHosts

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.belmontCrest.cardCrafter.controller.cardHandlers.updateDecksCardList
import com.belmontCrest.cardCrafter.controller.navigation.AddCardDestination
import com.belmontCrest.cardCrafter.controller.navigation.DeckListDestination
import com.belmontCrest.cardCrafter.controller.navigation.DeckNavDestination
import com.belmontCrest.cardCrafter.controller.navigation.DeckViewDestination
import com.belmontCrest.cardCrafter.controller.navigation.EditDeckDestination
import com.belmontCrest.cardCrafter.controller.navigation.EditingCardDestination
import com.belmontCrest.cardCrafter.controller.navigation.MainNavDestination
import com.belmontCrest.cardCrafter.controller.navigation.NavViewModel
import com.belmontCrest.cardCrafter.controller.navigation.ViewAllCardsDestination
import com.belmontCrest.cardCrafter.controller.navigation.ViewDueCardsDestination
import com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels.CardDeckViewModel
import com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels.EditingCardListViewModel
import com.belmontCrest.cardCrafter.controller.viewModels.deckViewsModels.MainViewModel
import com.belmontCrest.cardCrafter.model.uiModels.Fields
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.views.cardViews.addCardViews.AddCardView
import com.belmontCrest.cardCrafter.views.cardViews.cardDeckViews.CardDeckView
import com.belmontCrest.cardCrafter.views.cardViews.editCardViews.EditCardsList
import com.belmontCrest.cardCrafter.views.cardViews.editCardViews.EditingCardView
import com.belmontCrest.cardCrafter.views.deckViews.DeckView
import com.belmontCrest.cardCrafter.views.deckViews.EditDeckView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun DeckNavHost(
    navController: NavHostController,
    cardDeckVM: CardDeckViewModel,
    fields: Fields, mainViewModel: MainViewModel,
    onDeckView: Boolean, navViewModel: NavViewModel,
    getUIStyle: GetUIStyle, editingCardListVM: EditingCardListViewModel
) {
    val deckNavController = rememberNavController()
    val listState = rememberLazyListState()

    val deckView = DeckView(
        fields, getUIStyle,
    )
    val addCardView = AddCardView(fields, getUIStyle)
    val cardDeckView = CardDeckView(
        cardDeckVM, getUIStyle, fields
    )
    val editDeckView = EditDeckView(fields, getUIStyle)
    val editingCardView = EditingCardView(
        editingCardListVM, getUIStyle
    )
    val editCardsList =
        EditCardsList(
            editingCardListVM,
            fields, listState, getUIStyle
        )

    LaunchedEffect(Unit) {
        navViewModel.updateDeckNav(deckNavController)
    }

    val coroutineScope = rememberCoroutineScope()

    val deck by navViewModel.deck.collectAsStateWithLifecycle()
    val selectedCard by navViewModel.card.collectAsStateWithLifecycle()
    val cardsToUpdate by cardDeckVM.cardListToUpdate.collectAsStateWithLifecycle()
    NavHost(
        navController = deckNavController,
        startDestination = DeckViewDestination.route,
        route = DeckNavDestination.route,
    ) {
        composable(
            route = DeckViewDestination.route,
            enterTransition = { null }, exitTransition = { null }
        ) {
            BackHandler {
                navViewModel.updateRoute(DeckListDestination.route)
                BackNavHandler.returnToDeckListFromDeck(
                    navController, mainViewModel.updateCurrentTime(),
                    cardDeckVM.updateIndex(0), fields
                )
            }
            /**
             * If the application restarts, onDeckView value will be false
             * meaning it'll get the latest value of deck if it changes,
             * it'll stop getting the value once you return to the DeckList
             * and go back into the DeckOptions, since onDeckView will become
             * true. */

            deck.let {
                LaunchedEffect(Unit) {
                    if (!onDeckView) {
                        coroutineScope.launch {
                            navViewModel.getDeckById(it?.id ?: 0)
                            cardDeckVM.updateWhichDeck(it?.id ?: 0)
                        }
                    }
                }
            }
            deck?.let {
                deckView.ViewEditDeck(
                    deck = it,
                    goToAddCard = { id ->
                        fields.inDeckClicked.value = true
                        fields.mainClicked.value = false
                        navViewModel.updateRoute(AddCardDestination.createRoute(id))
                        deckNavController.navigate(AddCardDestination.createRoute(id))
                    },
                    goToDueCards = { id ->
                        fields.inDeckClicked.value = true
                        cardDeckVM.updateWhichDeck(id)
                        fields.mainClicked.value = false
                        fields.leftDueCardView.value = false
                        navViewModel.updateRoute(ViewDueCardsDestination.route)
                        deckNavController.navigate(ViewDueCardsDestination.route)
                    }
                )
            }
        }
        composable(AddCardDestination.route) {
            BackHandler {
                fields.inDeckClicked.value = false
                fields.resetFields()
                navViewModel.updateRoute(DeckViewDestination.route)
                deckNavController.popBackStack(
                    DeckViewDestination.route,
                    inclusive = false
                )
            }

            deck?.let {
                addCardView.AddCard(
                    deck = it,
                    onNavigate = {
                        fields.inDeckClicked.value = false
                        fields.resetFields()
                        navViewModel.updateRoute(DeckViewDestination.route)
                        deckNavController.navigate(
                            DeckViewDestination.route
                        )
                    }
                )
            }
        }
        composable(ViewDueCardsDestination.route) {
            BackHandler {
                fields.inDeckClicked.value = false
                navViewModel.updateRoute(DeckViewDestination.route)
                deckNavController.popBackStack(
                    DeckViewDestination.route,
                    inclusive = false
                )
                fields.leftDueCardView.value = true
                deck?.let {
                    /** If the list is empty, no cards
                     *  have been due even before the user joined,
                     *  or the user finished the deck.
                     */
                    if (cardsToUpdate.isNotEmpty()) {
                        coroutineScope.launch(Dispatchers.IO) {
                            updateDecksCardList(
                                it,
                                cardsToUpdate,
                                cardDeckVM
                            )
                        }
                    }
                }
            }
            deck?.let {
                cardDeckView.ViewCard(
                    deck = it
                )
            }
        }
        composable(EditDeckDestination.route) { backStackEntry ->
            val currentName =
                backStackEntry.arguments?.getString("currentName")

            BackHandler {
                fields.inDeckClicked.value = false
                navViewModel.updateRoute(DeckViewDestination.route)
                deckNavController.popBackStack(
                    DeckViewDestination.route,
                    inclusive = false
                )
            }
            deck?.let {
                editDeckView.EditDeck(
                    currentName = currentName ?: "",
                    deck = it,
                    onNavigate = {
                        fields.inDeckClicked.value = false
                        navViewModel.updateRoute(DeckViewDestination.route)
                        deckNavController.navigate(
                            DeckViewDestination.route
                        )
                    },
                    onDelete = {
                        fields.inDeckClicked.value = false
                        navViewModel.updateRoute(MainNavDestination.route)
                        navController.navigate(MainNavDestination.route)
                    }
                )
            }
        }
        composable(ViewAllCardsDestination.route) {
            BackHandler {
                fields.inDeckClicked.value = false
                navViewModel.updateRoute(DeckViewDestination.route)
                deckNavController.popBackStack(
                    DeckViewDestination.route,
                    inclusive = false
                )
            }
            deck?.let { thisDeck ->
                editCardsList.ViewFlashCards(
                    onNavigate = {
                        fields.inDeckClicked.value = false
                        navViewModel.updateRoute(DeckViewDestination.route)
                        deckNavController.navigate(DeckViewDestination.route)
                    },
                    goToEditCard = { index, cardId ->
                        coroutineScope.launch {
                            navViewModel.getCardById(cardId)
                        }
                        fields.resetFields()
                        navViewModel.updateRoute(
                            EditingCardDestination.createRoute(index)
                        )
                        deckNavController.navigate(
                            EditingCardDestination.createRoute(index)
                        )
                    }
                )
            }
        }
        composable(
            route = EditingCardDestination.route,
            arguments = listOf(
                navArgument("index") { type = NavType.IntType })
        ) { backStackEntry ->
            val index = backStackEntry.arguments?.getInt("index")

            BackHandler {
                editCardsList.isEditing.value = false
                fields.inDeckClicked.value = false

                navViewModel.updateRoute(ViewAllCardsDestination.route)
                deckNavController.popBackStack(
                    ViewAllCardsDestination.route,
                    inclusive = false
                )
            }
            selectedCard?.let {
                editingCardView.EditFlashCardView(
                    card = it,
                    fields = fields,
                    selectedCard = mutableStateOf(selectedCard),
                    index = index ?: 0,
                    onNavigateBack = {
                        editCardsList.isEditing.value = false
                        fields.inDeckClicked.value = false

                        navViewModel.updateRoute(ViewAllCardsDestination.route)
                        deckNavController.navigate(ViewAllCardsDestination.route)
                    }
                )
            }
        }
    }
}