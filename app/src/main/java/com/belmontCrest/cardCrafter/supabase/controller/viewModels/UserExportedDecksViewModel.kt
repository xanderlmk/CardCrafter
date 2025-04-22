package com.belmontCrest.cardCrafter.supabase.controller.viewModels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.SUCCESS
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.SBTablesRepository
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.UserExportedDecksRepository
import com.belmontCrest.cardCrafter.supabase.model.tables.CardsToDisplay
import com.belmontCrest.cardCrafter.supabase.model.tables.FourSBCards
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsWithCT
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardList
import com.belmontCrest.cardCrafter.supabase.model.tables.SBDeckListDto
import com.belmontCrest.cardCrafter.supabase.model.tables.toUUIDs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class UserExportedDecksViewModel(
    private val sbTableRepository: SBTablesRepository,
    private val uEDRepository: UserExportedDecksRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val UEDVM = "UEDVM"
        private const val TIMEOUT_MILLIS = 4_000L
        private const val SUPABASE_TIMEOUT = 6_000L
    }

    private val _userExportedDecks = MutableStateFlow(SBDeckListDto())
    val userExportedDecks = _userExportedDecks.asStateFlow()
    private val _userCards = MutableStateFlow(SBCardList())
    private val _uuid = MutableStateFlow(savedStateHandle["_uuid"] ?: "")
    val userCards = combine(
        _userCards, _uuid
    ) { cards, uuid ->
        val filtered = cards.cts.filter { it.deckUUID == uuid }
        Log.i(UEDVM, "Size of deck: ${filtered.size}")
        SBCardList(filtered)
    }.stateIn(
        initialValue = SBCardList(),
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS)
    )
    private val _cardsToDisplay = MutableStateFlow(CardsToDisplay())
    val cardsToDisplay = combine(
        userCards, _cardsToDisplay
    ) { cards, cardsTD ->
        // Lookup a CT by cardIdentifier
        fun findCT(cardIdentifier: String?): SBCardColsWithCT? =
            cardIdentifier?.let { lookup ->
                cards.cts.firstOrNull { it.cardIdentifier == lookup }
            }

        FourSBCards(
            first = findCT(cardsTD.cardOne),
            second = findCT(cardsTD.cardTwo),
            third = findCT(cardsTD.cardThree),
            fourth = findCT(cardsTD.cardFour)
        )
    }.stateIn(
        initialValue = FourSBCards(),
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS)
    )

    init {
        getUserDeckList()
    }

    fun getUserDeckList() {
        viewModelScope.launch {
            try {
                uEDRepository.userExportedDecks().collectLatest { list ->
                    _userExportedDecks.update {
                        SBDeckListDto(list)
                    }.also {
                        getAllCards()
                    }
                }
            } catch (e: Exception) {
                Log.e(UEDVM, "$e")
                _userExportedDecks.update {
                    SBDeckListDto()
                }
            }
        }
    }

    fun updateUUUID(uuid: String) {
        savedStateHandle["_uuid"] = uuid
        _uuid.update {
            uuid
        }.also {
            getCTDs(uuid)
        }
    }

    private suspend fun getAllCards() {
        _userCards.update {
            uEDRepository.userDeckCards(_userExportedDecks.value.toUUIDs())
        }
    }
    private fun getCTDs(uuid: String) {
        viewModelScope.launch {
            withTimeout(SUPABASE_TIMEOUT) {
                try {
                    val result = sbTableRepository.getCardsToDisplay(uuid)
                    if (result.second == SUCCESS) {
                        _cardsToDisplay.update {
                            result.first
                        }
                    } else {
                        Log.e("SupabaseVM", "Failed to retrieve cards")
                    }
                } catch (e: Exception) {
                    Log.e("SupabaseVM", "$e")
                }
            }
        }
    }
}