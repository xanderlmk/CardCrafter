package com.belmontCrest.cardCrafter.supabase.controller.viewModels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.NULL_DECK
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.SUCCESS
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.MERGE_FAILED
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
import com.belmontCrest.cardCrafter.supabase.model.tables.toUUIDs
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
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    companion object {
        private const val UEDVM = "UEDVM"
        private const val TIMEOUT_MILLIS = 4_000L
        private const val SUPABASE_TIMEOUT = 6_000L
    }

    private val _userExportedDecks = MutableStateFlow(SBDeckListDto())

    private val _coOwnedDecks = MutableStateFlow(SBDeckListDto())

    val allDecks = combine(_coOwnedDecks, _userExportedDecks) { coOwned, owned ->
        SBDeckListDto((coOwned.list + owned.list).distinctBy { it.deckUUID }).also {
            println(it)
            getAllCards(it.toUUIDs())
        }
    }.stateIn(
        initialValue = SBDeckListDto(),
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS)
    )

    private val _userCards = MutableStateFlow(SBCardList())
    private val _uuid = MutableStateFlow(savedStateHandle["_uuid"] ?: "")
    val userCards = combine(_userCards, _uuid) { cards, uuid ->
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

    private val _coOwners = MutableStateFlow(listOf<CoOwnerWithUsername>())
    val coOwners = _coOwners.asStateFlow()

    private val userId = uEDRepository.userId()
    val isCoOwner = combine(_uuid, allDecks) { uuid, decks ->
        val deck = decks.list.find { it.deckUUID == uuid }
        if (deck == null) {
            false
        } else {
            deck.userId == userId
        }
    }.stateIn(
        initialValue = false,
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS)
    )

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _pickedDeck = combine(allDecks, _uuid) { decks, uuid ->
        val deck = decks.list.find { it.deckUUID == uuid }
        Log.d(UEDVM, "$deck")
        deck
    }.stateIn(
        initialValue = null,
        scope = viewModelScope,
        started = SharingStarted.Eagerly
    )

    init {
        getUserDeckList()
    }


    suspend fun mergeRemoteWithLocal(
        onProgress: (Float) -> Unit,
    ): Int {
        return withContext(Dispatchers.IO) {
            try {
                val deck = _pickedDeck.value
                if (deck == null) {
                    Log.e(UEDVM, "NULL DECK")
                    return@withContext NULL_DECK
                }
                val cts = userCards.value.cts.toListOfSealedCTToImport()
                mergeDecksRepository.mergeDeck(
                    deck, cts,
                ) { onProgress(it) }
                SUCCESS
            } catch (e: Exception) {
                Log.e(UEDVM, "$e")
                MERGE_FAILED
            }
        }
    }

    fun getUserDeckList() {
        viewModelScope.launch {
            try {
                _isLoading.update { true }
                uEDRepository.userExportedDecks().collectLatest { list ->
                    _userExportedDecks.update {
                        _isLoading.update { false }
                        SBDeckListDto(list)
                    }
                }
            } catch (e: Exception) {
                Log.e(UEDVM, "$e")
                _userExportedDecks.update {
                    _isLoading.update { false }
                    SBDeckListDto()
                }
            }
        }
        viewModelScope.launch {
            try {
                uEDRepository.userCoOwnedDecks().collectLatest { list ->
                    _coOwnedDecks.update {
                        SBDeckListDto(list)
                    }
                }
            } catch (e: Exception) {
                Log.e(UEDVM, "$e")
                _coOwnedDecks.update {
                    SBDeckListDto()
                }
            }
        }
    }

    fun updateUUUID(uuid: String) {
        savedStateHandle["_uuid"] = uuid
        _uuid.update { uuid }.also {
            viewModelScope.launch {
                getCTDs(uuid)
                getCoOwnersForDeck(uuid)
            }
        }
    }

    private fun getCoOwnersForDeck(uuid: String) {
        viewModelScope.launch {
            withTimeout(SUPABASE_TIMEOUT) {
                try {
                    val result = uEDRepository.coOwners(uuid)
                    if (result.second == SUCCESS) {
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
            uEDRepository.insertCoOwner(_uuid.value, username)
        }
    }

    fun getAllCoOwners() {
        viewModelScope.launch {
            try {
                val result = uEDRepository.coOwners(_uuid.value)
                if (result.second == SUCCESS) {
                    _coOwners.update {
                        result.first
                    }
                    Log.i(UEDVM, "${result.first}")
                } else {
                    Log.e(UEDVM, "Failed to retrieve co owners")
                }
            } catch (e: Exception) {
                Log.e(UEDVM, "$e")
            }
        }
    }

    private suspend fun getAllCards(uuids: List<String>) {
        _userCards.update {
            try {
                uEDRepository.userDeckCards(uuids)
            } catch (e: Exception) {
                Log.e(UEDVM, "$e")
                SBCardList()
            }
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
                        Log.e(UEDVM, "Failed to retrieve cards")
                    }
                } catch (e: Exception) {
                    Log.e(UEDVM, "$e")
                }
            }
        }
    }
}