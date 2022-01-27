@file:OptIn(ExperimentalMaterialApi::class)

package com.kelsos.mbrc.features.radio

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.kelsos.mbrc.R
import com.kelsos.mbrc.app.LocalSnackbarHostState
import com.kelsos.mbrc.common.state.domain.PlayerState
import com.kelsos.mbrc.common.state.models.PlayingPosition
import com.kelsos.mbrc.common.ui.EmptyScreen
import com.kelsos.mbrc.common.ui.RemoteTopAppBar
import com.kelsos.mbrc.common.ui.SingleLineRow
import com.kelsos.mbrc.common.ui.pagingDataFlow
import com.kelsos.mbrc.features.library.PlayingTrack
import com.kelsos.mbrc.features.minicontrol.MiniControl
import com.kelsos.mbrc.features.minicontrol.MiniControlState
import com.kelsos.mbrc.features.minicontrol.MiniControlViewModel
import com.kelsos.mbrc.theme.RemoteTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.koin.androidx.compose.koinViewModel

@Composable
fun RadioScreen(
  openDrawer: () -> Unit,
  navigateToHome: () -> Unit,
) {
  val vm = koinViewModel<RadioViewModel>()
  val miniVm = koinViewModel<MiniControlViewModel>()
  val vmState by miniVm.state.collectAsState(initial = MiniControlState())

  RadioScreen(
    openDrawer = openDrawer,
    stations = vm.radios.collectAsLazyPagingItems(),
    events = vm.emitter,
    actions = vm.actions,
  ) {
    MiniControl(
      vmState = vmState,
      perform = { miniVm.perform(it) },
      navigateToHome = navigateToHome,
    )
  }
}

@Composable
private fun RadioScreen(
  openDrawer: () -> Unit,
  stations: LazyPagingItems<RadioStation>,
  events: Flow<RadioUiMessages>,
  actions: RadioActions,
  content: @Composable () -> Unit,
) {
  val messages =
    mapOf(
      RadioUiMessages.QueueFailed to stringResource(id = R.string.radio__queue_failed),
      RadioUiMessages.QueueSuccess to stringResource(id = R.string.radio__queue_success),
      RadioUiMessages.NetworkError to stringResource(id = R.string.radio__queue_network_error),
      RadioUiMessages.RefreshSuccess to stringResource(id = R.string.radio__refresh_success),
      RadioUiMessages.RefreshFailed to stringResource(id = R.string.radio__refresh_failed),
    )

  val snackbarHostState = LocalSnackbarHostState.current
  LaunchedEffect(snackbarHostState) {
    events.collect { message ->
      snackbarHostState.showSnackbar(messages.getValue(message))
    }
  }

  Column(modifier = Modifier.fillMaxSize()) {
    RemoteTopAppBar(openDrawer = openDrawer) {}

    val isRefreshing = stations.loadState.refresh is LoadState.Loading
    val pullRefreshState = rememberPullRefreshState(isRefreshing, { actions.reload() })

    if (stations.itemCount == 0) {
      EmptyScreen(
        modifier = Modifier.weight(1f),
        text = stringResource(id = R.string.radio__no_radio_stations),
        imageVector = Icons.Filled.Radio,
        contentDescription = stringResource(id = R.string.radio__empty_icon_content_description),
      ) {
        TextButton(onClick = { actions.reload() }) {
          Text(text = stringResource(id = R.string.press_to_sync))
        }
      }
    } else {
      Box(Modifier.pullRefresh(pullRefreshState)) {
        RadioStationContent(modifier = Modifier.fillMaxSize(1f), stations = stations, play = {
          actions.play(it)
        })
        PullRefreshIndicator(isRefreshing, pullRefreshState, Modifier.align(Alignment.TopCenter))
      }
    }

    Row {
      content()
    }
  }
}

@Composable
fun RadioStationContent(
  modifier: Modifier = Modifier,
  stations: LazyPagingItems<RadioStation>,
  play: (path: String) -> Unit,
) {
  val listState = rememberLazyListState()

  LazyColumn(
    state = listState,
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
    modifier = modifier.fillMaxWidth(),
  ) {
    items(count = stations.itemCount, key = stations.itemKey { it.id }) { index ->
      val station = stations[index]
      if (station !== null) {
        RadioStationRow(station = station, play = play)
      }
    }
  }
}

@Composable
fun RadioStationRow(
  station: RadioStation?,
  play: (path: String) -> Unit,
) = SingleLineRow(text = station?.name) {
  station?.let {
    play(it.url)
  }
}

@Preview
@Composable
fun PreviewRadioScreen() {
  RemoteTheme {
    RadioScreen(
      openDrawer = { },
      stations =
        pagingDataFlow(
          RadioStation(name = "Radio 1", url = "", id = 1),
        ).collectAsLazyPagingItems(),
      events = emptyFlow(),
      actions =
        object : RadioActions {
          override fun play(path: String) = Unit

          override fun reload() = Unit
        },
      {
        MiniControl(
          vmState =
            MiniControlState(
              playingTrack =
                PlayingTrack(
                  artist = "Caravan Palace",
                  album = "Panic",
                  title = "Rock It for Me",
                  year = "2008",
                ),
              playingPosition = PlayingPosition(63000, 174000),
              playingState = PlayerState.Playing,
            ),
          perform = {},
          navigateToHome = {},
        )
      },
    )
  }
}
