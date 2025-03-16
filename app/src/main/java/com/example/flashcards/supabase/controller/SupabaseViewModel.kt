package com.example.flashcards.supabase.controller

import android.database.sqlite.SQLiteConstraintException
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.model.repositories.CardTypeRepository
import com.example.flashcards.model.repositories.FlashCardRepository
import com.example.flashcards.model.repositories.ScienceSpecificRepository
import com.example.flashcards.model.tablesAndApplication.CT
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.model.uiModels.PreferencesManager
import com.example.flashcards.supabase.model.SBCards
import com.example.flashcards.supabase.model.SBDeckList
import com.example.flashcards.supabase.model.SBDecks
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
import java.util.Date


@OptIn(SupabaseExperimental::class, ExperimentalCoroutinesApi::class)
class SupabaseViewModel(
    private val flashCardRepository: FlashCardRepository,
    private val cardTypeRepository: CardTypeRepository,
    private val sSRepository: ScienceSpecificRepository
) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 4_000L
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

    suspend fun getDeckList(supabase: SupabaseClient) {
        supabase.from("deck")
            .selectAsFlow(SBDecks::deckUUID).collect { list ->
                privateList.update {
                    it.copy(
                        list = list
                    )
                }
            }
    }

    private suspend fun checkIfDeckExists(name: String): Int {
        return withContext(Dispatchers.IO) {
            try {
                flashCardRepository.checkIfDeckExists(name)
            } catch (e: SQLiteConstraintException) {
                Log.d(
                    "SupabaseViewModel",
                    "Error checking deck existence: ${e.message}"
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    suspend fun importDeck(
        sbDecks: SBDecks, supabase: SupabaseClient, preferences: PreferencesManager
    ): Int {
        var returnValue = 0
        viewModelScope.launch {
            val exists = checkIfDeckExists(sbDecks.name)
            if (exists > 0) {
                /** deck already exists; return 100. */
                returnValue = 100
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
            if (cardList.isNotEmpty()) {
                cardList.map {
                    val success = downloadCards(
                        it, supabase, sbDecks.deckUUID, flashCardRepository,
                        cardTypeRepository, deckId.toInt(), sSRepository, preferences
                    )
                    if (success != 0){
                        returnValue = success
                        return@launch
                    }
                }
            } else {
                Log.d("SupabaseViewModel", "List is empty!!")
            }
        }.join()

        return returnValue
    }

    suspend fun insertDeckAndCards(
        deck: Deck,
        supabase: SupabaseClient,
        cts: List<CT>,
        description: String
    ): Int {
        val user = supabase.auth.currentUserOrNull()
        if (user != null) {
            try {
                /** if successful, return 0 */
                var success = 0
                viewModelScope.launch {
                    try {
                        supabase.from("deck")
                            .insert(
                                SBDecks(
                                    deckUUID = deck.uuid,
                                    user_id = user.id,
                                    name = deck.name,
                                    description = description
                                )
                            )
                    } catch (e: Exception) {
                        Log.d("SupabaseViewModel", "Couldn't upload Deck : $e")
                        /** return 4 means the deck did not upload. */
                        success = 4
                        return@launch
                    }
                    val insertCards = insertCTList(cts, supabase, deck)
                    if (!insertCards) {
                        /** return 2 means some cardType upload was unsuccessful. */
                        success = 2
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
        }
        Log.d("SupabaseViewModel", "User is null!")
        /** return 1 means the user does not exist somehow */
        return 1
    }
}
