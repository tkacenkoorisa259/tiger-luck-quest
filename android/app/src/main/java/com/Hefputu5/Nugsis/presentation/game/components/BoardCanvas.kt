package com.Hefputu5.Nugsis.presentation.game.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.Hefputu5.Nugsis.R
import com.Hefputu5.Nugsis.core.config.GameConfig
import com.Hefputu5.Nugsis.core.theme.Palette
import com.Hefputu5.Nugsis.core.ui.Labels
import com.Hefputu5.Nugsis.domain.model.BeastType
import com.Hefputu5.Nugsis.domain.model.BoardLayout
import com.Hefputu5.Nugsis.domain.model.CellKind
import com.Hefputu5.Nugsis.presentation.game.GameUiState
import kotlin.math.floor

private val BoardPadding = 8.dp
private val BoardBorder = 2.dp

@Composable
fun GameBoard(
    state: GameUiState,
    onTileTap: (Int) -> Unit,
    onGateTap: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val frame = BoardPadding + BoardBorder
        val widthCap = if (maxWidth - 24.dp < 360.dp) maxWidth - 24.dp else 360.dp
        val byWidth = floor((widthCap - frame * 2).value / GameConfig.COLS).dp
        val byHeight = floor((maxHeight - frame * 2).value / GameConfig.ROWS).dp
        val tile: Dp = if (byWidth < byHeight) byWidth else byHeight
        val boardWidth = tile * GameConfig.COLS + frame * 2
        val boardHeight = tile * GameConfig.ROWS + frame * 2

        Box(
            modifier = Modifier
                .size(width = boardWidth, height = boardHeight)
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Palette.SurfaceAlt, Palette.Night)
                    )
                )
                .border(BoardBorder, Palette.BorderJade, RoundedCornerShape(22.dp))
        ) {
            for (row in 0 until GameConfig.ROWS) {
                for (col in 0 until GameConfig.COLS) {
                    BoardCell(
                        state = state,
                        col = col,
                        row = row,
                        tile = tile,
                        frame = frame,
                        onTileTap = onTileTap,
                        onGateTap = onGateTap
                    )
                }
            }
            if (state.pathPreview) {
                for (beast in state.beasts) {
                    val target = state.previewSteps[beast.id] ?: continue
                    if (target >= GameConfig.ROWS) continue
                    val col = BoardLayout.trailColumn(beast.trail)
                    Box(
                        modifier = Modifier
                            .offset(x = frame + tile * col, y = frame + tile * target)
                            .size(tile)
                            .padding(4.dp)
                            .border(2.dp, Palette.Amber.copy(alpha = 0.55f), RoundedCornerShape(12.dp))
                    )
                }
            }
            for (guardian in state.guardians) {
                Box(
                    modifier = Modifier
                        .offset(x = frame + tile * guardian.col, y = frame + tile * guardian.row)
                        .size(tile),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(Labels.guardianSprite(guardian.type)),
                        contentDescription = stringResource(Labels.guardianName(guardian.type)),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(tile * 0.78f)
                    )
                }
            }
            for (beast in state.beasts) {
                val col = BoardLayout.trailColumn(beast.trail)
                Box(
                    modifier = Modifier
                        .offset(x = frame + tile * col, y = frame + tile * beast.row)
                        .size(tile),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(Labels.beastSprite(beast.type)),
                        contentDescription = stringResource(Labels.beastName(beast.type)),
                        contentScale = ContentScale.Fit,
                        colorFilter = if (beast.type == BeastType.SHADOW) {
                            ColorFilter.tint(Palette.Crimson.copy(alpha = 0.45f))
                        } else {
                            null
                        },
                        modifier = Modifier.size(if (beast.type == BeastType.SHADOW) tile else tile * 0.72f)
                    )
                    HealthPips(
                        hp = beast.hp,
                        maxHp = beast.maxHp,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 3.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BoardCell(
    state: GameUiState,
    col: Int,
    row: Int,
    tile: Dp,
    frame: Dp,
    onTileTap: (Int) -> Unit,
    onGateTap: (Int) -> Unit
) {
    val kind = BoardLayout.kindAt(col, row)
    val index = BoardLayout.cellIndex(col, row)
    val trail = BoardLayout.trailOfColumn(col)
    val isRubble = index in state.rubble
    val occupied = state.guardians.any { it.col == col && it.row == row }
    val armed = state.armed
    val trailOpen = trail != null && state.gatesOpen.getOrElse(trail) { true }

    val base = Modifier
        .offset(x = frame + tile * col, y = frame + tile * row)
        .size(tile)
        .padding(3.dp)

    when (kind) {
        CellKind.GATE -> {
            val gateIndex = trail ?: 0
            val open = state.gatesOpen.getOrElse(gateIndex) { true }
            val label = stringResource(
                if (open) R.string.cd_gate_open_fmt else R.string.cd_gate_sealed_fmt,
                gateIndex + 1
            )
            Box(
                modifier = base
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (open) Palette.SurfaceRaised else Palette.Surface)
                    .border(
                        2.dp,
                        if (open) Palette.Amber.copy(alpha = 0.75f) else Palette.BorderJade,
                        RoundedCornerShape(14.dp)
                    )
                    .semantics { stateDescription = label }
                    .clickable(enabled = state.interactive) { onGateTap(gateIndex) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(if (open) R.drawable.ic_lock_open else R.drawable.ic_lock),
                    contentDescription = label,
                    tint = if (open) Palette.Amber else Palette.TextMuted,
                    modifier = Modifier.size(tile * 0.4f)
                )
            }
        }

        CellKind.CANOPY -> Box(
            modifier = base
                .clip(RoundedCornerShape(14.dp))
                .background(Palette.Night.copy(alpha = 0.55f))
        )

        CellKind.WALL -> Box(
            modifier = base
                .clip(RoundedCornerShape(14.dp))
                .background(Palette.Surface.copy(alpha = 0.9f))
                .border(1.dp, Palette.BorderSoft, RoundedCornerShape(14.dp))
        )

        CellKind.TRAIL -> {
            val dim = if (trailOpen) 1f else 0.45f
            Box(
                modifier = base
                    .clip(RoundedCornerShape(14.dp))
                    .background(Palette.SurfaceAlt.copy(alpha = dim))
                    .border(1.dp, Palette.Night.copy(alpha = 0.6f * dim), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (row == BoardLayout.shrineRow) {
                    Box(
                        modifier = Modifier
                            .size(tile * 0.18f)
                            .clip(CircleShape)
                            .background(
                                Palette.Jade.copy(
                                    alpha = if (trail == state.bonusTrail) 0.75f else 0.35f
                                )
                            )
                    )
                }
            }
        }

        CellKind.TEMPLE -> Box(
            modifier = base
                .clip(RoundedCornerShape(14.dp))
                .background(Palette.SurfaceRaised)
                .border(2.dp, Palette.Amber.copy(alpha = 0.6f), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = stringResource(R.string.cd_temple),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(tile * 0.78f)
            )
        }

        CellKind.PLATFORM -> {
            val legal = armed != null && !isRubble && !occupied && state.interactive
            val label = when {
                isRubble -> stringResource(R.string.cd_rubble)
                else -> stringResource(R.string.cd_platform_free)
            }
            Box(
                modifier = base
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isRubble) Palette.Night.copy(alpha = 0.72f) else Palette.SurfaceRaised)
                    .border(
                        if (legal) 2.dp else 1.dp,
                        when {
                            legal -> Palette.Jade
                            isRubble -> Palette.BorderSoft
                            else -> Palette.BorderJade
                        },
                        RoundedCornerShape(14.dp)
                    )
                    .semantics { stateDescription = label }
                    .clickable(enabled = legal) { onTileTap(index) },
                contentAlignment = Alignment.Center
            ) {
                if (isRubble) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = label,
                        tint = Palette.TextMuted.copy(alpha = 0.7f),
                        modifier = Modifier.size(tile * 0.3f)
                    )
                }
            }
        }
    }
}

@Composable
private fun HealthPips(hp: Int, maxHp: Int, modifier: Modifier = Modifier) {
    val shown = if (maxHp > 7) 7 else maxHp
    val filled = if (maxHp > 7) {
        (hp * 7 + maxHp - 1) / maxHp
    } else {
        hp
    }
    Row(modifier = modifier) {
        for (index in 0 until shown) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(
                        if (index < filled) Palette.Crimson else Palette.Crimson.copy(alpha = 0.22f)
                    )
            )
            if (index < shown - 1) Spacer(modifier = Modifier.width(2.dp))
        }
    }
}

@Composable
fun TurnTimerPips(total: Int, left: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier.height(6.dp), verticalAlignment = Alignment.CenterVertically) {
        for (index in 0 until total) {
            Box(
                modifier = Modifier
                    .size(width = 10.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        if (index < left) Palette.Jade else Color.White.copy(alpha = 0.12f)
                    )
            )
            if (index < total - 1) Spacer(modifier = Modifier.width(3.dp))
        }
    }
}
