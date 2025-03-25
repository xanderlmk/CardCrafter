package com.belmontCrest.cardCrafter.supabase.controller

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.belmontCrest.cardCrafter.controller.cardHandlers.mapAllCardTypesToCTs
import com.belmontCrest.cardCrafter.controller.viewModels.deckViewsModels.checkIfDeckExists
import com.belmontCrest.cardCrafter.controller.viewModels.deckViewsModels.checkIfDeckUUIDExists
import com.belmontCrest.cardCrafter.model.repositories.CardTypeRepository
import com.belmontCrest.cardCrafter.model.repositories.FlashCardRepository
import com.belmontCrest.cardCrafter.model.repositories.ScienceSpecificRepository
import com.belmontCrest.cardCrafter.model.tablesAndApplication.AllCardTypes
import com.belmontCrest.cardCrafter.model.tablesAndApplication.Deck
import com.belmontCrest.cardCrafter.model.uiModels.PreferencesManager
import com.belmontCrest.cardCrafter.model.uiModels.SealedAllCTs
import com.belmontCrest.cardCrafter.supabase.controller.supabaseVMFunctions.downloadCards
import com.belmontCrest.cardCrafter.supabase.controller.supabaseVMFunctions.insertDeck.tryExportDeck
import com.belmontCrest.cardCrafter.supabase.controller.supabaseVMFunctions.upsertDeck.tryUpsertDeck
import com.belmontCrest.cardCrafter.supabase.model.SBCards
import com.belmontCrest.cardCrafter.supabase.model.SBDeckList
import com.belmontCrest.cardCrafter.supabase.model.SBDecks
import com.belmontCrest.cardCrafter.supabase.model.createSupabase
import com.belmontCrest.cardCrafter.supabase.model.getSBKey
import com.belmontCrest.cardCrafter.supabase.model.getSBUrl
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.IDToken
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketException
import java.util.Date
import java.util.concurrent.CancellationException


