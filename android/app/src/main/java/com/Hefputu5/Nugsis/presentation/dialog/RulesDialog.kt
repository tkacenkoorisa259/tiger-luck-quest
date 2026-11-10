package com.Hefputu5.Nugsis.presentation.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
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
import com.Hefputu5.Nugsis.core.ui.DialogSurface
import com.Hefputu5.Nugsis.core.ui.Labels
import com.Hefputu5.Nugsis.core.ui.PrimaryButton
import com.Hefputu5.Nugsis.domain.model.BeastType
import com.Hefputu5.Nugsis.domain.model.GuardianType

@Composable
fun RulesDialog(onDismiss: () -> Unit) {
    DialogSurface(
        title = stringResource(R.string.rules_title),
        onDismiss = onDismiss,
        accent = Palette.JadeSoft
    ) {
        RuleSection(
            head = stringResource(R.string.rules_trails_head),
            body = stringResource(R.string.rules_trails_body)
        )
        RuleSection(
            head = stringResource(R.string.rules_guardians_head),
            body = stringResource(R.string.rules_guardians_body)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (type in GuardianType.entries) {
                UnitBadge(
                    spriteRes = Labels.guardianSprite(type),
                    nameRes = Labels.guardianName(type),
                    accentJade = true
                )
            }
        }
        RuleSection(
            head = stringResource(R.string.rules_beasts_head),
            body = stringResource(R.string.rules_beasts_body)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            UnitBadge(
                spriteRes = Labels.beastSprite(BeastType.PROWLER),
                nameRes = Labels.beastName(BeastType.PROWLER),
                accentJade = false
            )
            UnitBadge(
                spriteRes = Labels.beastSprite(BeastType.APE),
                nameRes = Labels.beastName(BeastType.APE),
                accentJade = false
            )
        }
        RuleSection(
            head = stringResource(R.string.rules_roar_head),
            body = stringResource(R.string.rules_roar_body)
        )
        PrimaryButton(
            text = stringResource(R.string.got_it),
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
            height = 52
        )
    }
}

@Composable
private fun UnitBadge(spriteRes: Int, nameRes: Int, accentJade: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(spriteRes),
            contentDescription = stringResource(nameRes),
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(40.dp)
        )
        Text(
            text = stringResource(nameRes),
            style = MaterialTheme.typography.labelSmall,
            color = if (accentJade) Palette.Jade else Palette.Crimson,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RuleSection(head: String, body: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = head,
            style = MaterialTheme.typography.labelSmall,
            color = Palette.Jade
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = Palette.TextSecondary,
            textAlign = TextAlign.Start
        )
    }
}
