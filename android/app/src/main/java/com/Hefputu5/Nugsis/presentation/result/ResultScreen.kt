package com.Hefputu5.Nugsis.presentation.result

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Hefputu5.Nugsis.R
import com.Hefputu5.Nugsis.core.di.AppViewModelFactory
import com.Hefputu5.Nugsis.core.theme.Palette
import com.Hefputu5.Nugsis.core.ui.ClayTile
import com.Hefputu5.Nugsis.core.ui.Labels
import com.Hefputu5.Nugsis.core.ui.PrimaryButton
import com.Hefputu5.Nugsis.core.ui.ScreenBackground
import com.Hefputu5.Nugsis.core.ui.SecondaryButton
import com.Hefputu5.Nugsis.core.ui.StatCard
import com.Hefputu5.Nugsis.domain.model.GameOutcome
import com.Hefputu5.Nugsis.domain.model.RunSummary

@Composable
fun ResultScreen(
    summary: RunSummary,
    onPlayAgain: () -> Unit,
    onNextExpedition: () -> Unit,
    onMenu: () -> Unit
) {
    val resultViewModel: ResultViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by resultViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(summary) {
        resultViewModel.bind(summary)
    }

    ResultContent(
        summary = summary,
        totalStars = state.totalStars,
        onPlayAgain = onPlayAgain,
        onNextExpedition = onNextExpedition,
        onMenu = onMenu
    )
}

@Composable
private fun ResultContent(
    summary: RunSummary,
    totalStars: Int,
    onPlayAgain: () -> Unit,
    onNextExpedition: () -> Unit,
    onMenu: () -> Unit
) {
    val victory = summary.outcome == GameOutcome.VICTORY
    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        appeared = true
    }
    val pop by animateFloatAsState(
        targetValue = if (appeared) 1f else 0.82f,
        animationSpec = tween(420, easing = FastOutSlowInEasing),
        label = "result-pop"
    )

    ScreenBackground(
        overlay = if (victory) {
            listOf(Color(0xF2041009), Color(0xCC0D3A22), Color(0xF2041009))
        } else {
            listOf(Color(0xF2140606), Color(0xCC2A0F0F), Color(0xF2041009))
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = stringResource(Labels.levelName(summary.levelIndex)),
                style = MaterialTheme.typography.labelSmall,
                color = Palette.TextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(
                    if (victory) R.string.result_victory else R.string.result_defeat
                ),
                style = MaterialTheme.typography.displaySmall,
                color = if (victory) Palette.Jade else Palette.Crimson,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .scale(pop)
                    .semantics { liveRegion = LiveRegionMode.Polite }
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                for (index in 0 until 3) {
                    Icon(
                        painter = painterResource(R.drawable.ic_star),
                        contentDescription = null,
                        tint = if (index < summary.stars) {
                            Palette.Gold
                        } else {
                            Palette.TextPrimary.copy(alpha = 0.16f)
                        },
                        modifier = Modifier.size(42.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(
                    when (summary.stars) {
                        0 -> R.string.star_cond_1
                        1 -> R.string.star_cond_2
                        2 -> R.string.star_cond_3
                        else -> R.string.star_cond_all
                    }
                ),
                style = MaterialTheme.typography.labelSmall,
                color = Palette.TextMuted,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(18.dp))

            StatGrid(summary = summary, totalStars = totalStars)

            if (summary.newRelics.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                RelicBanner(summary)
            }

            Spacer(modifier = Modifier.weight(1f))

            if (summary.nextUnlocked) {
                PrimaryButton(
                    text = stringResource(R.string.next_expedition),
                    onClick = onNextExpedition,
                    iconRes = R.drawable.ic_map,
                    modifier = Modifier.fillMaxWidth(),
                    height = 56
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            PrimaryButton(
                text = stringResource(R.string.play_again),
                onClick = onPlayAgain,
                iconRes = R.drawable.ic_play,
                modifier = Modifier.fillMaxWidth(),
                accentStart = if (summary.nextUnlocked) Palette.Gold else Palette.Jade,
                accentEnd = if (summary.nextUnlocked) Palette.Amber else Palette.JadeSoft,
                height = 56
            )

            Spacer(modifier = Modifier.height(10.dp))

            SecondaryButton(
                text = stringResource(R.string.menu),
                onClick = onMenu,
                iconRes = R.drawable.ic_map,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

@Composable
private fun StatGrid(summary: RunSummary, totalStars: Int) {
    val cards = buildList {
        add(
            Triple(
                stringResource(R.string.stat_turns_held),
                summary.turnsHeld,
                Palette.TextPrimary
            )
        )
        if (summary.wavesRepelled > 0) {
            add(
                Triple(
                    stringResource(R.string.stat_waves_repelled),
                    summary.wavesRepelled,
                    Palette.Amber
                )
            )
        }
        if (summary.jadeEarned > 0) {
            add(
                Triple(
                    stringResource(R.string.stat_jade_earned),
                    summary.jadeEarned,
                    Palette.Jade
                )
            )
        }
        if (summary.wardsLeft > 0) {
            add(
                Triple(
                    stringResource(R.string.stat_wards_left),
                    summary.wardsLeft,
                    Palette.JadeSoft
                )
            )
        }
        if (totalStars > 0) {
            add(Triple(stringResource(R.string.stat_stars), totalStars, Palette.Gold))
        }
    }
    if (cards.size < 2) return
    val rows = cards.chunked(2)
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        for (row in rows) {
            if (row.size < 2) continue
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for (card in row) {
                    StatCard(
                        label = card.first,
                        value = stringResource(R.string.count_fmt, card.second),
                        accent = card.third,
                        modifier = Modifier.weight(1f).height(62.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun RelicBanner(summary: RunSummary) {
    val relic = summary.newRelics.first()
    ClayTile(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        container = Palette.SurfaceRaised,
        accent = Palette.Gold,
        radius = 20.dp,
        elevation = 14.dp,
        borderWidth = 1.5.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(Labels.relicIcon(relic)),
                contentDescription = null,
                tint = Palette.Gold,
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = stringResource(R.string.new_relic),
                    style = MaterialTheme.typography.labelSmall,
                    color = Palette.Gold
                )
                Text(
                    text = stringResource(Labels.relicName(relic)),
                    style = MaterialTheme.typography.titleSmall,
                    color = Palette.TextPrimary
                )
                Text(
                    text = stringResource(Labels.relicPassive(relic)),
                    style = MaterialTheme.typography.labelSmall,
                    color = Palette.TextSecondary
                )
            }
        }
    }
}
