package com.Hefputu5.Nugsis.presentation.progress

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.Hefputu5.Nugsis.core.di.AppViewModelFactory
import com.Hefputu5.Nugsis.core.theme.Palette
import com.Hefputu5.Nugsis.core.ui.ClayTile
import com.Hefputu5.Nugsis.core.ui.Labels
import com.Hefputu5.Nugsis.core.ui.ScreenBackground
import com.Hefputu5.Nugsis.core.ui.ScreenHeader
import com.Hefputu5.Nugsis.core.ui.StatCard

@Composable
fun ProgressPanel(onBack: () -> Unit) {
    val progressViewModel: ProgressViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by progressViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(progressViewModel) {
        progressViewModel.refresh()
    }

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
            ScreenHeader(
                title = stringResource(R.string.title_progress),
                onBack = onBack
            )

            SummaryGrid(state = state)

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.rows, key = { it.index }) { row ->
                    ExpeditionLine(row = row)
                }
            }

            if (state.bestStreak > 0) {
                StreakCard(streak = state.bestStreak)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SummaryGrid(state: ProgressUiState) {
    val cards = buildList {
        if (state.clearedLevels > 0) {
            add(Triple(stringResource(R.string.stat_expeditions), state.clearedLevels, Palette.Jade))
        }
        if (state.totalStars > 0) {
            add(Triple(stringResource(R.string.stat_stars), state.totalStars, Palette.Gold))
        }
        if (state.wavesRepelled > 0) {
            add(Triple(stringResource(R.string.stat_waves_repelled), state.wavesRepelled, Palette.Amber))
        }
        if (state.jadeEarned > 0) {
            add(Triple(stringResource(R.string.stat_jade_earned), state.jadeEarned, Palette.JadeSoft))
        }
    }
    if (cards.size < 2) {
        Text(
            text = stringResource(R.string.progress_empty),
            style = MaterialTheme.typography.labelSmall,
            color = Palette.TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 18.dp)
        )
        return
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        for (chunk in cards.chunked(2)) {
            if (chunk.size < 2) continue
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for (card in chunk) {
                    StatCard(
                        label = card.first,
                        value = stringResource(R.string.count_fmt, card.second),
                        accent = card.third,
                        modifier = Modifier.weight(1f).height(60.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ExpeditionLine(row: ExpeditionRow) {
    ClayTile(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp),
        container = if (row.unlocked) Palette.SurfaceRaised else Palette.Surface,
        accent = if (row.unlocked) Palette.Jade else Palette.TextMuted,
        radius = 16.dp,
        elevation = if (row.unlocked) 8.dp else 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.count_fmt, row.index),
                style = MaterialTheme.typography.titleMedium,
                color = if (row.unlocked) Palette.Jade else Palette.TextMuted
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(Labels.levelName(row.index)),
                    style = MaterialTheme.typography.titleSmall,
                    color = if (row.unlocked) Palette.TextPrimary else Palette.TextMuted
                )
                Text(
                    text = if (row.unlocked) {
                        stringResource(R.string.best_wards_fmt, row.bestWards)
                    } else {
                        stringResource(R.string.locked)
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = Palette.TextMuted
                )
            }
            if (row.unlocked) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    for (star in 0 until 3) {
                        Icon(
                            painter = painterResource(R.drawable.ic_star),
                            contentDescription = null,
                            tint = if (star < row.stars) {
                                Palette.Gold
                            } else {
                                Palette.TextPrimary.copy(alpha = 0.16f)
                            },
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun StreakCard(streak: Int) {
    ClayTile(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(70.dp),
        container = Palette.SurfaceAlt,
        accent = Palette.Amber,
        radius = 18.dp,
        elevation = 10.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.stat_best_streak),
                style = MaterialTheme.typography.labelSmall,
                color = Palette.Amber
            )
            Text(
                text = stringResource(R.string.streak_fmt, streak),
                style = MaterialTheme.typography.titleSmall,
                color = Palette.TextPrimary
            )
        }
    }
}
