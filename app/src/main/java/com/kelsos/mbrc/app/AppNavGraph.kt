package com.kelsos.mbrc.app

import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
  object Help : Destination("help")

  fun matches(route: String): Boolean = route == this.route
}

@Composable
fun AppNavGraph(
  navController: NavHostController = rememberNavController(),
  scaffoldState: ScaffoldState = rememberScaffoldState(),
  startDestination: Destination = Destination.Home
) {
  val actions = remember(navController) { AppActions(navController) }
  val coroutineScope = rememberCoroutineScope()
  val openDrawer: () -> Unit = { coroutineScope.launch { scaffoldState.drawerState.open() } }

  NavHost(
    navController = navController,
    startDestination = startDestination.route
  ) {
    composable(Destination.Home.route) {
    }
    composable(Destination.Library.route) {
    }
    composable(Destination.NowPlaying.route) {
    }
    composable(Destination.Playlists.route) {
    }
    composable(Destination.Radio.route) {
    }
    composable(Destination.Lyrics.route) {
    }
    composable(Destination.OutputSelection.route) {
    }
    composable(Destination.Settings.route) {
    }
    composable(Destination.Help.route) {
    }
  }
}

class AppActions(navController: NavController) {
  val upPress: () -> Unit = {
    navController.navigateUp()
  }
}
