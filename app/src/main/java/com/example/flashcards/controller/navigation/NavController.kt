package com.example.flashcards.controller.navigation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.flashcards.views.cardViews.addCardViews.AddCardView
import com.example.flashcards.views.deckViews.AddDeckView
import com.example.flashcards.views.cardViews.cardDeckViews.CardDeckView
import com.example.flashcards.views.miscFunctions.ChoosingView
import com.example.flashcards.views.cardViews.editCardViews.DeckEditView
import com.example.flashcards.views.deckViews.DeckView
import com.example.flashcards.views.MainView
import com.example.flashcards.model.uiModels.View
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.navigation
import com.example.flashcards.controller.updateDecksCardList
import com.example.flashcards.controller.viewModels.BasicCardViewModel
import com.example.flashcards.controller.viewModels.CardTypeViewModel
import com.example.flashcards.controller.viewModels.CardViewModel
import com.example.flashcards.controller.viewModels.deckViewsModels.DeckViewModel
import com.example.flashcards.controller.viewModels.CardDeckViewModel
import com.example.flashcards.controller.viewModels.HintCardViewModel
import com.example.flashcards.controller.viewModels.MultiChoiceCardViewModel
import com.example.flashcards.controller.viewModels.ThreeCardViewModel
import com.example.flashcards.model.uiModels.Fields
import com.example.flashcards.model.uiModels.Preferences
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.model.uiModels.BasicCardUiState
import com.example.flashcards.model.uiModels.HintCardUiState
import com.example.flashcards.model.uiModels.MultiChoiceUiCardState
import com.example.flashcards.model.uiModels.ThreeCardUiState
import com.example.flashcards.ui.theme.ColorSchemeClass
import com.example.flashcards.views.GeneralSettings
import com.example.flashcards.views.deckViews.EditDeckView
import com.example.flashcards.views.cardViews.editCardViews.EditingCardView
import com.example.flashcards.ui.theme.GetModifier
import kotlinx.coroutines.launch


data class AllViewModels(
    val basicCardViewModel: BasicCardViewModel,
    val hintCardViewModel: HintCardViewModel,
    val threeCardViewModel: ThreeCardViewModel,
    val multiChoiceCardViewModel: MultiChoiceCardViewModel
)

data class AllTypesUiStates(
    val basicCardUiState: BasicCardUiState,
    val hintUiStates: HintCardUiState,
    val threeCardUiState: ThreeCardUiState,
    val multiChoiceUiCardState: MultiChoiceUiCardState,
)

