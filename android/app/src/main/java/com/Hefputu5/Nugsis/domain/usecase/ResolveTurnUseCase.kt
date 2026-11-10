package com.Hefputu5.Nugsis.domain.usecase

import com.Hefputu5.Nugsis.core.config.GameConfig
import com.Hefputu5.Nugsis.domain.model.Beast
import com.Hefputu5.Nugsis.domain.model.BeastType
import com.Hefputu5.Nugsis.domain.model.BoardLayout
import com.Hefputu5.Nugsis.domain.model.GameOutcome
import com.Hefputu5.Nugsis.domain.model.Guardian
import com.Hefputu5.Nugsis.domain.model.GuardianType
import com.Hefputu5.Nugsis.domain.model.RelicId
import com.Hefputu5.Nugsis.domain.model.RunState
import com.Hefputu5.Nugsis.domain.model.Rules
import kotlin.random.Random

class ResolveTurnUseCase {

    fun totemBonusFor(state: RunState, guardian: Guardian): Int =
        state.guardians.count {
            it.type == GuardianType.TOTEM &&
                BoardLayout.distance(it.col, it.row, guardian.col, guardian.row) == 1
        }

    fun isGuarded(state: RunState, trail: Int, row: Int): Boolean {
        val col = BoardLayout.trailColumn(trail)
        return state.guardians.any { BoardLayout.distance(it.col, it.row, col, row) == 1 }
    }

    fun willSprint(state: RunState, beast: Beast, relic: RelicId?): Boolean {
        if (!Rules.statsOf(beast.type).canSprint) return false
        if (relic == RelicId.MIST_VEIL && state.turn <= 1) return false
        return !isGuarded(state, beast.trail, beast.row)
    }

    fun guardiansFire(state: RunState, relic: RelicId?): RunState {
        if (state.beasts.isEmpty()) return state
        val hp = state.beasts.associate { it.id to it.hp }.toMutableMap()
        val ordered = state.guardians.sortedWith(compareBy({ it.row }, { it.col }))
        for (guardian in ordered) {
            val stats = Rules.statsOf(guardian.type)
            if (guardian.type == GuardianType.TOTEM) continue
            val bonus = totemBonusFor(state, guardian) +
                if (guardian.type == GuardianType.SENTRY && relic == RelicId.TIGER_TOTEM) 1 else 0
            val inRange = state.beasts.filter { beast ->
                (hp[beast.id] ?: 0) > 0 &&
                    BoardLayout.distance(
                        guardian.col,
                        guardian.row,
                        BoardLayout.trailColumn(beast.trail),
                        beast.row
                    ) <= stats.range
            }
            if (inRange.isEmpty()) continue
            val targets = if (guardian.type == GuardianType.SENTRY) {
                listOf(inRange.maxByOrNull { it.row } ?: inRange.first())
            } else {
                inRange
            }
            for (target in targets) {
                val armour = Rules.statsOf(target.type).armour
                val applied = maxOf(1, stats.damage + bonus - armour)
                hp[target.id] = (hp[target.id] ?: 0) - applied
            }
        }
        val survivors = state.beasts.mapNotNull { beast ->
            val left = hp[beast.id] ?: beast.hp
            if (left > 0) beast.copy(hp = left) else null
        }
        val killed = state.beasts.filter { beast -> survivors.none { it.id == beast.id } }
        val prowlers = killed.count { it.type == BeastType.PROWLER }
        val threshold = Rules.roarThreshold(relic)
        val waveCleared = killed.isNotEmpty() && survivors.isEmpty()
        return state.copy(
            beasts = survivors,
            roarCharge = minOf(threshold, state.roarCharge + killed.size),
            prowlersKilled = state.prowlersKilled + prowlers,
            beastsDefeated = state.beastsDefeated + killed.size,
            wavesRepelled = state.wavesRepelled + if (waveCleared) 1 else 0
        )
    }

