package com.Hefputu5.Nugsis.domain.model

import com.Hefputu5.Nugsis.core.config.GameConfig

enum class GuardianType { SENTRY, ARCHER, TOTEM }

enum class BeastType { PROWLER, APE, SHADOW }

enum class CellKind { GATE, CANOPY, TRAIL, PLATFORM, TEMPLE, WALL }

enum class GameOutcome { VICTORY, DEFEAT }

enum class TurnPhase { PLANNING, RESOLVING, FINISHED }

enum class TurnSpeed { NORMAL, FAST }

data class GuardianStats(val cost: Int, val range: Int, val damage: Int)

data class BeastStats(val hp: Int, val armour: Int, val wardDamage: Int, val canSprint: Boolean)

data class Guardian(val type: GuardianType, val col: Int, val row: Int)

data class Beast(
    val id: Int,
    val type: BeastType,
    val trail: Int,
    val row: Int,
    val hp: Int,
    val maxHp: Int,
    val sprinting: Boolean
)

data class Expedition(
    val index: Int,
    val turnLimit: Int,
    val waves: Int,
    val beastsPerWave: Int,
    val hasApe: Boolean,
    val hasBoss: Boolean,
    val rubbleCount: Int
)

data class RunState(
    val expedition: Expedition,
    val seed: Long,
    val turn: Int,
    val jade: Int,
    val wards: Int,
    val maxWards: Int,
    val roarCharge: Int,
    val rubble: Set<Int>,
    val bonusTrail: Int,
    val guardians: List<Guardian>,
    val beasts: List<Beast>,
    val gatesOpen: List<Boolean>,
    val pendingSpawns: List<BeastType>,
    val spawnCursor: Int,
    val wavesSpawned: Int,
    val wavesRepelled: Int,
    val beastsDefeated: Int,
    val jadeEarned: Int,
    val nextBeastId: Int,
    val sprintSeen: Boolean,
    val prowlersKilled: Int,
    val archersPlaced: Int,
    val gatesSealed: Int,
    val roarsUsed: Int,
    val outcome: GameOutcome?
)

data class RunSummary(
    val levelIndex: Int,
    val outcome: GameOutcome,
    val wardsLeft: Int,
    val maxWards: Int,
    val turnsHeld: Int,
    val wavesRepelled: Int,
    val jadeEarned: Int,
    val stars: Int,
    val openTrails: Int,
    val newRelics: List<RelicId>,
    val nextUnlocked: Boolean
)

data class AppSettings(
    val pathPreview: Boolean,
    val confirmTurnStart: Boolean,
    val turnSpeed: TurnSpeed
)

object Rules {

    fun statsOf(type: GuardianType): GuardianStats = when (type) {
        GuardianType.ARCHER -> GuardianStats(cost = 4, range = 2, damage = 1)
        GuardianType.SENTRY -> GuardianStats(cost = 6, range = 1, damage = 3)
        GuardianType.TOTEM -> GuardianStats(cost = 8, range = 0, damage = 0)
    }

    fun statsOf(type: BeastType): BeastStats = when (type) {
        BeastType.PROWLER -> BeastStats(hp = 3, armour = 0, wardDamage = 1, canSprint = true)
        BeastType.APE -> BeastStats(hp = 7, armour = 1, wardDamage = 1, canSprint = false)
        BeastType.SHADOW -> BeastStats(hp = 18, armour = 1, wardDamage = 2, canSprint = false)
    }

    fun costOf(type: GuardianType, relic: RelicId?): Int {
        val base = statsOf(type).cost
        return if (type == GuardianType.ARCHER && relic == RelicId.BAMBOO_CHARM) 3 else base
    }

    fun sealCost(relic: RelicId?): Int =
        if (relic == RelicId.VINE_LOCK) 0 else GameConfig.SEAL_COST

    fun roarThreshold(relic: RelicId?): Int =
        if (relic == RelicId.AMBER_EYE) 5 else GameConfig.ROAR_THRESHOLD

    fun roarDamage(relic: RelicId?): Int =
        if (relic == RelicId.SHADOW_PELT) 5 else GameConfig.ROAR_DAMAGE

    fun startingJade(relic: RelicId?): Int =
        if (relic == RelicId.SUN_DISC) GameConfig.BASE_JADE + 5 else GameConfig.BASE_JADE

    fun startingWards(relic: RelicId?): Int =
        if (relic == RelicId.STONE_WARD) GameConfig.BASE_WARDS + 1 else GameConfig.BASE_WARDS

    fun turnIncomeBonus(relic: RelicId?): Int =
        if (relic == RelicId.JADE_FANG) 2 else 0
}

object BoardLayout {

    fun trailColumn(trail: Int): Int = trail * 2

    fun trailOfColumn(col: Int): Int? = if (col % 2 == 0) col / 2 else null

    fun cellIndex(col: Int, row: Int): Int = row * GameConfig.COLS + col

    fun columnOf(index: Int): Int = index % GameConfig.COLS

    fun rowOf(index: Int): Int = index / GameConfig.COLS

    fun kindAt(col: Int, row: Int): CellKind {
        val onTrail = col % 2 == 0 && col <= 4
        return when {
            row == 0 && onTrail -> CellKind.GATE
            row == 0 -> CellKind.CANOPY
            row == GameConfig.TEMPLE_ROW && col == GameConfig.TEMPLE_COL -> CellKind.TEMPLE
            row == GameConfig.TEMPLE_ROW && onTrail -> CellKind.WALL
            row == GameConfig.TEMPLE_ROW -> CellKind.PLATFORM
            onTrail -> CellKind.TRAIL
            else -> CellKind.PLATFORM
        }
    }

    val platformCells: List<Int> by lazy {
        val cells = mutableListOf<Int>()
        for (row in 0 until GameConfig.ROWS) {
            for (col in 0 until GameConfig.COLS) {
                if (kindAt(col, row) == CellKind.PLATFORM) cells.add(cellIndex(col, row))
            }
        }
        cells
    }

    val shrineRow: Int = 3

    fun distance(colA: Int, rowA: Int, colB: Int, rowB: Int): Int =
        kotlin.math.abs(colA - colB) + kotlin.math.abs(rowA - rowB)
}
