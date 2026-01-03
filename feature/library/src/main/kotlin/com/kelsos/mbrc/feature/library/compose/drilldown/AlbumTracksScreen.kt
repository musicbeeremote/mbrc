package com.kelsos.mbrc.feature.library.compose.drilldown

import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.kelsos.mbrc.core.common.utilities.AppError
import com.kelsos.mbrc.core.common.utilities.Outcome
import com.kelsos.mbrc.core.queue.Queue
import com.kelsos.mbrc.core.ui.compose.EmptyScreen
import com.kelsos.mbrc.core.ui.compose.LoadingScreen
import com.kelsos.mbrc.core.ui.compose.NavigationIconType
import com.kelsos.mbrc.core.ui.compose.QueueResultEffect
import com.kelsos.mbrc.core.ui.compose.ScreenScaffold
import com.kelsos.mbrc.feature.library.R
import com.kelsos.mbrc.feature.library.albums.AlbumInfo
import com.kelsos.mbrc.feature.library.compose.components.AlbumCoverByKey
import com.kelsos.mbrc.feature.library.compose.components.TrackListItem
import com.kelsos.mbrc.feature.library.tracks.AlbumTracksViewModel
import com.kelsos.mbrc.feature.library.tracks.TrackUiMessage
import com.kelsos.mbrc.feature.minicontrol.MiniControl
import kotlinx.coroutines.flow.map
import org.koin.androidx.compose.koinViewModel

@Composable
fun AlbumTracksScreen(
  albumInfo: AlbumInfo,
  onNavigateBack: () -> Unit,
  onNavigateToPlayer: () -> Unit,
  snackbarHostState: SnackbarHostState,
  modifier: Modifier = Modifier,
  viewModel: AlbumTracksViewModel = koinViewModel()
) {
  val tracks = viewModel.tracks.collectAsLazyPagingItems()
  val title = albumInfo.album.ifEmpty { stringResource(R.string.non_album_tracks) }

  // Load album tracks
  LaunchedEffect(albumInfo) {
    viewModel.load(albumInfo)
  }

  // Handle queue results
  val queueResults = remember {
    viewModel.events.map { event ->
      when (event) {
        is TrackUiMessage.QueueSuccess -> Outcome.Success(event.tracksCount)
        is TrackUiMessage.QueueFailed -> Outcome.Failure(AppError.OperationFailed)
        is TrackUiMessage.NetworkUnavailable -> Outcome.Failure(AppError.NetworkUnavailable)
      }
    }
  }

  QueueResultEffect(
    queueResults = queueResults,
    snackbarHostState = snackbarHostState
  )

  ScreenScaffold(
    title = title,
    snackbarHostState = snackbarHostState,
    navigationIcon = NavigationIconType.Back(onNavigateBack),
    modifier = modifier
  ) { paddingValues ->
    Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
      when (tracks.loadState.refresh) {
        is LoadState.Loading if tracks.itemCount == 0 -> {
          LoadingScreen(modifier = Modifier.weight(1f))
        }

        is LoadState.NotLoading if tracks.itemCount == 0 -> {
          EmptyScreen(
            message = stringResource(R.string.common_empty_no_tracks),
            icon = Icons.Default.MusicNote,
            modifier = Modifier.weight(1f)
          )
        }

        else -> {
          val listState = rememberLazyListState()
          LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            flingBehavior = ScrollableDefaults.flingBehavior()
          ) {
            item(
              key = "album_header",
              contentType = "header"
            ) {
              AlbumHeaderHorizontal(
                albumInfo = albumInfo,
                onPlayClick = { viewModel.queueAlbum(albumInfo) },
                modifier = Modifier.fillMaxWidth()
              )
            }

            items(
              count = tracks.itemCount,
              key = { index -> tracks.peek(index)?.id ?: index },
              contentType = { "track_item" }
            ) { index ->
              tracks[index]?.let { track ->
                TrackListItem(
                  track = track,
                  onClick = { viewModel.queue(Queue.Default, track) },
                  onQueue = { queue -> viewModel.queue(queue, track) },
                  showCover = false,
                  showAlbum = false
                )
              }
            }
          }
        }
      }

      MiniControl(
        onNavigateToPlayer = onNavigateToPlayer,
        snackbarHostState = snackbarHostState
      )
    }
  }
}

@Composable
private fun AlbumHeaderHorizontal(
  albumInfo: AlbumInfo,
  onPlayClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .height(156.dp)
      .padding(16.dp),
    verticalAlignment = Alignment.Top
  ) {
    Card(
      elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
      shape = RoundedCornerShape(8.dp)
    ) {
      AlbumCoverByKey(
        artist = albumInfo.artist,
        album = albumInfo.album,
        size = 124.dp
      )
    }
    Spacer(modifier = Modifier.width(16.dp))
    Column(
      modifier = Modifier
        .weight(1f)
        .height(124.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Column {
        Text(
          text = albumInfo.album.ifEmpty { stringResource(R.string.non_album_tracks) },
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = albumInfo.artist.ifEmpty { stringResource(R.string.unknown_artist) },
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
      FilledIconButton(
        onClick = onPlayClick,
        colors = IconButtonDefaults.filledIconButtonColors(
          containerColor = MaterialTheme.colorScheme.primary
        )
      ) {
        Icon(
          imageVector = Icons.Default.PlayArrow,
          contentDescription = stringResource(R.string.menu_play)
        )
      }
    }
  }
}
