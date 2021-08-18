package com.kelsos.mbrc.features.playlists

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.state.domain.PlayerState
import com.kelsos.mbrc.common.state.models.PlayingPosition
import com.kelsos.mbrc.common.ui.EmptyScreen
import com.kelsos.mbrc.common.ui.RemoteTopAppBar
import com.kelsos.mbrc.common.ui.SingleLineRow
import com.kelsos.mbrc.common.ui.SwipeRefreshScreen
import com.kelsos.mbrc.common.ui.pagingDataFlow
import com.kelsos.mbrc.features.library.PlayingTrack
import com.kelsos.mbrc.features.minicontrol.MiniControl
import com.kelsos.mbrc.features.minicontrol.MiniControlViewModel
import com.kelsos.mbrc.theme.RemoteTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import org.koin.androidx.compose.getViewModel

@Composable
fun PlaylistScreen(
  openDrawer: () -> Unit,
  navigateToHome: () -> Unit,
  snackbarHostState: SnackbarHostState
) {
  val vm = getViewModel<PlaylistViewModel>()
  val miniVm = getViewModel<MiniControlViewModel>()
  val playingTrack by miniVm.playingTrack.collectAsState(initial = PlayingTrack())
  val position by miniVm.playingPosition.collectAsState(initial = PlayingPosition())
  val playingState by miniVm.playerStatus.map { it.state }.distinctUntilChanged()
    .collectAsState(initial = PlayerState.Undefined)

  PlaylistScreen(
    playlists = vm.playlists.collectAsLazyPagingItems(),
    events = vm.emitter,
    snackbarHostState = snackbarHostState,
    openDrawer = openDrawer,
    onRefresh = { vm.reload() },
    play = { vm.play(it) }
  ) {
    MiniControl(
      playingTrack = playingTrack,
      position = position,
      state = playingState,
      perform = { miniVm.perform(it) },
      navigateToHome = navigateToHome
    )
  }
}

@Composable
fun PlaylistScreen(
  playlists: LazyPagingItems<Playlist>,
  events: Flow<PlaylistUiMessages>,
  snackbarHostState: SnackbarHostState,
  openDrawer: () -> Unit,
  onRefresh: () -> Unit,
  play: (path: String) -> Unit,
  content: @Composable () -> Unit,
) = Surface {
  Column(modifier = Modifier.fillMaxSize()) {
    RemoteTopAppBar(openDrawer = openDrawer) {}

    val messages = mapOf(
      PlaylistUiMessages.RefreshSuccess to stringResource(id = R.string.playlists__refresh_success),
      PlaylistUiMessages.RefreshFailed to stringResource(id = R.string.playlists__refresh_failed)
    )

    LaunchedEffect(snackbarHostState) {
      events.collect { message ->
        snackbarHostState.showSnackbar(messages.getValue(message))
      }
    }

    val isRefreshing = playlists.loadState.refresh is LoadState.Loading

    if (playlists.itemCount == 0) {
      EmptyScreen(
        modifier = Modifier.weight(1f),
        text = stringResource(id = R.string.playlists_list_empty),
        imageVector = Icons.Filled.QueueMusic,
        contentDescription = stringResource(id = R.string.playlists_list_empty)
      ) {
        TextButton(onClick = { onRefresh() }) {
          Text(text = stringResource(id = R.string.press_to_sync))
        }
      }
    } else {
      SwipeRefreshScreen(
        modifier = Modifier.weight(1f),
        items = playlists,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh
      ) {
        PlaylistRow(playlist = it, clicked = play)
      }
    }

    Row {
      content()
    }
  }
}

@Composable
fun PlaylistRow(playlist: Playlist?, clicked: (path: String) -> Unit) =
  SingleLineRow(text = playlist?.name) {
    playlist?.let { it ->
      clicked(it.url)
    }
  }

@Preview(device = Devices.PIXEL_4)
@Composable
fun PlaylistScreenPreview() {
  RemoteTheme {
    PlaylistScreen(
      playlists = pagingDataFlow(
        Playlist(
          name = "tracks",
          url = "",
          id = 1
        )
      ).collectAsLazyPagingItems(),
      events = emptyFlow(),
      snackbarHostState = SnackbarHostState(),
      play = {},
      openDrawer = {},
      onRefresh = { }
    ) {
      MiniControl(
        playingTrack = PlayingTrack(
          artist = "Caravan Palace",
          album = "Panic",
          title = "Rock It for Me",
          year = "2008"
        ),
        position = PlayingPosition(63000, 174000),
        state = PlayerState.Playing,
        perform = {},
        navigateToHome = {}
      )
    }
  }
}
