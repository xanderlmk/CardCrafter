package com.example.flashcards.controller.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.model.uiModels.CardListUiState
import com.example.flashcards.model.repositories.CardTypeRepository
import com.example.flashcards.model.tablesAndApplication.AllCardTypes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

class CardTypeViewModel(private val cardTypeRepository: CardTypeRepository,
                        savedStateHandle: SavedStateHandle) : ViewModel() {

    private val uiState = MutableStateFlow(CardListUiState())

    var cardListUiState = uiState.asStateFlow()
    var backupCardList: List<AllCardTypes> = cardListUiState.value.allCards
    var backupCard: AllCardTypes? = cardListUiState.value.allCards.firstOrNull()
    companion object {
        private const val TIMEOUT_MILLIS = 4_000L
    }

    private val errorMessage = MutableStateFlow<String?>(null)

    fun updateBackupList() {
        backupCardList = uiState.value.allCards
    }



    fun updateBackupCard(index: Int) {
        backupCard = uiState.value.allCards[index]
    }

    /*suspend fun restoreBackup(index: Int){
        uiState.map {
            it.copy(allCards = backupCardList)
        }.collect {
            uiState.value = it
        }
    }*/


    suspend fun getDueTypesForDeck(deckId: Int){
        return withContext(Dispatchers.IO)
        {
            var complete = false
            try {
                viewModelScope.launch {
                //    viewModelScope.launch(Dispatchers.IO){
                    withTimeout(TIMEOUT_MILLIS) {
                        cardTypeRepository.getDueAllCardTypes(deckId).map { allCards ->
                            CardListUiState(allCards = allCards)
                        }.collect { state ->
                            uiState.value = state
                            complete = true
                        }
                    }
                }
                while (!complete) {
                    delay(20)
                }
                return@withContext
            } catch (e: TimeoutCancellationException) {
                errorMessage.value = "Request timed out. Please try again."
                println(e)
                return@withContext
            }
        }
    }

    fun getAllTypesForDeck(deckId: Int) {
        viewModelScope.launch {
            cardTypeRepository.getAllCardTypes(deckId).map { allCards ->
                CardListUiState(allCards = allCards)
            }.collect { state ->
                uiState.value = state
            }
            clearErrorMessage()
        }
    }

    fun setErrorMessage(message: String) {
        uiState.value = uiState.value.copy(errorMessage = message)
    }

    fun clearErrorMessage() {
        uiState.value = uiState.value.copy(errorMessage = "")
    }
}



