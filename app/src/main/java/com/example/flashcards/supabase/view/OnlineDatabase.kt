package com.example.flashcards.supabase.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flashcards.supabase.controller.SupabaseViewModel
import com.example.flashcards.ui.theme.GetModifier
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth

class OnlineDatabase(
    private val supabase: SupabaseClient,
    private val getModifier: GetModifier,
    private val supabaseVM: SupabaseViewModel
) {
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @Composable
    fun SupabaseView(onNavigate: () -> Unit) {
        val deckList by supabaseVM.deckList.collectAsStateWithLifecycle()
        LaunchedEffect(Unit) {
            supabaseVM.getDeckList(supabase)
        }
        if (supabase.auth.currentUserOrNull() == null) {
            SignUp(supabase, supabaseVM, getModifier)
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(
                        horizontal = 4.dp,
                        vertical = 8.dp
                    )
                ) {
                    items(deckList.list) { deck ->
                        Text(deck.name)
                    }
                }
                Button(
                    onClick = { onNavigate() }) {
                    Text("Return")
                }
            }
        }
    }
}