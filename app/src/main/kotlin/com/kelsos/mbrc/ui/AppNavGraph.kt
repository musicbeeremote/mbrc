package com.kelsos.mbrc.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kelsos.mbrc.feature.content.playlists.compose.PlaylistScreen
import com.kelsos.mbrc.feature.content.radio.compose.RadioScreen
import com.kelsos.mbrc.feature.library.albums.AlbumInfo
import com.kelsos.mbrc.feature.library.compose.LibraryScreen
import com.kelsos.mbrc.feature.library.compose.drilldown.AlbumTracksScreen
import com.kelsos.mbrc.feature.library.compose.drilldown.ArtistAlbumsScreen
import com.kelsos.mbrc.feature.library.compose.drilldown.GenreArtistsScreen
import com.kelsos.mbrc.feature.misc.help.compose.HelpFeedbackScreen
import com.kelsos.mbrc.feature.playback.nowplaying.compose.NowPlayingScreen
import com.kelsos.mbrc.feature.playback.player.compose.PlayerScreen
import com.kelsos.mbrc.feature.settings.compose.AppLicenseScreen
import com.kelsos.mbrc.feature.settings.compose.ConnectionManagerScreen
import com.kelsos.mbrc.feature.settings.compose.LicensesScreen
import com.kelsos.mbrc.feature.settings.compose.SettingsScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Main navigation graph for the MusicBee Remote app.
 * Defines all screen destinations and their navigation arguments.
 */
@Composable
fun AppNavGraph(
  navController: NavHostController,
  snackbarHostState: SnackbarHostState,
  startDestination: String = Screen.Home.route,
  onOpenDrawer: () -> Unit = {}
) {
  NavHost(
    navController = navController,
    startDestination = startDestination
  ) {
    // Main screens accessible from drawer
    composable(Screen.Home.route) {
      PlayerScreen(
        onNavigateToNowPlaying = { navController.navigate(Screen.NowPlayingList.route) },
        snackbarHostState = snackbarHostState,
        onOpenDrawer = onOpenDrawer
      )
    }

    composable(Screen.Library.route) {
      LibraryScreen(
        onOpenDrawer = onOpenDrawer,
        onNavigateToGenreArtists = { genre ->
          val encodedName = URLEncoder.encode(genre.genre, StandardCharsets.UTF_8.toString())
          navController.navigate("genre_artists/${genre.id}/$encodedName")
        },
        onNavigateToArtistAlbums = { artist ->
          val encodedName = URLEncoder.encode(artist.artist, StandardCharsets.UTF_8.toString())
          navController.navigate("artist_albums/${artist.id}/$encodedName")
        },
        onNavigateToAlbumTracks = { album ->
          val encodedAlbum = URLEncoder.encode(album.album, StandardCharsets.UTF_8.toString())
          val encodedArtist = URLEncoder.encode(album.artist, StandardCharsets.UTF_8.toString())
          navController.navigate("album_tracks/${album.id}/$encodedAlbum/$encodedArtist")
        },
        onNavigateToPlayer = { navController.navigate(Screen.Home.route) },
        snackbarHostState = snackbarHostState
      )
    }

    composable(Screen.Playlists.route) {
      PlaylistScreen(
        onNavigateToPlayer = { navController.navigate(Screen.Home.route) },
        snackbarHostState = snackbarHostState,
        onOpenDrawer = onOpenDrawer
      )
    }

    composable(Screen.Radio.route) {
      RadioScreen(
        onNavigateToPlayer = { navController.navigate(Screen.Home.route) },
        snackbarHostState = snackbarHostState,
        onOpenDrawer = onOpenDrawer
      )
    }

    composable(Screen.Settings.route) {
      SettingsScreen(
        snackbarHostState = snackbarHostState,
        onOpenDrawer = onOpenDrawer,
        onNavigateToLicenses = { navController.navigate(Screen.Licenses.route) },
        onNavigateToAppLicense = { navController.navigate(Screen.AppLicense.route) }
      )
    }

    composable(Screen.Licenses.route) {
      LicensesScreen(onNavigateBack = { navController.popBackStack() })
    }

    composable(Screen.AppLicense.route) {
      AppLicenseScreen(onNavigateBack = { navController.popBackStack() })
    }

    composable(Screen.Help.route) {
      HelpFeedbackScreen(
        snackbarHostState = snackbarHostState,
        onOpenDrawer = onOpenDrawer
      )
    }

    // Detail screens with arguments
    composable(
      route = Screen.AlbumTracks.ROUTE,
      arguments = listOf(
        navArgument("albumId") { type = NavType.LongType },
        navArgument("album") { type = NavType.StringType },
        navArgument("artist") { type = NavType.StringType }
      )
    ) { backStackEntry ->
      val album = URLDecoder.decode(
        backStackEntry.arguments?.getString("album").orEmpty(),
        StandardCharsets.UTF_8.toString()
      )
      val artist = URLDecoder.decode(
        backStackEntry.arguments?.getString("artist").orEmpty(),
        StandardCharsets.UTF_8.toString()
      )
      val albumInfo = AlbumInfo(album = album, artist = artist, cover = null)
      AlbumTracksScreen(
        albumInfo = albumInfo,
        onNavigateBack = { navController.popBackStack() },
        onNavigateToPlayer = { navController.navigate(Screen.Home.route) },
        snackbarHostState = snackbarHostState
      )
    }

    composable(
      route = Screen.ArtistAlbums.ROUTE,
      arguments = listOf(
        navArgument("artistId") { type = NavType.LongType },
        navArgument("artistName") { type = NavType.StringType }
      )
    ) { backStackEntry ->
      val artistName = URLDecoder.decode(
        backStackEntry.arguments?.getString("artistName").orEmpty(),
        StandardCharsets.UTF_8.toString()
      )
      ArtistAlbumsScreen(
        artistName = artistName,
        onNavigateBack = { navController.popBackStack() },
        onNavigateToAlbumTracks = { album ->
          val encodedAlbum = URLEncoder.encode(album.album, StandardCharsets.UTF_8.toString())
          val encodedArtist = URLEncoder.encode(album.artist, StandardCharsets.UTF_8.toString())
          navController.navigate("album_tracks/${album.id}/$encodedAlbum/$encodedArtist")
        },
        onNavigateToPlayer = { navController.navigate(Screen.Home.route) },
        snackbarHostState = snackbarHostState
      )
    }

    composable(
      route = Screen.GenreArtists.ROUTE,
      arguments = listOf(
        navArgument("genreId") { type = NavType.LongType },
        navArgument("genreName") { type = NavType.StringType }
      )
    ) { backStackEntry ->
      val genreId = backStackEntry.arguments?.getLong("genreId") ?: 0L
      val genreName = URLDecoder.decode(
        backStackEntry.arguments?.getString("genreName").orEmpty(),
        StandardCharsets.UTF_8.toString()
      )
      GenreArtistsScreen(
        genreId = genreId,
        genreName = genreName,
        onNavigateBack = { navController.popBackStack() },
        onNavigateToArtistAlbums = { artist ->
          val encodedName = URLEncoder.encode(artist.artist, StandardCharsets.UTF_8.toString())
          navController.navigate("artist_albums/${artist.id}/$encodedName")
        },
        onNavigateToPlayer = { navController.navigate(Screen.Home.route) },
        snackbarHostState = snackbarHostState
      )
    }

    composable(Screen.NowPlayingList.route) {
      NowPlayingScreen(
        onOpenDrawer = onOpenDrawer,
        onNavigateToPlayer = { navController.navigate(Screen.Home.route) },
        snackbarHostState = snackbarHostState
      )
    }

    composable(Screen.ConnectionManager.route) {
      ConnectionManagerScreen(
        snackbarHostState = snackbarHostState,
        onOpenDrawer = onOpenDrawer
      )
    }
  }
}

