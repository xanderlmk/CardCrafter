package com.example.flashcards.views.deckViews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcards.controller.viewModels.DeckViewModel
import com.example.flashcards.R
import com.example.flashcards.controller.DeleteDeck
import com.example.flashcards.controller.updateDeckName
import com.example.flashcards.controller.updateMultipliers
import com.example.flashcards.controller.updateReviewAmount
import com.example.flashcards.model.uiModels.Fields
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.views.miscFunctions.BackButton
import com.example.flashcards.views.miscFunctions.EditDoubleField
import com.example.flashcards.views.miscFunctions.EditTextField
import com.example.flashcards.ui.theme.GetModifier
import com.example.flashcards.views.miscFunctions.EditIntField
import com.example.flashcards.views.miscFunctions.returnDeckError
import com.example.flashcards.views.miscFunctions.returnMultiplierError
import com.example.flashcards.views.miscFunctions.returnReviewError
import kotlinx.coroutines.delay
import kotlin.String

class EditDeckView(
    private var viewModel: DeckViewModel,
    private var fields: Fields,
    private var getModifier: GetModifier
) {
    @Composable
    fun EditDeck(
        currentName: String, deck: Deck,
        onNavigate: () -> Unit, onDelete: () -> Unit
    ) {
        var newDeckName by remember { mutableStateOf(currentName) }
        var newGoodMultiplier by remember { mutableDoubleStateOf(deck.goodMultiplier) }
        var newBadMultiplier by remember { mutableDoubleStateOf(deck.badMultiplier) }
        var newReviewAmount by remember { mutableStateOf(deck.reviewAmount.toString()) }
        val multiplierErrorMessage = remember { mutableStateOf("") }
        val reviewErrorMessage = remember { mutableStateOf("") }
        val deckErrorMessage = remember { mutableStateOf("") }
        val multiplierSuccessful = remember { mutableStateOf("") }
        val reviewAmountSuccessful = remember { mutableStateOf("") }
        val isSubmitting = remember { mutableStateOf(false) }
        val expanded = remember { List(3) {mutableStateOf(false) }}

        val deckErrors = returnDeckError()
        val reviewAmountErrors = returnReviewError()
        val multiplierErrors = returnMultiplierError()
        val updatedMultipliers = stringResource(R.string.updated_multiplier)
        val updatedReview = stringResource(R.string.updated_review)

        val coroutineScope = rememberCoroutineScope()

        Box(
            modifier = getModifier.boxViewsModifier()
        ) {
            BackButton(
                onBackClick = {
                    onNavigate()
                },
                modifier = getModifier.backButtonModifier(),
                getModifier = getModifier
            )
            Column(
                modifier = Modifier
                    .padding(top = 20.dp, start = 15.dp, end = 15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.edit_deck) + ": $currentName",
                    fontSize = 30.sp,
                    lineHeight = 32.sp,
                    textAlign = TextAlign.Center,
                    color = getModifier.titleColor(),
                    fontWeight = Bold,
                    modifier = Modifier.padding(
                        top = 20.dp,
                        start = 50.dp, end = 50.dp
                    )
                )
                if (!expanded[0].value) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 8.dp,
                                vertical = 12.dp
                            ),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                expanded[0].value = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = getModifier.secondaryButtonColor(),
                                contentColor = getModifier.buttonTextColor()
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.change_deck_name))
                        }
                    }
                } else {
                    EditTextField(
                        value = newDeckName,
                        onValueChanged = {
                            newDeckName = it
                            deckErrorMessage.value = "" // Clear error when user types
                        },
                        labelStr = stringResource(R.string.deck_name),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 10.dp,
                                vertical = 12.dp
                            ),
                    )

                    if (deckErrorMessage.value.isNotEmpty()) {
                        Text(
                            text = deckErrorMessage.value,
                            color = Color.Red,
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Spacer(modifier = Modifier.padding(12.dp))
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                deckErrorMessage.value = ""
                                newDeckName = currentName
                                expanded[0].value = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = getModifier.tertiaryButtonColor(),
                                contentColor = getModifier.onTertiaryButtonColor()
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.cancel))
                        }

                        Button(
                            onClick = {
                                updateDeckName(
                                    viewModel, newDeckName,
                                    deckErrorMessage, deckErrors,
                                    currentName,  isSubmitting, deck,
                                    onNavigate, coroutineScope
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = getModifier.tertiaryButtonColor(),
                                contentColor = getModifier.onTertiaryButtonColor()
                            ),
                            enabled = !isSubmitting.value,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (isSubmitting.value) {
                                CircularProgressIndicator(
                                    color = getModifier.titleColor(),
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text(stringResource(R.string.submit))
                            }
                        }
                    }
                }
                if (!expanded[1].value) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 8.dp,
                                vertical = 12.dp
                            ),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                expanded[1].value = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = getModifier.secondaryButtonColor(),
                                contentColor = getModifier.buttonTextColor()
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.edit_multiplier))
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 10.dp,
                                vertical = 12.dp
                            ),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        EditDoubleField(
                            value = newGoodMultiplier.toString(),
                            onValueChanged = {
                                newGoodMultiplier = it.toDouble()
                                multiplierErrorMessage.value = "" // Clear error when user types
                            },
                            labelStr = stringResource(R.string.good_multiplier),
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .padding(end = 2.dp)
                        )
                        EditDoubleField(
                            value = newBadMultiplier.toString(),
                            onValueChanged = {
                                newBadMultiplier = it.toDouble()
                                multiplierErrorMessage.value = "" // Clear error when user types
                            },
                            labelStr = stringResource(R.string.bad_multiplier),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 2.dp)
                        )
                    }
                    Row {
                        if (multiplierSuccessful.value.isNotEmpty()) {
                            Text(
                                text = multiplierSuccessful.value,
                                color = Color.Red,
                                modifier = Modifier.fillMaxWidth(),
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                        if (multiplierErrorMessage.value.isNotEmpty()) {
                            Text(
                                text = multiplierErrorMessage.value,
                                color = Color.Red,
                                modifier = Modifier.fillMaxWidth(),
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        } else {
                            Spacer(modifier = Modifier.padding(12.dp))
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                multiplierErrorMessage.value = ""
                                newBadMultiplier = deck.badMultiplier
                                newGoodMultiplier = deck.goodMultiplier
                                expanded[1].value = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = getModifier.tertiaryButtonColor(),
                                contentColor = getModifier.onTertiaryButtonColor()
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.cancel))
                        }

                        Button(
                            onClick = {
                                updateMultipliers(
                                    viewModel,
                                    newGoodMultiplier,
                                    newBadMultiplier,
                                    multiplierErrorMessage,
                                    isSubmitting,
                                    deck, multiplierSuccessful,
                                    multiplierErrors, updatedMultipliers,
                                    coroutineScope
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = getModifier.tertiaryButtonColor(),
                                contentColor = getModifier.onTertiaryButtonColor()
                            ),
                            enabled = !isSubmitting.value,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (isSubmitting.value) {
                                CircularProgressIndicator(
                                    color = getModifier.titleColor(),
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text(stringResource(R.string.submit))
                            }
                        }
                    }
                }
                if (!expanded[2].value) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 8.dp,
                                vertical = 12.dp
                            ),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                expanded[2].value = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = getModifier.secondaryButtonColor(),
                                contentColor = getModifier.buttonTextColor()
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 8.dp,
                                    vertical = 12.dp
                                ),
                        ) {
                            Text(stringResource(R.string.edit_review_amount))
                        }
                    }
                } else {
                    EditIntField(
                        value = newReviewAmount,
                        onValueChanged = {
                            newReviewAmount = it
                        },
                        labelStr = stringResource(R.string.review_amount),
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(end = 2.dp)
                            .padding(
                                horizontal = 10.dp,
                                vertical = 12.dp
                            )
                            .align(Alignment.CenterHorizontally)
                    )
                    Row {
                        if (reviewAmountSuccessful.value.isNotEmpty()) {
                            Text(
                                text = reviewAmountSuccessful.value,
                                color = Color.Red,
                                modifier = Modifier.fillMaxWidth(),
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        }

                        if (reviewErrorMessage.value.isNotEmpty()) {
                            Text(
                                text = reviewErrorMessage.value,
                                color = Color.Red,
                                modifier = Modifier.fillMaxWidth(),
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        } else {
                            Spacer(modifier = Modifier.padding(12.dp))
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                reviewErrorMessage.value = ""
                                newReviewAmount = deck.reviewAmount.toString()
                                expanded[2].value = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = getModifier.tertiaryButtonColor(),
                                contentColor = getModifier.onTertiaryButtonColor()
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.cancel))
                        }

                        Button(
                            onClick = {
                                updateReviewAmount(
                                    viewModel, newReviewAmount.toIntOrNull()?: 0,
                                    reviewErrorMessage, isSubmitting, deck,
                                    reviewAmountSuccessful, reviewAmountErrors,
                                    updatedReview, coroutineScope
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = getModifier.tertiaryButtonColor(),
                                contentColor = getModifier.onTertiaryButtonColor()
                            ),
                            enabled = !isSubmitting.value,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (isSubmitting.value) {
                                CircularProgressIndicator(
                                    color = getModifier.titleColor(),
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text(stringResource(R.string.submit))
                            }
                        }
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(50.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        DeleteDeck(
                            viewModel, deck,
                            getModifier, coroutineScope,
                            fields, onDelete
                        )
                    }
                }
            }
        }

        LaunchedEffect(multiplierSuccessful.value) {
            if (multiplierSuccessful.value.isNotBlank()) {
                delay(1500)
                multiplierSuccessful.value = ""
                expanded[1].value = false
            }
        }
        LaunchedEffect(reviewAmountSuccessful.value) {
            if (reviewAmountSuccessful.value.isNotBlank()) {
                delay(1500)
                reviewAmountSuccessful.value = ""
                expanded[2].value = false
            }
        }
    }
}
