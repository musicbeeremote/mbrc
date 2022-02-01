package com.kelsos.mbrc.features.player

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.state.domain.PlayerState
import com.kelsos.mbrc.common.state.domain.Repeat
import com.kelsos.mbrc.common.state.models.PlayerStatusModel
import com.kelsos.mbrc.common.state.models.PlayingPosition
import com.kelsos.mbrc.common.state.models.TrackRating
import com.kelsos.mbrc.common.ui.TrackCover
import com.kelsos.mbrc.events.ShuffleMode
import com.kelsos.mbrc.features.library.PlayingTrack
import com.kelsos.mbrc.theme.RemoteTheme
import org.koin.androidx.compose.getViewModel

@Composable
fun PlayerScreen(openDrawer: () -> Unit, share: (track: PlayingTrack) -> Unit) {
  val vm = getViewModel<PlayerViewModel>()
  val vmState by vm.state.collectAsState(initial = PlayerStateModel())
  val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

  RemoteTheme {
    if (isLandscape) {
      PlayerScreenLandscape(
        state = vmState,
        perform = { vm.interact(it) },
        share = share,
        openDrawer
      )
    } else {
      PlayerScreenPortrait(
        state = vmState,
        perform = { vm.interact(it) },
        share = share,
        openDrawer
      )
    }
  }
}

@Composable
fun PlayerScreenLandscape(
  state: PlayerStateModel,
  perform: (action: PlayerAction) -> Unit,
  share: (track: PlayingTrack) -> Unit,
  openDrawer: () -> Unit
) = Surface() {
  Column(modifier = Modifier.fillMaxSize()) {
    PlayerScreenAppBar(state, openDrawer, perform, share)
    Row(modifier = Modifier.fillMaxWidth()) {
      Column(modifier = Modifier.fillMaxWidth(fraction = 0.6f)) {
        Spacer(modifier = Modifier.weight(weight = 2f))
        TrackInfo(state.playingTrack)
        Spacer(modifier = Modifier.weight(weight = 1f))
        ProgressControl(state.playingPosition, perform)
        Spacer(modifier = Modifier.weight(weight = 1f))
        PlayerActions(state.playerStatus, perform)
      }
      TrackCover(
        modifier = Modifier
          .padding(16.dp)
          .fillMaxSize(),
        coverUrl = state.playingTrack.coverUrl,
      )
    }
  }
}

@Composable
fun PlayerScreenPortrait(
  state: PlayerStateModel,
  perform: (action: PlayerAction) -> Unit,
  share: (track: PlayingTrack) -> Unit,
  openDrawer: () -> Unit
) = Surface {
  Column(modifier = Modifier.fillMaxSize()) {
    PlayerScreenAppBar(state, openDrawer, perform, share)
    TrackCover(
      modifier = Modifier
        .padding(16.dp)
        .fillMaxSize(),
      coverUrl = state.playingTrack.coverUrl,
    )
    ProgressControl(state.playingPosition, perform)
    Spacer(modifier = Modifier.weight(2f))
    TrackInfo(state.playingTrack)
    Spacer(modifier = Modifier.weight(1f))
    PlayerActions(state.playerStatus, perform)
  }
}

@Composable
private fun ProgressControl(
  playingPosition: PlayingPosition,
  perform: (action: PlayerAction) -> Unit
) = Row(
  modifier = Modifier
    .fillMaxWidth()
    .padding(horizontal = 16.dp),
  verticalAlignment = Alignment.CenterVertically,
  horizontalArrangement = Arrangement.SpaceBetween
) {
  Text(text = playingPosition.currentMinutes, style = MaterialTheme.typography.caption)
  Slider(
    value = playingPosition.current.toFloat(),
    onValueChange = {
      perform(PlayerAction.Seek(it.toInt()))
    },
    valueRange = 0f..playingPosition.total.toFloat(),
    modifier = Modifier.fillMaxWidth(fraction = 0.8f)
  )
  Text(text = playingPosition.totalMinutes, style = MaterialTheme.typography.caption)
}

@Composable
private fun TrackInfo(
  playingTrack: PlayingTrack
) = Row(
  modifier = Modifier
    .padding(16.dp)
    .fillMaxWidth(),
  horizontalArrangement = Arrangement.SpaceBetween
) {
  Column(modifier = Modifier.weight(weight = 1f)) {
    Text(
      text = playingTrack.title,
      style = MaterialTheme.typography.h6,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
    Text(
      text = playingTrack.artistInfo(),
      style = MaterialTheme.typography.subtitle2,
      color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
  }
  Column(
    modifier = Modifier
      .wrapContentWidth()
      .padding(start = 8.dp)
  ) {
    VolumeControl()
  }
}

@Composable
private fun VolumeControl() {
  var showDialog by remember { mutableStateOf(false) }
  IconButton(onClick = { showDialog = true }) {
    Icon(
      imageVector = Icons.Filled.VolumeUp,
      contentDescription = stringResource(id = R.string.main_button_mute_description)
    )
  }
  VolumeDialog(
    showDialog = showDialog,
    dismiss = {
      showDialog = false
    }
  )
}

@Preview(device = Devices.PIXEL_4)
@Composable
fun PlayerScreenPortaitPreview() {
  RemoteTheme {
    PlayerScreenPortrait(
      state = PlayerStateModel(
        playingTrack = PlayingTrack(
          artist = "Caravan Palace",
          album = "Panic",
          title = "Rock It for Me",
          year = "2008"
        ),
        playingPosition = PlayingPosition(63000, 174000),
        playerStatus = PlayerStatusModel(
          mute = true,
          state = PlayerState.Paused,
          repeat = Repeat.One,
          scrobbling = false,
          shuffle = ShuffleMode.AutoDJ,
          volume = 10
        ),
        trackRating = TrackRating(lfmRating = LfmRating.Loved, rating = 4.5f),
      ),
      perform = {},
      share = {},
      openDrawer = {}
    )
  }
}

@Preview(device = Devices.PIXEL_4)
@Composable
fun PlayerScreenLandscapePreview() {
  RemoteTheme {
    PlayerScreenLandscape(
      state = PlayerStateModel(
        playingTrack = PlayingTrack(
          artist = "Caravan Palace",
          album = "Panic",
          title = "Rock It for Me",
          year = "2008"
        ),
        playingPosition = PlayingPosition(63000, 174000),
        playerStatus = PlayerStatusModel(
          mute = true,
          state = PlayerState.Paused,
          repeat = Repeat.One,
          scrobbling = false,
          shuffle = ShuffleMode.AutoDJ,
          volume = 10
        ),
        trackRating = TrackRating(lfmRating = LfmRating.Loved, rating = 4.5f),
      ),
      perform = {},
      share = {},
      openDrawer = {}
    )
  }
}
