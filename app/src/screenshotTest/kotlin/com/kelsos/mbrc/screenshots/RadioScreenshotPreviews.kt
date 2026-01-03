package com.kelsos.mbrc.screenshots

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.kelsos.mbrc.core.data.radio.RadioStation
import com.kelsos.mbrc.core.ui.compose.EmptyScreen
import com.kelsos.mbrc.core.ui.theme.RemoteTheme
import com.kelsos.mbrc.feature.content.radio.compose.RadioStationItem

private val sampleStation = RadioStation(
  id = 1,
  name = "1.FM - Absolute 90s Party Zone",
  url = "http://example.com/stream"
)

@PreviewTest
@Preview(showBackground = true)
@Composable
fun RadioStationItemLight() {
  RemoteTheme(darkTheme = false) {
    Surface {
      RadioStationItem(
        station = sampleStation,
        onPlay = {}
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun RadioStationItemDark() {
  RemoteTheme(darkTheme = true) {
    Surface {
      RadioStationItem(
        station = sampleStation,
        onPlay = {}
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun EmptyRadioScreenLight() {
  RemoteTheme(darkTheme = false) {
    Surface(modifier = Modifier.fillMaxSize()) {
      EmptyScreen(
        message = "No radio stations found",
        icon = Icons.Default.Radio
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun EmptyRadioScreenDark() {
  RemoteTheme(darkTheme = true) {
    Surface(modifier = Modifier.fillMaxSize()) {
      EmptyScreen(
        message = "No radio stations found",
        icon = Icons.Default.Radio
      )
    }
  }
}
