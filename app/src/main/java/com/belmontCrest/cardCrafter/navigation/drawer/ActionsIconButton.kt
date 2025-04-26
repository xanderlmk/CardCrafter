package com.belmontCrest.cardCrafter.navigation.drawer

import android.icu.text.CaseMap.Title
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.controller.cardHandlers.toCard
import com.belmontCrest.cardCrafter.navigation.destinations.AddCardDestination
import com.belmontCrest.cardCrafter.navigation.destinations.DeckNavDestination
import com.belmontCrest.cardCrafter.navigation.destinations.DeckViewDestination
import com.belmontCrest.cardCrafter.navigation.destinations.EditDeckDestination
import com.belmontCrest.cardCrafter.navigation.destinations.EditingCardDestination
import com.belmontCrest.cardCrafter.navigation.NavViewModel
import com.belmontCrest.cardCrafter.navigation.destinations.ViewAllCardsDestination
import com.belmontCrest.cardCrafter.navigation.destinations.ViewDueCardsDestination
import com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels.CardDeckViewModel
import com.belmontCrest.cardCrafter.model.application.AppViewModelProvider
import com.belmontCrest.cardCrafter.model.uiModels.Fields
import com.belmontCrest.cardCrafter.navigation.destinations.DeckListDestination
import com.belmontCrest.cardCrafter.navigation.destinations.ExportSBDestination
import com.belmontCrest.cardCrafter.navigation.destinations.MainNavDestination
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.PersonalDeckSyncViewModel
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.SupabaseViewModel
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.SyncStatus
import com.belmontCrest.cardCrafter.supabase.view.uploadDeck.CardPickerDropdown
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.backButtonModifier
import com.belmontCrest.cardCrafter.ui.theme.redoButtonModifier
import com.belmontCrest.cardCrafter.ui.theme.settingsButtonModifier
import com.belmontCrest.cardCrafter.uiFunctions.BackButton
import com.belmontCrest.cardCrafter.uiFunctions.CardOptionsButton
import com.belmontCrest.cardCrafter.uiFunctions.CardTypesButton
import com.belmontCrest.cardCrafter.uiFunctions.RedoCardButton
import com.belmontCrest.cardCrafter.uiFunctions.SettingsButton
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun ActionIconButton(
    getUIStyle: GetUIStyle,
    cardDeckVM: CardDeckViewModel,
    fields: Fields,
    navViewModel: NavViewModel,
    supabaseVM: SupabaseViewModel
) {
    val deckNavController by navViewModel.deckNav.collectAsStateWithLifecycle()
    val sc by navViewModel.card.collectAsStateWithLifecycle()
    val wd by navViewModel.wd.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val onNavigateBack: () -> Unit = {
        fields.isEditing.value = false
        fields.inDeckClicked.value = false
        coroutineScope.launch {
            navViewModel.getCardById(0)
        }
        navViewModel.updateRoute(ViewAllCardsDestination.route)
        deckNavController?.navigate(ViewAllCardsDestination.route)
    }
    val cr = navViewModel.route.collectAsStateWithLifecycle().value

    when (cr.name) {

        MainNavDestination.route -> {
            val pdsVM: PersonalDeckSyncViewModel = viewModel(factory = AppViewModelProvider.Factory)
            val syncStatus by pdsVM.syncStatus.collectAsStateWithLifecycle()
            if (syncStatus != SyncStatus.Idle && syncStatus != SyncStatus.Syncing &&
                syncStatus != SyncStatus.Success){
                AlertDialog(
                    onDismissRequest = {pdsVM.resetSyncStatus()},
                    title = { Text("Card Details") },
                    text = { Text("Error") },
                    confirmButton = {},
                    dismissButton = {},
                )
            }
            IconButton(onClick = {
                coroutineScope.launch {
                    pdsVM.syncDecks()
                }
            }) {
                Icon(
                    painter = painterResource(R.drawable.cloud_sync),
                    contentDescription = "Cloud Sync"
                )
            }
        }
        DeckListDestination.route -> {
            val pdsVM: PersonalDeckSyncViewModel = viewModel(factory = AppViewModelProvider.Factory)
            val syncStatus by pdsVM.syncStatus.collectAsStateWithLifecycle()
            if (syncStatus != SyncStatus.Idle && syncStatus != SyncStatus.Syncing &&
                syncStatus != SyncStatus.Success){
                AlertDialog(
                    onDismissRequest = {pdsVM.resetSyncStatus()},
                    title = { Text("Card Details") },
                    text = { Text("Error") },
                    confirmButton = {},
                    dismissButton = {},
                )
            }
            IconButton(onClick = {
                coroutineScope.launch {
                    pdsVM.syncDecks()
                }
            }) {
                Icon(
                    painter = painterResource(R.drawable.cloud_sync),
                    contentDescription = "Cloud Sync"
                )
            }
        }

        DeckNavDestination.route -> {
            wd.deck?.let {
                SettingsButton(
                    onNavigateToEditDeck = {
                        if (!fields.inDeckClicked.value) {
                            fields.inDeckClicked.value = true
                            fields.mainClicked.value = false
                            navViewModel.updateRoute(
                                EditDeckDestination.createRoute(it.name)
                            )
                            deckNavController?.navigate(
                                EditDeckDestination.createRoute(it.name)
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
        }

        DeckViewDestination.route -> {
            wd.deck?.let {
                SettingsButton(
                    onNavigateToEditDeck = {
                        if (!fields.inDeckClicked.value) {
                            fields.inDeckClicked.value = true
                            fields.mainClicked.value = false
                            navViewModel.updateRoute(
                                EditDeckDestination.createRoute(it.name)
                            )
                            deckNavController?.navigate(
                                EditDeckDestination.createRoute(it.name)
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
        }

        ViewAllCardsDestination.route -> {
            BackButton(
                onBackClick = {
                    fields.inDeckClicked.value = false
                    navViewModel.updateRoute(DeckViewDestination.route)
                    deckNavController?.navigate(DeckViewDestination.route)
                },
                getUIStyle = getUIStyle,
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .backButtonModifier()
            )
        }

        AddCardDestination.route -> {
            CardTypesButton(getUIStyle, navViewModel)
        }

        ViewDueCardsDestination.route -> {
            RedoCardButton(
                onRedoClick = {
                    cardDeckVM.updateRedoClicked(true)
                },
                modifier = Modifier
                    .redoButtonModifier(),
                getUIStyle = getUIStyle
            )
        }

        EditingCardDestination.route -> {
            val expanded = rememberSaveable { mutableStateOf(false) }
            sc.ct?.toCard()?.let {
                CardOptionsButton(
                    navViewModel, getUIStyle, it, fields,
                    expanded, Modifier, onNavigateBack
                )
            }
        }

        ExportSBDestination.route -> {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = getUIStyle.defaultIconColor(),
                    modifier = Modifier
                        .size(30.dp)
                )
                CardPickerDropdown(
                    getUIStyle, supabaseVM, Modifier.wrapContentSize(Alignment.TopEnd)
                )
            }
        }
    }
}
