package com.kelsos.mbrc.core.ui.compose

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kelsos.mbrc.core.ui.theme.RemoteTheme
import kotlin.math.sin

/**
 * An animated wave/wiggly line indicator for streaming content.
 * Shows a continuous wave animation that moves from left to right.
 *
 * @param height Total height of the indicator. Defaults to 40.dp to match ThinSlider's default height.
 * @param waveAmplitude The amplitude of the wave oscillation.
 */
@Composable
fun WaveProgressIndicator(
  modifier: Modifier = Modifier,
  color: Color = MaterialTheme.colorScheme.onSurface,
  backgroundColor: Color = color.copy(alpha = 0.3f),
  height: Dp = 40.dp,
  waveAmplitude: Dp = 4.dp,
  strokeWidth: Dp = 2.dp,
  waveLength: Float = 60f,
  animationDurationMs: Int = 1500
) {
  val infiniteTransition = rememberInfiniteTransition(label = "wave")
  val phase by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = waveLength,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = animationDurationMs, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "wave_phase"
  )

  Canvas(
    modifier = modifier
      .fillMaxWidth()
      .height(height)
  ) {
    val width = size.width
    val height = size.height
    val centerY = height / 2
    val amplitude = waveAmplitude.toPx()
    val strokePx = strokeWidth.toPx()

    // Draw background line (straight)
    drawLine(
      color = backgroundColor,
      start = Offset(0f, centerY),
      end = Offset(width, centerY),
      strokeWidth = strokePx,
      cap = StrokeCap.Round
    )

    // Draw animated wave
    val path = Path()
    var isFirst = true

    var x = 0f
    while (x <= width) {
      val y = centerY + amplitude * sin((x + phase) * (2 * Math.PI / waveLength)).toFloat()
      if (isFirst) {
        path.moveTo(x, y)
        isFirst = false
      } else {
        path.lineTo(x, y)
      }
      x += 2f // Step size for smoothness
    }

    drawPath(
      path = path,
      color = color,
      style = Stroke(width = strokePx, cap = StrokeCap.Round)
    )
  }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
private fun WaveProgressIndicatorPreview() {
  RemoteTheme(darkTheme = false) {
    Surface(modifier = Modifier.padding(16.dp)) {
      WaveProgressIndicator(
        modifier = Modifier.fillMaxWidth()
      )
    }
  }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
private fun WaveProgressIndicatorDarkPreview() {
  RemoteTheme(darkTheme = true) {
    Surface(modifier = Modifier.padding(16.dp)) {
      WaveProgressIndicator(
        modifier = Modifier.fillMaxWidth()
      )
    }
  }
}
