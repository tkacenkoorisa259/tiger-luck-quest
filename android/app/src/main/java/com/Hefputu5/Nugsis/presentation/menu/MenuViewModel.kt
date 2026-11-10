package com.Hefputu5.Nugsis.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Hefputu5.Nugsis.domain.repository.ProgressRepository
import com.Hefputu5.Nugsis.domain.repository.RelicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MenuViewModel(
    private val progressRepository: ProgressRepository,
    private val relicRepository: RelicRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MenuUiState())
    val state: StateFlow<MenuUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val snapshot = withContext(Dispatchers.Default) {
                val progress = progressRepository.load()
                val unlocked = relicRepository.unlocked()
                MenuUiState(
                    highestUnlocked = progress.unlockedLevels,
                    clearedLevels = progress.clearedLevels,
                    totalStars = progress.totalStars,
                    unlockedRelics = unlocked.size,
                    wavesRepelled = progress.wavesRepelled,
                    equipped = relicRepository.equipped()
                )
            }
            _state.update { snapshot }
        }
    }
}
