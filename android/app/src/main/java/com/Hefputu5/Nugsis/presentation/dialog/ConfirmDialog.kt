package com.Hefputu5.Nugsis.presentation.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.Hefputu5.Nugsis.R
import com.Hefputu5.Nugsis.core.theme.Palette
import com.Hefputu5.Nugsis.core.ui.DialogSurface
import com.Hefputu5.Nugsis.core.ui.PrimaryButton
import com.Hefputu5.Nugsis.core.ui.SecondaryButton

@Composable
fun ConfirmDialog(
    title: String,
    body: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    accent: Color = Palette.Jade
) {
    DialogSurface(title = title, onDismiss = onDismiss, accent = accent) {
        Text(
            text = body,
            style = MaterialTheme.typography.bodyLarge,
            color = Palette.TextSecondary,
            textAlign = TextAlign.Center
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SecondaryButton(
                text = stringResource(R.string.back),
                onClick = onDismiss,
                modifier = Modifier.weight(1f)
            )
            PrimaryButton(
                text = confirmText,
                onClick = onConfirm,
                modifier = Modifier.weight(1f),
                accentStart = accent,
                accentEnd = accent,
                height = 48
            )
        }
    }
}
