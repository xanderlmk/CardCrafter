package com.belmontCrest.cardCrafter.supabase.view

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.belmontCrest.cardCrafter.model.tablesAndApplication.Deck
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.mainViewModifier
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.model.tablesAndApplication.CT
import com.belmontCrest.cardCrafter.supabase.controller.SupabaseViewModel
import com.belmontCrest.cardCrafter.views.miscFunctions.EditTextFieldNonDone
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun LocalDecks(
    dismiss: MutableState<Boolean>, deckList: List<Deck>,
    getUIStyle: GetUIStyle, supabaseVM: SupabaseViewModel,
    supabase: SupabaseClient
) {
    val sealedAllCTs by supabaseVM.sealedAllCTs.collectAsStateWithLifecycle()
    if (dismiss.value) {
        val uploadPress = rememberSaveable { mutableStateOf(false) }
        val pickedDeck = rememberSaveable { mutableStateOf<Deck?>(null) }
        LaunchedEffect(pickedDeck.value) {
            pickedDeck.value?.let {
                supabaseVM.getAllCardsForDeck(it.id)
            }
        }
        val coroutineScope = rememberCoroutineScope()
        Dialog(
            onDismissRequest = {
                dismiss.value = false
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(.85f)
                    .fillMaxHeight(.90f)
                    .background(
                        color = getUIStyle.altBackground(),
                        shape = RoundedCornerShape(16.dp)
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                pickedDeck.value?.let {
                    UploadThisDeck(
                        uploadPress, it, sealedAllCTs.allCTs,
                        supabase, supabaseVM, coroutineScope, getUIStyle
                    )
                }
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
                                    pickedDeck.value = deck
                                    uploadPress.value = true
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

@Composable
fun FailedUpload(dismiss: MutableState<Boolean>) {
    if (dismiss.value) {
        AlertDialog(
            onDismissRequest = {
                dismiss.value = false
            },
            dismissButton = {
                Button(onClick = {
                    dismiss.value = false
                }) {
                    Text("Ok")
                }
            },
            confirmButton = {},
            title = {
                Text("Failed!")
            },
            modifier = Modifier.fillMaxWidth(0.75f)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun UploadThisDeck(
    dismiss: MutableState<Boolean>, deck: Deck, cts: List<CT>,
    supabase: SupabaseClient, supabaseVM: SupabaseViewModel,
    coroutineScope: CoroutineScope, getUIStyle: GetUIStyle
) {
    if (dismiss.value) {
        var enabled by rememberSaveable { mutableStateOf(true) }
        var description by rememberSaveable { mutableStateOf("") }
        var failed = remember { mutableStateOf(false) }
        var success by remember { mutableStateOf(false) }
        val context = LocalContext.current
        LaunchedEffect(success) {
            if (success) {
                dismiss.value = false
            }
        }
        FailedUpload(failed)
        AlertDialog(
            onDismissRequest = {
                if (enabled) {
                    dismiss.value = false
                }
            },
            dismissButton = {
                Button(onClick = {
                    if (enabled) {
                        dismiss.value = false
                    }
                }) {
                    Text("Cancel")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (description.length < 20) {
                            Toast.makeText(
                                context,
                                "Description must be longer!", Toast.LENGTH_SHORT
                            ).show()
                        } else if (cts.isEmpty()) {
                            Toast.makeText(
                                context,
                                """
                                    CardList is empty, please wait for it
                                    to load or add at least 20 cards.
                                """.trimIndent(), Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            coroutineScope.launch {
                                enabled = false
                                supabaseVM.exportDeck(
                                    deck, supabase,
                                    cts, description
                                ).let {
                                    if (it > 0) {
                                        enabled = true
                                        failed.value = true
                                    } else {
                                        success = true
                                        Toast.makeText(
                                            context,
                                            "Success!", Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    },
                    enabled = enabled
                ) { Text("Ok") }
            },
            title = {
                Text(
                    text = "Upload ${deck.name}?",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Enter a description",
                        color = getUIStyle.titleColor(),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    EditTextFieldNonDone(
                        value = description,
                        onValueChanged = {
                            description = it
                        },
                        labelStr = "Description",
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.35f)
                            .padding(2.dp)
                    )
                }

            },
            modifier = Modifier.fillMaxWidth(0.85f)
        )
    }
}