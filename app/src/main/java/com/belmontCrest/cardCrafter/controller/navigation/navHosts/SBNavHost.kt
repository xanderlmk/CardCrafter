package com.belmontCrest.cardCrafter.controller.navigation.navHosts

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.belmontCrest.cardCrafter.controller.navigation.destinations.SBNavDestination
import com.belmontCrest.cardCrafter.controller.navigation.destinations.MainNavDestination
import com.belmontCrest.cardCrafter.controller.navigation.NavViewModel
import com.belmontCrest.cardCrafter.controller.navigation.destinations.ExportSBDestination
import com.belmontCrest.cardCrafter.controller.navigation.destinations.ImportSBDestination
import com.belmontCrest.cardCrafter.controller.navigation.destinations.SupabaseDestination
import com.belmontCrest.cardCrafter.controller.navigation.destinations.UserEDDestination
import com.belmontCrest.cardCrafter.controller.navigation.destinations.UserProfileDestination
import com.belmontCrest.cardCrafter.controller.viewModels.deckViewsModels.MainViewModel
import com.belmontCrest.cardCrafter.controller.viewModels.deckViewsModels.updateCurrentTime
import com.belmontCrest.cardCrafter.model.uiModels.Fields
import com.belmontCrest.cardCrafter.model.uiModels.PreferencesManager
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.SupabaseViewModel
import com.belmontCrest.cardCrafter.supabase.view.UserExportedDecks
import com.belmontCrest.cardCrafter.supabase.view.profile.MyProfile
import com.belmontCrest.cardCrafter.supabase.view.importDeck.ImportDeck
import com.belmontCrest.cardCrafter.supabase.view.OnlineDatabase
import com.belmontCrest.cardCrafter.supabase.view.UploadThisDeck
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupabaseNav(
    fields: Fields, mainViewModel: MainViewModel,
    supabaseVM: SupabaseViewModel, getUIStyle: GetUIStyle,
    preferences: PreferencesManager, navController: NavHostController,
    navViewModel: NavViewModel
) {
    val sbNavController = rememberNavController()

    LaunchedEffect(Unit) {
        navViewModel.updateSBNav(sbNavController)
    }
    /** Our Supabase Client and Views. */
    val onlineDatabase = OnlineDatabase(
        getUIStyle,
        supabaseVM,
        mainViewModel.deckUiState.collectAsStateWithLifecycle().value.deckList,
    )
    val importDeck = ImportDeck(
        getUIStyle,
        preferences
    )
    val sbDeck by supabaseVM.deck.collectAsStateWithLifecycle()
    val pickedDeck by supabaseVM.pickedDeck.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    val startingNavRoute by navViewModel.startingSBRoute.collectAsStateWithLifecycle()
    val startDestination = if (startingNavRoute.name == SupabaseDestination.route) {
        SupabaseDestination.route
    } else {
        UserProfileDestination.route
    }
    NavHost(
        navController = sbNavController,
        startDestination = startDestination,
        route = SBNavDestination.route,
        modifier = Modifier
    ) {
        composable(
            SupabaseDestination.route,
            enterTransition = { null },
            exitTransition = { null },
            popEnterTransition = { null }) {
            BackHandler {
                navViewModel.updateRoute(MainNavDestination.route)
                BackNavHandler.returnToDeckListFromSB(
                    navController, updateCurrentTime(), fields
                )
            }
            onlineDatabase.SupabaseView(
                onImportDeck = { uuid ->
                    navViewModel.updateRoute(SupabaseDestination.route)
                    sbNavController.navigate(ImportSBDestination.route)
                    coroutineScope.launch {
                        supabaseVM.updateUUID(uuid)
                    }
                },
                onExportDeck = {
                    navViewModel.updateRoute(SupabaseDestination.route)
                    sbNavController.navigate(ExportSBDestination.route)
                },
            )
        }
        composable(ImportSBDestination.route) {
            BackHandler {
                navViewModel.updateRoute(SupabaseDestination.route)
                sbNavController.popBackStack(
                    SupabaseDestination.route, inclusive = false
                )
            }
            sbDeck?.let {
                importDeck.GetDeck(
                    deck = it,
                    onNavigate = {
                        navViewModel.updateRoute(SupabaseDestination.route)
                        sbNavController.navigate(SupabaseDestination.route)
                    }
                )
            }
        }
        composable(ExportSBDestination.route) {
            BackHandler {
                sbNavController.popBackStack(
                    SupabaseDestination.route, inclusive = false
                )
            }
            pickedDeck?.let {
                UploadThisDeck(
                    dismiss = {
                        sbNavController.navigate(SupabaseDestination.route)
                    },
                    it,
                    supabaseVM,
                    getUIStyle
                )
            }
        }
        composable(UserProfileDestination.route) {
            BackHandler {
                if (startDestination == UserProfileDestination.route) {
                    navViewModel.updateRoute(MainNavDestination.route)
                    BackNavHandler.returnToDeckListFromSB(
                        navController, updateCurrentTime(), fields
                    )
                } else {
                    navViewModel.updateRoute(SupabaseDestination.route)
                    sbNavController.popBackStack(
                        SupabaseDestination.route, inclusive = false
                    )
                }
            }
            MyProfile(getUIStyle, supabaseVM, startDestination) {
                navViewModel.updateRoute(SupabaseDestination.route)
                sbNavController.navigate(SupabaseDestination.route)
            }
        }
        composable(UserEDDestination.route) {
            BackHandler {
                navViewModel.updateRoute(SupabaseDestination.route)
                sbNavController.popBackStack(
                    SupabaseDestination.route, inclusive = false
                )
            }
            UserExportedDecks(getUIStyle)
        }
    }
}