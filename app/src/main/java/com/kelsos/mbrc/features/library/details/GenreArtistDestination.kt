package com.kelsos.mbrc.features.library.details

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kelsos.mbrc.app.RemoteDestination

object GenreArtistDestination : RemoteDestination {
  override val route: String = "genre_artist_route"
  override val destination: String = "genre_artists_destination"
  const val genreIdArg: String = "genreId"
}

fun NavGraphBuilder.genreArtistsGraph() {
  composable(
    route = "${GenreArtistDestination.route}/{${GenreArtistDestination.genreIdArg}}",
    arguments = listOf(
      navArgument(GenreArtistDestination.genreIdArg) {
        type = NavType.LongType
      }
    )
  ) {
    GenreArtistRoute()
  }
}
