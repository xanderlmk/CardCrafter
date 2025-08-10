package com.belmontCrest.cardCrafter.navigation.navHosts

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.belmontCrest.cardCrafter.model.application.AppVMProvider
import com.belmontCrest.cardCrafter.navigation.destinations.AddDeckDestination
import com.belmontCrest.cardCrafter.navigation.destinations.SBNavDestination
import com.belmontCrest.cardCrafter.navigation.drawer.CustomNavigationDrawer
import com.belmontCrest.cardCrafter.navigation.destinations.DeckListDestination
import com.belmontCrest.cardCrafter.navigation.destinations.DeckNavDestination
import com.belmontCrest.cardCrafter.navigation.destinations.DeckViewDestination
import com.belmontCrest.cardCrafter.navigation.destinations.MainNavDestination
import com.belmontCrest.cardCrafter.navigation.NavViewModel
import com.belmontCrest.cardCrafter.navigation.destinations.SettingsDestination
import com.belmontCrest.cardCrafter.navigation.destinations.SupabaseDestination
import com.belmontCrest.cardCrafter.navigation.destinations.ViewDueCardsDestination
import com.belmontCrest.cardCrafter.controller.view.models.cardViewsModels.EditingCardListViewModel
import com.belmontCrest.cardCrafter.controller.view.models.deckViewsModels.MainViewModel
import com.belmontCrest.cardCrafter.model.ui.Fields
import com.belmontCrest.cardCrafter.model.application.PreferencesManager
import com.belmontCrest.cardCrafter.model.application.setPreferenceValues
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.SupabaseViewModel
import com.belmontCrest.cardCrafter.uiFunctions.showToastMessage
import com.belmontCrest.cardCrafter.ui.theme.ColorSchemeClass
import com.belmontCrest.cardCrafter.views.mainViews.GeneralSettings
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun AppNavHost(
    mainNavController: NavHostController,
    navViewModel: NavViewModel, supabaseVM: SupabaseViewModel,
    fields: Fields, preferences: PreferencesManager,
) {
    val colorScheme = remember { ColorSchemeClass() }
    var onDeckView by remember { mutableStateOf(false) }
    colorScheme.colorScheme = MaterialTheme.colorScheme
    val pc = setPreferenceValues(preferences)
    val getUIStyle = rememberUpdatedState(
        GetUIStyle(colorScheme, pc.darkTheme, pc.dynamicTheme, pc.cuteTheme)
    ).value
    val mainView = MainView(getUIStyle, fields)
    val addDeckView = AddDeckView(getUIStyle)
    val generalSettings = GeneralSettings(getUIStyle, preferences)
    val coroutineScope = rememberCoroutineScope()
    CustomNavigationDrawer(
        mainNavController = mainNavController, fields = fields,
        getUIStyle = getUIStyle, navVM = navViewModel, supabaseVM = supabaseVM
    ) {
        NavHost(
            navController = mainNavController,
            startDestination = DeckListDestination.route,
            route = MainNavDestination.route,
            modifier = Modifier
        ) {
            composable(DeckListDestination.route) {
                BackHandler {
                    // Exit the app when back is pressed on the main screen
                    (mainNavController.context as? Activity)?.finish()
                }
                val mainViewModel: MainViewModel = viewModel(factory = AppVMProvider.Factory)
                mainView.DeckList(
                    mainViewModel,
                    onNavigateToDeck = { id ->
                        coroutineScope.launch { navViewModel.getDeckById(id) }
                        coroutineScope.launch { navViewModel.clearSavedCards() }
                        fields.navigateToDeck()
                        onDeckView = true
                        navViewModel.updateStartingDeckRoute(DeckViewDestination.route)
                        navViewModel.updateRoute(DeckNavDestination.route)
                        mainNavController.navigate(DeckNavDestination.route)
                    },
                    onNavigateToAddDeck = {
                        navViewModel.updateRoute(AddDeckDestination.route)
                        mainNavController.navigate(AddDeckDestination.route)
                    },
                    onNavigateToSBDeckList = {
                        navViewModel.updateStartingSBRoute(SupabaseDestination.route)
                        navViewModel.updateRoute(SBNavDestination.route)
                        mainNavController.navigate(SBNavDestination.route)
                    },
                    goToDueCards = { id ->
                        coroutineScope.launch { navViewModel.getDeckById(id) }
                        coroutineScope.launch { navViewModel.clearSavedCards() }
                        fields.navigateToDueCards()
                        navViewModel.updateStartingDeckRoute(ViewDueCardsDestination.route)
                        navViewModel.updateRoute(ViewDueCardsDestination.route)
                        mainNavController.navigate(DeckNavDestination.route)
                    }
                )
            }
            /** Our Supabase Nav Controller to call*/
            composable(SBNavDestination.route) {
                val context = LocalContext.current
                LaunchedEffect(Unit) {
                    coroutineScope.launch {
                        supabaseVM.updateStatus()
                        supabaseVM.getOwner()
                    }
                    coroutineScope.launch {
                        val result = supabaseVM.getGoogleId()
                        if (!result.first) {
                            showToastMessage(context, result.second)
                        }
                    }
                }
                SupabaseNav(
                    fields, supabaseVM, getUIStyle, preferences, mainNavController, navViewModel
                )
            }
            composable(
                SettingsDestination.route,
                enterTransition = { null },
                exitTransition = { null }) {
                BackHandler {
                    fields.mainClicked.value = false
                    navViewModel.updateRoute(DeckListDestination.route)
                    mainNavController.popBackStack(
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
                    mainNavController.popBackStack(DeckListDestination.route, inclusive = false)
                }
                addDeckView.AddDeck(
                    onNavigate = {
                        fields.mainClicked.value = false
                        navViewModel.updateRoute(DeckListDestination.route)
                        mainNavController.navigate(DeckListDestination.route)
                    },
                    reviewAmount = pc.reviewAmount.toString(),
                    cardAmount = pc.cardAmount.toString()
                )
            }
            /** Our Deck Nav Controller to call*/
            composable(DeckNavDestination.route) {
                val editingCardListVM: EditingCardListViewModel =
                    viewModel(factory = AppVMProvider.Factory)
                DeckNavHost(
                    mainNavController, fields, onDeckView,
                    navViewModel, getUIStyle, editingCardListVM, preferences
                )
            }
        }
    }
}
