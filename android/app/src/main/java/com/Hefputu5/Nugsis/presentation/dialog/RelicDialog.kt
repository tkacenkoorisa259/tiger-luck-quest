package com.Hefputu5.Nugsis.presentation.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.Hefputu5.Nugsis.R
import com.Hefputu5.Nugsis.core.theme.Palette
import com.Hefputu5.Nugsis.core.ui.DialogSurface
import com.Hefputu5.Nugsis.core.ui.Labels
import com.Hefputu5.Nugsis.core.ui.PrimaryButton
import com.Hefputu5.Nugsis.core.ui.SecondaryButton
import com.Hefputu5.Nugsis.domain.model.RelicProgress

@Composable
fun RelicDialog(
    relic: RelicProgress,
    onToggleEquip: () -> Unit,
    onClose: () -> Unit
) {
    DialogSurface(
        title = stringResource(Labels.relicName(relic.id)),
        onDismiss = onClose,
        accent = if (relic.unlocked) Palette.Gold else Palette.TextMuted
    ) {
        Icon(
            painter = painterResource(Labels.relicIcon(relic.id)),
            contentDescription = null,
            tint = if (relic.unlocked) Palette.Gold else Palette.TextMuted,
            modifier = Modifier.size(52.dp)
        )
        Text(
            text = stringResource(Labels.relicPassive(relic.id)),
            style = MaterialTheme.typography.bodyLarge,
            color = Palette.TextPrimary,
            textAlign = TextAlign.Center
        )
        if (!relic.unlocked) {
            Text(
                text = stringResource(Labels.relicUnlock(relic.id)),
                style = MaterialTheme.typography.labelSmall,
                color = Palette.TextSecondary,
                textAlign = TextAlign.Center
            )
            LinearProgressIndicator(
                progress = { relic.current.toFloat() / relic.target.toFloat() },
                modifier = Modifier.fillMaxWidth(),
                color = Palette.Jade,
                trackColor = Palette.SurfaceAlt
            )
            Text(
                text = stringResource(R.string.ratio_fmt, relic.current, relic.target),
                style = MaterialTheme.typography.labelSmall,
                color = Palette.TextMuted
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SecondaryButton(
                text = stringResource(R.string.close),
                onClick = onClose,
                modifier = Modifier.weight(1f)
            )
            if (relic.unlocked) {
                PrimaryButton(
                    text = stringResource(
                        if (relic.equipped) R.string.unequip else R.string.equip
                    ),
                    onClick = onToggleEquip,
                    modifier = Modifier.weight(1f),
                    accentStart = Palette.Gold,
                    accentEnd = Palette.Amber,
                    height = 48
                )
            }
        }
    }
}
