package com.kelsos.mbrc.features.minicontrol

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kelsos.mbrc.common.state.domain.PlayerState
import com.kelsos.mbrc.common.state.models.PlayingPosition
import com.kelsos.mbrc.common.ui.TrackCover
import com.kelsos.mbrc.features.library.PlayingTrack
import com.kelsos.mbrc.theme.DarkBackground
import com.kelsos.mbrc.theme.RemoteTheme

@Composable
fun MiniControl(
  playingTrack: PlayingTrack,
  position: PlayingPosition,
  state: PlayerState,
  perform: (action: MiniControlAction) -> Unit = {},
  navigateToHome: () -> Unit = {}
) = Surface(Modifier.background(color = DarkBackground)) {
  Column(
    modifier = Modifier
      .height(50.dp)
      .fillMaxWidth()
  ) {
    Row(modifier = Modifier.height(2.dp)) {
      LinearProgressIndicator(
        progress = position.current.toFloat().div(position.total),
        modifier = Modifier.fillMaxWidth()
      )
    }
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 2.dp)
        .height(48.dp)
    ) {
      TrackCover(
        coverUrl = playingTrack.coverUrl,
        modifier = Modifier
          .size(44.dp)
          .padding(2.dp),
        cornerRadius = 2.dp
      )
      PlayingTrackInfo(
        modifier = Modifier.weight(1f),
        navigateToHome = navigateToHome,
        playingTrack = playingTrack
      )
      PlayerControls(perform, state)
    }
  }
}

@Composable
private fun PlayingTrackInfo(
  modifier: Modifier = Modifier,
  navigateToHome: () -> Unit,
  playingTrack: PlayingTrack
) {
  Column(
    modifier = modifier
      .padding(horizontal = 4.dp, vertical = 2.dp)
      .clickable {
        navigateToHome()
      },
    verticalArrangement = Arrangement.Center
  ) {
    Text(text = playingTrack.title, style = MaterialTheme.typography.body1)
    Text(
      text = playingTrack.artist,
      style = MaterialTheme.typography.subtitle2,
      color = MaterialTheme.colors.onSurface.copy(0.7f)
    )
  }
}

@Composable
private fun PlayerControls(
  perform: (action: MiniControlAction) -> Unit,
  state: PlayerState
) = Column {
  Row {
    IconButton(onClick = { perform(MiniControlAction.PlayPrevious) }) {
      Icon(imageVector = Icons.Filled.SkipPrevious, contentDescription = null)
    }
    PlayPauseButton(perform, state)
    IconButton(onClick = { perform(MiniControlAction.PlayNext) }) {
      Icon(imageVector = Icons.Filled.SkipNext, contentDescription = null)
    }
  }
}

@Composable
private fun PlayPauseButton(
  perform: (action: MiniControlAction) -> Unit,
  state: PlayerState
) = IconButton(onClick = { perform(MiniControlAction.PlayPause) }) {
  val imageVector = if (state == PlayerState.Playing) {
    Icons.Filled.Pause
  } else {
    Icons.Filled.PlayArrow
  }
  Icon(
    imageVector = imageVector,
    contentDescription = null
  )
}

@Preview
@Composable
fun MiniControlPreview() {
  RemoteTheme {
    Row(modifier = Modifier.fillMaxSize()) {
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
