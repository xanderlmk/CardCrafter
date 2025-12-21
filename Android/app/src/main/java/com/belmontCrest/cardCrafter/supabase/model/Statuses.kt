package com.belmontCrest.cardCrafter.supabase.model

import com.belmontCrest.cardCrafter.BuildConfig

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

data class TimestampTZResult(
    val returnValue: Int,
    val timestamp: String = ""
)

object AuthRepoVals {
    const val SB_OWNER_TN = BuildConfig.SB_OWNER_TN
    const val POST_FUNCTION_STRING = "functions/v1/getKeys"
    const val SUCCESS = "yay"
    const val TIMEOUT = "timeout"
    const val AUTH_REPO = "Auth Repository"
}