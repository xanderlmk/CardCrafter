package com.belmontCrest.cardCrafter.supabase.controller.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.belmontCrest.cardCrafter.controller.viewModels.deckViewsModels.checkIfDeckExists
import com.belmontCrest.cardCrafter.controller.viewModels.deckViewsModels.checkIfDeckUUIDExists
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.FlashCardRepository
import com.belmontCrest.cardCrafter.model.uiModels.PreferencesManager
import com.belmontCrest.cardCrafter.supabase.controller.supabaseVMFunctions.converters.sbctToSealedCts
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.BASIC_CT_ERROR
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.CANCELLED
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.DECK_EXISTS
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.EMPTY_CARD_LIST
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.HINT_CT_ERROR
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.MULTI_CT_ERROR
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.NETWORK_ERROR
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.NOTATION_CT_ERROR
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.REPLACED_DECK
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.SUCCESS
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.THREE_CT_ERROR
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.UNKNOWN_ERROR
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.UUID_CONFLICT
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCardWithCT
import com.belmontCrest.cardCrafter.supabase.model.tables.SBDeckDto
import com.belmontCrest.cardCrafter.supabase.model.tables.SealedCT
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repository.ImportRepository
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repository.SupabaseToRoomRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.SocketException
import java.util.concurrent.CancellationException

class ImportDeckViewModel(
    private val flashCardRepository: FlashCardRepository,
    private val supabaseToRoomRepository: SupabaseToRoomRepository,
    private val importRepository: ImportRepository,
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

                val cardObject = getCardList(sbDeckDto) {
                    onProgress(it)
                }
                if (cardObject.returnValue != SUCCESS) {
                    return@withContext cardObject.returnValue
                }
                supabaseToRoomRepository.insertDeckList(
                    sbDeckDto, cardObject.cardList, sbDeckDto.name,
                    preferences.reviewAmount.intValue,
                    preferences.cardAmount.intValue,
                    onProgress = {
                        onProgress(it)
                    }, cardObject.total
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

                val cardObject = getCardList(sbDeckDto) {
                    onProgress(it)
                }
                if (cardObject.returnValue != SUCCESS) {
                    return@withContext cardObject.returnValue
                }
                supabaseToRoomRepository.insertDeckList(
                    sbDeckDto, cardObject.cardList, name,
                    preferences.reviewAmount.intValue,
                    preferences.cardAmount.intValue,
                    onProgress = {
                        onProgress(it)
                    }, cardObject.total
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
                val cardObject = getCardList(sbDeckDto) {
                    onProgress(it)
                }
                if (cardObject.returnValue != SUCCESS) {
                    return@withContext Pair(cardObject.returnValue, "")
                }
                supabaseToRoomRepository.replaceDeckList(
                    sbDeckDto, cardObject.cardList,
                    preferences.reviewAmount.intValue,
                    preferences.cardAmount.intValue, name,
                    onProgress = {
                        onProgress(it)
                    }, cardObject.total
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

    private suspend fun getCardList(
        sbDeckDto: SBDeckDto, onProgress: (Float) -> Unit
    ): CardObject {
        val basicCheck = importRepository.checkBasicCardList(sbDeckDto.deckUUID)
        val threeCheck = importRepository.checkThreeCardList(sbDeckDto.deckUUID)
        val hintCheck = importRepository.checkHintCardList(sbDeckDto.deckUUID)
        val multiCheck = importRepository.checkMultiCardList(sbDeckDto.deckUUID)
        val notationCheck = importRepository.checkNotationCardList(sbDeckDto.deckUUID)
        val checkLists = checkList(
            basic = basicCheck.second, three = threeCheck.second,
            hint = hintCheck.second, multi = multiCheck.second,
            notation = notationCheck.second
        )
        if (checkLists != SUCCESS) {
            return CardObject(
                cardList = listOf(), returnValue = checkLists, total = 0
            )
        }

        val allCards = mutableListOf<SBCardWithCT>().apply {
            addAll(basicCheck.first)
            addAll(hintCheck.first)
            addAll(threeCheck.first)
            addAll(multiCheck.first)
            addAll(notationCheck.first)
        }

        if (allCards.isEmpty()) {
            return CardObject(
                cardList = listOf(), returnValue = EMPTY_CARD_LIST, total = 0
            )
        }

        val sortedCards = allCards.sortedBy { it.sortKey() }

        /** First we map all the cards, then we download them/
         *  Hence we need to multiply the total by 2
         */
        val total = allCards.size * 2
        val ctList = sbctToSealedCts(
            sortedCards, onProgress = {
                onProgress(it)
            }, total
        )
        return CardObject(
            cardList = ctList, returnValue = SUCCESS, total = total
        )
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

private fun checkList(
    basic: Int, three: Int, hint: Int, multi: Int, notation: Int
): Int {
    if (basic != SUCCESS) return BASIC_CT_ERROR
    if (hint != SUCCESS) return HINT_CT_ERROR
    if (three != SUCCESS) return THREE_CT_ERROR
    if (multi != SUCCESS) return MULTI_CT_ERROR
    if (notation != SUCCESS) return NOTATION_CT_ERROR
    return SUCCESS
}

private data class CardObject(
    val cardList: List<SealedCT>,
    val returnValue: Int,
    val total: Int
)