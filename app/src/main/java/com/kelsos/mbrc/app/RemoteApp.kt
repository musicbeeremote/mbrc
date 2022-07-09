package com.kelsos.mbrc.app

import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kelsos.mbrc.NavigationViewModel
import com.kelsos.mbrc.features.help.SendFeedback
import com.kelsos.mbrc.features.library.PlayingTrack
import com.kelsos.mbrc.features.player.PlayerDestination
import com.kelsos.mbrc.networking.connections.ConnectionStatus
import com.kelsos.mbrc.theme.RemoteTheme
import kotlinx.coroutines.launch

@Composable
fun RemoteApp(
  viewModel: NavigationViewModel = viewModel(),
  sendFeedback: SendFeedback,
  share: (track: PlayingTrack) -> Unit = {}
) {
  RemoteTheme {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: PlayerDestination.route

    val connection by viewModel.connection.collectAsState(initial = ConnectionStatus.Off)

    Scaffold(
      scaffoldState = scaffoldState,
      drawerContent = {
        AppDrawer(
          currentRoute = currentRoute,
          navigateTo = { destination ->
            coroutineScope.launch { scaffoldState.drawerState.close() }
            navController.navigate(destination)
          },
          connection = connection,
          onConnect = { viewModel.connect() },
          exitApp = {}
        )
      }
    ) {
      AppNavGraph(
        navController = navController,
        scaffoldState = scaffoldState,
        share = share,
        sendFeedback = sendFeedback
      )
    }
  }
}
