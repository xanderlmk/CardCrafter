package com.belmontCrest.cardCrafter.supabase.view.importDeck

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.model.uiModels.PreferencesManager
import com.belmontCrest.cardCrafter.supabase.controller.SupabaseViewModel
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.EMPTY_STRING
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.REPLACED_DECK
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.SUCCESS
import com.belmontCrest.cardCrafter.supabase.model.SBDecks
import com.belmontCrest.cardCrafter.supabase.view.showToastMessage
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.boxViewsModifier
import com.belmontCrest.cardCrafter.uiFunctions.CancelButton
import com.belmontCrest.cardCrafter.uiFunctions.EditTextField
import com.belmontCrest.cardCrafter.uiFunctions.SubmitButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
class ImportDeck(
    private val getUIStyle: GetUIStyle,
    private val supabaseVM: SupabaseViewModel,
    private val preferences: PreferencesManager
) {
    @Composable
    fun GetDeck(deck: SBDecks, onNavigate: () -> Unit) {
        val coroutineScope = rememberCoroutineScope()
        val success = rememberSaveable { mutableIntStateOf(-1) }
        var enabled by rememberSaveable { mutableStateOf(true) }
        val conflict = rememberSaveable { mutableStateOf(false) }
        var progress by rememberSaveable { mutableFloatStateOf(0f) }
        var errorMessage by rememberSaveable { mutableStateOf("") }
        val context = LocalContext.current
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .boxViewsModifier(getUIStyle.getColorScheme())
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                ConflictDeck(conflict, deck, onNavigate)
                Text(
                    text = "Import ${deck.name} ?",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(4.dp)
                )
                Text(
                    text = deck.description,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(4.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SubmitButton(
                        onClick = {
                            coroutineScope.launch {
                                enabled = false
                                success.intValue = supabaseVM.importDeck(
                                    sbDecks = deck,
                                    preferences = preferences,
                                    onProgress = {
                                        progress = it
                                    },
                                    onError = {
                                        errorMessage = it
                                    }
                                )
                                if (success.intValue == SUCCESS) {
                                    Toast.makeText(
                                        context, "Success!", Toast.LENGTH_SHORT
                                    ).show()
                                    onNavigate()
                                } else {
                                    if (success.intValue == ReturnValues.DECK_EXISTS) {
                                        conflict.value = true
                                    }
                                    enabled = true
                                }
                            }
                        }, enabled, getUIStyle, "Ok"
                    )
                    CancelButton(onNavigate, enabled, getUIStyle)
                }
                if (!enabled) {
                    ImportingDeck(progress, getUIStyle)
                }
            }
        }
    }

    @Composable
    fun ConflictDeck(
        dismiss: MutableState<Boolean>,
        deck: SBDecks,
        onNavigate: () -> Unit
    ) {
        val context = LocalContext.current
        if (dismiss.value) {
            var progress by rememberSaveable { mutableFloatStateOf(0f) }
            val coroutineScope = rememberCoroutineScope()
            var enabled by rememberSaveable { mutableStateOf(true) }
            var which by rememberSaveable { mutableStateOf("") }
            var newName by rememberSaveable { mutableStateOf("") }
            var result by rememberSaveable { mutableIntStateOf(-10) }
            var errorMessage by rememberSaveable { mutableStateOf("") }

            LaunchedEffect(result) {
                if (result != -10) {
                    delay(2000)
                    result = -10
                }
            }
            if (!enabled) {
                ImportingDeck(progress, getUIStyle)
            }
            Dialog(
                onDismissRequest = {
                    if (enabled) {
                        dismiss.value = false
                    }
                }
            ) {
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth(.925f)
                        .fillMaxHeight(.90f)
                        .border(
                            width = 4.dp,
                            shape = RoundedCornerShape(18.dp),
                            color = if (getUIStyle.getIsDarkTheme() == true) {
                                Color.Gray
                            } else {
                                Color.Black
                            }
                        )
                        .background(
                            color = getUIStyle.dialogColor(),
                            shape = RoundedCornerShape(18.dp)
                        )
                        .padding(10.dp)
                ) {
                    ConflictDeckMessage()
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SubmitButton(
                            onClick = {
                                coroutineScope.launch {
                                    enabled = false
                                    val thisResult = supabaseVM.replaceDeck(
                                        deck, preferences,
                                        onProgress = {
                                            progress = it
                                        },
                                        onError = {
                                            errorMessage = it
                                        })
                                    result = thisResult.first
                                    if (result == SUCCESS) {
                                        showToastMessage(
                                            context, "Success!",
                                            onNavigate = {
                                                onNavigate()
                                            }, dismiss
                                        )
                                    } else if (result == REPLACED_DECK) {
                                        showToastMessage(
                                            context, "Success, the deck is called:\n" +
                                                    thisResult.second,
                                            onNavigate = {
                                                onNavigate()
                                            }, dismiss
                                        )
                                    } else {
                                        showToastMessage(context, errorMessage)
                                        enabled = true
                                    }
                                }
                            }, enabled, getUIStyle,
                            "Replace"
                        )
                        Button(
                            onClick = {
                                which = "newDeck"
                            }, colors = ButtonDefaults.buttonColors(
                                containerColor = getUIStyle.secondaryButtonColor(),
                                contentColor = getUIStyle.buttonTextColor()
                            )
                        ) {
                            Text("New Deck")
                        }
                    }
                    CancelButton(onNavigate, enabled, getUIStyle)
                    if (which == "newDeck") {
                        Text(
                            "Please provide a new name for ${deck.name}"
                        )
                        EditTextField(
                            value = newName,
                            onValueChanged = {
                                newName = it
                            },
                            labelStr = stringResource(R.string.deck_name),
                            modifier = Modifier
                                .padding(4.dp)
                        )
                        SubmitButton(
                            onClick = {
                                coroutineScope.launch {
                                    if (newName.isBlank()) {
                                        result = EMPTY_STRING
                                        return@launch
                                    }
                                    enabled = false
                                    result = supabaseVM.createNewDeck(
                                        deck, preferences, newName,
                                        onProgress = {
                                            progress = it
                                        },
                                        onError = {
                                            errorMessage = it
                                        }
                                    )
                                    if (result == SUCCESS) {
                                        showToastMessage(
                                            context, "Success!",
                                            onNavigate = {
                                                onNavigate()
                                            }, dismiss
                                        )
                                    } else {
                                        enabled = true
                                    }
                                }
                            }, enabled, getUIStyle,
                            stringResource(R.string.submit)
                        )
                        ImportDeckErrorMessage(result, errorMessage)
                    }
                }
            }
        }
    }
}
