package com.Hefputu5.Nugsis.presentation.progress

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

data class ExpeditionRow(
    val index: Int,
    val unlocked: Boolean,
    val stars: Int,
    val bestWards: Int
)

data class ProgressUiState(
    val clearedLevels: Int = 0,
    val totalStars: Int = 0,
    val wavesRepelled: Int = 0,
    val jadeEarned: Int = 0,
    val bestStreak: Int = 0,
    val rows: List<ExpeditionRow> = emptyList()
)

class ProgressViewModel(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProgressUiState())
    val state: StateFlow<ProgressUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val snapshot = withContext(Dispatchers.Default) {
                val progress = progressRepository.load()
                ProgressUiState(
                    clearedLevels = progress.clearedLevels,
                    totalStars = progress.totalStars,
                    wavesRepelled = progress.wavesRepelled,
                    jadeEarned = progress.jadeEarned,
                    bestStreak = progress.bestStreak,
                    rows = SampleData.expeditions.map { expedition ->
                        ExpeditionRow(
                            index = expedition.index,
                            unlocked = expedition.index <= progress.unlockedLevels,
                            stars = progress.stars.getOrElse(expedition.index - 1) { 0 },
                            bestWards = progress.bestWards.getOrElse(expedition.index - 1) { 0 }
                        )
                    }
                )
            }
            _state.update { snapshot }
        }
    }
}
