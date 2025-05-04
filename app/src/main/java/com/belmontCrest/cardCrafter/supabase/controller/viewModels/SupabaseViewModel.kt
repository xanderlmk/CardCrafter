package com.belmontCrest.cardCrafter.supabase.controller.viewModels

import android.app.Application
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.belmontCrest.cardCrafter.controller.cardHandlers.toCard
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.localDatabase.tables.ImportedDeckInfo
import com.belmontCrest.cardCrafter.model.uiModels.SealedAllCTs
import com.belmontCrest.cardCrafter.supabase.controller.networkConnectivityFlow
import com.belmontCrest.cardCrafter.supabase.model.tables.FourSelectedCards
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.NULL_CARDS
import com.belmontCrest.cardCrafter.supabase.model.createSupabase.GoogleCredentials
import com.belmontCrest.cardCrafter.supabase.model.tables.OwnerDto
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.NULL_OWNER
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.NULL_UPDATED_ON
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.SUCCESS
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.ownerRepos.ExportRepository
import com.belmontCrest.cardCrafter.supabase.model.tables.SBDeckListDto
import com.belmontCrest.cardCrafter.supabase.model.tables.SBDeckDto
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.authRepo.AuthRepository
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.SBTablesRepository
import com.belmontCrest.cardCrafter.supabase.model.tables.CardsToDisplay
import com.belmontCrest.cardCrafter.supabase.model.tables.add
import com.belmontCrest.cardCrafter.supabase.model.tables.isEmpty
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.annotations.SupabaseInternal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@OptIn(
    SupabaseExperimental::class,
    ExperimentalCoroutinesApi::class,
    SupabaseInternal::class
)
@RequiresApi(Build.VERSION_CODES.Q)
class SupabaseViewModel(
    private val exportRepository: ExportRepository,
    private val sbTableRepository: SBTablesRepository,
    private val authRepository: AuthRepository,
    application: Application
) : AndroidViewModel(application) {
    companion object {
        private const val TIMEOUT_MILLIS = 4_000L
        private const val SUPABASE_VM = "SupabaseVM"
    }

    private var _googleClientId = MutableStateFlow("")
    val googleClientId = _googleClientId.asStateFlow()

    private val _currentUser = MutableStateFlow(authRepository.getCurrentUser())
    val currentUser = _currentUser.asStateFlow()
    private var isClientActive = true

    private val _deckList = MutableStateFlow(SBDeckListDto())
    val deckList = _deckList.asStateFlow()
    private val uuid = MutableStateFlow("")
    val deck: StateFlow<SBDeckDto?> = uuid.map { currentUUID ->
        _deckList.value.list.find { it.deckUUID == currentUUID }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = null
    )
    private val deckId = MutableStateFlow(0)
    val pickedDeck: StateFlow<Deck?> = deckId.flatMapLatest { id ->
        if (id == 0) {
            flowOf(null)
        } else {
            exportRepository.getDeckStream(id).map { it }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = null
    )
    val sealedAllCTs = deckId.flatMapLatest { id ->
        if (id == 0) {
            flowOf(SealedAllCTs())
        } else {
            exportRepository.getAllCardTypes(id).map {
                SealedAllCTs(it.toMutableList())
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = SealedAllCTs()
    )
    private val importedDeckInfo: StateFlow<ImportedDeckInfo?> = deckId.flatMapLatest { id ->
        if (id == 0) {
            flowOf(null)
        } else {
            exportRepository.getImportedDeckInfo(id).map { it }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    private val _ownerDto: MutableStateFlow<OwnerDto?> = MutableStateFlow(null)
    val owner = _ownerDto.asStateFlow()

    private val _cardsToDisplay: MutableStateFlow<CardsToDisplay?> = MutableStateFlow(null)

    val selectedCards: StateFlow<FourSelectedCards> = combine(
        sealedAllCTs, _cardsToDisplay
    ) { sealedAll, cardsTD ->
        if (cardsTD == null) {
            FourSelectedCards()
        } else {
            // Lookup a CT by cardIdentifier
            fun findCT(cardIdentifier: String?): CT? =
                cardIdentifier?.let { lookup ->
                    sealedAll.allCTs.firstOrNull { it.toCard().cardIdentifier == lookup }
                }
            // Build the FourSelectedCards
            FourSelectedCards(
                first = findCT(cardsTD.cardOne),
                second = findCT(cardsTD.cardTwo),
                third = findCT(cardsTD.cardThree),
                fourth = findCT(cardsTD.cardFour)
            )

        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = FourSelectedCards()
    )

    private fun getOnlineCTD(uuid: String) {
        viewModelScope.launch {
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

    fun addCardsToDisplay(cardIdentifier: String) {
        _cardsToDisplay.update {
            it?.add(cardIdentifier)
        }
    }


    fun updateCardsToDisplayUUID(uuid: String) {
        _cardsToDisplay.update {
            CardsToDisplay(deckUUID = uuid)
        }.also {
            getOnlineCTD(uuid)
        }
    }

    fun disconnectSupabaseRT() {
        sbTableRepository.disconnectRealtime()
    }

    fun connectSupabase() {
        viewModelScope.launch {
            sbTableRepository.connectRealtime()
        }
    }

    init {
        viewModelScope.launch {
            application.networkConnectivityFlow().collectLatest { isConnected ->
                if (!isConnected && isClientActive) {
                    Log.d("NETWORK", "NETWORK HAS BEEN DISCONNECTED")
                    authRepository.closeSupabase()
                    delay(2000)
                    _deckList.update {
                        SBDeckListDto()
                    }
                    _ownerDto.update {
                        null
                    }
                    isClientActive = false
                } else if (isConnected && !isClientActive) {
                    // Reinitialize the client only if it's not active
                    authRepository.reCreateSupabase()
                    Log.d("NETWORK", "RECONNECTED!")
                    getDeckList()
                    getOwner()
                    isClientActive = true
                }
            }
        }
        viewModelScope.launch {
            delay(1250)
            getOwner()
        }
    }

    /** Google Oauth */
    suspend fun getGoogleId(): Pair<Boolean, String> {
        return withContext(Dispatchers.IO) {
            authRepository.getGoogleCredentials().let { credentials ->
                when (credentials) {
                    is GoogleCredentials.Success -> {
                        _googleClientId.update {
                            credentials.credentials
                        }
                        Pair(true, "")
                    }

                    is GoogleCredentials.Failure -> {
                        Pair(false, credentials.errorMessage)
                    }
                }
            }
        }
    }

    suspend fun signUpWithGoogle(
        googleIdToken: String,
        rawNonce: String
    ): Boolean {
        return withContext(Dispatchers.IO) {
            authRepository.signInWithGoogle(googleIdToken, rawNonce).let {
                _currentUser.update {
                    authRepository.getCurrentUser()
                }
                getOwner()
                it
            }
        }
    }

    /** End of Google Oauth */

    suspend fun signUpWithEmail(email: String, password: String): String {
        return withContext(Dispatchers.IO) {
            authRepository.signUpWithEmail(email, password)
        }
    }

    suspend fun deepLinker(intent: Intent, callback: (String, String) -> Unit): String {
        return withContext(Dispatchers.IO) {
            authRepository.deepLinker(intent) { email, createdAt ->
                callback(email, createdAt)
            }
        }
    }

    suspend fun signInSyncedDBUser(): String {
        return withContext(Dispatchers.IO) {
            authRepository.signInSyncedDBUser()
        }
    }

    suspend fun signInWithEmail(email: String, password: String): String {
        return withContext(Dispatchers.IO) {
            authRepository.signInWithEmail(email, password).let {
                _currentUser.update {
                    authRepository.getCurrentUser()
                }
                getOwner()
                it
            }
        }
    }

    fun changeDeckId(id: Int) {
        deckId.update { id }
    }

    fun updateUUID(thisUUID: String) {
        uuid.update { thisUUID }
    }

    fun updateStatus() {
        _currentUser.update {
            authRepository.getCurrentUser()
        }
    }

    fun getDeckList() {
        viewModelScope.launch {
            sbTableRepository.getDeckList().collectLatest { list ->
                _deckList.update {
                    SBDeckListDto(list)
                }
            }
        }
    }

    /** If users want to become an Owner... here the functions lol. */
    fun getOwner() {
        viewModelScope.launch {
            _ownerDto.update {
                authRepository.getOwner()
            }
        }
    }

    suspend fun createOwner(username: String, fName: String, lName: String): Boolean {
        return withContext(Dispatchers.IO) {
            authRepository.createOwner(username, fName, lName)
        }
    }
    /** End of owner functions */

    /** Functions for when the user decides to export */
    suspend fun exportDeck(
        deck: Deck,
        description: String,
    ): Int {
        return withContext(Dispatchers.IO) {
            if (_ownerDto.value == null) {
                return@withContext NULL_OWNER
            }
            val ctd = _cardsToDisplay.value
            if (ctd == null || ctd.isEmpty()) {
                return@withContext NULL_CARDS
            }
            val result =
                sbTableRepository.exportDeck(deck, description, sealedAllCTs.value.allCTs, ctd)

            if (result.timestamp.isNotBlank()) {
                exportRepository.updateNewInfo(
                    ImportedDeckInfo(uuid = deck.uuid, lastUpdatedOn = result.timestamp), deck.id
                )
            }
            /** if successful, return 0 */
            result.returnValue
        }
    }

    suspend fun updateExportedDeck(
        deck: Deck,
        description: String
    ): Int {
        return withContext(Dispatchers.IO) {
            if (_ownerDto.value == null) {
                return@withContext NULL_OWNER
            }
            val ctd = _cardsToDisplay.value ?: return@withContext NULL_CARDS
            val luo = importedDeckInfo.value?.lastUpdatedOn
            if (luo == null) {
                Log.e(SUPABASE_VM, "No import Deck info")
                return@withContext NULL_UPDATED_ON
            }

            val result = sbTableRepository.upsertDeck(
                deck, description, sealedAllCTs.value.allCTs, ctd, luo
            )

            if (result.timestamp.isNotBlank()) {
                exportRepository.updateNewInfo(
                    ImportedDeckInfo(uuid = deck.uuid, lastUpdatedOn = result.timestamp), deck.id
                )
            }
            /** if successful, return 0 */
            result.returnValue
        }
    }
    /** End of export decks */
}
