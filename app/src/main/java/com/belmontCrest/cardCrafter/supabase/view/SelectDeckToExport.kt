package com.belmontCrest.cardCrafter.supabase.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.mainViewModifier
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.SupabaseViewModel
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.CC_LESS_THAN_20
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.DECK_EXISTS
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.NOT_DECK_OWNER
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.SUCCESS
import com.belmontCrest.cardCrafter.uiFunctions.SubmitButton
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun LocalDecks(
    dismiss: MutableState<Boolean>, deckList: List<Deck>,
    getUIStyle: GetUIStyle, supabaseVM: SupabaseViewModel,
    uploadThisDeck: () -> Unit
) {
    if (dismiss.value) {
        Dialog(
            onDismissRequest = {
                dismiss.value = false
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(0.9f)
                    .background(
                        color = getUIStyle.altBackground(),
                        shape = RoundedCornerShape(16.dp)
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(
                        vertical = 24.dp,
                        horizontal = 6.dp
                    )
                ) {
                    items(deckList) { deck ->
                        Text(
                            text = deck.name,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(4.dp)
                                .mainViewModifier(getUIStyle.getColorScheme())
                                .clickable {
                                    supabaseVM.changeDeckId(deck.id)
                                    uploadThisDeck()
                                }
                        )
                    }
                }
                Button(
                    onClick = {
                        dismiss.value = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = getUIStyle.secondaryButtonColor(),
                        contentColor = getUIStyle.buttonTextColor()
                    )
                ) { Text(stringResource(R.string.cancel)) }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun FailedUpload(
    dismiss: MutableState<Boolean>, message: String,
    code: Int, getUIStyle: GetUIStyle, deck: Deck, description: String,
    supabaseVM: SupabaseViewModel, onSuccess: () -> Unit
) {
    if (dismiss.value) {
        val coroutineScope = rememberCoroutineScope()
        var enabled by rememberSaveable { mutableStateOf(true) }
        val context = LocalContext.current
        AlertDialog(
            onDismissRequest = {
                if (enabled) {
                    dismiss.value = false
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        dismiss.value = false
                    }, enabled = enabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = getUIStyle.secondaryButtonColor(),
                        contentColor = getUIStyle.buttonTextColor()
                    )
                ) {
                    Text("Return")
                }
            },
            confirmButton = {
                SubmitButton(
                    onClick = {
                        coroutineScope.launch {
                            enabled = false
                            supabaseVM.updateExportedDeck(
                                deck, description
                            ).let {
                                if (it == SUCCESS) {
                                    showToastMessage(
                                        context, "Success!",
                                        onNavigate = {
                                            onSuccess()
                                        }, dismiss
                                    )
                                } else if (it == NOT_DECK_OWNER) {
                                    showToastMessage(
                                        context,
                                        "You are not the owner of this deck.",
                                        dismiss = dismiss
                                    )
                                } else if (it == CC_LESS_THAN_20) {
                                    showToastMessage(
                                        context,
                                        "Card count is less than 20.",
                                        dismiss = dismiss
                                    )
                                } else {
                                    showToastMessage(
                                        context,
                                        "Unable to upload this deck.\n" +
                                                "Try again later.",
                                        dismiss = dismiss
                                    )
                                }
                            }

                        }
                    }, enabled, getUIStyle, "Update"
                )
            },
            title = {
                Text("Failed!")
            },
            text = {
                Column {
                    Text(
                        text = message,
                        color = getUIStyle.titleColor()
                    )
                    if (code == DECK_EXISTS) {
                        Text(
                            text = "If you are the owner, you can update this deck.",
                            color = getUIStyle.titleColor()
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(0.85f)
        )
    }
}
