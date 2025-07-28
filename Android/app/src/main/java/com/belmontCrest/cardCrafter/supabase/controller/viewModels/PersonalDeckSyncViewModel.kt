package com.belmontCrest.cardCrafter.supabase.controller.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belmontCrest.cardCrafter.localDatabase.tables.SyncedDeckInfo
import com.belmontCrest.cardCrafter.localDatabase.tables.toInstant
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.NO_DECKS_TO_SYNC
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.NULL_USER
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.SUCCESS
import com.belmontCrest.cardCrafter.supabase.model.SyncStatus
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.personalSyncedRepos.UserSyncedInfoRepository
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.personalSyncedRepos.PersonalDeckSyncRepository
import com.belmontCrest.cardCrafter.supabase.model.tables.ListOfDecks
import com.belmontCrest.cardCrafter.supabase.model.tables.PDUpdatedOn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class PersonalDeckSyncViewModel(
    private val personalDeckSyncRepository: PersonalDeckSyncRepository,
    private val userSyncedInfoRepository: UserSyncedInfoRepository
) : ViewModel() {

    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.Idle)
    val syncStatus = _syncStatus.asStateFlow()
    companion object {
        const val PDSVM = "PDSVM"
        private val json = Json
    }

    fun syncDecks() {
        _syncStatus.update { SyncStatus.Syncing }
        viewModelScope.launch {
            try {
                val uuid = personalDeckSyncRepository.getUserUUID() ?: run {
                    _syncStatus.update { SyncStatus.Error("Null user") }
                    return@launch
                }
                val syncInfo = userSyncedInfoRepository.getSyncInfo(uuid)
                if (syncInfo != null) {
                    val remoteSync = personalDeckSyncRepository.getLastUpdatedOn()
                    if (remoteSync.second != SUCCESS) {
                        _syncStatus.update { SyncStatus.Error("Error") }
                        return@launch
                    }
                    if (remoteSync.first != null) {
                        val remoteInstant = remoteSync.first!!.updatedOn.toInstant()
                        val localInstant = syncInfo.lastUpdatedOn.toInstant()
                        if (remoteInstant != localInstant) {
                            _syncStatus.update { SyncStatus.Conflict }
                            return@launch
                        }
                    }
                } else {
                    val remoteSync = personalDeckSyncRepository.getLastUpdatedOn()
                    if (remoteSync.second != SUCCESS) {
                        _syncStatus.update { SyncStatus.Error("Error") }
                        return@launch
                    }
                    if (remoteSync.first != null) {
                        _syncStatus.update { SyncStatus.Conflict }
                        return@launch
                    }
                }
                val decks = userSyncedInfoRepository.getDB()
                val result = personalDeckSyncRepository.syncUserDecks(decks)
                when (result.second) {
                    SUCCESS -> {
                        userSyncedInfoRepository.insertOrUpdateSyncInfo(
                            SyncedDeckInfo(
                                uuid, result.first
                            )
                        )
                        _syncStatus.update { SyncStatus.Success }
                        Log.d(PDSVM, "Successfully Synced Decks")
                    }

                    NULL_USER -> {
                        Log.e(PDSVM, "Sync failed with code: $result")
                        _syncStatus.update { SyncStatus.Error("User not authenticated") }
                    }

                    NO_DECKS_TO_SYNC -> {
                        Log.e(PDSVM, "Sync failed with code: $result")
                        _syncStatus.update { SyncStatus.Error("No decks to sync") }
                    }

                    else -> {
                        Log.e(PDSVM, "Sync failed with code: $result")
                        _syncStatus.update { SyncStatus.Error("Sync failed with code: $result") }
                    }
                }
            } catch (e: Exception) {
                Log.e(PDSVM, "Error syncing decks: ${e.message}")
                _syncStatus.update { SyncStatus.Error("Error: ${e.message}") }
            }
        }
    }

    fun fetchRemoteDecks() {
        viewModelScope.launch {
            try {
                val uuid = personalDeckSyncRepository.getUserUUID() ?: run {
                    _syncStatus.update { SyncStatus.Error("Null user") }
                    return@launch
                }
                _syncStatus.update { SyncStatus.Syncing }
                val (personalDecks, result) = personalDeckSyncRepository.fetchRemoteDecks()
                if (result == SUCCESS) {
                    if (personalDecks == null) {
                        _syncStatus.update { SyncStatus.Error("No decks retrieved") }
                        return@launch
                    }
                    val remoteSync = personalDeckSyncRepository.getLastUpdatedOn()
                    if (!validateRemoteSync(remoteSync)) {
                        return@launch
                    }
                    val syncInfo = remoteSync.first?.updatedOn
                    if (syncInfo == null) {
                        _syncStatus.update { SyncStatus.Error("Empty updated_on") }
                        return@launch
                    }
                    userSyncedInfoRepository.insertOrUpdateSyncInfo(
                        SyncedDeckInfo(
                            uuid = uuid, lastUpdatedOn = syncInfo
                        )
                    )
                    val allDecks =
                        json.decodeFromJsonElement(ListOfDecks.serializer(), personalDecks.data)

                    userSyncedInfoRepository.replaceDB(allDecks)
                    _syncStatus.update { SyncStatus.Success }
                } else {
                    Log.e(PDSVM, "Failed to fetch remote decks: $result")
                    _syncStatus.update { SyncStatus.Error("Sync failed with code: $result") }
                }
            } catch (e: Exception) {
                Log.e(PDSVM, "Error fetching remote decks: ${e.message}")
                _syncStatus.update { SyncStatus.Error("Error: ${e.message}") }
            }
        }
    }

    /** Override the remote deck */
    fun overrideSyncDecks() {
        _syncStatus.update { SyncStatus.Syncing }
        viewModelScope.launch {
            try {
                val uuid = personalDeckSyncRepository.getUserUUID() ?: run {
                    _syncStatus.update { SyncStatus.Error("Null user") }
                    return@launch
                }
                val decks = userSyncedInfoRepository.getDB()
                val result = personalDeckSyncRepository.syncUserDecks(decks)
                when (result.second) {
                    SUCCESS -> {
                        userSyncedInfoRepository.insertOrUpdateSyncInfo(
                            SyncedDeckInfo(
                                uuid, result.first
                            )
                        )
                        _syncStatus.update { SyncStatus.Success }
                        Log.d(PDSVM, "Successfully Synced Decks")
                    }

                    NULL_USER -> {
                        Log.e(PDSVM, "Sync failed with code: $result")
                        _syncStatus.update { SyncStatus.Error("User not authenticated") }
                    }

                    NO_DECKS_TO_SYNC -> {
                        Log.e(PDSVM, "Sync failed with code: $result")
                        _syncStatus.update { SyncStatus.Error("No decks to sync") }
                    }

                    else -> {
                        Log.e(PDSVM, "Sync failed with code: $result")
                        _syncStatus.update { SyncStatus.Error("Sync failed with code: $result") }
                    }
                }
            } catch (e: Exception) {
                Log.e(PDSVM, "Error syncing decks: ${e.message}")
                _syncStatus.update { SyncStatus.Error("Error: ${e.message}") }
            }
        }
    }

    fun resetSyncStatus() {
        _syncStatus.value = SyncStatus.Idle
    }

    /** This will be called in fetchRemoteDecks(), if it's null there's an error
     *  because fetchRemoteDecks will only be called if there's a syncing
     *  conflict.
     */
    private fun validateRemoteSync(remoteSync: Pair<PDUpdatedOn?, Int>): Boolean {
        if (remoteSync.second != SUCCESS || remoteSync.first == null) {
            val errorMessage = if (remoteSync.second != SUCCESS) "Error" else "Empty updated_on"
            _syncStatus.update { SyncStatus.Error(errorMessage) }
            return false
        }
        return true
    }
}
