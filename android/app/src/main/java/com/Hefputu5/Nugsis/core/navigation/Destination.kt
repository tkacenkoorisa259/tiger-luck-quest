package com.Hefputu5.Nugsis.core.navigation

import com.Hefputu5.Nugsis.domain.model.RunSummary

sealed interface Screen {
    data object Splash : Screen
    data object Menu : Screen
    data object LevelSelect : Screen
    data class Game(val levelIndex: Int, val attempt: Int) : Screen
    data class GameOver(val summary: RunSummary) : Screen
    data object Collection : Screen
    data object Progress : Screen
    data object Settings : Screen
}
