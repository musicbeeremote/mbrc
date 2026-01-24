package com.kelsos.mbrc.screenshots

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.kelsos.mbrc.core.ui.compose.EmptyScreen
import com.kelsos.mbrc.core.ui.theme.RemoteTheme

@PreviewTest
@Preview(showBackground = true)
@Composable
fun EmptyGenreArtistsScreenLight() {
  RemoteTheme(darkTheme = false) {
    Surface(modifier = Modifier.fillMaxSize()) {
      EmptyScreen(
        message = "No artists found",
        icon = Icons.Default.Person
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun EmptyGenreArtistsScreenDark() {
  RemoteTheme(darkTheme = true) {
    Surface(modifier = Modifier.fillMaxSize()) {
      EmptyScreen(
        message = "No artists found",
        icon = Icons.Default.Person
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun EmptyGenreAlbumsScreenLight() {
  RemoteTheme(darkTheme = false) {
    Surface(modifier = Modifier.fillMaxSize()) {
      EmptyScreen(
        message = "No albums found",
        icon = Icons.Default.Album
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun EmptyGenreAlbumsScreenDark() {
  RemoteTheme(darkTheme = true) {
    Surface(modifier = Modifier.fillMaxSize()) {
      EmptyScreen(
        message = "No albums found",
        icon = Icons.Default.Album
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun EmptyArtistAlbumsScreenLight() {
  RemoteTheme(darkTheme = false) {
    Surface(modifier = Modifier.fillMaxSize()) {
      EmptyScreen(
        message = "No albums found",
        icon = Icons.Default.Album
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun EmptyArtistAlbumsScreenDark() {
  RemoteTheme(darkTheme = true) {
    Surface(modifier = Modifier.fillMaxSize()) {
      EmptyScreen(
        message = "No albums found",
        icon = Icons.Default.Album
      )
    }
  }
}
