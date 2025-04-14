package com.belmontCrest.cardCrafter.supabase.controller.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belmontCrest.cardCrafter.supabase.model.tables.UserProfile
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserProfileViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _userProfile: MutableStateFlow<UserProfile?> = MutableStateFlow(null)
    val userProfile = _userProfile.asStateFlow()
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        getUserInfo()
    }

    fun getUserInfo() {
        viewModelScope.launch {
            _isLoading.update {
                true
            }
            authRepository.getUserProfile().let { user ->
                _userProfile.update {
                    user
                }
                _isLoading.update {
                    false
                }
            }
        }
    }

    suspend fun signOut(): Boolean {
        return withContext(Dispatchers.IO) {
            authRepository.signOut()
        }
    }

}