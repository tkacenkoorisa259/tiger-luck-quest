package com.Hefputu5.Nugsis.domain.repository

import com.Hefputu5.Nugsis.domain.model.PlayerProgress
import com.Hefputu5.Nugsis.domain.model.RunSummary

interface ProgressRepository {
    fun load(): PlayerProgress
    fun recordRun(summary: RunSummary, streak: Int, counters: RunCounters)
    fun reset()
}

data class RunCounters(
    val archersPlaced: Int,
    val prowlersKilled: Int,
    val gatesSealed: Int,
    val roarsUsed: Int,
    val sprintSurvived: Boolean,
    val lostWithZeroWards: Boolean
)
