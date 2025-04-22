package com.belmontCrest.cardCrafter.supabase.view.uploadDeck

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.belmontCrest.cardCrafter.controller.cardHandlers.getCardType
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.model.FSProp
import com.belmontCrest.cardCrafter.model.FWProp
import com.belmontCrest.cardCrafter.model.TAProp
import com.belmontCrest.cardCrafter.model.TextProps
import com.belmontCrest.cardCrafter.model.Type
import com.belmontCrest.cardCrafter.model.toTextProp
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.SupabaseViewModel
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.CC_LESS_THAN_20
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.DECK_EXISTS
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.NULL_CARDS
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.SUCCESS
import com.belmontCrest.cardCrafter.supabase.model.tables.isThereCards
import com.belmontCrest.cardCrafter.uiFunctions.showToastMessage
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.scrollableBoxViewModifier
import com.belmontCrest.cardCrafter.uiFunctions.CancelButton
import com.belmontCrest.cardCrafter.uiFunctions.CustomText
import com.belmontCrest.cardCrafter.uiFunctions.EditTextFieldNonDone
import com.belmontCrest.cardCrafter.uiFunctions.SubmitButton
import com.belmontCrest.cardCrafter.views.miscFunctions.details.toCardDetails
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun UploadThisDeck(
    dismiss: () -> Unit, deck: Deck, supabaseVM: SupabaseViewModel, getUIStyle: GetUIStyle
) {
    var enabled by rememberSaveable { mutableStateOf(true) }
    var description by rememberSaveable { mutableStateOf("") }
    var failed = remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var code by remember { mutableIntStateOf(-1) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val selectedCards by supabaseVM.selectedCards.collectAsStateWithLifecycle()
    val defaultModifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.scrollableBoxViewModifier(
            rememberScrollState(),
            getUIStyle.getColorScheme()
        )
    ) {
        FailedUpload(
            failed, message, code, getUIStyle, deck, description, supabaseVM, dismiss
        )
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            CustomText(
                "Upload ${deck.name}?", getUIStyle, defaultModifier,
                TextProps(FSProp.Font22, FWProp.Bold, TAProp.Center)
            )
            HorizontalDivider()
            CustomText(
                "Selected cards", getUIStyle, defaultModifier,
                TextProps(FSProp.Font20, FWProp.SemiBold, TAProp.Center)
            )
            if (selectedCards.isThereCards()) {
                selectedCards.first?.let { ShowCardDetails(it, getUIStyle) }
                selectedCards.second?.let { ShowCardDetails(it, getUIStyle) }
                selectedCards.third?.let { ShowCardDetails(it, getUIStyle) }
                selectedCards.fourth?.let { ShowCardDetails(it, getUIStyle) }
            } else {
                CustomText(
                    "No cards selected", getUIStyle,
                    defaultModifier, TAProp.Center.toTextProp()
                )

            }
            HorizontalDivider()
            CustomText(
                "Enter a description", getUIStyle,
                defaultModifier, TAProp.Center.toTextProp()
            )
            EditTextFieldNonDone(
                value = description, onValueChanged = {
                    description = it
                }, labelStr = "Description", modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()
            ) {
                CancelButton(
                    onClick = {
                        dismiss()
                    }, enabled, getUIStyle
                )
                SubmitButton(
                    onClick = {
                        if (description.length < 20) {
                            showToastMessage(context, "Description must be longer!")
                        } else {
                            coroutineScope.launch {
                                enabled = false
                                supabaseVM.exportDeck(
                                    deck, description
                                ).let {
                                    if (it == SUCCESS) {
                                        showToastMessage(
                                            context, "Success!", onNavigate = { dismiss() })
                                    } else if (it == DECK_EXISTS) {
                                        code = it; enabled = true; failed.value = true
                                        message = "Deck already exists!"
                                    } else if (it == CC_LESS_THAN_20) {
                                        code = it; enabled = true; failed.value = true
                                        message = "Card count less than 20."
                                    } else if (it == NULL_CARDS) {
                                        code = it; enabled = true; failed.value = true
                                        message = "Please select at least one card to display"
                                    } else {
                                        code = it; enabled = true; failed.value = true
                                        message = "Something went wrong."
                                    }
                                }
                            }
                        }
                    }, enabled, getUIStyle, "Export"
                )
            }

        }
    }
}

@Composable
private fun ShowCardDetails(ct: CT, getUIStyle: GetUIStyle) {
    val defaultModifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp, horizontal = 6.dp)
    var showDetails by rememberSaveable { mutableStateOf(false) }
    val charMap = mapOf<Int, Char>(0 to 'a', 1 to 'b', 2 to 'c', 3 to 'd')
    val cardDetails = ct.toCardDetails()
    Column(
        modifier = Modifier
            .border(2.dp, getUIStyle.defaultIconColor(), RoundedCornerShape(12.dp)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showDetails) {
            CustomText("Q: ${cardDetails.question.value}", getUIStyle, defaultModifier)
            if (ct.getCardType() == Type.HINT || ct.getCardType() == Type.THREE) {
                val label = if (ct.getCardType() == Type.HINT) "Hint" else "Middle"
                CustomText("$label: ${cardDetails.middleField.value}", getUIStyle, defaultModifier)
            }
            if (ct.getCardType() == Type.MULTI) {
                cardDetails.choices.mapIndexed { index, it ->
                    if (it.value.isNotBlank()) {
                        val letter = charMap[index] ?: '?'
                        CustomText("$letter. ${it.value}", getUIStyle, defaultModifier)
                    }
                }
                CustomText("Correct: ${cardDetails.correct.value}", getUIStyle, defaultModifier)
            }
            if (ct.getCardType() == Type.NOTATION) {
                cardDetails.stringList.mapIndexed { index, it ->
                    CustomText("Step ${index + 1}) ${it.value}", getUIStyle, defaultModifier)
                }
            }
            if (ct.getCardType() != Type.MULTI) {
                CustomText("A: ${cardDetails.answer.value}", getUIStyle, defaultModifier)
            }
            Button(
                onClick = {
                    showDetails = false
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = getUIStyle.buttonColor(),
                    contentColor = getUIStyle.buttonTextColor()
                )
            ) {
                Text("Hide card details")
            }
        } else {
            LimitedText("Q: ${cardDetails.question.value}", getUIStyle, defaultModifier) {
                showDetails = it
            }
        }
    }
}


@Composable
private fun LimitedText(
    text: String,
    getUIStyle: GetUIStyle,
    modifier: Modifier,
    onShow: (Boolean) -> Unit
) {
    Text(
        text = text,
        color = getUIStyle.titleColor(),
        textAlign = TextAlign.Start,
        modifier = modifier,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
    Button(
        onClick = {
            onShow(true)
        }, colors = ButtonDefaults.buttonColors(
            containerColor = getUIStyle.buttonColor(), contentColor = getUIStyle.buttonTextColor()
        )
    ) {
        Text("Show card details")
    }
}