package com.belmontCrest.cardCrafter.supabase.view.exportDeck

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.mainViewModifier
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.SupabaseViewModel

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun LocalDecks(
    dismiss: MutableState<Boolean>, deckList: List<Deck>,
    getUIStyle: GetUIStyle, supabaseVM: SupabaseViewModel,
    uploadThisDeck: (String) -> Unit
) {
    if (dismiss.value) {
        Dialog(
            onDismissRequest = {
                dismiss.value = false
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(0.9f)
                    .background(
                        color = getUIStyle.altBackground(),
                        shape = RoundedCornerShape(16.dp)
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
                                    supabaseVM.changeDeckId(deck.id)
                                    uploadThisDeck(deck.uuid)
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