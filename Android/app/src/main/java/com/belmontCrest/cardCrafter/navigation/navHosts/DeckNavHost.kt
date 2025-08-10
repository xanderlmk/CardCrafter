package com.belmontCrest.cardCrafter.navigation.navHosts

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.belmontCrest.cardCrafter.navigation.destinations.AddCardDestination
import com.belmontCrest.cardCrafter.navigation.destinations.DeckListDestination
import com.belmontCrest.cardCrafter.navigation.destinations.DeckNavDestination
import com.belmontCrest.cardCrafter.navigation.destinations.DeckViewDestination
import com.belmontCrest.cardCrafter.navigation.destinations.EditDeckDestination
import com.belmontCrest.cardCrafter.navigation.destinations.EditingCardDestination
import com.belmontCrest.cardCrafter.navigation.destinations.MainNavDestination
import com.belmontCrest.cardCrafter.navigation.NavViewModel
import com.belmontCrest.cardCrafter.navigation.destinations.ViewAllCardsDestination
import com.belmontCrest.cardCrafter.navigation.destinations.ViewDueCardsDestination
import com.belmontCrest.cardCrafter.controller.view.models.cardViewsModels.CardDeckViewModel
import com.belmontCrest.cardCrafter.controller.view.models.cardViewsModels.EditCardViewModel
import com.belmontCrest.cardCrafter.controller.view.models.cardViewsModels.EditingCardListViewModel
import com.belmontCrest.cardCrafter.model.application.AppVMProvider
import com.belmontCrest.cardCrafter.model.application.PreferencesManager
import com.belmontCrest.cardCrafter.model.application.setPreferenceValues
import com.belmontCrest.cardCrafter.model.ui.Fields
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.views.cardViews.addCardViews.AddCardView
import com.belmontCrest.cardCrafter.views.cardViews.cardDeckViews.CardDeckView
import com.belmontCrest.cardCrafter.views.cardViews.editCardViews.EditCardsList
import com.belmontCrest.cardCrafter.views.cardViews.editCardViews.EditingCardView
import com.belmontCrest.cardCrafter.views.deckViews.DeckView
import com.belmontCrest.cardCrafter.views.deckViews.EditDeckView
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun DeckNavHost(
    navController: NavHostController, fields: Fields, onDeckView: Boolean,
    navViewModel: NavViewModel,
    getUIStyle: GetUIStyle, editingCardListVM: EditingCardListViewModel,
    preferences: PreferencesManager
) {
    val deckNavController = rememberNavController()
    val listState = rememberLazyListState()
    val pc = setPreferenceValues(preferences)
    val startingDeckRoute by navViewModel.startingDeckRoute.collectAsStateWithLifecycle()
    val deckView = DeckView(
        fields, getUIStyle,
    )
    val addCardView = AddCardView(getUIStyle, pc, navViewModel)

    val editDeckView = EditDeckView(fields, getUIStyle)
    val editingCardView = EditingCardView(getUIStyle, pc)
    val editCardsList =
        EditCardsList(
            editingCardListVM,
            fields, listState, getUIStyle
        )

    LaunchedEffect(Unit) {
        navViewModel.updateDeckNav(deckNavController)
    }
    val coroutineScope = rememberCoroutineScope()

    /** Determining whether to start on the DeckView or DueCards */
    val startDestination = if (startingDeckRoute.name == ViewDueCardsDestination.route) {
        ViewDueCardsDestination.route
    } else {
        DeckViewDestination.route
    }

    val wd by navViewModel.wd.collectAsStateWithLifecycle()
    val sc by navViewModel.card.collectAsStateWithLifecycle()
    val showKB by navViewModel.showKatexKeyboard.collectAsStateWithLifecycle()

    NavHost(
        navController = deckNavController,
        startDestination = startDestination,
        route = DeckNavDestination.route,
    ) {
        composable(
            route = DeckViewDestination.route,
            enterTransition = { null }, exitTransition = { null }
        ) {
            BackHandler {
                navViewModel.updateUIIndex(0)
                navViewModel.updateRoute(DeckListDestination.route)
                BackNavHandler.returnToDeckListFromDeck(
                    navController, navViewModel.updateTime(),
                    navViewModel.getDeckById(0), fields
                )
            }
            /**
             * If the application restarts, onDeckView value will be false
             * meaning it'll get the latest value of deck if it changes,
             * it'll stop getting the value once you return to the DeckList
             * and go back into the DeckOptions, since onDeckView will become
             * true. */

            wd.deck.let {
                LaunchedEffect(Unit) {
                    if (!onDeckView) {
                        coroutineScope.launch {
                            navViewModel.getDeckById(it?.id ?: 0)
                        }
                    }
                }
            }
            wd.deck?.let {
                deckView.ViewEditDeck(
                    deck = it,
                    goToAddCard = { id, uuid ->
                        fields.inDeckClicked.value = true
                        fields.mainClicked.value = false
                        navViewModel.updateRoute(AddCardDestination.route)
                        deckNavController.navigate(AddCardDestination.createRoute(id, uuid))
                    },
                    goToDueCards = {
                        fields.navigateToDueCards()
                        navViewModel.updateRoute(ViewDueCardsDestination.route)
                        deckNavController.navigate(ViewDueCardsDestination.route)
                    }
                )
            }
        }
        composable(
            AddCardDestination.route,
            arguments = listOf(
                navArgument("deckId") { type = NavType.IntType },
                navArgument("deckUUID") { type = NavType.StringType }
            )
        ) {
            BackHandler {
                if (showKB) {
                    navViewModel.toggleKeyboard(); navViewModel.resetOffset()
                } else {
                    fields.inDeckClicked.value = false
                    navViewModel.resetFields()
                    navViewModel.resetKeyboardStuff()
                    navViewModel.updateRoute(DeckViewDestination.route)
                    deckNavController.popBackStack(
                        DeckViewDestination.route,
                        inclusive = false
                    )
                }
            }

            wd.deck?.let {
                addCardView.AddCard(
                    deck = it
                )
            }
        }
        composable(ViewDueCardsDestination.route) {
            val cardDeckVM: CardDeckViewModel = viewModel(factory = AppVMProvider.Factory)
            val cardDeckView = CardDeckView(cardDeckVM, getUIStyle, fields)
            BackHandler {
                if (startingDeckRoute.name == ViewDueCardsDestination.route) {
                    navViewModel.updateRoute(DeckListDestination.route)
                    BackNavHandler.returnToDeckListFromDeck(
                        navController, navViewModel.updateTime(),
                        cardDeckVM.updateIndex(0), fields
                    )
                } else {
                    fields.inDeckClicked.value = false
                    navViewModel.updateRoute(DeckViewDestination.route)
                    deckNavController.popBackStack(
                        DeckViewDestination.route,
                        inclusive = false
                    )
                }
                fields.leftDueCardView.value = true
            }
            wd.deck?.let {
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
                navViewModel.updateTime()
                deckNavController.popBackStack(
                    DeckViewDestination.route, inclusive = false
                )
            }
            wd.deck?.let {
                editDeckView.EditDeck(
                    currentName = currentName ?: "",
                    deck = it,
                    onNavigate = {
                        fields.inDeckClicked.value = false
                        navViewModel.updateRoute(DeckViewDestination.route)
                        deckNavController.navigate(DeckViewDestination.route)
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
            val isSelecting by navViewModel.isSelecting.collectAsStateWithLifecycle()
            BackHandler {
                if (isSelecting) {
                    navViewModel.resetSelection()
                } else {
                    fields.inDeckClicked.value = false
                    navViewModel.resetSearchQuery()
                    navViewModel.updateRoute(DeckViewDestination.route)
                    deckNavController.popBackStack(DeckViewDestination.route, inclusive = false)
                }
            }
            wd.deck?.let { thisDeck ->
                editCardsList.ViewFlashCards(
                    navVM = navViewModel,
                    goToEditCard = { cardId ->
                        coroutineScope.launch { navViewModel.getCardById(cardId) }
                        navViewModel.updateRoute(EditingCardDestination.route)
                        deckNavController.navigate(EditingCardDestination.route)
                    }
                )
            }
        }
        composable(route = EditingCardDestination.route) { backStackEntry ->
            BackHandler {
                if (showKB) {
                    navViewModel.toggleKeyboard(); navViewModel.resetOffset()
                } else {
                    fields.navigateToCardList()
                    navViewModel.resetCard()
                    navViewModel.resetKeyboardStuff()
                    navViewModel.updateRoute(ViewAllCardsDestination.route)
                    deckNavController.popBackStack(
                        ViewAllCardsDestination.route, inclusive = false
                    )
                }
            }
            val editCardVM: EditCardViewModel = viewModel(factory = AppVMProvider.Factory)
            sc.ct?.let {
                editingCardView.EditFlashCardView(
                    ct = it, newType = navViewModel.type.collectAsStateWithLifecycle().value,
                    editCardVM = editCardVM,
                    onNavigateBack = {
                        navViewModel.resetKeyboardStuff()
                        fields.navigateToCardList()
                        navViewModel.resetCard()
                        navViewModel.updateRoute(ViewAllCardsDestination.route)
                        deckNavController.navigate(ViewAllCardsDestination.route)
                    }
                )
            }
        }
    }
}