package com.kelsos.mbrc.core.ui.compose

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private const val MIN_HEIGHT_FRACTION = 0.1f
private const val MAX_HEIGHT_FRACTION = 1f

/**
 * Animated audio bars indicator similar to Spotify's now playing indicator.
 * Shows 3 vertical bars that animate up and down at different speeds.
 *
 * @param modifier Modifier for the component
 * @param color Color of the bars
 * @param barWidth Width of each bar
 * @param barMaxHeight Maximum height of the bars
 * @param barSpacing Spacing between bars
 */
@Composable
fun AudioBarsIndicator(
  modifier: Modifier = Modifier,
  color: Color = MaterialTheme.colorScheme.primary,
  barWidth: Dp = 3.dp,
  barMaxHeight: Dp = 16.dp,
  barSpacing: Dp = 2.dp
) {
  val infiniteTransition = rememberInfiniteTransition(label = "audio_bars")

  // Each bar animates at a different speed and phase for natural look
  val bar1Height by infiniteTransition.animateFloat(
    initialValue = 0.3f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 400, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "bar1"
  )

  val bar2Height by infiniteTransition.animateFloat(
    initialValue = 0.5f,
    targetValue = 0.2f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 300, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "bar2"
  )

  val bar3Height by infiniteTransition.animateFloat(
    initialValue = 0.2f,
    targetValue = 0.8f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 500, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "bar3"
  )

  Row(
    modifier = modifier.height(barMaxHeight),
    horizontalArrangement = Arrangement.spacedBy(barSpacing),
    verticalAlignment = Alignment.Bottom
  ) {
    AudioBar(
      heightFraction = bar1Height,
      maxHeight = barMaxHeight,
      width = barWidth,
      color = color
    )
    AudioBar(
      heightFraction = bar2Height,
      maxHeight = barMaxHeight,
      width = barWidth,
      color = color
    )
    AudioBar(
      heightFraction = bar3Height,
      maxHeight = barMaxHeight,
      width = barWidth,
      color = color
    )
  }
}

@Composable
private fun AudioBar(heightFraction: Float, maxHeight: Dp, width: Dp, color: Color) {
  Box(
    modifier = Modifier
      .width(width)
      .height(maxHeight * heightFraction.coerceIn(MIN_HEIGHT_FRACTION, MAX_HEIGHT_FRACTION))
      .clip(RoundedCornerShape(topStart = 1.dp, topEnd = 1.dp))
      .background(color)
  )
}
