package com.Hefputu5.Nugsis.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Hefputu5.Nugsis.core.config.GameConfig
import com.Hefputu5.Nugsis.data.sample.SampleData
import com.Hefputu5.Nugsis.domain.model.AppSettings
import com.Hefputu5.Nugsis.domain.model.BoardLayout
import com.Hefputu5.Nugsis.domain.model.GameOutcome
import com.Hefputu5.Nugsis.domain.model.GuardianType
import com.Hefputu5.Nugsis.domain.model.RelicId
import com.Hefputu5.Nugsis.domain.model.RunState
import com.Hefputu5.Nugsis.domain.model.RunSummary
import com.Hefputu5.Nugsis.domain.model.Rules
import com.Hefputu5.Nugsis.domain.model.TurnPhase
import com.Hefputu5.Nugsis.domain.model.TurnSpeed
import com.Hefputu5.Nugsis.domain.repository.ProgressRepository
import com.Hefputu5.Nugsis.domain.repository.RelicRepository
import com.Hefputu5.Nugsis.domain.repository.RunCounters
import com.Hefputu5.Nugsis.domain.repository.SettingsRepository
import com.Hefputu5.Nugsis.domain.usecase.CalculateStarsUseCase
import com.Hefputu5.Nugsis.domain.usecase.EvaluateRelicUnlocksUseCase
import com.Hefputu5.Nugsis.domain.usecase.GenerateBoardUseCase
import com.Hefputu5.Nugsis.domain.usecase.ResolveTurnUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameViewModel(
    private val progressRepository: ProgressRepository,
    private val settingsRepository: SettingsRepository,
    private val relicRepository: RelicRepository,
    private val generateBoard: GenerateBoardUseCase,
    private val resolveTurn: ResolveTurnUseCase,
    private val calculateStars: CalculateStarsUseCase,
    private val evaluateRelics: EvaluateRelicUnlocksUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private val finished = Channel<RunSummary>(Channel.BUFFERED)
    val events: Flow<RunSummary> = finished.receiveAsFlow()

    private var run: RunState? = null
    private var relic: RelicId? = null
    private var settings: AppSettings = AppSettings(true, true, TurnSpeed.NORMAL)
    private var runKey: String = ""
    private var levelIndex: Int = 1
    private var seedAttempt: Int = 0
    private var timerJob: Job? = null
    private var resolveJob: Job? = null
    private var userPaused: Boolean = false
    private var secondsLeft: Int = GameConfig.TURN_TIMER_SECONDS

    fun enterRun(level: Int, runAttempt: Int) {
        val key = level.toString() + "-" + runAttempt
        if (key == runKey) {
            if (!userPaused) startTimer()
            return
        }
        runKey = key
        levelIndex = level
        seedAttempt = runAttempt
        beginRun()
    }

    fun leaveRun() {
        timerJob?.cancel()
        timerJob = null
        resolveJob?.cancel()
        resolveJob = null
    }

    private fun beginRun() {
        timerJob?.cancel()
        resolveJob?.cancel()
        userPaused = false
        _state.update { GameUiState(levelIndex = levelIndex, loading = true) }
        viewModelScope.launch {
            val prepared = withContext(Dispatchers.Default) {
                val loadedSettings = settingsRepository.load()
                val equipped = relicRepository.equipped()
                val expedition = SampleData.expedition(levelIndex)
                Triple(loadedSettings, equipped, generateBoard(expedition, equipped, seedAttempt))
            }
            settings = prepared.first
            relic = prepared.second
            run = prepared.third
            publish(loading = false)
            resetTimer()
            startTimer()
        }
    }

    fun arm(type: GuardianType) {
        val current = run ?: return
        if (!_state.value.interactive) return
        if (current.jade < Rules.costOf(type, relic)) return
        _state.update { it.copy(armed = if (it.armed == type) null else type) }
    }

    fun tapCell(cell: Int) {
        val current = run ?: return
        val armed = _state.value.armed ?: return
        if (!_state.value.interactive) return
        val next = resolveTurn.place(current, armed, cell, relic) ?: return
        run = next
        val stillAffordable = next.jade >= Rules.costOf(armed, relic)
        publish(armed = if (stillAffordable) armed else null)
    }

    fun tapGate(trail: Int) {
        val current = run ?: return
        if (!_state.value.interactive) return
        val next = resolveTurn.toggleGate(current, trail, relic) ?: return
        run = next
        publish()
    }

    fun roar() {
        val current = run ?: return
        if (!_state.value.interactive) return
        val next = resolveTurn.applyRoar(current, relic) ?: return
        run = next
        publish()
    }

    fun requestCommit() {
        val current = run ?: return
        if (!_state.value.interactive) return
        val cheapest = GuardianType.entries.minOf { Rules.costOf(it, relic) }
        if (settings.confirmTurnStart && current.jade >= cheapest && hasFreePlatform(current)) {
            _state.update { it.copy(askConfirm = true) }
            return
        }
        commit()
    }

    fun dismissConfirm() {
        _state.update { it.copy(askConfirm = false) }
    }

    fun confirmCommit() {
        _state.update { it.copy(askConfirm = false) }
        commit()
    }

    fun pause() {
        if (_state.value.phase == TurnPhase.FINISHED) return
        userPaused = true
        timerJob?.cancel()
        timerJob = null
        _state.update { it.copy(paused = true, armed = null) }
    }

    fun resume() {
        if (!userPaused) return
        userPaused = false
        _state.update { it.copy(paused = false) }
        if (_state.value.phase == TurnPhase.PLANNING) startTimer()
    }

    fun restart() {
        userPaused = false
        seedAttempt += 1
        beginRun()
    }

    private fun hasFreePlatform(state: RunState): Boolean =
        BoardLayout.platformCells.any { cell ->
            cell !in state.rubble && state.guardians.none {
                BoardLayout.cellIndex(it.col, it.row) == cell
            }
        }

    private fun startTimer() {
        timerJob?.cancel()
        if (run == null) return
        if (_state.value.phase != TurnPhase.PLANNING) return
        timerJob = viewModelScope.launch {
            _state.update { it.copy(secondsLeft = secondsLeft) }
            while (secondsLeft > 0) {
                delay(GameConfig.TICK_MS)
                secondsLeft -= 1
                _state.update { it.copy(secondsLeft = secondsLeft) }
            }
            commit()
        }
    }

    private fun resetTimer() {
        secondsLeft = GameConfig.TURN_TIMER_SECONDS
    }

    private fun commit() {
        val current = run ?: return
        if (_state.value.phase != TurnPhase.PLANNING) return
        timerJob?.cancel()
        timerJob = null
        resolveJob?.cancel()
        _state.update { it.copy(phase = TurnPhase.RESOLVING, armed = null, askConfirm = false) }
        resolveJob = viewModelScope.launch {
            val fast = settings.turnSpeed == TurnSpeed.FAST
            val step = if (fast) GameConfig.RESOLVE_STEP_MS / 2 else GameConfig.RESOLVE_STEP_MS
            val enemyStep = if (fast) GameConfig.ENEMY_STEP_MS / 2 else GameConfig.ENEMY_STEP_MS

            var next = resolveTurn.guardiansFire(current, relic)
            run = next
            publish(phase = TurnPhase.RESOLVING)
            delay(step)

            next = resolveTurn.beastsMove(next, relic)
            run = next
            publish(phase = TurnPhase.RESOLVING)
            delay(enemyStep)

            next = resolveTurn.collectIncome(next, relic)
            run = next
            publish(phase = TurnPhase.RESOLVING)
            delay(step)

            next = resolveTurn.spawnWave(next)
            run = next
            publish(phase = TurnPhase.RESOLVING)
            delay(step)

            next = resolveTurn.endCheck(next)
            run = next
            if (next.outcome != null) {
                finishRun(next)
            } else {
                publish(phase = TurnPhase.PLANNING)
                resetTimer()
                startTimer()
            }
        }
    }

    private suspend fun finishRun(state: RunState) {
        val outcome = state.outcome ?: GameOutcome.DEFEAT
        val stars = calculateStars(state)
        val streak = if (state.wards >= state.maxWards) state.wavesRepelled else 0
        val counters = RunCounters(
            archersPlaced = state.archersPlaced,
            prowlersKilled = state.prowlersKilled,
            gatesSealed = state.gatesSealed,
            roarsUsed = state.roarsUsed,
            sprintSurvived = state.sprintSeen && state.wards > 0,
            lostWithZeroWards = outcome == GameOutcome.DEFEAT && state.wards <= 0
        )
        val base = RunSummary(
            levelIndex = levelIndex,
            outcome = outcome,
            wardsLeft = state.wards,
            maxWards = state.maxWards,
            turnsHeld = state.turn,
            wavesRepelled = state.beastsDefeated,
            jadeEarned = state.jadeEarned,
            stars = stars,
            openTrails = state.gatesOpen.count { it },
            newRelics = emptyList(),
            nextUnlocked = outcome == GameOutcome.VICTORY && levelIndex < GameConfig.EXPEDITION_COUNT
        )
        val fresh = withContext(Dispatchers.Default) {
            progressRepository.recordRun(base, streak, counters)
            val progress = progressRepository.load()
            val unlocked = relicRepository.unlocked()
            val newly = evaluateRelics(progress, unlocked)
            relicRepository.unlock(newly)
            newly
        }
        _state.update { it.copy(phase = TurnPhase.FINISHED, armed = null, askConfirm = false) }
        finished.send(base.copy(newRelics = fresh))
    }

    private fun publish(
        loading: Boolean = false,
        phase: TurnPhase = _state.value.phase,
        armed: GuardianType? = _state.value.armed
    ) {
        val current = run ?: return
        val expedition = current.expedition
        _state.update {
            it.copy(
                levelIndex = levelIndex,
                turn = current.turn,
                turnLimit = expedition.turnLimit,
                jade = current.jade,
                wards = current.wards,
                maxWards = current.maxWards,
                roarCharge = current.roarCharge,
                roarThreshold = Rules.roarThreshold(relic),
                phase = phase,
                armed = armed,
                guardians = current.guardians,
                beasts = current.beasts,
                rubble = current.rubble,
                gatesOpen = current.gatesOpen,
                bonusTrail = current.bonusTrail,
                archerCost = Rules.costOf(GuardianType.ARCHER, relic),
                sentryCost = Rules.costOf(GuardianType.SENTRY, relic),
                totemCost = Rules.costOf(GuardianType.TOTEM, relic),
                sealCost = Rules.sealCost(relic),
                pathPreview = settings.pathPreview,
                previewSteps = if (settings.pathPreview) {
                    resolveTurn.previewSteps(current, relic)
                } else {
                    emptyMap()
                },
                sprintSeen = current.sprintSeen,
                loading = loading
            )
        }
    }
}
