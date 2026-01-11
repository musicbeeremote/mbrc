package com.kelsos.mbrc.feature.library.compose.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.kelsos.mbrc.core.common.settings.ArtistSortField
import com.kelsos.mbrc.core.common.settings.ArtistSortPreference
import com.kelsos.mbrc.core.common.settings.SortOrder
import com.kelsos.mbrc.core.common.settings.SortPreference
import com.kelsos.mbrc.core.common.utilities.AppError
import com.kelsos.mbrc.core.common.utilities.Outcome
import com.kelsos.mbrc.core.data.library.artist.Artist
import com.kelsos.mbrc.core.queue.Queue
import com.kelsos.mbrc.feature.library.R
import com.kelsos.mbrc.feature.library.artists.ArtistUiMessage
import com.kelsos.mbrc.feature.library.artists.BrowseArtistViewModel
import com.kelsos.mbrc.feature.library.compose.SortBottomSheet
import com.kelsos.mbrc.feature.library.compose.SortOption
import com.kelsos.mbrc.feature.library.compose.components.ArtistListItem
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import org.koin.androidx.compose.koinViewModel

val artistSortOptions = listOf(
  SortOption(ArtistSortField.NAME, R.string.sort_by_name)
)

@Composable
fun ArtistsTab(
  snackbarHostState: SnackbarHostState,
  isSyncing: Boolean,
  showSortSheet: Boolean,
  onNavigateToArtistAlbums: (Artist) -> Unit,
  onDismissSortSheet: () -> Unit,
  onSync: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: BrowseArtistViewModel = koinViewModel()
) {
  val artists = viewModel.artists.collectAsLazyPagingItems()
  val showSync by viewModel.showSync.collectAsStateWithLifecycle(initialValue = true)
  val sortPreference by viewModel.sortPreference.collectAsStateWithLifecycle(
    initialValue = SortPreference(ArtistSortField.NAME, SortOrder.ASC)
  )

  // Handle navigation events
  LaunchedEffect(Unit) {
    viewModel.events.filterIsInstance<ArtistUiMessage.OpenArtistAlbums>().collect { event ->
      onNavigateToArtistAlbums(event.artist)
    }
  }

  // Handle queue results
  val queueResults = remember {
    viewModel.events.map { event ->
      when (event) {
        is ArtistUiMessage.QueueSuccess -> Outcome.Success(event.tracksCount)
        is ArtistUiMessage.QueueFailed -> Outcome.Failure(AppError.OperationFailed)
        is ArtistUiMessage.NetworkUnavailable -> Outcome.Failure(AppError.NetworkUnavailable)
        else -> null
      }
    }.filterIsInstance<Outcome<Int>>()
  }

  LibraryBrowseTab(
    items = artists,
    queueResults = queueResults,
    snackbarHostState = snackbarHostState,
    syncState = SyncState(
      isSyncing = isSyncing,
      showSync = showSync,
      onSync = onSync
    ),
    emptyState = EmptyState(
      message = stringResource(R.string.artists_list_empty),
      icon = Icons.Default.Person
    ),
    itemKey = { it.id },
    modifier = modifier
  ) { artist ->
    ArtistListItem(
      artist = artist,
      onClick = { viewModel.queue(Queue.Default, artist) },
      onQueue = { queue -> viewModel.queue(queue, artist) }
    )
  }

  if (showSortSheet) {
    SortBottomSheet(
      title = stringResource(R.string.sort_title),
      options = artistSortOptions,
      selectedField = sortPreference.field,
      selectedOrder = sortPreference.order,
      onSortSelected = { field, order ->
        viewModel.updateSortPreference(ArtistSortPreference(field, order))
      },
      onDismiss = onDismissSortSheet
    )
  }
}
