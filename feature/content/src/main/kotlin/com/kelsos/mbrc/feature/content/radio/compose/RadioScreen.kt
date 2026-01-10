package com.kelsos.mbrc.feature.content.radio.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.kelsos.mbrc.core.data.radio.RadioStation
import com.kelsos.mbrc.core.ui.R as CoreUiR
import com.kelsos.mbrc.core.ui.compose.AudioBarsIndicator
import com.kelsos.mbrc.core.ui.compose.ScreenScaffold
import com.kelsos.mbrc.core.ui.compose.SingleLineRow
import com.kelsos.mbrc.core.ui.compose.SwipeRefreshScreen
import com.kelsos.mbrc.feature.content.R
import com.kelsos.mbrc.feature.content.radio.RadioUiMessages
import com.kelsos.mbrc.feature.content.radio.RadioViewModel
import com.kelsos.mbrc.feature.minicontrol.MiniControl
import org.koin.androidx.compose.koinViewModel

@Composable
fun RadioScreen(
  onNavigateToPlayer: () -> Unit,
  snackbarHostState: SnackbarHostState,
  onOpenDrawer: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: RadioViewModel = koinViewModel()
) {
  val stations = viewModel.state.radios.collectAsLazyPagingItems()
  val playingTrack by viewModel.playingTrack.collectAsState()
  var isRefreshing by remember { mutableStateOf(false) }

  val queueFailedMessage = stringResource(R.string.radio__play_failed)
  val queueSuccessMessage = stringResource(R.string.radio__play_successful)
  val refreshFailedMessage = stringResource(R.string.radio__loading_failed)
  val refreshSuccessMessage = stringResource(R.string.radio__loading_success)
  val networkErrorMessage = stringResource(CoreUiR.string.connection_error_network_unavailable)
  val title = stringResource(R.string.nav_radio)

  LaunchedEffect(Unit) {
    // Trigger initial load without user message
    viewModel.actions.reload(showUserMessage = false)
  }

  LaunchedEffect(Unit) {
    viewModel.state.events.collect { event ->
      val message = when (event) {
        is RadioUiMessages.QueueFailed -> queueFailedMessage

        is RadioUiMessages.QueueSuccess -> queueSuccessMessage

        is RadioUiMessages.RefreshFailed -> {
          isRefreshing = false
          refreshFailedMessage
        }

        is RadioUiMessages.RefreshSuccess -> {
          isRefreshing = false
          refreshSuccessMessage
        }

        is RadioUiMessages.NetworkUnavailable -> {
          isRefreshing = false
          networkErrorMessage
        }
      }

      snackbarHostState.showSnackbar(
        message = message,
        duration = SnackbarDuration.Short
      )
    }
  }

  ScreenScaffold(
    title = title,
    snackbarHostState = snackbarHostState,
    onOpenDrawer = onOpenDrawer,
    modifier = modifier
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      SwipeRefreshScreen(
        items = stations,
        isRefreshing = isRefreshing,
        onRefresh = {
          isRefreshing = true
          viewModel.actions.reload()
        },
        modifier = Modifier.weight(1f),
        emptyMessage = stringResource(R.string.radio__no_radio_stations),
        emptyIcon = Icons.Default.Radio,
        key = { it.id }
      ) { station ->
        RadioStationItem(
          station = station,
          isPlaying = station.url == playingTrack.path,
          onPlay = { viewModel.actions.play(it.url) }
        )
      }

      MiniControl(
        onNavigateToPlayer = onNavigateToPlayer,
        snackbarHostState = snackbarHostState
      )
    }
  }
}

@Composable
fun RadioStationItem(
  station: RadioStation,
  isPlaying: Boolean,
  onPlay: (RadioStation) -> Unit,
  modifier: Modifier = Modifier
) {
  SingleLineRow(
    text = station.name,
    onClick = { onPlay(station) },
    modifier = modifier,
    fontWeight = if (isPlaying) FontWeight.Bold else null,
    leadingContent = {
      Icon(
        imageVector = Icons.Default.Radio,
        contentDescription = null,
        modifier = Modifier.size(24.dp),
        tint = MaterialTheme.colorScheme.primary
      )
    },
    trailingContent = {
      IconButton(onClick = { onPlay(station) }) {
        if (isPlaying) {
          Box(
            modifier = Modifier.size(24.dp),
            contentAlignment = Alignment.Center
          ) {
            AudioBarsIndicator(
              color = MaterialTheme.colorScheme.primary,
              barMaxHeight = 18.dp
            )
          }
        } else {
          Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = stringResource(R.string.radio__play),
            tint = MaterialTheme.colorScheme.primary
          )
        }
      }
    }
  )
}
