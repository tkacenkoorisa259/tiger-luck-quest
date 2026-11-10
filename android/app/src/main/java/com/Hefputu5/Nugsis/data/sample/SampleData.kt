package com.Hefputu5.Nugsis.data.sample

import com.Hefputu5.Nugsis.domain.model.Expedition

object SampleData {

    val expeditions: List<Expedition> = listOf(
        Expedition(index = 1, turnLimit = 8, waves = 3, beastsPerWave = 2, hasApe = false, hasBoss = false, rubbleCount = 2),
        Expedition(index = 2, turnLimit = 10, waves = 4, beastsPerWave = 2, hasApe = true, hasBoss = false, rubbleCount = 2),
        Expedition(index = 3, turnLimit = 11, waves = 4, beastsPerWave = 2, hasApe = true, hasBoss = false, rubbleCount = 3),
        Expedition(index = 4, turnLimit = 12, waves = 5, beastsPerWave = 3, hasApe = true, hasBoss = false, rubbleCount = 3),
        Expedition(index = 5, turnLimit = 13, waves = 5, beastsPerWave = 3, hasApe = true, hasBoss = false, rubbleCount = 4),
        Expedition(index = 6, turnLimit = 14, waves = 6, beastsPerWave = 3, hasApe = true, hasBoss = false, rubbleCount = 4),
        Expedition(index = 7, turnLimit = 15, waves = 6, beastsPerWave = 4, hasApe = true, hasBoss = false, rubbleCount = 4),
        Expedition(index = 8, turnLimit = 16, waves = 7, beastsPerWave = 4, hasApe = true, hasBoss = true, rubbleCount = 4)
    )

    fun expedition(index: Int): Expedition =
        expeditions.getOrElse(index - 1) { expeditions.first() }
}
