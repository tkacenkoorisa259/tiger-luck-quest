package com.Hefputu5.Nugsis.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.Hefputu5.Nugsis.core.theme.Palette

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconRes: Int? = null,
    enabled: Boolean = true,
    accent: Color = Palette.Jade
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(18.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.5.dp,
            color = if (enabled) accent.copy(alpha = 0.75f) else Palette.BorderSoft
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Palette.Surface.copy(alpha = 0.55f),
            contentColor = if (enabled) Palette.TextPrimary else Palette.TextMuted,
            disabledContainerColor = Palette.Surface.copy(alpha = 0.35f),
            disabledContentColor = Palette.TextMuted
        ),
        modifier = modifier
            .height(48.dp)
            .semantics { contentDescription = text }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (iconRes != null) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = if (enabled) accent else Palette.TextMuted
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(text = text, style = MaterialTheme.typography.titleSmall)
        }
    }
}
