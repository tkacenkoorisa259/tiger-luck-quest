package com.Hefputu5.Nugsis.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.Hefputu5.Nugsis.core.theme.Palette

@Composable
fun DialogSurface(
    title: String,
    onDismiss: () -> Unit,
    accent: Color = Palette.Jade,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        ClayTile(
            modifier = Modifier.fillMaxWidth(),
            container = Palette.SurfaceRaised,
            accent = accent,
            radius = 26.dp,
            elevation = 24.dp,
            borderWidth = 1.5.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = accent
                )
                Spacer(modifier = Modifier.height(2.dp))
                content()
            }
        }
    }
}
