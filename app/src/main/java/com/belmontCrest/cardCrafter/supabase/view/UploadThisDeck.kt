package com.belmontCrest.cardCrafter.supabase.view

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.belmontCrest.cardCrafter.model.tablesAndApplication.Deck
import com.belmontCrest.cardCrafter.supabase.controller.SupabaseViewModel
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.CC_LESS_THAN_20
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.DECK_EXISTS
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.boxViewsModifier
import com.belmontCrest.cardCrafter.views.miscFunctions.EditTextFieldNonDone
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun UploadThisDeck(
    dismiss: () -> Unit, deck: Deck,
    supabaseVM: SupabaseViewModel,
    getUIStyle: GetUIStyle
) {
    var enabled by rememberSaveable { mutableStateOf(true) }
    var description by rememberSaveable { mutableStateOf("") }
    var failed = remember { mutableStateOf(false) }
    var success by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var code by remember { mutableIntStateOf(-1) }
    val coroutineScope = rememberCoroutineScope()
    val cts by supabaseVM.sealedAllCTs.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(success) {
        if (success) {
            dismiss()
        }
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .boxViewsModifier(getUIStyle.getColorScheme())
    ) {
        FailedUpload(
            failed, message, code, getUIStyle,
            deck, description, supabaseVM, dismiss
        )
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Upload ${deck.name}?",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = getUIStyle.titleColor(),
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
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
                    .fillMaxHeight(0.25f)
                    .padding(8.dp)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        if (enabled) {
                            dismiss()
                        }
                    }) {
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        if (description.length < 20) {
                            Toast.makeText(
                                context,
                                "Description must be longer!", Toast.LENGTH_SHORT
                            ).show()
                        } else if (cts.allCTs.isEmpty()) {
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
                                    deck, description
                                ).let {
                                    if (it == DECK_EXISTS) {
                                        enabled = true
                                        failed.value = true
                                        code = it
                                        message = "Deck already exists!"
                                    }  else if (it == CC_LESS_THAN_20){
                                        enabled = true
                                        failed.value = true
                                        code = it
                                        message = "Card count less than 20."
                                    }else {
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
                ) { Text("Export") }
            }
        }
    }
}