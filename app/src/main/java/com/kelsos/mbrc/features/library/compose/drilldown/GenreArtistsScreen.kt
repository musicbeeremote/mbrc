package com.kelsos.mbrc.features.library.compose.drilldown

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
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
import com.kelsos.mbrc.features.library.artists.Artist
import com.kelsos.mbrc.features.library.artists.ArtistUiMessage
import com.kelsos.mbrc.features.library.artists.GenreArtistsViewModel
import com.kelsos.mbrc.features.library.compose.components.ArtistListItem
import com.kelsos.mbrc.features.queue.Queue
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import org.koin.androidx.compose.koinViewModel

@Composable
fun GenreArtistsScreen(
  genreId: Long,
  genreName: String,
  onNavigateBack: () -> Unit,
  onNavigateToArtistAlbums: (Artist) -> Unit,
  snackbarHostState: SnackbarHostState,
  onScreenConfigChange: (ScreenConfig) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: GenreArtistsViewModel = koinViewModel()
) {
  val artists = viewModel.artists.collectAsLazyPagingItems()

  LaunchedEffect(genreId, genreName) {
    onScreenConfigChange(
      ScreenConfig(
        topBar = {
          RemoteTopAppBar(
            title = genreName,
            navigationIcon = { BackNavigationIcon(onClick = onNavigateBack) }
          )
        }
      )
    )
    viewModel.load(genreId)
  }

  LaunchedEffect(Unit) {
    viewModel.events.filterIsInstance<ArtistUiMessage.OpenArtistAlbums>().collect { event ->
      onNavigateToArtistAlbums(event.artist)
    }
  }

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

  QueueResultEffect(
    queueResults = queueResults,
    snackbarHostState = snackbarHostState
  )

  PagingListScreen(
    items = artists,
    modifier = modifier.fillMaxSize(),
    emptyMessage = stringResource(R.string.artists_list_empty),
    emptyIcon = Icons.Default.Person,
    key = { it.id }
  ) { artist ->
    ArtistListItem(
      artist = artist,
      onClick = { viewModel.queue(Queue.Default, artist) },
      onQueue = { queue -> viewModel.queue(queue, artist) }
    )
  }
}