    fun beastsMove(state: RunState, relic: RelicId?): RunState {
        if (state.beasts.isEmpty()) return state
        val moving = state.beasts.sortedByDescending { it.row }
        val placed = mutableListOf<Beast>()
        var wards = state.wards
        var sprintSeen = state.sprintSeen
        for (beast in moving) {
            val sprint = willSprint(state, beast, relic)
            if (sprint) sprintSeen = true
            var step = if (sprint) 2 else 1
            while (step > 0) {
                val target = beast.row + step
                val blocked = placed.any { it.trail == beast.trail && it.row == target }
                if (target >= GameConfig.TEMPLE_ROW || !blocked) break
                step -= 1
            }
            val target = beast.row + step
            if (target >= GameConfig.TEMPLE_ROW) {
                wards -= Rules.statsOf(beast.type).wardDamage
            } else {
                placed.add(beast.copy(row = target, sprinting = sprint))
            }
        }
        return state.copy(
            beasts = placed.sortedBy { it.id },
            wards = maxOf(0, wards),
            sprintSeen = sprintSeen
        )
    }

    fun collectIncome(state: RunState, relic: RelicId?): RunState {
        val openTrails = state.gatesOpen.count { it }
        val totems = state.guardians.count { it.type == GuardianType.TOTEM }
        val bonus = if (state.gatesOpen.getOrElse(state.bonusTrail) { false }) 2 else 0
        val gain = GameConfig.INCOME_BASE +
            GameConfig.INCOME_PER_TRAIL * openTrails +
            GameConfig.INCOME_PER_TOTEM * totems +
            Rules.turnIncomeBonus(relic) +
            bonus
        return state.copy(jade = state.jade + gain, jadeEarned = state.jadeEarned + gain)
    }

    fun spawnWave(state: RunState): RunState {
        val expedition = state.expedition
        val nextTurn = state.turn + 1
        val queue = state.pendingSpawns.toMutableList()
        if (isSpawnTurn(nextTurn) && state.wavesSpawned < expedition.waves) {
            queue.addAll(waveComposition(state, state.wavesSpawned))
        }
        val spawnedWave = if (isSpawnTurn(nextTurn) && state.wavesSpawned < expedition.waves) 1 else 0
        if (queue.isEmpty()) {
            return state.copy(wavesSpawned = state.wavesSpawned + spawnedWave)
        }
        val beasts = state.beasts.toMutableList()
        var cursor = state.spawnCursor
        var nextId = state.nextBeastId
        val remaining = mutableListOf<BeastType>()
        for (type in queue) {
            var trail = -1
            for (attempt in 0 until GameConfig.TRAIL_COUNT) {
                val candidate = (cursor + attempt) % GameConfig.TRAIL_COUNT
                val open = state.gatesOpen.getOrElse(candidate) { false }
                val free = beasts.none { it.trail == candidate && it.row == 0 }
                if (open && free) {
                    trail = candidate
                    cursor = candidate + 1
                    break
                }
            }
            if (trail < 0) {
                remaining.add(type)
                continue
            }
            val stats = Rules.statsOf(type)
            beasts.add(
                Beast(
                    id = nextId,
                    type = type,
                    trail = trail,
                    row = 0,
                    hp = stats.hp,
                    maxHp = stats.hp,
                    sprinting = false
                )
            )
            nextId += 1
        }
        return state.copy(
            beasts = beasts.sortedBy { it.id },
            pendingSpawns = remaining,
            spawnCursor = cursor % GameConfig.TRAIL_COUNT,
            nextBeastId = nextId,
            wavesSpawned = state.wavesSpawned + spawnedWave
        )
    }

    fun endCheck(state: RunState): RunState {
        if (state.wards <= 0) return state.copy(outcome = GameOutcome.DEFEAT)
        val allSpawned = state.wavesSpawned >= state.expedition.waves && state.pendingSpawns.isEmpty()
        val cleared = state.beasts.isEmpty()
        if (state.turn >= state.expedition.turnLimit && allSpawned && cleared) {
            return state.copy(outcome = GameOutcome.VICTORY)
        }
        if (state.turn >= state.expedition.turnLimit + 8) {
            return state.copy(outcome = GameOutcome.VICTORY)
        }
        return state.copy(turn = state.turn + 1)
    }

    fun resolveFully(state: RunState, relic: RelicId?): RunState {
        var next = guardiansFire(state, relic)
        next = beastsMove(next, relic)
        next = collectIncome(next, relic)
        next = spawnWave(next)
        return endCheck(next)
    }

