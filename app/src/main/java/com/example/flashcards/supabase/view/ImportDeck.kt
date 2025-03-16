package com.example.flashcards.supabase.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.flashcards.model.uiModels.PreferencesManager
import com.example.flashcards.supabase.controller.SupabaseViewModel
import com.example.flashcards.supabase.model.SBDecks
import com.example.flashcards.ui.theme.GetUIStyle
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.launch
import com.example.flashcards.R
import com.example.flashcards.ui.theme.boxViewsModifier

class ImportDeck(
    private val supabase: SupabaseClient,
    private val getUIStyle: GetUIStyle,
    private val supabaseViewModel: SupabaseViewModel,
    private val preferences: PreferencesManager
) {
    @RequiresApi(Build.VERSION_CODES.Q)
    @Composable
    fun GetDeck(deck: SBDecks, onNavigate: () -> Unit) {
        val coroutineScope = rememberCoroutineScope()
        val success = rememberSaveable { mutableIntStateOf(-1) }
        var enabled by rememberSaveable { mutableStateOf(true) }

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
                Text(
                    text = "Import ${deck.name} ?",
                    textAlign = TextAlign.Center
                )
                Row {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                enabled = false
                                success.intValue = supabaseViewModel.importDeck(
                                    sbDecks = deck,
                                    supabase = supabase,
                                    preferences = preferences
                                )
                                if (success.intValue == 0) {
                                    onNavigate()
                                } else {
                                    enabled = true
                                }
                            }
                        },
                        enabled = enabled
                    ) { Text("OK") }
                    Button(
                        onClick = {
                            onNavigate()
                        }
                    ) { Text(stringResource(R.string.cancel)) }
                }

            }
        }
    }
}