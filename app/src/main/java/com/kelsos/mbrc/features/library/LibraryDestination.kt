package com.kelsos.mbrc.features.library

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.kelsos.mbrc.app.RemoteDestination
import kotlinx.coroutines.CoroutineScope

object LibraryDestination : RemoteDestination {
  override val route: String = "library_route"
  override val destination: String = "library_destination"
}

fun NavGraphBuilder.libraryGraph(
  openDrawer: () -> Unit,
  libraryNavigator: LibraryNavigator,
  coroutineScope: CoroutineScope,
  nestedGraphs: NavGraphBuilder.() -> Unit
) {
  navigation(
    route = LibraryDestination.route,
    startDestination = LibraryDestination.destination
  ) {
    composable(route = LibraryDestination.destination) {
      LibraryScreen(
        openDrawer = openDrawer,
        coroutineScope = coroutineScope,
        libraryNavigator = libraryNavigator
      )
    }
    nestedGraphs()
  }
}
