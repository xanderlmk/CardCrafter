package com.belmontCrest.cardCrafter.supabase.controller.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.UserExportedDecksRepository
import com.belmontCrest.cardCrafter.supabase.model.tables.SBDeckListDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserExportedDecksViewModel(
    private val uEDRepository: UserExportedDecksRepository
) : ViewModel() {
    private val _userExportedDecks = MutableStateFlow(SBDeckListDto())
    val userExportedDecks = _userExportedDecks.asStateFlow()
    init {
        getUserDeckList()
    }
    fun getUserDeckList() {
        viewModelScope.launch {
            try {
                uEDRepository.userExportedDecks().collectLatest { list ->
                    _userExportedDecks.update {
                        SBDeckListDto(list)
                    }
                }
            } catch (e : Exception) {
                Log.e("UEDVM", "$e")
                _userExportedDecks.update {
                    SBDeckListDto()
                }
            }
        }
    }
}