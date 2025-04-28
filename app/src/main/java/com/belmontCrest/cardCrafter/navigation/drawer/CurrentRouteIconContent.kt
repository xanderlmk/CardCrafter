package com.belmontCrest.cardCrafter.navigation.drawer

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.model.FSProp
import com.belmontCrest.cardCrafter.model.FWProp
import com.belmontCrest.cardCrafter.model.TAProp
import com.belmontCrest.cardCrafter.model.TextProps
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
            val fillMWModifier = Modifier.fillMaxWidth()
            Dialog(
                onDismissRequest = {
                    pdsVM.resetSyncStatus()
                    navViewModel.resetIsBlocking()
                }
            ) {
                Box(
                    modifier = fillMWModifier
                        .background(
                            color = getUIStyle.altBackground(), shape = RoundedCornerShape(24.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Column(
                        modifier = fillMWModifier,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CustomText(
                            "Sync Conflict", getUIStyle, fillMWModifier,
                            TextProps(fw = FWProp.Bold, ta = TAProp.Start, fs = FSProp.Font20)
                        )
                        CustomText(
                            "Another sync has already been made.",
                            getUIStyle, fillMWModifier
                        )
                        CustomText(
                            "Would you like to replace this local one or the remote one?",
                            getUIStyle, fillMWModifier
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = fillMWModifier,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SubmitButton(
                                onClick = {
                                    navViewModel.updateIsBlocking()
                                    pdsVM.fetchRemoteDecks()

                                }, true, getUIStyle, "Replace This",
                                fontSize = 12.sp
                            )
                            SubmitButton(
                                onClick = {
                                    navViewModel.updateIsBlocking()
                                    pdsVM.overrideSyncDecks()
                                }, true, getUIStyle, "Replace Remote",
                                fontSize = 12.sp
                            )
                        }
                        CancelButton(
                            onClick = {
                                pdsVM.resetSyncStatus()
                                navViewModel.resetIsBlocking()
                            }, true, getUIStyle, fontSize = 12.sp
                        )
                    }
                }
            }
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