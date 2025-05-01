package com.belmontCrest.cardCrafter.supabase.controller.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belmontCrest.cardCrafter.supabase.model.RequestStatus
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.NETWORK_ERROR
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.CANCELLED
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.SUCCESS
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues.UNKNOWN_ERROR
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.CoOwnerRequestsRepository
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCoOwnerOf
import com.belmontCrest.cardCrafter.supabase.model.tables.SBCoOwnerWithDeck
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CoOwnerViewModel(
    private val coOwnerRequestsRepository: CoOwnerRequestsRepository
) : ViewModel() {
    private val _coOwnerOf = MutableStateFlow(SBCoOwnerOf())
    val coOwnerOf = _coOwnerOf.asStateFlow()

    private val _status = MutableStateFlow<RequestStatus>(RequestStatus.Idle)
    val status = _status.asStateFlow()

    private val _coOwner = MutableStateFlow<SBCoOwnerWithDeck?>(null)
    val coOwner = _coOwner.asStateFlow()
    init {
        getRequests()
    }

    fun getRequests() {
        viewModelScope.launch {
            val result = coOwnerRequestsRepository.getRequests()
            if (result.second != SUCCESS) {
                return@launch
            }
            result.first.collectLatest { list ->
                _coOwnerOf.update { SBCoOwnerOf(list) }
            }
        }
    }

    fun acceptRequest(uuid: String) {
        viewModelScope.launch {
            _status.update { RequestStatus.Sent }
            val result = coOwnerRequestsRepository.acceptRequest(uuid)
            when (result) {
                SUCCESS -> { _status.update { RequestStatus.Accepted }; getRequests() }
                NETWORK_ERROR -> _status.update { RequestStatus.Error("Network Error") }
                CANCELLED -> _status.update { RequestStatus.Error("Request was cancelled") }
                UNKNOWN_ERROR -> {
                    _status.update { RequestStatus.Error("Unknown Error") }
                }
            }
        }
    }

    fun resetRequestStatus() {
        _status.update { RequestStatus.Idle }
    }

    fun updateCoOwner(coOwner: SBCoOwnerWithDeck) {
        _coOwner.update { coOwner }
    }

}