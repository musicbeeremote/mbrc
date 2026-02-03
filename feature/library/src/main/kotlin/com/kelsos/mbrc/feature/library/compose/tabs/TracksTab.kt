package com.kelsos.mbrc.feature.library.compose.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.kelsos.mbrc.core.common.settings.SortOrder
import com.kelsos.mbrc.core.common.settings.SortPreference
import com.kelsos.mbrc.core.common.settings.TrackSortField
import com.kelsos.mbrc.core.common.settings.TrackSortPreference
import com.kelsos.mbrc.core.common.utilities.AppError
import com.kelsos.mbrc.core.common.utilities.Outcome
import com.kelsos.mbrc.core.queue.Queue
import com.kelsos.mbrc.feature.library.R
import com.kelsos.mbrc.feature.library.compose.SortBottomSheet
import com.kelsos.mbrc.feature.library.compose.SortOption
import com.kelsos.mbrc.feature.library.compose.components.TrackListItem
import com.kelsos.mbrc.feature.library.tracks.BrowseTrackViewModel
import com.kelsos.mbrc.feature.library.tracks.TrackUiMessage
import kotlinx.coroutines.flow.map
import org.koin.androidx.compose.koinViewModel

val trackSortOptions = listOf(
  SortOption(TrackSortField.TITLE, R.string.sort_by_track_title),
  SortOption(TrackSortField.ARTIST, R.string.sort_by_artist),
  SortOption(TrackSortField.ALBUM, R.string.sort_by_album),
  SortOption(TrackSortField.ALBUM_ARTIST, R.string.sort_by_album_artist)
)

@Composable
fun TracksTab(
  snackbarHostState: SnackbarHostState,
  isSyncing: Boolean,
  showSortSheet: Boolean,
  onDismissSortSheet: () -> Unit,
  onSync: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: BrowseTrackViewModel = koinViewModel()
) {
  val tracks = viewModel.tracks.collectAsLazyPagingItems()
  val showSync by viewModel.showSync.collectAsStateWithLifecycle(initialValue = true)
  val sortPreference by viewModel.sortPreference.collectAsStateWithLifecycle(
    initialValue = SortPreference(TrackSortField.TITLE, SortOrder.ASC)
  )

  val queueResults = remember {
    viewModel.events.map { event ->
      when (event) {
        is TrackUiMessage.QueueSuccess -> Outcome.Success(event.tracksCount)
        is TrackUiMessage.QueueFailed -> Outcome.Failure(AppError.OperationFailed)
        is TrackUiMessage.NetworkUnavailable -> Outcome.Failure(AppError.NetworkUnavailable)
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

  if (showSortSheet) {
    SortBottomSheet(
      title = stringResource(R.string.sort_title),
      options = trackSortOptions,
      selectedField = sortPreference.field,
      selectedOrder = sortPreference.order,
      onSortSelected = { field, order ->
        viewModel.updateSortPreference(TrackSortPreference(field, order))
      },
      onDismiss = onDismissSortSheet
    )
  }
}
