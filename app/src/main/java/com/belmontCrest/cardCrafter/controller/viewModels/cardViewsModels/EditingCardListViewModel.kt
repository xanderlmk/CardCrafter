@file:OptIn(ExperimentalCoroutinesApi::class)

package com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.CardTypeRepository
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.model.ui.SealedAllCTs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class EditingCardListViewModel(
    private val cardTypeRepository: CardTypeRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 4_000L
    }

    private val deckId = MutableStateFlow(savedStateHandle["deckId"] ?: 0)

    val sealedAllCTs = deckId.flatMapLatest { id ->
        if (id == 0) {
            flowOf(SealedAllCTs())
        } else {
            cardTypeRepository.getAllCardTypesStream(id).map {
                SealedAllCTs(it.toMutableList())
            }
        }
    }.stateIn(
        scope = viewModelScope,
        initialValue = SealedAllCTs(),
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS)
    )

    val searchQuery = cardTypeRepository.searchQuery.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = savedStateHandle["query"] ?: ""
    )
    val selectedCards = cardTypeRepository.selectedCards.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = emptyList()
    )

    fun updateQuery(query: String) {
        savedStateHandle["query"] = query
        cardTypeRepository.updateQuery(query)
    }

    fun updateId(
        id: Int,
    ) {
        savedStateHandle["deckId"] = id
        deckId.update { id }
    }

    fun toggleCard(ct: CT) {
        cardTypeRepository.toggleCard(ct)
    }

    init {
        updateQuery(savedStateHandle["query"] ?: "")
    }
}



