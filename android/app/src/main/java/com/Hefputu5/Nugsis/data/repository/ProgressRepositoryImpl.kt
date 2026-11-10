package com.Hefputu5.Nugsis.data.repository

import com.Hefputu5.Nugsis.core.config.GameConfig
import com.Hefputu5.Nugsis.data.local.PreferencesStore
import com.Hefputu5.Nugsis.domain.model.GameOutcome
import com.Hefputu5.Nugsis.domain.model.PlayerProgress
import com.Hefputu5.Nugsis.domain.model.RunSummary
import com.Hefputu5.Nugsis.domain.repository.ProgressRepository
import com.Hefputu5.Nugsis.domain.repository.RunCounters

class ProgressRepositoryImpl(private val store: PreferencesStore) : ProgressRepository {

    override fun load(): PlayerProgress = PlayerProgress(
        unlockedLevels = store.int(PreferencesStore.KEY_UNLOCKED_LEVELS, 1)
            .coerceIn(1, GameConfig.EXPEDITION_COUNT),
        stars = (1..GameConfig.EXPEDITION_COUNT).map {
            store.int(PreferencesStore.KEY_STARS + it, 0)
        },
        bestWards = (1..GameConfig.EXPEDITION_COUNT).map {
            store.int(PreferencesStore.KEY_BEST_WARDS + it, 0)
        },
        wavesRepelled = store.int(PreferencesStore.KEY_WAVES, 0),
        jadeEarned = store.int(PreferencesStore.KEY_JADE, 0),
        bestStreak = store.int(PreferencesStore.KEY_STREAK, 0),
        archersPlaced = store.int(PreferencesStore.KEY_ARCHERS, 0),
        prowlersKilled = store.int(PreferencesStore.KEY_PROWLERS, 0),
        gatesSealed = store.int(PreferencesStore.KEY_SEALED, 0),
        roarsUsed = store.int(PreferencesStore.KEY_ROARS, 0),
        sprintsSurvived = store.int(PreferencesStore.KEY_SPRINTS, 0),
        zeroWardLosses = store.int(PreferencesStore.KEY_ZERO_WARDS, 0)
    )

    override fun recordRun(summary: RunSummary, streak: Int, counters: RunCounters) {
        val level = summary.levelIndex
        val previousStars = store.int(PreferencesStore.KEY_STARS + level, 0)
        if (summary.stars > previousStars) {
            store.putInt(PreferencesStore.KEY_STARS + level, summary.stars)
        }
        val previousWards = store.int(PreferencesStore.KEY_BEST_WARDS + level, 0)
        if (summary.wardsLeft > previousWards) {
            store.putInt(PreferencesStore.KEY_BEST_WARDS + level, summary.wardsLeft)
        }
        if (summary.outcome == GameOutcome.VICTORY) {
            val unlocked = store.int(PreferencesStore.KEY_UNLOCKED_LEVELS, 1)
            val next = minOf(GameConfig.EXPEDITION_COUNT, maxOf(unlocked, level + 1))
            store.putInt(PreferencesStore.KEY_UNLOCKED_LEVELS, next)
        }
        store.putInt(
            PreferencesStore.KEY_WAVES,
            store.int(PreferencesStore.KEY_WAVES, 0) + summary.wavesRepelled
        )
        store.putInt(
            PreferencesStore.KEY_JADE,
            store.int(PreferencesStore.KEY_JADE, 0) + summary.jadeEarned
        )
        if (streak > store.int(PreferencesStore.KEY_STREAK, 0)) {
            store.putInt(PreferencesStore.KEY_STREAK, streak)
        }
        bump(PreferencesStore.KEY_ARCHERS, counters.archersPlaced)
        bump(PreferencesStore.KEY_PROWLERS, counters.prowlersKilled)
        bump(PreferencesStore.KEY_SEALED, counters.gatesSealed)
        bump(PreferencesStore.KEY_ROARS, counters.roarsUsed)
        if (counters.sprintSurvived) bump(PreferencesStore.KEY_SPRINTS, 1)
        if (counters.lostWithZeroWards) bump(PreferencesStore.KEY_ZERO_WARDS, 1)
    }

    override fun reset() {
        store.clear()
    }

    private fun bump(key: String, delta: Int) {
        if (delta <= 0) return
        store.putInt(key, store.int(key, 0) + delta)
    }
}
