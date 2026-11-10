package com.Hefputu5.Nugsis.presentation.game

import com.Hefputu5.Nugsis.core.config.GameConfig
import com.Hefputu5.Nugsis.domain.model.Beast
import com.Hefputu5.Nugsis.domain.model.Guardian
import com.Hefputu5.Nugsis.domain.model.GuardianType
import com.Hefputu5.Nugsis.domain.model.TurnPhase

data class GameUiState(
    val levelIndex: Int = 1,
    val turn: Int = 1,
    val turnLimit: Int = GameConfig.EXPEDITION_COUNT,
    val jade: Int = 0,
    val wards: Int = GameConfig.BASE_WARDS,
    val maxWards: Int = GameConfig.BASE_WARDS,
    val roarCharge: Int = 0,
    val roarThreshold: Int = GameConfig.ROAR_THRESHOLD,
    val secondsLeft: Int = GameConfig.TURN_TIMER_SECONDS,
    val phase: TurnPhase = TurnPhase.PLANNING,
    val armed: GuardianType? = null,
    val guardians: List<Guardian> = emptyList(),
    val beasts: List<Beast> = emptyList(),
    val rubble: Set<Int> = emptySet(),
    val gatesOpen: List<Boolean> = listOf(true, true, true),
    val bonusTrail: Int = 0,
    val archerCost: Int = 4,
    val sentryCost: Int = 6,
    val totemCost: Int = 8,
    val sealCost: Int = GameConfig.SEAL_COST,
    val pathPreview: Boolean = true,
    val previewSteps: Map<Int, Int> = emptyMap(),
    val sprintSeen: Boolean = false,
    val paused: Boolean = false,
    val askConfirm: Boolean = false,
    val loading: Boolean = true
) {
    fun costOf(type: GuardianType): Int = when (type) {
        GuardianType.ARCHER -> archerCost
        GuardianType.SENTRY -> sentryCost
        GuardianType.TOTEM -> totemCost
    }

    val roarReady: Boolean get() = roarCharge >= roarThreshold

    val interactive: Boolean get() = phase == TurnPhase.PLANNING && !paused && !loading
}
