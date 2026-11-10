package com.Hefputu5.Nugsis.presentation.menu

import com.Hefputu5.Nugsis.domain.model.RelicId

data class MenuUiState(
    val highestUnlocked: Int = 1,
    val clearedLevels: Int = 0,
    val totalStars: Int = 0,
    val unlockedRelics: Int = 0,
    val wavesRepelled: Int = 0,
    val equipped: RelicId? = null
)
