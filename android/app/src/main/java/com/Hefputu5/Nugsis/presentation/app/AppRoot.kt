package com.Hefputu5.Nugsis.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Hefputu5.Nugsis.core.config.GameConfig
import com.Hefputu5.Nugsis.core.di.AppViewModelFactory
import com.Hefputu5.Nugsis.core.navigation.Screen
import com.Hefputu5.Nugsis.core.theme.Palette
import com.Hefputu5.Nugsis.presentation.collection.CollectionPanel
import com.Hefputu5.Nugsis.presentation.game.GameScreen
import com.Hefputu5.Nugsis.presentation.levels.LevelSelectPanel
import com.Hefputu5.Nugsis.presentation.menu.MenuScreen
import com.Hefputu5.Nugsis.presentation.progress.ProgressPanel
import com.Hefputu5.Nugsis.presentation.result.ResultScreen
import com.Hefputu5.Nugsis.presentation.settings.SettingsPanel
import com.Hefputu5.Nugsis.presentation.splash.SplashScreen

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory.factory)
    val screen by appViewModel.screen.collectAsStateWithLifecycle()

    BackHandler(enabled = screen !is Screen.Splash && screen !is Screen.Menu) {
        appViewModel.back()
    }

    Box(modifier = Modifier.fillMaxSize().background(Palette.BackgroundBase)) {
        AnimatedContent(
            targetState = screen,
            transitionSpec = {
                (
                    fadeIn(tween(GameConfig.SCREEN_FADE_MS)) +
                        slideInVertically(tween(GameConfig.SCREEN_FADE_MS)) { full -> full / 14 }
                    ) togetherWith fadeOut(tween(GameConfig.SCREEN_FADE_MS - 40))
            },
            label = "screen"
        ) { target ->
            when (target) {
                is Screen.Splash -> SplashScreen(onFinished = { appViewModel.openMenu() })

                is Screen.Menu -> MenuScreen(
                    onPlay = { level -> appViewModel.startRun(level) },
                    onLevels = { appViewModel.navigate(Screen.LevelSelect) },
                    onCollection = { appViewModel.navigate(Screen.Collection) },
                    onProgress = { appViewModel.navigate(Screen.Progress) },
                    onSettings = { appViewModel.navigate(Screen.Settings) }
                )

                is Screen.LevelSelect -> LevelSelectPanel(
                    onBack = { appViewModel.back() },
                    onPlay = { level -> appViewModel.startRun(level) }
                )

                is Screen.Game -> GameScreen(
                    levelIndex = target.levelIndex,
                    attempt = target.attempt,
                    onMenu = { appViewModel.openMenu() },
                    onFinished = { summary -> appViewModel.showResult(summary) }
                )

                is Screen.GameOver -> ResultScreen(
                    summary = target.summary,
                    onPlayAgain = { appViewModel.startRun(target.summary.levelIndex) },
                    onNextExpedition = { appViewModel.startRun(target.summary.levelIndex + 1) },
                    onMenu = { appViewModel.openMenu() }
                )

                is Screen.Collection -> CollectionPanel(onBack = { appViewModel.back() })

                is Screen.Progress -> ProgressPanel(onBack = { appViewModel.back() })

                is Screen.Settings -> SettingsPanel(onBack = { appViewModel.back() })
            }
        }
    }
}
