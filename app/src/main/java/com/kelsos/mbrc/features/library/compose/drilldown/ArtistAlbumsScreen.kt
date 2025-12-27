package com.kelsos.mbrc.features.library.compose.drilldown

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.collectAsLazyPagingItems
import com.kelsos.mbrc.R
import com.kelsos.mbrc.app.ScreenConfig
import com.kelsos.mbrc.common.ui.compose.BackNavigationIcon
import com.kelsos.mbrc.common.ui.compose.PagingListScreen
import com.kelsos.mbrc.common.ui.compose.RemoteTopAppBar
import com.kelsos.mbrc.common.ui.helpers.QueueResult
import com.kelsos.mbrc.common.ui.helpers.QueueResultEffect
import com.kelsos.mbrc.features.library.albums.Album
import com.kelsos.mbrc.features.library.albums.AlbumUiMessage
import com.kelsos.mbrc.features.library.albums.ArtistAlbumsViewModel
import com.kelsos.mbrc.features.library.compose.components.AlbumListItem
import com.kelsos.mbrc.features.queue.Queue
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import org.koin.androidx.compose.koinViewModel

@Composable
fun ArtistAlbumsScreen(
  artistName: String,
  onNavigateBack: () -> Unit,
  onNavigateToAlbumTracks: (Album) -> Unit,
  snackbarHostState: SnackbarHostState,
  onScreenConfigChange: (ScreenConfig) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: ArtistAlbumsViewModel = koinViewModel()
) {
  val albums = viewModel.albums.collectAsLazyPagingItems()

  LaunchedEffect(artistName) {
    onScreenConfigChange(
      ScreenConfig(
        topBar = {
          RemoteTopAppBar(
            title = artistName,
            navigationIcon = { BackNavigationIcon(onClick = onNavigateBack) }
          )
        }
      )
    )
    viewModel.load(artistName)
  }

  LaunchedEffect(Unit) {
    viewModel.events.filterIsInstance<AlbumUiMessage.OpenAlbumTracks>().collect { event ->
      onNavigateToAlbumTracks(event.album)
    }
  }

  val queueResults = remember {
    viewModel.events.map { event ->
      when (event) {
        is AlbumUiMessage.QueueSuccess -> QueueResult.Success(event.tracksCount)
        is AlbumUiMessage.QueueFailed -> QueueResult.Failed
        is AlbumUiMessage.NetworkUnavailable -> QueueResult.NetworkUnavailable
        else -> null
      }
    }.filterIsInstance<QueueResult>()
  }

  QueueResultEffect(
    queueResults = queueResults,
    snackbarHostState = snackbarHostState
  )

  PagingListScreen(
    items = albums,
    modifier = modifier.fillMaxSize(),
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
}
