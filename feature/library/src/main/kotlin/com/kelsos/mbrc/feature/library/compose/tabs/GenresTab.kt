package com.kelsos.mbrc.feature.library.compose.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.kelsos.mbrc.core.common.settings.GenreSortField
import com.kelsos.mbrc.core.common.settings.GenreSortPreference
import com.kelsos.mbrc.core.common.settings.SortOrder
import com.kelsos.mbrc.core.common.settings.SortPreference
import com.kelsos.mbrc.core.common.utilities.AppError
import com.kelsos.mbrc.core.common.utilities.Outcome
import com.kelsos.mbrc.core.data.library.genre.Genre
import com.kelsos.mbrc.core.queue.Queue
import com.kelsos.mbrc.feature.library.R
import com.kelsos.mbrc.feature.library.compose.SortBottomSheet
import com.kelsos.mbrc.feature.library.compose.SortOption
import com.kelsos.mbrc.feature.library.compose.components.GenreListItem
import com.kelsos.mbrc.feature.library.genres.BrowseGenreViewModel
import com.kelsos.mbrc.feature.library.genres.GenreUiMessage
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import org.koin.androidx.compose.koinViewModel

val genreSortOptions = listOf(
  SortOption(GenreSortField.NAME, R.string.sort_by_name)
)

@Composable
fun GenresTab(
  snackbarHostState: SnackbarHostState,
  isSyncing: Boolean,
  showSortSheet: Boolean,
  onNavigateToGenreArtists: (Genre) -> Unit,
  onNavigateToGenreAlbums: (Genre) -> Unit,
  onDismissSortSheet: () -> Unit,
  onSync: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: BrowseGenreViewModel = koinViewModel()
) {
  val genres = viewModel.genres.collectAsLazyPagingItems()
  val showSync by viewModel.showSync.collectAsStateWithLifecycle(initialValue = true)
  val sortPreference by viewModel.sortPreference.collectAsStateWithLifecycle(
    initialValue = SortPreference(GenreSortField.NAME, SortOrder.ASC)
  )

  // Handle navigation events
  LaunchedEffect(Unit) {
    viewModel.events.filterIsInstance<GenreUiMessage.OpenArtists>().collect { event ->
      onNavigateToGenreArtists(event.genre)
    }
  }

  LaunchedEffect(Unit) {
    viewModel.events.filterIsInstance<GenreUiMessage.OpenAlbums>().collect { event ->
      onNavigateToGenreAlbums(event.genre)
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
      onQueue = { queue -> viewModel.queue(queue, genre) },
      onGoToAlbums = { viewModel.goToAlbums(genre) }
    )
  }

  if (showSortSheet) {
    SortBottomSheet(
      title = stringResource(R.string.sort_title),
      options = genreSortOptions,
      selectedField = sortPreference.field,
      selectedOrder = sortPreference.order,
      onSortSelected = { field, order ->
        viewModel.updateSortPreference(GenreSortPreference(field, order))
      },
      onDismiss = onDismissSortSheet
    )
  }
}
