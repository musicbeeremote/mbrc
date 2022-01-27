package com.kelsos.mbrc.features.library.details

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kelsos.mbrc.app.RemoteDestination

object GenreArtistDestination : RemoteDestination {
  override val route: String = "genre_artist_route"
  override val destination: String = "genre_artists_destination"
  const val GENRE_ID_ARG: String = "genreId"
}

fun NavGraphBuilder.genreArtistsGraph() {
  composable(
    route = "${GenreArtistDestination.route}/{${GenreArtistDestination.GENRE_ID_ARG}}",
    arguments =
      listOf(
        navArgument(GenreArtistDestination.GENRE_ID_ARG) {
          type = NavType.LongType
        },
      ),
  ) {
    GenreArtistRoute()
  }
}
