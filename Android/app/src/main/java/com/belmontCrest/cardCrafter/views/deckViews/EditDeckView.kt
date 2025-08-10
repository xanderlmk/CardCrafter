package com.belmontCrest.cardCrafter.views.deckViews

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.model.application.AppVMProvider
import com.belmontCrest.cardCrafter.controller.onClickActions.DeleteDeck
import com.belmontCrest.cardCrafter.controller.onClickActions.updateCardAmount
import com.belmontCrest.cardCrafter.controller.onClickActions.updateDeckName
import com.belmontCrest.cardCrafter.controller.onClickActions.updateMultipliers
import com.belmontCrest.cardCrafter.controller.onClickActions.updateReviewAmount
import com.belmontCrest.cardCrafter.controller.view.models.deckViewsModels.EditDeckViewModel
import com.belmontCrest.cardCrafter.model.ui.Fields
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.uiFunctions.EditDoubleField
import com.belmontCrest.cardCrafter.uiFunctions.EditTextField
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.scrollableBoxViewModifier
import com.belmontCrest.cardCrafter.uiFunctions.EditIntField
import com.belmontCrest.cardCrafter.views.misc.details.createDeckDetails
import com.belmontCrest.cardCrafter.views.misc.returnCardAmountError
import com.belmontCrest.cardCrafter.views.misc.returnDeckError
import com.belmontCrest.cardCrafter.views.misc.returnMultiplierError
import com.belmontCrest.cardCrafter.views.misc.returnReviewError
import kotlinx.coroutines.delay
import kotlin.String

