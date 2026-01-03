package com.kelsos.mbrc.feature.library.compose.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.collectAsLazyPagingItems
import com.kelsos.mbrc.core.common.utilities.AppError
import com.kelsos.mbrc.core.common.utilities.Outcome
import com.kelsos.mbrc.core.data.library.genre.Genre
import com.kelsos.mbrc.core.queue.Queue
import com.kelsos.mbrc.feature.library.R
import com.kelsos.mbrc.feature.library.compose.components.GenreListItem
import com.kelsos.mbrc.feature.library.genres.BrowseGenreViewModel
import com.kelsos.mbrc.feature.library.genres.GenreUiMessage
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import org.koin.androidx.compose.koinViewModel

@Composable
fun GenresTab(
  snackbarHostState: SnackbarHostState,
  isSyncing: Boolean,
  onNavigateToGenreArtists: (Genre) -> Unit,
  onSync: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: BrowseGenreViewModel = koinViewModel()
) {
  val genres = viewModel.genres.collectAsLazyPagingItems()
  val showSync by viewModel.showSync.collectAsState(initial = true)

  // Handle navigation events
  LaunchedEffect(Unit) {
    viewModel.events.filterIsInstance<GenreUiMessage.OpenArtists>().collect { event ->
      onNavigateToGenreArtists(event.genre)
    }
  }

  // Handle queue results
  val queueResults = remember {
    viewModel.events.map { event ->
      when (event) {
        is GenreUiMessage.QueueSuccess -> Outcome.Success(event.tracksCount)
        is GenreUiMessage.QueueFailed -> Outcome.Failure(AppError.OperationFailed)
        is GenreUiMessage.NetworkUnavailable -> Outcome.Failure(AppError.NetworkUnavailable)
        else -> null
      }
    }.filterIsInstance<Outcome<Int>>()
  }

  LibraryBrowseTab(
    items = genres,
    queueResults = queueResults,
    snackbarHostState = snackbarHostState,
    syncState = SyncState(
      isSyncing = isSyncing,
      showSync = showSync,
      onSync = onSync
    ),
    emptyState = EmptyState(
      message = stringResource(R.string.genres_list_empty),
      icon = Icons.AutoMirrored.Filled.QueueMusic
    ),
    itemKey = { it.id },
    modifier = modifier
  ) { genre ->
    GenreListItem(
      genre = genre,
      onClick = { viewModel.queue(Queue.Default, genre) },
      onQueue = { queue -> viewModel.queue(queue, genre) }
    )
  }
}
