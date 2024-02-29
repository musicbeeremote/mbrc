package com.kelsos.mbrc.features.lyrics

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kelsos.mbrc.app.RemoteDestination

object LyricsDestination : RemoteDestination {
  override val route: String = "lyrics_route"
  override val destination: String = "lyrics_destination"
}

fun NavGraphBuilder.lyricsGraph(
  openDrawer: () -> Unit,
  navigateToHome: () -> Unit
) {
  composable(LyricsDestination.route) {
    LyricsScreen(
      openDrawer = openDrawer,
      navigateToHome = navigateToHome
    )
  }
}
