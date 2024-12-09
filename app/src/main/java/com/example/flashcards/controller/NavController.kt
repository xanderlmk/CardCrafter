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
import com.example.flashcards.views.AddCardView
import com.example.flashcards.views.AddDeckView
import com.example.flashcards.views.CardDeckView
import com.example.flashcards.views.ChoosingView
import com.example.flashcards.views.DeckEditView
import com.example.flashcards.views.DeckView
import com.example.flashcards.views.EditDeckName
import com.example.flashcards.views.MainView
import com.example.flashcards.views.View
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import com.example.flashcards.model.Deck
import com.example.flashcards.model.DeckWithCards
import kotlinx.coroutines.launch


@Composable
fun AppNavHost(
    navController : NavHostController,
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val mainView = MainView()
    val addDeckView = AddDeckView(mainViewModel)
    val deckView = DeckView(mainViewModel, navController)
    val uiState by mainViewModel.mainUiState.collectAsState()
    val addCardView = AddCardView(mainViewModel)
    val view = remember { View() }
    val choosingView = ChoosingView(navController)
    val cardViewModel : CardViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val cardUiState by cardViewModel.cardUiState.collectAsState()
    val cardDeckView = CardDeckView(cardViewModel)
    val editDeckName = EditDeckName(mainViewModel)
    val deckEditView = remember {DeckEditView(cardViewModel, navController)}
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
                mainViewModel,
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
            BackHandler {
                view.whichView.intValue = 0
                view.onView.value = false
                navController.popBackStack("DeckView/$deckId", inclusive = false)
            }
            // Pass the deckId to AddCard composable
            addCardView.AddCard(
                deckId = deckId ?: 0,
                onNavigate = { view.whichView.intValue = 0
                    view.onView.value = false
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
            BackHandler {
                view.whichView.intValue = 0
                view.onView.value = false
                navController.popBackStack("DeckView/$deckId", inclusive = false)
            }
            deckEditView.ViewFlashCards(deckId = deckId ?: 0,
                onNavigate = { view.whichView.intValue = 0
                    navController.navigate("DeckView/$deckId") }

            )
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
            val deck = uiState.deckList.find { it.id == deckId }
            val cardId = backStackEntry.arguments?.getInt("cardId")
            val coroutineScope = rememberCoroutineScope()
            var deckWithCards = remember { mutableStateOf(DeckWithCards(
                Deck(0, "Loading..."), emptyList())) }
            LaunchedEffect(Unit) {
                coroutineScope.launch {
                    cardViewModel.getDeckWithCards(deck?.id ?: 0).collect { data ->
                        deckWithCards.value = data
                    }
                }
            }
            val card = deckWithCards.value.cards.find { it.id == cardId }
            BackHandler {
                view.whichView.intValue = 0
                deckEditView.navigate.value = false
                deckEditView.isEditing.value = false
                deckEditView.selectedCard.value = null
                navController.popBackStack("ViewFlashCards/$deckId",inclusive = false)
            }
            card?.let {
               deckEditView.EditFlashCardView(
                   card = it,
                   onDismiss = {
                       view.whichView.intValue = 0
                       deckEditView.navigate.value = false
                       deckEditView.isEditing.value = false
                       deckEditView.selectedCard.value = null
                       navController.navigate("ViewFlashCards/$deckId")}
               )
            }
        }
    }
}