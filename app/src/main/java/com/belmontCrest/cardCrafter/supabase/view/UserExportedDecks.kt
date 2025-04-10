package com.belmontCrest.cardCrafter.supabase.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.belmontCrest.cardCrafter.controller.AppViewModelProvider
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.UserExportedDecksViewModel
import com.belmontCrest.cardCrafter.supabase.model.tables.SBDeckDto
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.boxViewsModifier

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun UserExportedDecks(getUIStyle: GetUIStyle) {
    val uEDVM: UserExportedDecksViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val deckList by uEDVM.userExportedDecks.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        uEDVM.getUserDeckList()
    }
    Box(
        modifier = Modifier
            .boxViewsModifier(getUIStyle.getColorScheme()),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            contentPadding = PaddingValues(
                horizontal = 4.dp,
                vertical = 8.dp
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(deckList.list) { deck ->
                DeckView(deck, getUIStyle)
            }
        }
    }
}
@Composable
private fun DeckView(deck: SBDeckDto, getUIStyle: GetUIStyle) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxHeight(.80f)
            .fillMaxWidth(.95f)
            .padding(8.dp)
            .background(
                color = getUIStyle.secondaryButtonColor(),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(4.dp)
    ) {
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