package com.example.flashcards.views.deckViews

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcards.model.tablesAndApplication.Deck
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flashcards.R
import com.example.flashcards.controller.AppViewModelProvider
import com.example.flashcards.controller.viewModels.deckViewsModels.DeckViewModel
import com.example.flashcards.model.tablesAndApplication.CT
import com.example.flashcards.model.uiModels.Fields
import com.example.flashcards.model.uiModels.SealedAllCTs
import com.example.flashcards.supabase.controller.SupabaseViewModel
import com.example.flashcards.views.miscFunctions.AddCardButton
import com.example.flashcards.views.miscFunctions.BackButton
import com.example.flashcards.views.miscFunctions.SettingsButton
import com.example.flashcards.ui.theme.GetModifier
import com.example.flashcards.views.miscFunctions.EditTextFieldNonDone
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class DeckView(
    private var fields: Fields,
    private var getModifier: GetModifier,
    private val supabaseVM: SupabaseViewModel,
) {
    @Composable
    fun ViewEditDeck(
        deck: Deck,
        sealedAllCTs: SealedAllCTs,
        supabase: SupabaseClient,
        onNavigate: () -> Unit,
        goToAddCard: (Int) -> Unit,
        goToDueCards: (Int) -> Unit,
        goToEditDeck: (Int, String) -> Unit,
        goToViewCards: (Int) -> Unit
    ) {
        val deckVM: DeckViewModel = viewModel(factory = AppViewModelProvider.Factory)
        var pressed = rememberSaveable { mutableStateOf(false) }
        val uploadPress = rememberSaveable { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()
        Box(
            modifier = getModifier
                .boxViewsModifier()
        ) {
            UploadThisDeck(
                uploadPress, deck, sealedAllCTs.allCTs,
                supabase, supabaseVM, coroutineScope, getModifier
            )
            ResetDeckDueDate(pressed, deckVM, deck.id, deck.cardAmount)
            BackButton(
                onBackClick = { onNavigate() },
                modifier = getModifier.backButtonModifier(),
                getModifier = getModifier
            )
            SettingsButton(
                onNavigateToEditDeck = {
                    if (!fields.inDeckClicked.value) {
                        fields.inDeckClicked.value = true
                        goToEditDeck(deck.id, deck.name)
                    }
                },
                onNavigateToEditCards = {
                    if (!fields.inDeckClicked.value) {
                        fields.inDeckClicked.value = true
                        goToViewCards(deck.id)
                    }
                },
                exportDeck = {
                    uploadPress.value = true
                },
                clientExists = supabase.auth.currentUserOrNull() != null,
                modifier = getModifier
                    .settingsButtonModifier()
                    .align(Alignment.TopEnd),
                getModifier = getModifier,
                fields = fields
            )
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = deck.name,
                        lineHeight = 42.sp,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                        color = getModifier.titleColor(),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 50.dp)
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(2f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp, start = 20.dp, end = 20.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth(0.55f)
                                .fillMaxHeight(.125f)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = {
                                            if (!fields.inDeckClicked.value) {
                                                fields.inDeckClicked.value = true
                                                goToDueCards(deck.id)
                                            }
                                        },
                                        onLongPress = {
                                            pressed.value = true
                                        }
                                    )
                                }
                                .background(
                                    color = getModifier.secondaryButtonColor(),
                                    shape = RoundedCornerShape(24.dp)
                                ),
                        ) {
                            Text(
                                text = stringResource(R.string.start_deck),
                                color = getModifier.buttonTextColor(),
                                modifier = Modifier
                                    .align(Alignment.Center),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
                Column(
                    modifier = getModifier.addButtonModifier()
                ) {
                    val bottomLeftModifier = Modifier
                        .padding(bottom = 12.dp)
                        .align(Alignment.End)
                    Box(
                        modifier = bottomLeftModifier,
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        AddCardButton(
                            onClick = {
                                if (!fields.inDeckClicked.value) {
                                    fields.inDeckClicked.value = true
                                    goToAddCard(deck.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun ResetDeckDueDate(
        pressed: MutableState<Boolean>,
        deckVM: DeckViewModel,
        deckId: Int, cardAmount: Int
    ) {
        if (pressed.value) {
            Dialog(onDismissRequest = { pressed.value = false }) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.98f)

                ) {
                    Column(
                        modifier = Modifier
                            .padding(vertical = 4.dp, horizontal = 20.dp)
                    ) {
                        Text(
                            text = "Would you like to reset the due date to today?",
                            color = getModifier.titleColor(),
                            modifier = Modifier
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            softWrap = true
                        )
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    pressed.value = false
                                },
                                modifier = Modifier.padding(horizontal = 10.dp)
                            ) {
                                Text("Cancel")
                            }
                            Button(
                                onClick = {
                                    deckVM.updateDueDate(deckId, cardAmount).also {
                                        pressed.value = false
                                    }
                                },
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                            ) {
                                Text("Ok")
                            }
                        }
                    }
                }
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

@Composable
fun UploadThisDeck(
    dismiss: MutableState<Boolean>, deck: Deck, cts: List<CT>,
    supabase: SupabaseClient, supabaseVM: SupabaseViewModel,
    coroutineScope: CoroutineScope, getModifier: GetModifier
) {
    if (dismiss.value) {
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
                dismiss.value = false
            },
            dismissButton = {
                Button(onClick = {
                    dismiss.value = false
                }) {
                    Text("Cancel")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (description.length > 20) {
                            coroutineScope.launch {
                                supabaseVM.insertDeckAndCards(
                                    deck,
                                    supabase,
                                    cts,
                                    description
                                ).let {
                                    if (it > 0) {
                                        failed.value = false
                                    } else {
                                        success = true
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Description must be longer!", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }) {
                    Text("Ok")
                }
            },
            title = {
                Text(
                    text = "Upload ${deck.name}?",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth())
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Enter a description",
                        color = getModifier.titleColor(),
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