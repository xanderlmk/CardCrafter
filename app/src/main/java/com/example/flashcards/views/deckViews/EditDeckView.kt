package com.example.flashcards.views.deckViews

import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.flashcards.R
import com.example.flashcards.controller.AppViewModelProvider
import com.example.flashcards.controller.onClickActions.DeleteDeck
import com.example.flashcards.controller.onClickActions.updateDeckName
import com.example.flashcards.controller.onClickActions.updateMultipliers
import com.example.flashcards.controller.onClickActions.updateReviewAmount
import com.example.flashcards.controller.viewModels.deckViewsModels.EditDeckViewModel
import com.example.flashcards.model.uiModels.Fields
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.views.miscFunctions.BackButton
import com.example.flashcards.views.miscFunctions.EditDoubleField
import com.example.flashcards.views.miscFunctions.EditTextField
import com.example.flashcards.ui.theme.GetModifier
import com.example.flashcards.views.miscFunctions.EditIntField
import com.example.flashcards.views.miscFunctions.createDeckDetails
import com.example.flashcards.views.miscFunctions.retrieveDeckDetails
import com.example.flashcards.views.miscFunctions.returnDeckError
import com.example.flashcards.views.miscFunctions.returnMultiplierError
import com.example.flashcards.views.miscFunctions.returnReviewError
import com.example.flashcards.views.miscFunctions.setDeckFields
import kotlinx.coroutines.delay
import kotlin.String

class EditDeckView(
    private var fields: Fields,
    private var getModifier: GetModifier
) {
    @Composable
    fun EditDeck(
        currentName: String, deck: Deck,
        onNavigate: () -> Unit, onDelete: () -> Unit
    ) {
        val vm: EditDeckViewModel = viewModel(factory = AppViewModelProvider.Factory)
        var deckDetails by remember {
            mutableStateOf(
                if (vm.deckIA == true) {
                    retrieveDeckDetails(vm)
                } else {
                    createDeckDetails(deck)
                }
            )
        }
        if (vm.deckIA == false || vm.deckIA == null) {
            setDeckFields(vm, deck, currentName)
        }
        val multiplierErrorMessage = remember { mutableStateOf("") }
        val reviewErrorMessage = remember { mutableStateOf("") }
        val deckErrorMessage = remember { mutableStateOf("") }
        val multiplierSuccessful = remember { mutableStateOf("") }
        val reviewAmountSuccessful = remember { mutableStateOf("") }
        val isSubmitting = remember { mutableStateOf(false) }
        var expanded = rememberSaveable { MutableList(3) { mutableStateOf(false) } }

        val deckErrors = returnDeckError()
        val reviewAmountErrors = returnReviewError()
        val multiplierErrors = returnMultiplierError()
        val updatedMultipliers = stringResource(R.string.updated_multiplier)
        val updatedReview = stringResource(R.string.updated_review)

        val coroutineScope = rememberCoroutineScope()
        val scrollState = rememberScrollState()

        Box(
            modifier = getModifier
                .scrollableBoxViewModifier(scrollState)
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
                    .padding(top = 20.dp, start = 15.dp, end = 15.dp)
                    .fillMaxSize(),
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
                        value = deckDetails.name.value,
                        onValueChanged = {
                            deckDetails.name.value = it
                            vm.updateNameField(it)
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
                                vm.updateNameField(currentName)
                                deckDetails.name.value = currentName
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
                                    vm, deckDetails.name.value,
                                    deckErrorMessage, deckErrors,
                                    currentName, isSubmitting, deck,
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
                            value = deckDetails.gm.value,
                            onValueChanged = {
                                deckDetails.gm.value = it
                                vm.updateGMField(it.toDoubleOrNull()?: 0.0)
                                multiplierErrorMessage.value = "" // Clear error when user types
                            },
                            labelStr = stringResource(R.string.good_multiplier),
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .padding(end = 2.dp)
                        )
                        EditDoubleField(
                            value = deckDetails.bm.value,
                            onValueChanged = {
                                deckDetails.bm.value = it
                                vm.updateBMField(it.toDoubleOrNull()?: 0.0)
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
                                vm.updateBMField(deck.badMultiplier)
                                deckDetails.bm.value = deck.badMultiplier.toString()
                                vm.updateGMField(deck.goodMultiplier)
                                deckDetails.gm.value = deck.goodMultiplier.toString()
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
                                    vm,
                                    deckDetails.gm.value.toDoubleOrNull() ?: deck.goodMultiplier,
                                    deckDetails.bm.value.toDoubleOrNull() ?: deck.badMultiplier,
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
                        ) {
                            Text(stringResource(R.string.edit_review_amount))
                        }
                    }
                } else {
                    EditIntField(
                        value = deckDetails.ra.value,
                        onValueChanged = {
                            deckDetails.ra.value = it
                            vm.updateRAField(it)
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
                                vm.updateRAField(deck.reviewAmount.toString())
                                deckDetails.ra.value = deck.reviewAmount.toString()
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
                                    vm, deckDetails.ra.value.toIntOrNull() ?: 0,
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
                            vm, deck,
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
