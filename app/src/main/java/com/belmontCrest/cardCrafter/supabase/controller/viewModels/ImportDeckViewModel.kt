package com.belmontCrest.cardCrafter.supabase.controller.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.belmontCrest.cardCrafter.controller.viewModels.deckViewsModels.checkIfDeckExists
import com.belmontCrest.cardCrafter.controller.viewModels.deckViewsModels.checkIfDeckUUIDExists
import com.belmontCrest.cardCrafter.model.databaseInterface.repositories.FlashCardRepository
import com.belmontCrest.cardCrafter.model.uiModels.PreferencesManager
import com.belmontCrest.cardCrafter.supabase.controller.supabaseVMFunctions.sbctToSealedCts
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.CANCELLED
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.DECK_EXISTS
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.EMPTY_CARD_LIST
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.NETWORK_ERROR
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.REPLACED_DECK
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.SUCCESS
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.UNKNOWN_ERROR
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.UUID_CONFLICT
import com.belmontCrest.cardCrafter.supabase.model.SBDeckDto
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repository.SBTablesRepository
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repository.SupabaseToRoomRepository
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.SocketException
import java.util.concurrent.CancellationException

class ImportDeckViewModel(
    private val flashCardRepository: FlashCardRepository,
    private val supabaseToRoomRepository: SupabaseToRoomRepository,
    private val sbTableRepository: SBTablesRepository,
    private val supabase: SupabaseClient
) : ViewModel() {

    /** Local import deck naming and uuid checks from online decks */
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
    /** End of checks */

    /** Local Imports from online decks */
    suspend fun importDeck(
        sbDeckDto: SBDeckDto,
        preferences: PreferencesManager,
        onProgress: (Float) -> Unit,
        onError: (String) -> Unit
    ): Int {
        return withContext(Dispatchers.IO) {
            try {
                val exists = checkDeckNameAndUUID(sbDeckDto.name, sbDeckDto.deckUUID)
                if (exists > 0) {
                    /** deck already exists; return 100. */
                    return@withContext DECK_EXISTS
                }
                val clCheck = sbTableRepository.checkCardList(sbDeckDto)
                if (clCheck.second == EMPTY_CARD_LIST) {
                    return@withContext EMPTY_CARD_LIST
                }
                val cardList = clCheck.first

                /** First we get the online cards, then we download them/
                 *  Hence we need to multiply the total by 2
                 */
                val total = cardList.size * 2
                val ctList = sbctToSealedCts(
                    cardList, supabase, onProgress = {
                        onProgress(it)
                    }, total
                )
                supabaseToRoomRepository.insertDeckList(
                    sbDeckDto, ctList, sbDeckDto.name,
                    preferences.reviewAmount.intValue,
                    preferences.cardAmount.intValue,
                    onProgress = {
                        onProgress(it)
                    }, total
                )
            } catch (e: Exception) {
                return@withContext returnError(e) {
                    onError(it)
                }.first
            }
            return@withContext SUCCESS
        }
    }

    suspend fun createNewDeck(
        sbDeckDto: SBDeckDto,
        preferences: PreferencesManager,
        name: String, onProgress: (Float) -> Unit,
        onError: (String) -> Unit
    ): Int {
        return withContext(Dispatchers.IO) {
            try {
                val exists = checkDeckName(name)
                if (exists > 0) {
                    /** deck name already exists; return 6. */
                    return@withContext DECK_EXISTS
                }
                val existingUUID = checkDeckUUID(sbDeckDto.deckUUID)
                /** If there's an existing uuid, we won't allow the user to
                 * create a new deck */
                if (existingUUID > 0) {
                    return@withContext UUID_CONFLICT
                }
                val clCheck = sbTableRepository.checkCardList(sbDeckDto)
                if (clCheck.second == EMPTY_CARD_LIST) {
                    return@withContext EMPTY_CARD_LIST
                }
                val cardList = clCheck.first

                /** First we get the online cards, then we download them/
                 *  Hence we need to multiply the total by 2
                 */
                val total = cardList.size * 2
                val ctList = sbctToSealedCts(
                    cardList, supabase, onProgress = {
                        onProgress(it)
                    }, total
                )
                supabaseToRoomRepository.insertDeckList(
                    sbDeckDto, ctList, name,
                    preferences.reviewAmount.intValue,
                    preferences.cardAmount.intValue,
                    onProgress = {
                        onProgress(it)
                    }, total
                )
            } catch (e: Exception) {
                return@withContext returnError(e) {
                    onError(it)
                }.first
            }
            return@withContext SUCCESS
        }
    }

    suspend fun replaceDeck(
        sbDeckDto: SBDeckDto,
        preferences: PreferencesManager,
        onProgress: (Float) -> Unit,
        onError: (String) -> Unit
    ): Pair<Int, String> {
        return withContext(Dispatchers.IO) {
            var name = sbDeckDto.name
            try {
                val deckSignature =
                    supabaseToRoomRepository.validateDeckSignature(sbDeckDto.deckUUID)
                /** Check the name of the 2 decks */
                if (deckSignature != null) {
                    Log.d("SupabaseVM", "Deck Signature is not null.")
                    if (deckSignature.name != sbDeckDto.name) {
                        /** If the names are not equal, check if you can just
                         *  input the name of the sbDeck and replace the name
                         *  of the local deck. */
                        val checkDeck = supabaseToRoomRepository.validateDeckName(sbDeckDto.name)
                        /** If there exists a deck with a name equal to the sbDeck,
                         *  but the uuid is NOT the same, use the name of the
                         *  deckSignature */
                        if (checkDeck != null) {
                            if (checkDeck.uuid != sbDeckDto.deckUUID) {
                                name = deckSignature.name
                            }
                        }
                    }
                }

                val clCheck = sbTableRepository.checkCardList(sbDeckDto)

                if (clCheck.second == EMPTY_CARD_LIST) {
                    return@withContext Pair(EMPTY_CARD_LIST, "")
                }
                val cardList = clCheck.first

                /** First we get the online cards, then we download them/
                 *  Hence we need to multiply the total by 2
                 */
                val total = cardList.size * 2
                val ctList = sbctToSealedCts(
                    cardList, supabase, onProgress = {
                        onProgress(it)
                    }, total
                )
                supabaseToRoomRepository.replaceDeckList(
                    sbDeckDto, ctList,
                    preferences.reviewAmount.intValue,
                    preferences.cardAmount.intValue, name,
                    onProgress = {
                        onProgress(it)
                    }, total
                )

            } catch (e: Exception) {
                return@withContext returnError(e) {
                    onError(it)
                }
            }
            if (name == sbDeckDto.name) {
                return@withContext Pair(SUCCESS, sbDeckDto.name)
            } else {
                return@withContext Pair(REPLACED_DECK, name)
            }
        }
    }
}

private fun returnError(e: Exception, onError: (String) -> Unit): Pair<Int, String> {
    return when (e) {
        is SocketException -> {
            onError("Network Error Occurred.")
            Log.e("Replace Deck SupabaseVM", "Network Error Occurred.")
            return Pair(NETWORK_ERROR, "")
        }

        is CancellationException -> {
            onError("Import was canceled.")
            Log.e("Replace Deck SupabaseVM", "Import was canceled.")
            return Pair(CANCELLED, "")
        }

        else -> {
            onError("Something went wrong.")
            Log.e("Replace Deck SupabaseVM", "Something went wrong: $e")
            return Pair(UNKNOWN_ERROR, "")
        }
    }
}