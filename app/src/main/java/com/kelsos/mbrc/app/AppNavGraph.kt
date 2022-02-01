package com.kelsos.mbrc.app

import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.kelsos.mbrc.features.help.HelpFeedbackScreen
import com.kelsos.mbrc.features.help.SendFeedback
import com.kelsos.mbrc.features.library.LibraryScreen
import com.kelsos.mbrc.features.library.PlayingTrack
import com.kelsos.mbrc.features.lyrics.LyricsScreen
import com.kelsos.mbrc.features.player.PlayerScreen
import com.kelsos.mbrc.features.playlists.PlaylistScreen
import com.kelsos.mbrc.features.radio.RadioScreen
import com.kelsos.mbrc.features.settings.ConnectionManagerScreen
import com.kelsos.mbrc.features.settings.composables.SettingsScreen
import kotlinx.coroutines.launch

sealed class Destination(val route: String) {
  object Home : Destination("home")
  object Library : Destination("library")
  object NowPlaying : Destination("now_playing")
  object Playlists : Destination("playlists")
  object Radio : Destination("radio")
  object Lyrics : Destination("lyrics")
  object OutputSelection : Destination("output_selection")
  object Settings : Destination("settings")
  object General : Destination("general")
  object ConnectionManager : Destination("connection_manager")
  object Help : Destination("help")

  fun matches(route: String): Boolean = route == this.route
}

val LocalSnackbarHostState =
  compositionLocalOf<SnackbarHostState> { error("No SnackbarHostState provided") }

@Composable
fun AppNavGraph(
  navController: NavHostController = rememberNavController(),
  scaffoldState: ScaffoldState = rememberScaffoldState(),
  startDestination: Destination = Destination.Home,
  share: (track: PlayingTrack) -> Unit,
  sendFeedback: SendFeedback
) {
  val actions = remember(navController) { AppActions(navController) }
  val coroutineScope = rememberCoroutineScope()
  val openDrawer: () -> Unit = { coroutineScope.launch { scaffoldState.drawerState.open() } }

  NavHost(
    navController = navController,
    startDestination = startDestination.route
  ) {
    composable(Destination.Home.route) {
      PlayerScreen(openDrawer, share)
    }
    composable(Destination.Library.route) {
      LibraryScreen(openDrawer, coroutineScope)
    }
    composable(Destination.NowPlaying.route) {
    }
    composable(Destination.Playlists.route) {
      CompositionLocalProvider(LocalSnackbarHostState provides scaffoldState.snackbarHostState) {
        PlaylistScreen(
          openDrawer = openDrawer,
          navigateToHome = actions.navigateToHome
        )
      }
    }
    composable(Destination.Radio.route) {
      CompositionLocalProvider(LocalSnackbarHostState provides scaffoldState.snackbarHostState) {
        RadioScreen(
          openDrawer = openDrawer,
          navigateToHome = actions.navigateToHome
        )
      }
    }
    composable(Destination.Lyrics.route) {
      LyricsScreen(openDrawer, navigateToHome = actions.navigateToHome)
    }
    composable(Destination.OutputSelection.route) {
    }
    navigation(startDestination = Destination.General.route, route = Destination.Settings.route) {
      composable(Destination.General.route) {
        SettingsScreen(navigateToConnectionManager = actions.navigateToConnectionManager)
      }
      composable(Destination.ConnectionManager.route) {
        ConnectionManagerScreen(snackbarHostState = scaffoldState.snackbarHostState)
      }
    }
    composable(Destination.Help.route) {
      HelpFeedbackScreen(openDrawer = openDrawer, coroutineScope = coroutineScope, sendFeedback)
    }
  }
}

class AppActions(navController: NavController) {
  val upPress: () -> Unit = {
    navController.navigateUp()
  }
  val navigateToHome: () -> Unit = {
    navController.navigate(Destination.Home.route)
  }
  val navigateToConnectionManager: () -> Unit = {
    navController.navigate(Destination.ConnectionManager.route)
  }
}
