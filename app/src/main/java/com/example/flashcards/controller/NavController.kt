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
import com.example.flashcards.views.addCardViews.AddCardView
import com.example.flashcards.views.deckViews.AddDeckView
import com.example.flashcards.views.cardViews.CardDeckView
import com.example.flashcards.views.miscFunctions.ChoosingView
import com.example.flashcards.views.editCardViews.DeckEditView
import com.example.flashcards.views.deckViews.DeckView
import com.example.flashcards.views.deckViews.EditDeckName
import com.example.flashcards.views.MainView
import com.example.flashcards.views.miscFunctions.View
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import com.example.flashcards.controller.viewModels.BasicCardViewModel
import com.example.flashcards.controller.viewModels.CardTypeViewModel
import com.example.flashcards.controller.viewModels.CardViewModel
import com.example.flashcards.controller.viewModels.DeckViewModel
import com.example.flashcards.controller.viewModels.HintCardViewModel
import com.example.flashcards.controller.viewModels.ThreeCardViewModel
import com.example.flashcards.model.Fields
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.views.editCardViews.EditingCardView
import kotlinx.coroutines.launch


@Composable
fun AppNavHost(
    navController : NavHostController,
    deckViewModel: DeckViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by deckViewModel.mainUiState.collectAsState()
    val fields = remember { Fields() }
    val basicCardViewModel : BasicCardViewModel =
        viewModel(factory = AppViewModelProvider.Factory)
    val threeCardViewModel : ThreeCardViewModel =
        viewModel(factory = AppViewModelProvider.Factory)
    val hintCardViewModel : HintCardViewModel =
        viewModel(factory = AppViewModelProvider.Factory)
    val cardTypeViewModel : CardTypeViewModel =
        viewModel(factory = AppViewModelProvider.Factory)
    val cardViewModel : CardViewModel =
        viewModel(factory = AppViewModelProvider.Factory)

    val cardListUiState by cardTypeViewModel.cardListUiState.collectAsState()

    val cardTypes : Triple<BasicCardViewModel, ThreeCardViewModel, HintCardViewModel> =
        Triple(basicCardViewModel, threeCardViewModel, hintCardViewModel)
    val view = remember { View() }

    val choosingView = ChoosingView(navController)
    val cardDeckView = CardDeckView(cardViewModel,cardTypeViewModel)
    val editDeckName = EditDeckName(deckViewModel)
    val deckEditView = remember {DeckEditView(cardViewModel, navController,
        cardTypeViewModel)}
    val editingCardView = EditingCardView(cardTypes)
    val mainView = MainView()
    val addDeckView = AddDeckView(deckViewModel)
    val deckView = DeckView(deckViewModel, navController,cardTypeViewModel)
    val addCardView = AddCardView(fields,cardTypes)
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
                    navController.navigate("DeckView/$deckId")
                    println("deckId : $deckId")
                }
                ,
                onNavigateToAddDeck = {navController.navigate("AddDeck")}
            )
        }
        composable("AddDeck"){
            BackHandler {
                view.whichView.intValue = 0
                view.onView.value = false
                navController.popBackStack("DeckList", inclusive = false)
            }
            addDeckView.AddDeck (
                onNavigate = {
                    view.whichView.intValue = 0
                    view.onView.value = false
                    navController.navigate("DeckList")
                }
            )
        }
        composable(
            route = "DeckView/{deckId}",
            arguments = listOf(navArgument("deckId") { type = NavType.IntType })
        ) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getInt("deckId")
            val deck = uiState.deckList.find { it.id == deckId }
            BackHandler {
                view.whichView.intValue = 0
                view.onView.value = false
                navController.popBackStack("DeckList", inclusive = false)
            }

            deck?.let {
                deckView.ViewEditDeck(
                    deck = it,
                    onNavigate = {view.whichView.intValue = 0
                        view.onView.value = false
                        navController.navigate("DeckList") },
                    whichView = view
                )
            }
        }
        composable(
            route = "WhichView/{deckId}",
            arguments = listOf(navArgument("deckId") { type = NavType.IntType })){
            backStackEntry ->
            val deckId = backStackEntry.arguments?.getInt("deckId")
            val deck = uiState.deckList.find { it.id == deckId }

            deck?.let {
                choosingView.WhichScreen(
                    deck = it,
                    view = view
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
            BackHandler {
                view.whichView.intValue = 0
                view.onView.value = false
                navController.popBackStack("DeckView/$deckId", inclusive = false)
            }
            // Use your ViewCard composable here
            cardDeckView.ViewCard(
                deckId = deckId ?: 0,
                onNavigate = {
                    view.whichView.intValue = 0
                    view.onView.value = false
                    navController.navigate("DeckView/$deckId")
                }
            )
        }

        composable("ChangeDeckName/{deckId}/{currentName}") { backStackEntry ->
            val deckId = backStackEntry.arguments?.getString("deckId")!!.toIntOrNull()
            val currentName = backStackEntry.arguments?.getString("currentName")
            BackHandler {
                view.whichView.intValue = 0
                view.onView.value = false
                navController.popBackStack("DeckView/$deckId", inclusive = false)
            }
            editDeckName.ChangeDeckName(
                currentName = currentName ?: "",
                deckId = deckId ?: 0,
                onNavigate = {
                    view.whichView.intValue = 0
                    view.onView.value = false
                    navController.navigate("DeckView/$deckId") })
        }

        composable("ViewFlashCards/{deckId}") { backStackEntry ->
            val deckId = backStackEntry.arguments?.getString("deckId")!!.toIntOrNull()
            val deck = uiState.deckList.find { it.id == deckId }
            BackHandler {
                view.whichView.intValue = 0
                view.onView.value = false
                navController.popBackStack("DeckView/$deckId", inclusive = false)
            }
            deck?.let {
                deckEditView.ViewFlashCards(
                    deck = it,
                    onNavigate = {
                        view.whichView.intValue = 0
                        navController.navigate("DeckView/$deckId")
                    }

                )
            }
        }
        composable(
            route = "EditingCard/{cardId}/{deckId}",
            arguments = listOf(navArgument("cardId") { type = NavType.IntType },
                navArgument("deckId"){ type = NavType.IntType})
        ) { backStackEntry ->
            //val cardId = backStackEntry.arguments?.getString("cardId")!!.toIntOrNull()
            //val deck = uiState.deckList.find { it.id == deckId }
            deckEditView.navigate.value = true
            val deckId = backStackEntry.arguments?.getInt("deckId")
            val cardId = backStackEntry.arguments?.getInt("cardId")
            val coroutineScope = rememberCoroutineScope()
            val card = remember { mutableStateOf<Card?>(null) }
            LaunchedEffect(Unit) {
                coroutineScope.launch {
                    cardViewModel.getDeckWithCards(deckId?: 0, cardTypeViewModel).run {
                        deckEditView.selectedCard.value = cardListUiState.allCards.find {
                            it.card.id == cardId
                        }?.card
                        card.value = deckEditView.selectedCard.value
                    }
                }
            }
            BackHandler {
                navController.popBackStack("ViewFlashCards/$deckId",inclusive = false)
                view.whichView.intValue = 0
                deckEditView.navigate.value = false
                deckEditView.isEditing.value = false
                deckEditView.selectedCard.value = null
            }
            card.value?.let { card ->
                editingCardView.EditFlashCardView(
                    card = card,
                    fields = fields,
                    cardListUiState = cardListUiState,
                    selectedCard = deckEditView.selectedCard,
                    onDismiss = {
                        navController.navigate("ViewFlashCards/$deckId")
                        view.whichView.intValue = 0
                        deckEditView.navigate.value = false
                        deckEditView.isEditing.value = false
                        deckEditView.selectedCard.value = null
                    }
                )
            }
        }
    }
}
/*

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