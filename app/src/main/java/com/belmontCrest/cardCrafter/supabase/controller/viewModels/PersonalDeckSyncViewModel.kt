package com.belmontCrest.cardCrafter.supabase.controller.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.UserSyncedInfoRepository
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repository.PersonalDeckSyncRepository
import com.belmontCrest.cardCrafter.supabase.model.tables.PersonalDecks
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PersonalDeckSyncViewModel(
    private val personalDeckSyncRepository: PersonalDeckSyncRepository,
    private val userSyncedInfoRepository: UserSyncedInfoRepository
) : ViewModel() {

    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.Idle)
    val syncStatus = _syncStatus.asStateFlow()

    private val _remoteDecks = MutableStateFlow<List<PersonalDecks>>(emptyList())
    val remoteDecks = _remoteDecks.asStateFlow()

    fun syncDecks() {
        _syncStatus.value = SyncStatus.Syncing

        viewModelScope.launch {
            try {
                val decks = userSyncedInfoRepository.getDB()
                val result = personalDeckSyncRepository.syncUserDecks(decks)

                when (result) {
                    ReturnValues.SUCCESS -> {
                        _syncStatus.value = SyncStatus.Success
                    }
                    ReturnValues.NULL_USER -> {
                        _syncStatus.value = SyncStatus.Error("User not authenticated")
                    }
                    ReturnValues.EMPTY_CARD_LIST -> {
                        _syncStatus.value = SyncStatus.Error("No decks to sync")
                    }
                    else -> {
                        _syncStatus.value = SyncStatus.Error("Sync failed with code: $result")
                    }
                }
            } catch (e: Exception) {
                Log.e("PersonalDeckSyncVM", "Error syncing decks: ${e.message}")
                _syncStatus.value = SyncStatus.Error("Error: ${e.message}")
            }
        }
    }

    fun fetchRemoteDecks() {
        viewModelScope.launch {
            try {
                val (decks, result) = personalDeckSyncRepository.fetchRemoteDecks()

                if (result == ReturnValues.SUCCESS) {
                    _remoteDecks.value = decks
                } else {
                    Log.e("PersonalDeckSyncVM", "Failed to fetch remote decks: $result")
                }
            } catch (e: Exception) {
                Log.e("PersonalDeckSyncVM", "Error fetching remote decks: ${e.message}")
            }
        }
    }

    fun resetSyncStatus() {
        _syncStatus.value = SyncStatus.Idle
    }

    sealed class SyncStatus {
        object Idle : SyncStatus()
        object Syncing : SyncStatus()
        object Success : SyncStatus()
        data class Error(val message: String) : SyncStatus()
    }
}