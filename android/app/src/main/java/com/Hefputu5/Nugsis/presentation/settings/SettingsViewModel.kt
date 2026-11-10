package com.Hefputu5.Nugsis.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Hefputu5.Nugsis.domain.model.AppSettings
import com.Hefputu5.Nugsis.domain.model.TurnSpeed
import com.Hefputu5.Nugsis.domain.repository.ProgressRepository
import com.Hefputu5.Nugsis.domain.repository.RelicRepository
import com.Hefputu5.Nugsis.domain.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class SettingsUiState(
    val settings: AppSettings = AppSettings(true, true, TurnSpeed.NORMAL),
    val askReset: Boolean = false,
    val resetDone: Boolean = false
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val progressRepository: ProgressRepository,
    private val relicRepository: RelicRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val loaded = withContext(Dispatchers.Default) { settingsRepository.load() }
            _state.update { it.copy(settings = loaded) }
        }
    }

    fun setPathPreview(enabled: Boolean) {
        _state.update { it.copy(settings = it.settings.copy(pathPreview = enabled)) }
        viewModelScope.launch {
            withContext(Dispatchers.Default) { settingsRepository.setPathPreview(enabled) }
        }
    }

    fun setConfirmTurnStart(enabled: Boolean) {
        _state.update { it.copy(settings = it.settings.copy(confirmTurnStart = enabled)) }
        viewModelScope.launch {
            withContext(Dispatchers.Default) { settingsRepository.setConfirmTurnStart(enabled) }
        }
    }

    fun setTurnSpeed(speed: TurnSpeed) {
        _state.update { it.copy(settings = it.settings.copy(turnSpeed = speed)) }
        viewModelScope.launch {
            withContext(Dispatchers.Default) { settingsRepository.setTurnSpeed(speed) }
        }
    }

    fun askReset() {
        _state.update { it.copy(askReset = true) }
    }

    fun dismissReset() {
        _state.update { it.copy(askReset = false) }
    }

    fun confirmReset() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                progressRepository.reset()
                relicRepository.reset()
                settingsRepository.reset()
            }
            val loaded = withContext(Dispatchers.Default) { settingsRepository.load() }
            _state.update { SettingsUiState(settings = loaded, askReset = false, resetDone = true) }
        }
    }

    fun consumeResetDone() {
        _state.update { it.copy(resetDone = false) }
    }
}
