package com.kelsos.mbrc.features.lyrics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ViewHeadline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.state.domain.PlayerState
import com.kelsos.mbrc.common.state.models.PlayingPosition
import com.kelsos.mbrc.common.ui.EmptyScreen
import com.kelsos.mbrc.common.ui.RemoteTopAppBar
import com.kelsos.mbrc.features.library.PlayingTrack
import com.kelsos.mbrc.features.minicontrol.MiniControl
import com.kelsos.mbrc.features.minicontrol.MiniControlViewModel
import com.kelsos.mbrc.theme.RemoteTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.koin.androidx.compose.getViewModel

@Composable
fun LyricsScreen(openDrawer: () -> Unit, navigateToHome: () -> Unit) {
  val vm = getViewModel<LyricsViewModel>()
  val lyrics by vm.lyrics.collectAsState(initial = emptyList())

  val miniVm = getViewModel<MiniControlViewModel>()
  val playingTrack by miniVm.playingTrack.collectAsState(initial = PlayingTrack())
  val position by miniVm.playingPosition.collectAsState(initial = PlayingPosition())
  val playingState by miniVm.playerStatus.map { it.state }.distinctUntilChanged()
    .collectAsState(initial = PlayerState.Undefined)

  LyricsScreen(openDrawer, lyrics) {
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
private fun LyricsScreen(
  openDrawer: () -> Unit,
  lyrics: List<String>,
  content: @Composable () -> Unit
) = Surface {
  Column(modifier = Modifier.fillMaxSize()) {
    RemoteTopAppBar(openDrawer = openDrawer) {}

    if (lyrics.isEmpty()) {
      EmptyScreen(
        modifier = Modifier.weight(1f),
        text = stringResource(id = R.string.no_lyrics),
        imageVector = Icons.Filled.ViewHeadline,
        contentDescription = stringResource(id = R.string.lyrics__empty_icon_content_description)
      )
    } else {
      LyricsContent(modifier = Modifier.weight(1f), lyrics = lyrics)
    }
    Row {
      content()
    }
  }
}

@Composable
private fun LyricsContent(
  modifier: Modifier = Modifier,
  lyrics: List<String>
) = Row(modifier = modifier) {
  LazyColumn(
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    items(items = lyrics) {
      Text(text = it, style = MaterialTheme.typography.body1)
    }
  }
}

@Preview(device = Devices.PIXEL_4)
@Composable
fun LyricsScreenPreview() {
  RemoteTheme {
    LyricsScreen(openDrawer = {}, lyrics = listOf("line one", "two", "three", "", "five")) {
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
