package com.kelsos.mbrc.features.settings

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.kelsos.mbrc.app.RemoteDestination
import com.kelsos.mbrc.features.settings.composables.SettingsScreen

object SettingsDestination : RemoteDestination {
  override val route: String = "settings_route"
  override val destination: String = "settings_destination"
}

fun NavGraphBuilder.settingsGraph(
  navigateToConnectionManager: () -> Unit,
  nestedGraphs: NavGraphBuilder.() -> Unit
) {
  navigation(
    route = SettingsDestination.route,
    startDestination = SettingsDestination.destination
  ) {
    composable(route = SettingsDestination.destination) {
      SettingsScreen(
        navigateToConnectionManager = navigateToConnectionManager
      )
    }
    nestedGraphs()
  }
}
