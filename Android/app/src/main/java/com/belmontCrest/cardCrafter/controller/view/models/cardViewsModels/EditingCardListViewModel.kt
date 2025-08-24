@file:OptIn(ExperimentalCoroutinesApi::class)

package com.belmontCrest.cardCrafter.controller.view.models.cardViewsModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.CardTypeRepository
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.DeckContentRepository
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.model.ui.states.SealedAllCTs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class EditingCardListViewModel(
    private val cardTypeRepository: CardTypeRepository,
    deckContentRepository: DeckContentRepository,
) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 4_000L
    }

    val sealedAllCTs = deckContentRepository.sealedAllCTs.stateIn(
        scope = viewModelScope, initialValue = SealedAllCTs(),
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS)
    )

    val searchQuery = cardTypeRepository.searchQuery.stateIn(
        scope = viewModelScope, started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = ""
    )
    val selectedCards = cardTypeRepository.selectedCards.stateIn(
        scope = viewModelScope, started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = emptyList()
    )

    fun toggleCard(ct: CT) = cardTypeRepository.toggleCard(ct)

}



