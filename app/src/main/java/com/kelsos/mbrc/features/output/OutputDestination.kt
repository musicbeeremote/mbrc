package com.kelsos.mbrc.features.output

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kelsos.mbrc.app.RemoteDestination

object OutputDestination : RemoteDestination {
  override val route: String = "output_route"
  override val destination: String = "output_destination"
}

fun NavGraphBuilder.outputGraph() {
  composable(OutputDestination.route) {
  }
}
