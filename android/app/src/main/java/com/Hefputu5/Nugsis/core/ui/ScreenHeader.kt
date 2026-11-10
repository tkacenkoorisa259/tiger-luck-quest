package com.Hefputu5.Nugsis.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.Hefputu5.Nugsis.R
import com.Hefputu5.Nugsis.core.theme.Palette

@Composable
fun ScreenHeader(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    trailing: @Composable RowScope.() -> Unit = {}
) {
    val backLabel = stringResource(R.string.cd_back)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(Palette.Night.copy(alpha = 0.45f))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ClayTile(
            modifier = Modifier.size(48.dp),
            container = Palette.SurfaceRaised,
            accent = Palette.Jade,
            radius = 24.dp,
            elevation = 10.dp,
            label = backLabel,
            onClick = onBack
        ) {
            Image(
                painter = painterResource(R.drawable.icon_home),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(26.dp)
                    .align(Alignment.Center)
            )
        }
        Box(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = Palette.TextPrimary,
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            content = trailing
        )
    }
}

@Composable
fun BadgePill(text: String, modifier: Modifier = Modifier) {
    ClayTile(
        modifier = modifier.height(36.dp),
        container = Palette.SurfaceRaised,
        accent = Palette.Gold,
        radius = 18.dp,
        elevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.ic_star),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(14.dp),
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Palette.Gold)
            )
            Box(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = Palette.TextPrimary
            )
        }
    }
}
