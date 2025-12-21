package com.belmontCrest.cardCrafter.supabase.controller.view.models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belmontCrest.cardCrafter.local.db.tables.Deck
import com.belmontCrest.cardCrafter.local.db.tables.ImportedDeckInfo
import com.belmontCrest.cardCrafter.model.ui.states.SealedAllCTs
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues
import com.belmontCrest.cardCrafter.supabase.model.createSupabase.GoogleCredentials
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.SBTablesRepository
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.authRepo.AuthRepository
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.ownerRepos.ExportRepository
import com.belmontCrest.cardCrafter.supabase.model.tables.CardsToDisplay
import com.belmontCrest.cardCrafter.supabase.model.tables.FourSelectedCards
import com.belmontCrest.cardCrafter.supabase.model.tables.SBDeckDto
import com.belmontCrest.cardCrafter.supabase.model.tables.isEmpty
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.annotations.SupabaseInternal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
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
class SupabaseViewModel(
    private val exportRepository: ExportRepository,
    private val sbTableRepository: SBTablesRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 4_000L
        private const val SUPABASE_VM = "SupabaseVM"
    }

    val googleClientId = authRepository.googleClientId

    val currentUser = authRepository.currentUser

    val deckList = sbTableRepository.deckList
    private val uuid = MutableStateFlow("")
    val deck: StateFlow<SBDeckDto?> = uuid.map { currentUUID ->
        deckList.value.list.find { it.deckUUID == currentUUID }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = null
    )
    val pickedDeck = exportRepository.pickedDeck.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = null
    )
    val sealedAllCTs = exportRepository.sealedAllCTs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SealedAllCTs()
    )

    val owner = authRepository.owner

    val selectedCards = exportRepository.selectedCards.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = FourSelectedCards()
    )

    private val importedDeckInfo = exportRepository.importedDeckInfo.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    private fun getOnlineCTD(uuid: String) {
        viewModelScope.launch {
            try {
                val result = sbTableRepository.getCardsToDisplay(uuid)
                if (result.second == ReturnValues.SUCCESS) {
                    exportRepository.updateCardToDisplay(result.first)
                } else {
                    Log.e("SupabaseVM", "Failed to retrieve cards")
                }
            } catch (e: Exception) {
                Log.e("SupabaseVM", "$e")
            }
        }
    }

    fun updateCardsToDisplayUUID(uuid: String) =
        exportRepository.updateCardToDisplay(CardsToDisplay(deckUUID = uuid))
            .also { getOnlineCTD(uuid) }

    /** Google Oauth ID */
    suspend fun getGoogleId(): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        if (googleClientId.value.isBlank())
            authRepository.getGoogleCredentials().let { credentials ->
                when (credentials) {
                    is GoogleCredentials.Success -> {
                        authRepository.updateGoogleId(credentials.credentials)
                        Pair(true, "")
                    }

                    is GoogleCredentials.Failure -> {
                        Pair(false, credentials.errorMessage)
                    }
                }
            }
        else Pair(true, "")
    }

    suspend fun signUpWithGoogle(googleIdToken: String, rawNonce: String): Boolean {
        return withContext(Dispatchers.IO) {
            authRepository.signInWithGoogle(googleIdToken, rawNonce).let {
                authRepository.getCurrentUser()
                getOwner()
                it
            }
        }
    }

    suspend fun signUpWithEmail(email: String, password: String): String {
        return withContext(Dispatchers.IO) {
            authRepository.signUpWithEmail(email, password)
        }
    }

    suspend fun signInWithEmail(email: String, password: String) =
        withContext(Dispatchers.IO) {
            authRepository.signInWithEmail(email, password).let {
                authRepository.getCurrentUser()
                getOwner()
                it
            }
        }

    fun changeDeckId(id: Int) = exportRepository.updateDeckId(id)

    fun updateUUID(thisUUID: String) = uuid.update { thisUUID }

    fun updateStatus() {
        authRepository.getCurrentUser()
    }

    fun getDeckList() {
        viewModelScope.launch {
            sbTableRepository.getDeckList().collectLatest { list ->
                sbTableRepository.updateSBDeckList(list)
            }
        }
    }

    /** If users want to become an Owner... here the functions lol. */
    fun getOwner() = viewModelScope.launch { authRepository.getOwner() }

    suspend fun createOwner(
        username: String,
        fName: String,
        lName: String
    ): Boolean {
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
            if (owner.value == null) {
                return@withContext ReturnValues.NULL_OWNER
            }
            val ctd = exportRepository.getCTD()
            if (ctd == null || ctd.isEmpty()) {
                return@withContext ReturnValues.NULL_CARDS
            }
            val result =
                sbTableRepository.exportDeck(
                    deck,
                    description,
                    sealedAllCTs.value.allCTs,
                    ctd
                )

            if (result.timestamp.isNotBlank()) {
                exportRepository.updateNewInfo(
                    ImportedDeckInfo(uuid = deck.uuid, lastUpdatedOn = result.timestamp),
                    deck.id
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
            if (owner.value == null) {
                return@withContext ReturnValues.NULL_OWNER
            }
            val ctd = exportRepository.getCTD() ?: return@withContext ReturnValues.NULL_CARDS
            val luo = importedDeckInfo.value?.lastUpdatedOn
            if (luo == null) {
                Log.e(SUPABASE_VM, "No import Deck info")
                return@withContext ReturnValues.NULL_UPDATED_ON
            }

            val result = sbTableRepository.upsertDeck(
                deck, description, sealedAllCTs.value.allCTs, ctd, luo
            )

            if (result.timestamp.isNotBlank()) {
                exportRepository.updateNewInfo(
                    ImportedDeckInfo(uuid = deck.uuid, lastUpdatedOn = result.timestamp),
                    deck.id
                )
            }
            /** if successful, return 0 */
            result.returnValue
        }
    }
    /** End of export decks */
}