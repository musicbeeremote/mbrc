package com.kelsos.mbrc.feature.library.compose.drilldown

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.collectAsLazyPagingItems
import com.kelsos.mbrc.core.common.utilities.AppError
import com.kelsos.mbrc.core.common.utilities.Outcome
import com.kelsos.mbrc.core.data.library.album.Album
import com.kelsos.mbrc.core.queue.Queue
import com.kelsos.mbrc.core.ui.compose.NavigationIconType
import com.kelsos.mbrc.core.ui.compose.PagingListScreen
import com.kelsos.mbrc.core.ui.compose.QueueResultEffect
import com.kelsos.mbrc.core.ui.compose.ScreenScaffold
import com.kelsos.mbrc.feature.library.R
import com.kelsos.mbrc.feature.library.albums.AlbumUiMessage
import com.kelsos.mbrc.feature.library.albums.GenreAlbumsViewModel
import com.kelsos.mbrc.feature.library.compose.components.AlbumListItem
import com.kelsos.mbrc.feature.minicontrol.MiniControl
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import org.koin.androidx.compose.koinViewModel

@Composable
fun GenreAlbumsScreen(
  genreId: Long,
  genreName: String,
  onNavigateBack: () -> Unit,
  onNavigateToAlbumTracks: (Album) -> Unit,
  onNavigateToPlayer: () -> Unit,
  snackbarHostState: SnackbarHostState,
  modifier: Modifier = Modifier,
  viewModel: GenreAlbumsViewModel = koinViewModel()
) {
  val albums = viewModel.albums.collectAsLazyPagingItems()

  // Load genre albums
  LaunchedEffect(genreId) {
    viewModel.load(genreId)
  }

  LaunchedEffect(Unit) {
    viewModel.events.filterIsInstance<AlbumUiMessage.OpenAlbumTracks>().collect { event ->
      onNavigateToAlbumTracks(event.album)
    }
  }

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

  QueueResultEffect(
    queueResults = queueResults,
    snackbarHostState = snackbarHostState
  )

  ScreenScaffold(
    title = genreName,
    snackbarHostState = snackbarHostState,
    navigationIcon = NavigationIconType.Back(onNavigateBack),
    modifier = modifier
  ) { paddingValues ->
    Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
      PagingListScreen(
        items = albums,
        modifier = Modifier.weight(1f),
        emptyMessage = stringResource(R.string.albums_list_empty),
        emptyIcon = Icons.Default.Album,
        key = { it.id }
      ) { album ->
        AlbumListItem(
          album = album,
          onClick = { viewModel.queue(Queue.Default, album) },
          onQueue = { queue -> viewModel.queue(queue, album) }
        )
      }

      MiniControl(
        onNavigateToPlayer = onNavigateToPlayer,
        snackbarHostState = snackbarHostState
      )
    }
  }
}
