package com.Hefputu5.Nugsis.presentation.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Hefputu5.Nugsis.R
import com.Hefputu5.Nugsis.core.config.GameConfig
import com.Hefputu5.Nugsis.core.di.AppViewModelFactory
import com.Hefputu5.Nugsis.core.theme.Palette
import com.Hefputu5.Nugsis.core.ui.ClayTile
import com.Hefputu5.Nugsis.core.ui.PrimaryButton
import com.Hefputu5.Nugsis.core.ui.ScreenBackground
import com.Hefputu5.Nugsis.core.ui.SecondaryButton
import com.Hefputu5.Nugsis.core.ui.StatCard
import com.Hefputu5.Nugsis.domain.model.GuardianType
import com.Hefputu5.Nugsis.domain.model.RunSummary
import com.Hefputu5.Nugsis.domain.model.TurnPhase
import com.Hefputu5.Nugsis.presentation.dialog.ConfirmDialog
import com.Hefputu5.Nugsis.presentation.dialog.PauseDialog
import com.Hefputu5.Nugsis.presentation.game.components.GameBoard
import com.Hefputu5.Nugsis.presentation.game.components.GuardianChip
import com.Hefputu5.Nugsis.presentation.game.components.TurnTimerPips
import com.Hefputu5.Nugsis.presentation.game.components.WardRow

@Composable
fun GameScreen(
    levelIndex: Int,
    attempt: Int,
    onMenu: () -> Unit,
    onFinished: (RunSummary) -> Unit
) {
    val gameViewModel: GameViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by gameViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(levelIndex, attempt) {
        gameViewModel.enterRun(levelIndex, attempt)
    }

    LaunchedEffect(gameViewModel) {
        gameViewModel.events.collect { summary -> onFinished(summary) }
    }

    DisposableEffect(levelIndex, attempt) {
        onDispose { gameViewModel.leaveRun() }
    }

    GameContent(
        state = state,
        onPause = { gameViewModel.pause() },
        onArm = { type -> gameViewModel.arm(type) },
        onTile = { cell -> gameViewModel.tapCell(cell) },
        onGate = { trail -> gameViewModel.tapGate(trail) },
        onRoar = { gameViewModel.roar() },
        onCommit = { gameViewModel.requestCommit() }
    )

    if (state.paused) {
        PauseDialog(
            onResume = { gameViewModel.resume() },
            onRestart = { gameViewModel.restart() },
            onMenu = onMenu
        )
    }

    if (state.askConfirm) {
        ConfirmDialog(
            title = stringResource(R.string.confirm_turn_title),
            body = stringResource(R.string.confirm_turn_body_fmt, state.jade),
            confirmText = stringResource(R.string.confirm),
            onConfirm = { gameViewModel.confirmCommit() },
            onDismiss = { gameViewModel.dismissConfirm() }
        )
    }
}

@Composable
private fun GameContent(
    state: GameUiState,
    onPause: () -> Unit,
    onArm: (GuardianType) -> Unit,
    onTile: (Int) -> Unit,
    onGate: (Int) -> Unit,
    onRoar: () -> Unit,
    onCommit: () -> Unit
) {
    ScreenBackground(
        overlay = listOf(
            Color(0xE6041009),
            Color(0xB30D2818),
            Color(0xF2041009)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            GameHeader(state = state, onPause = onPause)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                GameBoard(
                    state = state,
                    onTileTap = onTile,
                    onGateTap = onGate
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    label = stringResource(R.string.stat_jade),
                    value = stringResource(R.string.count_fmt, state.jade),
                    accent = Palette.Jade,
                    iconRes = R.drawable.ic_jade,
                    announce = true,
                    modifier = Modifier.weight(1f).fillMaxWidth()
                )
                StatCard(
                    label = stringResource(R.string.stat_roar),
                    value = stringResource(R.string.ratio_fmt, state.roarCharge, state.roarThreshold),
                    accent = Palette.Amber,
                    iconRes = R.drawable.ic_roar,
                    modifier = Modifier.weight(1f).fillMaxWidth()
                )
                StatCard(
                    label = stringResource(R.string.stat_time),
                    value = stringResource(R.string.count_fmt, state.secondsLeft),
                    accent = Palette.TextPrimary,
                    iconRes = R.drawable.ic_star,
                    modifier = Modifier.weight(1f).fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            TurnTimerPips(
                total = GameConfig.TURN_TIMER_SECONDS,
                left = state.secondsLeft,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            ControlsBlock(
                state = state,
                onArm = onArm,
                onRoar = onRoar,
                onCommit = onCommit
            )
        }
    }
}

@Composable
private fun GameHeader(state: GameUiState, onPause: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(Palette.Night.copy(alpha = 0.5f))
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ClayTile(
            modifier = Modifier.size(48.dp),
            container = Palette.SurfaceRaised,
            accent = Palette.Amber,
            radius = 24.dp,
            elevation = 10.dp,
            label = stringResource(R.string.cd_pause),
            onClick = onPause
        ) {
            Image(
                painter = painterResource(R.drawable.icon_pause),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.expedition_fmt, state.levelIndex),
                style = MaterialTheme.typography.labelSmall,
                color = Palette.TextSecondary
            )
            Text(
                text = stringResource(R.string.turn_fmt, state.turn, state.turnLimit),
                style = MaterialTheme.typography.titleMedium,
                color = Palette.TextPrimary
            )
        }
        WardRow(total = state.maxWards, left = state.wards)
    }
}

@Composable
private fun ControlsBlock(
    state: GameUiState,
    onArm: (GuardianType) -> Unit,
    onRoar: () -> Unit,
    onCommit: () -> Unit
) {
    val hint = when {
        state.armed != null -> stringResource(R.string.hint_armed)
        state.sprintSeen -> stringResource(R.string.hint_sprint)
        state.guardians.isEmpty() -> stringResource(R.string.hint_pick)
        else -> stringResource(R.string.hint_idle)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(Palette.Surface.copy(alpha = 0.94f))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = hint,
            style = MaterialTheme.typography.labelSmall,
            color = Palette.TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (type in GuardianType.entries) {
                GuardianChip(
                    type = type,
                    cost = state.costOf(type),
                    affordable = state.interactive && state.jade >= state.costOf(type),
                    armed = state.armed == type,
                    onClick = { onArm(type) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SecondaryButton(
                text = stringResource(R.string.roar),
                onClick = onRoar,
                iconRes = R.drawable.ic_roar,
                enabled = state.interactive && state.roarReady,
                accent = Palette.Amber,
                modifier = Modifier.width(126.dp)
            )
            PrimaryButton(
                text = if (state.phase == TurnPhase.RESOLVING) {
                    stringResource(R.string.resolving)
                } else {
                    stringResource(R.string.start_turn)
                },
                onClick = onCommit,
                iconRes = R.drawable.ic_play,
                enabled = state.interactive,
                modifier = Modifier.weight(1f),
                height = 56
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}
