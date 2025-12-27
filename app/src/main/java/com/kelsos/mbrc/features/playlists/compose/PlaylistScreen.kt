package com.kelsos.mbrc.features.playlists.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.ui.compose.SingleLineRow
import com.kelsos.mbrc.common.ui.compose.SwipeRefreshScreen
import com.kelsos.mbrc.features.playlists.Playlist
import com.kelsos.mbrc.features.playlists.PlaylistUiMessages
import com.kelsos.mbrc.features.playlists.PlaylistViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlaylistScreen(
  modifier: Modifier = Modifier,
  snackbarHostState: SnackbarHostState,
  viewModel: PlaylistViewModel = koinViewModel()
) {
  val playlists = viewModel.playlists.collectAsLazyPagingItems()
  var isRefreshing by remember { mutableStateOf(false) }
  val refreshFailedMessage = stringResource(R.string.playlists_load_failed)
  val refreshSuccessMessage = stringResource(R.string.playlists_load_success)
  val networkUnavailableMessage = stringResource(R.string.connection_error_network_unavailable)
  val playFailedMessage = stringResource(R.string.radio__play_failed)

  LaunchedEffect(Unit) {
    // Trigger initial load without user message
    viewModel.actions.reload(showUserMessage = false)
  }

  LaunchedEffect(Unit) {
    viewModel.events.collect { event ->
      val message = when (event) {
        is PlaylistUiMessages.RefreshFailed -> {
          isRefreshing = false
          refreshFailedMessage
        }

        is PlaylistUiMessages.RefreshSuccess -> {
          isRefreshing = false
          refreshSuccessMessage
        }

        is PlaylistUiMessages.NetworkUnavailable -> {
          isRefreshing = false
          networkUnavailableMessage
        }

        is PlaylistUiMessages.PlayFailed -> playFailedMessage
      }

      snackbarHostState.showSnackbar(
        message = message,
        duration = SnackbarDuration.Short
      )
    }
  }

  SwipeRefreshScreen(
    items = playlists,
    isRefreshing = isRefreshing,
    onRefresh = {
      isRefreshing = true
      viewModel.actions.reload()
    },
    modifier = modifier.fillMaxSize(),
    emptyMessage = stringResource(R.string.playlists_list_empty),
    emptyIcon = Icons.AutoMirrored.Filled.QueueMusic,
    key = { it.id }
  ) { playlist ->
    PlaylistItem(
      playlist = playlist,
      onPlay = { viewModel.actions.play(it.url) }
    )
  }
}

@Composable
internal fun PlaylistItem(
  playlist: Playlist,
  onPlay: (Playlist) -> Unit,
  modifier: Modifier = Modifier
) {
  SingleLineRow(
    text = playlist.name,
    onClick = { onPlay(playlist) },
    modifier = modifier,
    leadingContent = {
      Icon(
        imageVector = Icons.AutoMirrored.Filled.QueueMusic,
        contentDescription = null,
        modifier = Modifier.size(24.dp),
        tint = MaterialTheme.colorScheme.primary
      )
    },
    trailingContent = {
      IconButton(onClick = { onPlay(playlist) }) {
        Icon(
          imageVector = Icons.Default.PlayArrow,
          contentDescription = stringResource(R.string.action_play),
          tint = MaterialTheme.colorScheme.primary
        )
      }
    }
  )
}
