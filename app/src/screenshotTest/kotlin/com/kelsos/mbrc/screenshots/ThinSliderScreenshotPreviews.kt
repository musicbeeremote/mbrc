package com.kelsos.mbrc.screenshots

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import com.kelsos.mbrc.common.ui.compose.ThinSlider
import com.kelsos.mbrc.theme.RemoteTheme

@PreviewTest
@Preview(showBackground = true)
@Composable
fun ThinSliderLight() {
  RemoteTheme(darkTheme = false) {
    Surface(modifier = Modifier.fillMaxWidth()) {
      ThinSlider(
        value = 0.5f,
        onValueChange = {},
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun ThinSliderDark() {
  RemoteTheme(darkTheme = true) {
    Surface(modifier = Modifier.fillMaxWidth()) {
      ThinSlider(
        value = 0.5f,
        onValueChange = {},
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun ThinSliderAtStartLight() {
  RemoteTheme(darkTheme = false) {
    Surface(modifier = Modifier.fillMaxWidth()) {
      ThinSlider(
        value = 0f,
        onValueChange = {},
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun ThinSliderAtEndDark() {
  RemoteTheme(darkTheme = true) {
    Surface(modifier = Modifier.fillMaxWidth()) {
      ThinSlider(
        value = 1f,
        onValueChange = {},
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun ThinSliderLargeThumb() {
  RemoteTheme(darkTheme = false) {
    Surface(modifier = Modifier.fillMaxWidth()) {
      ThinSlider(
        value = 0.5f,
        onValueChange = {},
        thumbSize = 20.dp,
        trackHeight = 4.dp,
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun ThinSliderSmallThumb() {
  RemoteTheme(darkTheme = false) {
    Surface(modifier = Modifier.fillMaxWidth()) {
      ThinSlider(
        value = 0.5f,
        onValueChange = {},
        thumbSize = 8.dp,
        trackHeight = 4.dp,
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun ThinSliderThickTrack() {
  RemoteTheme(darkTheme = false) {
    Surface(modifier = Modifier.fillMaxWidth()) {
      ThinSlider(
        value = 0.5f,
        onValueChange = {},
        thumbSize = 12.dp,
        trackHeight = 8.dp,
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun ThinSliderThinTrackLargeThumb() {
  RemoteTheme(darkTheme = true) {
    Surface(modifier = Modifier.fillMaxWidth()) {
      ThinSlider(
        value = 0.7f,
        onValueChange = {},
        thumbSize = 16.dp,
        trackHeight = 2.dp,
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      )
    }
  }
}
