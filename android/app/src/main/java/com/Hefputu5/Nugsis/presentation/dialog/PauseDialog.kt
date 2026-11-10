package com.Hefputu5.Nugsis.presentation.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.Hefputu5.Nugsis.R
import com.Hefputu5.Nugsis.core.theme.Palette
import com.Hefputu5.Nugsis.core.ui.DialogSurface
import com.Hefputu5.Nugsis.core.ui.PrimaryButton
import com.Hefputu5.Nugsis.core.ui.SecondaryButton

@Composable
fun PauseDialog(
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onMenu: () -> Unit
) {
    DialogSurface(
        title = stringResource(R.string.pause_title),
        onDismiss = onResume,
        accent = Palette.Amber
    ) {
        Text(
            text = stringResource(R.string.pause_body),
            style = MaterialTheme.typography.bodyLarge,
            color = Palette.TextSecondary,
            textAlign = TextAlign.Center
        )
        PrimaryButton(
            text = stringResource(R.string.resume),
            onClick = onResume,
            iconRes = R.drawable.ic_play,
            modifier = Modifier.fillMaxWidth(),
            height = 52
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SecondaryButton(
                text = stringResource(R.string.restart),
                onClick = onRestart,
                modifier = Modifier.weight(1f),
                accent = Palette.Amber
            )
            SecondaryButton(
                text = stringResource(R.string.menu),
                onClick = onMenu,
                iconRes = R.drawable.ic_map,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
