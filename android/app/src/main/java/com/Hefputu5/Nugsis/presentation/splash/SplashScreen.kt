package com.Hefputu5.Nugsis.presentation.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Hefputu5.Nugsis.R
import com.Hefputu5.Nugsis.core.di.AppViewModelFactory
import com.Hefputu5.Nugsis.core.theme.Palette
import com.Hefputu5.Nugsis.core.ui.ScreenBackground
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val splashViewModel: SplashViewModel = viewModel(factory = AppViewModelFactory.factory)
    val finished by splashViewModel.finished.collectAsStateWithLifecycle()

    LaunchedEffect(finished) {
        if (finished) onFinished()
    }

    SplashContent()
}

@Composable
private fun SplashContent() {
    val transition = rememberInfiniteTransition(label = "splash")
    val ringAngle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing)),
        label = "ring"
    )
    val glow by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.78f,
        animationSpec = infiniteRepeatable(
            tween(1800, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "glow"
    )
    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(5200, easing = LinearEasing)),
        label = "drift"
    )

    ScreenBackground(
        overlay = listOf(
            Color(0xF5041009),
            Color(0xE00D2818),
            Color(0xF8041009)
        )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            for (index in 0 until 16) {
                val column = (index * 37 % 100) / 100f
                val phase = (drift + index / 16f) % 1f
                val x = width * (0.08f + column * 0.84f)
                val y = height * (0.98f - phase * 0.55f)
                drawCircle(
                    color = Palette.Amber.copy(alpha = 0.10f + (1f - phase) * 0.22f),
                    radius = 2.dp.toPx() + (index % 3).dp.toPx() * 0.6f,
                    center = Offset(x, y)
                )
            }
            val centre = Offset(width / 2f, height * 0.37f)
            for (ring in 0 until 3) {
                drawCircle(
                    color = Palette.Jade.copy(alpha = 0.08f + ring * 0.06f),
                    radius = (118 + ring * 44).dp.toPx(),
                    center = centre,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
            val sweepRadius = 206.dp.toPx()
            for (tick in 0 until 24) {
                val angle = Math.toRadians((ringAngle + tick * 15f).toDouble())
                drawCircle(
                    color = Palette.Jade.copy(alpha = if (tick % 6 == 0) 0.45f else 0.14f),
                    radius = 2.5.dp.toPx(),
                    center = Offset(
                        centre.x + sweepRadius * cos(angle).toFloat(),
                        centre.y + sweepRadius * sin(angle).toFloat()
                    )
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(216.dp)
                        .rotate(ringAngle * 0.25f)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    Palette.Jade.copy(alpha = glow * 0.22f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .size(176.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                listOf(Palette.SurfaceRaised, Palette.SurfaceAlt)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = stringResource(R.string.cd_emblem),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(124.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = stringResource(R.string.brand_title),
                style = MaterialTheme.typography.displayMedium,
                color = Palette.TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(R.string.brand_subtitle),
                style = MaterialTheme.typography.labelSmall,
                color = Palette.TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(44.dp))

            LinearProgressIndicator(
                modifier = Modifier
                    .width(184.dp)
                    .height(5.dp),
                color = Palette.Jade,
                trackColor = Palette.SurfaceAlt,
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = stringResource(R.string.loading),
                style = MaterialTheme.typography.labelSmall,
                color = Palette.TextMuted
            )
        }
    }
}
