package com.Hefputu5.Nugsis.domain.usecase

import com.Hefputu5.Nugsis.domain.model.PlayerProgress
import com.Hefputu5.Nugsis.domain.model.RelicId
import com.Hefputu5.Nugsis.domain.model.RelicProgress

class EvaluateRelicUnlocksUseCase {

    fun target(id: RelicId): Int = when (id) {
        RelicId.JADE_FANG -> 1
        RelicId.SUN_DISC -> 3
        RelicId.BAMBOO_CHARM -> 25
        RelicId.TIGER_TOTEM -> 20
        RelicId.STONE_WARD -> 1
        RelicId.MIST_VEIL -> 1
        RelicId.AMBER_EYE -> 5
        RelicId.VINE_LOCK -> 15
        RelicId.SHADOW_PELT -> 1
    }

    fun current(id: RelicId, progress: PlayerProgress): Int = when (id) {
        RelicId.JADE_FANG -> if (progress.stars.getOrElse(0) { 0 } > 0) 1 else 0
        RelicId.SUN_DISC -> progress.totalStars
        RelicId.BAMBOO_CHARM -> progress.archersPlaced
        RelicId.TIGER_TOTEM -> progress.prowlersKilled
        RelicId.STONE_WARD -> progress.zeroWardLosses
        RelicId.MIST_VEIL -> progress.sprintsSurvived
        RelicId.AMBER_EYE -> progress.roarsUsed
        RelicId.VINE_LOCK -> progress.gatesSealed
        RelicId.SHADOW_PELT -> if (progress.stars.getOrElse(7) { 0 } > 0) 1 else 0
    }

    operator fun invoke(progress: PlayerProgress, alreadyUnlocked: Set<RelicId>): List<RelicId> =
        RelicId.entries.filter { it !in alreadyUnlocked && current(it, progress) >= target(it) }

    fun snapshot(
        progress: PlayerProgress,
        unlocked: Set<RelicId>,
        equipped: RelicId?
    ): List<RelicProgress> = RelicId.entries.map { id ->
        RelicProgress(
            id = id,
            unlocked = id in unlocked,
            equipped = id == equipped,
            current = minOf(current(id, progress), target(id)),
            target = target(id)
        )
    }
}
