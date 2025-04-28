package com.belmontCrest.cardCrafter.supabase.controller.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belmontCrest.cardCrafter.localDatabase.tables.SyncedDeckInfo
import com.belmontCrest.cardCrafter.localDatabase.tables.toInstant
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.NO_DECKS_TO_SYNC
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.NULL_USER
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.SUCCESS
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.UserSyncedInfoRepository
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.PersonalDeckSyncRepository
import com.belmontCrest.cardCrafter.supabase.model.tables.ListOfDecks
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
    private val json = Json

    fun syncDecks() {
        _syncStatus.update { SyncStatus.Syncing }
        viewModelScope.launch {
            try {
                val uuid = personalDeckSyncRepository.getUserUUID()
                if (uuid == null) {
                    _syncStatus.update { SyncStatus.Error("Null Users") }; return@launch
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
                        Log.d("PDSVM", "Successfully Synced Decks")
                    }

                    NULL_USER -> {
                        Log.e("PDSVM", "Sync failed with code: $result")
                        _syncStatus.update { SyncStatus.Error("User not authenticated") }
                    }

                    NO_DECKS_TO_SYNC -> {
                        Log.e("PDSVM", "Sync failed with code: $result")
                        _syncStatus.update { SyncStatus.Error("No decks to sync") }
                    }

                    else -> {
                        Log.e("PDSVM", "Sync failed with code: $result")
                        _syncStatus.update { SyncStatus.Error("Sync failed with code: $result") }
                    }
                }
            } catch (e: Exception) {
                Log.e("PersonalDeckSyncVM", "Error syncing decks: ${e.message}")
                _syncStatus.update { SyncStatus.Error("Error: ${e.message}") }
            }
        }
    }

    fun fetchRemoteDecks() {
        viewModelScope.launch {
            try {
                _syncStatus.update { SyncStatus.Syncing }
                val (personalDecks, result) = personalDeckSyncRepository.fetchRemoteDecks()
                if (result == SUCCESS) {
                    if (personalDecks == null) {
                        return@launch
                    }
                    val remoteSync = personalDeckSyncRepository.getLastUpdatedOn()
                    val userUUID = personalDeckSyncRepository.getUserUUID()
                    if (userUUID == null) {
                        _syncStatus.update { SyncStatus.Error("Null user") }
                        return@launch
                    }
                    if (remoteSync.second != SUCCESS) {
                        _syncStatus.update { SyncStatus.Error("Error") }
                        return@launch
                    }
                    if (remoteSync.first == null) {
                        _syncStatus.update { SyncStatus.Error("Empty updated_on") }
                        return@launch
                    }
                    val syncInfo = remoteSync.first?.updatedOn
                    if (syncInfo == null) {
                        _syncStatus.update { SyncStatus.Error("Empty updated_on") }
                        return@launch
                    }
                    userSyncedInfoRepository.insertOrUpdateSyncInfo(
                        SyncedDeckInfo(
                            uuid = userUUID, lastUpdatedOn = syncInfo
                        )
                    )
                    val allDecks =
                        json.decodeFromJsonElement(ListOfDecks.serializer(), personalDecks.data)

                    userSyncedInfoRepository.replaceDB(allDecks)
                    _syncStatus.update { SyncStatus.Success }
                } else {
                    Log.e("PersonalDeckSyncVM", "Failed to fetch remote decks: $result")
                    _syncStatus.update { SyncStatus.Error("Sync failed with code: $result") }
                }
            } catch (e: Exception) {
                Log.e("PersonalDeckSyncVM", "Error fetching remote decks: ${e.message}")
                _syncStatus.update { SyncStatus.Error("Error: ${e.message}") }
            }
        }
    }

    /** Soon to be done */
    fun overrideSyncDecks() {
        TODO()
    }

    fun resetSyncStatus() {
        _syncStatus.value = SyncStatus.Idle
    }
}

sealed class SyncStatus {
    data object Idle : SyncStatus()
    data object Syncing : SyncStatus()
    data object Success : SyncStatus()
    data class Error(val message: String) : SyncStatus()
    data object Conflict : SyncStatus()
}