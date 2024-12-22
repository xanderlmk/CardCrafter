package com.example.flashcards.controller

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.flashcards.views.miscFunctions.View
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.navigation
import com.example.flashcards.controller.viewModels.BasicCardViewModel
import com.example.flashcards.controller.viewModels.CardTypeViewModel
import com.example.flashcards.controller.viewModels.CardViewModel
import com.example.flashcards.controller.viewModels.DeckViewModel
import com.example.flashcards.controller.viewModels.HintCardViewModel
import com.example.flashcards.controller.viewModels.ThreeCardViewModel
import com.example.flashcards.model.Fields
import com.example.flashcards.model.Preferences
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.ui.theme.ColorSchemeClass
import com.example.flashcards.views.GeneralSettings
import com.example.flashcards.views.deckViews.EditDeckView
import com.example.flashcards.views.cardViews.editCardViews.EditingCardView
import com.example.flashcards.views.miscFunctions.GetModifier
import kotlinx.coroutines.launch


@Composable
fun AppNavHost(
    navController: NavHostController,
    deckViewModel: DeckViewModel,
    modifier: Modifier = Modifier,
    preferences: Preferences
) {
    val uiState by deckViewModel.deckUiState.collectAsState()
    val fields = remember { Fields() }
    val listState = rememberLazyListState()
    val colorScheme = remember { ColorSchemeClass() }
    val getModifier = GetModifier(colorScheme)
    colorScheme.colorScheme = MaterialTheme.colorScheme
    val basicCardViewModel: BasicCardViewModel =
        viewModel(factory = AppViewModelProvider.Factory)
    val threeCardViewModel: ThreeCardViewModel =
        viewModel(factory = AppViewModelProvider.Factory)
    val hintCardViewModel: HintCardViewModel =
        viewModel(factory = AppViewModelProvider.Factory)
    val cardTypeViewModel: CardTypeViewModel =
        viewModel(factory = AppViewModelProvider.Factory)
    val cardViewModel: CardViewModel =
        viewModel(factory = AppViewModelProvider.Factory)

    val cardTypes: Triple<BasicCardViewModel, ThreeCardViewModel, HintCardViewModel> =
        Triple(basicCardViewModel, threeCardViewModel, hintCardViewModel)
    val view = remember { View() }

    val choosingView = ChoosingView()
    val cardDeckView = CardDeckView(
        cardViewModel, cardTypeViewModel,
        getModifier
    )
    val editDeckView = EditDeckView(deckViewModel, fields, getModifier)
    val deckEditView = remember {
        DeckEditView(
            cardViewModel,
            cardTypeViewModel, fields,
            listState, getModifier
        )
    }
    val editingCardView = EditingCardView(
        cardTypes, cardTypeViewModel, getModifier
    )
    val mainView = MainView(getModifier, fields)
    val addDeckView = AddDeckView(deckViewModel, getModifier)
    val deckView = DeckView(cardTypeViewModel, fields, getModifier)
    val addCardView = AddCardView(fields, cardTypes, getModifier)
    val generalSettings = GeneralSettings(getModifier, preferences)

    val coroutineScope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = "DeckList",
        modifier = modifier
    ) {
        composable("DeckList") {
            BackHandler {
                // Exit the app when back is pressed on the main screen
                (navController.context as? Activity)?.finish()
            }
            mainView.DeckList(
                deckViewModel,
                // In DeckList Composable
                onNavigateToDeck = { deckId ->
                    navController.navigate("DeckOptions/$deckId")
                },
                onNavigateToAddDeck = { navController.navigate("AddDeck") },
                onNavigateToSettings = { navController.navigate("Settings") }
            )
        }
        composable("Settings") {
            BackHandler {
                fields.mainClicked.value = false
                navController.popBackStack("DeckList", inclusive = false)
            }
            generalSettings.SettingsView(
                onNavigate = {
                    fields.mainClicked.value = false
                    navController.navigate("DeckList")
                }
            )
        }
        composable("AddDeck") {
            BackHandler {
                view.whichView.intValue = 0
                view.onView.value = false
                fields.mainClicked.value = false
                navController.popBackStack("DeckList", inclusive = false)
            }
            addDeckView.AddDeck(
                onNavigate = {
                    view.whichView.intValue = 0
                    view.onView.value = false
                    fields.mainClicked.value = false
                    navController.navigate("DeckList")
                }
            )
        }
        navigation(
            route = "DeckOptions/{deckId}",
            startDestination = "DeckView/{deckId}",
            arguments = listOf(navArgument("deckId") { type = NavType.IntType })
        ) {

            composable(
                route = "DeckView/{deckId}",
                arguments = listOf(navArgument("deckId") { type = NavType.IntType })
            ) { backStackEntry ->
                val deckId = backStackEntry.arguments?.getInt("deckId")
                val deck = uiState.deckList.find { it.id == deckId }
                BackHandler {
                    view.whichView.intValue = 0
                    view.onView.value = false
                    fields.mainClicked.value = false
                    navController.popBackStack("DeckList", inclusive = false)
                }

                deck?.let {
                    deckView.ViewEditDeck(
                        deck = it,
                        onNavigate = {
                            view.whichView.intValue = 0
                            view.onView.value = false
                            fields.mainClicked.value = false
                            navController.navigate("DeckList")
                        },
                        whichView = view,
                        onNavigateToWhichView = {
                            navController.navigate("WhichView/${it.id}")
                        }
                    )
                }
            }
            composable(
                route = "WhichView/{deckId}"
            ) { backStackEntry ->
                val deckId = backStackEntry.arguments?.getString("deckId")!!.toIntOrNull()
                //val deck = uiState.deckList.find { it.id == deckId }
                val deck = remember { mutableStateOf<Deck?>(null) }

                LaunchedEffect(Unit) {
                    coroutineScope.launch {
                        deckViewModel.getDeckById(deckId ?: 0).collect { flow ->
                            deck.value = flow
                        }
                    }
                }
                deck.value?.let { deck ->
                    choosingView.WhichScreen(
                        deck = deck,
                        view = view,
                        goToAddCard = {
                            fields.inDeckClicked.value = false
                            navController.navigate("AddCard/${deck.id}")
                            view.onView.value = true
                        },
                        goToViewCard = {
                            fields.inDeckClicked.value = false
                            navController.navigate("ViewCard/${deck.id}")
                            view.onView.value = true
                        },
                        goToEditDeck = { id, name ->
                            fields.inDeckClicked.value = false
                            navController.navigate("EditDeck/${deck.id}/${deck.name}")
                            view.onView.value = true
                        },
                        goToViewCards = {
                            fields.inDeckClicked.value = false
                            navController.navigate("ViewFlashCards/${deck.id}")
                            view.onView.value = true
                        },
                    )
                }
            }
            composable("AddCard/{deckId}") { backStackEntry ->
                val deckId = backStackEntry.arguments?.getString("deckId")!!.toIntOrNull()
                fields.answer.value = ""
                fields.middleField.value = ""
                fields.question.value = ""
                BackHandler {
                    view.whichView.intValue = 0
                    view.onView.value = false
                    fields.answer.value = ""
                    fields.middleField.value = ""
                    fields.answer.value = ""
                    navController.popBackStack("DeckView/$deckId", inclusive = false)
                }
                // Pass the deckId to AddCard composable
                addCardView.AddCard(
                    deckId = deckId ?: 0,
                    onNavigate = {
                        view.whichView.intValue = 0
                        view.onView.value = false
                        fields.answer.value = ""
                        fields.middleField.value = ""
                        fields.answer.value = ""
                        navController.navigate("DeckView/$deckId")
                    }
                )
            }
            composable("ViewCard/{deckId}") { backStackEntry ->
                val deckId = backStackEntry.arguments?.getString("deckId")!!.toIntOrNull()

                val deck = remember { mutableStateOf<Deck?>(null) }

                LaunchedEffect(Unit) {
                    coroutineScope.launch {
                        deckViewModel.getDeckById(deckId ?: 0).collect { flow ->
                            deck.value = flow
                        }
                    }
                }

                BackHandler {
                    view.whichView.intValue = 0
                    view.onView.value = false
                    navController.popBackStack("DeckView/$deckId", inclusive = false)
                }
                // Use your ViewCard composable here
                deck.value?.let {
                    cardDeckView.ViewCard(
                        deck = it,
                        onNavigate = {
                            view.whichView.intValue = 0
                            view.onView.value = false
                            navController.navigate("DeckView/$deckId")
                        }
                    )
                }
            }

            composable("EditDeck/{deckId}/{currentName}") { backStackEntry ->
                val deckId = backStackEntry.arguments?.getString("deckId")!!.toIntOrNull()
                val currentName = backStackEntry.arguments?.getString("currentName")
                val deck = remember { mutableStateOf<Deck?>(null) }

                LaunchedEffect(Unit) {
                    coroutineScope.launch {
                        deckViewModel.getDeckById(deckId ?: 0).collect { flow ->
                            deck.value = flow
                        }
                    }
                }
                BackHandler {
                    view.whichView.intValue = 0
                    view.onView.value = false
                    navController.popBackStack("DeckView/$deckId", inclusive = true)
                }
                deck.value?.let {
                    editDeckView.EditDeck(
                        currentName = currentName ?: "",
                        deck = it,
                        onNavigate = {
                            view.whichView.intValue = 0
                            view.onView.value = false
                            navController.navigate("DeckView/$deckId")
                        },
                        onDelete = {
                            view.whichView.intValue = 0
                            view.onView.value = false
                            navController.navigate("DeckList")
                        }
                    )
                }
            }

            composable("ViewFlashCards/{deckId}") { backStackEntry ->
                val deckId = backStackEntry.arguments?.getString("deckId")!!.toIntOrNull()
                //val deck = uiState.deckList.find { it.id == deckId }
                val deck = remember { mutableStateOf<Deck?>(null) }

                LaunchedEffect(Unit) {
                    coroutineScope.launch {
                        deckViewModel.getDeckById(deckId ?: 0).collect { flow ->
                            deck.value = flow
                        }
                    }
                }

                BackHandler {
                    view.whichView.intValue = 0
                    view.onView.value = false
                    fields.scrollPosition.value = 0
                    navController.popBackStack("DeckView/$deckId", inclusive = false)
                }
                deck.value?.let {
                    deckEditView.ViewFlashCards(
                        deck = it,
                        onNavigate = {
                            fields.scrollPosition.value = 0
                            view.whichView.intValue = 0
                            navController.navigate("DeckView/$deckId")
                        },
                        goToEditCard = { selectedCardId ->
                            navController.navigate("EditingCard/${selectedCardId}/${it.id}")
                        }
                    )
                }
            }
            composable(
                route = "EditingCard/{cardId}/{deckId}",
                arguments = listOf(navArgument("cardId") { type = NavType.IntType },
                    navArgument("deckId") { type = NavType.IntType })
            ) { backStackEntry ->
                //val cardId = backStackEntry.arguments?.getString("cardId")!!.toIntOrNull()
                //val deck = uiState.deckList.find { it.id == deckId }
                deckEditView.navigate.value = true
                val deckId = backStackEntry.arguments?.getInt("deckId")
                val cardId = backStackEntry.arguments?.getInt("cardId")
                val card = remember { mutableStateOf<Card?>(null) }
                LaunchedEffect(Unit) {
                    coroutineScope.launch {
                        card.value = cardViewModel.getCardById(cardId ?: 0)
                        deckEditView.selectedCard.value = card.value
                    }
                }
                BackHandler {
                    deckEditView.selectedCard.value = null
                    view.whichView.intValue = 0
                    deckEditView.navigate.value = false
                    deckEditView.isEditing.value = false
                    navController.popBackStack("ViewFlashCards/$deckId", inclusive = false)
                }
                card.value?.let {
                    editingCardView.EditFlashCardView(
                        card = it,
                        fields = fields,
                        selectedCard = deckEditView.selectedCard,
                        onNavigateBack = {
                            deckEditView.selectedCard.value = null
                            view.whichView.intValue = 0
                            deckEditView.navigate.value = false
                            deckEditView.isEditing.value = false
                            navController.navigate("ViewFlashCards/$deckId")
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