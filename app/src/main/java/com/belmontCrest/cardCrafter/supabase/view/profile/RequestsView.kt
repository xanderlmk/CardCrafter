package com.belmontCrest.cardCrafter.supabase.view.profile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.belmontCrest.cardCrafter.model.titledTextProp
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.CoOwnerViewModel
import com.belmontCrest.cardCrafter.supabase.model.RequestStatus
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCoOwnerWithDeck
import com.belmontCrest.cardCrafter.supabase.model.tables.Status
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.boxViewsModifier
import com.belmontCrest.cardCrafter.uiFunctions.CancelButton
import com.belmontCrest.cardCrafter.uiFunctions.CustomText
import com.belmontCrest.cardCrafter.uiFunctions.SubmitButton
import com.belmontCrest.cardCrafter.uiFunctions.showToastMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.Q)
class RequestsView(
    private val getUIStyle: GetUIStyle,
    private val corVM: CoOwnerViewModel
) {
    @Composable
    fun Requests() {
        val coOwnerOf by corVM.coOwnerOf.collectAsStateWithLifecycle()
        var pressed by rememberSaveable { mutableStateOf(false) }
        val coOwner by corVM.coOwner.collectAsStateWithLifecycle()
        Box(
            modifier = Modifier.boxViewsModifier(getUIStyle.getColorScheme()),
            contentAlignment = Alignment.Center
        ) {
            coOwner?.let { coOwnerWithDeck ->
                CoOwnerDialog(coOwnerWithDeck, pressed) { pressed = it }
            }
            LazyColumn(
                contentPadding = PaddingValues(
                    horizontal = 4.dp,
                    vertical = 8.dp
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                items(coOwnerOf.cof) { cwd ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxHeight(.80f)
                            .fillMaxWidth(.95f)
                            .padding(8.dp)
                            .background(
                                color = getUIStyle.secondaryButtonColor(),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(4.dp)
                            .clickable { corVM.updateCoOwner(cwd);pressed = !pressed }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            CustomText(cwd.deckName, getUIStyle)
                            CustomText(cwd.status.name, getUIStyle)
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun CoOwnerDialog(
        coOwner: SBCoOwnerWithDeck, pressed: Boolean, onPressed: (Boolean) -> Unit
    ) {
        val coroutineScope = rememberCoroutineScope()
        var enabled by rememberSaveable { mutableStateOf(true) }
        val status by corVM.status.collectAsStateWithLifecycle()
        val context = LocalContext.current
        LaunchedEffect(status) {
            when (val ss = status) {
                is RequestStatus.Error -> {
                    showToastMessage(context, ss.message)
                    corVM.resetRequestStatus()
                }

                RequestStatus.Idle -> {
                    if (!enabled) {
                        enabled = true
                    }
                }

                RequestStatus.Sent -> {
                    /** Do nothing */
                }

                RequestStatus.Declined -> {
                    showToastMessage(context, "Declined Request")
                    corVM.resetRequestStatus()
                }

                RequestStatus.Accepted -> {
                    showToastMessage(context, "Accepted request")
                    corVM.resetRequestStatus()
                }
            }
        }
        if (pressed) {
            Dialog(
                onDismissRequest = {
                    if (enabled) {
                        onPressed(false)
                    }
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.975f)
                        .background(
                            color = getUIStyle.altBackground(),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (coOwner.status) {
                        Status.Pending -> {
                            PendingView(
                                coOwner, coroutineScope, enabled,
                                onPressed = { onPressed(it) }, onEnabled = { enabled = it }
                            )
                        }

                        Status.Accepted -> {
                            AcceptedView(coOwner)
                        }
                        Status.Declined -> TODO()
                        Status.Revoked -> TODO()
                        Status.Cancelled -> TODO()
                        Status.Expired -> TODO()
                    }
                }
            }
        }
    }

    @Composable
    private fun PendingView(
        coOwner: SBCoOwnerWithDeck, coroutineScope: CoroutineScope,
        enabled: Boolean, onPressed: (Boolean) -> Unit, onEnabled: (Boolean) -> Unit
    ) {
        CustomText(
            "Become a Co-Owner for ${coOwner.deckName}?",
            getUIStyle, Modifier.fillMaxWidth(), titledTextProp()
        )
        SubmitButton(
            onClick = {
                coroutineScope.launch {
                    onEnabled(false); corVM.acceptRequest(coOwner.deckUUID)
                }
            },
            enabled, getUIStyle, "Accept Request"
        )
        CancelButton(onClick = { onPressed(false) }, enabled, getUIStyle)
    }

    @Composable
    private fun AcceptedView(coOwner: SBCoOwnerWithDeck) {
        CustomText("You are a Co-owner of ${coOwner.deckName}", getUIStyle)
    }
}