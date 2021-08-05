package com.kelsos.mbrc.features.player

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Checkbox
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PauseCircleFilled
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.state.domain.PlayerState
import com.kelsos.mbrc.common.state.domain.Repeat
import com.kelsos.mbrc.common.state.models.PlayerStatusModel
import com.kelsos.mbrc.common.state.models.PlayingPosition
import com.kelsos.mbrc.common.state.models.TrackRating
import com.kelsos.mbrc.common.ui.RemoteTopAppBar
import com.kelsos.mbrc.common.ui.TrackCover
import com.kelsos.mbrc.events.ShuffleMode
import com.kelsos.mbrc.features.library.PlayingTrack
import com.kelsos.mbrc.theme.RemoteTheme
import org.koin.androidx.compose.getViewModel

@Composable
fun PlayerScreen(openDrawer: () -> Unit, share: (track: PlayingTrack) -> Unit) {
  val vm = getViewModel<PlayerViewModel>()
  val playingTrack by vm.playingTrack.collectAsState(initial = PlayingTrack())
  val playingPosition by vm.playingPosition.collectAsState(initial = PlayingPosition())
  val playerStatus by vm.playerStatus.collectAsState(initial = PlayerStatusModel())
  val trackRating by vm.playingTrackRating.collectAsState(initial = TrackRating())

  val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

  RemoteTheme {
    if (isLandscape) {
      PlayerScreenLandscape(
        playingTrack = playingTrack,
        playingPosition = playingPosition,
        playerStatus = playerStatus,
        trackRating = trackRating,
        perform = { vm.interact(it) },
        share = share,
        openDrawer
      )
    } else {
      PlayerScreenPortrait(
        playingTrack = playingTrack,
        playingPosition = playingPosition,
        playerStatus = playerStatus,
        trackRating = trackRating,
        perform = { vm.interact(it) },
        share = share,
        openDrawer
      )
    }
  }
}

@Composable
fun PlayerScreenLandscape(
  playingTrack: PlayingTrack,
  playingPosition: PlayingPosition,
  playerStatus: PlayerStatusModel,
  trackRating: TrackRating,
  perform: (action: PlayerAction) -> Unit,
  share: (track: PlayingTrack) -> Unit,
  openDrawer: () -> Unit
) = Column(modifier = Modifier.fillMaxSize()) {
  PlayerScreenAppBar(openDrawer, perform, trackRating, playerStatus, share, playingTrack)
  Row(modifier = Modifier.fillMaxWidth()) {
    Column(modifier = Modifier.fillMaxWidth(0.6f)) {
      Spacer(modifier = Modifier.weight(2f))
      TrackInfo(playingTrack)
      Spacer(modifier = Modifier.weight(1f))
      ProgressControl(playingPosition, perform)
      Spacer(modifier = Modifier.weight(1f))
      PlayerActions(playerStatus, perform)
    }
    TrackCover(
      modifier = Modifier
        .padding(16.dp)
        .fillMaxSize(),
      coverUrl = playingTrack.coverUrl,
    )
  }
}

@Composable
fun PlayerScreenPortrait(
  playingTrack: PlayingTrack,
  playingPosition: PlayingPosition,
  playerStatus: PlayerStatusModel,
  trackRating: TrackRating,
  perform: (action: PlayerAction) -> Unit,
  share: (track: PlayingTrack) -> Unit,
  openDrawer: () -> Unit
) = Column(modifier = Modifier.fillMaxSize()) {
  PlayerScreenAppBar(openDrawer, perform, trackRating, playerStatus, share, playingTrack)
  TrackCover(
    modifier = Modifier
      .padding(16.dp)
      .fillMaxSize(),
    coverUrl = playingTrack.coverUrl,
  )
  ProgressControl(playingPosition, perform)
  Spacer(modifier = Modifier.weight(2f))
  TrackInfo(playingTrack)
  Spacer(modifier = Modifier.weight(1f))
  PlayerActions(playerStatus, perform)
}

@Composable
private fun PlayerScreenAppBar(
  openDrawer: () -> Unit,
  perform: (action: PlayerAction) -> Unit,
  trackRating: TrackRating,
  playerStatus: PlayerStatusModel,
  share: (track: PlayingTrack) -> Unit,
  playingTrack: PlayingTrack
) {
  RemoteTopAppBar(openDrawer = openDrawer) {
    Row {
      FavoriteButton(perform, trackRating)
      RatingButton()
      OverflowMenu(playerStatus.scrobbling, perform) {
        share(playingTrack)
      }
    }
  }
}

@Composable
private fun RatingButton() {
  var showDialog by remember { mutableStateOf(false) }
  IconButton(onClick = { showDialog = true }) {
    Icon(
      imageVector = Icons.Filled.Star,
      contentDescription = stringResource(id = R.string.player_topbar_rating)
    )
  }
  RatingDialog(showDialog = showDialog, dismiss = { showDialog = false })
}

@Composable
private fun OverflowMenu(
  scrobbling: Boolean,
  perform: (action: PlayerAction) -> Unit,
  share: () -> Unit
) {
  var showMenu by remember { mutableStateOf(false) }
  IconButton(onClick = { showMenu = !showMenu }) {
    Icon(
      imageVector = Icons.Filled.MoreVert,
      contentDescription = stringResource(id = R.string.player_topbar_more)
    )
  }
  DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
    DropdownMenuItem(onClick = { perform(PlayerAction.ToggleScrobbling) }) {
      Checkbox(checked = scrobbling, onCheckedChange = { })
      Text(
        text = stringResource(id = R.string.player_topbar_lastfm_scrobble),
        modifier = Modifier.padding(start = 8.dp)
      )
    }
    DropdownMenuItem(onClick = { share() }) {
      Text(text = stringResource(id = R.string.player_topbar_share))
    }
  }
}

@Composable
private fun FavoriteButton(
  perform: (action: PlayerAction) -> Unit,
  trackRating: TrackRating
) = IconButton(onClick = { perform(PlayerAction.ToggleFavorite) }) {
  val imageVector = if (trackRating.isFavorite()) {
    Icons.Filled.Favorite
  } else {
    Icons.Filled.FavoriteBorder
  }
  val resId = if (trackRating.isFavorite()) {
    R.string.player_topbar_favorite
  } else {
    R.string.player_topbar_not_favorite
  }
  Icon(
    imageVector = imageVector,
    contentDescription = stringResource(id = resId)
  )
}

@Composable
private fun PlayerActions(
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
  modifier = Modifier.size(iconSize.times(2.8f))
) {
  val imageVector = when (playerStatus.state) {
    PlayerState.Playing -> Icons.Filled.PauseCircleFilled
    else -> Icons.Filled.PlayCircleFilled
  }
  Icon(
    imageVector = imageVector,
    contentDescription = stringResource(id = R.string.main_button_play_pause_description),
    modifier = Modifier.size(iconSize.times(2.8f)),
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
    modifier = Modifier.fillMaxWidth(0.8f)
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
  Column(modifier = Modifier.weight(1f)) {
    Text(
      text = playingTrack.title,
      style = MaterialTheme.typography.h6,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
    Text(
      text = playingTrack.artistInfo(),
      style = MaterialTheme.typography.subtitle2,
      color = MaterialTheme.colors.onSurface.copy(0.7f),
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
  }
  Column(modifier = Modifier.wrapContentWidth().padding(start = 8.dp)) {
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

@Preview(showBackground = true)
@Composable
fun PlayerScreenPreview() {
  RemoteTheme {
    PlayerScreenPortrait(
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
      perform = {},
      share = {},
      openDrawer = {}
    )
  }
}
