package com.belmontCrest.cardCrafter.views.cardViews.addCardViews

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.belmontCrest.cardCrafter.model.application.AppVMProvider
import com.belmontCrest.cardCrafter.navigation.NavViewModel
import com.belmontCrest.cardCrafter.controller.view.models.cardViewsModels.AddCardViewModel
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.model.Type
import com.belmontCrest.cardCrafter.model.application.preferences.PreferenceValues
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.boxViewsModifier

class AddCardView(
    private var getUIStyle: GetUIStyle,
    private val preference: PreferenceValues,
    private val navViewModel: NavViewModel
) {
    @RequiresApi(Build.VERSION_CODES.Q)
    @Composable
    fun AddCard(deck: Deck) {
        val addCardVM: AddCardViewModel = viewModel(factory = AppVMProvider.Factory)
        val type by navViewModel.type.collectAsStateWithLifecycle()

        Box(
            modifier = Modifier
                .boxViewsModifier(getUIStyle.getColorScheme())
        ) {

            when (type) {
                Type.BASIC -> AddBasicCard(
                    addCardVM, deck, getUIStyle
                )

                Type.THREE -> AddThreeCard(
                    addCardVM, deck, getUIStyle
                )

                Type.HINT -> AddHintCard(
                    addCardVM, deck, getUIStyle
                )

                Type.MULTI -> AddMultiChoiceCard(
                    addCardVM, deck,
                    getUIStyle
                )

                Type.NOTATION -> AddNotationCard(
                    addCardVM, deck, preference.height, preference.width,
                    getUIStyle, Modifier
                        .fillMaxSize()
                        .zIndex(2f)
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                )

                Type.CREATE_NEW -> AddCustomCard(
                    addCardVM, deck, preference.height, preference.width,
                    getUIStyle, Modifier
                        .fillMaxSize()
                        .zIndex(2f)
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                )

                else -> AddSavedTypeCard(
                    addCardVM, deck, preference.height, preference.width,
                    getUIStyle, Modifier
                        .fillMaxSize()
                        .zIndex(2f)
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                )
            }
        }

    }
}