@OptIn(
    SupabaseExperimental::class,
    ExperimentalCoroutinesApi::class,
    SupabaseInternal::class
)
@RequiresApi(Build.VERSION_CODES.Q)
class SupabaseViewModel(
    private val flashCardRepository: FlashCardRepository,
    private val cardTypeRepository: CardTypeRepository,
    private val sSRepository: ScienceSpecificRepository,
    application: Application
) : AndroidViewModel(application) {
    companion object {
        private const val TIMEOUT_MILLIS = 4_000L
        private const val DECK_EXISTS = 100
        private const val UUID_CONFLICT = 101
        private const val EMPTY_CARD_LIST = 88
        private const val NULL_USER = 1
        private const val NETWORK_ERROR = 500
        private const val CANCELLED = 499
        private const val SUCCESSFUL_QUERY = 0
        private const val UNKNOWN_ERROR = 504
    }

    private var thisSupabase: StateFlow<SupabaseClient> = flowOf(
        createSupabase(
            getSBUrl(), getSBKey()
        )
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = createSupabase(
            getSBUrl(), getSBKey()
        )
    )
    val supabase = thisSupabase
    private var isClientActive = true

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
    private val deckId = MutableStateFlow(0)
    private val localDeck: StateFlow<Deck?> = deckId.flatMapLatest { id ->
        if (id == 0) {
            flowOf(null)
        } else {
            flashCardRepository.getDeckStream(id).map {
                it
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = null
    )
    val pickedDeck = localDeck
    private val sealedUiState = deckId.flatMapLatest { id ->
        if (id == 0) {
            flowOf(SealedAllCTs())
        } else {
            cardTypeRepository.getAllCardTypes(id).map {
                updateSealedUiState(it)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = SealedAllCTs()
    )
    var sealedAllCTs = sealedUiState

    init {
        viewModelScope.launch {
            application.networkConnectivityFlow().collectLatest { isConnected ->
                if (!isConnected && isClientActive) {
                    Log.d("NETWORK", "NETWORK HAS BEEN DISCONNECTED")
                    thisSupabase.value.close()
                    delay(2000)
                    privateList.update {
                        SBDeckList()
                    }
                    isClientActive = false
                } else if (isConnected && !isClientActive) {
                    // Reinitialize the client only if it's not active
                    Log.d("NETWORK", "RECONNECTED!")
                    thisSupabase = flowOf(
                        createSupabase(getSBUrl(), getSBKey())
                    ).stateIn(
                        scope = viewModelScope,
                        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                        initialValue = createSupabase(getSBUrl(), getSBKey())
                    )
                    getDeckList()
                    isClientActive = true
                }
            }
        }
    }

    fun changeDeckId(id: Int) {
        deckId.value = id
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
        googleIdToken: String,
        rawNonce: String
    ) {
        thisSupabase.value.auth.signInWith(IDToken) {
            idToken = googleIdToken
            provider = Google
            nonce = rawNonce
        }
    }

    fun getDeckList() {
        try {
            viewModelScope.launch {
                thisSupabase.value.from("deck")
                    .selectAsFlow(SBDecks::deckUUID).collect { list ->
                        privateList.update {
                            it.copy(
                                list = list
                            )
                        }
                    }
            }
        } catch (e: Exception) {
            when (e) {
                is SocketException -> {
                    Log.d("SupabaseVm", "Network Error Occurred: $e")
                }

                is CancellationException -> {
                    Log.d("SupabaseVm", "Cancelled: $e")
                }

                else -> {
                    Log.d("SupabaseVm", "Unknown error: $e")
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
        sbDecks: SBDecks,
        preferences: PreferencesManager,
        onProgress: (Float) -> Unit,
        onError: (String) -> Unit
    ): Int {
        return withContext(Dispatchers.IO) {
            try {
                val exists = checkDeckNameAndUUID(sbDecks.name, sbDecks.deckUUID)
                if (exists > 0) {
                    /** deck already exists; return 100. */
                    return@withContext DECK_EXISTS
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

                val cardList = thisSupabase.value.from("card")
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
                            card, thisSupabase.value, sbDecks.deckUUID, flashCardRepository,
                            cardTypeRepository, deckId.toInt(), sSRepository, preferences
                        )
                        if (success != 0) {
                            return@withContext success
                        }
                        onProgress((index + 1).toFloat() / total)
                    }
                } else {
                    Log.d("SupabaseViewModel", "List is empty!!")
                    return@withContext EMPTY_CARD_LIST
                }
            } catch (e: Exception) {
                when (e) {
                    is SocketException -> {
                        onError("Network Error Occurred.")
                        return@withContext NETWORK_ERROR
                    }

                    is CancellationException -> {
                        onError("Import was canceled.")
                        return@withContext CANCELLED
                    }

                    else -> {
                        onError("Something went wrong.")
                        return@withContext UNKNOWN_ERROR
                    }
                }
            }
            return@withContext SUCCESSFUL_QUERY
        }
    }

    suspend fun exportDeck(
        deck: Deck,
        description: String
    ): Int {
        return withContext(Dispatchers.IO) {
            val user = thisSupabase.value.auth.currentUserOrNull()
            if (user == null) {
                Log.d("SupabaseViewModel", "User is null!")
                return@withContext NULL_USER
            }
            /** if successful, return 0 */
            val success = tryExportDeck(
                thisSupabase.value, deck, user.id,
                description, sealedUiState.value.allCTs
            )
            return@withContext success
        }
    }

    suspend fun updateExportedDeck(
        deck: Deck,
        description: String
    ): Int {
        return withContext(Dispatchers.IO) {
            val user = thisSupabase.value.auth.currentUserOrNull()
            if (user == null) {
                Log.d("SupabaseViewModel", "User is null!")
                return@withContext NULL_USER
            }
            /** if successful, return 0 */
            val success = tryUpsertDeck(
                supabase = thisSupabase.value,
                deck = deck,
                description = description,
                userId = user.id,
                cts = sealedUiState.value.allCTs
            )
            return@withContext success
        }
    }

    suspend fun createNewDeck(
        sbDecks: SBDecks,
        preferences: PreferencesManager, name: String
    ): Int {
        return withContext(Dispatchers.IO) {
            val exists = checkDeckName(name)
            if (exists > 0) {
                /** deck name already exists; return 100. */
                return@withContext DECK_EXISTS
            }
            var uuid = sbDecks.deckUUID
            val existingUUID = checkDeckUUID(sbDecks.deckUUID)

            /** If there's an existing uuid, we won't allow the user to
             * create a new deck */
            if (existingUUID > 0) {
                return@withContext UUID_CONFLICT
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
            val cardList = thisSupabase.value.from("card")
                .select(columns = Columns.ALL) {
                    filter {
                        eq("deckUUID", sbDecks.deckUUID)
                    }
                }
                .decodeList<SBCards>()
            if (cardList.isNotEmpty()) {
                cardList.map {
                    val success = downloadCards(
                        it, thisSupabase.value, uuid, flashCardRepository,
                        cardTypeRepository, deckId.toInt(), sSRepository, preferences
                    )
                    if (success != 0) {
                        return@withContext success
                    }
                }
            } else {
                Log.d("SupabaseViewModel", "List is empty!!")
                return@withContext EMPTY_CARD_LIST
            }
            return@withContext SUCCESSFUL_QUERY
        }
    }
}