/**
 * Type-safe navigation destinations using sealed classes.
 * This ensures compile-time safety for navigation and makes refactoring easier.
 */
sealed class Screen(val route: String) {
  // Main screens (drawer items)
  data object Home : Screen("home")
  data object Library : Screen("library")
  data object Playlists : Screen("playlists")
  data object Radio : Screen("radio")
  data object Settings : Screen("settings")
  data object Help : Screen("help")

  // Screens without arguments
  data object NowPlayingList : Screen("now_playing_list")
  data object ConnectionManager : Screen("connection_manager")
  data object Licenses : Screen("licenses")
  data object AppLicense : Screen("app_license")

  // Detail screens with arguments (using companion objects for route templates)
  data class AlbumTracks(val albumId: Long, val album: String, val artist: String) :
    Screen("album_tracks/$albumId/$album/$artist") {
    companion object {
      const val ROUTE = "album_tracks/{albumId}/{album}/{artist}"
    }
  }

  data class ArtistAlbums(val artistId: Long, val artistName: String) :
    Screen("artist_albums/$artistId/$artistName") {
    companion object {
      const val ROUTE = "artist_albums/{artistId}/{artistName}"
    }
  }

  data class GenreArtists(val genreId: Long, val genreName: String) :
    Screen("genre_artists/$genreId/$genreName") {
    companion object {
      const val ROUTE = "genre_artists/{genreId}/{genreName}"
    }
  }
}

/**
 * Drawer navigation items for easy iteration.
 */
val drawerScreens = listOf(
  Screen.Home,
  Screen.Library,
  Screen.Playlists,
  Screen.Radio,
  Screen.Settings,
  Screen.Help
)
