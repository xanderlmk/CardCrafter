package com.belmontCrest.cardCrafter.supabase.controller.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belmontCrest.cardCrafter.localDatabase.tables.SyncedDeckInfo
import com.belmontCrest.cardCrafter.localDatabase.tables.toInstant
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.SUCCESS
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.UserSyncedInfoRepository
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.PersonalDeckSyncRepository
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
                val uuid = personalDeckSyncRepository.getUserUUID()
                if (uuid == null) {
                    _syncStatus.value = SyncStatus.Error("Null Users"); return@launch
                }
                val syncInfo = userSyncedInfoRepository.getSyncInfo(uuid)
                if (syncInfo != null) {
                    val remoteSync = personalDeckSyncRepository.getLastUpdatedOn()
                    if (remoteSync.second != SUCCESS) {
                        SyncStatus.Error("Error")
                        return@launch
                    }

                    if (remoteSync.first != null) {
                        Log.d("test", "here")
                        val remoteInstant = remoteSync.first!!.updatedOn.toInstant()
                        val localInstant = syncInfo.lastUpdatedOn.toInstant()
                        Log.d("test", "hereV2")
                        if (remoteInstant != localInstant) {
                            Log.d("test", "not equals")
                            SyncStatus.Error("Sync Conflict")
                            return@launch
                        }
                    }
                }
                val decks = userSyncedInfoRepository.getDB()
                val result = personalDeckSyncRepository.syncUserDecks(decks)
                when (result.second) {
                    ReturnValues.SUCCESS -> {

                        userSyncedInfoRepository.insertOrUpdateSyncInfo(SyncedDeckInfo(
                            uuid, result.first
                        ))
                        _syncStatus.value = SyncStatus.Success
                        Log.d("PDSVM", "Successfully Synced Decks")
                    }

                    ReturnValues.NULL_USER -> {
                        Log.e("PDSVM", "Sync failed with code: $result")
                        _syncStatus.value = SyncStatus.Error("User not authenticated")
                    }

                    ReturnValues.EMPTY_CARD_LIST -> {
                        Log.e("PDSVM", "Sync failed with code: $result")
                        _syncStatus.value = SyncStatus.Error("No decks to sync")
                    }

                    else -> {
                        Log.e("PDSVM", "Sync failed with code: $result")
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


}

sealed class SyncStatus {
    data object Idle : SyncStatus()
    data object Syncing : SyncStatus()
    data object Success : SyncStatus()
    data class Error(val message: String) : SyncStatus() {
        fun returnMessage(): String{ return message}
    }

}