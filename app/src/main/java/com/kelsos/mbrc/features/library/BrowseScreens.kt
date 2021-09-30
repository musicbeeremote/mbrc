package com.kelsos.mbrc.features.library

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
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
import com.kelsos.mbrc.features.library.data.key
import com.kelsos.mbrc.features.library.presentation.AlbumViewModel
import com.kelsos.mbrc.features.library.presentation.ArtistViewModel
import com.kelsos.mbrc.features.library.presentation.GenreViewModel
import com.kelsos.mbrc.features.library.presentation.TrackViewModel
import com.kelsos.mbrc.features.queue.Queue
import com.kelsos.mbrc.theme.RemoteTheme
import org.koin.androidx.compose.getViewModel
import java.io.File

typealias QueueAction = (queue: Queue) -> Unit

typealias QueueActionWithId = (queue: Queue, id: Long) -> Unit

@Composable
fun <T : Any> BrowseScreen(
  items: LazyPagingItems<T>,
  text: String,
  key: (t: T) -> Long,
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
      ScreenContent(items = items, itemContent = itemContent, key = key)
    }
  }
}

@Composable
private fun ActionMenu(@StringRes defaultAction: Int? = null, action: QueueAction) {
  DropdownMenuItem(onClick = { action(Queue.Now) }) {
    Text(text = stringResource(id = R.string.menu_play))
  }
  if (defaultAction != null) {
    DropdownMenuItem(onClick = { action(Queue.Default) }) {
      Text(text = stringResource(id = defaultAction))
    }
  } else {
    DropdownMenuItem(onClick = { action(Queue.PlayAlbum) }) {
      Text(text = stringResource(id = R.string.menu_play_album))
    }
    DropdownMenuItem(onClick = { action(Queue.PlayArtist) }) {
      Text(text = stringResource(id = R.string.menu_play_artist))
    }
    DropdownMenuItem(onClick = { action(Queue.PlayAll) }) {
      Text(text = stringResource(id = R.string.menu_play_queue_all))
    }
  }
  DropdownMenuItem(onClick = { action(Queue.Next) }) {
    Text(text = stringResource(id = R.string.menu_queue_next))
  }
  DropdownMenuItem(onClick = { action(Queue.Last) }) {
    Text(text = stringResource(id = R.string.menu_queue_last))
  }
}

@Composable
fun GenresScreen(sync: () -> Unit, action: QueueActionWithId) {
  val vm = getViewModel<GenreViewModel>()
  GenresScreen(genres = vm.genres.collectAsLazyPagingItems(), sync = sync, action = action)
}

@Composable
fun GenresScreen(
  genres: LazyPagingItems<Genre>,
  sync: () -> Unit,
  action: QueueActionWithId
) = BrowseScreen(
  items = genres,
  text = stringResource(id = R.string.library_genres_list_empty),
  key = { it.id },
  sync = sync
) { genre ->
  SingleLineRow(
    text = genre?.genre,
    clicked = {
      genre?.let { genre ->
        action(Queue.Default, genre.id)
      }
    }
  ) {
    ActionMenu(defaultAction = R.string.menu_genre_artists) { queue ->
      genre?.let { genre ->
        action(queue, genre.id)
      }
    }
  }
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
      sync = {},
      action = { _, _ -> }
    )
  }
}

@Composable
fun ArtistsScreen(sync: () -> Unit, action: QueueActionWithId) {
  val vm = getViewModel<ArtistViewModel>()
  ArtistsScreen(artists = vm.artists.collectAsLazyPagingItems(), sync = sync, action = action)
}

@Composable
fun ArtistsScreen(
  artists: LazyPagingItems<Artist>,
  sync: () -> Unit,
  action: QueueActionWithId
) = BrowseScreen(
  items = artists,
  text = stringResource(id = R.string.library_artists_list_empty),
  key = { it.id },
  sync = sync,
) { artist ->
  SingleLineRow(
    text = artist?.artist,
    clicked = {
      artist?.let { artist ->
        action(Queue.Default, artist.id)
      }
    }
  ) {
    ActionMenu(
      defaultAction = R.string.menu_artist_albums,
      action = { queue ->
        artist?.let { artist ->
          action(queue, artist.id)
        }
      }
    )
  }
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
      sync = {},
      action = { _, _ -> }
    )
  }
}

@Composable
fun AlbumsScreen(sync: () -> Unit, action: QueueActionWithId) {
  val vm = getViewModel<AlbumViewModel>()
  AlbumsScreen(albums = vm.albums.collectAsLazyPagingItems(), sync = sync, action = action)
}

@Composable
fun AlbumsScreen(
  albums: LazyPagingItems<Album>,
  sync: () -> Unit,
  action: QueueActionWithId
) = BrowseScreen(
  items = albums,
  text = stringResource(id = R.string.library_albums_list_empty),
  key = { it.id },
  sync = sync
) { album ->

  val cover = if (album != null) {
    val cache = File(LocalContext.current.cacheDir, "covers")
    File(cache, album.key()).toUri().toString()
  } else {
    ""
  }
  DoubleLineRow(
    lineOne = album?.album, lineTwo = album?.artist, coverUrl = cover,
    clicked = {
      album?.let { album ->
        action(Queue.Default, album.id)
      }
    }
  ) {
    ActionMenu(
      defaultAction = R.string.menu_album_tracks,
      action = { queue ->
        album?.let { album ->
          action(queue, album.id)
        }
      }
    )
  }
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
      sync = {},
      action = { _, _ -> }
    )
  }
}

@Composable
fun TracksScreen(sync: () -> Unit, action: QueueActionWithId) {
  val vm = getViewModel<TrackViewModel>()
  TracksScreen(tracks = vm.tracks.collectAsLazyPagingItems(), sync = sync, action = action)
}

@Composable
fun TracksScreen(
  tracks: LazyPagingItems<Track>,
  sync: () -> Unit,
  action: QueueActionWithId
) = BrowseScreen(
  items = tracks,
  text = stringResource(id = R.string.library_tracks_list_empty),
  key = { it.id },
  sync = sync
) { track ->
  val cover = if (track != null) {
    val cache = File(LocalContext.current.cacheDir, "covers")
    File(cache, track.key()).toUri().toString()
  } else {
    ""
  }
  DoubleLineRow(
    lineOne = track?.title,
    lineTwo = track?.artist,
    coverUrl = cover,
    clicked = {
      track?.let { track ->
        action(Queue.Default, track.id)
      }
    }
  ) {
    ActionMenu { queue ->
      track?.let { track ->
        action(queue, track.id)
      }
    }
  }
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
      sync = {},
      action = { _, _ -> }
    )
  }
}
