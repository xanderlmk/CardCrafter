package com.belmontCrest.cardCrafter.controller.navigation.drawer

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
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
import com.belmontCrest.cardCrafter.controller.navigation.destinations.DeckListDestination
import com.belmontCrest.cardCrafter.controller.navigation.destinations.MainNavDestination
import com.belmontCrest.cardCrafter.controller.navigation.NavViewModel
import com.belmontCrest.cardCrafter.controller.navigation.destinations.SBNavDestination
import com.belmontCrest.cardCrafter.controller.navigation.destinations.SettingsDestination
import com.belmontCrest.cardCrafter.controller.navigation.destinations.ViewAllCardsDestination
import com.belmontCrest.cardCrafter.controller.navigation.destinations.ViewDueCardsDestination
import com.belmontCrest.cardCrafter.controller.navigation.destinations.SupabaseDestination
import com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels.CardDeckViewModel
import com.belmontCrest.cardCrafter.controller.viewModels.deckViewsModels.updateCurrentTime
import com.belmontCrest.cardCrafter.model.uiModels.Fields
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.SupabaseViewModel
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(InternalComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CustomNavigationDrawer(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    fields: Fields,
    getUIStyle: GetUIStyle,
    navViewModel: NavViewModel,
    cardDeckVM: CardDeckViewModel,
    supabaseVM: SupabaseViewModel,
    content: @Composable () -> Unit,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()


    val modalContent = ModalContent(
        navController = navController, fields = fields,
        getUIStyle = getUIStyle, navViewModel = navViewModel,
        cardDeckVM = cardDeckVM, supabaseVM = supabaseVM,
        cr = navViewModel.route.collectAsStateWithLifecycle().value,
        wd = navViewModel.wd.collectAsStateWithLifecycle().value
    )
    // Extract the current route.
    val cr = navViewModel.route.collectAsStateWithLifecycle().value
    val stateSize = cardDeckVM.stateSize.collectAsStateWithLifecycle().value
    val stateIndex = cardDeckVM.stateIndex.collectAsStateWithLifecycle().value


    val deckName by navViewModel.deckName.collectAsStateWithLifecycle()

    // Determine the title based on the current route.
    val titleText = when (cr.name) {
        MainNavDestination.route -> "Decks"
        DeckListDestination.route -> "Decks"
        SettingsDestination.route -> "Settings"
        ViewAllCardsDestination.route -> deckName.name
        ViewDueCardsDestination.route -> if (stateSize == 0) {
            ""
        } else {
            "Card ${stateIndex + 1} out of $stateSize"
        }

        SBNavDestination.route -> "Online Decks"
        SupabaseDestination.route -> "Online Decks"
        else -> "CardCrafter"
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .fillMaxWidth(0.50f)
            ) {
                modalContent.Home(coroutineScope)
                modalContent.Settings(coroutineScope)
                modalContent.UserProfile(coroutineScope)
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
                        ActionIconButton(
                            getUIStyle, cardDeckVM,
                            fields, navViewModel, supabaseVM
                        )
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
    navViewModel: NavViewModel,
    cardDeckVM: CardDeckViewModel,
    fields: Fields
) {
    coroutineScope.launch {
        updateCurrentTime()
        navViewModel.resetCard()
        cardDeckVM.updateIndex(0)
        fields.scrollPosition.value = 0
        fields.inDeckClicked.value = true
        fields.mainClicked.value = false
    }
}