@Composable
fun AppNavHost(
    navController: NavHostController,
    deckViewModel: DeckViewModel,
    cardViewModel: CardViewModel,
    dueCardsViewModel: CardDeckViewModel,
    cardTypes: AllViewModels,
    cardTypeViewModel: CardTypeViewModel,
    modifier: Modifier = Modifier,
    preferences: Preferences
) {
    val deckUiState by deckViewModel.deckUiState.collectAsState()
    val basicCardUiState by
    cardTypes.basicCardViewModel.basicCardUiState.collectAsState()
    val hintCardUiState by
    cardTypes.hintCardViewModel.hintCardUiState.collectAsState()
    val threeCardUiState by
    cardTypes.threeCardViewModel.threeCardUiState.collectAsState()
    val multiCardUiState by
    cardTypes.multiChoiceCardViewModel.multiChoiceUiState.collectAsState()
    val allTypesUiStates =
        AllTypesUiStates(
            basicCardUiState,
            hintCardUiState,
            threeCardUiState,
            multiCardUiState
        )

    val cardList by cardTypeViewModel.cardListUiState.collectAsState()


    val fields = remember { Fields() }
    val listState = rememberLazyListState()
    val colorScheme = remember { ColorSchemeClass() }
    val getModifier = remember { GetModifier(colorScheme) }
    colorScheme.colorScheme = MaterialTheme.colorScheme
    val view = remember { View() }
    val selectedCard: MutableState<Card?> = remember { mutableStateOf(null) }

    val choosingView = ChoosingView()
    val cardDeckView = CardDeckView(
        dueCardsViewModel, cardTypeViewModel,
        getModifier
    )
    val editDeckView = EditDeckView(deckViewModel, fields, getModifier)
    val deckEditView = remember {
        DeckEditView(
            cardViewModel,
            cardTypeViewModel,
            cardTypes, fields, listState,
            selectedCard, getModifier
        )
    }
    val editingCardView = EditingCardView(
        cardViewModel, cardTypes, cardTypeViewModel,
        allTypesUiStates, getModifier
    )
    val mainView = MainView(getModifier, fields)
    val addDeckView = AddDeckView(deckViewModel, getModifier)
    val deckView = DeckView(
        cardTypeViewModel, fields,
        dueCardsViewModel, getModifier
    )
    val addCardView = AddCardView(fields, cardTypes, getModifier)
    val generalSettings = GeneralSettings(getModifier, preferences)

    val coroutineScope = rememberCoroutineScope()

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
                deckViewModel,
                // In DeckList Composable
                onNavigateToDeck = { deckId ->
                    navController.navigate(DeckOptionsDestination.createRoute(deckId))
                },
                onNavigateToAddDeck = {
                    navController.navigate(AddDeckDestination.route)
                },
                onNavigateToSettings = {
                    navController.navigate(SettingsDestination.route)
                }
            )
        }
        composable(SettingsDestination.route) {
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
        composable(AddDeckDestination.route) {
            BackHandler {
                view.whichView.intValue = 0
                view.onView.value = false
                fields.mainClicked.value = false
                navController.popBackStack(
                    DeckListDestination.route,
                    inclusive = false
                )
            }
            addDeckView.AddDeck(
                onNavigate = {
                    view.whichView.intValue = 0
                    view.onView.value = false
                    fields.mainClicked.value = false
                    navController.navigate(DeckListDestination.route)
                }
            )
        }
        navigation(
            route = DeckOptionsDestination.route,
            startDestination = DeckViewDestination.route,
            arguments = listOf(navArgument("deckId") { type = NavType.IntType })
        ) {
            composable(
                route = DeckViewDestination.route,
                arguments = listOf(navArgument("deckId") { type = NavType.IntType })
            ) { backStackEntry ->
                val deckId = backStackEntry.arguments?.getInt("deckId")
                val deck = deckUiState.deckList.find { it.id == deckId }

                LaunchedEffect(Unit) {
                    coroutineScope.launch {
                        dueCardsViewModel.getDueCards(deckId ?: 0, cardTypeViewModel).also {
                            cardTypeViewModel.updateBackupList()
                        }
                    }
                }
                BackHandler {
                    view.whichView.intValue = 0
                    view.onView.value = false
                    fields.mainClicked.value = false
                    navController.popBackStack(
                        DeckListDestination.route,
                        inclusive = false
                    )
                }
                deck?.let {
                    deckView.ViewEditDeck(
                        deck = it,
                        onNavigate = {
                            view.whichView.intValue = 0
                            view.onView.value = false
                            fields.mainClicked.value = false
                            navController.navigate(DeckListDestination.route)
                        },
                        whichView = view,
                        onNavigateToWhichView = {
                            navController.navigate(WhichViewDestination.createRoute(it.id))
                        }
                    )
                }
            }
            composable(
                route = WhichViewDestination.route
            ) { backStackEntry ->
                val deckId = backStackEntry.arguments?.getString("deckId")!!.toIntOrNull()
                //val deck = uiState.deckList.find { it.id == deckId }
                val deck = remember { mutableStateOf<Deck?>(null) }

                LaunchedEffect(Unit) {
                    coroutineScope.launch {
                        deckViewModel.getDeckById(deckId ?: 0, cardTypes).collect { flow ->
                            deck.value = flow
                        }
                    }
                }
                deck.value?.let { deck ->
                    choosingView.WhichScreen(
                        deck = deck,
                        view = view,
                        goToAddCard = {
                            fields.mainClicked.value = false
                            navController.navigate(AddCardDestination.createRoute(deck.id))
                            view.onView.value = true
                        },
                        goToViewCard = {
                            fields.mainClicked.value = false
                            navController.navigate(ViewCardDestination.createRoute(deck.id))
                            view.onView.value = true
                        },
                        goToEditDeck = { id, name ->
                            fields.mainClicked.value = false
                            navController.navigate(
                                EditDeckDestination.createRoute(
                                    deck.id,
                                    deck.name
                                )
                            )
                            view.onView.value = true
                        },
                        goToViewCards = {
                            fields.mainClicked.value = false
                            navController.navigate(ViewAllCardsDestination.createRoute(deck.id))
                            view.onView.value = true
                        },
                    )
                }
            }
            composable(AddCardDestination.route) { backStackEntry ->
                val deckId = backStackEntry.arguments?.getString("deckId")!!.toIntOrNull()

                val deck = remember { mutableStateOf<Deck?>(null) }

                LaunchedEffect(Unit) {
                    coroutineScope.launch {
                        deckViewModel.getDeckById(deckId ?: 0, cardTypes).collect { flow ->
                            deck.value = flow
                        }
                    }
                }
                BackHandler {
                    view.whichView.intValue = 0
                    view.onView.value = false
                    fields.inDeckClicked.value = false
                    fields.resetFields()
                    navController.popBackStack(
                        DeckViewDestination.createRoute(deckId ?: 0),
                        inclusive = false
                    )
                }
                // Pass the deckId to AddCard composable
                deck.value?.let {
                    addCardView.AddCard(
                        deck = it,
                        onNavigate = {
                            view.whichView.intValue = 0
                            view.onView.value = false
                            fields.inDeckClicked.value = false
                            fields.resetFields()
                            navController.navigate(DeckViewDestination.createRoute(deckId ?: 0))
                        }
                    )
                }
            }
            composable(ViewCardDestination.route) { backStackEntry ->
                val deckId = backStackEntry.arguments?.getString("deckId")!!.toIntOrNull()

                val deck = remember { mutableStateOf<Deck?>(null) }

                LaunchedEffect(Unit) {
                    coroutineScope.launch {
                        deckViewModel.getDeckById(deckId ?: 0, cardTypes).collect { flow ->
                            deck.value = flow
                        }
                    }
                }

                BackHandler {
                    view.whichView.intValue = 0
                    view.onView.value = false
                    fields.inDeckClicked.value = false
                    navController.popBackStack(
                        DeckViewDestination.createRoute(deckId ?: 0),
                        inclusive = false
                    )
                    deck.value?.let {
                        coroutineScope.launch {
                            /** This function also gets the due cards */
                            updateDecksCardList(
                                it,
                                cardList.allCards.map { cardTypes ->
                                    cardTypes.card
                                },
                                dueCardsViewModel,
                                cardTypeViewModel
                            )
                        }
                    }
                }
                // Use your ViewCard composable here
                deck.value?.let {
                    cardDeckView.ViewCard(
                        deck = it,
                        onNavigate = {
                            view.whichView.intValue = 0
                            view.onView.value = false
                            fields.inDeckClicked.value = false
                            navController.navigate(DeckViewDestination.createRoute(deckId ?: 0))
                            coroutineScope.launch {
                                /** This function also gets the due cards */
                                updateDecksCardList(
                                    it,
                                    cardList.allCards.map { cardTypes ->
                                        cardTypes.card
                                    },
                                    dueCardsViewModel,
                                    cardTypeViewModel
                                )
                            }
                        }
                    )
                }
            }

            composable(EditDeckDestination.route) { backStackEntry ->
                val deckId = backStackEntry.arguments?.getString("deckId")!!.toIntOrNull()
                val currentName = backStackEntry.arguments?.getString("currentName")
                val deck = remember { mutableStateOf<Deck?>(null) }

                LaunchedEffect(Unit) {
                    coroutineScope.launch {
                        deckViewModel.getDeckById(deckId ?: 0, cardTypes).collect { flow ->
                            deck.value = flow
                        }
                    }
                }
                BackHandler {
                    view.whichView.intValue = 0
                    view.onView.value = false
                    fields.inDeckClicked.value = false
                    navController.popBackStack(
                        DeckViewDestination.createRoute(deckId ?: 0),
                        inclusive = false
                    )
                }
                deck.value?.let {
                    editDeckView.EditDeck(
                        currentName = currentName ?: "",
                        deck = it,
                        onNavigate = {
                            view.whichView.intValue = 0
                            view.onView.value = false
                            fields.inDeckClicked.value = false
                            navController.navigate(DeckViewDestination.createRoute(deckId ?: 0))
                        },
                        onDelete = {
                            view.whichView.intValue = 0
                            view.onView.value = false
                            fields.inDeckClicked.value = false
                            navController.navigate(DeckListDestination.route)
                        }
                    )
                }
            }

            composable(ViewAllCardsDestination.route) { backStackEntry ->
                val deckId = backStackEntry.arguments?.getString("deckId")!!.toIntOrNull()
                //val deck = uiState.deckList.find { it.id == deckId }
                val deck = remember { mutableStateOf<Deck?>(null) }

                LaunchedEffect(Unit) {
                    coroutineScope.launch {
                        deckViewModel.getDeckById(deckId ?: 0, cardTypes).collect { flow ->
                            deck.value = flow
                        }
                    }
                }

                BackHandler {
                    view.whichView.intValue = 0
                    view.onView.value = false
                    fields.scrollPosition.value = 0
                    fields.inDeckClicked.value = false
                    navController.popBackStack(
                        DeckViewDestination.createRoute(deckId ?: 0),
                        inclusive = false
                    )
                }
                deck.value?.let {
                    deckEditView.ViewFlashCards(
                        deck = it,
                        onNavigate = {
                            fields.scrollPosition.value = 0
                            view.whichView.intValue = 0
                            fields.inDeckClicked.value = false
                            navController.navigate(DeckViewDestination.createRoute(deckId ?: 0))
                        },
                        goToEditCard = {
                            navController.navigate(
                                EditingCardDestination.createRoute(it.id)
                            )
                        }
                    )
                }
            }
            composable(
                route = EditingCardDestination.route,
                arguments = listOf(navArgument("deckId") { type = NavType.IntType })
            ) { backStackEntry ->
                //val cardId = backStackEntry.arguments?.getString("cardId")!!.toIntOrNull()
                //val deck = uiState.deckList.find { it.id == deckId }
                val deckId = backStackEntry.arguments?.getInt("deckId")

                BackHandler {
                    selectedCard.value = null
                    view.whichView.intValue = 0
                    deckEditView.isEditing.value = false
                    fields.resetFields()
                    navController.popBackStack(
                        ViewAllCardsDestination.createRoute(deckId ?: 0),
                        inclusive = false
                    )
                }
                selectedCard.value?.let {
                    editingCardView.EditFlashCardView(
                        card = it,
                        fields = fields,
                        selectedCard = selectedCard,
                        onNavigateBack = {
                            selectedCard.value = null
                            view.whichView.intValue = 0
                            deckEditView.isEditing.value = false
                            fields.resetFields()
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

/* For future reference
composable(
            route = "EditingBasicCard/{cardId}/{deckId}",
            arguments = listOf(navArgument("cardId") { type = NavType.IntType },
                navArgument("deckId"){ type = NavType.IntType})
        ) { backStackEntry ->
            //val cardId = backStackEntry.arguments?.getString("cardId")!!.toIntOrNull()
            //val deck = uiState.deckList.find { it.id == deckId }

            deckEditView.navigate.value = true
            val deckId = backStackEntry.arguments?.getInt("deckId")
            //val deck = uiState.deckList.find { it.id == deckId }
            val cardId = backStackEntry.arguments?.getInt("cardId")
            val coroutineScope = rememberCoroutineScope()
            //var deckWithCards = remember { mutableStateOf(DeckWithCards(
            //Deck(0, "Loading..."), emptyList())) }
            val nullCard = remember { mutableStateOf(BasicCardType(Card(0, 0, Calendar.getInstance().time, 0, false, 0, ""), BasicCard(0, "", ""))) }
            val basicCard = cardListUiState.allCards.find { it.basicCard?.cardId == cardId }

           // val card = deckWithCards.value.cards.find { it.id == cardId }


            BackHandler {
                view.whichView.intValue = 0
                deckEditView.navigate.value = false
                deckEditView.isEditing.value = false
                deckEditView.selectedCard.value = null
                navController.popBackStack("ViewFlashCards/$deckId",inclusive = false)
            }
            basicCard?.let {
                EditBasicCard(
                    basicCard = basicCard.basicCard?: nullCard.value.basicCard,
                    onDismiss = {
                        view.whichView.intValue = 0
                        deckEditView.navigate.value = false
                        deckEditView.isEditing.value = false
                        deckEditView.selectedCard.value = null
                        navController.navigate("ViewFlashCards/$deckId")
                    },
                    basicCardViewModel,
                    fields
                )
            }
            //}
        }
        composable(
            route = "EditingThreeCard/{cardId}/{deckId}",
            arguments = listOf(navArgument("cardId") { type = NavType.IntType },
                navArgument("deckId"){ type = NavType.IntType})
        ) { backStackEntry ->
            //val cardId = backStackEntry.arguments?.getString("cardId")!!.toIntOrNull()
            //val deck = uiState.deckList.find { it.id == deckId }

            deckEditView.navigate.value = true
            val deckId = backStackEntry.arguments?.getInt("deckId")
            //val deck = uiState.deckList.find { it.id == deckId }
            val cardId = backStackEntry.arguments?.getInt("cardId")
            val coroutineScope = rememberCoroutineScope()
            //var deckWithCards = remember { mutableStateOf(DeckWithCards(
            //Deck(0, "Loading..."), emptyList())) }
            val nullCard = remember { mutableStateOf(ThreeCardType(
                Card(0, 0, Calendar.getInstance().time, 0, false, 0, ""),
                ThreeFieldCard(0, "", "",""))) }
            val threeCard = cardListUiState.allCards.find { it.threeFieldCard?.cardId == cardId }

            // val card = deckWithCards.value.cards.find { it.id == cardId }


            BackHandler {
                view.whichView.intValue = 0
                deckEditView.navigate.value = false
                deckEditView.isEditing.value = false
                deckEditView.selectedCard.value = null
                navController.popBackStack("ViewFlashCards/$deckId",inclusive = false)
            }
            threeCard?.let {
            EditThreeCard(
                threeCard = threeCard.threeFieldCard?: nullCard.value.threeFieldCard,
                onDismiss = {
                    view.whichView.intValue = 0
                    deckEditView.navigate.value = false
                    deckEditView.isEditing.value = false
                    deckEditView.selectedCard.value = null
                    navController.navigate("ViewFlashCards/$deckId")},
                threeCardViewModel,
                fields
            )
            }
        }

        composable(
            route = "EditingHintCard/{cardId}/{deckId}",
            arguments = listOf(navArgument("cardId") { type = NavType.IntType },
                navArgument("deckId"){ type = NavType.IntType})
        ) { backStackEntry ->
            //val cardId = backStackEntry.arguments?.getString("cardId")!!.toIntOrNull()
            //val deck = uiState.deckList.find { it.id == deckId }

            deckEditView.navigate.value = true
            val deckId = backStackEntry.arguments?.getInt("deckId")
            //val deck = uiState.deckList.find { it.id == deckId }
            val cardId = backStackEntry.arguments?.getInt("cardId")
            val coroutineScope = rememberCoroutineScope()
            //var deckWithCards = remember { mutableStateOf(DeckWithCards(
            //Deck(0, "Loading..."), emptyList())) }
            val nullCard = remember { mutableStateOf(HintCardType(Card(0, 0, Calendar.getInstance().time, 0, false, 0, ""),
                HintCard(0, "", "",""))) }
            val hintCard = cardListUiState.allCards.find { it.hintCard?.cardId == cardId }


            // val card = deckWithCards.value.cards.find { it.id == cardId }


            BackHandler {
                view.whichView.intValue = 0
                deckEditView.navigate.value = false
                deckEditView.isEditing.value = false
                deckEditView.selectedCard.value = null
                navController.popBackStack("ViewFlashCards/$deckId",inclusive = false)
            }
            hintCard?.let {
            EditHintCard(
                hintCard = hintCard.hintCard?: nullCard.value.hintCard,
                onDismiss = {
                    view.whichView.intValue = 0
                    deckEditView.navigate.value = false
                    deckEditView.isEditing.value = false
                    deckEditView.selectedCard.value = null
                    navController.navigate("ViewFlashCards/$deckId")},
                hintCardViewModel,
                fields
            )
            }
        }
 */