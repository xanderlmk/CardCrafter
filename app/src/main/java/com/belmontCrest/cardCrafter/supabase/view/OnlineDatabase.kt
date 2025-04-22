package com.belmontCrest.cardCrafter.supabase.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.SupabaseViewModel
import com.belmontCrest.cardCrafter.supabase.model.tables.SBDeckDto
import com.belmontCrest.cardCrafter.supabase.view.authViews.CreateAccount
import com.belmontCrest.cardCrafter.supabase.view.authViews.SignUp
import com.belmontCrest.cardCrafter.supabase.view.uploadDeck.LocalDecks
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.boxViewsModifier
import com.belmontCrest.cardCrafter.uiFunctions.ExportDeckButton
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

class OnlineDatabase(
    private val getUIStyle: GetUIStyle,
    private val supabaseVM: SupabaseViewModel,
    private val localDeckList: List<Deck>
) {
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @Composable
    fun SupabaseView(
        onImportDeck: (String) -> Unit,
        onExportDeck: (String) -> Unit,
        onUseEmail: () -> Unit
    ) {
        val currentUser by supabaseVM.currentUser.collectAsStateWithLifecycle()
        val deckList by supabaseVM.deckList.collectAsStateWithLifecycle()
        val owner by supabaseVM.owner.collectAsStateWithLifecycle()
        var pressed = rememberSaveable { mutableStateOf(false) }

        var refreshing by rememberSaveable { mutableStateOf(false) }
        LaunchedEffect(refreshing) {
            if (refreshing) {
                supabaseVM.getDeckList()
                refreshing = false
            }
        }
        LaunchedEffect(deckList) {
            if (deckList.list.isEmpty()) {
                supabaseVM.getDeckList()
            }
        }
        LaunchedEffect(currentUser) {
            if (currentUser == null) {
                supabaseVM.updateStatus()
            }
        }
        LaunchedEffect(owner) {
            if (owner == null) {
                supabaseVM.getOwner()
            }
        }
        if (currentUser == null) {
            SignUp(supabaseVM, getUIStyle, onUseEmail) {
                refreshing = it
            }
        } else {
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = refreshing),
                onRefresh = { refreshing = true },
            ) {
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
                            DeckView(deck, onImportDeck)
                        }
                    }
                    ExportDeckButton(
                        onClick = { pressed.value = true },
                        modifier = Modifier.align(Alignment.BottomEnd)
                    )
                }
            }
        }
    }

    @Composable
    private fun DeckView(deck: SBDeckDto, onImportDeck: (String) -> Unit) {
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
                .clickable {
                    onImportDeck(deck.deckUUID)
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()

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
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    drawLine(
                        start = Offset(x = 0f, y = 0f),
                        end = Offset(x = size.width, y = 0f),
                        color = getUIStyle.titleColor(),
                        strokeWidth = 5f
                    )
                }
                Text(
                    text = deck.description,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    fontSize = 20.sp,
                    color = getUIStyle.titleColor(),
                )
            }
        }
    }
}
