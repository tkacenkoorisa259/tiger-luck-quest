package com.Hefputu5.Nugsis.core.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.Hefputu5.Nugsis.presentation.app.AppViewModel
import com.Hefputu5.Nugsis.presentation.collection.CollectionViewModel
import com.Hefputu5.Nugsis.presentation.game.GameViewModel
import com.Hefputu5.Nugsis.presentation.levels.LevelSelectViewModel
import com.Hefputu5.Nugsis.presentation.menu.MenuViewModel
import com.Hefputu5.Nugsis.presentation.progress.ProgressViewModel
import com.Hefputu5.Nugsis.presentation.result.ResultViewModel
import com.Hefputu5.Nugsis.presentation.settings.SettingsViewModel
import com.Hefputu5.Nugsis.presentation.splash.SplashViewModel

object AppViewModelFactory {

    val factory: ViewModelProvider.Factory = viewModelFactory {
        initializer { AppViewModel() }
        initializer { SplashViewModel() }
        initializer {
            MenuViewModel(
                ServiceLocator.progressRepository,
                ServiceLocator.relicRepository
            )
        }
        initializer { LevelSelectViewModel(ServiceLocator.progressRepository) }
        initializer {
            GameViewModel(
                ServiceLocator.progressRepository,
                ServiceLocator.settingsRepository,
                ServiceLocator.relicRepository,
                ServiceLocator.generateBoard,
                ServiceLocator.resolveTurn,
                ServiceLocator.calculateStars,
                ServiceLocator.evaluateRelics
            )
        }
        initializer { ResultViewModel(ServiceLocator.progressRepository) }
        initializer {
            CollectionViewModel(
                ServiceLocator.progressRepository,
                ServiceLocator.relicRepository,
                ServiceLocator.evaluateRelics
            )
        }
        initializer { ProgressViewModel(ServiceLocator.progressRepository) }
        initializer {
            SettingsViewModel(
                ServiceLocator.settingsRepository,
                ServiceLocator.progressRepository,
                ServiceLocator.relicRepository
            )
        }
    }
}
