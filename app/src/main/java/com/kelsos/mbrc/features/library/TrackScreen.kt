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
import com.kelsos.mbrc.features.library.data.Track
import com.kelsos.mbrc.features.library.data.key
import com.kelsos.mbrc.features.library.presentation.TrackViewModel
import com.kelsos.mbrc.features.queue.Queue
import com.kelsos.mbrc.theme.RemoteTheme
import org.koin.androidx.compose.koinViewModel
import java.io.File

@Composable
fun TracksScreen(
  sync: () -> Unit,
  action: QueueActionWithId,
) {
  val vm = koinViewModel<TrackViewModel>()
  TracksScreen(tracks = vm.tracks.collectAsLazyPagingItems(), sync = sync, action = action)
}

@Composable
fun TracksScreen(
  tracks: LazyPagingItems<Track>,
  sync: () -> Unit,
  action: QueueActionWithId,
) = BrowseScreen(
  items = tracks,
  text = stringResource(id = R.string.library_tracks_list_empty),
  key = { it.id },
  sync = sync,
) { track ->
  val cover =
    if (track != null) {
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
    },
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
      tracks =
        pagingDataFlow(
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
            id = 1,
          ),
        ).collectAsLazyPagingItems(),
      sync = {},
      action = { _, _ -> },
    )
  }
}
