package com.belmontCrest.cardCrafter.navigation.drawer

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.model.application.AppViewModelProvider
import com.belmontCrest.cardCrafter.model.uiModels.Fields
import com.belmontCrest.cardCrafter.navigation.NavViewModel
import com.belmontCrest.cardCrafter.navigation.destinations.EditDeckDestination
import com.belmontCrest.cardCrafter.navigation.destinations.ViewAllCardsDestination
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.PersonalDeckSyncViewModel
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.SyncStatus
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.settingsButtonModifier
import com.belmontCrest.cardCrafter.uiFunctions.CancelButton
import com.belmontCrest.cardCrafter.uiFunctions.ContentIcons
import com.belmontCrest.cardCrafter.uiFunctions.CustomText
import com.belmontCrest.cardCrafter.uiFunctions.SettingsButton
import com.belmontCrest.cardCrafter.uiFunctions.SubmitButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/** For MainNavDestination and DeckListDestination */
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MainDLRouteContent(
    getUIStyle: GetUIStyle, coroutineScope: CoroutineScope, navViewModel: NavViewModel
) {
    val ci = ContentIcons(getUIStyle)
    val pdsVM: PersonalDeckSyncViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val syncStatus by pdsVM.syncStatus.collectAsStateWithLifecycle()
    when (val status = syncStatus) {
        is SyncStatus.Error -> {
            AlertDialog(
                onDismissRequest = {
                    pdsVM.resetSyncStatus()
                    navViewModel.resetIsBlocking()
                },
                title = { Text("Error") },
                text = { CustomText(status.message, getUIStyle) },
                confirmButton = {},
                dismissButton = {},
            )
        }

        SyncStatus.Conflict -> {
            AlertDialog(
                onDismissRequest = {
                    pdsVM.resetSyncStatus()
                    navViewModel.resetIsBlocking()
                },
                title = { Text("Sync Conflict") },
                text = {
                    Column {
                        CustomText("Another sync has already been made", getUIStyle)
                        CustomText("Would you like to replace it?", getUIStyle)
                    }
                },
                confirmButton = {
                    SubmitButton(
                        onClick = {
                            navViewModel.updateIsBlocking()
                            pdsVM.fetchRemoteDecks()

                        }, true, getUIStyle, "Replace"
                    )
                },
                dismissButton = {
                    CancelButton(
                        onClick = {
                            pdsVM.resetSyncStatus()
                            navViewModel.resetIsBlocking()
                        }, true, getUIStyle
                    )
                },
            )
        }

        SyncStatus.Idle -> {
            /** Do nothing */
        }

        SyncStatus.Success -> {
            LaunchedEffect(Unit) {
                coroutineScope.launch {
                    navViewModel.resetIsBlocking()
                    pdsVM.resetSyncStatus()
                }
            }
        }

        SyncStatus.Syncing -> {
            LaunchedEffect(Unit) {
                coroutineScope.launch {
                    navViewModel.updateIsBlocking()
                }
            }
        }
    }
    IconButton(onClick = {
        coroutineScope.launch {
            pdsVM.syncDecks()
        }
    }) {
        ci.ContentIcon(
            "Cloud Sync", painterResource(R.drawable.cloud_sync), Modifier
        )
    }
}

/** For DeckNavDestination and DeckViewDestination */
@Composable
fun DeckRouteContent(
    fields: Fields, navViewModel: NavViewModel, deck: Deck,
    deckNavController: NavHostController?, getUIStyle: GetUIStyle
) {
    SettingsButton(
        onNavigateToEditDeck = {
            if (!fields.inDeckClicked.value) {
                fields.inDeckClicked.value = true
                fields.mainClicked.value = false
                navViewModel.updateRoute(
                    EditDeckDestination.createRoute(deck.name)
                )
                deckNavController?.navigate(
                    EditDeckDestination.createRoute(deck.name)
                )
            }
        },
        onNavigateToEditCards = {
            if (!fields.inDeckClicked.value) {
                fields.inDeckClicked.value = true
                fields.mainClicked.value = false
                navViewModel.updateRoute(ViewAllCardsDestination.route)
                deckNavController?.navigate(ViewAllCardsDestination.route)
            }
        },
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .settingsButtonModifier(),
        getUIStyle = getUIStyle,
        fields = fields
    )
}