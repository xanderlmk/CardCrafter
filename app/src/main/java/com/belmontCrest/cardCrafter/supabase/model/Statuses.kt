package com.belmontCrest.cardCrafter.supabase.model

sealed class SyncStatus {
    data object Idle : SyncStatus()
    data object Syncing : SyncStatus()
    data object Success : SyncStatus()
    data class Error(val message: String) : SyncStatus()
    data object Conflict : SyncStatus()
}

sealed class RequestStatus {
    data object Idle : RequestStatus()
    data object Sent : RequestStatus()
    data object Declined : RequestStatus()
    data object Accepted : RequestStatus()
    data class Error(val message: String) : RequestStatus()
}