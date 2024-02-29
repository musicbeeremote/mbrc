package com.kelsos.mbrc.features.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.PauseCircleFilled
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.state.domain.PlayerState
import com.kelsos.mbrc.common.state.domain.Repeat
import com.kelsos.mbrc.common.state.models.PlayerStatusModel
import com.kelsos.mbrc.events.ShuffleMode

@Composable
private fun ShuffleButton(
  perform: (action: PlayerAction) -> Unit,
  playerStatus: PlayerStatusModel,
  iconSize: Dp
) = IconButton(onClick = { perform(PlayerAction.ToggleShuffle) }) {
  val imageVector = when (playerStatus.shuffle) {
    ShuffleMode.AutoDJ -> Icons.Filled.Headset
    ShuffleMode.Off,
    ShuffleMode.Shuffle -> Icons.Filled.Shuffle
  }
  val tint = when (playerStatus.shuffle) {
    ShuffleMode.Off -> MaterialTheme.colors.onSurface
    else -> MaterialTheme.colors.secondary
  }
  Icon(
    imageVector = imageVector,
    contentDescription = stringResource(id = R.string.main_button_shuffle_description),
    modifier = Modifier.size(iconSize),
    tint = tint
  )
}

@Composable
private fun NextButton(
  perform: (action: PlayerAction) -> Unit,
  iconSize: Dp
) = IconButton(onClick = { perform(PlayerAction.PlayNext) }) {
  Icon(
    imageVector = Icons.Filled.SkipNext,
    contentDescription = stringResource(id = R.string.main_button_next_description),
    modifier = Modifier.size(iconSize)
  )
}

@Composable
private fun PlayButton(
  perform: (action: PlayerAction) -> Unit,
  playerStatus: PlayerStatusModel,
  iconSize: Dp
) = IconButton(
  onClick = { perform(PlayerAction.ResumePlayOrPause) },
  modifier = Modifier.size(iconSize.times(other = 2.8f))
) {
  val imageVector = when (playerStatus.state) {
    PlayerState.Playing -> Icons.Filled.PauseCircleFilled
    else -> Icons.Filled.PlayCircleFilled
  }
  Icon(
    imageVector = imageVector,
    contentDescription = stringResource(id = R.string.main_button_play_pause_description),
    modifier = Modifier.size(iconSize.times(other = 2.8f)),
    tint = MaterialTheme.colors.secondary
  )
}

@Composable
private fun PreviousButton(
  perform: (action: PlayerAction) -> Unit,
  iconSize: Dp
) = IconButton(onClick = { perform(PlayerAction.PlayPrevious) }) {
  Icon(
    imageVector = Icons.Filled.SkipPrevious,
    contentDescription = stringResource(id = R.string.main_button_previous_description),
    modifier = Modifier.size(iconSize)
  )
}

@Composable
private fun RepeatButton(
  perform: (action: PlayerAction) -> Unit,
  playerStatus: PlayerStatusModel,
  iconSize: Dp
) = IconButton(onClick = { perform(PlayerAction.ToggleRepeat) }) {
  val icon = when (playerStatus.repeat) {
    Repeat.All,
    Repeat.None -> Icons.Filled.Repeat
    Repeat.One -> Icons.Filled.RepeatOne
  }
  val tint = when (playerStatus.repeat) {
    Repeat.All,
    Repeat.One -> MaterialTheme.colors.secondary
    Repeat.None -> MaterialTheme.colors.onSurface
  }
  Icon(
    imageVector = icon,
    contentDescription = stringResource(id = R.string.main_button_repeat_description),
    modifier = Modifier.size(iconSize),
    tint = tint
  )
}

@Composable
fun PlayerActions(
  playerStatus: PlayerStatusModel,
  perform: (action: PlayerAction) -> Unit
) = Row(
  horizontalArrangement = Arrangement.SpaceBetween,
  verticalAlignment = Alignment.CenterVertically,
  modifier = Modifier
    .padding(16.dp)
    .fillMaxWidth()
) {
  val iconSize = 24.dp
  RepeatButton(perform, playerStatus, iconSize)
  PreviousButton(perform, iconSize)
  PlayButton(perform, playerStatus, iconSize)
  NextButton(perform, iconSize)
  ShuffleButton(perform, playerStatus, iconSize)
}
