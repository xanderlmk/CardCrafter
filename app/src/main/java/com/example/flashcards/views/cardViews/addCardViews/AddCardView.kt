package com.example.flashcards.views.cardViews.addCardViews

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flashcards.R
import com.example.flashcards.controller.AppViewModelProvider
import com.example.flashcards.controller.viewModels.cardViewsModels.AddCardViewModel
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.model.uiModels.Fields
import com.example.flashcards.views.miscFunctions.BackButton
import com.example.flashcards.ui.theme.GetModifier
import com.example.flashcards.views.miscFunctions.getSavableFields
import com.example.flashcards.views.miscFunctions.symbols.SymbolDocumentation


class AddCardView(
    private var fields: Fields,
    private var getModifier: GetModifier
) {
    @Composable
    fun AddCard(deck: Deck, onNavigate: () -> Unit) {
        var expanded by rememberSaveable { mutableStateOf(false) }
        val type = rememberSaveable { mutableStateOf("basic") }
        val addCardVM : AddCardViewModel =
            viewModel(factory = AppViewModelProvider.Factory)
        val helpForMath = rememberSaveable { mutableStateOf(false) }

        fields = getSavableFields(fields)
        Box(
            modifier = getModifier.boxViewsModifier()
        ) {
            SymbolDocumentation(helpForMath,getModifier)
            BackButton(
                onBackClick = {
                    fields.resetFields()
                    onNavigate()
                },
                modifier = getModifier.backButtonModifier(),
                getModifier = getModifier
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.TopEnd)
                ) {
                    IconButton(
                        onClick = { expanded = true },
                        modifier = Modifier
                            .padding(4.dp)
                            .size(54.dp)
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Card Type",
                            tint = getModifier.titleColor()
                        )
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(
                            onClick = { type.value = "basic" },
                            text = { Text(stringResource(R.string.basic_card)) })
                        DropdownMenuItem(
                            onClick = { type.value = "three" },
                            text = { Text(stringResource(R.string.three_field_card)) })
                        DropdownMenuItem(
                            onClick = { type.value = "hint" },
                            text = { Text(stringResource(R.string.hint_card)) })
                        DropdownMenuItem(
                            onClick = { type.value = "multi" },
                            text = { Text(stringResource(R.string.multi_choice_card)) })
                        DropdownMenuItem(
                            onClick = { type.value = "math"},
                            text = { Text("Math")}
                        )
                    }
                }
                val text = when (type.value) {
                    "hint" -> {
                        stringResource(R.string.hint)
                    }
                    "three" -> {
                        stringResource(R.string.three_fields)
                    }
                    "multi" -> {
                        stringResource(R.string.multi)
                    }
                    "math" -> {
                        "Math"
                    }
                    else -> {
                        stringResource(R.string.basic)
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = text,
                        fontSize = 35.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 40.sp,
                        color = getModifier.titleColor(),
                        fontWeight = FontWeight.Bold,
                        modifier =
                        if (type.value == "math") {Modifier.padding(start = 8.dp)}
                        else {Modifier}
                    )
                    if (type.value == "math") {
                        Text(
                            text = "?", fontSize = 35.sp,
                            textAlign = TextAlign.Right,
                            lineHeight = 40.sp,
                            color = getModifier.iconColor(),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .clickable {
                                    helpForMath.value = true
                                }
                        )
                    }
                }
                when (type.value) {
                    "basic" -> AddBasicCard(
                        addCardVM, deck,
                        fields, getModifier
                    )
                    "three" -> AddThreeCard(
                        addCardVM, deck,
                        fields, getModifier
                    )
                    "hint" -> AddHintCard(
                        addCardVM, deck,
                        fields, getModifier
                    )
                    "multi" -> AddMultiChoiceCard(
                        addCardVM, deck,
                        fields, getModifier
                    )
                    "math" -> AddMathCard(addCardVM, deck,
                        fields, getModifier)
                    else -> AddBasicCard(
                        addCardVM, deck,
                        fields, getModifier
                    )
                }
            }
        }
    }
}