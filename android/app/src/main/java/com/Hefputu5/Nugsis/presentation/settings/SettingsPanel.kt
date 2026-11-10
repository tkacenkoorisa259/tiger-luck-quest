package com.Hefputu5.Nugsis.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Hefputu5.Nugsis.R
import com.Hefputu5.Nugsis.core.di.AppViewModelFactory
import com.Hefputu5.Nugsis.core.theme.Palette
import com.Hefputu5.Nugsis.core.ui.ClayTile
import com.Hefputu5.Nugsis.core.ui.ScreenBackground
import com.Hefputu5.Nugsis.core.ui.ScreenHeader
import com.Hefputu5.Nugsis.core.ui.SecondaryButton
import com.Hefputu5.Nugsis.domain.model.TurnSpeed
import com.Hefputu5.Nugsis.presentation.dialog.ConfirmDialog

@Composable
fun SettingsPanel(onBack: () -> Unit) {
    val settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by settingsViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(settingsViewModel) {
        settingsViewModel.refresh()
    }

    LaunchedEffect(state.resetDone) {
        if (state.resetDone) {
            settingsViewModel.consumeResetDone()
            onBack()
        }
    }

    ScreenBackground(
        overlay = listOf(
            Color(0xE60D2818),
            Color(0xB3132D1E),
            Color(0xF2041009)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            ScreenHeader(
                title = stringResource(R.string.title_settings),
                onBack = onBack
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SwitchRow(
                    title = stringResource(R.string.setting_path_preview),
                    caption = stringResource(R.string.setting_path_preview_caption),
                    checked = state.settings.pathPreview,
                    onChange = { settingsViewModel.setPathPreview(it) }
                )
                SwitchRow(
                    title = stringResource(R.string.setting_confirm_turn),
                    caption = stringResource(R.string.setting_confirm_turn_caption),
                    checked = state.settings.confirmTurnStart,
                    onChange = { settingsViewModel.setConfirmTurnStart(it) }
                )
                SpeedRow(
                    speed = state.settings.turnSpeed,
                    onChange = { settingsViewModel.setTurnSpeed(it) }
                )
                ResetRow(onReset = { settingsViewModel.askReset() })
            }
        }
    }

    if (state.askReset) {
        ConfirmDialog(
            title = stringResource(R.string.reset_progress),
            body = stringResource(R.string.reset_progress_caption),
            confirmText = stringResource(R.string.confirm),
            onConfirm = { settingsViewModel.confirmReset() },
            onDismiss = { settingsViewModel.dismissReset() },
            accent = Palette.Crimson
        )
    }
}

@Composable
private fun SwitchRow(
    title: String,
    caption: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit
) {
    val stateText = stringResource(if (checked) R.string.cd_on else R.string.cd_off)
    ClayTile(
        modifier = Modifier
            .fillMaxWidth()
            .height(78.dp),
        container = Palette.SurfaceRaised,
        accent = if (checked) Palette.Jade else Palette.TextMuted,
        radius = 20.dp,
        elevation = 10.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = Palette.TextPrimary
                )
                Text(
                    text = caption,
                    style = MaterialTheme.typography.labelSmall,
                    color = Palette.TextMuted
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Palette.OnAccent,
                    checkedTrackColor = Palette.Jade,
                    uncheckedThumbColor = Palette.TextMuted,
                    uncheckedTrackColor = Palette.Surface
                ),
                modifier = Modifier.semantics { stateDescription = stateText }
            )
        }
    }
}

@Composable
private fun SpeedRow(speed: TurnSpeed, onChange: (TurnSpeed) -> Unit) {
    ClayTile(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp),
        container = Palette.SurfaceRaised,
        accent = Palette.Amber,
        radius = 20.dp,
        elevation = 10.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.setting_turn_speed),
                style = MaterialTheme.typography.titleSmall,
                color = Palette.TextPrimary
            )
            Text(
                text = stringResource(R.string.setting_turn_speed_caption),
                style = MaterialTheme.typography.labelSmall,
                color = Palette.TextMuted
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SecondaryButton(
                    text = stringResource(R.string.speed_normal),
                    onClick = { onChange(TurnSpeed.NORMAL) },
                    accent = if (speed == TurnSpeed.NORMAL) Palette.Jade else Palette.TextMuted,
                    modifier = Modifier.width(126.dp)
                )
                SecondaryButton(
                    text = stringResource(R.string.speed_fast),
                    onClick = { onChange(TurnSpeed.FAST) },
                    accent = if (speed == TurnSpeed.FAST) Palette.Amber else Palette.TextMuted,
                    modifier = Modifier.width(126.dp)
                )
            }
        }
    }
}

@Composable
private fun ResetRow(onReset: () -> Unit) {
    ClayTile(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp),
        container = Palette.Surface,
        accent = Palette.Crimson,
        radius = 20.dp,
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.reset_progress_caption),
                style = MaterialTheme.typography.labelSmall,
                color = Palette.TextMuted
            )
            Spacer(modifier = Modifier.height(8.dp))
            SecondaryButton(
                text = stringResource(R.string.reset_progress),
                onClick = onReset,
                accent = Palette.Crimson,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
