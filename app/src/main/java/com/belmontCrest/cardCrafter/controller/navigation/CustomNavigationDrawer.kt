package com.belmontCrest.cardCrafter.controller.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.belmontCrest.cardCrafter.controller.cardHandlers.updateDecksCardList
import com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels.CardDeckViewModel
import com.belmontCrest.cardCrafter.controller.viewModels.deckViewsModels.MainViewModel
import com.belmontCrest.cardCrafter.model.tablesAndApplication.Deck
import com.belmontCrest.cardCrafter.model.uiModels.Fields
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(InternalComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CustomNavigationDrawer(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    fields: Fields,
    getUIStyle: GetUIStyle,
    mainViewModel: MainViewModel,
    navViewModel: NavViewModel,
    deck: Deck?,
    cardDeckVM: CardDeckViewModel,
    content: @Composable () -> Unit,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Observe the current back stack entry as state.
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val cardsToUpdate by cardDeckVM.cardListToUpdate.collectAsStateWithLifecycle()
    // Extract the current route.
    val currentRoute = navBackStackEntry.value?.destination?.route
    val stateSize = cardDeckVM.stateSize.collectAsStateWithLifecycle().value
    val stateIndex = cardDeckVM.stateIndex.collectAsStateWithLifecycle().value

    // Determine the title based on the current route.
    val titleText = when (currentRoute) {
        DeckListDestination.route -> "Decks"
        SettingsDestination.route -> "Settings"
        ViewAllCardsDestination.route -> DeckViewDestination.name
        ViewDueCardsDestination.route -> if (stateSize == 0) {
            ""
        } else {
            "Card ${stateIndex + 1} out of $stateSize"
        }

        else -> "App"
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .fillMaxWidth(0.50f)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            fields.mainClicked.value = false
                            if (currentRoute == ViewDueCardsDestination.route) {
                                deck?.let {
                                    /** If the list is empty, no cards
                                     *  have been due even before the user joined,
                                     *  or the user finished the deck.
                                     */
                                    println("updating cards!")
                                    if (cardsToUpdate.isNotEmpty()) {
                                        coroutineScope.launch(Dispatchers.IO) {
                                            updateDecksCardList(
                                                it,
                                                cardsToUpdate,
                                                cardDeckVM
                                            )
                                        }
                                    }
                                }
                            }
                            launchHome(
                                coroutineScope, mainViewModel,
                                navViewModel, cardDeckVM, fields
                            )
                            navController.navigate(DeckListDestination.route)
                        }
                        .padding(top = 15.dp, bottom = 6.dp, start = 15.dp, end = 15.dp)
                )
                {
                    Text(
                        text = "Home"
                    )
                    Icon(
                        imageVector = Icons.Filled.Home,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .size(28.dp)
                            .background(
                                color = getUIStyle.buttonColor(),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentDescription = "Home",
                        tint = getUIStyle.iconColor()
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (currentRoute == ViewDueCardsDestination.route) {
                                deck?.let {
                                    /** If the list is empty, no cards
                                     *  have been due even before the user joined,
                                     *  or the user finished the deck.
                                     */
                                    println("updating cards!")
                                    if (cardsToUpdate.isNotEmpty()) {
                                        coroutineScope.launch(Dispatchers.IO) {
                                            updateDecksCardList(
                                                it,
                                                cardsToUpdate,
                                                cardDeckVM
                                            )
                                        }
                                    }
                                }
                            }
                            cardDeckVM.updateIndex(0)
                            navController.navigate(SettingsDestination.route)
                        }
                        .padding(vertical = 6.dp, horizontal = 15.dp)
                )
                {
                    Text(
                        "Settings"
                    )
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .size(28.dp)
                            .background(
                                color = getUIStyle.buttonColor(),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentDescription = "Main Settings",
                        tint = getUIStyle.iconColor()
                    )
                }
            }
        },
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    colors =
                        TopAppBarDefaults.topAppBarColors(
                            containerColor = getUIStyle.navBarColor(),
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                    title = {
                        Text(
                            text = titleText,
                            color = getUIStyle.titleColor(),
                            textAlign = TextAlign.Start,
                            fontSize = 22.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp)
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    drawerState.open()
                                }
                            }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Localized description"
                            )
                        }
                    },
                    actions = {
                        deck?.let {
                            ActionIconButton(
                                currentRoute ?: "", navController,
                                getUIStyle, cardDeckVM,
                                it, fields,
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(modifier.padding(innerPadding)) {
                content()
            }
        }
    }
}


fun launchHome(
    coroutineScope: CoroutineScope,
    mainViewModel: MainViewModel,
    navViewModel: NavViewModel,
    cardDeckVM: CardDeckViewModel,
    fields: Fields
) {
    coroutineScope.launch {
        mainViewModel.updateCurrentTime()
        navViewModel.resetCard()
        cardDeckVM.updateIndex(0)
        fields.scrollPosition.value = 0
        fields.inDeckClicked.value = true
        fields.mainClicked.value = false
    }
}