package com.Hefputu5.Nugsis.domain.usecase

import com.Hefputu5.Nugsis.core.config.GameConfig
import com.Hefputu5.Nugsis.domain.model.BoardLayout
import com.Hefputu5.Nugsis.domain.model.Expedition
import com.Hefputu5.Nugsis.domain.model.GameOutcome
import com.Hefputu5.Nugsis.domain.model.Guardian
import com.Hefputu5.Nugsis.domain.model.GuardianType
import com.Hefputu5.Nugsis.domain.model.RelicId
import com.Hefputu5.Nugsis.domain.model.RunState
import com.Hefputu5.Nugsis.domain.model.Rules
import kotlin.random.Random

class GenerateBoardUseCase(private val resolveTurn: ResolveTurnUseCase) {

    fun seedFor(levelIndex: Int, attempt: Int): Long =
        (levelIndex.toLong() * 7919L) xor (attempt.toLong() * 104729L)

    operator fun invoke(expedition: Expedition, relic: RelicId?, attempt: Int): RunState {
        val baseSeed = seedFor(expedition.index, attempt)
        for (retry in 0 until GameConfig.MAX_GENERATION_ATTEMPTS) {
            val seed = baseSeed + retry * 1013L
            val candidate = build(expedition, relic, seed, fallback = false)
            if (isSolvable(candidate, relic)) return candidate
        }
        return build(expedition, relic, baseSeed, fallback = true)
    }

    private fun build(
        expedition: Expedition,
        relic: RelicId?,
        seed: Long,
        fallback: Boolean
    ): RunState {
        val random = Random(seed)
        val platforms = BoardLayout.platformCells
        val rubbleCount = expedition.rubbleCount.coerceIn(0, platforms.size - 4)
        val rubble = if (fallback) {
            setOf(platforms.first(), platforms.last())
        } else {
            platforms.shuffled(random).take(rubbleCount).toSet()
        }
        val state = RunState(
            expedition = expedition,
            seed = seed,
            turn = 1,
            jade = Rules.startingJade(relic),
            wards = Rules.startingWards(relic),
            maxWards = Rules.startingWards(relic),
            roarCharge = 0,
            rubble = rubble,
            bonusTrail = random.nextInt(GameConfig.TRAIL_COUNT),
            guardians = emptyList(),
            beasts = emptyList(),
            gatesOpen = List(GameConfig.TRAIL_COUNT) { true },
            pendingSpawns = emptyList(),
            spawnCursor = 0,
            wavesSpawned = 0,
            wavesRepelled = 0,
            beastsDefeated = 0,
            jadeEarned = 0,
            nextBeastId = 1,
            sprintSeen = false,
            prowlersKilled = 0,
            archersPlaced = 0,
            gatesSealed = 0,
            roarsUsed = 0,
            outcome = null
        )
        return resolveTurn.openingWave(state)
    }

    private fun isSolvable(start: RunState, relic: RelicId?): Boolean {
        var state = start
        var guard = 0
        while (state.outcome == null && guard < start.expedition.turnLimit + 12) {
            state = referencePlanning(state, relic)
            state = resolveTurn.resolveFully(state, relic)
            guard += 1
        }
        return state.outcome == GameOutcome.VICTORY && state.wards >= 1
    }

    private fun referencePlanning(state: RunState, relic: RelicId?): RunState {
        var current = state
        var budget = 6
        while (budget > 0) {
            val cost = Rules.costOf(GuardianType.ARCHER, relic)
            if (current.jade < cost) break
            val free = BoardLayout.platformCells.filter { cell ->
                cell !in current.rubble && current.guardians.none {
                    BoardLayout.cellIndex(it.col, it.row) == cell
                }
            }
            if (free.isEmpty()) break
            val best = free.maxByOrNull { cell -> coverage(current, cell) } ?: break
            current = current.copy(
                jade = current.jade - cost,
                archersPlaced = current.archersPlaced + 1,
                guardians = current.guardians + Guardian(
                    type = GuardianType.ARCHER,
                    col = BoardLayout.columnOf(best),
                    row = BoardLayout.rowOf(best)
                )
            )
            budget -= 1
        }
        return current
    }

    private fun coverage(state: RunState, cell: Int): Int {
        val col = BoardLayout.columnOf(cell)
        val row = BoardLayout.rowOf(cell)
        val occupied = state.beasts.count {
            BoardLayout.distance(col, row, BoardLayout.trailColumn(it.trail), it.row) <= 2
        }
        val reach = (1 until GameConfig.TEMPLE_ROW).sumOf { trailRow ->
            (0 until GameConfig.TRAIL_COUNT).count { trail ->
                BoardLayout.distance(col, row, BoardLayout.trailColumn(trail), trailRow) <= 2
            }
        }
        return occupied * 10 + reach + row
    }
}
