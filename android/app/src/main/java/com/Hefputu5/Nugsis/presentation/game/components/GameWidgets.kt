package com.Hefputu5.Nugsis.presentation.game.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.Hefputu5.Nugsis.R
import com.Hefputu5.Nugsis.core.theme.Palette
import com.Hefputu5.Nugsis.core.ui.ClayTile
import com.Hefputu5.Nugsis.core.ui.Labels
import com.Hefputu5.Nugsis.domain.model.GuardianType

@Composable
fun GuardianChip(
    type: GuardianType,
    cost: Int,
    affordable: Boolean,
    armed: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = when {
        armed -> Palette.Amber
        affordable -> Palette.Jade
        else -> Palette.TextMuted
    }
    val stateText = if (affordable) {
        stringResource(R.string.cd_jade_fmt, cost)
    } else {
        stringResource(R.string.cd_not_enough_jade)
    }
    ClayTile(
        modifier = modifier.height(84.dp),
        container = if (affordable) Palette.SurfaceRaised else Palette.Surface,
        accent = accent,
        radius = 18.dp,
        elevation = if (armed) 14.dp else 8.dp,
        borderWidth = if (armed) 2.dp else 1.dp,
        label = stringResource(Labels.guardianName(type)),
        state = stateText,
        onClick = if (affordable) onClick else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(Labels.guardianSprite(type)),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(34.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(Labels.guardianName(type)),
                style = MaterialTheme.typography.labelSmall,
                color = if (affordable) Palette.TextPrimary else Palette.TextMuted,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_jade),
                    contentDescription = null,
                    tint = if (affordable) Palette.Jade else Palette.Crimson,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = stringResource(R.string.count_fmt, cost),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (affordable) Palette.Jade else Palette.Crimson
                )
            }
        }
    }
}

@Composable
fun WardRow(total: Int, left: Int, modifier: Modifier = Modifier) {
    val description = stringResource(R.string.cd_wards_fmt, left)
    Row(
        modifier = modifier.semantics {
            stateDescription = description
            liveRegion = LiveRegionMode.Polite
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (index in 0 until total) {
            Icon(
                painter = painterResource(
                    if (index < left) R.drawable.ic_shield else R.drawable.ic_shield_broken
                ),
                contentDescription = null,
                tint = if (index < left) Palette.Jade else Palette.Crimson.copy(alpha = 0.45f),
                modifier = Modifier.size(20.dp)
            )
            if (index < total - 1) Spacer(modifier = Modifier.width(4.dp))
        }
    }
}
