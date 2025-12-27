package com.kelsos.mbrc.screenshots

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.kelsos.mbrc.common.ui.compose.EmptyScreen
import com.kelsos.mbrc.features.nowplaying.NowPlaying
import com.kelsos.mbrc.features.nowplaying.compose.NowPlayingTrackItem
import com.kelsos.mbrc.theme.RemoteTheme

// Track Item - Normal State

@PreviewTest
@Preview(showBackground = true)
@Composable
fun NowPlayingTrackItemLight() {
  RemoteTheme(darkTheme = false) {
    Surface {
      NowPlayingTrackItem(
        track = NowPlaying(
          id = 1,
          title = "Bohemian Rhapsody",
          artist = "Queen",
          path = "/music/queen/bohemian_rhapsody.mp3",
          position = 1
        ),
        isPlaying = false,
        isDragging = false,
        onClick = {},
        modifier = Modifier.fillMaxWidth()
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun NowPlayingTrackItemDark() {
  RemoteTheme(darkTheme = true) {
    Surface {
      NowPlayingTrackItem(
        track = NowPlaying(
          id = 1,
          title = "Bohemian Rhapsody",
          artist = "Queen",
          path = "/music/queen/bohemian_rhapsody.mp3",
          position = 1
        ),
        isPlaying = false,
        isDragging = false,
        onClick = {},
        modifier = Modifier.fillMaxWidth()
      )
    }
  }
}

// Track Item - Playing State

@PreviewTest
@Preview(showBackground = true)
@Composable
fun NowPlayingTrackItemPlayingLight() {
  RemoteTheme(darkTheme = false) {
    Surface {
      NowPlayingTrackItem(
        track = NowPlaying(
          id = 1,
          title = "Stairway to Heaven",
          artist = "Led Zeppelin",
          path = "/music/led_zeppelin/stairway.mp3",
          position = 5
        ),
        isPlaying = true,
        isDragging = false,
        onClick = {},
        modifier = Modifier.fillMaxWidth()
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun NowPlayingTrackItemPlayingDark() {
  RemoteTheme(darkTheme = true) {
    Surface {
      NowPlayingTrackItem(
        track = NowPlaying(
          id = 1,
          title = "Stairway to Heaven",
          artist = "Led Zeppelin",
          path = "/music/led_zeppelin/stairway.mp3",
          position = 5
        ),
        isPlaying = true,
        isDragging = false,
        onClick = {},
        modifier = Modifier.fillMaxWidth()
      )
    }
  }
}

// Track Item - Dragging State

@PreviewTest
@Preview(showBackground = true)
@Composable
fun NowPlayingTrackItemDraggingLight() {
  RemoteTheme(darkTheme = false) {
    Surface {
      NowPlayingTrackItem(
        track = NowPlaying(
          id = 1,
          title = "Hotel California",
          artist = "Eagles",
          path = "/music/eagles/hotel_california.mp3",
          position = 3
        ),
        isPlaying = false,
        isDragging = true,
        onClick = {},
        modifier = Modifier.fillMaxWidth()
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun NowPlayingTrackItemDraggingDark() {
  RemoteTheme(darkTheme = true) {
    Surface {
      NowPlayingTrackItem(
        track = NowPlaying(
          id = 1,
          title = "Hotel California",
          artist = "Eagles",
          path = "/music/eagles/hotel_california.mp3",
          position = 3
        ),
        isPlaying = false,
        isDragging = true,
        onClick = {},
        modifier = Modifier.fillMaxWidth()
      )
    }
  }
}

// Empty Queue Screen

@PreviewTest
@Preview(showBackground = true)
@Composable
fun EmptyNowPlayingScreenLight() {
  RemoteTheme(darkTheme = false) {
    Surface(modifier = Modifier.fillMaxSize()) {
      EmptyScreen(
        message = "Queue is empty",
        icon = Icons.AutoMirrored.Filled.QueueMusic
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun EmptyNowPlayingScreenDark() {
  RemoteTheme(darkTheme = true) {
    Surface(modifier = Modifier.fillMaxSize()) {
      EmptyScreen(
        message = "Queue is empty",
        icon = Icons.AutoMirrored.Filled.QueueMusic
      )
    }
  }
}
