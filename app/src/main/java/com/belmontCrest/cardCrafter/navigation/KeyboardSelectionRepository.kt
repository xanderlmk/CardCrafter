package com.belmontCrest.cardCrafter.navigation

import com.belmontCrest.cardCrafter.model.ui.SelectedKeyboard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update



interface KeyboardSelectionRepository {
    val showKatexKeyboard : StateFlow<Boolean>
    val selectedKB : StateFlow<SelectedKeyboard?>
    val resetOffset : StateFlow<Boolean>

    fun updateSelectedKB(selectedKeyboard: SelectedKeyboard)
    fun resetSelectedKB()
    fun retrieveKB(retrievedKB : SelectedKeyboard?)
    fun toggleKeyboard()
    fun resetOffset()
    fun resetDone()
    fun resetKeyboardStuff()
    fun retrieveShowKB(show : Boolean)
}
class KeyboardSelectionRepoImpl : KeyboardSelectionRepository {
    private val _showKatexKeyboard = MutableStateFlow(false)
    override val showKatexKeyboard = _showKatexKeyboard.asStateFlow()
    private val _selectedKB: MutableStateFlow<SelectedKeyboard?> = MutableStateFlow(null)
    override val selectedKB = _selectedKB.asStateFlow()
    private val _resetOffset = MutableStateFlow(false)
    override val resetOffset = _resetOffset.asStateFlow()

    override fun updateSelectedKB(selectedKeyboard: SelectedKeyboard) {
        _selectedKB.update { selectedKeyboard }
    }

    override fun resetSelectedKB() {
        _selectedKB.update { null }
    }

    override fun retrieveKB(retrievedKB : SelectedKeyboard?) {
        _selectedKB.update { retrievedKB }
    }

    override fun toggleKeyboard() {
        _showKatexKeyboard.update { !it }
    }

    override fun resetOffset() {
        _resetOffset.update { true }
    }

    override fun resetDone() {
        _resetOffset.update { false }
    }

    /** Reset the selected keyboard to null and don't show the keyboard */
    override fun resetKeyboardStuff() {
        resetSelectedKB()
        _showKatexKeyboard.update { false }
    }

    override fun retrieveShowKB(show: Boolean) {
        _showKatexKeyboard.update { show }
    }
}
