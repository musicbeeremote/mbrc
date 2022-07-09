package com.kelsos.mbrc.app

import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.kelsos.mbrc.features.help.SendFeedback
import com.kelsos.mbrc.features.help.helpGraph
import com.kelsos.mbrc.features.library.LibraryNavigator
import com.kelsos.mbrc.features.library.PlayingTrack
import com.kelsos.mbrc.features.library.details.AlbumTrackDestination
import com.kelsos.mbrc.features.library.details.ArtistAlbumDestination
import com.kelsos.mbrc.features.library.details.GenreArtistDestination
import com.kelsos.mbrc.features.library.details.albumTrackGraph
import com.kelsos.mbrc.features.library.details.artistsAlbumGraph
import com.kelsos.mbrc.features.library.details.genreArtistsGraph
import com.kelsos.mbrc.features.library.libraryGraph
import com.kelsos.mbrc.features.lyrics.lyricsGraph
import com.kelsos.mbrc.features.nowplaying.nowPlayingGraph
import com.kelsos.mbrc.features.output.outputGraph
import com.kelsos.mbrc.features.player.PlayerDestination
import com.kelsos.mbrc.features.player.playerGraph
import com.kelsos.mbrc.features.playlists.playlistGraph
import com.kelsos.mbrc.features.radio.radioGraph
import com.kelsos.mbrc.features.settings.ConnectionManagerDestination
import com.kelsos.mbrc.features.settings.connectionManagerGraph
import com.kelsos.mbrc.features.settings.settingsGraph
import kotlinx.coroutines.launch

interface RemoteDestination {
  val route: String
  val destination: String
}

val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> {
  error("No SnackbarHostState provided")
}

class LibraryNavigation(private val navController: NavController) : LibraryNavigator {
  override fun navigateToGenreArtists(id: Long) {
    navController.navigate("${GenreArtistDestination.route}/$id")
  }

  override fun navigateToArtistAlbums(id: Long) {
    navController.navigate("${ArtistAlbumDestination.route}/$id")
  }

  override fun navigateToAlbumTracks(id: Long) {
    navController.navigate("${AlbumTrackDestination.route}/$id")
  }
}

@Composable
fun AppNavGraph(
  navController: NavHostController = rememberNavController(),
  scaffoldState: ScaffoldState = rememberScaffoldState(),
  startDestination: String = PlayerDestination.route,
  share: (track: PlayingTrack) -> Unit,
  sendFeedback: SendFeedback
) {
  val actions = remember(navController) { AppActions(navController) }
  val coroutineScope = rememberCoroutineScope()
  val openDrawer: () -> Unit = { coroutineScope.launch { scaffoldState.drawerState.open() } }

  NavHost(
    navController = navController,
    startDestination = startDestination
  ) {
    playerGraph(
      openDrawer = openDrawer,
      share = share
    )
    libraryGraph(
      openDrawer = openDrawer,
      libraryNavigator = LibraryNavigation(navController),
      coroutineScope = coroutineScope
    ) {
      genreArtistsGraph()
      artistsAlbumGraph()
      albumTrackGraph()
    }
    nowPlayingGraph()
    playlistGraph(
      openDrawer = openDrawer,
      navigateToHome = actions.navigateToHome,
      snackbarHostState = scaffoldState.snackbarHostState
    )
    radioGraph(
      openDrawer = openDrawer,
      navigateToHome = actions.navigateToHome,
      snackbarHostState = scaffoldState.snackbarHostState
    )
    lyricsGraph(
      openDrawer = openDrawer,
      navigateToHome = actions.navigateToHome
    )
    outputGraph()
    settingsGraph(
      navigateToConnectionManager = actions.navigateToConnectionManager
    ) {
      connectionManagerGraph(snackbarHostState = scaffoldState.snackbarHostState)
    }
    helpGraph(
      openDrawer = openDrawer,
      coroutineScope = coroutineScope,
      sendFeedback = sendFeedback
    )
  }
}

class AppActions(navController: NavController) {
  val upPress: () -> Unit = {
    navController.navigateUp()
  }
  val navigateToHome: () -> Unit = {
    navController.navigate(PlayerDestination.route)
  }
  val navigateToConnectionManager: () -> Unit = {
    navController.navigate(ConnectionManagerDestination.route)
  }
}
