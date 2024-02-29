package com.kelsos.mbrc.features.playlists

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import com.kelsos.mbrc.app.LocalSnackbarHostState
import com.kelsos.mbrc.common.state.domain.PlayerState
import com.kelsos.mbrc.common.state.models.PlayingPosition
import com.kelsos.mbrc.common.ui.EmptyScreen
import com.kelsos.mbrc.common.ui.RemoteTopAppBar
import com.kelsos.mbrc.common.ui.SingleLineRow
import com.kelsos.mbrc.common.ui.SwipeRefreshScreen
import com.kelsos.mbrc.common.ui.SwipeScreenContent
import com.kelsos.mbrc.common.ui.pagingDataFlow
import com.kelsos.mbrc.features.library.PlayingTrack
import com.kelsos.mbrc.features.minicontrol.MiniControl
import com.kelsos.mbrc.features.minicontrol.MiniControlState
import com.kelsos.mbrc.features.minicontrol.MiniControlViewModel
import com.kelsos.mbrc.theme.RemoteTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.koin.androidx.compose.getViewModel

@Composable
fun PlaylistScreen(
  openDrawer: () -> Unit,
  navigateToHome: () -> Unit
) {
  val vm = getViewModel<PlaylistViewModel>()
  val miniVm = getViewModel<MiniControlViewModel>()
  val vmState by miniVm.state.collectAsState(initial = MiniControlState())

  PlaylistScreen(
    playlists = vm.playlists.collectAsLazyPagingItems(),
    events = vm.emitter,
    openDrawer = openDrawer,
    actions = vm.actions
  ) {
    MiniControl(
      vmState = vmState,
      perform = { miniVm.perform(it) },
      navigateToHome = navigateToHome
    )
  }
}

@Composable
fun PlaylistScreen(
  playlists: LazyPagingItems<Playlist>,
  events: Flow<PlaylistUiMessages>,
  openDrawer: () -> Unit,
  actions: IPlaylistActions,
  content: @Composable () -> Unit
) = Surface {
  Column(modifier = Modifier.fillMaxSize()) {
    RemoteTopAppBar(openDrawer = openDrawer) {}

    val messages = mapOf(
      PlaylistUiMessages.RefreshSuccess to stringResource(id = R.string.playlists__refresh_success),
      PlaylistUiMessages.RefreshFailed to stringResource(id = R.string.playlists__refresh_failed)
    )

    val snackbarHostState = LocalSnackbarHostState.current
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
        TextButton(onClick = { actions.reload() }) {
          Text(text = stringResource(id = R.string.press_to_sync))
        }
      }
    } else {
      SwipeRefreshScreen(
        modifier = Modifier.weight(1f),
        content = SwipeScreenContent(
          items = playlists,
          isRefreshing = isRefreshing,
          key = { it.id },
          onRefresh = actions::reload
        )
      ) {
        PlaylistRow(playlist = it, clicked = actions::play)
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
      openDrawer = {},
      actions = object : IPlaylistActions {
        override fun play(path: String) = Unit
        override fun reload() = Unit
      }
    ) {
      MiniControl(
        vmState = MiniControlState(
          playingTrack = PlayingTrack(
            artist = "Caravan Palace",
            album = "Panic",
            title = "Rock It for Me",
            year = "2008"
          ),
          playingPosition = PlayingPosition(63000, 174000),
          playingState = PlayerState.Playing
        ),
        perform = {},
        navigateToHome = {}
      )
    }
  }
}
