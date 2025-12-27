package com.kelsos.mbrc.features.library.compose.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.collectAsLazyPagingItems
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.ui.helpers.QueueResult
import com.kelsos.mbrc.features.library.compose.components.TrackListItem
import com.kelsos.mbrc.features.library.tracks.BrowseTrackViewModel
import com.kelsos.mbrc.features.library.tracks.TrackUiMessage
import com.kelsos.mbrc.features.queue.Queue
import kotlinx.coroutines.flow.map
import org.koin.androidx.compose.koinViewModel

@Composable
fun TracksTab(
  snackbarHostState: SnackbarHostState,
  isSyncing: Boolean,
  onSync: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: BrowseTrackViewModel = koinViewModel()
) {
  val tracks = viewModel.tracks.collectAsLazyPagingItems()
  val showSync by viewModel.showSync.collectAsState(initial = true)

  val queueResults = remember {
    viewModel.events.map { event ->
      when (event) {
        is TrackUiMessage.QueueSuccess -> QueueResult.Success(event.tracksCount)
        is TrackUiMessage.QueueFailed -> QueueResult.Failed
        is TrackUiMessage.NetworkUnavailable -> QueueResult.NetworkUnavailable
      }
    }
  }

  LibraryBrowseTab(
    items = tracks,
    queueResults = queueResults,
    snackbarHostState = snackbarHostState,
    syncState = SyncState(
      isSyncing = isSyncing,
      showSync = showSync,
      onSync = onSync
    ),
    emptyState = EmptyState(
      message = stringResource(R.string.common_empty_no_tracks),
      icon = Icons.Default.MusicNote
    ),
    itemKey = { it.id },
    modifier = modifier
  ) { track ->
    TrackListItem(
      track = track,
      onClick = { viewModel.queue(Queue.Default, track) },
      onQueue = { queue -> viewModel.queue(queue, track) }
    )
  }
}
