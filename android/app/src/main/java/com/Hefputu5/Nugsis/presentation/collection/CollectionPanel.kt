package com.Hefputu5.Nugsis.presentation.collection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
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
import com.Hefputu5.Nugsis.domain.model.RelicProgress
import com.Hefputu5.Nugsis.presentation.dialog.RelicDialog

@Composable
fun CollectionPanel(onBack: () -> Unit) {
    val collectionViewModel: CollectionViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by collectionViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(collectionViewModel) {
        collectionViewModel.refresh()
    }

    ScreenBackground(
        overlay = listOf(
            Color(0xE60D2818),
            Color(0xB31A3A28),
            Color(0xF2041009)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            ScreenHeader(
                title = stringResource(R.string.title_relics),
                onBack = onBack
            ) {
                BadgePill(
                    text = stringResource(
                        R.string.ratio_fmt,
                        state.unlockedCount,
                        GameConfig.RELIC_COUNT
                    )
                )
            }

            if (state.unlockedCount == 0) {
                Text(
                    text = stringResource(R.string.collection_empty),
                    style = MaterialTheme.typography.labelSmall,
                    color = Palette.TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 14.dp)
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.relics, key = { it.id.name }) { relic ->
                    RelicTile(
                        relic = relic,
                        onClick = { collectionViewModel.select(relic.id) }
                    )
                }
            }
        }
    }

    val opened = state.relics.firstOrNull { it.id == state.selected }
    if (opened != null) {
        RelicDialog(
            relic = opened,
            onToggleEquip = { collectionViewModel.toggleEquip(opened.id) },
            onClose = { collectionViewModel.select(null) }
        )
    }
}

@Composable
private fun RelicTile(relic: RelicProgress, onClick: () -> Unit) {
    ClayTile(
        modifier = Modifier
            .fillMaxWidth()
            .height(112.dp),
        container = if (relic.unlocked) Palette.SurfaceRaised else Palette.Surface,
        accent = when {
            relic.equipped -> Palette.Jade
            relic.unlocked -> Palette.Gold
            else -> Palette.TextMuted
        },
        radius = 20.dp,
        elevation = if (relic.unlocked) 12.dp else 4.dp,
        borderWidth = if (relic.equipped) 2.dp else 1.dp,
        label = stringResource(Labels.relicName(relic.id)),
        state = stringResource(
            when {
                relic.equipped -> R.string.cd_equipped
                relic.unlocked -> R.string.cd_unlocked
                else -> R.string.cd_locked
            }
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(
                    if (relic.unlocked) Labels.relicIcon(relic.id) else R.drawable.ic_lock
                ),
                contentDescription = null,
                tint = if (relic.unlocked) Palette.Gold else Palette.TextMuted,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(Labels.relicName(relic.id)),
                style = MaterialTheme.typography.labelSmall,
                color = if (relic.unlocked) Palette.TextPrimary else Palette.TextMuted,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            if (relic.equipped) {
                Spacer(modifier = Modifier.height(5.dp))
                Box(
                    modifier = Modifier
                        .size(width = 28.dp, height = 3.dp)
                        .background(Palette.Jade)
                )
            }
        }
    }
}
