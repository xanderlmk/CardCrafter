package com.belmontCrest.cardCrafter.navigation

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.belmontCrest.cardCrafter.model.application.dataStore
import com.belmontCrest.cardCrafter.model.ui.states.SelectedKeyboard
import com.belmontCrest.cardCrafter.model.ui.states.TypeInfo
import com.belmontCrest.cardCrafter.model.ui.states.Types
import com.belmontCrest.cardCrafter.views.misc.CARD_CRAFTER
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json


interface KeyboardSelectionRepo {
    val showKatexKeyboard: StateFlow<Boolean>
    val selectedKB: StateFlow<SelectedKeyboard?>
    val resetOffset: StateFlow<Boolean>
    val notationParamSelected: StateFlow<Boolean>
    val customTypes: StateFlow<Types>
    val type: StateFlow<String>
    fun updateSelectedKB(selectedKeyboard: SelectedKeyboard)
    fun resetSelectedKB()
    fun toggleKeyboardIcon()
    fun resetOffset()
    fun resetDone()
    fun resetKeyboardStuff()
    fun onCreate(showKB: Boolean, newType: String)
    fun updateNotationParamSelected(isNotationType: Boolean)
    fun updateList(typeInfo: TypeInfo)
    fun updateType(newType: String)
}

class KeyboardSelectionRepoImpl(
    private val context: Context, private val scope: CoroutineScope
) : KeyboardSelectionRepo {

    companion object {
        private val CUSTOM_TYPES = stringSetPreferencesKey("custom_types_set")
        private val slConverter = ListStringConverter()
    }

    private val _showKatexKeyboard = MutableStateFlow(false)
    override val showKatexKeyboard = _showKatexKeyboard.asStateFlow()
    private val _selectedKB: MutableStateFlow<SelectedKeyboard?> = MutableStateFlow(null)
    override val selectedKB = _selectedKB.asStateFlow()
    private val _resetOffset = MutableStateFlow(false)
    override val resetOffset = _resetOffset.asStateFlow()

    private val _type = MutableStateFlow("basic")
    override val type = _type.asStateFlow()

    override fun updateType(newType: String) = _type.update { newType }

    override val customTypes = context.dataStore.data.map { preferences ->
        Types(slConverter.fromSet(preferences[CUSTOM_TYPES] ?: emptySet()))
    }.stateIn(
        scope = scope, initialValue = Types(),
        started = SharingStarted.Eagerly
    )
    private val _notationParamSelected = MutableStateFlow(false)
    override val notationParamSelected = _notationParamSelected.asStateFlow()

    /**
     * Check if the type already exists, if not,
     * add a new Type and save it as a string (Jsonify)
     */
    override fun updateList(typeInfo: TypeInfo) {
        scope.launch {
            context.dataStore.edit { setting ->
                val newList = customTypes.value.ts.toMutableList()
                val listToSet = newList.map { it.t }.toSet()
                if (typeInfo.t in listToSet) return@edit
                newList.add(typeInfo)
                setting[CUSTOM_TYPES] = slConverter.setToString(newList)
            }
        }
    }

    override fun updateSelectedKB(selectedKeyboard: SelectedKeyboard) =
        _selectedKB.update { selectedKeyboard }

    override fun resetSelectedKB() = _selectedKB.update { null }

    override fun toggleKeyboardIcon() = _showKatexKeyboard.update { !it }

    override fun resetOffset() = _resetOffset.update { true }

    override fun resetDone() = _resetOffset.update { false }

    /** Reset the selected keyboard to null and don't show the keyboard */
    override fun resetKeyboardStuff() {
        resetSelectedKB()
        _showKatexKeyboard.update { false }
    }

    override fun updateNotationParamSelected(isNotationType: Boolean) =
        _notationParamSelected.update { isNotationType }


    override fun onCreate(showKB: Boolean, newType: String) {
        _showKatexKeyboard.update { showKB }; _type.update { newType }
    }
}

private class ListStringConverter {
    fun fromSet(set: Set<String>): List<TypeInfo> {
        try {
            if (set.isEmpty()) return emptyList()
            val list = set.map { Json.decodeFromString<TypeInfo>(it) }
            return list
        } catch (e: Exception) {
            Log.e(CARD_CRAFTER, "Failed to deserialize set: $e")
            return emptyList()
        }
    }

    fun setToString(typeInfos: List<TypeInfo>): Set<String> {
        if (typeInfos.isEmpty()) return emptySet()
        return typeInfos.map { Json.encodeToString(TypeInfo.serializer(), it) }.toSet()
    }
}