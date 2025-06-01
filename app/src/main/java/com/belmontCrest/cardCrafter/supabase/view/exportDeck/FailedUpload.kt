package com.belmontCrest.cardCrafter.supabase.view.exportDeck

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.SupabaseViewModel
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.DECK_EXISTS
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.NOT_DECK_OWNER
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.NULL_CARDS
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.NULL_UPDATED_ON
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.STATIC_NUM
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.SUCCESS
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.UPDATED_ON_CONFLICT
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.uiFunctions.CustomText
import com.belmontCrest.cardCrafter.uiFunctions.SubmitButton
import com.belmontCrest.cardCrafter.uiFunctions.showToastMessage
import kotlinx.coroutines.launch


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
        var returnCode by rememberSaveable { mutableIntStateOf(STATIC_NUM) }
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
                if (code == DECK_EXISTS) {
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
                                    } else {
                                        returnCode = it
                                        enabled = true
                                    }
                                }

                            }
                        }, enabled, getUIStyle, "Update"
                    )
                }
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
                            text = "If you are the owner or a co-owner, you can update this deck.",
                            color = getUIStyle.titleColor()
                        )
                    }

                    if (returnCode != STATIC_NUM) {
                        val text = codeToString(returnCode)
                        CustomText(text, getUIStyle)
                    }

                }
            },
            modifier = Modifier.fillMaxWidth(0.95f)
        )
    }
}

@Composable
fun codeToString(code: Int): String = when (code) {
    NOT_DECK_OWNER -> {
        stringResource(R.string.not_owner_or_co_owner)
    }
    UPDATED_ON_CONFLICT -> {
        "The deck has already been updated, please get those changes before uploading."
    }
    NULL_UPDATED_ON -> {
        "There is no internal timestamp to compare, please re-download the deck before uploading"
    }
    NULL_CARDS -> {
        "There is no cards to display. Please pick at least one card to display."
    }
    else -> {
        "Unable to upload this deck.\nTry again later."
    }
}
