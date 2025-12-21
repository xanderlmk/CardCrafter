package com.belmontCrest.cardCrafter.supabase.controller.view.models

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belmontCrest.cardCrafter.local.db.repositories.DeckListRepository
import com.belmontCrest.cardCrafter.model.ui.states.DeckUiState
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.SBTablesRepository
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.ownerRepos.MergeDecksRepository
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.ownerRepos.UserExportedDecksRepository
import com.belmontCrest.cardCrafter.supabase.model.tables.CardsToDisplay
import com.belmontCrest.cardCrafter.supabase.model.tables.CoOwnerWithUsername
import com.belmontCrest.cardCrafter.supabase.model.tables.FourSBCards
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardColsWithCT
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardList
import com.belmontCrest.cardCrafter.supabase.model.tables.SBDeckListDto
import com.belmontCrest.cardCrafter.supabase.model.tables.toListOfSealedCTToImport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

class UserExportedDecksViewModel(
    private val sbTableRepository: SBTablesRepository,
    private val uEDRepository: UserExportedDecksRepository,
    private val mergeDecksRepository: MergeDecksRepository,
    deckListRepository: DeckListRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    companion object {
        private const val UEDVM = "UEDVM"
        private const val TIMEOUT_MILLIS = 4_000L
        private const val SUPABASE_TIMEOUT = 6_000L
    }

    val allDecks = uEDRepository.allDecks.stateIn(
        initialValue = SBDeckListDto(),
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS)
    )
    val deckUiState = deckListRepository.deckUiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = DeckUiState()
    )

    val userCards = uEDRepository.userCards.stateIn(
        initialValue = SBCardList(),
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS)
    )

    private val _cardsToDisplay = MutableStateFlow(CardsToDisplay())
    val cardsToDisplay = combine(userCards, _cardsToDisplay) { cards, cardsTD ->
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

    private val _coOwners = MutableStateFlow(listOf<CoOwnerWithUsername>())
    val coOwners = _coOwners.asStateFlow()

    val isCoOwner = uEDRepository.isCoOwner.stateIn(
        initialValue = false,
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS)
    )
    val isLoading = uEDRepository.isLoading

    private val _pickedDeck = uEDRepository.pickedDeck.stateIn(
        initialValue = null,
        scope = viewModelScope,
        started = SharingStarted.Eagerly
    )

    init {
        getUserDeckList()
    }

    suspend fun mergeRemoteWithLocal(
        reviewAmount: Int, cardAmount: Int, onProgress: (Float) -> Unit
    ): Int {
        return withContext(Dispatchers.IO) {
            try {
                val deck = _pickedDeck.value
                if (deck == null) {
                    Log.e(UEDVM, "NULL DECK")
                    return@withContext ReturnValues.NULL_DECK
                }
                val cts = userCards.value.cts.toListOfSealedCTToImport()
                val deckExists = mergeDecksRepository.doesDeckExist(deck.deckUUID)
                if (deckExists) {
                    mergeDecksRepository.mergeDeck(
                        deck, cts,
                    ) { onProgress(it) }
                } else {
                    mergeDecksRepository.insertDeckList(
                        deck, cts, reviewAmount, cardAmount
                    ) { onProgress(it) }
                }
                ReturnValues.SUCCESS
            } catch (e: Exception) {
                Log.e(UEDVM, "$e")
                ReturnValues.MERGE_FAILED
            }
        }
    }

    fun getUserDeckList() {
        viewModelScope.launch {
            try {
                uEDRepository.updateIsLoading(true)
                uEDRepository.userExportedDecks().collectLatest { list ->
                    uEDRepository.updateIsLoading(false)
                    uEDRepository.updateUserDeckList(list)
                }
            } catch (e: Exception) {
                Log.e(UEDVM, "$e")
                uEDRepository.updateUserDeckList(emptyList())
            } finally {
                uEDRepository.updateIsLoading(false)
            }
        }
        viewModelScope.launch {
            try {
                uEDRepository.userCoOwnedDecks().collectLatest { list ->
                    uEDRepository.updateUserCoOwnedDecks(list)
                }
            } catch (e: Exception) {
                Log.e(UEDVM, "$e")
                uEDRepository.updateUserCoOwnedDecks(emptyList())
            }
        }
    }

    fun updateUUUID(uuid: String) {
        savedStateHandle["_uuid"] = uuid
        uEDRepository.updateUUID(uuid).also {
            getCoOwnersForDeck(uuid); getCTDs(uuid)
        }
    }

    private fun getCoOwnersForDeck(uuid: String) {
        viewModelScope.launch {
            withTimeout(SUPABASE_TIMEOUT) {
                try {
                    val result = uEDRepository.coOwners(uuid)
                    if (result.second == ReturnValues.SUCCESS) {
                        _coOwners.update { result.first }
                        Log.i(UEDVM, "${result.first}")
                    } else {
                        Log.e(UEDVM, "Failed to retrieve co owners")
                    }
                } catch (e: Exception) {
                    Log.e(UEDVM, "$e")
                }
            }
        }
    }

    suspend fun insertCoOwner(username: String): Int {
        return withContext(Dispatchers.IO) {
            uEDRepository.insertCoOwner(uEDRepository.uuidVal(), username)
        }
    }

    fun getAllCoOwners() {
        viewModelScope.launch {
            try {
                val result = uEDRepository.coOwners(uEDRepository.uuidVal())
                if (result.second == ReturnValues.SUCCESS) _coOwners.update { result.first }
            } catch (e: Exception) {
                Log.e(UEDVM, "$e")
            }
        }
    }

    private fun getCTDs(uuid: String) = viewModelScope.launch {
        withTimeout(SUPABASE_TIMEOUT) {
            try {
                val result = sbTableRepository.getCardsToDisplay(uuid)
                if (result.second == ReturnValues.SUCCESS) {
                    _cardsToDisplay.update {
                        result.first
                    }
                } else {
                    Log.e(UEDVM, "Failed to retrieve cards")
                }
            } catch (e: Exception) {
                Log.e(UEDVM, "$e")
            }
        }
    }

    init {
        if (uEDRepository.uuidVal().isBlank())
            uEDRepository.updateUUID(savedStateHandle["_uuid"] ?: "")
    }
}