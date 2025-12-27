package com.kelsos.mbrc.screenshots

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.kelsos.mbrc.common.ui.compose.EmptyScreen
import com.kelsos.mbrc.features.library.albums.Album
import com.kelsos.mbrc.features.library.artists.Artist
import com.kelsos.mbrc.features.library.compose.components.AlbumListItem
import com.kelsos.mbrc.features.library.compose.components.ArtistListItem
import com.kelsos.mbrc.features.library.compose.components.GenreListItem
import com.kelsos.mbrc.features.library.compose.components.TrackListItem
import com.kelsos.mbrc.features.library.genres.Genre
import com.kelsos.mbrc.features.library.tracks.Track
import com.kelsos.mbrc.theme.RemoteTheme

@PreviewTest
@Preview(showBackground = true)
@Composable
fun GenreListItemLight() {
  RemoteTheme(darkTheme = false) {
    Surface {
      GenreListItem(
        genre = Genre(genre = "Rock", id = 1),
        onClick = {},
        onQueue = {}
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun GenreListItemDark() {
  RemoteTheme(darkTheme = true) {
    Surface {
      GenreListItem(
        genre = Genre(genre = "Rock", id = 1),
        onClick = {},
        onQueue = {}
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun ArtistListItemLight() {
  RemoteTheme(darkTheme = false) {
    Surface {
      ArtistListItem(
        artist = Artist(artist = "Pink Floyd", id = 1),
        onClick = {},
        onQueue = {}
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun ArtistListItemDark() {
  RemoteTheme(darkTheme = true) {
    Surface {
      ArtistListItem(
        artist = Artist(artist = "Pink Floyd", id = 1),
        onClick = {},
        onQueue = {}
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun AlbumListItemLight() {
  RemoteTheme(darkTheme = false) {
    Surface {
      AlbumListItem(
        album = Album(
          id = 1,
          artist = "Pink Floyd",
          album = "The Dark Side of the Moon",
          cover = null
        ),
        onClick = {},
        onQueue = {}
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun AlbumListItemDark() {
  RemoteTheme(darkTheme = true) {
    Surface {
      AlbumListItem(
        album = Album(
          id = 1,
          artist = "Pink Floyd",
          album = "The Dark Side of the Moon",
          cover = null
        ),
        onClick = {},
        onQueue = {}
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun TrackListItemLight() {
  RemoteTheme(darkTheme = false) {
    Surface {
      TrackListItem(
        track = Track(
          id = 1,
          artist = "Pink Floyd",
          album = "The Dark Side of the Moon",
          albumArtist = "Pink Floyd",
          title = "Time",
          genre = "Rock",
          src = "/music/time.mp3",
          disc = 1,
          trackno = 4,
          year = "1973"
        ),
        onClick = {},
        onQueue = {}
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun TrackListItemDark() {
  RemoteTheme(darkTheme = true) {
    Surface {
      TrackListItem(
        track = Track(
          id = 1,
          artist = "Pink Floyd",
          album = "The Dark Side of the Moon",
          albumArtist = "Pink Floyd",
          title = "Time",
          genre = "Rock",
          src = "/music/time.mp3",
          disc = 1,
          trackno = 4,
          year = "1973"
        ),
        onClick = {},
        onQueue = {}
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun EmptyGenresScreenLight() {
  RemoteTheme(darkTheme = false) {
    Surface(modifier = Modifier.fillMaxSize()) {
      EmptyScreen(
        message = "No genres found",
        icon = Icons.AutoMirrored.Filled.QueueMusic
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun EmptyGenresScreenDark() {
  RemoteTheme(darkTheme = true) {
    Surface(modifier = Modifier.fillMaxSize()) {
      EmptyScreen(
        message = "No genres found",
        icon = Icons.AutoMirrored.Filled.QueueMusic
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun EmptyArtistsScreenLight() {
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
fun EmptyArtistsScreenDark() {
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
fun EmptyAlbumsScreenLight() {
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
fun EmptyAlbumsScreenDark() {
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
fun EmptyTracksScreenLight() {
  RemoteTheme(darkTheme = false) {
    Surface(modifier = Modifier.fillMaxSize()) {
      EmptyScreen(
        message = "No tracks found",
        icon = Icons.Default.MusicNote
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun EmptyTracksScreenDark() {
  RemoteTheme(darkTheme = true) {
    Surface(modifier = Modifier.fillMaxSize()) {
      EmptyScreen(
        message = "No tracks found",
        icon = Icons.Default.MusicNote
      )
    }
  }
}
