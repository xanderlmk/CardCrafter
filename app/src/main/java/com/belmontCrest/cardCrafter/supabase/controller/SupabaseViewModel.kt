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
import com.belmontCrest.cardCrafter.supabase.controller.supabaseVMFunctions.exportDeck.tryExportDeck
import com.belmontCrest.cardCrafter.supabase.controller.supabaseVMFunctions.upsertDeck.tryUpsertDeck
import com.belmontCrest.cardCrafter.supabase.model.GoogleClientResponse
import com.belmontCrest.cardCrafter.supabase.model.Owner
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.EMPTY_CARD_LIST
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.NULL_OWNER
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.SUCCESS
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.UUID_CONFLICT
import com.belmontCrest.cardCrafter.supabase.model.SBCards
import com.belmontCrest.cardCrafter.supabase.model.SBDeckList
import com.belmontCrest.cardCrafter.supabase.model.SBDecks
import com.belmontCrest.cardCrafter.supabase.model.createSupabase
import com.belmontCrest.cardCrafter.supabase.model.getSBKey
import com.belmontCrest.cardCrafter.supabase.model.getSBUrl
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.IDToken
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.realtime.selectAsFlow
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
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
    }

    private var googleClientId = MutableStateFlow("")
    val clientId = googleClientId.asStateFlow()

    private var _supabase = MutableStateFlow(createSupabase(getSBUrl(), getSBKey()))
    val supabase = _supabase.asStateFlow()
    private val thisUser = MutableStateFlow(_supabase.value.auth.currentUserOrNull())
    val currentUser = thisUser.asStateFlow()
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

    private val _owner: MutableStateFlow<Owner?> = MutableStateFlow(null)
    val owner = _owner.asStateFlow()

    suspend fun connectSupabase() {
        _supabase.value.useHTTPS
        _supabase.value.realtime.connect()
    }

    init {
        viewModelScope.launch {
            application.networkConnectivityFlow().collectLatest { isConnected ->
                if (!isConnected && isClientActive) {
                    Log.d("NETWORK", "NETWORK HAS BEEN DISCONNECTED")
                    _supabase.value.close()
                    delay(2000)
                    privateList.update {
                        SBDeckList()
                    }
                    isClientActive = false
                } else if (isConnected && !isClientActive) {
                    // Reinitialize the client only if it's not active
                    Log.d("NETWORK", "RECONNECTED!")
                    _supabase.value = createSupabase(getSBUrl(), getSBKey())
                    getDeckList()
                    getOwner()
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

    fun updateStatus() {
        thisUser.update {
            _supabase.value.auth.currentUserOrNull()
        }
    }

    suspend fun signUpWithGoogle(
        googleIdToken: String,
        rawNonce: String
    ) {
        _supabase.value.auth.signInWith(IDToken) {
            idToken = googleIdToken
            provider = Google
            nonce = rawNonce
        }
        thisUser.update {
            _supabase.value.auth.currentUserOrNull()
        }
        getOwner()
    }

    fun getDeckList() {
        try {
            viewModelScope.launch {
                _supabase.value.from("deck")
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

    fun getOwner() {
        val user = _supabase.value.auth.currentUserOrNull()
        if (user == null) {
            return
        }
        viewModelScope.launch {
            _owner.update {
                _supabase.value.from("owner")
                    .select(Columns.ALL) {
                        filter {
                            eq("user_id", user.id)
                        }
                    }.decodeSingleOrNull<Owner>()
            }
        }
    }

    suspend fun createOwner(username: String, fName: String, lName: String): Boolean {
        return withContext(Dispatchers.IO) {
            val user = _supabase.value.auth.currentUserOrNull()
            if (user == null) {
                return@withContext false
            }
            try {
                _supabase.value.from("owner")
                    .insert(
                        Owner(user.id, username, fName, lName)
                    )
                getOwner()
                return@withContext true
            } catch (e: Exception) {
                Log.d("SupabaseViewModel", "$e")
                return@withContext false
            }
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
                    return@withContext ReturnValues.DECK_EXISTS
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

                val cardList = _supabase.value.from("card")
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
                            card, _supabase.value, sbDecks.deckUUID, flashCardRepository,
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
                        return@withContext ReturnValues.NETWORK_ERROR
                    }

                    is CancellationException -> {
                        onError("Import was canceled.")
                        return@withContext ReturnValues.CANCELLED
                    }

                    else -> {
                        onError("Something went wrong.")
                        return@withContext ReturnValues.UNKNOWN_ERROR
                    }
                }
            }
            return@withContext SUCCESS
        }
    }

    suspend fun exportDeck(
        deck: Deck,
        description: String,
    ): Int {
        return withContext(Dispatchers.IO) {
            if (_owner.value == null) {
                return@withContext NULL_OWNER
            }
            /** if successful, return 0 */
            tryExportDeck(_supabase.value, deck, description, sealedUiState.value.allCTs)
        }
    }


    suspend fun updateExportedDeck(
        deck: Deck,
        description: String
    ): Int {
        return withContext(Dispatchers.IO) {
            if (_owner.value == null) {
                return@withContext NULL_OWNER
            }
            /** if successful, return 0 */
            tryUpsertDeck(_supabase.value, deck, description, sealedUiState.value.allCTs)
        }
    }

    fun getGoogleId() {
        viewModelScope.launch {
            try {
                val response: HttpResponse =
                    _supabase.value.httpClient.post("${getSBUrl()}/$POST_FUNCTION_STRING") {
                        header(HttpHeaders.Authorization, "Bearer ${getSBKey()}")
                        header(HttpHeaders.ContentType, ContentType.Application.Json)
                    }
                if (response.status == HttpStatusCode.OK) {
                    val googleResponse = response.body<GoogleClientResponse>()
                    googleClientId.update {
                        googleResponse.google_id
                    }
                } else {
                    Log.e("Error", "Unexpected response: ${response.status}")
                }
            } catch (e: Exception) {
                Log.e("Error", "Network call failed", e)
            }
        }
    }

    suspend fun createNewDeck(
        sbDecks: SBDecks,
        preferences: PreferencesManager, name: String
    ): Int {
        return withContext(Dispatchers.IO) {
            val exists = checkDeckName(name)
            if (exists > 0) {
                /** deck name already exists; return 6. */
                return@withContext ReturnValues.DECK_EXISTS
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
            val cardList = _supabase.value.from("card")
                .select(columns = Columns.ALL) {
                    filter {
                        eq("deckUUID", sbDecks.deckUUID)
                    }
                }
                .decodeList<SBCards>()
            if (cardList.isNotEmpty()) {
                cardList.map {
                    val success = downloadCards(
                        it, _supabase.value, uuid, flashCardRepository,
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
            return@withContext SUCCESS
        }
    }
}


private const val POST_FUNCTION_STRING = "functions/v1/getKeys"

