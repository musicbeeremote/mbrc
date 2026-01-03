package com.kelsos.mbrc.screenshots

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.kelsos.mbrc.core.data.playlist.Playlist
import com.kelsos.mbrc.core.ui.compose.EmptyScreen
import com.kelsos.mbrc.core.ui.theme.RemoteTheme
import com.kelsos.mbrc.feature.content.playlists.compose.PlaylistItem

private val samplePlaylist = Playlist(
  id = 1,
  name = "My Favorite Songs",
  url = "playlist://favorites"
)

@PreviewTest
@Preview(showBackground = true)
@Composable
fun PlaylistItemLight() {
  RemoteTheme(darkTheme = false) {
    Surface {
      PlaylistItem(
        playlist = samplePlaylist,
        onPlay = {}
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun PlaylistItemDark() {
  RemoteTheme(darkTheme = true) {
    Surface {
      PlaylistItem(
        playlist = samplePlaylist,
        onPlay = {}
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun EmptyPlaylistScreenLight() {
  RemoteTheme(darkTheme = false) {
    Surface(modifier = Modifier.fillMaxSize()) {
      EmptyScreen(
        message = "No playlists found",
        icon = Icons.AutoMirrored.Filled.QueueMusic
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun EmptyPlaylistScreenDark() {
  RemoteTheme(darkTheme = true) {
    Surface(modifier = Modifier.fillMaxSize()) {
      EmptyScreen(
        message = "No playlists found",
        icon = Icons.AutoMirrored.Filled.QueueMusic
      )
    }
  }
}
