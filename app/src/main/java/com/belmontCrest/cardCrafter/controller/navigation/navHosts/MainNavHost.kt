package com.belmontCrest.cardCrafter.controller.navigation.navHosts

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.belmontCrest.cardCrafter.views.deckViews.AddDeckView
import com.belmontCrest.cardCrafter.views.mainViews.MainView
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.belmontCrest.cardCrafter.controller.AppViewModelProvider
import com.belmontCrest.cardCrafter.controller.navigation.destinations.AddDeckDestination
import com.belmontCrest.cardCrafter.controller.navigation.destinations.SBNavDestination
import com.belmontCrest.cardCrafter.controller.navigation.drawer.CustomNavigationDrawer
import com.belmontCrest.cardCrafter.controller.navigation.destinations.DeckListDestination
import com.belmontCrest.cardCrafter.controller.navigation.destinations.DeckNavDestination
import com.belmontCrest.cardCrafter.controller.navigation.destinations.DeckViewDestination
import com.belmontCrest.cardCrafter.controller.navigation.destinations.MainNavDestination
import com.belmontCrest.cardCrafter.controller.navigation.NavViewModel
import com.belmontCrest.cardCrafter.controller.navigation.destinations.SettingsDestination
import com.belmontCrest.cardCrafter.controller.navigation.destinations.ViewDueCardsDestination
import com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels.EditingCardListViewModel
import com.belmontCrest.cardCrafter.controller.viewModels.deckViewsModels.MainViewModel
import com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels.CardDeckViewModel
import com.belmontCrest.cardCrafter.model.uiModels.Fields
import com.belmontCrest.cardCrafter.model.uiModels.PreferencesManager
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.SupabaseViewModel
import com.belmontCrest.cardCrafter.ui.theme.ColorSchemeClass
import com.belmontCrest.cardCrafter.views.mainViews.GeneralSettings
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun AppNavHost(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    editingCardListVM: EditingCardListViewModel,
    navViewModel: NavViewModel,
    supabaseVM: SupabaseViewModel,
    fields: Fields,
    preferences: PreferencesManager,
) {
    val cardDeckVM: CardDeckViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val colorScheme = remember { ColorSchemeClass() }
    var onDeckView by remember { mutableStateOf(false) }
    colorScheme.colorScheme = MaterialTheme.colorScheme
    val getUIStyle = rememberUpdatedState(
        GetUIStyle(
            colorScheme,
            preferences.darkTheme.value,
            preferences.customScheme.value
        )
    ).value
    val mainView = MainView(getUIStyle, fields)
    val addDeckView = AddDeckView(getUIStyle)
    val generalSettings = GeneralSettings(getUIStyle, preferences)
    val coroutineScope = rememberCoroutineScope()
    CustomNavigationDrawer(
        navController = navController,
        fields = fields,
        getUIStyle = getUIStyle,
        navViewModel = navViewModel,
        cardDeckVM = cardDeckVM,
    ) {
        NavHost(
            navController = navController,
            startDestination = DeckListDestination.route,
            route = MainNavDestination.route,
            modifier = Modifier
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
                        coroutineScope.launch {
                            navViewModel.getDeckById(id)
                        }
                        coroutineScope.launch {
                            editingCardListVM.updateId(id)
                        }
                        fields.navigateToDeck()
                        onDeckView = true
                        navViewModel.updateStartingRoute(DeckViewDestination.route)
                        navViewModel.updateRoute(DeckNavDestination.route)
                        navController.navigate(DeckNavDestination.route)
                    },
                    onNavigateToAddDeck = {
                        navViewModel.updateRoute(AddDeckDestination.route)
                        navController.navigate(AddDeckDestination.route)
                    },
                    onNavigateToSBDeckList = {
                        coroutineScope.launch {
                            supabaseVM.updateStatus()
                            supabaseVM.getOwner()
                        }
                        coroutineScope.launch {
                            supabaseVM.getGoogleId()
                        }
                        navViewModel.updateRoute(SBNavDestination.route)
                        navController.navigate(SBNavDestination.route)
                    },
                    goToDueCards = { id ->
                        coroutineScope.launch {
                            navViewModel.getDeckById(id)
                        }
                        coroutineScope.launch {
                            editingCardListVM.updateId(id)
                        }
                        fields.navigateToDueCards()
                        cardDeckVM.updateWhichDeck(id)
                        navViewModel.updateStartingRoute(ViewDueCardsDestination.route)
                        navViewModel.updateRoute(ViewDueCardsDestination.route)
                        navController.navigate(DeckNavDestination.route)
                    }
                )
            }
            /** Our Supabase Nav Controller to call*/
            composable(SBNavDestination.route) {
                SupabaseNav(
                    fields, mainViewModel, supabaseVM, getUIStyle,
                    preferences, navController, navViewModel
                )
            }
            composable(
                SettingsDestination.route,
                enterTransition = { null },
                exitTransition = { null }) {
                BackHandler {
                    fields.mainClicked.value = false
                    navViewModel.updateRoute(DeckListDestination.route)
                    navController.popBackStack(
                        DeckListDestination.route, inclusive = false
                    )
                }
                generalSettings.SettingsView()
            }
            composable(
                AddDeckDestination.route,
                enterTransition = { null },
                exitTransition = { null }) {
                BackHandler {
                    fields.mainClicked.value = false
                    navViewModel.updateRoute(DeckListDestination.route)
                    navController.popBackStack(
                        DeckListDestination.route,
                        inclusive = false
                    )
                }
                addDeckView.AddDeck(
                    onNavigate = {
                        fields.mainClicked.value = false
                        navViewModel.updateRoute(DeckListDestination.route)
                        navController.navigate(DeckListDestination.route)
                    },
                    reviewAmount = preferences.reviewAmount.intValue.toString(),
                    cardAmount = preferences.cardAmount.intValue.toString()
                )
            }
            /** Our Deck Nav Controller to call*/
            composable(DeckNavDestination.route) {
                DeckNavHost(
                    navController, cardDeckVM, fields, onDeckView,
                    navViewModel, getUIStyle, editingCardListVM,
                )
            }
        }
    }
}
