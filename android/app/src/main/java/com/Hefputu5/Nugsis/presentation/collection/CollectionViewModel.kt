package com.Hefputu5.Nugsis.presentation.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Hefputu5.Nugsis.domain.model.RelicId
import com.Hefputu5.Nugsis.domain.model.RelicProgress
import com.Hefputu5.Nugsis.domain.repository.ProgressRepository
import com.Hefputu5.Nugsis.domain.repository.RelicRepository
import com.Hefputu5.Nugsis.domain.usecase.EvaluateRelicUnlocksUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class CollectionUiState(
    val relics: List<RelicProgress> = emptyList(),
    val unlockedCount: Int = 0,
    val selected: RelicId? = null
)

class CollectionViewModel(
    private val progressRepository: ProgressRepository,
    private val relicRepository: RelicRepository,
    private val evaluateRelics: EvaluateRelicUnlocksUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CollectionUiState())
    val state: StateFlow<CollectionUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val relics = withContext(Dispatchers.Default) {
                val progress = progressRepository.load()
                evaluateRelics.snapshot(
                    progress = progress,
                    unlocked = relicRepository.unlocked(),
                    equipped = relicRepository.equipped()
                )
            }
            _state.update { current ->
                current.copy(relics = relics, unlockedCount = relics.count { it.unlocked })
            }
        }
    }

    fun select(id: RelicId?) {
        _state.update { it.copy(selected = id) }
    }

    fun toggleEquip(id: RelicId) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val equipped = relicRepository.equipped()
                relicRepository.equip(if (equipped == id) null else id)
            }
            _state.update { it.copy(selected = null) }
            refresh()
        }
    }
}
