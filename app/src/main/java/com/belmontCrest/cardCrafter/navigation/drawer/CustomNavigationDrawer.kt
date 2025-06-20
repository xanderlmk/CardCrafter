package com.belmontCrest.cardCrafter.navigation.drawer

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.navigation.destinations.DeckListDestination
import com.belmontCrest.cardCrafter.navigation.destinations.MainNavDestination
import com.belmontCrest.cardCrafter.navigation.NavViewModel
import com.belmontCrest.cardCrafter.navigation.destinations.SBNavDestination
import com.belmontCrest.cardCrafter.navigation.destinations.SettingsDestination
import com.belmontCrest.cardCrafter.navigation.destinations.ViewAllCardsDestination
import com.belmontCrest.cardCrafter.navigation.destinations.ViewDueCardsDestination
import com.belmontCrest.cardCrafter.navigation.destinations.SupabaseDestination
import com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels.CardDeckViewModel
import com.belmontCrest.cardCrafter.controller.viewModels.deckViewsModels.updateCurrentTime
import com.belmontCrest.cardCrafter.model.Type
import com.belmontCrest.cardCrafter.model.dialogHeight
import com.belmontCrest.cardCrafter.model.dialogWidth
import com.belmontCrest.cardCrafter.model.getIsLandScape
import com.belmontCrest.cardCrafter.model.ui.Fields
import com.belmontCrest.cardCrafter.navigation.destinations.AddCardDestination
import com.belmontCrest.cardCrafter.navigation.destinations.ExportSBDestination
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.SupabaseViewModel
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.uiFunctions.ContentIcons
import com.belmontCrest.cardCrafter.uiFunctions.katex.SymbolDocumentation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(InternalComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CustomNavigationDrawer(
    mainNavController: NavHostController,
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
        navController = mainNavController, fields = fields,
        getUIStyle = getUIStyle, navViewModel = navViewModel,
        cardDeckVM = cardDeckVM, supabaseVM = supabaseVM,
        cr = navViewModel.route.collectAsStateWithLifecycle().value,
        wd = navViewModel.wd.collectAsStateWithLifecycle().value,
        coroutineScope = coroutineScope
    )

    /** Current route */
    val cr = navViewModel.route.collectAsStateWithLifecycle().value
    val stateSize = cardDeckVM.stateSize.collectAsStateWithLifecycle().value
    val stateIndex = cardDeckVM.stateIndex.collectAsStateWithLifecycle().value

    val deckName by navViewModel.deckName.collectAsStateWithLifecycle()
    val isSelecting by navViewModel.isSelecting.collectAsStateWithLifecycle()
    val owner by supabaseVM.owner.collectAsStateWithLifecycle()
    val type by navViewModel.type.collectAsStateWithLifecycle()

    // Determine the title based on the current route.
    val titleText = when (cr.name) {
        MainNavDestination.route -> stringResource(R.string.deck_list)
        DeckListDestination.route -> stringResource(R.string.deck_list)
        SettingsDestination.route -> "Settings"
        ViewAllCardsDestination.route -> deckName.name
        ViewDueCardsDestination.route ->
            if (stateSize == 0) "" else "Card ${stateIndex + 1} out of $stateSize"

        SBNavDestination.route -> "Online Decks"
        SupabaseDestination.route -> "Online Decks"
        ExportSBDestination.route -> "Select 4 cards here "
        AddCardDestination.route -> when (type) {
            Type.HINT -> stringResource(R.string.hint)
            Type.THREE -> stringResource(R.string.three_fields)
            Type.MULTI -> stringResource(R.string.multi)
            Type.NOTATION -> "Notation"
            else -> stringResource(R.string.basic)
        }

        else -> "CardCrafter"
    }
    val isBlocking by navViewModel.isBlocking.collectAsStateWithLifecycle()
    val ci = ContentIcons(getUIStyle)
    val helpForNotation = rememberSaveable { mutableStateOf(false) }
    val isLandScape = getIsLandScape()
    val widthMod =
        if (isLandScape)
            Modifier
                .fillMaxWidth(.8f)
                .height(dialogHeight())
        else Modifier
            .width(dialogWidth())
            .fillMaxHeight(.95f)
    val scrollState = rememberScrollState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .fillMaxWidth(0.50f)
            ) {
                modalContent.Home()
                modalContent.Settings()
                modalContent.UserProfile()
                if (owner != null) {
                    modalContent.ExportDecks()
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
                    title = { Title(titleText, getUIStyle, type, cr.name, helpForNotation) },
                    navigationIcon = {
                        if (isSelecting) {
                            IconButton(onClick = { navViewModel.resetSelection() }) {
                                ci.ContentIcon(Icons.Filled.Clear)
                            }
                        } else {
                            IconButton(
                                onClick = { coroutineScope.launch { drawerState.open() } }
                            ) { ci.ContentIcon(Icons.Filled.Menu) }
                        }
                    },
                    actions = {
                        ActionIconButton(
                            getUIStyle, cardDeckVM, fields, navViewModel,
                            supabaseVM, mainNavController
                        )
                    }
                )
            }
        ) { innerPadding ->
            Box(Modifier.padding(innerPadding)) {
                content()
                SymbolDocumentation(
                    helpForNotation, getUIStyle, scrollState, widthMod
                        .padding(20.dp)
                        .verticalScroll(scrollState)
                        .border(
                            width = 2.dp,
                            shape = RoundedCornerShape(18.dp),
                            color = if (getUIStyle.getIsDarkTheme()) Color.Gray else Color.Black

                        )
                        .background(
                            color = getUIStyle.dialogColor(), shape = RoundedCornerShape(18.dp)
                        )
                        .padding(10.dp)
                )
            }
            if (isBlocking) {
                // A semi-transparent layer that consumes every pointer event
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f))
                        .pointerInput(Unit) {
                            // swallow all touch events while visible
                            awaitPointerEventScope {
                                while (true) {
                                    awaitPointerEvent()
                                }
                            }
                        }
                ) {
                    CircularProgressIndicator(
                        Modifier.align(Alignment.Center),
                        color = getUIStyle.titleColor()
                    )
                }
            }
        }
    }
}

