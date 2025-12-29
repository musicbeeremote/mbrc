package com.kelsos.mbrc.common.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kelsos.mbrc.theme.RemoteTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThinSlider(
  value: Float,
  onValueChange: (Float) -> Unit,
  modifier: Modifier = Modifier,
  onValueChangeFinished: (() -> Unit)? = null,
  trackColor: Color = MaterialTheme.colorScheme.primary,
  inactiveTrackColor: Color = trackColor.copy(alpha = 0.3f),
  thumbColor: Color = trackColor,
  thumbSize: Dp = 12.dp,
  trackHeight: Dp = 4.dp
) {
  val sliderColors = SliderDefaults.colors(
    thumbColor = thumbColor,
    activeTrackColor = trackColor,
    inactiveTrackColor = inactiveTrackColor
  )
  val height = maxOf(thumbSize, trackHeight) * 2 + 16.dp
  Slider(
    value = value,
    onValueChange = onValueChange,
    onValueChangeFinished = onValueChangeFinished,
    modifier = modifier.height(height),
    colors = sliderColors,
    thumb = {
      Box(
        modifier = Modifier.height(height),
        contentAlignment = Alignment.Center
      ) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(32.dp))
            .size(thumbSize)
            .background(thumbColor)
        )
      }
    },
    track = { sliderState ->
      val fraction = sliderState.valueRange.let {
        (sliderState.value - it.start) / (it.endInclusive - it.start)
      }
      Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(trackHeight)
            .clip(CircleShape)
            .background(inactiveTrackColor)
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth(fraction)
              .height(trackHeight)
              .clip(CircleShape)
              .background(trackColor)
          )
        }
      }
    }
  )
}

/**
 * Interactive preview for manually testing ThinSlider alignment with different sizes.
 * Adjust thumbSize and trackHeight values to test different combinations.
 */
@Preview(showBackground = true, widthDp = 400, heightDp = 400)
@Composable
fun ThinSliderManualTestPreview() {
  RemoteTheme(darkTheme = false) {
    Surface(modifier = Modifier.padding(16.dp)) {
      Column {
        Text("Default (12dp thumb, 4dp track)", style = MaterialTheme.typography.labelSmall)
        ThinSlider(
          value = 0.5f,
          onValueChange = {},
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("Large thumb (20dp thumb, 4dp track)", style = MaterialTheme.typography.labelSmall)
        ThinSlider(
          value = 0.5f,
          onValueChange = {},
          thumbSize = 20.dp,
          trackHeight = 4.dp,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("Small thumb (8dp thumb, 4dp track)", style = MaterialTheme.typography.labelSmall)
        ThinSlider(
          value = 0.5f,
          onValueChange = {},
          thumbSize = 8.dp,
          trackHeight = 4.dp,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("Thick track (12dp thumb, 8dp track)", style = MaterialTheme.typography.labelSmall)
        ThinSlider(
          value = 0.5f,
          onValueChange = {},
          thumbSize = 12.dp,
          trackHeight = 8.dp,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
          "Thin track, large thumb (16dp thumb, 2dp track)",
          style = MaterialTheme.typography.labelSmall
        )
        ThinSlider(
          value = 0.5f,
          onValueChange = {},
          thumbSize = 16.dp,
          trackHeight = 2.dp,
          modifier = Modifier.fillMaxWidth()
        )
      }
    }
  }
}
