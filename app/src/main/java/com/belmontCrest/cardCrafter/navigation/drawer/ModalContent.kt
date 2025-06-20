package com.belmontCrest.cardCrafter.navigation.drawer

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.controller.cardHandlers.updateDecksCardList
import com.belmontCrest.cardCrafter.navigation.NavViewModel
import com.belmontCrest.cardCrafter.navigation.destinations.DeckListDestination
import com.belmontCrest.cardCrafter.navigation.destinations.SBNavDestination
import com.belmontCrest.cardCrafter.navigation.destinations.SettingsDestination
import com.belmontCrest.cardCrafter.navigation.destinations.UserProfileDestination
import com.belmontCrest.cardCrafter.navigation.destinations.ViewDueCardsDestination
import com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels.CardDeckViewModel
import com.belmontCrest.cardCrafter.model.FSProp
import com.belmontCrest.cardCrafter.model.paddingForModal
import com.belmontCrest.cardCrafter.model.returnFontSizeBasedOnDp
import com.belmontCrest.cardCrafter.model.toTextProp
import com.belmontCrest.cardCrafter.model.ui.Fields
import com.belmontCrest.cardCrafter.model.ui.states.StringVar
import com.belmontCrest.cardCrafter.model.ui.states.WhichDeck
import com.belmontCrest.cardCrafter.navigation.destinations.AddCardDestination
import com.belmontCrest.cardCrafter.navigation.destinations.EditingCardDestination
import com.belmontCrest.cardCrafter.navigation.destinations.UserEDDestination
import com.belmontCrest.cardCrafter.navigation.destinations.ViewAllCardsDestination
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.SupabaseViewModel
import com.belmontCrest.cardCrafter.uiFunctions.showToastMessage
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.uiFunctions.ContentIcons
import com.belmontCrest.cardCrafter.uiFunctions.CustomText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ModalContent(
    private val navController: NavHostController,
    private val fields: Fields,
    private val getUIStyle: GetUIStyle,
    private val navViewModel: NavViewModel,
    private val cardDeckVM: CardDeckViewModel,
    private val supabaseVM: SupabaseViewModel,
    private val cr: StringVar,
    private val wd: WhichDeck,
    private val coroutineScope: CoroutineScope
) {
    private val mdModifier = Modifier
        .padding(horizontal = 4.dp)
        .size(28.dp)
        .background(color = getUIStyle.buttonColor(), shape = RoundedCornerShape(12.dp))
        .zIndex(2f)
    private val ci = ContentIcons(getUIStyle)

    @Composable
    fun Home() {
        val fontSize = returnFontSizeBasedOnDp()
        CustomRow(
            onClick = {
                launchUpdates()
                fields.mainClicked.value = false
                launchHome(coroutineScope, navViewModel, cardDeckVM, fields)
                navViewModel.updateRoute(DeckListDestination.route)
                navController.navigate(DeckListDestination.route)
            })
        {
            CustomText(text = "Home", getUIStyle, props = FSProp.Default.toTextProp(fontSize))
            ci.ContentIcon(Icons.Filled.Home, "Home", mdModifier)
        }
    }

    @Composable
    fun ExportDecks() {
        val fontSize = returnFontSizeBasedOnDp()
        CustomRow(onClick = {
            launchUpdates()
            navViewModel.updateRoute(UserEDDestination.route)
            navViewModel.updateStartingSBRoute(UserEDDestination.route)
            navController.navigate(SBNavDestination.route)
        }) {
            CustomText("Exported Decks", getUIStyle, props = FSProp.Default.toTextProp(fontSize))
            ci.ContentIcon(
                painterResource(R.drawable.rounded_playing_cards),
                "Rounded Playing Cards", mdModifier
            )
        }
    }

    @Composable
    fun Settings() {
        val fontSize = returnFontSizeBasedOnDp()
        CustomRow(
            onClick = {
                launchUpdates()
                cardDeckVM.updateIndex(0)
                navViewModel.updateRoute(SettingsDestination.route)
                navController.navigate(SettingsDestination.route)
            }
        )
        {
            CustomText("Settings", getUIStyle, props = FSProp.Default.toTextProp(fontSize))
            ci.ContentIcon(Icons.Default.Settings, "Main Settings", mdModifier)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @Composable
    fun UserProfile() {
        val fontSize = returnFontSizeBasedOnDp()
        val context = LocalContext.current
        CustomRow(
            onClick = {
                if (cr.name != UserProfileDestination.route) {
                    launchUpdates()
                    navViewModel.updateStartingSBRoute(UserProfileDestination.route)
                    navViewModel.updateRoute(UserProfileDestination.route)
                    navController.navigate(SBNavDestination.route)
                    coroutineScope.launch {
                        val result = supabaseVM.getGoogleId()
                        if (!result.first) showToastMessage(context, result.second)

                    }
                }
            }
        ) {
            CustomText("User Profile", getUIStyle, props = FSProp.Default.toTextProp(fontSize))
            ci.ContentIcon(Icons.Default.AccountCircle, "User Profile", mdModifier)
        }
    }

    private fun updateCards() {
        if (cr.name == ViewDueCardsDestination.route) {
            wd.deck?.let {
                /** If the list is empty, no cards
                 *  have been due even before the user joined,
                 *  or the user finished the deck.
                 */
                println("updating cards!")
                coroutineScope.launch(Dispatchers.IO) { updateDecksCardList(it, cardDeckVM) }
            }
        }
    }

    /** Reset the offset, which keyboard is selected and if it should show. */
    private fun resetKeyboardStuff() {
        if (cr.name == AddCardDestination.route || cr.name == EditingCardDestination.route) {
            navViewModel.resetKeyboardStuff()
        }
    }

    private fun resetSelectionAndQuery() {
        if (cr.name == ViewAllCardsDestination.route) {
            navViewModel.resetSelection()
            navViewModel.resetSearchQuery()
        }
    }

    private fun launchUpdates() {
        resetKeyboardStuff()
        updateCards()
        resetSelectionAndQuery()
    }
}

@Composable
fun CustomRow(
    onClick: () -> Unit, content: @Composable () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .paddingForModal()
    )
    { content() }
}