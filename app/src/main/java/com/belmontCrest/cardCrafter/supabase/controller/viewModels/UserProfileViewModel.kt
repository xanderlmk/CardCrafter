package com.belmontCrest.cardCrafter.supabase.controller.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belmontCrest.cardCrafter.supabase.model.UserProfile
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserProfileViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _userProfile: MutableStateFlow<UserProfile?> = MutableStateFlow(null)
    val userProfile = _userProfile.asStateFlow()

    init {
        getUserInfo()
    }

    fun getUserInfo() {
        viewModelScope.launch {
            _userProfile.update {
                authRepository.getUserProfile()
            }
        }
    }

}