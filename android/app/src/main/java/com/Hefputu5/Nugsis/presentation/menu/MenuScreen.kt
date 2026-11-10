package com.Hefputu5.Nugsis.presentation.menu

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.Hefputu5.Nugsis.core.config.GameConfig
import com.Hefputu5.Nugsis.core.di.AppViewModelFactory
import com.Hefputu5.Nugsis.core.theme.Palette
import com.Hefputu5.Nugsis.core.ui.BadgePill
import com.Hefputu5.Nugsis.core.ui.ClayTile
import com.Hefputu5.Nugsis.core.ui.Labels
import com.Hefputu5.Nugsis.core.ui.PrimaryButton
import com.Hefputu5.Nugsis.core.ui.ScreenBackground
import com.Hefputu5.Nugsis.domain.model.GuardianType
import com.Hefputu5.Nugsis.presentation.dialog.RulesDialog

@Composable
fun MenuScreen(
    onPlay: (Int) -> Unit,
    onLevels: () -> Unit,
    onCollection: () -> Unit,
    onProgress: () -> Unit,
    onSettings: () -> Unit
) {
    val menuViewModel: MenuViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by menuViewModel.state.collectAsStateWithLifecycle()
    var showRules by remember { mutableStateOf(false) }

    LaunchedEffect(menuViewModel) {
        menuViewModel.refresh()
    }

    MenuContent(
        state = state,
        onPlay = onPlay,
        onLevels = onLevels,
        onCollection = onCollection,
        onProgress = onProgress,
        onSettings = onSettings,
        onRules = { showRules = true }
    )

    if (showRules) {
        RulesDialog(onDismiss = { showRules = false })
    }
}

@Composable
private fun MenuContent(
    state: MenuUiState,
    onPlay: (Int) -> Unit,
    onLevels: () -> Unit,
    onCollection: () -> Unit,
    onProgress: () -> Unit,
    onSettings: () -> Unit,
    onRules: () -> Unit
) {
    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        appeared = true
    }
    val reveal by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(420, easing = FastOutSlowInEasing),
        label = "menu-reveal"
    )

    ScreenBackground(
        overlay = listOf(
            Color(0xCC0D2818),
            Color(0x990F3024),
            Color(0xE6041009)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp)
                .alpha(0.3f + reveal * 0.7f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = stringResource(R.string.cd_emblem),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.brand_title),
                    style = MaterialTheme.typography.titleSmall,
                    color = Palette.TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                BadgePill(
                    text = stringResource(
                        R.string.ratio_fmt,
                        state.totalStars,
                        GameConfig.MAX_STARS
                    ),
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth().weight(1.1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BentoTile(
                    iconRes = R.drawable.ic_map,
                    accent = Palette.Jade,
                    label = stringResource(R.string.levels),
                    caption = stringResource(R.string.cleared_fmt, state.clearedLevels),
                    onClick = onLevels,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
                BentoTile(
                    iconRes = R.drawable.ic_star,
                    accent = Palette.Gold,
                    label = stringResource(R.string.collection),
                    caption = stringResource(R.string.relics_fmt, state.unlockedRelics),
                    onClick = onCollection,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth().weight(1.1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BentoTile(
                    iconRes = R.drawable.ic_chart,
                    accent = Palette.Amber,
                    label = stringResource(R.string.progress),
                    caption = stringResource(R.string.waves_fmt, state.wavesRepelled),
                    onClick = onProgress,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
                BentoTile(
                    iconRes = R.drawable.ic_gear,
                    accent = Palette.TextSecondary,
                    label = stringResource(R.string.settings),
                    caption = stringResource(R.string.menu_settings_caption),
                    onClick = onSettings,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            BentoTile(
                iconRes = R.drawable.ic_info,
                accent = Palette.JadeSoft,
                label = stringResource(R.string.rules),
                caption = stringResource(R.string.menu_rules_caption),
                onClick = onRules,
                modifier = Modifier.fillMaxWidth().height(70.dp),
                horizontal = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            HeroStrip(level = state.highestUnlocked, modifier = Modifier.fillMaxWidth().weight(1f))

            Spacer(modifier = Modifier.height(12.dp))

            PrimaryButton(
                text = stringResource(R.string.play),
                onClick = { onPlay(state.highestUnlocked) },
                iconRes = R.drawable.ic_play,
                modifier = Modifier.fillMaxWidth(),
                height = 60
            )

            Spacer(modifier = Modifier.height(12.dp))

            RelicStrip(state = state, onClick = onCollection)

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HeroStrip(level: Int, modifier: Modifier = Modifier) {
    ClayTile(
        modifier = modifier,
        container = Palette.SurfaceRaised,
        accent = Palette.Jade,
        radius = 26.dp,
        elevation = 16.dp,
        borderWidth = 1.5.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.sprite_1),
                contentDescription = stringResource(Labels.guardianName(GuardianType.SENTRY)),
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxHeight().width(86.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    text = stringResource(R.string.expedition_fmt, level),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Palette.TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.brand_subtitle),
                    style = MaterialTheme.typography.labelSmall,
                    color = Palette.TextSecondary
                )
            }
        }
    }
}

@Composable
private fun BentoTile(
    iconRes: Int,
    accent: Color,
    label: String,
    caption: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    horizontal: Boolean = false
) {
    ClayTile(
        modifier = modifier,
        container = Palette.SurfaceRaised,
        accent = accent,
        radius = 22.dp,
        elevation = 12.dp,
        label = label,
        onClick = onClick
    ) {
        if (horizontal) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleSmall,
                        color = Palette.TextPrimary
                    )
                    Text(
                        text = caption,
                        style = MaterialTheme.typography.labelSmall,
                        color = Palette.TextMuted
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleSmall,
                    color = Palette.TextPrimary
                )
                Text(
                    text = caption,
                    style = MaterialTheme.typography.labelSmall,
                    color = Palette.TextMuted
                )
            }
        }
    }
}

@Composable
private fun RelicStrip(state: MenuUiState, onClick: () -> Unit) {
    val equipped = state.equipped
    ClayTile(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp),
        container = Palette.SurfaceAlt,
        accent = if (equipped != null) Palette.Gold else Palette.TextMuted,
        radius = 18.dp,
        elevation = 10.dp,
        label = stringResource(R.string.collection),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (equipped != null) {
                Icon(
                    painter = painterResource(Labels.relicIcon(equipped)),
                    contentDescription = null,
                    tint = Palette.Gold,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(
                        R.string.relic_strip_fmt,
                        stringResource(Labels.relicName(equipped)),
                        stringResource(Labels.relicPassive(equipped))
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = Palette.TextPrimary
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.menu_relic_empty),
                        style = MaterialTheme.typography.labelSmall,
                        color = Palette.TextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
