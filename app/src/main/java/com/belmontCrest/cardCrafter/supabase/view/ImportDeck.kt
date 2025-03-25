package com.belmontCrest.cardCrafter.supabase.view

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.belmontCrest.cardCrafter.model.uiModels.PreferencesManager
import com.belmontCrest.cardCrafter.supabase.controller.SupabaseViewModel
import com.belmontCrest.cardCrafter.supabase.model.SBDecks
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import kotlinx.coroutines.launch
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.ui.theme.boxViewsModifier
import com.belmontCrest.cardCrafter.views.miscFunctions.EditTextField


private const val EMPTY_STRING = -100

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
        var errorMessage by rememberSaveable { mutableStateOf<String>("") }
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
                ConflictDeck(conflict, deck)
                Text(
                    text = "Import ${deck.name} ?",
                    textAlign = TextAlign.Center
                )
                Row {
                    Button(
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
                                if (success.intValue == 0) {
                                    onNavigate()
                                } else {
                                    if (success.intValue == 100) {
                                        conflict.value = true
                                    }
                                    enabled = true
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = getUIStyle.secondaryButtonColor(),
                            contentColor = getUIStyle.buttonTextColor()
                        ),
                        enabled = enabled
                    ) { Text("OK") }
                    Button(
                        onClick = {
                            onNavigate()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = getUIStyle.secondaryButtonColor(),
                            contentColor = getUIStyle.buttonTextColor()
                        ),
                        enabled = enabled
                    ) { Text(stringResource(R.string.cancel)) }
                }
                if (!enabled) {
                    Dialog(onDismissRequest = {}) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .background(color = Color.Gray),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (errorMessage.isBlank()) {
                                Text(
                                    "Importing Deck...\n" +
                                            "Please do not turn off your device."
                                )
                                Spacer(Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier.fillMaxWidth(),
                                    color = Color.White,
                                    trackColor = Color.Gray,
                                )
                                Spacer(Modifier.height(4.dp))
                                Text("${(progress * 100).toInt()}%")
                            } else {
                                Text("Error: $errorMessage", color = Color.Red)
                                Spacer(Modifier.height(8.dp))
                                Button(onClick = {
                                    errorMessage = ""
                                    enabled = true
                                }) {
                                    Text("Retry")
                                }
                            }
                        }

                    }
                }

            }
        }
    }

    @Composable
    fun ConflictDeck(
        dismiss: MutableState<Boolean>,
        deck: SBDecks,
    ) {
        val context = LocalContext.current
        if (dismiss.value) {
            val coroutineScope = rememberCoroutineScope()
            var loading by rememberSaveable { mutableStateOf(false) }
            var which by rememberSaveable { mutableStateOf("") }
            var newName by rememberSaveable { mutableStateOf("") }
            var result by rememberSaveable { mutableIntStateOf(-1) }
            Dialog(
                onDismissRequest = {
                    if (!loading) {
                        dismiss.value = false
                    }
                }
            ) {
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize(.90f)
                        .border(
                            width = 4.dp,
                            shape = RoundedCornerShape(18.dp),
                            color = if (getUIStyle.getIsDarkTheme() == true) {
                                Color.Gray
                            } else {
                                Color.Black
                            }
                        )
                        .padding(10.dp)
                ) {
                    Text(
                        text = """
                        |A deck with this name or signature already exists!
                        |Would you like to replace it or create a new deck?
                    """.trimMargin(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "(NOTE: If the deck's signature already exist " +
                                "you must replace the deck. " +
                                "You cannot create a new deck.",
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 11.sp
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                which = "replace"
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = getUIStyle.secondaryButtonColor(),
                                contentColor = getUIStyle.buttonTextColor()
                            ),
                            enabled = !loading
                        ) {
                            Text("Replace")
                        }
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
                    Button(
                        onClick = {
                            if (!loading) {
                                dismiss.value = false
                            }
                        }, colors = ButtonDefaults.buttonColors(
                            containerColor = getUIStyle.secondaryButtonColor(),
                            contentColor = getUIStyle.buttonTextColor()
                        ),
                        enabled = !loading
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
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
                                .height(50.dp)
                                .padding(2.dp)
                        )
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    if (newName.isBlank()) {
                                        result = EMPTY_STRING
                                        return@launch
                                    }

                                    result = supabaseVM.createNewDeck(
                                        deck, preferences, newName
                                    )
                                    if (result == 0) {
                                        Toast.makeText(
                                            context, "Success!", Toast.LENGTH_SHORT
                                        ).show()
                                        dismiss.value = false
                                    } else {
                                        loading = false
                                    }
                                }
                            },
                            enabled = !loading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = getUIStyle.secondaryButtonColor(),
                                contentColor = getUIStyle.buttonTextColor()
                            )
                        ) {
                            Text(stringResource(R.string.submit))
                        }
                        if (result == 101) {
                            Text(
                                text = """
                        |Deck signature already exists,
                        |You must replace it.
                    """.trimMargin(),
                                modifier = Modifier.fillMaxWidth(),
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                        } else if (result == 100) {
                            Text(
                                text = """
                        |Name already exists!
                        |Try another name.
                    """.trimMargin(),
                                modifier = Modifier.fillMaxWidth(),
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                        } else if (result == EMPTY_STRING) {
                            Text(
                                text = """
                        |Name can't be empty!
                    """.trimMargin(),
                                modifier = Modifier.fillMaxWidth(),
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                }

            }
        }
    }
}