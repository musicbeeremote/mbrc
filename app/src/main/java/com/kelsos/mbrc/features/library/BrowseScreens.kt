package com.kelsos.mbrc.features.library

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.ui.DoubleLineRow
import com.kelsos.mbrc.common.ui.EmptyScreen
import com.kelsos.mbrc.common.ui.ScreenContent
import com.kelsos.mbrc.common.ui.SingleLineRow
import com.kelsos.mbrc.common.ui.pagingDataFlow
import com.kelsos.mbrc.features.library.data.Album
import com.kelsos.mbrc.features.library.data.Artist
import com.kelsos.mbrc.features.library.data.Genre
import com.kelsos.mbrc.features.library.data.Track
import com.kelsos.mbrc.features.library.presentation.AlbumViewModel
import com.kelsos.mbrc.features.library.presentation.ArtistViewModel
import com.kelsos.mbrc.features.library.presentation.GenreViewModel
import com.kelsos.mbrc.features.library.presentation.TrackViewModel
import com.kelsos.mbrc.theme.RemoteTheme
import org.koin.androidx.compose.getViewModel

@Composable
fun <T : Any> BrowseScreen(
  items: LazyPagingItems<T>,
  text: String,
  sync: () -> Unit,
  itemContent: @Composable LazyItemScope.(value: T?) -> Unit
) = Surface(modifier = Modifier.fillMaxSize()) {
  Column(modifier = Modifier.fillMaxSize()) {
    if (items.itemCount == 0) {
      EmptyScreen(
        modifier = Modifier.weight(1f),
        text = text,
        imageVector = Icons.Filled.MusicNote,
        contentDescription = text
      ) {
        TextButton(onClick = sync) {
          Text(text = stringResource(id = R.string.press_to_sync))
        }
      }
    } else {
      ScreenContent(items = items, itemContent = itemContent)
    }
  }
}

@Composable
fun GenresScreen(sync: () -> Unit = {}) {
  val vm = getViewModel<GenreViewModel>()
  GenresScreen(genres = vm.genres.collectAsLazyPagingItems(), sync = sync)
}

@Composable
fun GenresScreen(
  genres: LazyPagingItems<Genre>,
  sync: () -> Unit
) = BrowseScreen(
  items = genres,
  text = stringResource(id = R.string.library_genres_list_empty),
  sync = sync
) {
  SingleLineRow(text = it?.genre, clicked = {})
}

@Preview
@Composable
fun GenresScreenPreview() {
  RemoteTheme {
    GenresScreen(
      genres = pagingDataFlow(
        Genre(
          genre = "Metal",
          id = 1
        )
      ).collectAsLazyPagingItems(),
      sync = {}
    )
  }
}

@Composable
fun ArtistsScreen(sync: () -> Unit = {}) {
  val vm = getViewModel<ArtistViewModel>()
  ArtistsScreen(artists = vm.artists.collectAsLazyPagingItems(), sync = sync)
}

@Composable
fun ArtistsScreen(
  artists: LazyPagingItems<Artist>,
  sync: () -> Unit
) = BrowseScreen(
  items = artists,
  text = stringResource(id = R.string.library_artists_list_empty),
  sync = sync
) {
  SingleLineRow(text = it?.artist, clicked = { })
}

@Preview
@Composable
fun ArtistsScreenPreview() {
  RemoteTheme {
    ArtistsScreen(
      artists = pagingDataFlow(
        Artist(
          artist = "Gamma Ray",
          id = 1
        )
      ).collectAsLazyPagingItems(),
      sync = {}
    )
  }
}

@Composable
fun AlbumsScreen(sync: () -> Unit = {}) {
  val vm = getViewModel<AlbumViewModel>()
  AlbumsScreen(albums = vm.albums.collectAsLazyPagingItems(), sync = sync)
}

@Composable
fun AlbumsScreen(
  albums: LazyPagingItems<Album>,
  sync: () -> Unit
) = BrowseScreen(
  items = albums,
  text = stringResource(id = R.string.library_albums_list_empty),
  sync = sync
) {
  DoubleLineRow(lineOne = it?.album, lineTwo = it?.artist, coverUrl = null, clicked = { })
}

@Preview
@Composable
fun AlbumsScreenPreview() {
  RemoteTheme {
    AlbumsScreen(
      albums = pagingDataFlow(
        Album(
          album = "No world order",
          artist = "Gamma Ray",
          cover = "",
          id = 1
        )
      ).collectAsLazyPagingItems(),
      sync = {}
    )
  }
}

@Composable
fun TracksScreen(sync: () -> Unit = {}) {
  val vm = getViewModel<TrackViewModel>()
  TracksScreen(tracks = vm.tracks.collectAsLazyPagingItems(), sync = sync)
}

@Composable
fun TracksScreen(
  tracks: LazyPagingItems<Track>,
  sync: () -> Unit
) = BrowseScreen(
  items = tracks,
  text = stringResource(id = R.string.library_tracks_list_empty),
  sync = sync
) {
  DoubleLineRow(lineOne = it?.title, lineTwo = it?.album, coverUrl = null, clicked = { })
}

@Preview
@Composable
fun TrackScreenPreview() {
  RemoteTheme {
    TracksScreen(
      tracks = pagingDataFlow(
        Track(
          album = "No world order",
          artist = "Gamma Ray",
          title = "Damn the machine",
          src = "",
          disc = 1,
          trackno = 6,
          albumArtist = "Gamma Ray",
          genre = "Power Metal",
          year = "2001",
          id = 1
        )
      ).collectAsLazyPagingItems(),
      sync = {}
    )
  }
}
