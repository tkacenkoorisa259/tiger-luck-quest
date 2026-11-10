package com.Hefputu5.Nugsis.presentation.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Hefputu5.Nugsis.domain.model.RunSummary
import com.Hefputu5.Nugsis.domain.repository.ProgressRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ResultUiState(
    val summary: RunSummary? = null,
    val bestStars: Int = 0,
    val totalStars: Int = 0
)

class ResultViewModel(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ResultUiState())
    val state: StateFlow<ResultUiState> = _state.asStateFlow()

    private var bound: RunSummary? = null

    fun bind(summary: RunSummary) {
        if (bound == summary) return
        bound = summary
        viewModelScope.launch {
            val progress = withContext(Dispatchers.Default) { progressRepository.load() }
            _state.update {
                it.copy(
                    summary = summary,
                    bestStars = progress.stars.getOrElse(summary.levelIndex - 1) { 0 },
                    totalStars = progress.totalStars
                )
            }
        }
    }
}
