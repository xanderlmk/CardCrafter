package com.belmontCrest.cardCrafter.supabase.controller

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belmontCrest.cardCrafter.controller.cardHandlers.mapAllCardTypesToCTs
import com.belmontCrest.cardCrafter.controller.viewModels.deckViewsModels.checkIfDeckExists
import com.belmontCrest.cardCrafter.controller.viewModels.deckViewsModels.checkIfDeckUUIDExists
import com.belmontCrest.cardCrafter.model.repositories.CardTypeRepository
import com.belmontCrest.cardCrafter.model.repositories.FlashCardRepository
import com.belmontCrest.cardCrafter.model.repositories.ScienceSpecificRepository
import com.belmontCrest.cardCrafter.model.tablesAndApplication.AllCardTypes
import com.belmontCrest.cardCrafter.model.tablesAndApplication.CT
import com.belmontCrest.cardCrafter.model.tablesAndApplication.Deck
import com.belmontCrest.cardCrafter.model.uiModels.CardListUiState
import com.belmontCrest.cardCrafter.model.uiModels.PreferencesManager
import com.belmontCrest.cardCrafter.model.uiModels.SealedAllCTs
import com.belmontCrest.cardCrafter.supabase.controller.supabaseVMFunctions.downloadCards
import com.belmontCrest.cardCrafter.supabase.controller.supabaseVMFunctions.tryExportDeck
import com.belmontCrest.cardCrafter.supabase.model.SBCards
import com.belmontCrest.cardCrafter.supabase.model.SBDeckList
import com.belmontCrest.cardCrafter.supabase.model.SBDecks
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.IDToken
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketException
import java.util.Date
import java.util.concurrent.CancellationException


