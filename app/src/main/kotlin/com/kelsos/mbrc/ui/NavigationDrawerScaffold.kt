package com.kelsos.mbrc.ui

import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.kelsos.mbrc.BuildConfig
import kotlinx.coroutines.launch

/**
 * Hosts [content] behind the modal navigation drawer at every window width.
 *
 * Wide windows deliberately get the same dismissible drawer as phones: a rail or a permanent drawer
 * keeps the destinations on screen for good, and on a tablet or a phone in landscape that space is
 * better spent on the player and the library.
 */
@Composable
fun NavigationDrawerScaffold(
  drawerState: DrawerState,
  navController: NavHostController,
  drawerViewModel: DrawerViewModel,
  onRequestLocalNetworkAccess: () -> Unit,
  content: @Composable () -> Unit
) {
  val currentBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = currentBackStackEntry?.destination?.route
  val connectionStatus by drawerViewModel.connectionStatus.collectAsStateWithLifecycle()
  val connectionName by drawerViewModel.connectionName.collectAsStateWithLifecycle()
  val scope = rememberCoroutineScope()

  val onConnectionToggle = remember(drawerViewModel, onRequestLocalNetworkAccess) {
    {
      if (!drawerViewModel.toggleConnection()) {
        onRequestLocalNetworkAccess()
      }
    }
  }

  val onNavigate = rememberDrawerNavigation(navController) {
    if (drawerState.isOpen) {
      scope.launch { drawerState.close() }
    }
  }

  ModalNavigationDrawer(
    drawerState = drawerState,
    // Only enable Material's full-area drag while the drawer is open (so
    // swipe-to-close still works). When closed, the drawer's horizontal
    // AnchoredDraggable competes with per-row SwipeToDismissBox on the
    // now playing queue. Opening from the closed state is handled by a
    // narrow left-edge detector at the call site, plus the toolbar menu button.
    gesturesEnabled = drawerState.isOpen,
    drawerContent = {
      DrawerContent(
        currentRoute = currentRoute,
        connectionStatus = connectionStatus,
        connectionName = connectionName,
        versionName = BuildConfig.VERSION_NAME,
        onConnectionToggle = onConnectionToggle,
        onNavigate = onNavigate
      )
    },
    content = content
  )
}

/**
 * Builds the drawer's top-level navigation callback. [onNavigated] runs whether or not the
 * navigation was performed, so the drawer still closes when the tap was swallowed by the
 * resumed-entry guard.
 */
@Composable
private fun rememberDrawerNavigation(
  navController: NavHostController,
  onNavigated: () -> Unit
): (Screen) -> Unit = remember(navController, onNavigated) {
  { screen: Screen ->
    // Navigate on the tap instead of awaiting the close animation first. A navigation that
    // lands a few hundred milliseconds later can arrive in the middle of a predictive back
    // gesture, and the pop that gesture then performs targets an entry that is no longer the
    // top of the back stack, which crashes (#348). The guard covers the residual case where
    // a gesture is already under way when the item is tapped.
    val current = navController.currentBackStackEntry
    if (current == null || current.lifecycleIsResumed()) {
      navController.navigate(screen.route) {
        popUpTo(navController.graph.startDestinationId) {
          saveState = true
        }
        launchSingleTop = true
        restoreState = true
      }
    }
    onNavigated()
  }
}
