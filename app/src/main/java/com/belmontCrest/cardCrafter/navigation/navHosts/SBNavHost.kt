package com.belmontCrest.cardCrafter.navigation.navHosts

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.belmontCrest.cardCrafter.model.application.AppViewModelProvider
import com.belmontCrest.cardCrafter.navigation.destinations.SBNavDestination
import com.belmontCrest.cardCrafter.navigation.destinations.MainNavDestination
import com.belmontCrest.cardCrafter.navigation.NavViewModel
import com.belmontCrest.cardCrafter.navigation.destinations.ExportSBDestination
import com.belmontCrest.cardCrafter.navigation.destinations.ImportSBDestination
import com.belmontCrest.cardCrafter.navigation.destinations.SupabaseDestination
import com.belmontCrest.cardCrafter.navigation.destinations.UserEDDestination
import com.belmontCrest.cardCrafter.navigation.destinations.UserProfileDestination
import com.belmontCrest.cardCrafter.controller.viewModels.deckViewsModels.MainViewModel
import com.belmontCrest.cardCrafter.controller.viewModels.deckViewsModels.updateCurrentTime
import com.belmontCrest.cardCrafter.model.uiModels.Fields
import com.belmontCrest.cardCrafter.model.uiModels.PreferencesManager
import com.belmontCrest.cardCrafter.navigation.destinations.CoOwnerRequestsDestination
import com.belmontCrest.cardCrafter.navigation.destinations.SBCardListDestination
import com.belmontCrest.cardCrafter.navigation.destinations.UseEmailDestination
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.CoOwnerViewModel
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.ImportDeckViewModel
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.SupabaseViewModel
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.UserExportedDecksViewModel
import com.belmontCrest.cardCrafter.supabase.view.profile.UserExportedDecks
import com.belmontCrest.cardCrafter.supabase.view.profile.MyProfile
import com.belmontCrest.cardCrafter.supabase.view.importDeck.ImportDeck
import com.belmontCrest.cardCrafter.supabase.view.OnlineDatabase
import com.belmontCrest.cardCrafter.supabase.view.uploadDeck.UploadThisDeck
import com.belmontCrest.cardCrafter.supabase.view.authViews.email.EmailView
import com.belmontCrest.cardCrafter.supabase.view.profile.CardListView
import com.belmontCrest.cardCrafter.supabase.view.profile.RequestsView
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

    LaunchedEffect(Unit) { supabaseVM.signInSyncedDBUser() }
    LaunchedEffect(Unit) { navViewModel.updateSBNav(sbNavController) }
    /** Our Supabase Client and Views. */
    val onlineDatabase = OnlineDatabase(getUIStyle, supabaseVM)
    val importDeck = ImportDeck(getUIStyle, preferences)
    val sbDeck by supabaseVM.deck.collectAsStateWithLifecycle()
    val pickedDeck by supabaseVM.pickedDeck.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    val startingNavRoute by navViewModel.startingSBRoute.collectAsStateWithLifecycle()
    val startDestination = if (startingNavRoute.name == SupabaseDestination.route) {
        SupabaseDestination.route
    } else if (startingNavRoute.name == UserProfileDestination.route) {
        UserProfileDestination.route
    } else {
        UserEDDestination.route
    }
    val uEDVM: UserExportedDecksViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val clv = CardListView(uEDVM, getUIStyle)
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
                    navViewModel.updateRoute(ImportSBDestination.route)
                    sbNavController.navigate(ImportSBDestination.createRoute(uuid))
                    coroutineScope.launch {
                        supabaseVM.updateUUID(uuid)
                    }
                },
                onUseEmail = {
                    navViewModel.updateRoute(UseEmailDestination.route)
                    sbNavController.navigate(UseEmailDestination.route)
                }
            )
        }
        composable(UseEmailDestination.route) {
            BackHandler {
                if (startDestination == UserProfileDestination.route) {
                    navViewModel.updateRoute(UserProfileDestination.route)
                    sbNavController.popBackStack(
                        UserProfileDestination.route, inclusive = false
                    )
                } else {
                    navViewModel.updateRoute(SupabaseDestination.route)
                    sbNavController.popBackStack(
                        SupabaseDestination.route, inclusive = false
                    )
                }
            }
            EmailView(
                supabaseVM = supabaseVM, getUIStyle = getUIStyle,
                onNavigate = {
                    if (startDestination == UserProfileDestination.route) {
                        navViewModel.updateRoute(UserProfileDestination.route)
                        sbNavController.popBackStack(
                            UserProfileDestination.route, inclusive = false
                        )
                    } else {
                        navViewModel.updateRoute(SupabaseDestination.route)
                        sbNavController.popBackStack(
                            SupabaseDestination.route, inclusive = false
                        )
                    }
                }
            )
        }
        composable(
            ImportSBDestination.route,
            arguments = listOf(navArgument("uuid") {
                type = NavType.StringType
            })
        ) {
            BackHandler {
                navViewModel.updateRoute(SupabaseDestination.route)
                sbNavController.popBackStack(
                    SupabaseDestination.route, inclusive = false
                )
            }
            val importDeckVM: ImportDeckViewModel =
                viewModel(factory = AppViewModelProvider.Factory)

            sbDeck?.let {
                importDeck.GetDeck(
                    deck = it,
                    onNavigate = {
                        navViewModel.updateRoute(SupabaseDestination.route)
                        sbNavController.navigate(SupabaseDestination.route)
                    }, importDeckVM = importDeckVM
                )
            }
        }
        composable(ExportSBDestination.route) {
            BackHandler {
                navViewModel.updateRoute(UserEDDestination.route)
                sbNavController.popBackStack(
                    UserEDDestination.route, inclusive = false
                )
            }
            pickedDeck?.let {
                UploadThisDeck(
                    dismiss = {
                        navViewModel.updateRoute(UserEDDestination.route)
                        sbNavController.navigate(UserEDDestination.route)
                    },
                    it, supabaseVM, getUIStyle
                )
            }
        }
        composable(UserProfileDestination.route) {
            BackHandler {
                navViewModel.updateRoute(MainNavDestination.route)
                BackNavHandler.returnToDeckListFromSB(
                    navController, updateCurrentTime(), fields
                )
            }
            MyProfile(
                getUIStyle, supabaseVM, startDestination,
                onUseEmail = {
                    navViewModel.updateRoute(UseEmailDestination.route)
                    sbNavController.navigate(UseEmailDestination.route)
                }, onSignOut = {
                    navViewModel.updateRoute(SupabaseDestination.route)
                    sbNavController.navigate(SupabaseDestination.route)
                })
        }
        composable(UserEDDestination.route) {
            BackHandler {
                navViewModel.updateRoute(MainNavDestination.route)
                BackNavHandler.returnToDeckListFromSB(
                    navController, updateCurrentTime(), fields
                )
            }
            UserExportedDecks(
                getUIStyle,
                uEDVM,
                onNavigate = { uuid ->
                    uEDVM.updateUUUID(uuid)
                    navViewModel.updateRoute(SBCardListDestination.route)
                    sbNavController.navigate(SBCardListDestination.route)
                },
                onExportDeck = { uuid ->
                    navViewModel.updateRoute(ExportSBDestination.route)
                    sbNavController.navigate(ExportSBDestination.route)
                    supabaseVM.updateCardsToDisplayUUID(uuid)
                },
                localDeckList = mainViewModel.deckUiState.collectAsStateWithLifecycle().value.deckList,
                supabaseVM = supabaseVM
            )
        }

        composable(SBCardListDestination.route) {
            BackHandler {
                navViewModel.updateRoute(UserEDDestination.route)
                sbNavController.popBackStack(
                    UserEDDestination.route, inclusive = false
                )
            }
            clv.AllCards()
        }

        composable(CoOwnerRequestsDestination.route) {
            BackHandler {
                navViewModel.updateRoute(UserProfileDestination.route)
                sbNavController.popBackStack(
                    UserProfileDestination.route, inclusive = false
                )
            }
            val corVM: CoOwnerViewModel = viewModel(factory = AppViewModelProvider.Factory)
            val rv = RequestsView(getUIStyle, corVM)
            rv.Requests()
        }
    }
}