@Composable
private fun Title(
    titleText: String, getUIStyle: GetUIStyle, type: String, cr: String,
    helpForNotation: MutableState<Boolean>,
) {
    when (cr) {
        ExportSBDestination.route -> {
            Text(
                text = titleText,
                color =
                    if (getUIStyle.getIsCuteTheme()) getUIStyle.defaultIconColor()
                    else getUIStyle.iconColor(),
                textAlign = TextAlign.End,
                maxLines = 2, overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 22.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )
        }

        AddCardDestination.route -> {
            CardType(titleText, getUIStyle, type, helpForNotation)
        }

        else -> {
            Text(
                text = titleText,
                color =
                    if (getUIStyle.getIsCuteTheme()) getUIStyle.defaultIconColor()
                    else getUIStyle.iconColor(),
                textAlign = TextAlign.Start,
                maxLines = 2, overflow = TextOverflow.Ellipsis,
                fontSize = 22.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            )
        }
    }
}

@Composable
private fun CardType(
    titleText: String, getUIStyle: GetUIStyle, type: String, helpForNotation: MutableState<Boolean>
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = titleText,
            fontSize = 22.sp,
            textAlign = TextAlign.Start,
            color =
                if (getUIStyle.getIsCuteTheme()) getUIStyle.defaultIconColor()
                else getUIStyle.iconColor(),
            fontWeight = FontWeight.SemiBold,
            modifier =
                if (type == Type.NOTATION) {
                    Modifier.padding(horizontal = 4.dp)
                } else {
                    Modifier
                }
        )
        if (type == Type.NOTATION) {
            Text(
                text = "?", fontSize = 22.sp,
                textAlign = TextAlign.Right,
                color =
                    if (getUIStyle.getIsCuteTheme()) getUIStyle.defaultIconColor()
                    else getUIStyle.iconColor(),
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Cursive,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable { helpForNotation.value = true }
            )

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