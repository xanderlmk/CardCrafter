package com.belmontCrest.cardCrafter.supabase.view.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.belmontCrest.cardCrafter.model.FSProp
import com.belmontCrest.cardCrafter.model.FWProp
import com.belmontCrest.cardCrafter.model.TAProp
import com.belmontCrest.cardCrafter.model.TextProps
import com.belmontCrest.cardCrafter.model.Type
import com.belmontCrest.cardCrafter.model.application.PreferenceValues
import com.belmontCrest.cardCrafter.model.cardListTextProp
import com.belmontCrest.cardCrafter.model.toTextProp
import com.belmontCrest.cardCrafter.model.ui.states.CDetails
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.UserExportedDecksViewModel
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.SUCCESS
import com.belmontCrest.cardCrafter.supabase.model.tables.CoOwnerWithUsername
import com.belmontCrest.cardCrafter.supabase.model.tables.toList
import com.belmontCrest.cardCrafter.supabase.view.importDeck.ImportingDeck
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.borderedModifier
import com.belmontCrest.cardCrafter.ui.theme.boxViewsModifier
import com.belmontCrest.cardCrafter.uiFunctions.CustomText
import com.belmontCrest.cardCrafter.uiFunctions.EditTextField
import com.belmontCrest.cardCrafter.uiFunctions.buttons.PullDeck
import com.belmontCrest.cardCrafter.uiFunctions.buttons.SubmitButton
import com.belmontCrest.cardCrafter.uiFunctions.showToastMessage
import com.belmontCrest.cardCrafter.views.miscFunctions.details.toCardDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class CardListView(
    private val uEDVM: UserExportedDecksViewModel,
    private val getUIStyle: GetUIStyle,
    private val preferences: PreferenceValues
) {
    @Composable
    fun AllCards() {
        val cardList by uEDVM.userCards.collectAsStateWithLifecycle()
        val cardsToDisplay by uEDVM.cardsToDisplay.collectAsStateWithLifecycle()
        val coOwners by uEDVM.coOwners.collectAsStateWithLifecycle()
        var showCards by rememberSaveable { mutableStateOf(false) }
        val scrollState = rememberLazyListState()
        var progress by rememberSaveable { mutableFloatStateOf(0f) }
        var enabled by rememberSaveable { mutableStateOf(true) }
        val isCoOwner by uEDVM.isCoOwner.collectAsStateWithLifecycle()
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current
        val modifier = Modifier
            .padding(vertical = 10.dp, horizontal = 4.dp)
            .fillMaxWidth()
        val ctdModifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
        Box(
            modifier = Modifier
                .boxViewsModifier(getUIStyle.getColorScheme()),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top
            ) {
                if (showCards) {
                    SubmitButton(
                        onClick = { showCards = !showCards }, true, getUIStyle,
                        "Hide cards", modifier
                    )
                    HorizontalDivider(thickness = 2.dp)
                    LazyColumn(
                        contentPadding = PaddingValues(
                            horizontal = 4.dp,
                            vertical = 10.dp
                        ),
                        state = scrollState,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        items(cardList.cts) { card ->
                            val cd = card.toCardDetails()
                            CardView(cd, card.type)
                        }
                    }
                } else {
                    SubmitButton(
                        onClick = { showCards = !showCards }, enabled, getUIStyle,
                        "Show all cards", modifier
                    )
                    HorizontalDivider(thickness = 2.dp)
                    CustomText(
                        "Cards To Display",
                        getUIStyle,
                        ctdModifier,
                        TAProp.Center.toTextProp()
                    )
                    cardsToDisplay.toList().map {
                        val cd = it.toCardDetails()
                        CardView(cd, it.type)
                    }
                    if (isCoOwner) {
                        ListOfOwners(coOwners, enabled) {
                            enabled = it
                        }
                    }
                }
            }
            if (!enabled) {
                ImportingDeck(progress, getUIStyle)
            }
            PullDeck(Modifier.align(Alignment.BottomEnd), getUIStyle) {
                coroutineScope.launch {
                    enabled = false
                    val result = uEDVM.mergeRemoteWithLocal(
                        preferences.reviewAmount, preferences.cardAmount
                    ) { progress = it }

                    if (result != SUCCESS) {
                        showToastMessage(context, "No success.")
                        enabled = true
                    } else {
                        showToastMessage(context, "Success!")
                        enabled = true
                    }
                }
            }
        }
    }

    @Composable
    private fun ListOfOwners(
        coOwners: List<CoOwnerWithUsername>, enabled: Boolean, onEnable: (Boolean) -> Unit
    ) {
        val scrollState = rememberLazyListState()
        var clicked by rememberSaveable { mutableStateOf(false) }
        var username by rememberSaveable { mutableStateOf("") }
        val coroutineScope = rememberCoroutineScope()
        if (coOwners.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .borderedModifier(getUIStyle)
                    .fillMaxWidth()
                    .height(250.dp),
                state = scrollState,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                contentPadding = PaddingValues(
                    horizontal = 4.dp,
                    vertical = 10.dp
                )
            ) {
                items(coOwners) { coOwner ->
                    CustomText("Username: ${coOwner.coOwner.username}", getUIStyle)
                    CustomText("Status: ${coOwner.status.name}", getUIStyle)
                }
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomText(
                "Add an owner", getUIStyle, Modifier
                    .clickable { clicked = !clicked }
                    .fillMaxWidth()
                    .padding(8.dp),
                TextProps(FSProp.Font20, FWProp.Bold, TAProp.Center)
            )
            AddCoOwner(
                clicked, onClicked = { clicked = it }, username, onUsername = { username = it },
                coroutineScope, onEnable, enabled
            )
        }
    }

    @Composable
    private fun AddCoOwner(
        clicked: Boolean, onClicked: (Boolean) -> Unit, username: String,
        onUsername: (String) -> Unit, coroutineScope: CoroutineScope,
        onEnable: (Boolean) -> Unit, enabled: Boolean
    ) {
        val context = LocalContext.current
        if (clicked) {
            Dialog(
                onDismissRequest = {
                    if (enabled) {
                        onClicked(false)
                    }
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(height = 275.dp, width = 10.dp)
                        .background(
                            color = getUIStyle.altBackground(), shape = RoundedCornerShape(20.dp)
                        )
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CustomText("Enter Username", getUIStyle)
                    EditTextField(
                        value = username,
                        onValueChanged = { onUsername(it) },
                        labelStr = "Username", Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                    SubmitButton(
                        onClick = {
                            coroutineScope.launch {
                                onEnable(false)
                                if (username.isBlank()) {
                                    showToastMessage(context, "Username is blank")
                                    onEnable(true)
                                    return@launch
                                }
                                uEDVM.insertCoOwner(username).let {
                                    if (it == SUCCESS) {
                                        showToastMessage(context, "Successfully requested User!")
                                        onUsername("")
                                        uEDVM.getAllCoOwners()
                                    } else {
                                        showToastMessage(context, "Error")
                                    }
                                    onEnable(true)
                                }
                            }
                        }, enabled, getUIStyle, "Add",
                        innerModifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }

    @Composable
    private fun CardView(cd: CDetails, type: String) {
        val modifier = Modifier.padding(6.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .background(
                    color = getUIStyle.secondaryButtonColor(),
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            when (type) {
                Type.BASIC -> {
                    CustomText(cd.question, getUIStyle, modifier, cardListTextProp())
                }

                Type.THREE -> {
                    CustomText(cd.question, getUIStyle, modifier, cardListTextProp())
                }

                Type.HINT -> {
                    CustomText(cd.question, getUIStyle, modifier, cardListTextProp())
                }

                Type.MULTI -> {
                    CustomText(cd.question, getUIStyle, modifier, cardListTextProp())
                }

                Type.NOTATION -> {
                    CustomText(cd.question, getUIStyle, modifier, cardListTextProp())
                }
            }
        }
    }
}