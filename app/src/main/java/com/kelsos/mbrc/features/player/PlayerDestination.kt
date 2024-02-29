package com.kelsos.mbrc.features.player

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kelsos.mbrc.app.RemoteDestination
import com.kelsos.mbrc.features.library.PlayingTrack

object PlayerDestination : RemoteDestination {
  override val route: String = "player_route"
  override val destination: String = "player_destination"
}

fun NavGraphBuilder.playerGraph(
  openDrawer: () -> Unit,
  share: (track: PlayingTrack) -> Unit
) {
  composable(PlayerDestination.route) {
    PlayerScreen(openDrawer, share)
  }
}
