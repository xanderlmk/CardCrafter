package com.example.flashcards.controller

import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.flashcards.model.Deck
import com.example.flashcards.views.AddCardView
import com.example.flashcards.views.AddDeckView
import com.example.flashcards.views.CardDeckView
import com.example.flashcards.views.ChoosingView
import com.example.flashcards.views.DeckEditView
import com.example.flashcards.views.DeckView
import com.example.flashcards.views.EditDeckName
import com.example.flashcards.views.MainView
import com.example.flashcards.views.View


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
    val cardDeckView = CardDeckView(cardViewModel)
    val editDeckName = EditDeckName(mainViewModel)
    val deckEditView = DeckEditView(cardViewModel)
    NavHost(
        navController = navController,
        startDestination = "DeckList",
        modifier = modifier
    ) {
        composable("DeckList") {
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
            addDeckView.AddDeck (
                onNavigate = {navController.navigate("DeckList")}
            )
        }
        composable(
            route = "DeckView/{deckId}",
            arguments = listOf(navArgument("deckId") { type = NavType.IntType })
        ) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getInt("deckId")
            val deck = uiState.deckList.find { it.id == deckId }

            deck?.let {
                deckView.ViewEditDeck(
                    deck = it,
                    onNavigate = { navController.navigate("DeckList") },
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

            // Pass the deckId to AddCard composable
            addCardView.AddCard(
                deckId = deckId ?: 0,
                onNavigate = { view.whichView.intValue = 0
                    navController.navigate("DeckView/$deckId")
                }
            )
        }
        composable("ViewCard/{deckId}") { backStackEntry ->
            val deckId = backStackEntry.arguments?.getString("deckId")!!.toIntOrNull()
            // Use your ViewCard composable here
            cardDeckView.ViewCard(
                deckId = deckId ?: 0,
                onNavigate = {
                    view.whichView.intValue = 0
                    navController.navigate("DeckView/$deckId")
                }
            )
        }

        composable("ChangeDeckName/{deckId}/{currentName}") { backStackEntry ->
            val deckId = backStackEntry.arguments?.getString("deckId")!!.toIntOrNull()
            val currentName = backStackEntry.arguments?.getString("currentName")
            editDeckName.ChangeDeckName(
                currentName = currentName ?: "",
                deckId = deckId ?: 0,
                onNavigate = { view.whichView.intValue = 0
                    navController.navigate("DeckView/$deckId") })
        }

        composable("ViewFlashCards/{deckId}") { backStackEntry ->
            val deckId = backStackEntry.arguments?.getString("deckId")!!.toIntOrNull()
            // Use your ViewFlashCards composable here
            deckEditView.ViewFlashCards(deckId = deckId ?: 0,
                onNavigate = { view.whichView.intValue = 0
                    navController.navigate("DeckView/$deckId") })
        }
    }
}