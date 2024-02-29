package com.kelsos.mbrc.features.library.details

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kelsos.mbrc.app.RemoteDestination

object ArtistAlbumDestination : RemoteDestination {
  override val route: String = "artist_album_route"
  override val destination: String = "artist_album_destination"
  const val artistIdArg: String = "artistId"
}

fun NavGraphBuilder.artistsAlbumGraph() {
  composable(
    route = "${ArtistAlbumDestination.route}/{${ArtistAlbumDestination.artistIdArg}}",
    arguments = listOf(
      navArgument(ArtistAlbumDestination.artistIdArg) {
        type = NavType.LongType
      }
    )
  ) {
    ArtistAlbumRoute()
  }
}
