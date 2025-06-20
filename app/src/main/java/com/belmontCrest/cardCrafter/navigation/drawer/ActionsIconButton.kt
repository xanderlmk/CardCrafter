package com.belmontCrest.cardCrafter.navigation.drawer

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.belmontCrest.cardCrafter.controller.cardHandlers.toCard
import com.belmontCrest.cardCrafter.navigation.destinations.AddCardDestination
import com.belmontCrest.cardCrafter.navigation.destinations.DeckNavDestination
import com.belmontCrest.cardCrafter.navigation.destinations.DeckViewDestination
import com.belmontCrest.cardCrafter.navigation.destinations.EditingCardDestination
import com.belmontCrest.cardCrafter.navigation.NavViewModel
import com.belmontCrest.cardCrafter.navigation.destinations.ViewAllCardsDestination
import com.belmontCrest.cardCrafter.navigation.destinations.ViewDueCardsDestination
import com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels.CardDeckViewModel
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.model.ui.states.Decision
import com.belmontCrest.cardCrafter.model.ui.states.Dialogs
import com.belmontCrest.cardCrafter.model.ui.Fields
import com.belmontCrest.cardCrafter.navigation.destinations.AddDeckDestination
import com.belmontCrest.cardCrafter.navigation.destinations.CoOwnerRequestsDestination
import com.belmontCrest.cardCrafter.navigation.destinations.DeckListDestination
import com.belmontCrest.cardCrafter.navigation.destinations.EditDeckDestination
import com.belmontCrest.cardCrafter.navigation.destinations.ExportSBDestination
import com.belmontCrest.cardCrafter.navigation.destinations.MainNavDestination
import com.belmontCrest.cardCrafter.navigation.destinations.UserProfileDestination
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.SupabaseViewModel
import com.belmontCrest.cardCrafter.supabase.view.exportDeck.CardPickerDropdown
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.backButtonModifier
import com.belmontCrest.cardCrafter.ui.theme.redoButtonModifier
import com.belmontCrest.cardCrafter.uiFunctions.buttons.BackButton
import com.belmontCrest.cardCrafter.uiFunctions.buttons.CardListOptions
import com.belmontCrest.cardCrafter.uiFunctions.buttons.CardOptionsButton
import com.belmontCrest.cardCrafter.uiFunctions.buttons.CardTypesButton
import com.belmontCrest.cardCrafter.uiFunctions.buttons.MailButton
import com.belmontCrest.cardCrafter.uiFunctions.buttons.RedoCardButton
import com.belmontCrest.cardCrafter.uiFunctions.showToastMessage
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun ActionIconButton(
    getUIStyle: GetUIStyle,
    cardDeckVM: CardDeckViewModel,
    fields: Fields,
    navViewModel: NavViewModel,
    supabaseVM: SupabaseViewModel,
    mainNavController: NavHostController
) {
    val deckNavController by navViewModel.deckNav.collectAsStateWithLifecycle()
    val sbNavController by navViewModel.sbNav.collectAsStateWithLifecycle()
    val sc by navViewModel.card.collectAsStateWithLifecycle()
    val wd by navViewModel.wd.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val isSelecting by navViewModel.isSelecting.collectAsStateWithLifecycle()
    val onNavigateBack: () -> Unit = {
        fields.isEditing.value = false
        fields.inDeckClicked.value = false
        coroutineScope.launch { navViewModel.getCardById(0) }
        navViewModel.updateRoute(ViewAllCardsDestination.route)
        deckNavController?.navigate(ViewAllCardsDestination.route)
    }
    val cr by navViewModel.route.collectAsStateWithLifecycle()
    val selectable by navViewModel.selectable.collectAsStateWithLifecycle()

    when (cr.name) {
        MainNavDestination.route -> {
            MainDLRouteContent(getUIStyle, coroutineScope, navViewModel, mainNavController)
        }

        DeckListDestination.route -> {
            MainDLRouteContent(getUIStyle, coroutineScope, navViewModel, mainNavController)
        }

        AddDeckDestination.route -> {
            BackButton(
                onBackClick = {
                    fields.mainClicked.value = false
                    navViewModel.updateRoute(DeckListDestination.route)
                    mainNavController.navigate(DeckListDestination.route)
                },
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .backButtonModifier(),
                getUIStyle = getUIStyle
            )
        }

        DeckNavDestination.route -> {
            wd.deck?.let {
                DeckRouteContent(fields, navViewModel, it, deckNavController, getUIStyle)
            }
        }

        DeckViewDestination.route -> {
            wd.deck?.let {
                DeckRouteContent(fields, navViewModel, it, deckNavController, getUIStyle)
            }
        }

        EditDeckDestination.route -> {
            BackButton(
                onBackClick = {
                    cardDeckVM.updateWhichDeck(0)
                    fields.inDeckClicked.value = false
                    navViewModel.updateRoute(DeckViewDestination.route)
                    deckNavController?.navigate(DeckViewDestination.route)
                },
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .backButtonModifier(),
                getUIStyle = getUIStyle
            )
        }

        ViewAllCardsDestination.route -> {
            var deckList by rememberSaveable { mutableStateOf(emptyList<Deck>()) }
            LaunchedEffect(Unit) {
                coroutineScope.launch { deckList = navViewModel.getAllDecks() }
            }
            if (!isSelecting) {
                BackButton(
                    onBackClick = {
                        fields.inDeckClicked.value = false
                        navViewModel.updateRoute(DeckViewDestination.route)
                        deckNavController?.navigate(DeckViewDestination.route)
                    },
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .backButtonModifier(),
                    getUIStyle = getUIStyle,
                )
            } else {
                var enabled by rememberSaveable { mutableStateOf(true) }
                var showDelDialog by rememberSaveable { mutableStateOf(false) }
                var showCORMDialog by rememberSaveable { mutableStateOf(false) }
                var showDupDialog by rememberSaveable { mutableStateOf(false) }
                val expanded = rememberSaveable { mutableStateOf(false) }
                val onFinished: () -> Unit = {
                    showCORMDialog = false; enabled = true; expanded.value = false
                    showDupDialog = false; showDelDialog = false
                }
                CardListOptions(
                    onDelete = {
                        coroutineScope.launch {
                            enabled = false
                            val success = navViewModel.deleteCardList()
                            if (!success) showToastMessage(context, "Failed to delete cards")
                            onFinished()
                        }
                    },
                    onDuplicate = {
                        wd.deck?.let { deck ->
                            coroutineScope.launch {
                                enabled = false
                                val (success, _) = navViewModel.copyCardList(deck.id)
                                if (!success) showToastMessage(context, "Failed to duplicate cards")
                                else showToastMessage(context, "Duplicated cards successfully!")
                                onFinished()
                            }
                        }
                    },
                    onCopyMoveCL = { decision, deckId ->
                        when (decision) {
                            Decision.Copy -> {
                                coroutineScope.launch {
                                    enabled = false
                                    val (success, deckName) = navViewModel.copyCardList(deckId)
                                    if (!success) showToastMessage(
                                        context, "Failed to copy cards to $$**", deckName
                                    )
                                    else showToastMessage(
                                        context, "Copied cards to $$**!", deckName
                                    )
                                    onFinished()
                                }
                            }

                            Decision.Idle -> {
                                showToastMessage(context, "Error: no valid decision made.")
                            }

                            Decision.Move -> {
                                coroutineScope.launch {
                                    enabled = false
                                    val (success, deckName) = navViewModel.moveCardList(deckId)
                                    if (!success) showToastMessage(
                                        context, "Failed to move cards to $$**", deckName
                                    )
                                    else showToastMessage(
                                        context, "Moved cards to $$**!", deckName
                                    )
                                    onFinished()
                                }
                            }
                        }
                    }, deckList = deckList, selectedDeck = wd.deck, getUIStyle = getUIStyle,
                    onSelectAll = { coroutineScope.launch { navViewModel.selectAll() } },
                    onDeselectAll = { navViewModel.deselectAll() }, selectable = selectable,
                    onCORMDialogToggle = { showCORMDialog = it }, expanded = expanded,
                    onDelDialogToggle = { showDelDialog = it }, enabled = enabled,
                    onDupDialogToggle = { showDupDialog = it },
                    dialogs = Dialogs(
                        showDelete = showDelDialog, showMoveCopy = showCORMDialog,
                        showDuplicate = showDupDialog
                    )
                )
            }
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

        UserProfileDestination.route -> {
            MailButton(onClick = {
                sbNavController?.navigate(CoOwnerRequestsDestination.route)
                navViewModel.updateRoute(CoOwnerRequestsDestination.route)
            }, getUIStyle)
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
