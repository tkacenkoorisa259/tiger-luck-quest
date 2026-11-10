package com.Hefputu5.Nugsis.presentation.levels

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.Hefputu5.Nugsis.core.ui.BadgePill
import com.Hefputu5.Nugsis.core.ui.ClayTile
import com.Hefputu5.Nugsis.core.ui.Labels
import com.Hefputu5.Nugsis.core.ui.ScreenBackground
import com.Hefputu5.Nugsis.core.ui.ScreenHeader

@Composable
fun LevelSelectPanel(onBack: () -> Unit, onPlay: (Int) -> Unit) {
    val levelsViewModel: LevelSelectViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by levelsViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(levelsViewModel) {
        levelsViewModel.refresh()
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
                title = stringResource(R.string.title_expeditions),
                onBack = onBack
            ) {
                BadgePill(
                    text = stringResource(
                        R.string.ratio_fmt,
                        state.totalStars,
                        GameConfig.MAX_STARS
                    )
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.levels, key = { it.index }) { level ->
                    LevelTile(level = level, onPlay = onPlay)
                }
            }

            Text(
                text = stringResource(R.string.levels_footer),
                style = MaterialTheme.typography.labelSmall,
                color = Palette.TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            )
        }
    }
}

@Composable
private fun LevelTile(level: LevelCard, onPlay: (Int) -> Unit) {
    val label = stringResource(Labels.levelName(level.index))
    ClayTile(
        modifier = Modifier
            .fillMaxWidth()
            .height(138.dp),
        container = if (level.unlocked) Palette.SurfaceRaised else Palette.Surface,
        accent = if (level.unlocked) Palette.Jade else Palette.TextMuted,
        radius = 22.dp,
        elevation = if (level.unlocked) 12.dp else 4.dp,
        label = label,
        state = stringResource(if (level.unlocked) R.string.cd_unlocked else R.string.cd_locked),
        onClick = if (level.unlocked) {
            { onPlay(level.index) }
        } else {
            null
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (level.unlocked) {
                Text(
                    text = stringResource(R.string.count_fmt, level.index),
                    style = MaterialTheme.typography.displaySmall,
                    color = Palette.Jade
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleSmall,
                    color = Palette.TextPrimary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    for (star in 0 until 3) {
                        Icon(
                            painter = painterResource(R.drawable.ic_star),
                            contentDescription = null,
                            tint = if (star < level.stars) {
                                Palette.Gold
                            } else {
                                Palette.TextPrimary.copy(alpha = 0.18f)
                            },
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                    }
                }
                Text(
                    text = stringResource(R.string.level_turns_fmt, level.turnLimit, level.waves),
                    style = MaterialTheme.typography.labelSmall,
                    color = Palette.TextMuted
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_lock),
                    contentDescription = null,
                    tint = Palette.TextMuted,
                    modifier = Modifier.size(26.dp)
                )
                Text(
                    text = stringResource(R.string.locked),
                    style = MaterialTheme.typography.titleSmall,
                    color = Palette.TextMuted
                )
                Text(
                    text = stringResource(R.string.level_locked_fmt, level.index - 1),
                    style = MaterialTheme.typography.labelSmall,
                    color = Palette.TextMuted.copy(alpha = 0.75f)
                )
            }
        }
    }
}
