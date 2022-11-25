package com.kelsos.mbrc.features.library.details

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kelsos.mbrc.app.RemoteDestination

object AlbumTrackDestination : RemoteDestination {
  override val route: String = "album_track_route"
  override val destination: String = "album_track_destination"
  const val albumIdArg: String = "albumId"
}

fun NavGraphBuilder.albumTrackGraph() {
  composable(
    route = "${AlbumTrackDestination.route}/{${AlbumTrackDestination.albumIdArg}}",
    arguments = listOf(
      navArgument(AlbumTrackDestination.albumIdArg) {
        type = NavType.LongType
      }
    )
  ) {
    AlbumTrackRoute()
  }
}