@OptIn(SupabaseExperimental::class, ExperimentalCoroutinesApi::class)
@RequiresApi(Build.VERSION_CODES.Q)
class SupabaseViewModel(
    private val flashCardRepository: FlashCardRepository,
    private val cardTypeRepository: CardTypeRepository,
    private val sSRepository: ScienceSpecificRepository
) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 4_000L
        private const val DECK_EXISTS = 100
        private const val UUID_CONFLICT = 101
        private const val EMPTY_CARD_LIST = 88
        private const val NULL_USER = 1
        private const val NETWORK_ERROR = 500
    }

    private val privateList = MutableStateFlow(SBDeckList())
    val deckList = privateList.asStateFlow()
    private val uuid = MutableStateFlow("")
    val deck: StateFlow<SBDecks?> = uuid.map { currentUUID ->
        privateList.value.list.find { it.deckUUID == currentUUID }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = null
    )
    private val sealedUiState = MutableStateFlow(SealedAllCTs())
    var sealedAllCTs = sealedUiState.asStateFlow()


    suspend fun getAllCardsForDeck(
        deckId: Int,
    ) {
        return withContext(Dispatchers.IO) {
            viewModelScope.launch {
                cardTypeRepository.getAllCardTypes(deckId).map { allCards ->
                    CardListUiState(allCards = allCards)
                }.collect { state ->
                    sealedUiState.update {
                        updateSealedUiState(state.allCards)
                    }
                }
            }
        }
    }

    private fun updateSealedUiState(
        allCards: List<AllCardTypes>
    ): SealedAllCTs {
        var allCTs = try {
            mapAllCardTypesToCTs(allCards)
        } catch (e: IllegalStateException) {
            Log.e(
                "GetDueTypesForDeck",
                "Invalid AllCardType data: ${e.message}"
            )
            return SealedAllCTs()
        }
        return SealedAllCTs(
            allCTs = allCTs.toMutableList()
        )
    }

    fun updateUUID(thisUUID: String) {
        uuid.value = thisUUID
    }

    suspend fun signUpWithGoogle(
        supabase: SupabaseClient,
        googleIdToken: String,
        rawNonce: String
    ) {
        supabase.auth.signInWith(IDToken) {
            idToken = googleIdToken
            provider = Google
            nonce = rawNonce
        }
    }

    fun getDeckList(supabase: SupabaseClient) {
        viewModelScope.launch {
            supabase.from("deck")
                .selectAsFlow(SBDecks::deckUUID).collect { list ->
                    privateList.update {
                        it.copy(
                            list = list
                        )
                    }
                }
        }
    }

    private suspend fun checkDeckNameAndUUID(name: String, uuid: String): Int {
        return withContext(Dispatchers.IO) {
            checkIfDeckExists(name, uuid, flashCardRepository)
        }
    }

    private suspend fun checkDeckName(name: String): Int {
        return withContext(Dispatchers.IO) {
            checkIfDeckExists(name, flashCardRepository)
        }
    }

    private suspend fun checkDeckUUID(uuid: String): Int {
        return withContext(Dispatchers.IO) {
            checkIfDeckUUIDExists(uuid, flashCardRepository)
        }
    }

    suspend fun importDeck(
        sbDecks: SBDecks, supabase: SupabaseClient,
        preferences: PreferencesManager,
        onProgress: (Float) -> Unit,
        onError: (String) -> Unit
    ): Int {
        var returnValue = 0
        try {
            viewModelScope.launch {
                try {
                    val exists = checkDeckNameAndUUID(sbDecks.name, sbDecks.deckUUID)
                    if (exists > 0) {
                        /** deck already exists; return 100. */
                        returnValue = DECK_EXISTS
                        return@launch
                    }
                    val deckId = flashCardRepository.insertDeck(
                        Deck(
                            name = sbDecks.name,
                            uuid = sbDecks.deckUUID,
                            nextReview = Date(),
                            lastUpdated = Date(),
                            reviewAmount = preferences.reviewAmount.intValue,
                            cardAmount = preferences.cardAmount.intValue
                        )
                    )

                    val cardList = supabase.from("card")
                        .select(columns = Columns.ALL) {
                            filter {
                                eq("deckUUID", sbDecks.deckUUID)
                            }
                        }
                        .decodeList<SBCards>()

                    Log.d("SupabaseViewModel", "$cardList")

                    if (cardList.isNotEmpty()) {
                        val total = cardList.size
                        cardList.mapIndexed { index, card ->
                            val success = downloadCards(
                                card, supabase, sbDecks.deckUUID, flashCardRepository,
                                cardTypeRepository, deckId.toInt(), sSRepository, preferences
                            )
                            if (success != 0) {
                                returnValue = success
                                return@launch
                            }
                            onProgress((index + 1).toFloat() / total)
                        }
                    } else {
                        Log.d("SupabaseViewModel", "List is empty!!")
                        returnValue = EMPTY_CARD_LIST
                    }
                } catch (e: Exception) {
                    when (e) {
                        is SocketException -> {
                            onError("Network Error Occurred.")
                            returnValue = NETWORK_ERROR
                        }
                        else -> {
                            onError("Something went wrong.")
                        }
                    }
                }
            }.join()
        } catch (e: Exception) {
            when (e) {
                is CancellationException -> {
                    onError("Import was canceled.")
                }
                else -> {
                    onError("Something went wrong.")
                }
            }
        }
        return returnValue
    }
    suspend fun exportDeck(
        deck: Deck,
        supabase: SupabaseClient,
        cts: List<CT>,
        description: String
    ): Int {
        val user = supabase.auth.currentUserOrNull()
        if (user == null) {
            Log.d("SupabaseViewModel", "User is null!")
            return NULL_USER
        }
        try {
            /** if successful, return 0 */
            var success = 0
            viewModelScope.launch {
               success = tryExportDeck(supabase, deck, user.id, description, cts)
                if(success != 0){
                    return@launch
                }
            }.join()
            return success
        } catch (e: Exception) {
            Log.d(
                "SupabaseViewModel", "Failed to upload Deck and it's Cards: $e"
            )
            /** return 5 means the deck and it's contents couldn't
             * upload successfully */
            return 5
        }

        Log.d("SupabaseViewModel", "User is null!")
    }

    suspend fun createNewDeck(
        sbDecks: SBDecks, supabase: SupabaseClient,
        preferences: PreferencesManager, name: String
    ): Int {
        var returnValue = 0
        viewModelScope.launch {
            val exists = checkDeckName(name)
            if (exists > 0) {
                /** deck name already exists; return 100. */
                returnValue = DECK_EXISTS
                return@launch
            }
            var uuid = sbDecks.deckUUID
            val existingUUID = checkDeckUUID(sbDecks.deckUUID)

            /** If there's an existing uuid, we won't allow the user to
             * create a new deck */
            if (existingUUID > 0) {
                returnValue = UUID_CONFLICT
                return@launch
            }
            val deckId = flashCardRepository.insertDeck(
                Deck(
                    name = name,
                    uuid = uuid,
                    nextReview = Date(),
                    lastUpdated = Date(),
                    reviewAmount = preferences.reviewAmount.intValue,
                    cardAmount = preferences.cardAmount.intValue
                )
            )
            val cardList = supabase.from("card")
                .select(columns = Columns.ALL) {
                    filter {
                        eq("deckUUID", sbDecks.deckUUID)
                    }
                }
                .decodeList<SBCards>()
            if (cardList.isNotEmpty()) {
                cardList.map {
                    val success = downloadCards(
                        it, supabase, uuid, flashCardRepository,
                        cardTypeRepository, deckId.toInt(), sSRepository, preferences
                    )
                    if (success != 0) {
                        returnValue = success
                        return@launch
                    }
                }
            } else {
                Log.d("SupabaseViewModel", "List is empty!!")
                returnValue = EMPTY_CARD_LIST
            }
        }.join()
        return returnValue
    }
}




