package com.kelsos.mbrc.feature.library.compose.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.kelsos.mbrc.core.common.settings.AlbumSortField
import com.kelsos.mbrc.core.common.settings.AlbumSortPreference
import com.kelsos.mbrc.core.common.settings.SortOrder
import com.kelsos.mbrc.core.common.settings.SortPreference
import com.kelsos.mbrc.core.common.utilities.AppError
import com.kelsos.mbrc.core.common.utilities.Outcome
import com.kelsos.mbrc.core.data.library.album.Album
import com.kelsos.mbrc.core.queue.Queue
import com.kelsos.mbrc.feature.library.R
import com.kelsos.mbrc.feature.library.albums.AlbumUiMessage
import com.kelsos.mbrc.feature.library.albums.BrowseAlbumViewModel
import com.kelsos.mbrc.feature.library.compose.SortBottomSheet
import com.kelsos.mbrc.feature.library.compose.SortOption
import com.kelsos.mbrc.feature.library.compose.components.AlbumListItem
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import org.koin.androidx.compose.koinViewModel

val albumSortOptions = listOf(
  SortOption(AlbumSortField.NAME, R.string.sort_by_name),
  SortOption(AlbumSortField.ARTIST, R.string.sort_by_artist)
)

@Composable
fun AlbumsTab(
  snackbarHostState: SnackbarHostState,
  isSyncing: Boolean,
  showSortSheet: Boolean,
  onNavigateToAlbumTracks: (Album) -> Unit,
  onDismissSortSheet: () -> Unit,
  onSync: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: BrowseAlbumViewModel = koinViewModel()
) {
  val albums = viewModel.albums.collectAsLazyPagingItems()
  val showSync by viewModel.showSync.collectAsStateWithLifecycle(initialValue = true)
  val sortPreference by viewModel.sortPreference.collectAsStateWithLifecycle(
    initialValue = SortPreference(AlbumSortField.NAME, SortOrder.ASC)
  )

  // Handle navigation events
  LaunchedEffect(Unit) {
    viewModel.events.filterIsInstance<AlbumUiMessage.OpenAlbumTracks>().collect { event ->
      onNavigateToAlbumTracks(event.album)
    }
  }

  // Handle queue results
  val queueResults = remember {
    viewModel.events.map { event ->
      when (event) {
        is AlbumUiMessage.QueueSuccess -> Outcome.Success(event.tracksCount)
        is AlbumUiMessage.QueueFailed -> Outcome.Failure(AppError.OperationFailed)
        is AlbumUiMessage.NetworkUnavailable -> Outcome.Failure(AppError.NetworkUnavailable)
        else -> null
      }
    }.filterIsInstance<Outcome<Int>>()
  }

  LibraryBrowseTab(
    items = albums,
    queueResults = queueResults,
    snackbarHostState = snackbarHostState,
    syncState = SyncState(
      isSyncing = isSyncing,
      showSync = showSync,
      onSync = onSync
    ),
    emptyState = EmptyState(
      message = stringResource(R.string.albums_list_empty),
      icon = Icons.Default.Album
    ),
    itemKey = { it.id },
    modifier = modifier
  ) { album ->
    AlbumListItem(
      album = album,
      onClick = { viewModel.queue(Queue.Default, album) },
      onQueue = { queue -> viewModel.queue(queue, album) }
    )
  }

  if (showSortSheet) {
    SortBottomSheet(
      title = stringResource(R.string.sort_title),
      options = albumSortOptions,
      selectedField = sortPreference.field,
      selectedOrder = sortPreference.order,
      onSortSelected = { field, order ->
        viewModel.updateSortPreference(AlbumSortPreference(field, order))
      },
      onDismiss = onDismissSortSheet
    )
  }
}
