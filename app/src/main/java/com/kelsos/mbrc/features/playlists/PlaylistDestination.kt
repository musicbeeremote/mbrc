package com.kelsos.mbrc.features.playlists

import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kelsos.mbrc.app.LocalSnackbarHostState
import com.kelsos.mbrc.app.RemoteDestination

object PlaylistDestination : RemoteDestination {
  override val route: String = "playlist_route"
  override val destination: String = "playlist_destination"
}

fun NavGraphBuilder.playlistGraph(
  openDrawer: () -> Unit,
  navigateToHome: () -> Unit,
  snackbarHostState: SnackbarHostState
) {
  composable(PlaylistDestination.route) {
    CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
      PlaylistScreen(
        openDrawer = openDrawer,
        navigateToHome = navigateToHome
      )
    }
  }
}
