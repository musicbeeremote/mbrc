package com.kelsos.mbrc.features.library

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.ui.SingleLineRow
import com.kelsos.mbrc.common.ui.pagingDataFlow
import com.kelsos.mbrc.features.library.data.Artist
import com.kelsos.mbrc.features.library.presentation.ArtistViewModel
import com.kelsos.mbrc.features.queue.Queue
import com.kelsos.mbrc.theme.RemoteTheme
import org.koin.androidx.compose.getViewModel

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
  sync = sync
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
