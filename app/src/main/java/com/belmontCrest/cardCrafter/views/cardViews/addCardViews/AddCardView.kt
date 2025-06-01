package com.belmontCrest.cardCrafter.views.cardViews.addCardViews

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.model.application.AppViewModelProvider
import com.belmontCrest.cardCrafter.navigation.NavViewModel
import com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels.AddCardViewModel
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.model.Type
import com.belmontCrest.cardCrafter.model.uiModels.Fields
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.boxViewsModifier
import com.belmontCrest.cardCrafter.views.miscFunctions.getSavableFields
import com.belmontCrest.cardCrafter.uiFunctions.symbols.SymbolDocumentation


class AddCardView(
    private var fields: Fields,
    private var getUIStyle: GetUIStyle,
    private val navViewModel: NavViewModel
) {
    @RequiresApi(Build.VERSION_CODES.Q)
    @Composable
    fun AddCard(deck: Deck) {
        val addCardVM: AddCardViewModel = viewModel(factory = AppViewModelProvider.Factory)
        val helpForNotation = rememberSaveable { mutableStateOf(false) }
        fields = getSavableFields(fields)
        val type by navViewModel.type.collectAsStateWithLifecycle()
        val selectedKB by addCardVM.selectedKB.collectAsStateWithLifecycle()
        val showKB by addCardVM.showKatexKeyboard.collectAsStateWithLifecycle()

        Box(
            modifier = Modifier
                .boxViewsModifier(getUIStyle.getColorScheme())
        ) {
            SymbolDocumentation(helpForNotation, getUIStyle)
            if (type == Type.NOTATION && selectedKB != null) {
                IconButton(
                    onClick = { addCardVM.toggleKeyboard() },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .size(30.dp)
                ) {
                    if (!showKB) {
                        Icon(
                            painterResource(R.drawable.twotone_keyboard),
                            contentDescription = "Keyboard"
                        )
                    } else {
                        Icon(
                            painterResource(R.drawable.twotone_keyboard_hide),
                            contentDescription = "Hide Keyboard"
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                val text = when (type) {
                    Type.HINT -> {
                        stringResource(R.string.hint)
                    }

                    Type.THREE -> {
                        stringResource(R.string.three_fields)
                    }

                    Type.MULTI -> {
                        stringResource(R.string.multi)
                    }

                    Type.NOTATION -> {
                        "Notation"
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
                        color = getUIStyle.titleColor(),
                        fontWeight = FontWeight.Bold,
                        modifier =
                            if (type == Type.NOTATION) {
                                Modifier.padding(start = 8.dp)
                            } else {
                                Modifier
                            }
                    )
                    if (type == Type.NOTATION) {
                        Text(
                            text = "?", fontSize = 35.sp,
                            textAlign = TextAlign.Right,
                            lineHeight = 40.sp,
                            color = getUIStyle.iconColor(),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .clickable {
                                    helpForNotation.value = true
                                }
                        )

                    }
                }
                when (type) {
                    Type.BASIC -> AddBasicCard(
                        addCardVM, deck,
                        fields, getUIStyle
                    )

                    Type.THREE -> AddThreeCard(
                        addCardVM, deck,
                        fields, getUIStyle
                    )

                    Type.HINT -> AddHintCard(
                        addCardVM, deck,
                        fields, getUIStyle
                    )

                    Type.MULTI -> AddMultiChoiceCard(
                        addCardVM, deck,
                        fields, getUIStyle
                    )

                    Type.NOTATION -> AddNotationCard(
                        addCardVM, deck,
                        fields, getUIStyle, Modifier
                            .zIndex(2f)
                            .align(AbsoluteAlignment.Left)
                            .padding(6.dp)
                    )

                    else -> AddBasicCard(
                        addCardVM, deck,
                        fields, getUIStyle
                    )
                }
            }
        }
    }
}