package com.kelsos.mbrc.features.library.compose.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.collectAsLazyPagingItems
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.ui.helpers.QueueResult
import com.kelsos.mbrc.features.library.artists.Artist
import com.kelsos.mbrc.features.library.artists.ArtistUiMessage
import com.kelsos.mbrc.features.library.artists.BrowseArtistViewModel
import com.kelsos.mbrc.features.library.compose.components.ArtistListItem
import com.kelsos.mbrc.features.queue.Queue
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import org.koin.androidx.compose.koinViewModel

@Composable
fun ArtistsTab(
  snackbarHostState: SnackbarHostState,
  isSyncing: Boolean,
  onNavigateToArtistAlbums: (Artist) -> Unit,
  onSync: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: BrowseArtistViewModel = koinViewModel()
) {
  val artists = viewModel.artists.collectAsLazyPagingItems()
  val showSync by viewModel.showSync.collectAsState(initial = true)

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
        is ArtistUiMessage.QueueSuccess -> QueueResult.Success(event.tracksCount)
        is ArtistUiMessage.QueueFailed -> QueueResult.Failed
        is ArtistUiMessage.NetworkUnavailable -> QueueResult.NetworkUnavailable
        else -> null
      }
    }.filterIsInstance<QueueResult>()
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
}
