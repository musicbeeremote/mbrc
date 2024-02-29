package com.kelsos.mbrc.features.settings

import androidx.compose.material.SnackbarHostState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kelsos.mbrc.app.RemoteDestination

object ConnectionManagerDestination : RemoteDestination {
  override val route: String = "connection_manager_route"
  override val destination: String = "connection_manager_destination"
}

fun NavGraphBuilder.connectionManagerGraph(
  snackbarHostState: SnackbarHostState
) {
  composable(route = ConnectionManagerDestination.route) {
    ConnectionManagerScreen(snackbarHostState = snackbarHostState)
  }
}
