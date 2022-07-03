package com.kelsos.mbrc.features.player

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.state.models.TrackRating
import com.kelsos.mbrc.common.ui.RemoteTopAppBar
import com.kelsos.mbrc.features.library.PlayingTrack

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
fun PlayerScreenAppBar(
  state: PlayerStateModel,
  openDrawer: () -> Unit,
  perform: (action: PlayerAction) -> Unit,
  share: (track: PlayingTrack) -> Unit
) {
  RemoteTopAppBar(openDrawer = openDrawer) {
    Row {
      FavoriteButton(perform, state.trackRating)
      RatingButton()
      OverflowMenu(state.playerStatus.scrobbling, perform) {
        share(state.playingTrack)
      }
    }
  }
}
