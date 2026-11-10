package com.Hefputu5.Nugsis.presentation.app

import androidx.lifecycle.ViewModel
import com.Hefputu5.Nugsis.core.navigation.Screen
import com.Hefputu5.Nugsis.domain.model.RunSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {

    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private val backStack = mutableListOf<Screen>()
    private var attempts = 0

    fun openMenu() {
        backStack.clear()
        _screen.value = Screen.Menu
    }

    fun navigate(target: Screen) {
        backStack.add(_screen.value)
        _screen.value = target
    }

    fun startRun(levelIndex: Int) {
        attempts += 1
        backStack.clear()
        backStack.add(Screen.Menu)
        _screen.value = Screen.Game(levelIndex, attempts)
    }

    fun showResult(summary: RunSummary) {
        backStack.clear()
        backStack.add(Screen.Menu)
        _screen.value = Screen.GameOver(summary)
    }

    fun back(): Boolean {
        val previous = backStack.removeLastOrNull() ?: return false
        _screen.value = previous
        return true
    }
}
