package com.kelsos.mbrc.features.library

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.ui.DoubleLineRow
import com.kelsos.mbrc.common.ui.pagingDataFlow
import com.kelsos.mbrc.features.library.data.Album
import com.kelsos.mbrc.features.library.data.key
import com.kelsos.mbrc.features.library.presentation.AlbumViewModel
import com.kelsos.mbrc.features.queue.Queue
import com.kelsos.mbrc.theme.RemoteTheme
import org.koin.androidx.compose.koinViewModel
import java.io.File

@Composable
fun AlbumsScreen(
  sync: () -> Unit,
  action: QueueActionWithId,
) {
  val vm = koinViewModel<AlbumViewModel>()
  AlbumsScreen(albums = vm.albums.collectAsLazyPagingItems(), sync = sync, action = action)
}

@Composable
fun AlbumsScreen(
  albums: LazyPagingItems<Album>,
  sync: () -> Unit,
  action: QueueActionWithId,
) = BrowseScreen(
  items = albums,
  text = stringResource(id = R.string.library_albums_list_empty),
  key = { it.id },
  sync = sync,
) { album ->

  val cover =
    if (album != null) {
      val cache = File(LocalContext.current.cacheDir, "covers")
      File(cache, album.key()).toUri().toString()
    } else {
      ""
    }
  DoubleLineRow(
    lineOne = album?.album,
    lineTwo = album?.artist,
    coverUrl = cover,
    clicked = {
      album?.let { album ->
        action(Queue.Default, album.id)
      }
    },
  ) {
    ActionMenu(
      defaultAction = R.string.menu_album_tracks,
      action = { queue ->
        album?.let { album ->
          action(queue, album.id)
        }
      },
    )
  }
}

@Preview
@Composable
fun AlbumsScreenPreview() {
  RemoteTheme {
    AlbumsScreen(
      albums =
        pagingDataFlow(
          Album(
            album = "No world order",
            artist = "Gamma Ray",
            cover = "",
            id = 1,
          ),
        ).collectAsLazyPagingItems(),
      sync = {},
      action = { _, _ -> },
    )
  }
}
