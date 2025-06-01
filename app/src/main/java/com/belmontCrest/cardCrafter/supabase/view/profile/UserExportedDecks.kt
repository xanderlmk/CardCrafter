package com.belmontCrest.cardCrafter.supabase.view.profile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.SupabaseViewModel
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.UserExportedDecksViewModel
import com.belmontCrest.cardCrafter.supabase.model.tables.SBDeckDto
import com.belmontCrest.cardCrafter.supabase.view.authViews.CreateAccount
import com.belmontCrest.cardCrafter.supabase.view.exportDeck.LocalDecks
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.boxViewsModifier
import com.belmontCrest.cardCrafter.uiFunctions.ExportDeckButton

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun UserExportedDecks(
    getUIStyle: GetUIStyle, uEDVM: UserExportedDecksViewModel,
    onNavigate: (String) -> Unit, onExportDeck: (String) -> Unit,
    localDeckList: List<Deck>,supabaseVM: SupabaseViewModel
) {
    val deckList by uEDVM.allDecks.collectAsStateWithLifecycle()
    val isLoading by uEDVM.isLoading.collectAsStateWithLifecycle()
    var pressed = rememberSaveable { mutableStateOf(false) }
    val owner by supabaseVM.owner.collectAsStateWithLifecycle()

    LaunchedEffect(owner) {
        if (owner == null) {
            supabaseVM.getOwner()
        }
    }
    if (isLoading) {
        Box(
            modifier = Modifier.boxViewsModifier(getUIStyle.getColorScheme()),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = getUIStyle.titleColor()
            )
        }
    } else {
        Box(
            modifier = Modifier
                .boxViewsModifier(getUIStyle.getColorScheme()),
            contentAlignment = Alignment.TopCenter
        ) {
            if (owner != null) {
                LocalDecks(
                    pressed, localDeckList, getUIStyle,
                    supabaseVM, onExportDeck
                )
            } else {
                CreateAccount(
                    supabaseVM, pressed, getUIStyle
                )
            }
            LazyColumn(
                contentPadding = PaddingValues(
                    horizontal = 4.dp,
                    vertical = 8.dp
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                items(deckList.list) { deck ->
                    DeckView(deck, getUIStyle, onNavigate)
                }
            }
            ExportDeckButton(
                onClick = { pressed.value = true },
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}

@Composable
private fun DeckView(deck: SBDeckDto, getUIStyle: GetUIStyle, onNavigate: (String) -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(8.dp)
            .background(
                color = getUIStyle.secondaryButtonColor(),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(4.dp)
            .clickable {
                onNavigate(deck.deckUUID)
            }) {
        Text(
            text = deck.name,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = getUIStyle.titleColor(),
        )
    }
}