class EditDeckView(
    private var fields: Fields,
    private var getUIStyle: GetUIStyle
) {
    @RequiresApi(Build.VERSION_CODES.Q)
    @Composable
    fun EditDeck(
        currentName: String, deck: Deck,
        onNavigate: () -> Unit, onDelete: () -> Unit
    ) {
        val vm: EditDeckViewModel = viewModel(factory = AppVMProvider.Factory)
        val deckDetails = createDeckDetails(deck)

        val multiplierErrorMessage = remember { mutableStateOf("") }
        val reviewErrorMessage = remember { mutableStateOf("") }
        val deckErrorMessage = remember { mutableStateOf("") }
        val cardAmountErrorMessage = remember { mutableStateOf("") }

        val multiplierSuccessful = remember { mutableStateOf("") }
        val reviewAmountSuccessful = remember { mutableStateOf("") }
        val cardAmountSuccessful = remember { mutableStateOf("") }
        val isSubmitting = remember { mutableStateOf(false) }
        val expanded = rememberSaveable { MutableList(4) { mutableStateOf(false) } }

        val deckErrors = returnDeckError()
        val reviewAmountErrors = returnReviewError()
        val multiplierErrors = returnMultiplierError()
        val cardAmountErrors = returnCardAmountError()

        val updatedMultipliers = stringResource(R.string.updated_multiplier)
        val updatedReview = stringResource(R.string.updated_review)
        val updatedCardAmount = stringResource(R.string.updated_card_amount)

        val coroutineScope = rememberCoroutineScope()
        val scrollState = rememberScrollState()

        Box(
            modifier = Modifier
                .scrollableBoxViewModifier(scrollState, getUIStyle.getColorScheme())
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 15.dp, end = 15.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!expanded[0].value) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                vertical = 12.dp
                            ),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                expanded[0].value = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = getUIStyle.secondaryButtonColor(),
                                contentColor = getUIStyle.buttonTextColor()
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
                                deckDetails.name.value = currentName
                                expanded[0].value = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = getUIStyle.tertiaryButtonColor(),
                                contentColor = getUIStyle.onTertiaryButtonColor()
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
                                containerColor = getUIStyle.tertiaryButtonColor(),
                                contentColor = getUIStyle.onTertiaryButtonColor()
                            ),
                            enabled = !isSubmitting.value,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (isSubmitting.value) {
                                CircularProgressIndicator(
                                    color = getUIStyle.titleColor(),
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
                                vertical = 12.dp
                            ),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                expanded[1].value = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = getUIStyle.secondaryButtonColor(),
                                contentColor = getUIStyle.buttonTextColor()
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
                                deckDetails.bm.value = deck.badMultiplier.toString()
                                deckDetails.gm.value = deck.goodMultiplier.toString()
                                expanded[1].value = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = getUIStyle.tertiaryButtonColor(),
                                contentColor = getUIStyle.onTertiaryButtonColor()
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
                                containerColor = getUIStyle.tertiaryButtonColor(),
                                contentColor = getUIStyle.onTertiaryButtonColor()
                            ),
                            enabled = !isSubmitting.value,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (isSubmitting.value) {
                                CircularProgressIndicator(
                                    color = getUIStyle.titleColor(),
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
                                vertical = 12.dp
                            ),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                expanded[2].value = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = getUIStyle.secondaryButtonColor(),
                                contentColor = getUIStyle.buttonTextColor()
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
                            reviewErrorMessage.value = ""
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
                                deckDetails.ra.value = deck.reviewAmount.toString()
                                expanded[2].value = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = getUIStyle.tertiaryButtonColor(),
                                contentColor = getUIStyle.onTertiaryButtonColor()
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
                                containerColor = getUIStyle.tertiaryButtonColor(),
                                contentColor = getUIStyle.onTertiaryButtonColor()
                            ),
                            enabled = !isSubmitting.value,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (isSubmitting.value) {
                                CircularProgressIndicator(
                                    color = getUIStyle.titleColor(),
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text(stringResource(R.string.submit))
                            }
                        }
                    }
                }
                if (!expanded[3].value) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                vertical = 12.dp
                            ),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                expanded[3].value = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = getUIStyle.secondaryButtonColor(),
                                contentColor = getUIStyle.buttonTextColor()
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Change Daily Card Amount")
                        }
                    }
                } else {
                    EditIntField(
                        value = deckDetails.ca.value,
                        onValueChanged = {
                            deckDetails.ca.value = it
                            cardAmountErrorMessage.value = ""
                        },
                        labelStr = "cardAmount",
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(end = 2.dp)
                            .padding(
                                horizontal = 10.dp,
                                vertical = 12.dp
                            )
                    )
                    Row {
                        if (cardAmountSuccessful.value.isNotEmpty()) {
                            Text(
                                text = cardAmountSuccessful.value,
                                color = Color.Red,
                                modifier = Modifier.fillMaxWidth(),
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                        if (cardAmountErrorMessage.value.isNotEmpty()) {
                            Text(
                                text = cardAmountErrorMessage.value,
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
                                cardAmountErrorMessage.value = ""
                                deckDetails.ca.value = deck.cardAmount.toString()
                                expanded[3].value = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = getUIStyle.tertiaryButtonColor(),
                                contentColor = getUIStyle.onTertiaryButtonColor()
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.cancel))
                        }

                        Button(
                            onClick = {
                                updateCardAmount(
                                    vm, deckDetails.ca.value.toIntOrNull() ?: 0,
                                    cardAmountErrorMessage, isSubmitting, deck,
                                    cardAmountSuccessful, cardAmountErrors,
                                    updatedCardAmount, coroutineScope
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = getUIStyle.tertiaryButtonColor(),
                                contentColor = getUIStyle.onTertiaryButtonColor()
                            ),
                            enabled = !isSubmitting.value,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (isSubmitting.value) {
                                CircularProgressIndicator(
                                    color = getUIStyle.titleColor(),
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
                            getUIStyle, coroutineScope,
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
        LaunchedEffect(cardAmountSuccessful.value) {
            if (cardAmountSuccessful.value.isNotEmpty()) {
                delay(1500)
                cardAmountSuccessful.value = ""
                expanded[3].value = false
            }
        }
    }
}
