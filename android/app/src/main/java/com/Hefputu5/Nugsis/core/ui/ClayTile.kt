package com.Hefputu5.Nugsis.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.Hefputu5.Nugsis.core.theme.Palette

@Composable
fun ClayTile(
    modifier: Modifier = Modifier,
    container: Color = Palette.SurfaceRaised,
    accent: Color = Palette.Jade,
    radius: Dp = 20.dp,
    elevation: Dp = 12.dp,
    borderWidth: Dp = 1.dp,
    label: String? = null,
    state: String? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(radius)
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.965f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "clay-press"
    )
    var base = modifier
        .scale(pressScale)
        .shadow(
            elevation = elevation,
            shape = shape,
            ambientColor = Palette.Night,
            spotColor = Palette.Night
        )
        .clip(shape)
        .background(container)
        .border(borderWidth, accent.copy(alpha = 0.30f), shape)
    if (onClick != null) {
        base = base.clickable(interactionSource = interaction, indication = null, onClick = onClick)
    }
    if (label != null || state != null) {
        base = base.semantics {
            if (label != null) contentDescription = label
            if (state != null) stateDescription = state
        }
    }
    Box(modifier = base) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Palette.HighlightTop, Color.Transparent),
                        startY = 0f,
                        endY = 160f
                    )
                )
        )
        content()
    }
}