    fun openingWave(state: RunState): RunState {
        val queue = waveComposition(state, 0)
        val seeded = state.copy(pendingSpawns = queue)
        val beasts = mutableListOf<Beast>()
        var cursor = 0
        var nextId = seeded.nextBeastId
        val remaining = mutableListOf<BeastType>()
        for (type in queue) {
            var trail = -1
            for (attempt in 0 until GameConfig.TRAIL_COUNT) {
                val candidate = (cursor + attempt) % GameConfig.TRAIL_COUNT
                if (beasts.none { it.trail == candidate && it.row == 0 }) {
                    trail = candidate
                    cursor = candidate + 1
                    break
                }
            }
            if (trail < 0) {
                remaining.add(type)
                continue
            }
            val stats = Rules.statsOf(type)
            beasts.add(
                Beast(nextId, type, trail, 0, stats.hp, stats.hp, false)
            )
            nextId += 1
        }
        return seeded.copy(
            beasts = beasts,
            pendingSpawns = remaining,
            spawnCursor = cursor % GameConfig.TRAIL_COUNT,
            nextBeastId = nextId,
            wavesSpawned = 1
        )
    }

    fun place(state: RunState, type: GuardianType, cell: Int, relic: RelicId?): RunState? {
        if (cell in state.rubble) return null
        if (BoardLayout.platformCells.none { it == cell }) return null
        val col = BoardLayout.columnOf(cell)
        val row = BoardLayout.rowOf(cell)
        if (state.guardians.any { it.col == col && it.row == row }) return null
        val cost = Rules.costOf(type, relic)
        if (state.jade < cost) return null
        return state.copy(
            jade = state.jade - cost,
            guardians = state.guardians + Guardian(type, col, row),
            archersPlaced = state.archersPlaced + if (type == GuardianType.ARCHER) 1 else 0
        )
    }

    fun canSeal(state: RunState, trail: Int): Boolean {
        if (!state.gatesOpen.getOrElse(trail) { false }) return false
        if (state.gatesOpen.count { it } <= 1) return false
        return state.beasts.none { it.trail == trail }
    }

    fun toggleGate(state: RunState, trail: Int, relic: RelicId?): RunState? {
        val open = state.gatesOpen.getOrElse(trail) { return null }
        if (!open) {
            val gates = state.gatesOpen.toMutableList()
            gates[trail] = true
            return state.copy(gatesOpen = gates)
        }
        if (!canSeal(state, trail)) return null
        val cost = Rules.sealCost(relic)
        if (state.jade < cost) return null
        val gates = state.gatesOpen.toMutableList()
        gates[trail] = false
        return state.copy(
            gatesOpen = gates,
            jade = state.jade - cost,
            gatesSealed = state.gatesSealed + 1
        )
    }

    fun applyRoar(state: RunState, relic: RelicId?): RunState? {
        if (state.roarCharge < Rules.roarThreshold(relic)) return null
        val damage = Rules.roarDamage(relic)
        val survivors = state.beasts.mapNotNull { beast ->
            val armour = Rules.statsOf(beast.type).armour
            val left = beast.hp - maxOf(1, damage - armour)
            if (left > 0) beast.copy(hp = left) else null
        }
        val killed = state.beasts.filter { beast -> survivors.none { it.id == beast.id } }
        val waveCleared = killed.isNotEmpty() && survivors.isEmpty()
        return state.copy(
            beasts = survivors,
            roarCharge = 0,
            roarsUsed = state.roarsUsed + 1,
            prowlersKilled = state.prowlersKilled + killed.count { it.type == BeastType.PROWLER },
            beastsDefeated = state.beastsDefeated + killed.size,
            wavesRepelled = state.wavesRepelled + if (waveCleared) 1 else 0
        )
    }

    fun previewSteps(state: RunState, relic: RelicId?): Map<Int, Int> =
        state.beasts.associate { beast ->
            val step = if (willSprint(state, beast, relic)) 2 else 1
            beast.id to minOf(GameConfig.TEMPLE_ROW, beast.row + step)
        }

    private fun isSpawnTurn(turn: Int): Boolean = turn >= 1 && (turn - 1) % 2 == 0

    private fun waveComposition(state: RunState, waveIndex: Int): List<BeastType> {
        val expedition = state.expedition
        val random = Random(state.seed * 31 + waveIndex)
        val size = expedition.beastsPerWave
        val types = mutableListOf<BeastType>()
        val isFinalWave = waveIndex == expedition.waves - 1
        if (expedition.hasBoss && isFinalWave) types.add(BeastType.SHADOW)
        while (types.size < size) {
            val ape = expedition.hasApe && random.nextInt(100) < 40 + waveIndex * 5
            types.add(if (ape) BeastType.APE else BeastType.PROWLER)
        }
        return types.shuffled(random)
    }
}
