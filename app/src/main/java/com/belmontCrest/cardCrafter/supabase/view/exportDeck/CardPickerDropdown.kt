package com.belmontCrest.cardCrafter.supabase.view.exportDeck

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.belmontCrest.cardCrafter.controller.cardHandlers.getCardType
import com.belmontCrest.cardCrafter.controller.cardHandlers.toCard
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.model.Type
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.SupabaseViewModel
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.views.miscFunctions.details.toCardDetails


@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun CardPickerDropdown(
    getUIStyle: GetUIStyle,
    supabaseVM: SupabaseViewModel, modifier: Modifier
) {
    val cts by supabaseVM.sealedAllCTs.collectAsStateWithLifecycle()
    var expanded by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCT by rememberSaveable { mutableStateOf<CT?>(null) }
    val charMap = mapOf<Int, Char>(0 to 'a', 1 to 'b', 2 to 'c', 3 to 'd')

    Box(modifier = modifier) {
        // Trigger button
        IconButton(onClick = { expanded = true }) {
            Icon(
                Icons.Default.MoreVert, contentDescription = "Pick a card",
                tint = getUIStyle.defaultIconColor()
            )
        }

        // The dropdown menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // 1) Search field at top
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search…", color = getUIStyle.defaultIconColor()) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
            )
            HorizontalDivider()

            // 2) Filtered list of card‑types
            val filtered = cts.allCTs.filter { ct ->
                if (searchQuery.isBlank()) {
                    return@filter true
                }
                val q = when (ct) {
                    is CT.Basic -> ct.basicCard.question
                    is CT.Hint -> ct.hintCard.question
                    is CT.ThreeField -> ct.threeFieldCard.question
                    is CT.MultiChoice -> ct.multiChoiceCard.question
                    is CT.Notation -> ct.notationCard.question
                }
                q.contains(searchQuery, ignoreCase = true)
            }

            if (filtered.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("No matches", style = MaterialTheme.typography.bodySmall) },
                    onClick = { /* nothing */ },
                    enabled = false
                )
            } else {
                filtered.map { ct ->
                    val preview = when (ct) {
                        is CT.Basic -> ct.basicCard.question
                        is CT.Hint -> ct.hintCard.question
                        is CT.ThreeField -> ct.threeFieldCard.question
                        is CT.MultiChoice -> ct.multiChoiceCard.question
                        is CT.Notation -> ct.notationCard.question
                    }
                    DropdownMenuItem(
                        text = {
                            Text(
                                preview,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        onClick = {
                            selectedCT = ct
                        }
                    )
                }
            }
        }
    }

    // 3) Detail dialog when you pick one
    selectedCT?.let { ct ->
        val cardDetails = ct.toCardDetails()
        AlertDialog(
            onDismissRequest = { selectedCT = null },
            title = { Text("Card Details") },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    CustomDialogText("Q: ${cardDetails.question.value}", getUIStyle)
                    Spacer(Modifier.height(8.dp))
                    if (ct.getCardType() == Type.HINT || ct.getCardType() == Type.THREE) {
                        val label = if (ct.getCardType() == Type.HINT) "Hint" else "Middle"
                        CustomDialogText("$label: ${cardDetails.middleField.value}", getUIStyle)
                        Spacer(Modifier.height(8.dp))
                    }
                    if (ct.getCardType() == Type.MULTI) {
                        cardDetails.choices.mapIndexed { index, it ->
                            if (it.value.isNotBlank()) {
                                val letter = charMap[index] ?: '?'
                                CustomDialogText("$letter. ${it.value}", getUIStyle)
                                Spacer(Modifier.height(4.dp))
                            }
                        }
                        CustomDialogText("Correct: ${cardDetails.correct.value}", getUIStyle)
                    }
                    if (ct.getCardType() == Type.NOTATION) {
                        cardDetails.stringList.mapIndexed { index, it ->
                            CustomDialogText("Step ${index + 1}) ${it.value}", getUIStyle)
                            Spacer(Modifier.height(4.dp))
                        }
                    }
                    if (ct.getCardType() != Type.MULTI) {
                        CustomDialogText("A: ${cardDetails.answer.value}", getUIStyle)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // 4) Slot the card ID into the next free spot
                        supabaseVM.addCardsToDisplay(ct.toCard().cardIdentifier)
                        selectedCT = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = getUIStyle.secondaryButtonColor(),
                        contentColor = getUIStyle.buttonTextColor()
                    )
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { selectedCT = null }, colors = ButtonDefaults.buttonColors(
                        containerColor = getUIStyle.secondaryButtonColor(),
                        contentColor = getUIStyle.buttonTextColor()
                    )
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun CustomDialogText(text: String, getUIStyle: GetUIStyle) {
    Text(text = text, color = getUIStyle.titleColor())
}

