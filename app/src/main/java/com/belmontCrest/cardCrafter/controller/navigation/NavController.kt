package com.belmontCrest.cardCrafter.controller.navigation

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.belmontCrest.cardCrafter.views.cardViews.addCardViews.AddCardView
import com.belmontCrest.cardCrafter.views.deckViews.AddDeckView
import com.belmontCrest.cardCrafter.views.cardViews.cardDeckViews.CardDeckView
import com.belmontCrest.cardCrafter.views.cardViews.editCardViews.EditCardsList
import com.belmontCrest.cardCrafter.views.deckViews.DeckView
import com.belmontCrest.cardCrafter.views.mainViews.MainView
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.navigation
import com.belmontCrest.cardCrafter.controller.AppViewModelProvider
import com.belmontCrest.cardCrafter.controller.cardHandlers.updateDecksCardList
import com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels.EditingCardListViewModel
import com.belmontCrest.cardCrafter.controller.viewModels.deckViewsModels.MainViewModel
import com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels.CardDeckViewModel
import com.belmontCrest.cardCrafter.model.uiModels.Fields
import com.belmontCrest.cardCrafter.model.uiModels.PreferencesManager
import com.belmontCrest.cardCrafter.supabase.controller.SupabaseViewModel
import com.belmontCrest.cardCrafter.supabase.view.ImportDeck
import com.belmontCrest.cardCrafter.ui.theme.ColorSchemeClass
import com.belmontCrest.cardCrafter.views.mainViews.GeneralSettings
import com.belmontCrest.cardCrafter.views.deckViews.EditDeckView
import com.belmontCrest.cardCrafter.views.cardViews.editCardViews.EditingCardView
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.supabase.view.OnlineDatabase
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun AppNavHost(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    editingCardListVM: EditingCardListViewModel,
    fields: Fields,
    modifier: Modifier = Modifier,
    supabase: SupabaseClient,
    preferences: PreferencesManager
) {
    val cardDeckVM: CardDeckViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val navViewModel: NavViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val cardsToUpdate by cardDeckVM.cardListToUpdate.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val colorScheme = remember { ColorSchemeClass() }
    var onDeckView by remember { mutableStateOf(false) }
    colorScheme.colorScheme = MaterialTheme.colorScheme
    val getUIStyle = rememberUpdatedState(
        GetUIStyle(
            colorScheme,
            preferences.darkTheme.value
        )
    ).value
    val selectedCard by navViewModel.card.collectAsStateWithLifecycle()
    /** Our Supabase Client and Views. */
    val supabaseVM: SupabaseViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val sbDeck by supabaseVM.deck.collectAsStateWithLifecycle()
    val onlineDatabase = OnlineDatabase(
        supabase,
        getUIStyle,
        supabaseVM,
        mainViewModel.deckUiState.collectAsStateWithLifecycle().value.deckList
    )
    val importDeck = ImportDeck(
        supabase,
        getUIStyle,
        supabaseVM,
        preferences
    )
    /** End of all Supabase things. */
    val cardDeckView = CardDeckView(
        cardDeckVM, getUIStyle, fields
    )
    val editDeckView = EditDeckView(fields, getUIStyle)
    val deckEditView =
        EditCardsList(
            editingCardListVM,
            fields, listState, getUIStyle
        )
    val editingCardView = EditingCardView(
        editingCardListVM, getUIStyle
    )
    val mainView = MainView(getUIStyle, fields)
    val addDeckView = AddDeckView(getUIStyle)
    val deckView = DeckView(
        fields, getUIStyle,
    )
    val addCardView = AddCardView(fields, getUIStyle)
    val generalSettings = GeneralSettings(getUIStyle, preferences)
    val coroutineScope = rememberCoroutineScope()
    val deck by navViewModel.deck.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = DeckListDestination.route,
        modifier = modifier
    ) {
        composable(DeckListDestination.route) {
            BackHandler {
                // Exit the app when back is pressed on the main screen
                (navController.context as? Activity)?.finish()
            }
            mainView.DeckList(
                mainViewModel,
                // In DeckList Composable
                onNavigateToDeck = { id ->
                    fields.leftDueCardView.value = false
                    fields.inDeckClicked.value = false
                    fields.scrollPosition.value = 0
                    onDeckView = true
                    navController.navigate(DeckOptionsDestination.createRoute(id))
                    coroutineScope.launch {
                        navViewModel.getDeckById(id)
                    }
                    coroutineScope.launch {
                        editingCardListVM.getAllCardsForDeck(id)
                    }
                },
                onNavigateToAddDeck = {
                    navController.navigate(AddDeckDestination.route)
                },
                onNavigateToSettings = {
                    navController.navigate(SettingsDestination.route)
                },
                onNavigateToSBDeckList = {
                    navController.navigate(SupabaseDestination.route)
                }
            )
        }
        composable(SupabaseDestination.route, enterTransition = { null }, exitTransition = { null }) {
            BackHandler {
                fields.mainClicked.value = false
                mainViewModel.updateCurrentTime()
                navController.popBackStack(
                    DeckListDestination.route, inclusive = false
                )
            }
            onlineDatabase.SupabaseView(
                onNavigate = {
                    fields.mainClicked.value = false
                    navController.navigate(DeckListDestination.route)
                    mainViewModel.updateCurrentTime()
                },
                onImportDeck = { uuid ->
                    navController.navigate(ImportSBDestination.route)
                    coroutineScope.launch {
                        supabaseVM.updateUUID(uuid)
                    }
                }
            )
        }
        composable(ImportSBDestination.route){
            BackHandler {
                navController.popBackStack(
                    SupabaseDestination.route, inclusive = false
                )
            }
            sbDeck?.let {
                importDeck.GetDeck(
                    deck = it,
                    onNavigate = {
                        navController.navigate(SupabaseDestination.route)
                    }
                )
            }
        }
        composable(SettingsDestination.route, enterTransition = { null }, exitTransition = { null }) {
            BackHandler {
                fields.mainClicked.value = false
                navController.popBackStack(
                    DeckListDestination.route, inclusive = false
                )
            }
            generalSettings.SettingsView(
                onNavigate = {
                    fields.mainClicked.value = false
                    navController.navigate(DeckListDestination.route)
                }
            )
        }
        composable(AddDeckDestination.route, enterTransition = { null }, exitTransition = { null }) {
            BackHandler {
                fields.mainClicked.value = false
                navController.popBackStack(
                    DeckListDestination.route,
                    inclusive = false
                )
            }
            addDeckView.AddDeck(
                onNavigate = {
                    fields.mainClicked.value = false
                    navController.navigate(DeckListDestination.route)
                },
                reviewAmount = preferences.reviewAmount.intValue.toString(),
                cardAmount = preferences.cardAmount.intValue.toString()
            )
        }
        navigation(
            route = DeckOptionsDestination.route,
            startDestination = DeckViewDestination.route
        ) {
            composable(
                route = DeckViewDestination.route,
                enterTransition = { null }, exitTransition = { null }
            ) { backStackEntry ->
                val deckId = backStackEntry.arguments?.getString("deckId")!!.toIntOrNull()
                BackHandler {
                    fields.scrollPosition.value = 0
                    fields.inDeckClicked.value = true
                    fields.mainClicked.value = false
                    navController.popBackStack(
                        DeckListDestination.route,
                        inclusive = false
                    )
                }
                /**
                 * If the application restarts, onDeckView value will be false
                 * meaning it'll get the latest value of deck if it changes,
                 * it'll stop getting the value once you return to the DeckList
                 * and go back into the DeckOptions, since onDeckView will become
                 * true. */
                LaunchedEffect(Unit) {
                    if (!onDeckView) {
                        coroutineScope.launch {
                            navViewModel.getDeckById(deckId ?: 0)
                            cardDeckVM.updateWhichDeck(deckId ?: 0)
                        }
                    }
                }
                deck?.let {
                    deckView.ViewEditDeck(
                        deck = it,
                        onNavigate = {
                            mainViewModel.updateCurrentTime()
                            navViewModel.resetCard()
                            fields.scrollPosition.value = 0
                            fields.inDeckClicked.value = true
                            fields.mainClicked.value = false
                            navController.navigate(DeckListDestination.route)
                        },
                        goToAddCard = { id ->
                            fields.inDeckClicked.value = true
                            fields.mainClicked.value = false
                            navController.navigate(AddCardDestination.createRoute(id))
                        },
                        goToDueCards = { id ->
                            fields.inDeckClicked.value = true
                            cardDeckVM.updateWhichDeck(id)
                            fields.mainClicked.value = false
                            fields.leftDueCardView.value = false
                            navController.navigate(ViewDueCardsDestination.createRoute(id))
                        },
                        goToEditDeck = { id, name ->
                            fields.inDeckClicked.value = true
                            fields.mainClicked.value = false
                            navController.navigate(
                                EditDeckDestination.createRoute(
                                    id,
                                    name
                                )
                            )
                        },
                        goToViewCards = { id ->
                            fields.inDeckClicked.value = true
                            fields.mainClicked.value = false
                            navController.navigate(ViewAllCardsDestination.createRoute(id))
                        },
                    )
                }
            }
            composable(AddCardDestination.route) { backStackEntry ->
                val deckId = backStackEntry.arguments?.getString("deckId")!!.toIntOrNull()
                BackHandler {
                    fields.inDeckClicked.value = false
                    fields.resetFields()
                    deck?.let {
                        coroutineScope.launch {
                            if (fields.cardsAdded.value > 0) {
                                navViewModel.updateCardsLeft(it, fields.cardsAdded.value)
                                fields.cardsAdded.value = 0
                            }
                        }
                    }
                    navController.popBackStack(
                        DeckViewDestination.createRoute(deckId ?: 0),
                        inclusive = false
                    )
                }

                deck?.let {
                    addCardView.AddCard(
                        deck = it,
                        onNavigate = {
                            fields.inDeckClicked.value = false
                            fields.resetFields()
                            coroutineScope.launch {
                                if (fields.cardsAdded.value > 0) {
                                    navViewModel.updateCardsLeft(it, fields.cardsAdded.value)
                                    fields.cardsAdded.value = 0
                                }
                            }
                            navController.navigate(DeckViewDestination.createRoute(deckId ?: 0))
                        }
                    )
                }
            }
            composable(ViewDueCardsDestination.route) { backStackEntry ->
                val deckId = backStackEntry.arguments?.getString("deckId")!!.toIntOrNull()

                BackHandler {
                    fields.inDeckClicked.value = false
                    navController.popBackStack(
                        DeckViewDestination.createRoute(deckId ?: 0),
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
                        deck = it,
                        onNavigate = {
                            fields.inDeckClicked.value = false
                            navController.navigate(DeckViewDestination.createRoute(deckId ?: 0))
                            fields.leftDueCardView.value = true
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
                    )
                }
            }
            composable(EditDeckDestination.route) { backStackEntry ->
                val deckId = backStackEntry.arguments?.getString("deckId")!!.toIntOrNull()
                val currentName = backStackEntry.arguments?.getString("currentName")

                BackHandler {
                    fields.inDeckClicked.value = false
                    navController.popBackStack(
                        DeckViewDestination.createRoute(deckId ?: 0),
                        inclusive = false
                    )
                }
                deck?.let {
                    editDeckView.EditDeck(
                        currentName = currentName ?: "",
                        deck = it,
                        onNavigate = {
                            fields.inDeckClicked.value = false
                            navController.navigate(DeckViewDestination.createRoute(deckId ?: 0))
                        },
                        onDelete = {
                            fields.inDeckClicked.value = false
                            navController.navigate(DeckListDestination.route)
                        }
                    )
                }
            }
            composable(ViewAllCardsDestination.route) { backStackEntry ->
                val deckId = backStackEntry.arguments?.getString("deckId")!!.toIntOrNull()
                BackHandler {
                    fields.inDeckClicked.value = false
                    navController.popBackStack(
                        DeckViewDestination.createRoute(deckId ?: 0),
                        inclusive = false
                    )
                }
                deck?.let { thisDeck ->
                    deckEditView.ViewFlashCards(
                        deck = thisDeck,
                        onNavigate = {
                            fields.inDeckClicked.value = false
                            navController.navigate(DeckViewDestination.createRoute(deckId ?: 0))
                        },
                        goToEditCard = { index, cardId ->
                            coroutineScope.launch {
                                navViewModel.getCardById(cardId)
                            }
                            fields.resetFields()
                            navController.navigate(
                                EditingCardDestination.createRoute(thisDeck.id, index)
                            )
                        }
                    )
                }
            }
            composable(
                route = EditingCardDestination.route,
                arguments = listOf(navArgument("deckId") { type = NavType.IntType },
                    navArgument("index") { type = NavType.IntType })
            ) { backStackEntry ->
                val deckId = backStackEntry.arguments?.getInt("deckId")
                val index = backStackEntry.arguments?.getInt("index")

                BackHandler {
                    deckEditView.isEditing.value = false
                    fields.inDeckClicked.value = false
                    navController.popBackStack(
                        ViewAllCardsDestination.createRoute(deckId ?: 0),
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
                            deckEditView.isEditing.value = false
                            fields.inDeckClicked.value = false
                            navController.navigate(
                                ViewAllCardsDestination.createRoute(deckId ?: 0)
                            )
                        }
                    )
                }
            }
        }
    }
}