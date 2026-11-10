package com.Hefputu5.Nugsis.presentation.levels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Hefputu5.Nugsis.data.sample.SampleData
import com.Hefputu5.Nugsis.domain.repository.ProgressRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class LevelCard(
    val index: Int,
    val unlocked: Boolean,
    val stars: Int,
    val turnLimit: Int,
    val waves: Int
)

data class LevelSelectUiState(
    val levels: List<LevelCard> = emptyList(),
    val totalStars: Int = 0
)

class LevelSelectViewModel(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LevelSelectUiState())
    val state: StateFlow<LevelSelectUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val snapshot = withContext(Dispatchers.Default) {
                val progress = progressRepository.load()
                LevelSelectUiState(
                    levels = SampleData.expeditions.map { expedition ->
                        LevelCard(
                            index = expedition.index,
                            unlocked = expedition.index <= progress.unlockedLevels,
                            stars = progress.stars.getOrElse(expedition.index - 1) { 0 },
                            turnLimit = expedition.turnLimit,
                            waves = expedition.waves
                        )
                    },
                    totalStars = progress.totalStars
                )
            }
            _state.update { snapshot }
        }
    }
}
