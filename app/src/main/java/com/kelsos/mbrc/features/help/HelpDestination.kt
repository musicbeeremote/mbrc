package com.kelsos.mbrc.features.help

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kelsos.mbrc.app.RemoteDestination
import kotlinx.coroutines.CoroutineScope

object HelpDestination : RemoteDestination {
  override val route: String = "help_route"
  override val destination: String = "help_destination"
}

fun NavGraphBuilder.helpGraph(
  openDrawer: () -> Unit,
  coroutineScope: CoroutineScope,
  sendFeedback: SendFeedback
) {
  composable(HelpDestination.route) {
    HelpFeedbackScreen(
      openDrawer = openDrawer,
      coroutineScope = coroutineScope,
      sendFeedback = sendFeedback
    )
  }
}
