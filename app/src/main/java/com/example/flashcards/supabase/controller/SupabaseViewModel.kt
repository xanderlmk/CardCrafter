package com.example.flashcards.supabase.controller

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.model.tablesAndApplication.CT
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.supabase.model.SBDeckList
import com.example.flashcards.supabase.model.SBDecks
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.IDToken
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@OptIn(SupabaseExperimental::class, ExperimentalCoroutinesApi::class)
class SupabaseViewModel(
) : ViewModel() {
    private val privateList = MutableStateFlow(SBDeckList())
    val deckList = privateList.asStateFlow()
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
                        /** return 4 means the deck was uploaded successfully */
                        success = 4
                        return@launch
                    }
                    cts.map { ct ->
                        when (ct) {
                            is CT.Basic -> {
                                try {
                                    val response = insertCard(
                                        supabase,
                                        uuid = deck.uuid,
                                        type = ct.card.type
                                    )
                                    insertBasicCT(response.id, supabase, ct.basicCard)
                                } catch (e: Exception) {
                                    Log.d(
                                        "SupabaseViewModel", "Unable to upload Card: $e"
                                    )
                                    /** return 2 means the cardType upload was unsuccessful. */
                                    success = 2
                                    return@launch
                                }

                            }

                            is CT.Hint -> {
                                try {
                                    val response = insertCard(
                                        supabase,
                                        uuid = deck.uuid,
                                        type = ct.card.type
                                    )
                                    insertHintCT(response.id, supabase, ct.hintCard)
                                } catch (e: Exception) {
                                    Log.d(
                                        "SupabaseViewModel", "Unable to upload Card: $e"
                                    )
                                    /** return 2 means the cardType upload was unsuccessful. */
                                    success = 2
                                    return@launch
                                }
                            }

                            is CT.ThreeField -> {
                                try {
                                    val response = insertCard(
                                        supabase,
                                        uuid = deck.uuid,
                                        type = ct.card.type
                                    )
                                    insertThreeCT(response.id, supabase, ct.threeFieldCard)
                                } catch (e: Exception) {
                                    Log.d(
                                        "SupabaseViewModel", "Unable to upload Card: $e"
                                    )
                                    /** return 2 means the cardType upload was unsuccessful. */
                                    success = 2
                                    return@launch
                                }
                            }

                            is CT.MultiChoice -> {
                                try {
                                    val response = insertCard(
                                        supabase,
                                        uuid = deck.uuid,
                                        type = ct.card.type
                                    )
                                    insertMultiCT(response.id, supabase, ct.multiChoiceCard)
                                } catch (e: Exception) {
                                    Log.d(
                                        "SupabaseViewModel", "Unable to upload Card: $e"
                                    )
                                    /** return 2 means the cardType upload was unsuccessful. */
                                    success = 2
                                    return@launch
                                }
                            }

                            is CT.Math -> {
                                try {
                                    val response = insertCard(
                                        supabase,
                                        uuid = deck.uuid,
                                        type = ct.card.type
                                    )
                                    insertMathCT(response.id, supabase, ct.mathCard)
                                } catch (e: Exception) {
                                    Log.d(
                                        "SupabaseViewModel", "Unable to upload Card: $e"
                                    )
                                    /** return 2 means the cardType upload was unsuccessful. */
                                    success = 2
                                    return@launch
                                }
                            }

                            else -> {
                                Log.d("SupabaseViewModel", "No type found for card")
                                /** return 3 means the cardType doesn't exist. */
                                success = 3
                                return@launch
                            }
                        }
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
